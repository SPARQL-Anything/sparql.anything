/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package io.github.sparqlanything.engine.functions;

import io.github.sparqlanything.model.annotations.FXFunctionDoc;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.sparql.util.FmtUtils;

@FXFunctionDoc(
	description = "fx:previous(?a) returns the container membership property that preceeds ?a (rdf:_2 -> rdf:_1)",
	example = "BIND(fx:previous(?slot) AS ?previousSlot)",
	group = "FUNCTIONS",
	label = "fx:previous"
)
public class Previous extends FunctionBase1 implements FXFunction {

	@Override
	public NodeValue exec(NodeValue nodeValue) {
		if(isContainerMembershipProperty(nodeValue)){
			int num = getInt(nodeValue);
			int prev = num - 1;
			return asContainerMembershipProperty(prev);
		}

		throw new ExprEvalException("Not a container membership property: " + FmtUtils.stringForNode(nodeValue.asNode())) ;
	}
}
