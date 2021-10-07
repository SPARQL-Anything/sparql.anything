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

package com.github.sparqlanything.rdf.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.Test;

import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.rdf.RDFTriplifier;

public class TestTriplifier {

	private static Graph getTestGraph() {
		Graph gs1 = GraphFactory.createGraphMem();
		gs1.add(new Triple(NodeFactory.createURI("http://example.org/a"), NodeFactory.createURI("http://example.org/b"),
				NodeFactory.createURI("http://example.org/c")));
		return gs1;
	}

	@Test
	public void testNTriples() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("ntriples.nt");
		Properties p = new Properties();
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		DatasetGraph dg;
		try {
			dg = st.triplify(p);
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTurtle() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("turtle.ttl");
		Properties p = new Properties();
		DatasetGraph dg;
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		try {
			dg = st.triplify(p);
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJSONLD() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("jsonld.jsonld");
		Properties p = new Properties();
		DatasetGraph dg;
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		try {
			dg = st.triplify(p);
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRDF() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("rdf.rdf");
		Properties p = new Properties();
		DatasetGraph dg;
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		try {
			dg = st.triplify(p);
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOWL() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("owl.owl");
		Properties p = new Properties();
		DatasetGraph dg;
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		try {
			dg = st.triplify(p);
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNQ() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("nquads.nq");
		Properties p = new Properties();
		DatasetGraph dg;
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		try {
			dg = st.triplify(p);
			assertTrue(dg.getGraph(NodeFactory.createURI("http://example.org/g")).isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTRDF() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("trdf.trdf");
		Properties p = new Properties();
		DatasetGraph dg;
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		try {
			dg = st.triplify(p);
			assertTrue(dg.getGraph(NodeFactory.createURI("http://example.org/g")).isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTRIG() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("trig.trig");
		Properties p = new Properties();
		DatasetGraph dg;
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		try {
			dg = st.triplify(p);
			assertTrue(dg.getGraph(NodeFactory.createURI("http://example.org/g")).isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTRIX() {
		RDFTriplifier st = new RDFTriplifier();
		URL url = st.getClass().getClassLoader().getResource("trix.trix");
		Properties p = new Properties();
		DatasetGraph dg;
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		try {
			dg = st.triplify(p);
			assertTrue(dg.getGraph(NodeFactory.createURI("http://example.org/g")).isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
