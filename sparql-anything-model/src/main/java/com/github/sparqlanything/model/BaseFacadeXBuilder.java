/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.model;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.TxnType;

import java.net.URI;
import java.io.File;
import java.util.Properties;

public class BaseFacadeXBuilder implements FacadeXGraphBuilder {
	private static final String PROPERTY_TDB2_NEW = "tdb2.new";
	private static final String PROPERTY_TDB2 = "tdb2";
	protected static final Logger log = LoggerFactory.getLogger(TripleFilteringFacadeXBuilder.class);
	protected final Properties properties;
	protected final Node mainGraphName;
	protected final DatasetGraph datasetGraph;
	//
	protected final boolean p_blank_nodes;
	protected final String p_namespace;
	protected final String p_root;
	protected final boolean p_trim_strings;
	protected final String p_null_string;
	protected static String currentTDB2Path = "";

	public BaseFacadeXBuilder(String resourceId, Properties properties) {
		// this(resourceId, DatasetGraphFactory.create(), properties);
		// this(resourceId, getDatasetGraph(), properties);
		this(resourceId, null, properties);
	}

	public static DatasetGraph getDatasetGraph(Properties properties){
		DatasetGraph dsg;
		String TDB2Path = "" ;
		boolean TDB2 = properties.containsKey(PROPERTY_TDB2); // TODO assumes "true"
		boolean TDB2New = properties.containsKey(PROPERTY_TDB2_NEW); // TODO assumes "true"
		// boolean TDB2New = properties.getProperty(PROPERTY_TDB2_NEW).equals("true");
		// return TDB2Factory.createDataset().asDatasetGraph(); // only for testing (it is slow)
		// try{
		// 	FileUtils.deleteDirectory(new File("/tmp/thetdb2/")) ;
		// } catch (Exception ex){
		// 	log.error(ex.toString());
		// }
		if(TDB2){
			if(BaseFacadeXBuilder.currentTDB2Path != "" && !TDB2New){
				TDB2Path = currentTDB2Path ;
			}else{
				try{
					TDB2Path = java.nio.file.Files.createTempDirectory("").toString();
					BaseFacadeXBuilder.currentTDB2Path = TDB2Path;
				}catch(Exception ex){
					log.error(ex.toString());
				}
			}
			log.debug("using on disk TBD2 at: {}", TDB2Path);
			dsg = TDB2Factory.connectDataset(TDB2Path).asDatasetGraph();
			if(dsg.isInTransaction()){
				// if we are reusing the same TDB2 then this will be true so
				// end the read txn from the previous query
				dsg.end();
			}
		}else{
			log.debug("using in memory DatasetGraph");
			dsg = DatasetGraphFactory.create() ;
		}
		return dsg;
	}

	protected BaseFacadeXBuilder(String resourceId, DatasetGraph ds, Properties properties) {
		this.properties = properties;
		this.mainGraphName = NodeFactory.createURI(resourceId);
		if(ds == null){
			log.debug("ds was null");
			this.datasetGraph = BaseFacadeXBuilder.getDatasetGraph(properties);
		} else {
			log.debug("ds was not null: " + ds);
			log.debug("ignoring it"); // TODO i don't think we have to have this case anymore
			this.datasetGraph = BaseFacadeXBuilder.getDatasetGraph(properties);
			// log.debug("datasetGraph is now:" + datasetGraph);
			// this.datasetGraph = ds;
		}

		// the single place to begin txn?
		if(this.datasetGraph.supportsTransactions() && !this.datasetGraph.isInTransaction()){
			log.debug("begin big write txn");
			// startedTransactionHere = true ;
			this.datasetGraph.begin(TxnType.WRITE);
		}

		this.p_blank_nodes = Triplifier.getBlankNodeArgument(properties);
		this.p_trim_strings = Triplifier.getTrimStringsArgument(properties);
		this.p_null_string = Triplifier.getNullStringArgument(properties);
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

		if(p_null_string != null && object.isLiteral() && object.getLiteral().toString().equals(p_null_string)){
			return false;
		}

		if(datasetGraph.supportsTransactions()){
			log.debug("begin txn");
			datasetGraph.begin();
		}
		Triple t = new Triple(subject, predicate, object);
		if (datasetGraph.getGraph(graph).contains(t)) {
			return false;
		}
		datasetGraph.getGraph(graph).add(t);
		if(datasetGraph.supportsTransactions()){
			log.debug("end txn");
			datasetGraph.end();
		}
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
		// datasetGraph.setDefaultGraph(datasetGraph.getUnionGraph());
		return datasetGraph;
		// return datasetGraph.getUnionGraph() ;
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
