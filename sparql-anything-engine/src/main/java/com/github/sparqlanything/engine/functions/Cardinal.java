/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.engine.functions;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.impl.XMLLiteralType;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.sparql.util.FmtUtils;
import org.semarglproject.vocab.XSD;

public class Cardinal extends FunctionBase1 implements FXFunction {

	@Override
	public NodeValue exec(NodeValue nodeValue) {
		if(isContainerMembershipProperty(nodeValue)){
			int num = getInt(nodeValue);
			return NodeValue.makeInteger(num);
		}

		throw new ExprEvalException("Not a container membership property: " + FmtUtils.stringForNode(nodeValue.asNode())) ;
	}
}
