/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.engine.functions;

import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.util.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serial extends FunctionBase {
	private static final Symbol SYMBOL = Symbol.create("io.github.sparqlanything.engine.functions.Serial_SYMBOL");
	private Map<Object,Integer> counters = null;

	@Override
	public NodeValue exec(List<NodeValue> list) {
		if(!counters.containsKey(list)){
			counters.put(list, 0);
		}
		int serial = counters.get(list) + 1;
		counters.put(list, serial);
		return NodeValue.makeInteger(serial);
	}
	@Override
	protected NodeValue exec(List<NodeValue> args, FunctionEnv env) {
		if(env.getContext().get(SYMBOL) == null){
			env.getContext().set(SYMBOL, new HashMap<Object,Integer>());
		}
		counters = env.getContext().get(SYMBOL);
		return this.exec(args);
	}

	@Override
	public void checkBuild(String s, ExprList exprList) {
		// Any number of values is accepted as counter
	}
}
