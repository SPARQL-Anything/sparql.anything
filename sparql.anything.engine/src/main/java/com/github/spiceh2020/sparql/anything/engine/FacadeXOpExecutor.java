package com.github.spiceh2020.sparql.anything.engine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIterDefaulting;
import org.apache.jena.sparql.engine.iterator.QueryIterRepeatApply;
import org.apache.jena.sparql.engine.iterator.QueryIterSingleton;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.util.Symbol;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.facadeiri.FacadeIRIParser;
import com.github.spiceh2020.sparql.anything.metadata.MetadataTriplifier;
import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class FacadeXOpExecutor extends OpExecutor {

	private TriplifierRegister triplifierRegister;

	private static final Logger logger = LoggerFactory.getLogger(FacadeXOpExecutor.class);
	private final MetadataTriplifier metadataTriplifier = new MetadataTriplifier();

	private Map<String, DatasetGraph> executedFacadeXIris;

	private final static Symbol inMemoryCache = Symbol.create("facade-x-in-memory-cache");
	// TODO
//	private final static Symbol audit = Symbol.create("facade-x-audit");
	public final static Symbol strategy = Symbol.create("facade-x-strategy");

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

		if (t == null)
			return DatasetGraphFactory.create();

		if (urlLocation != null) {
			logger.trace("Location provided {}", urlLocation);
			URL url = instantiateURL(urlLocation);
			dg = triplify(op, p, t, url);
			createMetadataGraph(dg, p, url);
			createAuditGraph(dg, p, url);
			// Remember the triplified data
			if (!executedFacadeXIris.containsKey(getInMemoryCacheKey(p, op))) {
				executedFacadeXIris.put(getInMemoryCacheKey(p, op), dg);
				logger.debug("Graph added to in-memory cache");
			}
		} else {
			logger.trace("No location, use content: {}", p.getProperty(IRIArgument.CONTENT.toString()));
			dg = t.triplify(p.getProperty(IRIArgument.CONTENT.toString()), p);
			logger.trace("Size: {} {}", dg.size(), dg.getDefaultGraph().size());

		}

		return dg;
	}

	protected QueryIterator execute(final OpService opService, QueryIterator input) {

		logger.trace("SERVICE uri: {}\n{}", opService.getService(), opService.toString());

		if (opService.getService().isVariable())
			return postponeService(opService, input);

		if (opService.getService().isURI() && isFacadeXURI(opService.getService().getURI())) {

			logger.trace("Facade-X uri: {}", opService.getService());

			try {

				Properties p = getProperties(opService.getService().getURI(), opService);

				DatasetGraph dg = getDatasetGraph(p, opService.getSubOp());

				logger.trace("Executing sub op {} {}", opService.getSubOp().toString(), dg.getDefaultGraph().size());

				return QC.execute(opService.getSubOp(), input,
						new ExecutionContext(execCxt.getContext(), dg.getDefaultGraph(), dg, execCxt.getExecutor()));

			} catch (IllegalArgumentException | SecurityException | IOException | InstantiationException
					| IllegalAccessException | InvocationTargetException | NoSuchMethodException
					| ClassNotFoundException e) {

				logger.error("An error occurred", e);

				throw new RuntimeException(e);

			} catch (UnboundVariableException e) {

				// Proceed with the next operation
				return QC.execute(opService.getSubOp(), input, execCxt);
			}

		}

		logger.trace("Not a Variable and not a IRI: {}", opService.getService());
		return super.execute(opService, input);
	}

	QueryIterator postponeService(final OpService opService, QueryIterator input) {
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

	QueryIterator postponeBGP(final OpBGP opBGP, QueryIterator input) {
		// Postpone to next iteration
		return new QueryIterRepeatApply(input, execCxt) {

			@Override
			protected QueryIterator nextStage(Binding binding) {
				logger.trace("Binding {}", Utils.bindingToString(binding));
				Op op2 = QC.substitute(opBGP, binding);
				QueryIterator thisStep = QueryIterSingleton.create(binding, this.getExecContext());
				QueryIterator cIter = QC.execute(op2, thisStep, super.getExecContext());
				cIter = new QueryIterDefaulting(cIter, binding, this.getExecContext());
				return cIter;
			}
		};
	}

	private URL instantiateURL(String urlLocation) throws MalformedURLException {
		URL url;
		try {
			url = new URL(urlLocation);
		} catch (MalformedURLException u) {
			logger.trace("Malformed url interpreting as file");
			url = new File(urlLocation).toURI().toURL();
		}
		return url;
	}

	private DatasetGraph triplify(final Op opService, Properties p, Triplifier t, URL url) throws IOException {
		DatasetGraph dg;
		Integer strategy = execCxt.getContext().get(FacadeXOpExecutor.strategy);
		if (strategy == null) {
			strategy = 1;
		}
		logger.debug("Execution strategy: {}", strategy);
		if (t != null) {
			if (strategy == 1) {
				logger.trace("Executing: {} {} [strategy={}]", url, p, strategy);
				dg = t.triplify(url, p, opService);
			} else {
				logger.trace("Executing: {} {} [strategy={}]", url, p, strategy);
				dg = t.triplify(url, p);
			}
		} else {
			// If triplifier is null, return an empty graph
			logger.error("No triplifier available for the input format!");
			dg = DatasetFactory.create().asDatasetGraph();
		}
		return dg;
	}

	private void createMetadataGraph(DatasetGraph dg, Properties p, URL url) throws IOException {
		if (triplifyMetadata(p)) {
			dg.addGraph(NodeFactory.createURI(Triplifier.METADATA_GRAPH_IRI),
					metadataTriplifier.triplify(url, p).getDefaultGraph());
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
			logger.trace("Guessing triplifier using file extension ");
			String tt = triplifierRegister.getTriplifierForExtension(FilenameUtils.getExtension(urlLocation));
			logger.trace("Guessed extension: {} :: {} ", FilenameUtils.getExtension(urlLocation), tt);
			t = (Triplifier) Class.forName(tt).getConstructor().newInstance();
		} else {
			logger.trace("No location provided, using the Text triplifier");
			t = (Triplifier) Class.forName("com.github.spiceh2020.sparql.anything.text.TextTriplifier").getConstructor()
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
		} else {
			properties = new Properties();
		}

		Op next = opService.getSubOp();
		logger.trace("Class Next operator {}", next.getClass());
		FXBGPFinder vis = new FXBGPFinder();
		next.visit(vis);
		if (vis.getServiceBGP() != null) {
			logger.trace("BGP Extracted {}:{}", vis.getServiceBGP().toString(), properties.size());
			extractPropertiesFromOpGraph(properties, vis.getServiceBGP());
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
		int c = 0;
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES)) {
				if (t.getObject().isVariable()) {
					Var s = Var.alloc("s" + c);
					Var p = Var.alloc("p" + c);
					pattern.add(new Triple(s, p, t.getObject()));
					c++;
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

	protected QueryIterator execute(final OpBGP opBGP, QueryIterator input) {
		logger.trace("executing  BGP {}", opBGP.toString());
		logger.trace("Size: {} {}", this.execCxt.getDataset().size(),
				this.execCxt.getDataset().getDefaultGraph().size());
		Properties p = new Properties();
		try {
			extractPropertiesFromOpGraph(p, opBGP);
			if (p.size() > 0) {

				logger.trace("BGP Properties {}", p.toString());

				DatasetGraph dg = getDatasetGraph(p, opBGP);

				return QC.execute(excludeFXProperties(opBGP), input,
						new ExecutionContext(execCxt.getContext(), dg.getDefaultGraph(), dg, execCxt.getExecutor()));

			}
		} catch (UnboundVariableException e) {

			OpBGP fakeBGP = extractFakePattern(opBGP);

			logger.trace("Fake pattern {}", fakeBGP.toString());

			return postponeBGP(opBGP, QC.executeDirect(fakeBGP.getPattern(), input, execCxt));

		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		return super.execute(opBGP, input);
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

	protected boolean isFacadeXURI(String uri) {
		if (uri.startsWith(FacadeIRIParser.SPARQL_ANYTHING_URI_SCHEMA)) {
			return true;
		}
		return false;
	}

}
