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

package io.github.sparqlanything.json.test;

import io.github.sparqlanything.json.JSONTriplifier;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.TripleFilteringFacadeXGraphBuilder;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class JSONTripleFilteringTest {
	public static final Logger log = LoggerFactory.getLogger(JSONTripleFilteringTest.class);

	protected FacadeXGraphBuilder getTripleFilteringBuilder(URL url, Op op, Properties p){
		return new TripleFilteringFacadeXGraphBuilder(url.toString(), op, p);
	}

	@Test
	public void friendsSinglePattern() throws IOException, TriplifierHTTPException {
		URL url = getClass().getClassLoader().getResource("./friends.json");
		JSONTriplifier jt = new JSONTriplifier();
		Properties properties = new Properties();
		OpBGP bgp = new OpBGP();
		bgp.getPattern().add(new Triple(new Node_Variable("a"), new Node_Variable("b"),
				ResourceFactory.createPlainLiteral("Romance").asNode()));
		DatasetGraph g1;
		properties.setProperty(IRIArgument.LOCATION.toString(),
				url.toString());
		FacadeXGraphBuilder builder = getTripleFilteringBuilder(url, bgp, properties);
		jt.triplify(properties, builder);
		g1 = builder.getDatasetGraph();
		// Only two triples matching the BGP
		log.info("Size is: {}", g1.getDefaultGraph().size());
		Iterator<Quad> quads = g1.find();
		while (quads.hasNext()) {
			Quad q = (Quad) quads.next();
			log.info("{} {} {}", q.getSubject(), q.getPredicate(), q.getObject());
		}
		assertTrue(g1.getDefaultGraph().size() == 2);
	}

	@Test
	public void friendsMultiplePatterns() throws IOException, TriplifierHTTPException {
		URL url = getClass().getClassLoader().getResource("./friends.json");
		JSONTriplifier jt = new JSONTriplifier();
		Properties properties = new Properties();
		OpBGP bgp = new OpBGP();
		bgp.getPattern().add(new Triple(new Node_Variable("a"), new Node_Variable("b"),
				ResourceFactory.createPlainLiteral("Romance").asNode()));
		bgp.getPattern().add(new Triple(new Node_Variable("a"), new Node_Variable("b"),
				ResourceFactory.createPlainLiteral("Comedy").asNode()));
		DatasetGraph g1;
		properties.setProperty(IRIArgument.LOCATION.toString(),
				url.toString());
		FacadeXGraphBuilder builder = getTripleFilteringBuilder(url, bgp, properties);
		jt.triplify(properties, builder);
		g1 = builder.getDatasetGraph();

		// Only four triples matching the BGP
		log.info("Size is: {}", g1.getDefaultGraph().size());
		Iterator<Quad> quads = g1.find();
		while (quads.hasNext()) {
			Quad q = (Quad) quads.next();
			log.info("{} {} {}", q.getSubject(), q.getPredicate(), q.getObject());
		}
		assertTrue(g1.getDefaultGraph().size() == 4);
	}

	@Test
	public void testEquals() {

	}
}
