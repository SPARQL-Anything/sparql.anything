/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.fuseki;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.Triplifier;
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

public class Endpoint {

	public static final String DEFAULT_PATH = "/sparql.anything";
	public static final String DEFAULT_SPARQL_ENDPOINT_GUI_PATH = "/sparql";
	public static final int DEFAULT_PORT = 3000;

	private static Logger logger = LoggerFactory.getLogger(Endpoint.class);
	private Builder builder = FusekiServer.create();
	private String path, guipath;
	private FusekiServer server;
	private int port;
	private static Endpoint instance;
	private static final String PORT = "p", PATH = "e", GUI = "g";

	private Endpoint() {
		builder.port(DEFAULT_PORT);
		path = DEFAULT_PATH;
		port = DEFAULT_PORT;
		guipath = DEFAULT_SPARQL_ENDPOINT_GUI_PATH;
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

	public void setGUIPath(String guipath) {
		if (path.charAt(0) != '/') {
			this.guipath = "/" + guipath;
		} else {
			this.guipath = guipath;
		}
	}

	public void start() {
		Dataset ds = DatasetFactory.create();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		logger.info("Starting sparql.anything endpoint..");
		logger.info("The server will be listening on http://localhost:{}{}", port, path);
		logger.info("The server will be available on http://localhost:{}{}", port, guipath);
//		builder.staticFileBase("src/main/resources/static/");
		builder.addServlet(this.guipath, new YASGUIServlet(Triplifier.XYZ_NS, path));
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

		options.addOption(Option.builder(GUI).argName("gui").hasArg().required(false)
				.desc("The path of the SPARQL endpoint GUI (Default " + DEFAULT_SPARQL_ENDPOINT_GUI_PATH + ").")
				.longOpt("gui").build());

		CommandLine commandLine = null;

		CommandLineParser cmdLineParser = new DefaultParser();
		try {
			commandLine = cmdLineParser.parse(options, args);

			String path = commandLine.getOptionValue(PATH, DEFAULT_PATH);
			String guipath = commandLine.getOptionValue(GUI, DEFAULT_SPARQL_ENDPOINT_GUI_PATH);
			int port = Integer.parseInt(commandLine.getOptionValue(PORT, DEFAULT_PORT + ""));

			Endpoint e = Endpoint.getInstance();
			e.setPath(path);
			e.setPort(port);
			e.setGUIPath(guipath);

			e.start();

		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar sparql-anything-fuseki-<version>.jar [-p port] [-e sparql-endpoint-path] [-g endpoint-gui-path]", options);
		}
	}

}
