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

import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.vocabulary.RDF;

public interface FXFunction {

	default boolean isContainerMembershipProperty(NodeValue... nodeValues) {
		for (NodeValue nodeValue : nodeValues) {
			if (!nodeValue.isIRI() || !nodeValue.asNode().getURI().startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#_")) {
				return false;
			}
		}
		return true;
	}


	default boolean isSlotConsistent(int number) {
		return number > 0;
	}

	default int getInt(NodeValue nodeValue) {
		return Integer.parseInt(nodeValue.asNode().getURI().substring(44));
	}

	default NodeValue asContainerMembershipProperty(int number) {
		if (isSlotConsistent(number)) {
			return NodeValue.makeNode(RDF.li(number).asNode());
		}
		throw new ExprEvalException("Illegal container membership property with index " + number);
	}
}
