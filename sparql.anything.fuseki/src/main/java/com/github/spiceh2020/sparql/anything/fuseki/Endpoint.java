package com.github.spiceh2020.sparql.anything.fuseki;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.main.FusekiServer.Builder;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.engine.FacadeX;

public class Endpoint {

	private static Logger logger = LoggerFactory.getLogger(Endpoint.class);
	private Builder builder = FusekiServer.create();
	public static final String DEFAULT_PATH = "/sparql.anything";
	private String path;
	private FusekiServer server;
	private int port;
	private static Endpoint instance;
	public static final int DEFAULT_PORT = 3000;
	private static final String PORT = "p", PATH = "e";

	private Endpoint() {
		builder.port(DEFAULT_PORT);
		path = DEFAULT_PATH;
		port = DEFAULT_PORT;
	}

	public static Endpoint getInstance() {
		if (instance == null) {
			instance = new Endpoint();
		}
		return instance;
	}

	public void setPort(int port) {
		this.port = port;
		builder.port(port);
	}

	public void setPath(String path) {
		if (path.charAt(0) != '/') {
			this.path = "/" + path;
		} else {
			this.path = path;
		}
	}

	public void start() {
		Dataset ds = DatasetFactory.create();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		logger.info("Starting sparql.anything endpoint..");
		logger.info("The server will be listening on http://localhost:{}{}", port, path);
		server = builder.add(path, ds).build();
		server.start();
	}

	public void stop() {
		server.stop();
		logger.info("SPARQL Endpoint ended!");
	}

	public static void main(String[] args) {

		logger.info("sparql.anything endpoint");
		Options options = new Options();

		options.addOption(Option.builder(PORT).argName("port").hasArg().required(false)
				.desc("The port where the server will be running on (Default " + DEFAULT_PORT + " ).").longOpt("port")
				.build());

		options.addOption(Option.builder(PATH).argName("path").hasArg().required(false)
				.desc("The path where the server will be running on (Default " + DEFAULT_PATH + ").").longOpt("path")
				.build());

		CommandLine commandLine = null;

		CommandLineParser cmdLineParser = new DefaultParser();
		try {
			commandLine = cmdLineParser.parse(options, args);

			String path = commandLine.getOptionValue(PATH, DEFAULT_PATH);
			int port = Integer.parseInt(commandLine.getOptionValue(PORT, DEFAULT_PORT + ""));

			Endpoint e = Endpoint.getInstance();
			e.setPath(path);
			e.setPort(port);

			e.start();

		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("process", options);
		}
	}

}
