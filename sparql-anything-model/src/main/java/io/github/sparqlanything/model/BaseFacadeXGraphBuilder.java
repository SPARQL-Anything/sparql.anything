/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

import org.apache.commons.io.FileUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.TxnType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.system.Txn;
import org.apache.jena.tdb1.TDB1Factory;
import org.apache.jena.tdb2.DatabaseMgr;
import org.apache.jena.tdb2.TDB2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class BaseFacadeXGraphBuilder extends BaseFacadeXBuilder implements FacadeXGraphBuilder {

	protected static final Logger log = LoggerFactory.getLogger(BaseFacadeXGraphBuilder.class);

	protected DatasetGraph datasetGraph;

	public BaseFacadeXGraphBuilder(Properties properties) {
		super(properties);
		initialiseDatasetGraph(properties);
		datasetGraph.begin(TxnType.WRITE);
	}


	private void initialiseDatasetGraph(Properties properties) {
		String ondiskPath = PropertyUtils.getStringProperty(properties, IRIArgument.ONDISK);
		if (ondiskPath != null) {
			log.trace("Using TDB2");
			boolean ondisk_reuse = PropertyUtils.getBooleanProperty(properties, IRIArgument.ONDISK_REUSE);
			File ondiskFile = new File(ondiskPath);
			log.debug("Ondisk reuse {}", ondisk_reuse);
			if (!ondisk_reuse && ondiskFile.isDirectory() && ondiskFile.exists()) {
				log.trace("Deleting directory: {}", ondiskFile.getAbsolutePath());
				try {
					FileUtils.deleteDirectory(ondiskFile);
				} catch (IOException e) {
					if (TDB1Factory.inUseLocation(ondiskPath)) {
						DatasetGraph dg = TDB2Factory.connectDataset(ondiskPath).asDatasetGraph();
						dg.end();
						Txn.executeWrite(dg, dg::clear);
						log.warn("Clearing TBD instead of deleting the TDB folder.");
					} else {
						throw new RuntimeException(e);
					}
				}
			}
			datasetGraph = TDB2Factory.connectDataset(ondiskPath).asDatasetGraph();
			if (datasetGraph.isInTransaction()) {
				// if we are reusing the same TDB2 then this will be true so
				// end the read txn from the previous query
				datasetGraph.end();
			}
		} else {
			log.debug("Using in memory DatasetGraph");
			// I don't think we ever reuse the same in memory DatasetGraph
			// so no need to end the previous query's read txn
			datasetGraph = DatasetGraphFactory.create();
		}
	}

	@Override
	public boolean add(Node graph, Node subject, Node predicate, Node object) {

		if (p_null_string != null && object.isLiteral() && object.getLiteral().getLexicalForm().equals(p_null_string)) {
			return false;
		}
		Triple t = Triple.create(subject, predicate, object);
		if (datasetGraph.getGraph(graph).contains(t)) {
			return false;
		}
		datasetGraph.getGraph(graph).add(t);
		return true;
	}

	/**
	 * This method returns triples from the union of all graphs.
	 *
	 * @return the union of all graphs as model
	 */
	public Model getModel() {
		return ModelFactory.createModelForGraph(getDatasetGraph().getUnionGraph());
	}

	@Override
	public DatasetGraph getDatasetGraph() {
		if (!DatabaseMgr.isTDB2(datasetGraph)) {
			// we have an in memory DatasetGraph
			datasetGraph.addGraph(NodeFactory.createURI("urn:x-arq:DefaultGraph"), datasetGraph.getUnionGraph());
//			datasetGraph.setDefaultGraph(datasetGraph.getUnionGraph());
			// we are unable to do that ^ with an on disk DatasetGraph (TDB2)
			// so that means you need to do `graph ?g {?s ?p ?o}` instead of simply
			// `{?s ?p ?o}` in a query when you use a TDB2
		}
		return datasetGraph;
	}


}
