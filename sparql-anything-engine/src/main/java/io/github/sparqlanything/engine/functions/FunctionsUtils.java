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

public abstract class FunctionsUtils {


	public static String nodeValueAsString(NodeValue nodeValue) {
		if (nodeValue.isLiteral()) {
			return nodeValue.getString();
		} else if (nodeValue.isIRI()) {
			return nodeValue.asNode().getURI();
		}

		throw new ExprEvalException("Argument must be literal or IRI");
	}

}
