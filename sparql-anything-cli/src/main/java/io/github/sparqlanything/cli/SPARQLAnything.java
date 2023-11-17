/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.cli;

import io.github.basilapi.basil.sparql.QueryParameter;
import io.github.basilapi.basil.sparql.Specification;
import io.github.basilapi.basil.sparql.SpecificationFactory;
import io.github.basilapi.basil.sparql.VariablesBinder;
import io.github.sparqlanything.engine.FXSymbol;
import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.engine.FacadeXOpExecutor;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.mgt.Explain;
import org.apache.jena.sys.JenaSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class SPARQLAnything {


	private static final Logger logger = LoggerFactory.getLogger(SPARQLAnything.class);
	private static Long duration = null;

	// TODO This should be moved to the engine module
	private static void initSPARQLAnythingEngine() {
		// Register the JSON-LD parser factory for extension  .json
		ReaderRIOTFactory parserFactoryJsonLD    = new RiotUtils.ReaderRIOTFactoryJSONLD();
		RDFParserRegistry.registerLangTriples(RiotUtils.JSON, parserFactoryJsonLD);
		// Setup FX executor
		JenaSystem.init();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
	}

	private static QueryExecution createQueryExecution(Query query, Dataset kb,  String[] configurations){
		QueryExecution qExec = QueryExecutionFactory.create(query,kb);
		setConfigurationsToContext(configurations, qExec);
		return qExec;
	}

	private static void executeQuery(String outputFormat, Dataset kb, Query query, PrintStream pw, String[] configurations)
			throws FileNotFoundException {
		if(logger.isTraceEnabled()) {
			logger.trace("[time] Before executeQuery: {}", System.currentTimeMillis() - duration);
		}
		if (query.isSelectType()) {
			switch (outputFormat) {
				case "JSON":
					ResultSetFormatter.outputAsJSON(pw, createQueryExecution(query, kb, configurations).execSelect());
					break;
				case "XML":
					ResultSetFormatter.outputAsXML(pw, createQueryExecution(query, kb, configurations).execSelect());
					break;
				case "CSV":
					ResultSetFormatter.outputAsCSV(pw, createQueryExecution(query, kb, configurations).execSelect());
					break;
				case "TEXT":
					pw.println(ResultSetFormatter.asText(createQueryExecution(query, kb, configurations).execSelect()));
					break;
				default:
					throw new RuntimeException("Unsupported format: " + outputFormat);
			}
		} else if (query.isAskType()) {
			switch (outputFormat) {
				case "JSON":
					ResultSetFormatter.outputAsJSON(pw, createQueryExecution(query, kb, configurations).execAsk());
					break;
				case "XML":
					ResultSetFormatter.outputAsXML(pw, createQueryExecution(query, kb, configurations).execAsk());
					break;
				case "CSV":
					ResultSetFormatter.outputAsCSV(pw, createQueryExecution(query, kb, configurations).execAsk());
					break;
				case "TEXT":
					pw.print(createQueryExecution(query, kb, configurations).execAsk());
					break;
				default:
					throw new RuntimeException("Unsupported format: " + outputFormat);
			}
		} else if (query.isDescribeType() || query.isConstructType()) {
			Model m;
			Dataset d = null;
			if (query.isConstructType()) {
				d = createQueryExecution(query, kb, configurations).execConstructDataset();
				// .execConstructDataset (instead of .execConstruct) so we can construct quads too
				// as described here: https://jena.apache.org/documentation/query/construct-quad.html
				m = d.getDefaultModel();
			} else {
				m = createQueryExecution(query, kb, configurations).execDescribe();
				// d = new DatasetImpl(m);
			}
			if (outputFormat.equals("JSON") || outputFormat.equals(Lang.JSONLD.getName()) ) {
				// JSON-LD format.equals(Lang.JSONLD11.getName())
				RDFDataMgr.write(pw, m, Lang.JSONLD);
			} else if ( outputFormat.equals(Lang.JSONLD11.getName()) ) {
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
			} else {
				throw new RuntimeException("Unsupported format: " + outputFormat);
			}
		}
		if(logger.isTraceEnabled()) {
			logger.trace("[time] After executeQuery: {}", System.currentTimeMillis() - duration);
		}
	}

	private static PrintStream getPrintWriter(String fileName, boolean append) throws FileNotFoundException {

		if (fileName != null) {
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
			HashMap<String, Set<Pair>> var_val_map = new HashMap<>();
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

				for (Pair p : var_val_map.entrySet().iterator().next().getValue()) {
					Set<Object> singleton = new HashSet<>();
					singleton.add(p);
					sets.add(singleton);
				}
			}
			for (Set<Object> s : sets) {
				final Map<Var, Node> bins = new HashMap<>();
				for (Object j : s) {
					Pair p = (Pair) j;
					String var = (String) p.getLeft();
					String val = (String) p.getRight();
					bins.put(Var.alloc(var), NodeFactory.createLiteral(val));
				}

				this.bindings.add(new Binding() {
					@Override
					public Iterator<Var> vars() {
						return bins.keySet().iterator();
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
		if(logger.isTraceEnabled()){
			duration = System.currentTimeMillis();
			logger.trace("[time] Load main class: {}",duration);
		}
	}

	private static void setConfigurationsToContext(String[] configurations, QueryExecution qExec) {
		if (configurations != null) {
			for (String configuration : configurations) {
				String[] configurationSplit = configuration.split("=");
				qExec.getContext().set(FXSymbol.create(configurationSplit[0]), configurationSplit[1]);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		if(logger.isTraceEnabled()){
			logger.trace("[time] Process starts: {}", System.currentTimeMillis() - duration);
		}

		logger.debug("SPARQL anything");

		CLI cli = new CLI();
		if(args.length == 0){
			cli.printHelp();
			return;
		}
		try {
			cli.parse(args);
			String query = cli.getQuery();
			Integer strategy = cli.getStrategy();
			if(cli.explain()) {
				ARQ.setExecutionLogging(Explain.InfoLevel.ALL);
			}
			if (strategy != null) {
				if (strategy == 1 || strategy == 0 || strategy == 2) {
					ARQ.getContext().set(FacadeXOpExecutor.strategy, strategy);
				} else {
					logger.error("Invalid value for parameter 'strategy': {}", strategy);
				}
			}
			if(logger.isTraceEnabled()) {
				logger.trace("[time] Before init: {}", System.currentTimeMillis() - duration);
			}
			initSPARQLAnythingEngine();
			if(logger.isTraceEnabled()) {
				logger.trace("[time] After init: {}", System.currentTimeMillis() - duration);
			}
			Dataset kb = null;
			String load = cli.getLoad();
			if (load != null) {

				logger.info("Loading data from: {}", load);
				if(logger.isTraceEnabled()) {
					logger.trace("[time] Before load: {}", System.currentTimeMillis() - duration);
				}
				// XXX Check if load is a URI first
				File loadSource;
				try{
					loadSource = new File(new URL(load).toURI());
				}catch(MalformedURLException e){
					loadSource = new File(load);
				}
				if (loadSource.isDirectory()) {

					logger.info("Loading files from directory: {}", loadSource);
					// If directory, load all files
					List<File> list = new ArrayList<>();
					Collection<File> files = FileUtils.listFiles(loadSource, null, true);
					for (File f : files) {
						logger.info("Adding file to be loaded: {}", f);
						list.add(f);
					}
					kb = DatasetFactory.createGeneral();
					for (File f : list) {
						try {
							Model m = ModelFactory.createDefaultModel();
							// read into the model.
							m.read(f.getAbsolutePath());
							kb.addNamedModel(f.toURI().toString(), m);
						} catch (Exception e) {
							logger.error("An error occurred while loading {}", f);
							logger.error(" - Problem was: {}", e.getMessage());
							if(logger.isDebugEnabled()){
								logger.error("",e);
							}
						}
					}
					logger.info("Loaded {} triples", kb.asDatasetGraph().getUnionGraph().size());
				} else if (loadSource.isFile()) {
					// If it is a file, load it
					logger.info("Load file: {}", loadSource);
					Path base = Paths.get(".");
					try{
						Path p =  loadSource.toPath();
						if(!p.isAbsolute()){
							p = base.relativize(loadSource.toPath());
						}
						kb = DatasetFactory.create(p.toFile().toURI().toString());
					} catch (Exception e) {
						logger.error("An error occurred while loading {}", loadSource);
						logger.error(" - Problem was: ", e);
				}
				} else {
					if(!loadSource.exists()){
						logger.error("Option 'load' failed (resource does not exist): {}", loadSource);
					}else {
						logger.error("Option 'load' failed (not a file or directory): {}", loadSource);
					}
					return;
				}
				if(logger.isTraceEnabled()) {
					logger.trace("[time] After load: {}", System.currentTimeMillis() - duration);
				}
			} else {
				kb = DatasetFactory.createGeneral();
			}

			String outputFileName = cli.getOutputFile();
			String outputPattern = cli.getOutputPattern();
			String[] values = cli.getValues();
			String[] configurations = cli.getConfigurations();
			if (outputPattern != null && outputFileName != null) {
				logger.warn("Option 'output' is ignored: 'output-pattern' given.");
			}
			if (values == null) {
				logger.debug("No input file");
				Query q = QueryFactory.create(query);
				executeQuery(cli.getFormat(q), kb, q, getPrintWriter(outputFileName, cli.getOutputAppend()), configurations);
			} else {

				ResultSet parameters = null;
				if(values.length == 1 && new File(values[0]).exists()){
					logger.debug("Input file name given");
					parameters = ResultSetFactory.load(values[0]);
				}else {
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
							outputFile = FilenameUtils.removeExtension(outputFileName) + "-" + parameters.getRowNumber() + "." + FilenameUtils.getExtension(outputFileName);
						}
						// else stays null and output goes to STDOUT
					}
					try {
						logger.trace("Executing Query: {}", q);
						executeQuery(cli.getFormat(q), kb, q, getPrintWriter(outputFile, cli.getOutputAppend()), configurations);
					} catch (Exception e1) {
						logger.error(
								"Iteration " + parameters.getRowNumber() + " failed with error: " + e1.getMessage());
						if (logger.isDebugEnabled()) {
							logger.error("Details:", e1);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("File not found: {}", e.getMessage());
		} catch(ParseException e1){
			logger.error("{}",e1.getMessage());
			cli.printHelp();
		}
		if(logger.isTraceEnabled()) {
			logger.trace("[time] Process ends: {}", System.currentTimeMillis() - duration);
		}
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
}
