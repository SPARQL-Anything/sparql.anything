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
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

@FXFunctionDoc(
	description = "The function fx:bnode( ?a) builds a blank node enforcing the node value as local identifier. This is useful when multiple construct templates are populated with bnode generated on different query solutions but we want them to be joined in the output RDF graph.",
	example = "BIND(fx:bnode(?id) AS ?blankNode)",
	group = "FUNCTIONS",
	label = "fx:bnode"
)
public class Bnode extends FunctionBase1 implements FXFunction {

	@Override
	public NodeValue exec(NodeValue nodeValue) {
		return NodeValue.makeNode(NodeFactory.createBlankNode(nodeValue.toString()));
//		throw new ExprEvalException("Not a container membership property: " + FmtUtils.stringForNode(nodeValue.asNode())) ;
	}
}
