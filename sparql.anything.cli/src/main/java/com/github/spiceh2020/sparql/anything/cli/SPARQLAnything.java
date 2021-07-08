package com.github.spiceh2020.sparql.anything.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingBase;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.xpath.Arg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.engine.FacadeX;
import com.github.spiceh2020.sparql.anything.engine.FacadeXOpExecutor;
import com.github.spiceh2020.sparql.anything.engine.TriplifierRegisterException;

import io.github.basilapi.basil.sparql.QueryParameter;
import io.github.basilapi.basil.sparql.Specification;
import io.github.basilapi.basil.sparql.SpecificationFactory;
import io.github.basilapi.basil.sparql.VariablesBinder;

public class SPARQLAnything {

	private static final String QUERY = "q";
	private static final String QUERY_LONG = "query";

	private static final String OUTPUT = "o";
	private static final String OUTPUT_LONG = "output";

	private static final String FORMAT = "f";
	private static final String FORMAT_LONG = "format";

	private static final String INPUT = "i";
	private static final String INPUT_LONG = "input";

	private static final String LOAD = "l";
	private static final String LOAD_LONG = "load";

	private static final String STRATEGY = "s";
	private static final String STRATEGY_LONG = "strategy";

	private static final String OUTPUTPATTERN = "p";
	private static final String OUTPUTPATTERN_LONG = "output-pattern";

	private static final String VALUES = "v";
	private static final String VALUES_LONG = "values";

	private static final Logger logger = LoggerFactory.getLogger(SPARQLAnything.class);

	private static String getQuery(String queryArgument) throws IOException {
		String query = queryArgument;
		File queryFile = new File(queryArgument);
		if (queryFile.exists()) {
			logger.trace("Loading query from file");
			// LOAD query from file
			BufferedReader br = new BufferedReader(new FileReader(queryFile));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			query = sb.toString();
			br.close();
		}
		return query;
	}

	private static void initSPARQLAnythingEngine() throws TriplifierRegisterException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
	}

	private static void executeQuery(CommandLine commandLine, Dataset kb, String query, PrintStream pw) throws FileNotFoundException {
		logger.trace("Executing Query: {}", query);
		Query q = QueryFactory.create(query);
		String format = getFormat(q, commandLine);
		if (q.isSelectType()) {
			if(format.equals("JSON")) {
				ResultSetFormatter.outputAsJSON(pw, QueryExecutionFactory.create(q, kb).execSelect());
			}else if(format.equals("XML")){
				ResultSetFormatter.outputAsXML(pw, QueryExecutionFactory.create(q, kb).execSelect());
			}else if(format.equals("CSV")){
				ResultSetFormatter.outputAsCSV(pw, QueryExecutionFactory.create(q, kb).execSelect());
			}else if(format.equals("TEXT")){
				pw.println(ResultSetFormatter.asText(QueryExecutionFactory.create(q, kb).execSelect()));
			}else {
				throw new RuntimeException("Unsupported format: " + format);
			}
		} else if (q.isAskType()) {
			if(format.equals("JSON")) {
				ResultSetFormatter.outputAsJSON(pw, QueryExecutionFactory.create(q, kb).execAsk());
			}else if(format.equals("XML")){
				ResultSetFormatter.outputAsXML(pw, QueryExecutionFactory.create(q, kb).execAsk());
			}else if(format.equals("CSV")){
				ResultSetFormatter.outputAsCSV(pw, QueryExecutionFactory.create(q, kb).execAsk());
			}else if(format.equals("TEXT")){
				ResultSetFormatter.outputAsCSV(pw, QueryExecutionFactory.create(q, kb).execAsk());
			}else {
				throw new RuntimeException("Unsupported format: " + format);
			}
//			pw.println(QueryExecutionFactory.create(q, kb).execAsk());
		} else if (q.isDescribeType() || q.isConstructType()) {
			Model m;
			if (q.isConstructType()) {
				m = QueryExecutionFactory.create(q, kb).execConstruct();
			}else {
				m = QueryExecutionFactory.create(q, kb).execDescribe();
			}
			if(format.equals("JSON")) {
				// JSON-LD
				RDFDataMgr.write(pw, m, Lang.JSONLD) ;
			}else if(format.equals("XML")){
				// RDF/XML
				RDFDataMgr.write(pw, m, Lang.RDFXML) ;
			}else if(format.equals("TTL")){
				// TURTLE
				RDFDataMgr.write(pw, m, Lang.TTL) ;
			}else if(format.equals("NT")){
				// N-Triples
				RDFDataMgr.write(pw, m, Lang.NT) ;
			}else if(format.equals("NQ")){
				// NQ
				RDFDataMgr.write(pw, m, Lang.NQ) ;
			}else {
				throw new RuntimeException("Unsupported format: " + format);
			}
		}
	}

	private static PrintStream getPrintWriter(CommandLine commandLine, String fileName) throws FileNotFoundException {

		if (fileName != null) {
			return new PrintStream(new File(fileName));
		}

		return System.out;
	}

	private static String getFormat(Query q, CommandLine commandLine) throws FileNotFoundException {
		if (commandLine.hasOption(FORMAT)) {
			return commandLine.getOptionValue(FORMAT).toUpperCase();
		}

		// Set default format for query type and STDOUT or FILE
		if(commandLine.getOptionValue(OUTPUT) != null ){
			if(q.isAskType()|| q.isSelectType()) {
				return "JSON";
			}else if(q.isConstructType()|| q.isDescribeType()) {
				return "TTL";
			}
		}
		//
		if(q.isDescribeType() || q.isConstructType()){
			return "TTL";
		}
		return "TEXT";
	}

	public static Query bindParameters(Specification specification, QuerySolution qs){
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
				logger.warn("Missing parameter: {}", qp);
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
			throw new RuntimeException(ms.toString());
		}
		return binder.toQuery();
	}

	public static String prepareOutputFromPattern(String template, QuerySolution qs){
		Iterator<String> vars = qs.varNames();
		while(vars.hasNext()){
			String var = vars.next();
			String v = "?" + var;
			template = template.replace(v, qs.get(var).toString());
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
		ArgValuesAsResultSet(String[] values){
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
			Map<String,Set<Pair>> var_val_map = new HashMap<String,Set<Pair>>();
			for(String value : values){
				String var = value.substring(0, value.indexOf('='));
				String val = value.substring(value.indexOf('=') + 1);
				if(!var_val_map.containsKey(var)){
					var_val_map.put(var, new HashSet<Pair>());
				}
				logger.debug("Value: {} -> {}", var, val);
				// If integer check if value represents range
				if(val.matches("^[0-9]+\\.\\.\\.[0-9]+$")){
					logger.trace("Range");
					String[] vv = val.split("\\.\\.\\.");
					int from = Integer.valueOf(vv[0]);
					int to = Integer.valueOf(vv[1]);
					logger.trace("Value: {} -> range({},{})", var, from, to);
					for(int x = from; x <=to; x++){
						var_val_map.get(var).add(Pair.of(var, Integer.toString(x)));
					}
				} else {
					var_val_map.get(var).add(Pair.of(var, val));
				}
			}
			// Generate bindings
			Set<Set<Object>> sets;
			if(var_val_map.values().size() > 1){
				sets = cartesianProduct(var_val_map.values().toArray(new HashSet[var_val_map.values().size()]));
			}else{
				sets =  new HashSet<Set<Object>>();

				for(Pair p: var_val_map.entrySet().iterator().next().getValue()) {
					Set<Object> singleton = new HashSet<Object>();
					singleton.add(p);
					sets.add(singleton);
				}
			}
			for(Set<Object> s : sets){
				final Map<Var,Node> bins = new HashMap<Var,Node>();
				for (Object j: s){
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
			return new ResultBinding(this.model,nextBinding());
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
						"Can't have a product of fewer than two sets (got " +
								sets.length + ")");

			return _cartesianProduct(0, sets);
		}

		private static Set<Set<Object>> _cartesianProduct(int index, Set<?>... sets) {
			Set<Set<Object>> ret = new HashSet<Set<Object>>();
			if (index == sets.length) {
				ret.add(new HashSet<Object>());
			} else {
				for (Object obj : sets[index]) {
					for (Set<Object> set : _cartesianProduct(index+1, sets)) {
						set.add(obj);
						ret.add(set);
					}
				}
			}
			return ret;
		}
	}

	public static ResultSet prepareResultSetFromArgValues(String [] values){
		return new ArgValuesAsResultSet(values);
	}

	public static void main(String[] args) throws Exception {
		logger.info("SPARQL anything");
		Options options = new Options();

		options.addOption(Option.builder(QUERY).argName("query").hasArg().required(true)
				.desc("The path to the file storing the query to execute or the query itself.").longOpt(QUERY_LONG)
				.build());

		options.addOption(Option.builder(OUTPUT).argName("file").hasArg()
				.desc("OPTIONAL - The path to the output file. [Default: STDOUT]").longOpt(OUTPUT_LONG).build());

		options.addOption(Option.builder(INPUT).argName("input").hasArg()
				.desc("OPTIONAL - The path to a SPARQL result set file to be used as input. When present, the query is pre-processed by substituting variable names with values from the bindings provided. The query is repeated for each set of bindings in the input result set.").longOpt(INPUT_LONG).build());

		options.addOption(Option.builder(LOAD).argName("load").hasArg()
				.desc("OPTIONAL - The path to one RDF file or a folder including a set of files to be loaded. When present, the data is loaded in memory and the query executed against it.").longOpt(LOAD_LONG).build());

		options.addOption(Option.builder(FORMAT).argName("string").hasArg()
				.desc("OPTIONAL -  Format of the output file. Supported values: JSON, XML, CSV, TEXT, TTL, NT, NQ. [Default: TEXT or TTL]")
				.longOpt(FORMAT_LONG).build());

		options.addOption(Option.builder(STRATEGY).argName("strategy").hasArg().optionalArg(true)
				.desc("OPTIONAL - Strategy for query evaluation. Possible values: '1' - triple filtering (default), '0' - triplify all data. The system fallbacks to '0' when the strategy is not implemented yet for the given resource type.")
				.longOpt(STRATEGY_LONG).build());

		options.addOption(Option.builder(OUTPUTPATTERN).argName("outputPattern").hasArg()
				.desc("OPTIONAL - Output filename pattern, e.g. 'myfile-?friendName.json'. Variables should start with '?' and refer to bindings from the input file. This option can only be used in combination with 'input' and is ignored otherwise. This option overrides 'output'.")
				.longOpt(OUTPUTPATTERN_LONG).build());

		options.addOption(Option.builder(VALUES).argName("values").hasArg(true).optionalArg(true)
				.desc("OPTIONAL - Values passed as input to a query template. When present, the query is pre-processed by substituting variable names with the values provided. The passed argument must follow the syntax: var_name=var_value. Multiple arguments are allowed. The query is repeated for each set of values.")
				.longOpt(VALUES_LONG).build());
		CommandLine commandLine = null;

		CommandLineParser cmdLineParser = new DefaultParser();
		try {
			commandLine = cmdLineParser.parse(options, args);
			String query = getQuery(commandLine.getOptionValue(QUERY));
			Integer strategy = ( commandLine.hasOption(STRATEGY) ? Integer.valueOf(commandLine.getOptionValue(STRATEGY)) : null);
			if(strategy != null){
				if(strategy == 1 || strategy == 0) {
					ARQ.getContext().set(FacadeXOpExecutor.strategy, strategy);
				}else{
					logger.error("Invalid value for parameter 'strategy': {}", strategy);
				}
			}

			initSPARQLAnythingEngine();

			Dataset kb = null;
			String load = commandLine.getOptionValue(LOAD);
			if(load != null){

				logger.info("Loading data from: {}", load);
				File loadSource = new File(load);
				if(loadSource.isDirectory()){

					logger.info("Loading from directory: {}", loadSource);
					// If directory, load all files
					List<String> list = new ArrayList<String>();
					Path base = Paths.get(".");
					File[] files = loadSource.listFiles();
					for (File f : files){
						logger.info("Adding location: {}", f);
						list.add(base.relativize(f.toPath()).toString());
					}
					kb = DatasetFactory.createGeneral();
					for (String l : list){
						try {
							Model m = ModelFactory.createDefaultModel();
							// read into the model.
							m.read(l);
							kb.addNamedModel(l,m);
						}catch(Exception e){
							logger.error("An error occurred while loading {}", l);
						}
					}
					logger.info("Loaded {} triples", kb.asDatasetGraph().size());
				}else if(loadSource.isFile()){
					// If it is a file, load it
					logger.info("Load location: {}", loadSource);
					Path base = Paths.get(".");
					kb = DatasetFactory.create(base.relativize(loadSource.toPath()).toString());
				}else{
					logger.error("Option 'load' failed (not a file or directory): {}", loadSource);
					return;
				}
			}else{
				kb = DatasetFactory.createGeneral();
			}
			String inputFile = commandLine.getOptionValue(INPUT);
			String outputFileName = commandLine.getOptionValue(OUTPUT);
			String outputPattern = commandLine.getOptionValue(OUTPUTPATTERN);
			String[] values = commandLine.getOptionValues(VALUES);
			if(outputPattern != null && outputFileName != null){
				logger.warn("Option 'output' is ignored: 'output-pattern' given.");
			}
			if(inputFile == null && values == null) {
				logger.debug("No input file");
				executeQuery(commandLine, kb, query, getPrintWriter(commandLine, outputFileName));
			} else {

				if(inputFile != null && values != null){
					throw new ParseException("Arguments 'input' and 'values' cannot be used together.");
				}
				ResultSet parameters = null;
				if(inputFile!=null) {
					logger.debug("Input file given");
					// Load the file
					parameters = ResultSetFactory.load(inputFile);
				}else{
					logger.debug("Values given");
					parameters = new ArgValuesAsResultSet(values);
				}
				// Specifications
				Specification specification = SpecificationFactory.create("", query);
				// Iterate over parameters
//				List<String> variables = parameters.getResultVars();
				while(parameters.hasNext()){
					QuerySolution qs = parameters.nextSolution();
					Query q = bindParameters(specification, qs);
					String outputFile = null;
					if(outputPattern != null){
						outputFile = prepareOutputFromPattern(outputPattern, qs);
					} else {
						if( outputFileName != null ){
							outputFile = outputFileName + "-" + parameters.getRowNumber();
						}
						// else stays null and output goes to STDOUT
					}
					try {
						executeQuery(commandLine, kb, q.toString(), getPrintWriter(commandLine, outputFile));
					}catch(Exception e1){
						logger.error("Iteration " + parameters.getRowNumber() + " failed with error: " + e1.getMessage());
						if(logger.isDebugEnabled()){
							logger.error("Details:", e1);
						}
					}
				}
			}
		}catch(FileNotFoundException e){
			logger.error("File not found: {}", e.getMessage());
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar sparql.anything-<version> -q query [-f format] [-i filepath]  [-l path] [-o filepath]", options);
		}
	}
}
