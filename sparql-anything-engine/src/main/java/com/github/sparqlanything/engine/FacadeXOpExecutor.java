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

package com.github.sparqlanything.engine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProcedure;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.table.TableUnit;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIterAssign;
import org.apache.jena.sparql.engine.iterator.QueryIterDefaulting;
import org.apache.jena.sparql.engine.iterator.QueryIterRepeatApply;
import org.apache.jena.sparql.engine.iterator.QueryIterSingleton;
import org.apache.jena.sparql.engine.join.Join;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.pfunction.PropFuncArg;
import org.apache.jena.sparql.util.Symbol;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VOID;
import org.apache.jena.query.TxnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sparqlanything.facadeiri.FacadeIRIParser;
import com.github.sparqlanything.metadata.MetadataTriplifier;
import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.TripleFilteringFacadeXBuilder;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import com.github.sparqlanything.zip.FolderTriplifier;

public class FacadeXOpExecutor extends OpExecutor {

	private TriplifierRegister triplifierRegister;
	public final static String PROPERTY_OPSERVICE_SILENT = "opservice.silent";

	private static final Logger logger = LoggerFactory.getLogger(FacadeXOpExecutor.class);
	private final MetadataTriplifier metadataTriplifier = new MetadataTriplifier();

	private Map<String, DatasetGraph> executedFacadeXIris;

	private final static Symbol inMemoryCache = Symbol.create("facade-x-in-memory-cache");
	// TODO
//	private final static Symbol audit = Symbol.create("facade-x-audit");
	public final static Symbol strategy = Symbol.create("facade-x-strategy");

	protected QueryIterator execute(OpPropFunc opPropFunc, QueryIterator input) {
		logger.trace(opPropFunc.toString());
		return super.execute(opPropFunc, input);
	}

	private List<Triple> getPropFuncTriples(BasicPattern e) {
		List<Triple> result = new ArrayList<>();
		e.forEach(t -> {
			if (t.getPredicate().isURI()
					&& t.getPredicate().getURI().equals(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot")) {
				result.add(t);
			}
		});
		return result;
	}

	private OpPropFunc getOpPropFuncAnySlot(Triple t) {
		return new OpPropFunc(NodeFactory.createURI(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot"),
				new PropFuncArg(t.getSubject()), new PropFuncArg(t.getObject()), OpTable.create(new TableUnit()));
	}

	public FacadeXOpExecutor(ExecutionContext execCxt) {
		super(execCxt);
		triplifierRegister = TriplifierRegister.getInstance();

		if (!execCxt.getContext().isDefined(inMemoryCache)) {
			logger.trace("Initialising in-memory cache");
			execCxt.getContext().set(inMemoryCache, new HashMap<String, DatasetGraph>());
		}

		executedFacadeXIris = execCxt.getContext().get(inMemoryCache);
	}

	private String getInMemoryCacheKey(Properties properties, Op op) {
		return properties.toString() + op.toString();
	}

	protected QueryIterator execute(OpProcedure opProc, QueryIterator input) {
		logger.trace("Op Procedure {}", opProc.toString());
		return super.exec(opProc, input);
	}

	private DatasetGraph getDatasetGraph(Properties p, Op op) throws IOException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		DatasetGraph dg = null;

		// If the operation was already executed in a previous call, reuse the same
		// in-memory graph
		// XXX Future implementations may use a caching system
		if (executedFacadeXIris.containsKey(getInMemoryCacheKey(p, op)))
			return executedFacadeXIris.get(getInMemoryCacheKey(p, op));

		logger.trace("Properties extracted: {}", p.toString());
		String urlLocation = p.getProperty(IRIArgument.LOCATION.toString());
		Triplifier t = getTriplifier(p);

		if (t == null) {
			logger.trace("No triplifier");
            // return TDB2Factory.createDataset().asDatasetGraph();
			// maybe we don't need TDB2s for all these throw away graphs
			return DatasetGraphFactory.create();
		}

		logger.trace("Triplifier {}\n{}", t.getClass().toString(), op.toString());
		dg = triplify(op, p, t);
		// after triplification commit and end the txn
		if(dg.supportsTransactions()){
			logger.debug("triplification done -- commiting and ending the big write txn");
			dg.commit();
			dg.end();
		}
		// TODO after triplification should we end the (write) txn?
		//   and maybe being another one for reading
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
		// TODO wrap this in a txn
		// logger.trace("Triplified, #triples in default graph {} {}", dg.getDefaultGraph().size(), op.toString());

//		else {
//			logger.trace("No location, use content: {}", p.getProperty(IRIArgument.CONTENT.toString()));
//			dg = t.triplify(p);
//			logger.trace("Size: {} {}", dg.size(), dg.getDefaultGraph().size());
//
//		}

		return dg;
	}

	protected QueryIterator execute(final OpService opService, QueryIterator input) {
		logger.trace("SERVICE uri: {} {}", opService.getService(), opService.toString());
		if (opService.getService().isVariable())
			return postponeService(opService, input);
		if (opService.getService().isURI() && isFacadeXURI(opService.getService().getURI())) {
			logger.trace("Facade-X uri: {}", opService.getService());
			try {
				Properties p = getProperties(opService.getService().getURI(), opService);
				DatasetGraph dg = getDatasetGraph(p, opService.getSubOp());
				FacadeXExecutionContext ec = new FacadeXExecutionContext(
						new ExecutionContext(execCxt.getContext(), dg.getDefaultGraph(), dg, execCxt.getExecutor()));
				return QC.execute(opService.getSubOp(), input, ec);
			} catch (IllegalArgumentException | SecurityException | IOException | InstantiationException
					| IllegalAccessException | InvocationTargetException | NoSuchMethodException
					| ClassNotFoundException e) {
				logger.error("An error occurred", e);
				throw new RuntimeException(e);
			} catch (UnboundVariableException e) {
				// Proceed with the next operation
//				logger.trace("Unbound variables, BGP {}", e.getOpBGP().toString());
				OpBGP fakeBGP = extractFakePattern(e.getOpBGP());
				if (e.getOpTable() != null) {
					logger.trace("Executing table");
					QueryIterator qIterT = e.getOpTable().getTable().iterator(execCxt);
					QueryIterator qIter = Join.join(input, qIterT, execCxt);
					return postponeService(opService, qIter);
				} else if (e.getOpExtend() != null) {
					logger.trace("Executing op extend");

					QueryIterator qIter = exec(e.getOpExtend().getSubOp(), input);
					qIter = new QueryIterAssign(qIter, e.getOpExtend().getVarExprList(), execCxt, true);
					return postponeService(opService, qIter);
				}
				logger.trace("Executing fake pattern {}", fakeBGP);
				return postponeService(opService, QC.execute(fakeBGP, input, execCxt));
			}
		}
		logger.trace("Not a Variable and not a IRI: {}", opService.getService());
		return super.execute(opService, input);
	}

	private QueryIterator postponeService(final OpService opService, QueryIterator input) {
		logger.trace("is variable: {}", opService.getService());
		// Postpone to next iteration
		return new QueryIterRepeatApply(input, execCxt) {
			@Override
			protected QueryIterator nextStage(Binding binding) {
				Op op2 = QC.substitute(opService, binding);
				QueryIterator thisStep = QueryIterSingleton.create(binding, this.getExecContext());
				QueryIterator cIter = QC.execute(op2, thisStep, super.getExecContext());
				cIter = new QueryIterDefaulting(cIter, binding, this.getExecContext());
				return cIter;
			}
		};
	}

	private QueryIterator postponeBGP(final OpBGP opBGP, QueryIterator input) {
		// Postpone to next iteration
		return new QueryIterRepeatApply(input, execCxt) {
			@Override
			protected QueryIterator nextStage(Binding binding) {
				logger.trace(Utils.bindingToString(binding));
				Op op2 = QC.substitute(opBGP, binding);
				QueryIterator thisStep = QueryIterSingleton.create(binding, this.getExecContext());
				QueryIterator cIter = QC.execute(op2, thisStep, super.getExecContext());
				cIter = new QueryIterDefaulting(cIter, binding, this.getExecContext());
				return cIter;
			}
		};
	}

	private DatasetGraph triplify(final Op op, Properties p, Triplifier t) throws IOException {
		DatasetGraph dg;
		Integer strategy = execCxt.getContext().get(FacadeXOpExecutor.strategy);
		if (strategy == null) {
			strategy = 1;
		}

		URL url = Triplifier.getLocation(p);
		String resourceId;
		if (url == null) {
			// XXX This method of passing content seems only supported by the
			// TextTriplifier.
			logger.trace("No location, use content: {}", p.getProperty(IRIArgument.CONTENT.toString()));
			String id = Integer.toString(p.getProperty(IRIArgument.CONTENT.toString(), "").toString().hashCode());
			resourceId = "content:" + id;
		} else {
			resourceId = url.toString();
		}

		// logger.trace("No location, use content: {}",
		// p.getProperty(IRIArgument.CONTENT.toString()));
//			dg = t.triplify(p);
//			logger.trace("Size: {} {}", dg.size(), dg.getDefaultGraph().size());

		logger.debug("Execution strategy: {} {}", strategy, op.toString());
		if (t != null) {
			FacadeXGraphBuilder builder;
			if (strategy == 1) {
				logger.trace("Executing: {} [strategy={}]", p, strategy);
				builder = new TripleFilteringFacadeXBuilder(resourceId, op, p);
			} else {
				logger.trace("Executing: {} [strategy={}]", p, strategy);
				builder = new BaseFacadeXBuilder(resourceId, p);
			}
			try {
				dg = t.triplify(p, builder);
			} catch (TriplifierHTTPException e) {
				if (p.getProperty(PROPERTY_OPSERVICE_SILENT).equals("true")) {
					// as per https://www.w3.org/TR/sparql11-federated-query/#serviceFailure
					// if silent is specified "errors encountered while accessing a remote SPARQL
					// endpoint should be ignored"
					//
					// so ignore errors by just returning an empty graph
					logger.warn("Errors encountered but the silent keyword was specified");
					dg = DatasetFactory.create().asDatasetGraph();
				} else {
					throw new IOException(e.toString());
				}
			}
		} else {
			// If triplifier is null, return an empty graph
			logger.error("No triplifier available for the input format!");
			dg = DatasetFactory.create().asDatasetGraph();
		}

		boolean startedTransactionHere = false ;
		if(dg.supportsTransactions() && !dg.isInTransaction()){
			logger.debug("begin small read txn"); // TODO logger here  and log elsewhere
			startedTransactionHere = true ;
			dg.begin(TxnType.READ);
		}
		logger.trace("union graph size {}",dg.getUnionGraph().size());
		logger.trace("Default graph size {}", dg.getDefaultGraph().size());
		if(startedTransactionHere){
			logger.debug("end small read txn");
			dg.end();
		}
		return dg;
	}

	private void createMetadataGraph(DatasetGraph dg, Properties p) throws IOException {
		if (triplifyMetadata(p)) {
			dg.addGraph(NodeFactory.createURI(Triplifier.METADATA_GRAPH_IRI),
					metadataTriplifier.triplify(p).getDefaultGraph());
		}
	}

	private void createAuditGraph(DatasetGraph dg, Properties p, URL url) {
		if (p.containsKey("audit") && (p.get("audit").equals("1") || p.get("audit").equals("true"))) {
			logger.info("audit information in graph: {}", Triplifier.AUDIT_GRAPH_IRI);
			logger.info("{} triples loaded ({})", dg.getGraph(NodeFactory.createURI(url.toString())).size(),
					NodeFactory.createURI(url.toString()));
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

	private Triplifier getTriplifier(Properties p) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Triplifier t;
		String urlLocation = p.getProperty(IRIArgument.LOCATION.toString());
		if (!p.containsKey(IRIArgument.LOCATION.toString()) && !p.containsKey(IRIArgument.CONTENT.toString())) {
			logger.error("Neither location nor content provided");
//			throw new RuntimeException("Neither location nor content provided");
			return null;
		}

		if (p.containsKey(IRIArgument.TRIPLIFIER.toString())) {
			logger.trace("Triplifier enforced");
			t = (Triplifier) Class.forName(p.getProperty(IRIArgument.TRIPLIFIER.toString())).getConstructor()
					.newInstance();
		} else if (p.containsKey(IRIArgument.MEDIA_TYPE.toString())) {
			logger.trace("MimeType enforced");
			t = (Triplifier) Class
					.forName(triplifierRegister
							.getTriplifierForMimeType(p.getProperty(IRIArgument.MEDIA_TYPE.toString())))
					.getConstructor().newInstance();
		} else if (p.containsKey(IRIArgument.LOCATION.toString())) {

			File f = new File(p.get(IRIArgument.LOCATION.toString()).toString().replace("file://", ""));

			logger.trace("Use location {}, exists on local FS? {}, is directory? {}", f.getAbsolutePath(), f.exists(),
					f.isDirectory());

			if (f.exists() && f.isDirectory()) {
				logger.trace("Return folder triplifier");
				t = new FolderTriplifier();
			} else if (IsFacadeXExtension.isFacadeXExtension(p.get(IRIArgument.LOCATION.toString()).toString())) {
				logger.trace("Guessing triplifier using file extension ");
				String tt = triplifierRegister.getTriplifierForExtension(FilenameUtils.getExtension(urlLocation));
				logger.trace("Guessed extension: {} :: {} ", FilenameUtils.getExtension(urlLocation), tt);
				t = (Triplifier) Class.forName(tt).getConstructor().newInstance();
			} else {
				return null;
			}

		} else {
			logger.trace("No location provided, using the Text triplifier");
			t = (Triplifier) Class.forName("com.github.sparqlanything.text.TextTriplifier").getConstructor()
					.newInstance();
		}
		return t;
	}

	private Properties getProperties(String url, OpService opService) throws UnboundVariableException {

		Properties properties;

		if (!url.equals(FacadeIRIParser.SPARQL_ANYTHING_URI_SCHEMA)) {
			FacadeIRIParser p = new FacadeIRIParser(url);
			properties = p.getProperties();

			// Setting defaults
			// namespace <urn:facade-x/ns#>
			if (!properties.containsKey(IRIArgument.NAMESPACE.toString())) {
				logger.trace("Setting default value for namespace: {}", Triplifier.XYZ_NS);
				properties.setProperty(IRIArgument.NAMESPACE.toString(), Triplifier.XYZ_NS);
			}
			if (opService.getSilent()) {
				// we can only see if silent was specified at the OpService so we need to stash
				// a boolean
				// at this point so we can use it when we triplify further down the Op tree
				properties.setProperty(PROPERTY_OPSERVICE_SILENT, "true");
			}
		} else {
			properties = new Properties();
		}

		Op next = opService.getSubOp();
		FXBGPFinder vis = new FXBGPFinder();
		next.visit(vis);
		logger.trace("Has Table {}", vis.hasTable());

		if (vis.getBGP() != null) {
			try {
				extractPropertiesFromOpGraph(properties, vis.getBGP());
			} catch (UnboundVariableException e) {
				if (vis.hasTable()) {
					logger.trace(vis.getOpTable().toString());
					logger.trace("BGP {}", vis.getBGP());
					logger.trace("Contains variable names {}",
							vis.getOpTable().getTable().getVarNames().contains(e.getVariableName()));
					if (vis.getOpTable().getTable().getVarNames().contains(e.getVariableName())) {
						e.setOpTable(vis.getOpTable());
					}
				}

				if (vis.getOpExtend() != null) {
					logger.trace("OpExtend {}", vis.getOpExtend());
					Iterator<Var> vars = vis.getOpExtend().getVarExprList().getVars().iterator();
					while (vars.hasNext()) {
						Var var = (Var) vars.next();
						if (var.getName().equals(e.getVariableName())) {
							e.setOpExtend(vis.getOpExtend());
						}
					}
				}

				throw e;
			}
			logger.trace("Number of properties {}", properties.size());
		} else {
			logger.trace("Couldn't find OpGraph");
		}

		return properties;
	}

	private void extractPropertiesFromOpGraph(Properties properties, OpBGP bgp) throws UnboundVariableException {
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES)) {
				if (t.getObject().isURI()) {
					properties.put(t.getPredicate().getURI().replace(Triplifier.FACADE_X_CONST_NAMESPACE_IRI, ""),
							t.getObject().getURI().toString());
				} else if (t.getObject().isLiteral()) {
					properties.put(t.getPredicate().getURI().replace(Triplifier.FACADE_X_CONST_NAMESPACE_IRI, ""),
							t.getObject().getLiteral().getValue().toString());
				} else if (t.getObject().isVariable()) {
					throw new UnboundVariableException(t.getObject().getName(), bgp);
				}
			}
		}
	}

	private OpBGP extractFakePattern(OpBGP bgp) {
		BasicPattern pattern = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES)) {
				if (t.getObject().isVariable()) {
					Var s = Var.alloc("s" + System.currentTimeMillis());
					Var p = Var.alloc("p" + System.currentTimeMillis());
					pattern.add(new Triple(s, p, t.getObject()));
				}
			}
		}
		return new OpBGP(pattern);
	}

	private OpBGP excludeFXProperties(OpBGP bgp) {
		BasicPattern result = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES))
				continue;
			result.add(t);
		}
		return new OpBGP(result);
	}

	private OpBGP excludeOpPropFunction(OpBGP bgp) {
		BasicPattern result = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getPredicate().isURI()
					&& t.getPredicate().getURI().equals(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot"))
				continue;
			result.add(t);
		}
		return new OpBGP(result);
	}

	protected QueryIterator execute(final OpPath s, QueryIterator input) {
//		logger.trace("Execute OpPath {} {}", s.toString(), Utils.queryIteratorToString( super.execute(s, input)));
		logger.trace("Execute OpPath {} ", s.toString());

		return super.execute(s, input);

	}

	protected QueryIterator execute(final OpBGP opBGP, QueryIterator input) {

		if(this.execCxt.getClass()!=FacadeXExecutionContext.class) {
			return super.execute(opBGP, input);
		}

		// i think we can consider this the start of the query and therefore the read txn
		boolean startedTransactionHere = false ;
		if(this.execCxt.getDataset().supportsTransactions() && ! this.execCxt.getDataset().isInTransaction()){
			logger.debug("begin big read txn"); // TODO logger here  and log elsewhere
			startedTransactionHere = true ;
			this.execCxt.getDataset().begin(TxnType.READ);
		}
		// TODO where to end the read txn it?

		
		
		logger.trace("executing  BGP {}", opBGP.toString());
		logger.trace("Size: {} {}", this.execCxt.getDataset().size(),
				this.execCxt.getDataset().getDefaultGraph().size());

		List<Triple> l = getPropFuncTriples(opBGP.getPattern());
		logger.trace("Triples with OpFunc: {}", l.size());
		QueryIterator input2 = input;
		for (Triple t : l) {
			input2 = QC.execute(getOpPropFuncAnySlot(t), input2, execCxt);
		}

		Properties p = new Properties();
		try {
			logger.debug("Input BGP {} ", opBGP.toString());
			extractPropertiesFromOpGraph(p, opBGP);
			if (p.size() > 0) {
				// if we have FX properties we at least need to excludeFXProperties()
				logger.trace("BGP Properties {}", p.toString());
				DatasetGraph dg;
				if (this.execCxt.getDataset().isEmpty()) {
					// we only need to call getDatasetGraph() if we have an empty one
					// otherwise we could triplify the same data multiple times
					dg = getDatasetGraph(p, opBGP);
				} else {
					dg = this.execCxt.getDataset();
				}
				return QC.execute(excludeOpPropFunction(excludeFXProperties(opBGP)), input2,
						new ExecutionContext(execCxt.getContext(), dg.getDefaultGraph(), dg, execCxt.getExecutor()));
			}
		} catch (UnboundVariableException e) {
			logger.trace("Unbound variables");
			OpBGP fakeBGP = extractFakePattern(opBGP);
			return postponeBGP(excludeOpPropFunction(opBGP), QC.executeDirect(fakeBGP.getPattern(), input2, execCxt));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| ClassNotFoundException | IOException e) {
			logger.error(e.getMessage());
		}
		logger.trace("Execute default {} {}", opBGP.toString(), excludeOpPropFunction(opBGP).toString());
		return super.execute(excludeOpPropFunction(opBGP), input2);
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

	private boolean isFacadeXURI(String uri) {
		if (uri.startsWith(FacadeIRIParser.SPARQL_ANYTHING_URI_SCHEMA)) {
			return true;
		}
		return false;
	}

}
