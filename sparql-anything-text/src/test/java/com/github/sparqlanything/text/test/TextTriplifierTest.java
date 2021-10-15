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

package com.github.sparqlanything.text.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import com.github.sparqlanything.text.TextTriplifier;

public class TextTriplifierTest {

	@Test
	public void test1() throws MalformedURLException, TriplifierHTTPException {
		TextTriplifier tt = new TextTriplifier();
		File f = new File("src/main/resources/testfile");
		URL url = f.toURI().toURL();
		try {
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			DatasetGraph dg = tt.triplify(p, new BaseFacadeXBuilder(Triplifier.getLocation(p).toString(), p));
			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(),
					NodeFactory.createLiteral("this is a test", XSDDatatype.XSDstring)));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p, url))).isIsomorphicWith(expectedGraph));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegex() throws MalformedURLException, TriplifierHTTPException {
		TextTriplifier tt = new TextTriplifier();
		File f = new File("src/main/resources/testfile");
		URL url = f.toURI().toURL();
		try {
			Properties p = new Properties();
			p.setProperty(TextTriplifier.REGEX, "\\w+");
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			DatasetGraph dg = tt.triplify(p, new BaseFacadeXBuilder(Triplifier.getLocation(p).toString(), p));
			Graph expectedGraph = GraphFactory.createGraphMem();
			Node root = NodeFactory.createBlankNode();
			expectedGraph
					.add(new Triple(root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(
					new Triple(root, RDF.li(1).asNode(), NodeFactory.createLiteral("this", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(root, RDF.li(2).asNode(), NodeFactory.createLiteral("is", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(root, RDF.li(3).asNode(), NodeFactory.createLiteral("a", XSDDatatype.XSDstring)));
			expectedGraph.add(
					new Triple(root, RDF.li(4).asNode(), NodeFactory.createLiteral("test", XSDDatatype.XSDstring)));

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p, url))).isIsomorphicWith(expectedGraph));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSplit() throws MalformedURLException, TriplifierHTTPException {
		TextTriplifier tt = new TextTriplifier();
		File f = new File("src/main/resources/testfile");
		URL url = f.toURI().toURL();
		try {
			Properties p = new Properties();
			p.setProperty(TextTriplifier.SPLIT, "\\s+");
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			DatasetGraph dg = tt.triplify(p, new BaseFacadeXBuilder(Triplifier.getLocation(p).toString(), p));
			Graph expectedGraph = GraphFactory.createGraphMem();
			Node root = NodeFactory.createBlankNode();
			expectedGraph
					.add(new Triple(root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(
					new Triple(root, RDF.li(1).asNode(), NodeFactory.createLiteral("this", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(root, RDF.li(2).asNode(), NodeFactory.createLiteral("is", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(root, RDF.li(3).asNode(), NodeFactory.createLiteral("a", XSDDatatype.XSDstring)));
			expectedGraph.add(
					new Triple(root, RDF.li(4).asNode(), NodeFactory.createLiteral("test", XSDDatatype.XSDstring)));

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p, url))).isIsomorphicWith(expectedGraph));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
