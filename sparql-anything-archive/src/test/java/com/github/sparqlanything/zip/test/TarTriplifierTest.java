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

package com.github.sparqlanything.zip.test;

import com.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.zip.TarTriplifier;
import com.github.sparqlanything.zip.ZipTriplifier;
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

public class TarTriplifierTest {
	private static final Logger L = LoggerFactory.getLogger(TarTriplifierTest.class);
	@Test
	public void test1() throws MalformedURLException {
		TarTriplifier tt = new TarTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.tar").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(url.toString(), p);
			tt.triplify(p, builder);
			DatasetGraph dg = builder.getDatasetGraph();

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral("test/test.csv")));
			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral("test/test.json")));
			expectedGraph.add(new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral("test/test.xml")));
			expectedGraph.add(new Triple(n, RDF.li(5).asNode(), NodeFactory.createLiteral("test/test.txt")));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral("test/")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p))).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			L.error("",e);
		}
	}

	@Test
	public void testMatches() throws MalformedURLException {
		TarTriplifier tt = new TarTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.tar").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			p.setProperty(ZipTriplifier.MATCHES.toString(), "test/.*\\..*");

			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(url.toString(), p);
			tt.triplify(p, builder);
			DatasetGraph dg = builder.getDatasetGraph();

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral("test/test.csv")));
			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral("test/test.json")));
			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral("test/test.xml")));
			expectedGraph.add(new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral("test/test.txt")));
//			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral("test/")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p))).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			L.error("",e);
		}
	}

	@Test
	public void testBNNODE() throws MalformedURLException {
		TarTriplifier tt = new TarTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.tar").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(url.toString(), p);
			tt.triplify(p, builder);
			DatasetGraph dg = builder.getDatasetGraph();

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createURI(Path.of(url.toURI()).toUri() + "#");
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral("test/test.csv")));
			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral("test/test.json")));
			expectedGraph.add(new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral("test/test.xml")));
			expectedGraph.add(new Triple(n, RDF.li(5).asNode(), NodeFactory.createLiteral("test/test.txt")));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral("test/")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p))).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			L.error("",e);
		}
	}

}
