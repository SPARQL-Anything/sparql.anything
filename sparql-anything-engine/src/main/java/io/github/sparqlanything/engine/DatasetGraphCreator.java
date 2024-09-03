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

package io.github.sparqlanything.engine;

import io.github.sparqlanything.metadata.MetadataTriplifier;
import io.github.sparqlanything.model.*;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class DatasetGraphCreator {

	private static final Logger logger = LoggerFactory.getLogger(DatasetGraphCreator.class);
	private final MetadataTriplifier metadataTriplifier = new MetadataTriplifier();
	private final ExecutionContext execCxt;

	public DatasetGraphCreator(ExecutionContext execCxt) {
		this.execCxt = execCxt;
	}

	public DatasetGraph getDatasetGraph(Triplifier t, Properties p, Op op) throws IOException {
		DatasetGraph dg;
		if (t == null) return DatasetGraphFactory.create();

		boolean useCache = !PropertyUtils.getBooleanProperty(p, IRIArgument.NO_CACHE);

		if (useCache && FacadeX.executedFacadeXIris.containsKey(getInMemoryCacheKey(p, op))) {
			dg = FacadeX.executedFacadeXIris.get(getInMemoryCacheKey(p, op));
			createAuditGraph(dg, p, true, op);
			return dg;
		}

		dg = triplify(op, p, t);
		createAuditGraph(dg, p, false, op);
		createMetadataGraph(dg, p);

		dg.commit();
		dg.end();

		// Remember the triplified data
		persistDatasetGraphInCache(p, op, dg, useCache);

		return dg;
	}

	private String getInMemoryCacheKey(Properties properties, Op op) {
		String key = properties.toString().concat(op.toString());
		logger.trace("Cache key {}", key);
		return key;
	}

	private void createAuditGraph(DatasetGraph dg, Properties p, boolean b, Op op) {
		if (PropertyUtils.getBooleanProperty(p, IRIArgument.AUDIT)) {
			String SD = "http://www.w3.org/ns/sparql-service-description#";
			Model audit = ModelFactory.createDefaultModel();
			Resource root = audit.createResource(Triplifier.AUDIT_GRAPH_IRI + "#root");
			Node nodeGraph = NodeFactory.createURI(Triplifier.AUDIT_GRAPH_IRI);

			// Check if the audit graph already exists (this could happen if the dataset graph comes from the cache)
			// In this case the audit the value of the cached graph property is updated
			if (dg.containsGraph(nodeGraph)) {
				Set<Node> graphNodes = new HashSet<>();
				dg.find(nodeGraph, null, NodeFactory.createURI(Triplifier.FACADE_X_CACHED_GRAPH), null).forEachRemaining(q -> {
					graphNodes.add(q.getSubject());
				});
				for (Node g : graphNodes) {
					dg.delete(nodeGraph, g, NodeFactory.createURI(Triplifier.FACADE_X_CACHED_GRAPH), NodeFactory.createLiteralByValue(false));
					dg.add(nodeGraph, g, NodeFactory.createURI(Triplifier.FACADE_X_CACHED_GRAPH), NodeFactory.createLiteralByValue(true));
				}
				return;
			}

			// For each graph
			Iterator<Node> graphs = dg.listGraphNodes();
			while (graphs.hasNext()) {
				Node g = graphs.next();
				Resource auditGraph = audit.createResource(g.getURI());
				root.addProperty(ResourceFactory.createProperty(SD.concat("namedGraph")), auditGraph);
				auditGraph.addProperty(RDF.type, ResourceFactory.createResource(SD.concat("NamedGraph")));
				auditGraph.addProperty(ResourceFactory.createProperty(SD.concat("name")), g.getURI());
				auditGraph.addLiteral(VOID.triples, dg.getGraph(g).size());
				auditGraph.addLiteral(auditGraph.getModel().createProperty(Triplifier.FACADE_X_CACHED_GRAPH), b);
				auditGraph.addProperty(auditGraph.getModel().createProperty(Triplifier.FACADE_X_CACHED_GRAPH_CREATION), new XSDDateTime(Calendar.getInstance()).toString(), XSDDatatype.XSDdateTime);
				auditGraph.addProperty(auditGraph.getModel().createProperty(Triplifier.FACADE_X_SPARQL_ALGEBRA), op.toString());
			}
			dg.addGraph(nodeGraph, audit.getGraph());
		}
	}

	private DatasetGraph triplify(final Op op, Properties p, Triplifier t) throws IOException {
		DatasetGraph dg;

		Integer strategy = PropertyExtractor.detectStrategy(p, execCxt);
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
					builder = new BaseFacadeXGraphBuilder(p);
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

		return dg;
	}

	private void createMetadataGraph(DatasetGraph dg, Properties p) throws IOException {
		if (triplifyMetadata(p)) {
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			metadataTriplifier.triplify(p, builder);
			dg.addGraph(NodeFactory.createURI(Triplifier.METADATA_GRAPH_IRI), builder.getDatasetGraph().getDefaultGraph());
		}
	}

	private void persistDatasetGraphInCache(Properties p, Op op, DatasetGraph dg, boolean useCache) {
		if (useCache && !FacadeX.executedFacadeXIris.containsKey(getInMemoryCacheKey(p, op))) {
			FacadeX.executedFacadeXIris.put(getInMemoryCacheKey(p, op), dg);
			logger.debug("Graph added to in-memory cache");
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
}
