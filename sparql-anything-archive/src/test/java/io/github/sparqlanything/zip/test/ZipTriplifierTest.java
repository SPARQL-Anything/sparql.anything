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

package io.github.sparqlanything.zip.test;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.zip.ZipTriplifier;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class ZipTriplifierTest {
	private static final Logger L = LoggerFactory.getLogger(ZipTriplifierTest.class);

	@Test
	public void test1() throws MalformedURLException {
		ZipTriplifier tt = new ZipTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.zip").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder( p);
			tt.triplify(p, builder);
			DatasetGraph dg = builder.getDatasetGraph();

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(Triple.create(n, RDF.li(1).asNode(), NodeFactory.createLiteralString("test.csv")));
			expectedGraph.add(Triple.create(n, RDF.li(2).asNode(), NodeFactory.createLiteralString("test.json")));
			expectedGraph.add(Triple.create(n, RDF.li(3).asNode(), NodeFactory.createLiteralString("test.xml")));
			expectedGraph.add(Triple.create(n, RDF.li(4).asNode(), NodeFactory.createLiteralString("test.txt")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p))).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			L.error("", e);
		}
	}


	@Test
	public void testMatches() throws MalformedURLException {
		ZipTriplifier tt = new ZipTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.zip").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			p.setProperty(ZipTriplifier.MATCHES.toString(), ".*\\.(csv|json)");
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder( p);
			tt.triplify(p, builder);
			DatasetGraph dg = builder.getDatasetGraph();

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(Triple.create(n, RDF.li(1).asNode(), NodeFactory.createLiteralString("test.csv")));
			expectedGraph.add(Triple.create(n, RDF.li(2).asNode(), NodeFactory.createLiteralString("test.json")));
//			expectedGraph.add(Triple.create(n, RDF.li(3).asNode(), NodeFactory.createLiteralString("test.xml")));
//			expectedGraph.add(Triple.create(n, RDF.li(4).asNode(), NodeFactory.createLiteralString("test.txt")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p))).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			L.error("", e);
		}
	}

	@Test
	public void testBNNODE() throws MalformedURLException {
		ZipTriplifier tt = new ZipTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.zip").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			tt.triplify(p, builder);
			DatasetGraph dg = builder.getDatasetGraph();

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createURI(Path.of(url.toURI()).toUri() + "#");
			expectedGraph.add(Triple.create(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(Triple.create(n, RDF.li(1).asNode(), NodeFactory.createLiteralString("test.csv")));
			expectedGraph.add(Triple.create(n, RDF.li(2).asNode(), NodeFactory.createLiteralString("test.json")));
			expectedGraph.add(Triple.create(n, RDF.li(3).asNode(), NodeFactory.createLiteralString("test.xml")));
			expectedGraph.add(Triple.create(n, RDF.li(4).asNode(), NodeFactory.createLiteralString("test.txt")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p))).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			L.error("", e);
		}
	}

}
