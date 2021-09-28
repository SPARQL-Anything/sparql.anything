/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.spiceh2020.sparql.anything.engine.functions.reflection;

import org.apache.jena.query.QueryBuildException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ReflectionFunctionFactory {
	public final static Logger logger = LoggerFactory.getLogger(ReflectionFunctionFactory.class);

	private Map<Class<?>, NodeValueConverter<?,?>> converters;
	private Map<Class<? extends NodeValue>, Set<NodeValueConverter<?,?>>> nodeValueConverters;

	public ReflectionFunctionFactory(){
		converters = new HashMap<Class<?>, NodeValueConverter<?,?>>();
		nodeValueConverters = new HashMap<Class<? extends NodeValue>, Set<NodeValueConverter<?,?>>>();
		register(new Converters.CharConverter());
		converters.put(char.class, new Converters.CharConverter());
		register(new Converters.StringConverter());
		register(new Converters.LongConverter());
		converters.put(long.class, new Converters.LongConverter());
		register(new Converters.DoubleConverter());
		converters.put(double.class, new Converters.DoubleConverter());
		register(new Converters.IntegerConverter());
		converters.put(int.class, new Converters.IntegerConverter());
		register(new Converters.BooleanConverter());
		converters.put(boolean.class, new Converters.BooleanConverter());
		register(new Converters.FloatConverter());
		converters.put(float.class, new Converters.FloatConverter());
	}

	public final void register(NodeValueConverter converter){
		converters.put(converter.getType(), converter);
		if(!nodeValueConverters.containsKey(converter.getNodeValueType())){
			nodeValueConverters.put(converter.getNodeValueType(), new HashSet<NodeValueConverter<?,?>>());
		}
		nodeValueConverters.get(converter.getNodeValueType()).add(converter);
	}

	public NodeValueConverter<?,?> getConverter(Class<?> clazz) throws NoConverterException {
		if(!converters.containsKey(clazz)){
			throw new NoConverterException(clazz);
		}
		return converters.get(clazz);
	}

	public FunctionFactory makeFunction(Method... methods){
		String methodName = methods[0].getName();
		final ReflectionFunction function = new ReflectionFunction(methodName,methods);
		return new FunctionFactory() {
			@Override
			public Function create(String s) {
				return function;
			}
		};
	}

	public FunctionFactory makeFunction(Class<?> type, String methodName){
		Method[] methods = type.getMethods();
		List<Method> list = new ArrayList();
		for(Method m: methods){
			if(m.getName().equals(methodName)){
				list.add(m);
			}
		}
		return  makeFunction((Method[]) list.toArray(new Method[list.size()]));
	}

	public class ReflectionFunction extends FunctionBase {

		private Map<Integer,List<Method>> methods;
		private String name;

		public ReflectionFunction(String name, Method... methodList) {
			this.methods = new HashMap<Integer,List<Method>>();
			this.name = name;
			for(Method method : methodList){
				int len = method.getParameterTypes().length;
				if(!methods.containsKey(len)){
					methods.put(len, new ArrayList<Method>());
				}
				methods.get(len).add(method);
			}
		}

		private boolean compatible(List<NodeValue> args, Method m){
			if (args.size() == m.getParameterCount()) {
				// Check compatibility of argument types
				for(int a = 0; a < m.getParameterCount(); a++){
					Class<?> par = m.getParameterTypes()[a];
					if(!converters.containsKey(par) && converters.get(par).compatibleWith(args.get(a))){
						return false;
					}
				}
				return true;
			}
			return false;
		}

		private Method findMethod(List<NodeValue> args){
			Set<Integer> methodsArgs = new HashSet<Integer>();
			// If static, check methods with
			int argsList = args.size();

			for(Map.Entry<Integer,List<Method>> ms : methods.entrySet()) {
				for(Method m : ms.getValue()) {
					if (Modifier.isStatic(m.getModifiers())) {
						methodsArgs.add(m.getParameterCount());
						if(compatible(args, m)){
							return m;
						}
					} else {
						methodsArgs.add(m.getParameterCount() + 1);
						List<NodeValue> args2 = new ArrayList<NodeValue>();
						args2.addAll(args);
						args2.remove(0);
						if (compatible(args2, m) ){
							// Stop checking
							return m;
						}
					}
				}
			}

			throw new QueryBuildException("Function '" + name + "' takes any of the following number of arguments: " + methodsArgs + ", " + args.size() + " given instead.");
		}

		public void checkBuild(String uri, ExprList args) {
			// XXX Query build is always successful
		}

		private Object[] createArguments(List<NodeValue> arguments, Class<?>[] parameterTypes) throws NoConverterException {
			Object[] args = new Object[arguments.size()];
			for(int i=0; i<arguments.size(); i++){
				args[i] =  ReflectionFunctionFactory.this.getConverter(parameterTypes[i]).asType(arguments.get(i));
			}
			return args;
		}

		public NodeValue exec(List<NodeValue> args) {
			Method method = findMethod(args);
			Object o = null;
			try {
				NodeValueConverter<?,?> returnTypeConverter = getConverter(method.getReturnType());
				if ( !Modifier.isStatic(method.getModifiers() ) ){
					NodeValueConverter<?,?> invokingOn = getConverter(method.getDeclaringClass());
					Object[] arguments = createArguments(args.subList(1, args.size()), method.getParameterTypes());
					o = method.invoke(invokingOn.asType(args.get(0)), arguments) ;
				} else {
					Object[] arguments = createArguments(args, method.getParameterTypes());
					o = method.invoke(null, arguments);
				}
				return returnTypeConverter.objectAsNodeValue(o);
			} catch (IllegalAccessException | InvocationTargetException | IncompatibleObjectException | NoConverterException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static ReflectionFunctionFactory instance = null;
	public static final ReflectionFunctionFactory get(){
		if(instance == null){
			instance = new ReflectionFunctionFactory();
		}
		return instance;
	}
}
