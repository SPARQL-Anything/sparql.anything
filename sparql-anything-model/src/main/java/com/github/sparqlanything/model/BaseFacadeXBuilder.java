/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.sparqlanything.model;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Properties;

public class BaseFacadeXBuilder implements FacadeXGraphBuilder {
	protected static final Logger log = LoggerFactory.getLogger(TripleFilteringFacadeXBuilder.class);
	protected final Properties properties;
	protected final Node mainGraphName;
	protected final DatasetGraph datasetGraph;
	//
	protected final boolean p_blank_nodes;
	protected final String p_namespace;
	protected final String p_root;
	protected final boolean p_trim_strings;

	public BaseFacadeXBuilder(String resourceId, Properties properties) {
		this(resourceId, DatasetGraphFactory.create(), properties);
	}

	protected BaseFacadeXBuilder(String resourceId, DatasetGraph ds, Properties properties) {
		this.properties = properties;
		this.mainGraphName = NodeFactory.createURI(resourceId);
		this.datasetGraph = ds;
		this.p_blank_nodes = Triplifier.getBlankNodeArgument(properties);
		this.p_trim_strings = Triplifier.getTrimStringsArgument(properties);
		this.p_namespace = Triplifier.getNamespaceArgument(properties);
		this.p_root = Triplifier.getRootArgument(properties, resourceId);
	}

//	@Deprecated
//	public void add(Resource subject, Property predicate, RDFNode object) {
//		add(subject.asNode(), predicate.asNode(), object.asNode());
//	}
//
//	@Override
//	public boolean add(Node subject, Node predicate, Node object) {
//		return add(mainGraphName, subject, predicate, object);
//	}

	@Override
	public boolean add(Node graph, Node subject, Node predicate, Node object) {
		Triple t = new Triple(subject, predicate, object);
		if (datasetGraph.getGraph(graph).contains(t)) {
			return false;
		}
		datasetGraph.getGraph(graph).add(t);
		return true;
	}

	@Override
	public boolean addContainer(String dataSourceId, String containerId, String slotKey, String childContainerId) {
		return add(NodeFactory.createURI(dataSourceId), container2node(containerId), key2predicate(slotKey),
				container2node(childContainerId));
	}

	@Override
	public boolean addContainer(String dataSourceId, String containerId, URI customKey, String childContainerId) {
		return add(NodeFactory.createURI(dataSourceId), container2node(containerId),
				NodeFactory.createURI(customKey.toString()), container2node(childContainerId));
	}

	@Override
	public boolean addContainer(String dataSourceId, String containerId, Integer slotKey, String childContainerId) {
		return add(NodeFactory.createURI(dataSourceId), container2node(containerId), RDF.li(slotKey).asNode(),
				container2node(childContainerId));
	}

	@Override
	public boolean addType(String dataSourceId, String containerId, String typeId) {
		return add(NodeFactory.createURI(dataSourceId), container2node(containerId), RDF.type.asNode(),
				NodeFactory.createURI(typeId));
	}

	@Override
	public boolean addType(String dataSourceId, String containerId, URI type) {
		return add(NodeFactory.createURI(dataSourceId), container2node(containerId), RDF.type.asNode(),
				NodeFactory.createURI(type.toString()));
	}

	@Override
	public boolean addValue(String dataSourceId, String containerId, String slotKey, Object value) {
		return add(NodeFactory.createURI(dataSourceId), container2node(containerId), key2predicate(slotKey),
				value2node(value));
	}

	@Override
	public boolean addValue(String dataSourceId, String containerId, Integer slotKey, Object value) {
		return add(NodeFactory.createURI(dataSourceId), container2node(containerId), RDF.li(slotKey).asNode(),
				value2node(value));
	}

	@Override
	public boolean addValue(String dataSourceId, String containerId, URI customKey, Object value) {
		return add(NodeFactory.createURI(dataSourceId), container2node(containerId),
				NodeFactory.createURI(customKey.toString()), value2node(value));
	}

	@Override
	public boolean addRoot(String dataSourceId, String rootId) {
		return add(NodeFactory.createURI(dataSourceId), container2node(rootId), RDF.type.asNode(),
				NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
	}

	@Override
	public Node container2node(String container) {
		if (p_blank_nodes) {
			return NodeFactory.createBlankNode(container);
		} else {
			return NodeFactory.createURI(container);
		}
	}

	@Override
	public Node key2predicate(String key) {
		return NodeFactory.createURI(this.p_namespace + key);
	}

	@Override
	public Node value2node(Object value) {
		if (value instanceof Node) {
			return (Node) value;
		} else {
			// trims_strings == true and if object is string, trim it
			if(p_trim_strings && value instanceof String){
				value = ((String)value).trim();
			}
			return ResourceFactory.createTypedLiteral(value).asNode();
		}
	}

	/**
	 * This includes triples from the default graph / union of all graphs.
	 *
	 * @return
	 */
	public Model getModel() {
		return ModelFactory.createModelForGraph(getDatasetGraph().getUnionGraph());
	}

	@Override
	public DatasetGraph getDatasetGraph() {
		datasetGraph.setDefaultGraph(datasetGraph.getUnionGraph());
		return datasetGraph;
	}

	/**
	 * The main graph is created when adding triples instead of quads. The main
	 * graph uses the resourceId as data source identifier / graph name
	 *
	 * @return
	 */
//	@Override
//	public Node getMainGraphName() {
//		return mainGraphName;
//	}
//
//	@Override
//	public Graph getMainGraph() {
//		return datasetGraph.getGraph(mainGraphName);
//	}
}
