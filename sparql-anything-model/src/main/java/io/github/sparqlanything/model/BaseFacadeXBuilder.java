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



package io.github.sparqlanything.model;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.net.URI;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public abstract class BaseFacadeXBuilder extends FacadeXAbstractNodeBuilder implements FacadeXQuadHandler, FacadeXComponentHandler {
	protected static final Set<String> predicateKeys = new HashSet<>();

	public BaseFacadeXBuilder(Properties properties) {
		super(properties);
	}

	protected String keepLabel(String key) {
		if (p_generate_predicate_labels) predicateKeys.add(key);
		return key;
	}

	protected void addPredicateLabelTriples(Graph graph) {
		if (p_generate_predicate_labels) {
			for (String key : predicateKeys) {
				graph.add(key2predicate(key), RDFS.label.asNode(), NodeFactory.createLiteralString(key));
			}
		}
	}

	public boolean addContainer(String dataSourceId, String containerId, String slotKey, String childContainerId) {
		return add(dataSourceId2node(dataSourceId), container2node(containerId, dataSourceId), key2predicate(keepLabel(slotKey)), container2node(childContainerId, dataSourceId));
	}

	public boolean addContainer(String dataSourceId, String containerId, URI customKey, String childContainerId) {
		return add(dataSourceId2node(dataSourceId), container2node(containerId, dataSourceId), NodeFactory.createURI(customKey.toString()), container2node(childContainerId, dataSourceId));
	}

	public boolean addContainer(String dataSourceId, String containerId, Integer slotKey, String childContainerId) {
		return addSlotStatement(dataSourceId, containerId, slotKey, childContainerId, true);
	}

	public boolean addType(String dataSourceId, String containerId, String typeId) {
		return add(dataSourceId2node(dataSourceId), container2node(containerId, dataSourceId), RDF.type.asNode(), key2predicate(keepLabel(typeId)));
	}

	public boolean addType(String dataSourceId, String containerId, URI type) {
		return add(dataSourceId2node(dataSourceId), container2node(containerId, dataSourceId), RDF.type.asNode(), NodeFactory.createURI(type.toString()));
	}

	public boolean addValue(String dataSourceId, String containerId, String slotKey, Object value) {
		return add(dataSourceId2node(dataSourceId), container2node(containerId, dataSourceId), key2predicate(keepLabel(slotKey)), value2node(value));
	}

	public boolean addValue(String dataSourceId, String containerId, URI customKey, Object value) {
		return add(dataSourceId2node(dataSourceId), container2node(containerId, dataSourceId), NodeFactory.createURI(customKey.toString()), value2node(value));
	}

	public boolean addValue(String dataSourceId, String containerId, Integer slotKey, Object value) {
		return addSlotStatement(dataSourceId, containerId, slotKey, value, false);
	}

	public boolean addRoot(String dataSourceId) {
		return add(dataSourceId2node(dataSourceId), container2node(SPARQLAnythingConstants.ROOT_ID, dataSourceId), RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
	}

	private boolean addSlotStatement(String dataSourceId, String containerId, Integer slotKey, Object object, boolean isObjectContainer) {
		Node g = dataSourceId2node(dataSourceId);
		Node s = container2node(containerId, dataSourceId);
		Node p = createIntegerKeyNode(slotKey);
		Node o = createObjectNode(dataSourceId, object, isObjectContainer);
		if (p_reify_slot_statements) {
			Node r = NodeFactory.createBlankNode();
			add(g, r, RDF.reifies.asNode(), NodeFactory.createTripleTerm(s, p, o));
			add(g, r, NodeFactory.createURI(Triplifier.FACADE_X_SLOT_KEY), NodeFactory.createLiteralDT(slotKey.toString(), XSDDatatype.XSDinteger));
		}
		return add(g, s, p, o);
	}

}
