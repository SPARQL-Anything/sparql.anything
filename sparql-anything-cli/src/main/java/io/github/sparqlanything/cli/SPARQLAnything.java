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



package io.github.sparqlanything.cli;

import com.github.jsonldjava.shaded.com.google.common.io.Files;
import io.github.basilapi.basil.sparql.*;
import io.github.sparqlanything.engine.FXSymbol;
import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.engine.FacadeXOpExecutor;
import io.github.sparqlanything.model.SPARQLAnythingConstants;
import io.github.sparqlanything.model.Utils;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.mgt.Explain;
import org.apache.jena.sys.JenaSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class SPARQLAnything {


	private static final Logger logger = LoggerFactory.getLogger(SPARQLAnything.class);
	private static Long duration = null;

	// TODO This should be moved to the engine module
	private static void initSPARQLAnythingEngine() {
		JenaSystem.init();

		// Setting up the Geosparql module if the dependency is included
		try {
			Class<?> k = Class.forName("org.apache.jena.geosparql.configuration.GeoSPARQLConfig");
			Method m = k.getMethod("setupMemoryIndex");
			m.invoke(null);
		} catch (ClassNotFoundException e) {
			//logger.warn("jena-geosparql dependency not available");
		} catch (NoSuchMethodException e) {
			logger.warn("NoSuchMethodException");
		} catch (InvocationTargetException e) {
			logger.warn("InvocationTargetException");
		} catch (IllegalAccessException e) {
			logger.warn("IllegalAccessException");
		}

		// Register the JSON-LD parser factory for extension  .json
		ReaderRIOTFactory parserFactoryJsonLD = new RiotUtils.ReaderRIOTFactoryJSONLD();
		RDFParserRegistry.registerLangTriples(RiotUtils.JSON, parserFactoryJsonLD);
		// Setup FX executor
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
	}

	private static QueryExecution createQueryExecution(Query query, Dataset kb, String[] configurations) {
		QueryExecution qExec = QueryExecutionFactory.create(query, kb);
		setConfigurationsToContext(configurations, qExec);
		return qExec;
	}

	private static void executeQuery(String outputFormat, Dataset kb, Query query, PrintStream pw, String[] configurations)
		throws FileNotFoundException {
		Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.BEFORE_QUERY_EXECUTION);
		try (QueryExecution qe = createQueryExecution(query, kb, configurations)) {
			if (query.isSelectType()) {
				ResultSet rs = qe.execSelect();
				switch (outputFormat) {
					case "JSON":
						ResultSetFormatter.outputAsJSON(pw, rs);
						break;
					case "XML":
						ResultSetFormatter.outputAsXML(pw, rs);
						break;
					case "CSV":
						ResultSetFormatter.outputAsCSV(pw, rs);
						break;
					case "TEXT":
						pw.println(ResultSetFormatter.asText(rs));
						break;
					default:
						throw new RuntimeException("Unsupported format: " + outputFormat);
				}

			} else if (query.isAskType()) {
				Boolean ask = qe.execAsk();
				switch (outputFormat) {
					case "JSON":
						ResultSetFormatter.outputAsJSON(pw, ask);
						break;
					case "XML":
						ResultSetFormatter.outputAsXML(pw, ask);
						break;
					case "CSV":
						ResultSetFormatter.outputAsCSV(pw, ask);
						break;
					case "TEXT":
						pw.print(ask);
						break;
					default:
						throw new RuntimeException("Unsupported format: " + outputFormat);
				}
			} else if (query.isDescribeType() || query.isConstructType()) {
				Model m;
				Dataset d = null;
				if (query.isConstructType()) {
					d = qe.execConstructDataset();
					// .execConstructDataset (instead of .execConstruct) so we can construct quads too
					// as described here: https://jena.apache.org/documentation/query/construct-quad.html
					m = d.getDefaultModel();
				} else {
					m = qe.execDescribe();
					// d = new DatasetImpl(m);
				}
				if (outputFormat.equals("JSON") || outputFormat.equals(Lang.JSONLD.getName())) {
					// JSON-LD format.equals(Lang.JSONLD11.getName())
					RDFDataMgr.write(pw, m, Lang.JSONLD);
				} else if (outputFormat.equals(Lang.JSONLD11.getName())) {
					RDFDataMgr.write(pw, m, Lang.JSONLD11);
				} else if (outputFormat.equals("XML")) {
					// RDF/XML
					RDFDataMgr.write(pw, m, Lang.RDFXML);
				} else if (outputFormat.equals("TTL") || outputFormat.equals(Lang.TURTLE.getName())) {
					// TURTLE
					RDFDataMgr.write(pw, m, Lang.TTL);
				} else if (outputFormat.equals("NT") || outputFormat.equals(Lang.NTRIPLES.getName())) {
					// N-Triples
					RDFDataMgr.write(pw, m, Lang.NT);
				} else if (outputFormat.equals("NQ") || outputFormat.equals(Lang.NQUADS.getName())) {
					// NQ
					RDFDataMgr.write(pw, Objects.requireNonNull(d), Lang.NQ);
				} else if (outputFormat.equals(Lang.TRIG.getName())) {
					// TRIG
					RDFDataMgr.write(pw, Objects.requireNonNull(d), Lang.TRIG);
				} else if (outputFormat.equals(Lang.TRIX.getName())) {
					// TRIG
					RDFDataMgr.write(pw, Objects.requireNonNull(d), Lang.TRIX);
				} else if (outputFormat.equals(Lang.CSV.getName())) {
					// CSV
					ResultSet rs = RiotUtils.asResultSet(Objects.requireNonNull(d.asDatasetGraph()));
					ResultSetFormatter.outputAsCSV(pw, rs);
				} else {
					throw new RuntimeException("Unsupported format: " + outputFormat);
				}
			}
		}
		Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.AFTER_QUERY_EXECUTION);
	}

	private static PrintStream getPrintStream(String fileName, boolean append) throws IOException {

		if (fileName != null) {
			Files.createParentDirs(new File(fileName));
			return new PrintStream(new FileOutputStream(fileName, append));
		}

		return System.out;
	}

	public static Query bindParameters(Specification specification, QuerySolution qs) throws Exception {
		VariablesBinder binder = new VariablesBinder(specification);

		List<String> missing = new ArrayList<>();
		for (QueryParameter qp : specification.getParameters()) {
			logger.trace("Looking into parameter {} ({})", qp.getName(), qp.isOptional());
			logger.trace("Checking against qs {}", qs);
			if (qs.contains("?" + qp.getName())) {
				RDFNode value = qs.get("?" + qp.getName());
				logger.debug("Setting {}->{}", qp.getName(), value.toString());
				binder.bind(qp.getName(), value.toString());
			} else if (!qp.isOptional()) {
				logger.warn("Missing parameter: {}", qp.getName());
				missing.add(qp.getName());
			}
		}

		if (!missing.isEmpty()) {
			StringBuilder ms = new StringBuilder();
			ms.append("Missing mandatory query parameters: ");
			for (String p : missing) {
				ms.append(p);
				ms.append("\t");
			}
			ms.append("\n");
			logger.error("Available query parameters not sufficient: {}", qs);
			throw new Exception(ms.toString());
		}
		Query q = binder.toQuery();
		logger.trace("Query after bindParameters: \n{}\n", q);
		return q;
	}

	public static String prepareOutputFromPattern(String template, QuerySolution qs) {
		logger.trace(" - template: {}", template);
		Iterator<String> vars = qs.varNames();
		while (vars.hasNext()) {

			String var = vars.next();

			Pattern p = Pattern.compile("[\\?|\\$]" + var + "([^0-9a-z_])",
				Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
			template = p.matcher(template).replaceAll(qs.get(var).toString() + "$1");
			if (logger.isTraceEnabled()) {
				logger.trace(" - var: {}", var);
				logger.trace(" - replacement: {}", qs.get(var).toString());
				logger.trace(" - template: {}", template);
			}
		}
		return template;
	}

	private static class ArgValuesAsResultSet implements ResultSetRewindable {
		private final String[] values;
		private List<String> variables;
		private Set<Binding> bindings;
		private Iterator<Binding> iterator;
		private Model model;
		private int row;

		ArgValuesAsResultSet(String[] values) {
			this.values = values;
			reset();
		}

		@Override
		public void reset() {
			this.variables = new ArrayList<>();
			this.bindings = new HashSet<>();
			this.model = ModelFactory.createDefaultModel();
			row = 0;
			// Populate
			HashMap<String, Set<Pair<String, String>>> var_val_map = new HashMap<>();
			for (String value : values) {
				String var = value.substring(0, value.indexOf('='));
				String val = value.substring(value.indexOf('=') + 1);
				if (!var_val_map.containsKey(var)) {
					var_val_map.put(var, new HashSet<>());
				}
				logger.debug("Value: {} -> {}", var, val);
				// If integer check if value represents range
				if (val.matches("^[0-9]+\\.\\.\\.[0-9]+$")) {
					logger.trace("Range");
					String[] vv = val.split("\\.\\.\\.");
					int from = Integer.parseInt(vv[0]);
					int to = Integer.parseInt(vv[1]);
					logger.trace("Value: {} -> range({},{})", var, from, to);
					for (int x = from; x <= to; x++) {
						var_val_map.get(var).add(Pair.of(var, Integer.toString(x)));
					}
				} else {
					var_val_map.get(var).add(Pair.of(var, val));
				}
			}
			// Generate bindings
			Set<Set<Object>> sets;
			if (var_val_map.values().size() > 1) {
				sets = cartesianProduct(var_val_map.values().toArray(new HashSet[var_val_map.values().size()]));
			} else {
				sets = new HashSet<>();

				for (Pair<String, String> p : var_val_map.entrySet().iterator().next().getValue()) {
					Set<Object> singleton = new HashSet<>();
					singleton.add(p);
					sets.add(singleton);
				}
			}
			for (Set<Object> s : sets) {
				final Map<Var, Node> bins = new HashMap<>();
				for (Object j : s) {
					Pair<String, String> p = (Pair<String, String>) j;
					String var = p.getLeft();
					String val = p.getRight();
					bins.put(Var.alloc(var), NodeFactory.createLiteralDT(val, XSDDatatype.XSDstring));
				}

				this.bindings.add(new Binding() {
					@Override
					public Iterator<Var> vars() {
						return bins.keySet().iterator();
					}

					@Override
					public Set<Var> varsMentioned() {
						throw new UnsupportedOperationException("Not implemented!");
					}

					@Override
					public boolean contains(Var var) {
						return bins.containsKey(var);
					}

					@Override
					public Node get(Var var) {
						return bins.get(var);
					}

					@Override
					public int size() {
						return bins.size();
					}

					@Override
					public boolean isEmpty() {
						return bins.isEmpty();
					}

					@Override
					public Binding detach() {
						throw new UnsupportedOperationException();
					}

					@Override
					public void forEach(BiConsumer<Var, Node> action) {
						// TODO Auto-generated method stub
						for (Var v : bins.keySet()) {
							Node n = bins.get(v);
							action.accept(v, n);
						}
					}
				});
			}
			this.iterator = bindings.iterator();
		}

		@Override
		public int size() {
			return bindings.size();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public QuerySolution next() {
			return nextSolution();
		}

		@Override
		public void forEachRemaining(Consumer<? super QuerySolution> consumer) {
			throw new UnsupportedOperationException("forEachRemaining not implemented");
		}

		@Override
		public QuerySolution nextSolution() {
			return new ResultBinding(this.model, nextBinding());
		}

		@Override
		public Binding nextBinding() {
			row += 1;
			return iterator.next();
		}

		@Override
		public int getRowNumber() {
			return row;
		}

		@Override
		public List<String> getResultVars() {
			return Collections.unmodifiableList(variables);
		}

		@Override
		public Model getResourceModel() {
			return this.model;
		}

		@Override
		public void close() {
			this.model.close();
		}

		// Credits: https://stackoverflow.com/a/714256/1035608
		public static Set<Set<Object>> cartesianProduct(Set<?>... sets) {
			if (sets.length < 2)
				throw new IllegalArgumentException(
					"Can't have a product of fewer than two sets (got " + sets.length + ")");

			return _cartesianProduct(0, sets);
		}

		private static Set<Set<Object>> _cartesianProduct(int index, Set<?>... sets) {
			Set<Set<Object>> ret = new HashSet<>();
			if (index == sets.length) {
				ret.add(new HashSet<>());
			} else {
				for (Object obj : sets[index]) {
					for (Set<Object> set : _cartesianProduct(index + 1, sets)) {
						set.add(obj);
						ret.add(set);
					}
				}
			}
			return ret;
		}
	}

	public static ResultSet prepareResultSetFromArgValues(String[] values) {
		return new ArgValuesAsResultSet(values);
	}

	static {
		Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.LOAD_MAIN_CLASS);
	}

	private static void setConfigurationsToContext(String[] configurations, QueryExecution qExec) {
		if (configurations != null) {
			// If the query has no FX service clauses the options will be added
			ServiceFinder sf = new ServiceFinder();
			Algebra.compile(qExec.getQuery()).visit(sf);


			if (!sf.hasFxService()) {
				qExec.getContext().setTrue(SPARQLAnythingConstants.NO_SERVICE_MODE);
			} else {
				logger.warn("Options passed with -c may be overwritten by options of the SERVICE IRI or in BGP of the query.");
			}

			for (String configuration : configurations) {
				String[] configurationSplit = configuration.split("=");
				qExec.getContext().set(FXSymbol.create(configurationSplit[0]), configurationSplit[1]);
			}
		}
	}


	private static String getOutputPattern(CLI cli, String outputFileName) {
		String outputPattern = cli.getOutputPattern();
		if (outputPattern != null && outputFileName != null) {
			logger.warn("Option 'output' is ignored: 'output-pattern' given.");
		}
		return outputPattern;
	}

	private static void strategy(CLI cli) {
		Integer strategy = cli.getStrategy();
		if (strategy != null) {
			if (strategy == 1 || strategy == 0 || strategy == 2) {
				ARQ.getContext().set(FacadeXOpExecutor.strategy, strategy);
			} else {
				logger.error("Invalid value for parameter 'strategy': {}", strategy);
			}
		}
	}

	private static void explain(CLI cli) {
		if (cli.isExplain()) {
			ARQ.setExecutionLogging(Explain.InfoLevel.ALL);
		}
	}

	private static void executeQueryWithValues(CLI cli, String query, Dataset kb, String outputFileName, String outputPattern, String[] values, String[] configurations) throws UnknownQueryTypeException {
		ResultSet parameters;
		if (values.length == 1 && new File(values[0]).exists()) {
			logger.debug("Input file name given");
			parameters = ResultSetFactory.load(values[0]);
		} else {
			parameters = new ArgValuesAsResultSet(values);
		}
		// Specifications
		Specification specification = SpecificationFactory.create("", query);
		// Iterate over parameters
		while (parameters.hasNext()) {
			QuerySolution qs = parameters.nextSolution();
			Query q;
			try {
				q = bindParameters(specification, qs);
			} catch (Exception e1) {
				logger.error("An exception occurred while evaluating the input parameters", e1);
				logger.error(
					"Iteration " + parameters.getRowNumber() + " failed with error: " + e1.getMessage());
				continue;
			}
			String outputFile = null;
			if (outputPattern != null) {
				outputFile = prepareOutputFromPattern(outputPattern, qs);
			} else {
				if (outputFileName != null) {
					outputFile = FilenameUtils.removeExtension(outputFileName) + (parameters.getRowNumber() == 1 && parameters.hasNext() ? "-" + parameters.getRowNumber() : "") + "." + FilenameUtils.getExtension(outputFileName);
				}
				// else stays null and output goes to STDOUT
			}
			// #528 Check no-clobber if output file already exists.
			if (outputFile != null && cli.getOutputNoClobber() && new File(outputFile).exists()) {
				logger.info("Skipping: `no-clobber` is on and file exists (iteration " + parameters.getRowNumber() + ")");
				continue;
			}
			// Remember if we are calling OS for a new file
			boolean newFile = false;
			if (outputFile != null && !new File(outputFile).exists()) {
				newFile = true;
			}
			try (PrintStream ps = getPrintStream(outputFile, cli.getOutputAppend())) {
				logger.trace("Executing Query: {}", q);
				executeQuery(cli.getFormat(q), kb, q, ps, configurations);
			} catch (Exception e1) {
				logger.error(
					"Iteration " + parameters.getRowNumber() + " failed with error: " + e1.getMessage());
				if (logger.isDebugEnabled()) {
					logger.error("Details:", e1);
				}
				// If an error occurred and the file is empty, delete the file.
				if (outputFile != null) {
					File f = new File(outputFile);
					if (newFile && f.exists() && f.length() == 0) {
						f.delete();
					}
				}
			}
		}
	}

	private static Dataset createDataset(String load) {
		Dataset kb = DatasetFactory.createGeneral();
		if (load != null) {

			logger.info("Loading data from: {}", load);
			Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.BEFORE_LOAD);
			// XXX Check if load is a URI first
			File loadSource;
			try {
				loadSource = new File(new URL(load).toURI());
			} catch (MalformedURLException | URISyntaxException e) {
				loadSource = new File(load);
			} catch (IllegalArgumentException e) {
				Model m = ModelFactory.createDefaultModel();
				RDFDataMgr.read(m, load);
				kb.addNamedModel(load, m);
				return kb;
			}
			if (loadSource.isDirectory()) {

				logger.info("Loading files from directory: {}", loadSource);
				// If directory, load all files
				Collection<File> files = FileUtils.listFiles(loadSource, null, true);
				for (File f : files) {
					logger.info("Adding file to be loaded: {}", f);
					try {
						Model m = ModelFactory.createDefaultModel();
						// read into the model.
						m.read(f.getAbsolutePath());
						kb.addNamedModel(f.toURI().toString(), m);
					} catch (Exception e) {
						logger.error("An error occurred while loading {}", f);
						logger.error(" - Problem was: {}", e.getMessage());
						if (logger.isDebugEnabled()) {
							logger.error("", e);
						}
					}
				}

				logger.info("Loaded {} triples", kb.asDatasetGraph().getUnionGraph().size());
			} else if (loadSource.isFile()) {
				// If it is a file, load it
				logger.info("Load file: {}", loadSource);
				Path base = Paths.get(".");
				try {
					Path p = loadSource.toPath();
					if (!p.isAbsolute()) {
						p = base.relativize(loadSource.toPath());
					}
					kb = DatasetFactory.create(p.toFile().toURI().toString());
				} catch (Exception e) {
					logger.error("An error occurred while loading {}", loadSource);
					logger.error(" - Problem was: ", e);
				}
			} else {
				if (!loadSource.exists()) {
					logger.error("Option 'load' failed (resource does not exist): {}", loadSource);
				} else {
					logger.error("Option 'load' failed (not a file or directory): {}", loadSource);
				}
				return kb;
			}
			Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.AFTER_LOAD);
		}

		return kb;
	}

	public static String callMain(String[] args) throws Exception {
		// Thanks to: https://stackoverflow.com/a/8708357/1035608
		// Create a stream to hold the output
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		// IMPORTANT: Save the old System.out!
		PrintStream old = System.out;
		// Tell Java to use your special stream
		System.setOut(ps);
		// Print some output: goes to your special stream
		main(args);
		// Put things back
		System.out.flush();
		System.setOut(old);
		// Show what happened
		return baos.toString();
	}

	private static void printProfileIfEnabled(CLI cli) throws FileNotFoundException {
		if (cli.getProfile() != null) {
			String outFile = cli.getProfile();
			if (outFile.isEmpty()) {
				outFile = "profile.tsv";
			}
			Utils.printProfile(outFile);
		}
	}


	public static void main(String[] args) throws Exception {

		Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.PROCESS_STARTS);

		logger.debug("SPARQL anything");

		CLI cli = new CLI();
		if (args.length == 0) {
			cli.printHelp();
			return;
		}
		try {
			cli.parse(args);
			Utils.loadJARs(cli.getLoadJar());
			String query = cli.getQuery();
			explain(cli);
			strategy(cli);
			Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.BEFORE_INIT);
			initSPARQLAnythingEngine();
			Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.AFTER_INIT);

			Dataset kb = createDataset(cli.getLoad());

			String outputFileName = cli.getOutputFile();
			String[] values = cli.getValues();
			String[] configurations = cli.getConfigurations();
			String outputPattern = getOutputPattern(cli, outputFileName);
			if (values == null) {
				logger.debug("No input file");

				// #528 Check no-clobber if output file already exists.
				if (outputFileName != null && cli.getOutputNoClobber() && new File(outputFileName).exists()) {
					logger.info("skipping: no-clobber is on and file exists");
					return;
				}
				Query q = QueryFactory.create(query);
				try (PrintStream ps = getPrintStream(outputFileName, cli.getOutputAppend())) {
					executeQuery(cli.getFormat(q), kb, q, ps, configurations);
				}
			} else {
				executeQueryWithValues(cli, query, kb, outputFileName, outputPattern, values, configurations);
			}

			Utils.profile(SPARQLAnythingConstants.PROFILE_EVENT.PROCESS_ENDS);
			printProfileIfEnabled(cli);

		} catch (FileNotFoundException e) {
			logger.error("File not found: {}", e.getMessage());
		} catch (QueryParseException | ParseException e1) {
			logger.error("SPARQL syntax error (or query file does not exists): {}", e1.getMessage());
		}

	}


}
