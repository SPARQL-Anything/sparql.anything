package com.github.spiceh2020.sparql.anything.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.riot.Lang;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.github.spiceh2020.sparql.anything.engine.FacadeX;
import com.github.spiceh2020.sparql.anything.engine.TriplifierRegisterException;

public class SPARQLAnything {

	private static final String QUERY = "q";
	private static final String QUERY_LONG = "query";

	private static final String OUTPUT = "o";
	private static final String OUTPUT_LONG = "output";

	private static final String FORMAT = "f";
	private static final String FORMAT_LONG = "format";

	private static final Logger logger = LogManager.getLogger(SPARQLAnything.class);

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

	private static void executeQuery(String query, PrintStream pw, String format) {
		logger.trace("Executing Query: " + query);
		Dataset kb = DatasetFactory.createGeneral();
		Query q = QueryFactory.create(query);
		if (q.isSelectType()) {
			pw.println(ResultSetFormatter.asText(QueryExecutionFactory.create(q, kb).execSelect()));
		} else if (q.isConstructType()) {
			QueryExecutionFactory.create(q, kb).execConstruct().write(pw, format);
		} else if (q.isAskType()) {
			pw.println(QueryExecutionFactory.create(q, kb).execAsk());
		} else if (q.isDescribeType()) {
			QueryExecutionFactory.create(q, kb).execDescribe().write(pw, format);
		}
	}

	private static PrintStream getPrintWriter(CommandLine commandLine) throws FileNotFoundException {

		if (commandLine.hasOption(FORMAT) && commandLine.hasOption(OUTPUT)) {
			return new PrintStream(new File(commandLine.getOptionValue(OUTPUT)));
		}

		return System.out;
	}

	private static String getFormat(CommandLine commandLine) throws FileNotFoundException {

		if (commandLine.hasOption(FORMAT)) {
			return commandLine.getOptionValue(FORMAT);
		}

		return Lang.TTL.toString();
	}

	public static void main(String[] args) throws Exception {
		logger.info("SPARQL anything");
		Options options = new Options();

		options.addOption(Option.builder(QUERY).argName("query").hasArg().required(true)
				.desc("The path to the file storing the query to execute or the query itself.").longOpt(QUERY_LONG)
				.build());

		options.addOption(Option.builder(OUTPUT).argName("file").hasArg()
				.desc("OPTIONAL - The path to the output file. [Default: STDOUT]").longOpt(OUTPUT_LONG).build());

		options.addOption(Option.builder(FORMAT).argName("string").hasArg()
				.desc("OPTIONAL -  Format of the output file;  TTL, NT for construct queries [Default: TTL]")
				.longOpt(FORMAT_LONG).build());

		CommandLine commandLine = null;

		CommandLineParser cmdLineParser = new DefaultParser();
		try {
			commandLine = cmdLineParser.parse(options, args);

			String query = getQuery(commandLine.getOptionValue(QUERY));

			initSPARQLAnythingEngine();

			executeQuery(query, getPrintWriter(commandLine), getFormat(commandLine));

		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("process", options);
		}

	}
}
