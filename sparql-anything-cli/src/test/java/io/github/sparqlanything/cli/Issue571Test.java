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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * #571 - Preserve the query's BASE in RDF output (TTL, TriG, RDF/XML).
 */
public class Issue571Test {

	private static final String BASE = "http://ex.org/";
	private static final String FULL = BASE + "a/b";

	private static final String Q_WITH_BASE =
		"BASE <" + BASE + "> CONSTRUCT { <a/b> <p> <o> } WHERE {}";

	private static final String Q_NO_BASE =
		"CONSTRUCT { <" + FULL + "> <" + BASE + "p> <" + BASE + "o> } WHERE {}";

	// Jena may write "@base <...> ." or "BASE <...>" depending on prefix style
	private static boolean hasBaseLine(String out, String base) {
		return Pattern.compile("(?mi)^\\s*@?base\\s*<" + Pattern.quote(base) + ">").matcher(out).find();
	}

	// Semantic check: the output, re-parsed, must still contain the absolute IRI
	private static boolean containsSubject(String out, Lang lang, String iri) {
		Model m = ModelFactory.createDefaultModel();
		RDFParser.fromString(out, lang).parse(m);
		return m.containsResource(ResourceFactory.createResource(iri));
	}

	// tests go here

	@Test
	public void baseWrittenInTurtle() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{"-q", Q_WITH_BASE, "-f", "TTL"});
		Assert.assertTrue("missing base line:\n" + out, hasBaseLine(out, BASE));
		Assert.assertTrue("IRI not relativised:\n" + out, out.contains("<a/b>"));
		Assert.assertTrue(containsSubject(out, Lang.TTL, FULL));
	}

	@Test
	public void baseWrittenWithValues() throws Exception {
		String q = "BASE <" + BASE + "> CONSTRUCT { <a/b> <p> ?v } WHERE { BIND($_x AS ?v) }";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-f", "TTL", "-v", "x=1"});
		Assert.assertTrue("missing base line:\n" + out, hasBaseLine(out, BASE));
		Assert.assertTrue(out.contains("<a/b>"));
	}

	@Test
	public void noBaseWhenQueryHasNone() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{"-q", Q_NO_BASE, "-f", "TTL"});
		Assert.assertFalse("unexpected base line:\n" + out,
			Pattern.compile("(?mi)^\\s*@?base\\s*<").matcher(out).find());
		Assert.assertTrue(out.contains("<" + FULL + ">"));
	}

	@Test
	public void noWriteBaseSuppresses() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{"-q", Q_WITH_BASE, "-f", "TTL", "--no-write-base"});
		Assert.assertFalse(hasBaseLine(out, BASE));
		Assert.assertTrue(out.contains("<" + FULL + ">"));
	}

	@Test
	public void cliBaseOption() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{"-q", Q_NO_BASE, "-f", "TTL", "--base", BASE});
		Assert.assertTrue(hasBaseLine(out, BASE));
		Assert.assertTrue(out.contains("<a/b>"));
		Assert.assertTrue(containsSubject(out, Lang.TTL, FULL));
	}

	@Test
	public void baseWrittenInTrig() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{"-q", Q_WITH_BASE, "-f", "TRIG"});
		Assert.assertTrue(hasBaseLine(out, BASE));
		Assert.assertTrue(out.contains("<a/b>"));
	}

	@Test
	public void baseWrittenInRdfXml() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{"-q", Q_WITH_BASE, "-f", "XML"});
		Assert.assertTrue("missing xml:base:\n" + out, out.contains("xml:base=\"" + BASE + "\""));
		Assert.assertTrue(containsSubject(out, Lang.RDFXML, FULL));
	}

	@Test
	public void baseWrittenWhenStreaming() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{"-q", Q_WITH_BASE, "-f", "TTL", "-st"});
		Assert.assertTrue("missing base line:\n" + out, hasBaseLine(out, BASE));
		Assert.assertTrue(containsSubject(out, Lang.TTL, FULL));
	}
}
