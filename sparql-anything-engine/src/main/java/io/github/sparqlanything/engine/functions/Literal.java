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
import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionEnv;

import java.util.List;

@FXFunctionDoc(
	description = "The function fx:literal( ?a , ?b ) builds a literal from the string representation of ?a, using ?b either as a typed literal (if a IRI is given) or a lang code (if a string of length of two is given).",
	example = "BIND(fx:literal(?string, xsd:int) AS ?result)",
	group = "FUNCTIONS",
	label = "fx:literal"
)
public class Literal extends FXBuilderFunction {

	@Override
	public void checkBuild(String uri, ExprList args) {
		if (args.size() != 2) {
			throw new QueryBuildException("fx:literal takes exactly two arguments");
		}
	}

	@Override
	protected NodeValue build(List<NodeValue> a, OnError mode, FunctionEnv env) {
		NodeValue v1 = a.get(0), v2 = a.get(1);
		if (v2.isIRI()) {
			return NodeValue.makeNode(v1.asString(), new BaseDatatype(v2.getNode().getURI()));
		}
		if (v2.isString() && !v2.getString().isEmpty()) {
			return NodeValue.makeLangString(v1.asString(), v2.getString());
		}
		throw new FXBuildException("Expected a datatype IRI or a non-empty language tag as second argument. Given: " + v2);
	}
}

