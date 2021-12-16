/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.engine.functions;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.jena.sparql.util.FmtUtils;

public class Before extends FunctionBase2 implements FXFunction {
	@Override
	public NodeValue exec(NodeValue nodeValue, NodeValue nodeValue1) {
		if(isContainerMembershipProperty(nodeValue, nodeValue1)){
			return NodeValue.booleanReturn(getInt(nodeValue) < getInt(nodeValue1));
		} else {
			throw new ExprEvalException("Not container membership properties: " + FmtUtils.stringForNode(nodeValue.asNode()) + " " + FmtUtils.stringForNode(nodeValue1.asNode())) ;
		}

	}
}
