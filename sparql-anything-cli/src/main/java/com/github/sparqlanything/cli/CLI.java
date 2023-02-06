/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jena.query.Query;
import org.apache.jena.riot.Lang;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

public class CLI {
	private static final Logger logger = LoggerFactory.getLogger(CLI.class);
	public static final String QUERY = "q";
	public static final String QUERY_LONG = "query";

	public static final String OUTPUT = "o";
	public static final String OUTPUT_LONG = "output";

	public static final String OUTPUT_APPEND = "a";
	public static final String OUTPUT_APPEND_LONG = "append";

	public static final String FORMAT = "f";
	public static final String FORMAT_LONG = "format";

	public static final String INPUT = "i";
	public static final String INPUT_LONG = "input";

	public static final String LOAD = "l";
	public static final String LOAD_LONG = "load";

	public static final String STRATEGY = "s";
	public static final String STRATEGY_LONG = "strategy";

	public static final String OUTPUTPATTERN = "p";
	public static final String OUTPUTPATTERN_LONG = "output-pattern";

	public static final String VALUES = "v";
	public static final String VALUES_LONG = "values";

	public static final String EXPLAIN = "e";
	public static final String EXPLAIN_LONG = "explain";

	private Options options;
	private CommandLine commandLine = null;

	public CLI(){
		init();
	}

	public void parse(String[] args) throws MissingOptionException, ParseException {
		CommandLineParser cmdLineParser = new DefaultParser();
		this.commandLine = cmdLineParser.parse(options, args);
	}

	private static String getQuery(String queryArgument) throws IOException {
		String query = queryArgument;

		// XXX Check if queryArgument is a URI first
		File queryFile;
		try{
			queryFile = new File(new URL(queryArgument).toURI());
		}catch(MalformedURLException | URISyntaxException e){
			queryFile = new File(queryArgument);
		}
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
	public String getQuery() throws IOException {
		return getQuery(commandLine.getOptionValue(CLI.QUERY));
	}
	void init(){
		this.options = new Options();

		options.addOption(Option.builder(QUERY).argName("query").hasArg().required(true)
				.desc("The path to the file storing the query to execute or the query itself.").longOpt(QUERY_LONG)
				.build());

		options.addOption(Option.builder(OUTPUT).argName("file").hasArg()
				.desc("OPTIONAL - The path to the output file. [Default: STDOUT]").longOpt(OUTPUT_LONG).build());

		options.addOption(Option.builder(OUTPUT_APPEND).hasArg(false)
				.desc("OPTIONAL - Should output to file be appended? WARNING: this option does not ensure that the whole file is valid -- that is up to the user to set up the conditions (such as using NQ serialization and not using bnodes)").longOpt(OUTPUT_APPEND_LONG).build());

		options.addOption(Option.builder(EXPLAIN).argName("explain").hasArg(false)
				.desc("OPTIONAL - Explain query execution").longOpt(EXPLAIN_LONG).build());

		options.addOption(Option.builder(LOAD).argName("load").hasArg().desc(
						"OPTIONAL - The path to one RDF file or a folder including a set of files to be loaded. When present, the data is loaded in memory and the query executed against it.")
				.longOpt(LOAD_LONG).build());

		options.addOption(Option.builder(FORMAT).argName("string").hasArg().desc(
						"OPTIONAL -  Format of the output file. Supported values: JSON, XML, CSV, TEXT, TTL, NT, NQ. [Default: TEXT or TTL]")
				.longOpt(FORMAT_LONG).build());

		options.addOption(Option.builder(STRATEGY).argName("strategy").hasArg().optionalArg(true).desc(
						"OPTIONAL - Strategy for query evaluation. Possible values: '1' - triple filtering (default), '0' - triplify all data. The system fallbacks to '0' when the strategy is not implemented yet for the given resource type.")
				.longOpt(STRATEGY_LONG).build());

		options.addOption(Option.builder(OUTPUTPATTERN).argName("outputPattern").hasArg().desc(
						"OPTIONAL - Output filename pattern, e.g. 'myfile-?friendName.json'. Variables should start with '?' and refer to bindings from the input file. This option can only be used in combination with 'input' and is ignored otherwise. This option overrides 'output'.")
				.longOpt(OUTPUTPATTERN_LONG).build());

		options.addOption(Option.builder(VALUES).argName("values").hasArg(true).optionalArg(true).desc(
						"OPTIONAL - Values passed as input parameter to a query template. When present, the query is pre-processed by substituting variable names with the values provided. The argument can be used in two ways. (1) Providing a single SPARQL ResultSet file. In this case, the query is executed for each set of bindings in the input result set. Only 1 file is allowed. (2) Named variable bindings: the argument value must follow the syntax: var_name=var_value. The argument can be passed multiple times and the query repeated for each set of values.")
				.longOpt(VALUES_LONG).build());
		options.addOption(Option.builder(INPUT).argName("input").hasArg().desc(
						"[Deprecated] OPTIONAL - The path to a SPARQL result set file to be used as input. When present, the query is pre-processed by substituting variable names with values from the bindings provided. The query is repeated for each set of bindings in the input result set.")
				.longOpt(INPUT_LONG).build());

	}

	public void printHelp(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(null); // XXX See issue #286
		String version = SPARQLAnything.class.getPackage().getImplementationVersion();
		formatter.printHelp(
				"java -jar sparql.anything-" + version + "  -q query [-f <output format>] [-v <filepath | name=value> ... ]  [-l path] [-o filepath]",
				options);
	}

	public Integer getStrategy() {
		return (commandLine.hasOption(CLI.STRATEGY) ? Integer.valueOf(commandLine.getOptionValue(CLI.STRATEGY))
				: null);
	}

	public String getLoad() {
		return commandLine.getOptionValue(CLI.LOAD);
	}

	public String getInputFile() {
		return commandLine.getOptionValue(CLI.INPUT);
	}

	public String getOutputFile() {
		return commandLine.getOptionValue(CLI.OUTPUT);
	}
	public boolean getOutputAppend() {
		return commandLine.hasOption(CLI.OUTPUT_APPEND);
	}

	public String getOutputPattern() {
		return commandLine.getOptionValue(CLI.OUTPUTPATTERN);
	}

	public String[] getValues() {
		return commandLine.getOptionValues(CLI.VALUES);
	}

	public static String guessLang(String name) {
		String suffix = FileUtils.getFilenameExt(name).toLowerCase(Locale.ROOT);
		if (suffix.equals("n3")) {
			return Lang.N3.getName();
		} else if (suffix.equals("nq")) {
			return Lang.NQ.getName();
		} else if (suffix.equals("json")) {
			return "JSON";
		} else if (suffix.equals("csv")) {
			return "CSV";
		} else if (suffix.equals("txt")) {
			return "TEXT";
		} else if (suffix.equals("xml")) {
			return "xml";
		} else if (suffix.equals("nt")) {
			return Lang.NTRIPLES.getName();
		} else if (suffix.equals("ttl")) {
			return Lang.TTL.getName();
		} else if (suffix.equals("rdf")) {
			return Lang.RDFXML.getName();
		} else {
			return suffix.equals("owl") ? Lang.RDFXML.getName() : null;
		}
	}
	public String getFormat(Query q) {
		if (commandLine.hasOption(CLI.FORMAT)) {
			return commandLine.getOptionValue(CLI.FORMAT).toUpperCase();
		}
		String format = null;

		// Set default format for query type and STDOUT or FILE
		if (commandLine.getOptionValue(CLI.OUTPUT) != null) {
			// Guess the format from the extension
			format = guessLang(commandLine.getOptionValue(CLI.OUTPUT));
		}

		if(format == null){
			if (q.isAskType() || q.isSelectType()) {
				return Lang.CSV.getName();
			} else if (q.isConstructType() || q.isDescribeType()) {
				return Lang.TTL.getName();
			} else if (q.isDescribeType() || q.isConstructType()) {
				return Lang.TTL.getName();
			}
		}
		return format;
	}

	public boolean explain() {
		return commandLine.hasOption(CLI.EXPLAIN);
	}
}
