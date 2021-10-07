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

package com.github.sparqlanything.zip.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import com.github.sparqlanything.zip.ZipTriplifier;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.zip.TarTriplifier;

public class TarTriplifierTest {

	@Test
	public void test1() throws MalformedURLException {
		TarTriplifier tt = new TarTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.tar").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			DatasetGraph dg = tt.triplify(p);

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral("test/test.csv")));
			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral("test/test.json")));
			expectedGraph.add(new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral("test/test.xml")));
			expectedGraph.add(new Triple(n, RDF.li(5).asNode(), NodeFactory.createLiteral("test/test.txt")));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral("test/")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(url.toString())).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
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
			DatasetGraph dg = tt.triplify(p);

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
			assertTrue(dg.getGraph(NodeFactory.createURI(url.toString())).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
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
			DatasetGraph dg = tt.triplify(p);

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createURI(url.toString() + "#");
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral("test/test.csv")));
			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral("test/test.json")));
			expectedGraph.add(new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral("test/test.xml")));
			expectedGraph.add(new Triple(n, RDF.li(5).asNode(), NodeFactory.createLiteral("test/test.txt")));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral("test/")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(url.toString())).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
