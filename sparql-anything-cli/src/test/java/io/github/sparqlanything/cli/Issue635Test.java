/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package io.github.sparqlanything.cli;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * #635 - Streaming CONSTRUCT/DESCRIBE output with duplicates (-st / --stream).
 *
 * A CONSTRUCT that emits the same constant triple for every solution yields many identical
 * triples. On the default (Model) path these collapse to one; with -st the output is streamed
 * triple-by-triple and the duplicates are preserved.
 *
 * Note: DESCRIBE over a virtual FacadeX graph (NO_SERVICE mode) returns nothing because the
 * describe phase queries the dataset's default graph directly, so the DESCRIBE test uses
 * loaded RDF (-l) where the triples actually live in the default graph.
 */
public class Issue635Test {

	private static final String TRIPLE = "<http://example.org/s> <http://example.org/p> <http://example.org/o>";

	private static final String CONSTRUCT_CONSTANT =
		"CONSTRUCT { " + TRIPLE + " } WHERE { ?s ?p ?o }";

	private String books() throws Exception {
		return Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
	}

	private static int countConstantTriple(String nt) {
		int c = 0;
		for (String line : nt.split("\\R")) {
			if (line.trim().equals(TRIPLE + " .")) {
				c++;
			}
		}
		return c;
	}

	/** Default path: the result Model de-duplicates, so the constant triple appears exactly once. */
	@Test
	public void defaultDeduplicates() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{
			"-q", CONSTRUCT_CONSTANT, "-c", "location=" + books(), "-f", "NT"
		});
		Assert.assertEquals(1, countConstantTriple(out));
	}

	/** With -ad: output is streamed and duplicates are preserved (the constant triple repeats). */
	@Test
	public void streamKeepsDuplicates() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{
			"-q", CONSTRUCT_CONSTANT, "-c", "location=" + books(), "-f", "NT", "-st"
		});
		Assert.assertTrue("expected the constant triple to repeat under -st", countConstantTriple(out) > 1);
	}

	/** -ad also applies to DESCRIBE for a streamable format; over loaded RDF it streams valid, non-empty N-Triples. */
	@Test
	public void streamDescribeStreams() throws Exception {
		String ttl = Objects.requireNonNull(
			getClass().getClassLoader().getResource("load-subdirs/dir1/file1.ttl")).toURI().toString();
		String out = SPARQLAnything.callMain(new String[]{
			"-q", "DESCRIBE ?s WHERE { ?s a <http://schema.org/Person> }", "-l", ttl, "-f", "NT", "-st"
		});
		Assert.assertFalse(out.trim().isEmpty());
		Assert.assertTrue(out.contains("<http://schema.org/Person>"));
	}
}
