package com.github.spiceh2020.sparql.anything.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import io.github.basilapi.basil.sparql.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.main.QC;

import com.github.spiceh2020.sparql.anything.engine.FacadeX;
import com.github.spiceh2020.sparql.anything.engine.TriplifierRegisterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPARQLAnything {

	private static final String QUERY = "q";
	private static final String QUERY_LONG = "query";

	private static final String OUTPUT = "o";
	private static final String OUTPUT_LONG = "output";

	private static final String FORMAT = "f";
	private static final String FORMAT_LONG = "format";

	private static final String INPUT = "i";
	private static final String INPUT_LONG = "input";

	private static final String OUTPUTPATTERN = "p";
	private static final String OUTPUTPATTERN_LONG = "output-pattern";
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

	private static void executeQuery(CommandLine commandLine, String query, PrintStream pw) throws FileNotFoundException {
		logger.trace("Executing Query: " + query);
		Dataset kb = DatasetFactory.createGeneral();
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
		} else if (q.isConstructType()) {
			if(format.equals("JSON")) {
				// JSON-LD
				Model m = QueryExecutionFactory.create(q, kb).execConstruct();
				RDFDataMgr.write(pw, m, Lang.JSONLD) ;
			}else if(format.equals("XML")){
				// RDF/XML
				QueryExecutionFactory.create(q, kb).execConstruct().write(pw, Lang.RDFXML.toString());
			}else if(format.equals("TTL")){
				// TURTLE
				QueryExecutionFactory.create(q, kb).execConstruct().write(pw, Lang.TTL.toString());
			}else if(format.equals("NT")){
				// N-Triples
				QueryExecutionFactory.create(q, kb).execConstruct().write(pw, Lang.NT.toString());
			}else if(format.equals("NQ")){
				// N-Triples
				QueryExecutionFactory.create(q, kb).execConstruct().write(pw, Lang.NQ.toString());
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
		} else if (q.isDescribeType()) {

			if(format.equals("JSON")) {
				// JSON-LD
				Model m = QueryExecutionFactory.create(q, kb).execConstruct();
				RDFDataMgr.write(pw, m, Lang.JSONLD) ;
			}else if(format.equals("XML")){
				// RDF/XML
				QueryExecutionFactory.create(q, kb).execDescribe().write(pw, Lang.RDFXML.toString());
			}else if(format.equals("TTL")){
				// TURTLE
				QueryExecutionFactory.create(q, kb).execDescribe().write(pw, Lang.TTL.toString());
			}else if(format.equals("NT")){
				// N-Triples
				QueryExecutionFactory.create(q, kb).execDescribe().write(pw, Lang.NT.toString());
			}else if(format.equals("NQ")){
				// N-Triples
				QueryExecutionFactory.create(q, kb).execDescribe().write(pw, Lang.NQ.toString());
			} else {
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
		// STDOUT
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

	public static void main(String[] args) throws Exception {
		logger.info("SPARQL anything");
		Options options = new Options();

		options.addOption(Option.builder(QUERY).argName("query").hasArg().required(true)
				.desc("The path to the file storing the query to execute or the query itself.").longOpt(QUERY_LONG)
				.build());

		options.addOption(Option.builder(OUTPUT).argName("file").hasArg()
				.desc("OPTIONAL - The path to the output file. [Default: STDOUT]").longOpt(OUTPUT_LONG).build());

		options.addOption(Option.builder(INPUT).argName("input").hasArg()
				.desc("OPTIONAL - The path to the input file (a SPARQL result set). When present, the query is pre-processed by substituting variable names with values from the bindings provided. The query is repeated for each set of bindings in the input result set.").longOpt(INPUT_LONG).build());

		options.addOption(Option.builder(FORMAT).argName("string").hasArg()
				.desc("OPTIONAL -  Format of the output file. Supported values: JSON, XML, CSV, TEXT, TTL, NT, NQ. [Default: TEXT or TTL]")
				.longOpt(FORMAT_LONG).build());

		options.addOption(Option.builder(OUTPUTPATTERN).argName("outputPattern").hasArg()
				.desc("OPTIONAL - Output filename pattern, e.g. 'myfile-?friendName.json'. Variables should start with '?' and refer to bindings from the input file. This option can only be used in combination with 'input' and is ignored otherwise. This option overrides 'output'.").longOpt(OUTPUTPATTERN_LONG).build());

		CommandLine commandLine = null;

		CommandLineParser cmdLineParser = new DefaultParser();
		try {
			commandLine = cmdLineParser.parse(options, args);

			String query = getQuery(commandLine.getOptionValue(QUERY));

			initSPARQLAnythingEngine();

			String inputFile = commandLine.getOptionValue(INPUT);
			String outputFileName = commandLine.getOptionValue(OUTPUT);
			String outputPattern = commandLine.getOptionValue(OUTPUTPATTERN);
			if(outputPattern != null && outputFileName != null){
				logger.warn("Option 'output' is ignored: 'output-pattern' given.");
			}
			if(inputFile == null) {
				logger.debug("No input file");
				executeQuery(commandLine, query, getPrintWriter(commandLine, outputFileName));
			} else {
				logger.debug("Input file given");
				// Load the file
				ResultSet parameters = ResultSetFactory.load(inputFile);
				// Specifications
				Specification specification = SpecificationFactory.create("", query);
				// Iterate over parameters
				List<String> variables = parameters.getResultVars();
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
						executeQuery(commandLine, q.toString(), getPrintWriter(commandLine, outputFile));
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
			formatter.printHelp("java -jar sparql.anything-<version> -q query [-f format] [-i input] [-o filepath]", options);
		}
	}
}
