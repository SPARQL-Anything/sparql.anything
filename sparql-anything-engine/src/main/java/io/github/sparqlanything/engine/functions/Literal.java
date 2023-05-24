/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.sparql.expr.ExprEvalTypeException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;

public class Literal extends FunctionBase2 {
	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2) {
		if(v2.isIRI()){
			return NodeValue.makeNode(v1.asString(), new BaseDatatype(v2.getNode().getURI()));
		}else if(v2.isString() && v2.getString().length() == 2){
			return NodeValue.makeLangString(v1.asString(), v2.getString());
		}
		throw new ExprEvalTypeException("Can only work with args (string,iri) or (string,string[2])"); // # NodeValue.NONE.getConstant();
	}
}

