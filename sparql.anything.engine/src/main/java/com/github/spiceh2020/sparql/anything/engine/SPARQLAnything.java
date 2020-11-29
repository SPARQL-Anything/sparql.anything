package com.github.spiceh2020.sparql.anything.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.github.spiceh2020.sparql.anything.json.JSONTriplifier;

public class SPARQLAnything {

	private static final String QUERY = "q";
	private static final String QUERY_LONG = "query";

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
			}
			query = sb.toString();
			br.close();
		}
		return query;
	}

	private static void initSPARQLAnythingEngine() throws TriplifierRegisterException {
		OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
			@Override
			public OpExecutor create(ExecutionContext execCxt) {
				return new TupleOpExecutor(execCxt);
			}
		};

		QC.setFactory(ARQ.getContext(), customExecutorFactory);
		TriplifierRegister.getInstance().registerTriplifier(new JSONTriplifier());
	}

	private static void executeQuery(String query) {
		logger.trace("Query: " + query);
		Dataset kb = DatasetFactory.createGeneral();
		Query q = QueryFactory.create(query);
		if (q.isSelectType()) {
			System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(q, kb).execSelect()));
		} else if (q.isConstructType()) {
			// TODO
		} else if (q.isAskType()) {
			// TODO
		} else if (q.isDescribeType()) {
			// TODO
		}
	}

	public static void main(String[] args) throws Exception {
		logger.info("SPARQL anything");
		Options options = new Options();
		Option outputFileOption = Option.builder(QUERY).argName("query").hasArg().required(true)
				.desc("The path to the file storing the query to execute or the query itself.").longOpt(QUERY_LONG)
				.build();

		options.addOption(outputFileOption);

		CommandLine commandLine = null;

		CommandLineParser cmdLineParser = new DefaultParser();
		try {
			commandLine = cmdLineParser.parse(options, args);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("process", options);
		}

		String query = getQuery(commandLine.getOptionValue(QUERY));

		initSPARQLAnythingEngine();

		executeQuery(query);

	}
}
