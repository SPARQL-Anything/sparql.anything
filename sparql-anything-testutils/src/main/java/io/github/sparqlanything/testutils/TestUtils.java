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

	public static final void printDebugDiff(Graph left, Graph right) {
		if (logger.isDebugEnabled()) {
			ExtendedIterator<Triple> it = left.find();
			logger.debug(">> Test left items are also in right");
			while (it.hasNext()) {
				Triple t = it.next();
				logger.trace(">> {}", t);

				if (!right.contains(t)) {
					logger.debug(">> {} not found in right", t);
					logger.debug(">> (T) {} {} {} {}", t.getSubject().getClass().getSimpleName(), t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(), (t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null) ? t.getObject().getLiteralDatatypeURI() : "");
				}
			}
			it = right.find();
			logger.debug(">> Test right items are also in left");
			while (it.hasNext()) {
				Triple t = it.next();
				logger.trace("<< {}", t);
				if (!left.contains(t)) {
					logger.debug("<< {} not found in left", t);
					logger.debug("<< (T) {} {} {} {}", t.getSubject().getClass().getSimpleName(), t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(), (t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null) ? t.getObject().getLiteralDatatypeURI() : "");
				}
			}
		}
	}

	public static final void printDebugDiff(DatasetGraph left, DatasetGraph right) {
		if (logger.isDebugEnabled()) {
			Iterator<Quad> it = left.find();
			while (it.hasNext()) {
				Quad q = it.next();
				logger.trace(">> {}", q);

				if (!right.contains(q)) {
					logger.debug(">> {} not found in right", q);
					logger.debug(">> (T) {} {} {} {} {}", q.getSubject().getClass().getSimpleName(), q.getPredicate().getClass().getSimpleName(), q.getObject().getClass().getSimpleName(), (q.getObject().isLiteral() && q.getObject().getLiteralDatatypeURI() != null) ? q.getObject().getLiteralDatatypeURI() : "", q.getGraph().getClass().getSimpleName());
				}
			}
			it = right.find();
			while (it.hasNext()) {
				Quad t = it.next();
				logger.trace("<< {}", t);
				if (!left.contains(t)) {
					logger.debug("<< {} not found in left", t);
					logger.debug("<< (T) {} {} {} {} {}", t.getSubject().getClass().getSimpleName(), t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(), (t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null) ? t.getObject().getLiteralDatatypeURI() : "", t.getGraph().getClass().getSimpleName());
				}
			}
		}
	}

	public static final void printWholeGraph(Graph expected, Graph obtained, boolean expectedResultsAvailable) {
		ByteArrayOutputStream baosExpected = new ByteArrayOutputStream();

		if (expectedResultsAvailable) RDFDataMgr.write(baosExpected, expected, Lang.TTL);
		ByteArrayOutputStream baosResult = new ByteArrayOutputStream();
		RDFDataMgr.write(baosResult, obtained, Lang.TTL);
		logger.warn("Whole files\n\nExpected\n\n{}\n\n--------\n\nResult\n\n{}", baosExpected, baosResult);

	}

	public static final void printWholeGraph(DatasetGraph expected, DatasetGraph obtained, boolean expectedResultsAvailable) {
		ByteArrayOutputStream baosExpected = new ByteArrayOutputStream();
		if (expectedResultsAvailable) RDFDataMgr.write(baosExpected, expected, Lang.NQ);
		ByteArrayOutputStream baosResult = new ByteArrayOutputStream();
		RDFDataMgr.write(baosResult, obtained, Lang.NQ);
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
