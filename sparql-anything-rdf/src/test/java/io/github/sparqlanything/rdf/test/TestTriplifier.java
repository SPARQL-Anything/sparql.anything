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

package io.github.sparqlanything.rdf.test;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.rdf.RDFTriplifier;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
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
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
			assertTrue(dg.getGraph(NodeFactory.createURI("http://example.org/g")).isIsomorphicWith(getTestGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
