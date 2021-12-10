/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.sparqlanything.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;

public class AbstractTriplifierTester {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractTriplifierTester.class);

	protected Triplifier triplifier;
	protected Properties properties;
	protected URL url;
	private String extension = null;

	protected Graph result;
	protected Graph expected;

	protected DatasetGraph expectedDatasetGraph;
	protected DatasetGraph resultDatasetGraph;

	private boolean printWholeGraph = false;
	private boolean useDatasetGraph = false;

	private String expectedExtension;

	public AbstractTriplifierTester(Triplifier t, Properties p, String extension, String expectedExtension) {
		this.triplifier = t;
		this.properties = p;
		this.extension = extension;
		this.expectedExtension = expectedExtension;
		if (expectedExtension.equals("nq")) {
			useDatasetGraph = true;
		}
	}

	public AbstractTriplifierTester(Triplifier t, Properties p, String extension) {
		this(t, p, extension, "ttl");
	}

	public void setPrintWholeGraph(boolean printWholeGraph) {
		this.printWholeGraph = printWholeGraph;
	}

	@Rule
	public TestName name = new TestName();

	private void prepare() throws URISyntaxException {
		logger.debug("{} (prepare)", name.getMethodName());
		// Root is Document
		String fileName = name.getMethodName().substring(4) + "." + extension;
		logger.debug("Input filename: {}", fileName);
		url = getClass().getClassLoader().getResource(fileName);
		properties.setProperty("location", url.toURI().toString());
		properties.setProperty("blank-nodes", "false");
		logger.debug("Input location: {}", url.toURI().toString());
		properties.setProperty("root", "http://www.example.org/document");
		//
		// RDF file name
		String rdfFileName = name.getMethodName().substring(4) + "." + expectedExtension;
		System.out.println(rdfFileName);
		if (!useDatasetGraph) {
			expected = RDFDataMgr.loadModel(getClass().getClassLoader().getResource(rdfFileName).toURI().toString())
					.getGraph();
		} else {
			expectedDatasetGraph = RDFDataMgr
					.loadDatasetGraph(getClass().getClassLoader().getResource(rdfFileName).toURI().toString());
		}

	}

	protected void properties(Properties properties) {
	}

	@Before
	public void run() throws URISyntaxException, TriplifierHTTPException, IOException {
		logger.info("{} (run)", name.getMethodName());
		// Template method
		prepare();
		//
		properties(properties);
		//
		perform();
		//
		inspect();
	}

	protected void inspect() {
		logger.debug("{} (inspect)", name.getMethodName());
		if (!useDatasetGraph) {
			if (logger.isDebugEnabled()) {
				ExtendedIterator<Triple> it = expected.find();
				while (it.hasNext()) {
					Triple t = it.next();
					logger.trace("E>> {}", t);

					if (!result.contains(t)) {
						logger.debug("{} not found in result", t);
						logger.debug("(T) {} {} {} {}", t.getSubject().getClass().getSimpleName(),
								t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(),
								(t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null)
										? t.getObject().getLiteralDatatypeURI()
										: "");
					}
				}
				it = result.find();
				while (it.hasNext()) {
					Triple t = it.next();
					logger.trace("<<R {}", t);
					if (!expected.contains(t)) {
						logger.debug("{} not found in expected", t);
						logger.debug("(T) {} {} {} {}", t.getSubject().getClass().getSimpleName(),
								t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(),
								(t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null)
										? t.getObject().getLiteralDatatypeURI()
										: "");
					}
				}
			}

			if (printWholeGraph) {
				ByteArrayOutputStream baosExpected = new ByteArrayOutputStream();
				RDFDataMgr.write(baosExpected, this.expected, Lang.TTL);
				ByteArrayOutputStream baosResult = new ByteArrayOutputStream();
				RDFDataMgr.write(baosResult, this.result, Lang.TTL);
				logger.warn("Whole files\n\nExpected\n\n{}\n\n--------\n\nResult\n\n{}", baosExpected.toString(),
						baosResult.toString());

			}
		} else {
			if (logger.isDebugEnabled()) {
				Iterator<Quad> it = this.expectedDatasetGraph.find();
				while (it.hasNext()) {
					Quad q = it.next();
					logger.trace("E>> {}", q);

					if (!resultDatasetGraph.contains(q)) {
						logger.debug("{} not found in result", q);
						logger.debug("(T) {} {} {} {} {}", q.getSubject().getClass().getSimpleName(),
								q.getPredicate().getClass().getSimpleName(), q.getObject().getClass().getSimpleName(),
								(q.getObject().isLiteral() && q.getObject().getLiteralDatatypeURI() != null)
										? q.getObject().getLiteralDatatypeURI()
										: "",
								q.getGraph().getClass().getSimpleName());
					}
				}
				it = this.resultDatasetGraph.find();
				while (it.hasNext()) {
					Quad t = it.next();
					logger.trace("<<R {}", t);
					if (!expectedDatasetGraph.contains(t)) {
						logger.debug("{} not found in expected", t);
						logger.debug("(T) {} {} {} {} {}", t.getSubject().getClass().getSimpleName(),
								t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(),
								(t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null)
										? t.getObject().getLiteralDatatypeURI()
										: "",
								t.getGraph().getClass().getSimpleName());
					}
				}
			}

			if (printWholeGraph) {
				ByteArrayOutputStream baosExpected = new ByteArrayOutputStream();
				RDFDataMgr.write(baosExpected, this.expectedDatasetGraph, Lang.NQ);
				ByteArrayOutputStream baosResult = new ByteArrayOutputStream();
				RDFDataMgr.write(baosResult, this.resultDatasetGraph, Lang.NQ);
				logger.warn("Whole files\n\nExpected\n\n{}\n\n--------\n\nResult\n\n{}", baosExpected.toString(),
						baosResult.toString());

			}
		}
	}

	protected void perform() throws TriplifierHTTPException, IOException, URISyntaxException {
		logger.debug("{} (perform)", name.getMethodName());
		logger.info("{}", properties);
		String graphName = Triplifier.getRootArgument(properties, url);
		logger.debug("Graph name: {}", graphName);

		if (!useDatasetGraph) {
			this.result = triplifier.triplify(properties, new BaseFacadeXBuilder(graphName, properties))
					.getGraph(NodeFactory.createURI(graphName));
		} else {
			this.resultDatasetGraph = triplifier.triplify(properties, new BaseFacadeXBuilder(graphName, properties));
		}
	}

	protected void assertResultIsIsomorphicWithExpected() {
		if (!useDatasetGraph) {
			assertTrue(this.result.isIsomorphicWith(expected));
		} else {
			Iterator<Node> it = this.expectedDatasetGraph.listGraphNodes();
			assertEquals(this.expectedDatasetGraph.size(), this.resultDatasetGraph.size());
			while (it.hasNext()) {
				Node g = (Node) it.next();
				assertTrue(resultDatasetGraph.containsGraph(g));
				assertTrue(expectedDatasetGraph.getGraph(g).isIsomorphicWith(this.resultDatasetGraph.getGraph(g)));
			}
		}
	}

}
