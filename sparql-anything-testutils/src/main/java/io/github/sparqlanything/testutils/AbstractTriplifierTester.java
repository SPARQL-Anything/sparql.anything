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

package io.github.sparqlanything.testutils;

import io.github.sparqlanything.model.*;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractTriplifierTester {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractTriplifierTester.class);
	private static final String locationUriGraph = "location";
	@Rule
	public TestName name = new TestName();
	protected Triplifier triplifier;
	protected Properties properties;
	protected URL url;
	protected String extension = null;
	protected Graph result;
	protected Graph expected;
	protected DatasetGraph expectedDatasetGraph;
	protected DatasetGraph resultDatasetGraph;
	protected Exception resultException = null;
	protected boolean useDatasetGraph = false;
	protected boolean throwsException = false;
	protected String expectedExtension;
	protected boolean loadExpectedResult = true;
	protected boolean printWholeGraph = false;

	public AbstractTriplifierTester(Triplifier t, Properties p, String extension) {
		this(t, p, extension, "ttl");
	}

	public AbstractTriplifierTester(Triplifier t, Properties p, String extension, String expectedExtension) {
		this.triplifier = t;
		this.properties = p;
		this.extension = extension;
		this.expectedExtension = expectedExtension;
		if (expectedExtension.equals("nq")) {
			useDatasetGraph = true;
		}
	}

	public void setPrintWholeGraph(boolean printWholeGraph) {
		this.printWholeGraph = printWholeGraph;
	}

	protected void prepare() throws URISyntaxException {
		logger.debug("{} (prepare)", name.getMethodName());
		// Root is Document
		// Ignore content after '$', to allow multiple methods to reuse the same files
		String fileName;
		if (name.getMethodName().contains("$")) {
			fileName = name.getMethodName().substring(4);
			fileName = fileName.substring(0, fileName.indexOf('$'));
			fileName = fileName + "." + extension;
		} else {
			fileName = name.getMethodName().substring(4) + "." + extension;
		}
		logger.debug("Input filename: {}", fileName);
		url = getClass().getClassLoader().getResource(fileName);
		properties.setProperty("location", url.toURI().toString());
		properties.setProperty("blank-nodes", "false");
		logger.debug("Input location: {}", url.toURI());
		properties.setProperty("root", "http://www.example.org/document");
		//
		// RDF file name
		String rdfFileName;
		if (name.getMethodName().contains("$")) {
			rdfFileName = name.getMethodName().substring(4);
			rdfFileName = rdfFileName.substring(0, rdfFileName.indexOf('$'));
			rdfFileName = rdfFileName + "." + expectedExtension;
		} else {
			rdfFileName = name.getMethodName().substring(4) + "." + expectedExtension;
		}

		if (loadExpectedResult) {
			if (!useDatasetGraph) {
				expected = RDFDataMgr.loadModel(getClass().getClassLoader().getResource(rdfFileName).toURI().toString()).getGraph();
			} else {
				expectedDatasetGraph = replaceLocation(RDFDataMgr.loadDatasetGraph(getClass().getClassLoader().getResource(rdfFileName).toURI().toString()));
			}
		}

	}

	protected void properties(Properties properties) {
	}

	@Before
	public void run() throws URISyntaxException, TriplifierHTTPException, IOException {
		logger.info("{} (run)", name.getMethodName());

		try {
			// Template method
			prepare();
			//
			properties(properties);
			//
			perform();
			//
			inspect();
		} catch (Exception e) {
			if (throwsException) {
				resultException = e;
			} else {
				throw e;
			}
		}
	}

	protected void inspect() {
		logger.debug("{} (inspect)", name.getMethodName());
		logger.debug("Expected (left) VS Result (right)");
		if (!useDatasetGraph) {
			TestUtils.printDebugDiff(expected, result);
			if (printWholeGraph) {
				TestUtils.printWholeGraph(expected, result);
			}
		} else {
			TestUtils.printDebugDiff(this.expectedDatasetGraph, this.resultDatasetGraph);
			if (printWholeGraph) {
				TestUtils.printWholeGraph(replaceLocation(this.expectedDatasetGraph), this.resultDatasetGraph);
			}
		}
	}

	protected void perform() throws TriplifierHTTPException, IOException, URISyntaxException {
		logger.debug("{} (perform)", name.getMethodName());
		logger.info("{}", properties);
		String graphName = Triplifier.getRootArgument(properties);
		logger.debug("Graph name: {}", graphName);
		FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(graphName, properties);
		if (properties.containsKey("slice")) {
			final Slicer slicer = (Slicer) triplifier;
			final Iterable<Slice> it = slicer.slice(properties);
			final Iterator<Slice> iterator = it.iterator();
			while (iterator.hasNext()) {
				Slice slice = iterator.next();
				slicer.triplify(slice, properties, b);
			}
		} else {
			triplifier.triplify(properties, b);
		}
		if (!useDatasetGraph) {
			this.result = b.getDatasetGraph().getGraph(NodeFactory.createURI(graphName));
		} else {
			this.resultDatasetGraph = b.getDatasetGraph();
		}
	}


	protected void assertResultIsIsomorphicWithExpected() {
		if (!useDatasetGraph) {
			assertTrue(this.result.isIsomorphicWith(expected));
		} else {
			TestUtils.assertIsomorphic(this.expectedDatasetGraph, this.resultDatasetGraph);
		}
	}


	protected void assertNotBlankNode() {
		this.resultDatasetGraph.find(null, null, null, null).forEachRemaining(q -> {
			assertFalse(q.getSubject().isBlank());
			assertFalse(q.getPredicate().isBlank());
			assertFalse(q.getObject().isBlank());
			assertFalse(q.getGraph().isBlank());
		});
	}


	protected DatasetGraph replaceLocation(DatasetGraph g) {
		DatasetGraph dg = DatasetGraphFactory.create();
		g.find().forEachRemaining(q -> {
			dg.add(new Quad(resolveNode(q.getGraph()), new Triple(resolveNode(q.getSubject()), resolveNode(q.getPredicate()), resolveNode(q.getObject()))));
		});
		return dg;
	}

	private Node resolveNode(Node n) {
		if (n.isURI() && n.getURI().equals(locationUriGraph)) {
			try {
				return NodeFactory.createURI(url.toURI().toString());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return n;
	}

}
