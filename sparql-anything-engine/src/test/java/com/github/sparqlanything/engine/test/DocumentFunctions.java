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

package com.github.sparqlanything.engine.test;

import com.github.sparqlanything.engine.FacadeX;

import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DocumentFunctions {

	/**
	 * This is useful for generating the markdown documentation of registered functions.
	 */
	@Test
	public void printDocumentation(){
		OpExecutorFactory ef = FacadeX.ExecutorFactory; // classloader does the appropriate configuration
		FunctionRegistry fr = FunctionRegistry.get();
		Iterator<String> functions = fr.keys();
		List<String> list = new ArrayList<String>();
		functions.forEachRemaining(list::add);
		Collections.sort(list);
		functions = list.iterator();

		while(functions.hasNext()){
			String function = functions.next();
			//FunctionFactory ff = fr.get(function);
			//Function f = ff.create(function);

		}

	}
}
