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

import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * #642 - `-f trig` (and `-f trix`) produce no output for a CONSTRUCT ... GRAPH query,
 * while `-f nq` works. CLI.getFormat() upper-cases the value, but the CONSTRUCT branch
 * matches TriG/TriX only against Lang.getName() ("TriG"/"TriX"), so "TRIG" never matches
 * and falls through to the "Unsupported format" branch.
 */
public class Issue642Test {

	private static final String GRAPH = "http://example.org/g";

	private static final String CONSTRUCT_GRAPH =
		"CONSTRUCT { GRAPH <" + GRAPH + "> { <http://example.org/s> <http://example.org/p> <http://example.org/o> } } "
			+ "WHERE { ?s ?p ?o }";

	private String books() throws Exception {
		return Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
	}

	/** Control: NQ already works and yields the quad in the named graph. */
	@Test
	public void nqOutputsNamedGraph() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{
			"-q", CONSTRUCT_GRAPH, "-c", "location=" + books(), "-f", "nq"
		});
		Dataset ds = RDFParser.fromString(out,Lang.NQUADS).toDataset();
		Assert.assertFalse("named graph should not be empty", ds.getNamedModel(GRAPH).isEmpty());
	}

	/** Reproduces #642: -f trig should emit the named graph, but currently produces nothing. */
	@Test
	public void trigOutputsNamedGraph() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{
			"-q", CONSTRUCT_GRAPH, "-c", "location=" + books(), "-f", "trig"
		});
		Assert.assertFalse("TriG output should not be empty", out.trim().isEmpty());
		Dataset ds = RDFParser.fromString(out,Lang.TRIG).toDataset();
		Assert.assertFalse("named graph should not be empty", ds.getNamedModel(GRAPH).isEmpty());
	}

	@Test
	public void trixOutputsNamedGraph() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{
			"-q", CONSTRUCT_GRAPH, "-c", "location=" + books(), "-f", "trix"
		});
		Assert.assertFalse("TriX output should not be empty", out.trim().isEmpty());
		Dataset ds = RDFParser.fromString(out,Lang.TRIX).toDataset();
		Assert.assertFalse("named graph should not be empty", ds.getNamedModel(GRAPH).isEmpty());
	}
}
