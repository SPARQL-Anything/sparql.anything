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
import org.apache.jena.query.Dataset;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.TxnType;

import java.net.URI;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Properties;

public class BaseFacadeXGraphBuilder implements FacadeXGraphBuilder {
	private static final String PROPERTY_ONDISK_REUSE = "ondisk.reuse";
	private static final String PROPERTY_ONDISK = "ondisk";
	protected static final Logger log = LoggerFactory.getLogger(TripleFilteringFacadeXGraphBuilder.class);
	protected final Properties properties;
	protected final Node mainGraphName;

	// when using a TDB2 this is defined 
	protected static Dataset dataset = null; // TODO making this static is a kludge maybe
	protected final DatasetGraph datasetGraph;
	//
	protected final boolean p_blank_nodes;
	protected final String p_namespace;
	protected final String p_root;
	protected final boolean p_trim_strings;
	protected final String p_null_string;
	protected static String previousTDB2Path = "";

	public BaseFacadeXGraphBuilder(String resourceId, Properties properties) {
		this(resourceId, null, properties);
	}

	// this is where all graph (graphs that we actually put triples in) creation happens
	public static DatasetGraph getDatasetGraph(Properties properties){
		DatasetGraph dsg;
		String TDB2Path = "" ;
		boolean ONDISK = properties.containsKey(PROPERTY_ONDISK);
		boolean ONDISK_REUSE = properties.containsKey(PROPERTY_ONDISK_REUSE); // TODO any string counts as "true"

		if(ONDISK){
			if(BaseFacadeXGraphBuilder.previousTDB2Path != "" && ONDISK_REUSE){
				TDB2Path = previousTDB2Path ;
			}else{
				try{
					if(previousTDB2Path!=""){
						log.debug("deleting previous TDB2 at: {}",previousTDB2Path);
						FileUtils.deleteDirectory(new File(previousTDB2Path)) ;
					}
					if(Files.isDirectory(Paths.get(properties.getProperty(PROPERTY_ONDISK)))){
						TDB2Path = Files.createTempDirectory(Paths.get(properties.getProperty(PROPERTY_ONDISK)),"").toString();
					}else{
						log.debug("the specified path is not a directory: {}\nusing /tmp instead", 
								properties.getProperty(PROPERTY_ONDISK));
						TDB2Path = Files.createTempDirectory(Paths.get("/tmp"),"").toString();
					}
					// store the TDB2Path for next time (in case we want to reuse it or delete it)
					BaseFacadeXGraphBuilder.previousTDB2Path = TDB2Path;
				}catch(Exception ex){
					log.error(ex.toString());
				}
			}
			log.debug("using on disk TBD2 at: {}", TDB2Path);
			dataset = TDB2Factory.connectDataset(TDB2Path);
			dsg = dataset.asDatasetGraph();
			if(dsg.isInTransaction()){
				// if we are reusing the same TDB2 then this will be true so
				// end the read txn from the previous query
				dsg.end();
			}
		}else{
			log.debug("using in memory DatasetGraph");
			// i don't think we ever reuse the same in memory DatasetGraph
			// so no need to end the previous query's read txn
			dsg = DatasetGraphFactory.create() ;
		}
		return dsg;
	}

	protected BaseFacadeXGraphBuilder(String resourceId, DatasetGraph ds, Properties properties) {
		this.properties = properties;
		this.mainGraphName = NodeFactory.createURI(resourceId);
		this.datasetGraph = BaseFacadeXGraphBuilder.getDatasetGraph(properties);

		// the single place to begin write txns
		log.debug("begin write txn");
		this.datasetGraph.begin(TxnType.WRITE);

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

	public Node container2node(String container) {
		if (p_blank_nodes) {
			return FacadeXGraphBuilder.super.container2BlankNode(container);
			//return NodeFactory.createBlankNode(container);
		} else {
//			return NodeFactory.createURI(container);
			return FacadeXGraphBuilder.super.container2URI(container);
		}
	}

	public Node key2predicate(String key) {
		return FacadeXGraphBuilder.super.key2predicate(this.p_namespace, key);
	}

	public Node value2node(Object value) {
		// trims_strings == true and if object is string, trim it
		if(p_trim_strings && value instanceof String){
			value = ((String)value).trim();
		}
		return FacadeXGraphBuilder.super.value2node(value);
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
		if(dataset == null){
			// we have an in memory DatasetGraph
			datasetGraph.setDefaultGraph(datasetGraph.getUnionGraph());
			// we are unable to do that ^ with an on disk DatasetGraph (TDB2)
			// so that means you need to do `graph ?g {?s ?p ?o}` instead of simply
			// `{?s ?p ?o}` in a query when you use a TDB2
		}
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
