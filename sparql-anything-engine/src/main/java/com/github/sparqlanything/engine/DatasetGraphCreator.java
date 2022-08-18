/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.engine;

import com.github.sparqlanything.metadata.MetadataTriplifier;
import com.github.sparqlanything.model.*;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.util.Symbol;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DatasetGraphCreator {

	private static final Logger logger = LoggerFactory.getLogger(DatasetGraphCreator.class);
	private final static Symbol inMemoryCache = Symbol.create("facade-x-in-memory-cache");
	private final MetadataTriplifier metadataTriplifier = new MetadataTriplifier();
	private final Map<String, DatasetGraph> executedFacadeXIris;
	private final ExecutionContext execCxt;

	public DatasetGraphCreator(ExecutionContext execCxt) {
		this.execCxt = execCxt;

		if (!execCxt.getContext().isDefined(inMemoryCache)) {
			logger.trace("Initialising in-memory cache");
			execCxt.getContext().set(inMemoryCache, new HashMap<String, DatasetGraph>());
		}
		executedFacadeXIris = execCxt.getContext().get(inMemoryCache);
	}


	public DatasetGraph getDatasetGraph(Triplifier t, Properties p, Op op) throws IOException {
		DatasetGraph dg;
		if (t == null) {
			return DatasetGraphFactory.create();
		}
		// If the operation was already executed in a previous call, reuse the same
		// in-memory graph
		// XXX Future implementations may use a caching system
		if (executedFacadeXIris.containsKey(getInMemoryCacheKey(p, op)))
			return executedFacadeXIris.get(getInMemoryCacheKey(p, op));

		logger.trace("Properties extracted: {}", p);
		String urlLocation = p.getProperty(IRIArgument.LOCATION.toString());

		logger.trace("Triplifier {}\n{}", t.getClass().toString(), op);
		dg = triplify(op, p, t);

		logger.debug("triplification done -- commiting and ending the write txn");
		dg.commit();
		dg.end();

		dg.begin(ReadWrite.READ);
		logger.debug("Size default graph {}", dg.getDefaultGraph().size());
		logger.debug("Size of the graph {}: {}", p.getProperty(IRIArgument.LOCATION.toString()), dg.getGraph(NodeFactory.createURI(p.getProperty(IRIArgument.LOCATION.toString()) + "#")).size());
		dg.end();

		if (urlLocation != null) {
			logger.trace("Location provided {}", urlLocation);
			URL url = Triplifier.instantiateURL(urlLocation);
			createMetadataGraph(dg, p);
			createAuditGraph(dg, p, url);
		}
		// Remember the triplified data
		if (!executedFacadeXIris.containsKey(getInMemoryCacheKey(p, op))) {
			executedFacadeXIris.put(getInMemoryCacheKey(p, op), dg);
			logger.debug("Graph added to in-memory cache");
		}
		// TODO wrap this in a txn or move it to a place where we are already in a txn
		// logger.trace("Triplified, #triples in default graph {} {}", dg.getDefaultGraph().size(), op.toString());

//		else {
//			logger.trace("No location, use content: {}", p.getProperty(IRIArgument.CONTENT.toString()));
//			dg = t.triplify(p);
//			logger.trace("Size: {} {}", dg.size(), dg.getDefaultGraph().size());
//
//		}

		return dg;
	}

	private void createMetadataGraph(DatasetGraph dg, Properties p) throws IOException {
		if (triplifyMetadata(p)) {
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(Triplifier.getRootArgument(p), p);
			metadataTriplifier.triplify(p, builder);
			dg.addGraph(NodeFactory.createURI(Triplifier.METADATA_GRAPH_IRI), builder.getDatasetGraph().getDefaultGraph());
		}
	}

	private boolean triplifyMetadata(Properties p) {
		boolean result = false;
		if (p.containsKey(IRIArgument.METADATA.toString())) {
			try {
				result = Boolean.parseBoolean(p.getProperty(IRIArgument.METADATA.toString()));
			} catch (Exception e) {
				result = false;
			}
		}
		return result;
	}

	private void createAuditGraph(DatasetGraph dg, Properties p, URL url) {
		if (p.containsKey("audit") && (p.get("audit").equals("1") || p.get("audit").equals("true"))) {
			logger.info("audit information in graph: {}", Triplifier.AUDIT_GRAPH_IRI);
			logger.info("{} triples loaded ({})", dg.getGraph(NodeFactory.createURI(url.toString())).size(), NodeFactory.createURI(url.toString()));
			String SD = "http://www.w3.org/ns/sparql-service-description#";
			Model audit = ModelFactory.createDefaultModel();
			Resource root = audit.createResource(Triplifier.AUDIT_GRAPH_IRI + "#root");

			// For each graph
			Iterator<Node> graphs = dg.listGraphNodes();
			while (graphs.hasNext()) {
				Node g = graphs.next();
				Resource auditGraph = audit.createResource(g.getURI());
				root.addProperty(ResourceFactory.createProperty(SD + "namedGraph"), auditGraph);
				auditGraph.addProperty(RDF.type, ResourceFactory.createResource(SD + "NamedGraph"));
				auditGraph.addProperty(ResourceFactory.createProperty(SD + "name"), g.getURI());
				auditGraph.addLiteral(VOID.triples, dg.getGraph(g).size());
			}
			dg.addGraph(NodeFactory.createURI(Triplifier.AUDIT_GRAPH_IRI), audit.getGraph());
		}
	}

	private DatasetGraph triplify(final Op op, Properties p, Triplifier t) throws IOException {
		DatasetGraph dg;

		Integer strategy = PropertyUtils.detectStrategy(p, execCxt);
//		null;
//		// Local value for strategy?
//		String localStrategy = p.getProperty(IRIArgument.STRATEGY.toString());
//		// Global value for strategy?
//		Integer globalStrategy = execCxt.getContext().get(FacadeXOpExecutor.strategy);
//		if(localStrategy != null){
//			if(globalStrategy!=null){
//				logger.warn("Local strategy {} overriding global strategy {}", localStrategy, globalStrategy);
//			}
//			strategy = Integer.parseInt(localStrategy);
//		} else if(globalStrategy!=null){
//			strategy = globalStrategy;
//		} else{
//			// Defaul strategy
//			strategy = 1;
//		}
		URL url = Triplifier.getLocation(p);
		String resourceId = Triplifier.getResourceId(p);

		logger.debug("Execution strategy: {} {}", strategy, op.toString());
		if (t != null) {
			try {

				FacadeXGraphBuilder builder;
				if (strategy == 1) {
					logger.trace("Executing: {} [strategy={}]", p, strategy);
					builder = new TripleFilteringFacadeXGraphBuilder(resourceId, op, p);
				} else {
					logger.trace("Executing: {} [strategy={}]", p, strategy);
					builder = new BaseFacadeXGraphBuilder(resourceId, p);
				}
				t.triplify(p, builder);
				dg = builder.getDatasetGraph();
			} catch (Exception e) {
				if (p.containsKey(IRIArgument.OP_SERVICE_SILENT.toString()) && p.getProperty(IRIArgument.OP_SERVICE_SILENT.toString()).equals("true")) {
					// as per https://www.w3.org/TR/sparql11-federated-query/#serviceFailure
					// if silent is specified "errors encountered while accessing a remote SPARQL
					// endpoint should be ignored"
					//
					// so ignore errors by just returning an empty graph
					logger.warn("Errors encountered but the silent keyword was specified. Returning empty graph.");
					dg = DatasetFactory.create().asDatasetGraph();
				} else {
					throw new IOException(e);
				}
			}
		} else {
			// If triplifier is null, return an empty graph
			logger.error("No triplifier available for the input format! Returning empty graph.");
			dg = DatasetFactory.create().asDatasetGraph();
		}

		// logger.trace("Union graph size {}",dg.getUnionGraph().size());
		// logger.trace("Default graph size {}", dg.getDefaultGraph().size());
		return dg;
	}

	private String getInMemoryCacheKey(Properties properties, Op op) {
		return properties.toString() + op.toString();
	}
}
