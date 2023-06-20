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

package io.github.sparqlanything.model;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ResourceFactory;

public interface FacadeXNodeBuilder {

	default Node dataSourceId2node(String dataSourceId) {
		return NodeFactory.createURI(getRoot().concat(dataSourceId));
	}

	String getRoot();

	default Node container2URI(String container) {
		return NodeFactory.createURI(container);
	}

	default Node container2BlankNode(String container) {
		return NodeFactory.createBlankNode(container);
	}

	default Node key2predicate(String key) {
		return NodeFactory.createURI(getNamespace().concat(Triplifier.toSafeURIString(key)));
	}

	String getNamespace();

	default Node value2node(Object value) {
		if (value instanceof Node) {
			return (Node) value;
		} else {
			return ResourceFactory.createTypedLiteral(value).asNode();
		}
	}
}
