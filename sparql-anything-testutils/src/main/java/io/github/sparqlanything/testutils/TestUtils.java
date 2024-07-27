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

package io.github.sparqlanything.testutils;

import io.github.sparqlanything.model.SPARQLAnythingConstants;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {

	protected static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

	public static void printDebugDiff(Graph expected, Graph result) {
		if (logger.isDebugEnabled()) {
			ExtendedIterator<Triple> it = expected.find();
			logger.debug(">> Test expected items are also in result");
			while (it.hasNext()) {
				Triple t = it.next();
				if (!result.contains(t)) {
					logger.debug(">> {} not found in result", t);
				}
			}
			it = result.find();
			logger.debug(">> Test result items are also in expected");
			while (it.hasNext()) {
				Triple t = it.next();
				if (!expected.contains(t)) {
					logger.debug("<< {} not found in expected", t);
				}
			}
		}
	}

	public static void printDebugDiff(DatasetGraph expected, DatasetGraph result) {

		logger.debug(">> Test expected items are also in result");
		Iterator<Quad> it = expected.find();
		while (it.hasNext()) {
			Quad q = it.next();
			if (!result.contains(q)) {
				logger.debug(">> {} not found in expected", q);
			}
		}

		logger.debug(">> Test result items are also in expected");
		it = result.find();
		while (it.hasNext()) {
			Quad t = it.next();
			if (!expected.contains(t)) {
				logger.debug("<< {} not found in result", t);
			}
		}
	}

	public static void printWholeGraph(Graph expected, Graph obtained, boolean expectedResultsAvailable) {
		ByteArrayOutputStream baosExpected = new ByteArrayOutputStream();
		expected.getPrefixMapping().setNsPrefixes(SPARQLAnythingConstants.PREFIXES);
		obtained.getPrefixMapping().setNsPrefixes(SPARQLAnythingConstants.PREFIXES);
		if (expectedResultsAvailable) RDFDataMgr.write(baosExpected, expected, Lang.TTL);
		ByteArrayOutputStream baosResult = new ByteArrayOutputStream();
		RDFDataMgr.write(baosResult, obtained, Lang.TTL);
		logger.warn("Whole files\n\nExpected\n\n{}\n\n--------\n\nResult\n\n{}", baosExpected, baosResult);

	}

	public static void printWholeGraph(DatasetGraph expected, DatasetGraph obtained, boolean expectedResultsAvailable) {
		expected.getDefaultGraph().getPrefixMapping().setNsPrefixes(SPARQLAnythingConstants.PREFIXES);
		obtained.getDefaultGraph().getPrefixMapping().setNsPrefixes(SPARQLAnythingConstants.PREFIXES);
		ByteArrayOutputStream baosExpected = new ByteArrayOutputStream();
		if (expectedResultsAvailable) RDFDataMgr.write(baosExpected, expected, Lang.TRIG);
		ByteArrayOutputStream baosResult = new ByteArrayOutputStream();
		RDFDataMgr.write(baosResult, obtained, Lang.TRIG);
		logger.warn("Whole files\n\nExpected\n\n{}\n\n--------\n\nObtained\n\n{}", baosExpected, baosResult);
	}

	public static void assertIsomorphic(DatasetGraph expected, DatasetGraph got) {

		Iterator<Node> it = expected.listGraphNodes();
		Set<String> expectedGraphUris = new HashSet<>();
		while (it.hasNext()) {
			expectedGraphUris.add(it.next().getURI());
		}

		it = got.listGraphNodes();
		Set<String> resultGraphUris = new HashSet<>();

		while (it.hasNext()) {
			resultGraphUris.add(it.next().getURI());
		}

		assertTrue((expected.getDefaultGraph().isIsomorphicWith(got.getDefaultGraph())));
		assertEquals(expectedGraphUris, resultGraphUris);

		it = expected.listGraphNodes();
		while (it.hasNext()) {
			Node g = it.next();
			assertTrue(got.containsGraph(g));
			assertTrue(expected.getGraph(g).isIsomorphicWith(got.getGraph(g)));
		}
	}
}
