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
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase;

import java.util.List;

@FXFunctionDoc(
	description = "The function fx:entity (?a ... ?n) accepts a list of arguments and performs concatenation and automatic casting to string. Container membership properties (rdf:_1,rdf:_2,...) are cast to numbers and then to strings (\\\"1\\\",\\\"2\\\").",
	example = "BIND(fx:entity(\\\"http://example.org/\\\", ?name, ?id) AS ?uri)",
	group = "FUNCTIONS",
	label = "fx:entity"
)
public class Entity extends FunctionBase implements FXFunction {

	@Override
	public NodeValue exec(List<NodeValue> list) {
		StringBuilder sb = new StringBuilder();
		for(NodeValue node: list){
			if(node.isString()){
				sb.append(node.getString());
			}else if(node.isInteger()){
				sb.append(node.getInteger().intValue());
			}else if(node.isLiteral()){
				sb.append(node.asString());
			}else if(node.isBlank()){
				sb.append(node.getNode().getBlankNodeLabel());
			}else if(node.isIRI()){
				if(isContainerMembershipProperty(node)){
					sb.append(getInt(node));
				}else{
					String uri = node.asNode().getURI();
					sb.append(uri);
				}
			}else{
				throw new RuntimeException("Unsupported node value: " + node);
			}
		}
		return NodeValue.makeNode(NodeFactory.createURI(sb.toString()));
	}

	@Override
	public void checkBuild(String s, ExprList exprList) {

	}
}
