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

package com.github.sparqlanything.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
// import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.main.QC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sparqlanything.engine.FacadeX;
import com.github.sparqlanything.engine.FacadeXOpExecutor;
import com.github.sparqlanything.engine.TriplifierRegisterException;

import io.github.basilapi.basil.sparql.QueryParameter;
import io.github.basilapi.basil.sparql.Specification;
import io.github.basilapi.basil.sparql.SpecificationFactory;
import io.github.basilapi.basil.sparql.VariablesBinder;

public class SPARQLAnything {


	private static final Logger logger = LoggerFactory.getLogger(SPARQLAnything.class);

	private static void initSPARQLAnythingEngine() throws TriplifierRegisterException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
	}

	private static void executeQuery(String outputFormat, Dataset kb, Query query, PrintStream pw)
			throws FileNotFoundException {
		String format = outputFormat;
		Query q = query;
		if (q.isSelectType()) {
			if (format.equals("JSON")) {
				ResultSetFormatter.outputAsJSON(pw, QueryExecutionFactory.create(q, kb).execSelect());
			} else if (format.equals("XML")) {
				ResultSetFormatter.outputAsXML(pw, QueryExecutionFactory.create(q, kb).execSelect());
			} else if (format.equals("CSV")) {
				ResultSetFormatter.outputAsCSV(pw, QueryExecutionFactory.create(q, kb).execSelect());
			} else if (format.equals("TEXT")) {
				pw.println(ResultSetFormatter.asText(QueryExecutionFactory.create(q, kb).execSelect()));
			} else {
				throw new RuntimeException("Unsupported format: " + format);
			}
		} else if (q.isAskType()) {
			if (format.equals("JSON")) {
				ResultSetFormatter.outputAsJSON(pw, QueryExecutionFactory.create(q, kb).execAsk());
			} else if (format.equals("XML")) {
				ResultSetFormatter.outputAsXML(pw, QueryExecutionFactory.create(q, kb).execAsk());
			} else if (format.equals("CSV")) {
				ResultSetFormatter.outputAsCSV(pw, QueryExecutionFactory.create(q, kb).execAsk());
			} else if (format.equals("TEXT")) {
				ResultSetFormatter.outputAsCSV(pw, QueryExecutionFactory.create(q, kb).execAsk());
			} else {
				throw new RuntimeException("Unsupported format: " + format);
			}
//			pw.println(QueryExecutionFactory.create(q, kb).execAsk());
		} else if (q.isDescribeType() || q.isConstructType()) {
			Model m;
			Dataset d = null;
			if (q.isConstructType()) {
				d = QueryExecutionFactory.create(q, kb).execConstructDataset();
				// .execConstructDataset (instead of .execConstruct) so we can construct quads too
				// as described here: https://jena.apache.org/documentation/query/construct-quad.html
				m = d.getDefaultModel();
			} else {
				m = QueryExecutionFactory.create(q, kb).execDescribe();
				// d = new DatasetImpl(m);
			}
			if (format.equals("JSON")) {
				// JSON-LD
				RDFDataMgr.write(pw, m, Lang.JSONLD);
			} else if (format.equals("XML")) {
				// RDF/XML
				RDFDataMgr.write(pw, m, Lang.RDFXML);
			} else if (format.equals("TTL")) {
				// TURTLE
				RDFDataMgr.write(pw, m, Lang.TTL);
			} else if (format.equals("NT")) {
				// N-Triples
				RDFDataMgr.write(pw, m, Lang.NT);
			} else if (format.equals("NQ")) {
				// NQ
				RDFDataMgr.write(pw, d, Lang.NQ);
			} else if (format.equals("TRIG")) {
				// TRIG
				RDFDataMgr.write(pw, d, Lang.TRIG);
			} else {
				throw new RuntimeException("Unsupported format: " + format);
			}
		}
	}

	private static PrintStream getPrintWriter(String fileName) throws FileNotFoundException {

		if (fileName != null) {
			return new PrintStream(new File(fileName));
		}

		return System.out;
	}

	public static Query bindParameters(Specification specification, QuerySolution qs) throws Exception {
		VariablesBinder binder = new VariablesBinder(specification);

		List<String> missing = new ArrayList<String>();
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
			logger.error("Available query parameters not sufficient: {}", qs.toString());
			throw new Exception(ms.toString());
		}
		return binder.toQuery();
	}

	public static String prepareOutputFromPattern(String template, QuerySolution qs) {
		logger.trace(" - template: {}", template);
		Iterator<String> vars = qs.varNames();
		while (vars.hasNext()) {

			String var = vars.next();
//			String v = "?" + var;
//			template = template.replace(v, qs.get(var).toString());
			// ( PN_CHARS_U | [0-9] ) ( PN_CHARS_U | [0-9] | #x00B7 | [#x0300-#x036F] |
			// [#x203F-#x2040] )*
			// #x00B7 middle dot
			// chars with accents #x0300-#x036F

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
		private String[] values;
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
			this.variables = new ArrayList<String>();
			this.bindings = new HashSet<Binding>();
			this.model = ModelFactory.createDefaultModel();
			row = 0;
			// Populate
			Map<String, Set<Pair>> var_val_map = new HashMap<String, Set<Pair>>();
			for (String value : values) {
				String var = value.substring(0, value.indexOf('='));
				String val = value.substring(value.indexOf('=') + 1);
				if (!var_val_map.containsKey(var)) {
					var_val_map.put(var, new HashSet<Pair>());
				}
				logger.debug("Value: {} -> {}", var, val);
				// If integer check if value represents range
				if (val.matches("^[0-9]+\\.\\.\\.[0-9]+$")) {
					logger.trace("Range");
					String[] vv = val.split("\\.\\.\\.");
					int from = Integer.valueOf(vv[0]);
					int to = Integer.valueOf(vv[1]);
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
				sets = new HashSet<Set<Object>>();

				for (Pair p : var_val_map.entrySet().iterator().next().getValue()) {
					Set<Object> singleton = new HashSet<Object>();
					singleton.add(p);
					sets.add(singleton);
				}
			}
			for (Set<Object> s : sets) {
				final Map<Var, Node> bins = new HashMap<Var, Node>();
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
						Iterator<Var> vIter = bins.keySet().iterator();
						while (vIter.hasNext()) {
							Var v = vIter.next();
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

		// Credits: https://stackoverflow.com/a/714256/1035608
		public static Set<Set<Object>> cartesianProduct(Set<?>... sets) {
			if (sets.length < 2)
				throw new IllegalArgumentException(
						"Can't have a product of fewer than two sets (got " + sets.length + ")");

			return _cartesianProduct(0, sets);
		}

		private static Set<Set<Object>> _cartesianProduct(int index, Set<?>... sets) {
			Set<Set<Object>> ret = new HashSet<Set<Object>>();
			if (index == sets.length) {
				ret.add(new HashSet<Object>());
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

	public static void main(String[] args) throws Exception {
		logger.info("SPARQL anything");
		CLI cli = new CLI();
		try {
			cli.parse(args);
			String query = cli.getQuery();
			Integer strategy = cli.getStrategy();
			if (strategy != null) {
				if (strategy == 1 || strategy == 0 || strategy == 2) {
					ARQ.getContext().set(FacadeXOpExecutor.strategy, strategy);
				} else {
					logger.error("Invalid value for parameter 'strategy': {}", strategy);
				}
			}

			initSPARQLAnythingEngine();

			Dataset kb = null;
			String load = cli.getLoad();
			if (load != null) {

				logger.info("Loading data from: {}", load);
				File loadSource = new File(load);
				if (loadSource.isDirectory()) {

					logger.info("Loading from directory: {}", loadSource);
					// If directory, load all files
					List<String> list = new ArrayList<String>();
					Path base = Paths.get(".");
					File[] files = loadSource.listFiles();
					for (File f : files) {
						logger.info("Adding location: {}", f);
						list.add(base.relativize(f.toPath()).toString());
					}
					kb = DatasetFactory.createGeneral();
					for (String l : list) {
						try {
							Model m = ModelFactory.createDefaultModel();
							// read into the model.
							m.read(l);
							kb.addNamedModel(l, m);
						} catch (Exception e) {
							logger.error("An error occurred while loading {}", l);
						}
					}
					logger.info("Loaded {} triples", kb.asDatasetGraph().getUnionGraph().size());
				} else if (loadSource.isFile()) {
					// If it is a file, load it
					logger.info("Load location: {}", loadSource);
					Path base = Paths.get(".");
					kb = DatasetFactory.create(base.relativize(loadSource.toPath()).toString());
				} else {
					logger.error("Option 'load' failed (not a file or directory): {}", loadSource);
					return;
				}
			} else {
				kb = DatasetFactory.createGeneral();
			}
			String inputFile = cli.getInputFile();
			String outputFileName = cli.getOutputFile();
			String outputPattern = cli.getOutputPattern();
			String[] values = cli.getValues();
			if (outputPattern != null && outputFileName != null) {
				logger.warn("Option 'output' is ignored: 'output-pattern' given.");
			}
			if (inputFile == null && values == null) {
				logger.debug("No input file");
				Query q = QueryFactory.create(query);
				executeQuery(cli.getFormat(q), kb, q, getPrintWriter(outputFileName));
			} else {

				if (inputFile != null && values != null) {
					throw new ParseException("Arguments 'input' and 'values' cannot be used together.");
				}
				ResultSet parameters = null;
				if (inputFile != null) {
					logger.debug("Input file given");
					// Load the file
					parameters = ResultSetFactory.load(inputFile);
				} else {
					logger.debug("Values given");
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
							outputFile = outputFileName + "-" + parameters.getRowNumber();
						}
						// else stays null and output goes to STDOUT
					}
					try {

						executeQuery(cli.getFormat(q), kb, q, getPrintWriter(outputFile));

						logger.trace("Executing Query: {}", query);
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
		} catch (ParseException e) {
			cli.printHelp();
		}
	}
}
