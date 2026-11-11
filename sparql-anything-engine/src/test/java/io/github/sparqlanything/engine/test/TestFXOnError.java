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
package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.engine.functions.FXBuilderFunction;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestFXOnError {

	private static final String P = "PREFIX fx: <http://sparql.xyz/facade-x/ns/>\n"
		+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";

	private RDFNode eval(String bind, String mode) {
		return eval("", bind, mode);
	}

	private RDFNode eval(String prologue, String bind, String mode) {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query q = QueryFactory.create(prologue + P + "SELECT ?r WHERE { BIND(" + bind + " AS ?r) }");
		try (QueryExecution qe = QueryExecutionFactory.create(q, DatasetFactory.createGeneral())) {
			if (mode != null) qe.getContext().set(FXBuilderFunction.ONERROR, mode);
			ResultSet rs = qe.execSelect();
			assertTrue(rs.hasNext());
			return rs.next().get("r");
		}
	}

	// fx:entity

	@Test
	public void entityInvalidDefaultIsUnbound() {
		assertNull(eval("fx:entity(\"some space\")", null));
	}

	@Test
	public void entityInvalidLenientKeepsLegacy() {
		assertEquals("some space", eval("fx:entity(\"some space\")", "lenient").asResource().getURI());
	}

	@Test
	public void entityInvalidWarnIsUnbound() {
		assertNull(eval("fx:entity(\"some space\")", "warn"));
	}

	@Test
	public void entityInvalidFailThrows() {
		Exception e = assertThrows(FXBuilderFunction.FXFunctionException.class,
			() -> eval("fx:entity(\"some \", \"space\")", "fail"));
		assertTrue(e.getMessage().contains("entity"));
		assertTrue(e.getMessage().contains("\"space\""));
	}

	@Test
	public void entityRelativeResolvedAgainstBase() {
		assertEquals("http://example.org/a/b",
			eval("BASE <http://example.org/>\n", "fx:entity(\"a/\", \"b\")", null).asResource().getURI());
	}

	@Test
	public void entityRelativeLenientNotResolved() {
		assertEquals("a/b",
			eval("BASE <http://example.org/>\n", "fx:entity(\"a/\", \"b\")", "lenient").asResource().getURI());
	}

	@Test
	public void entityValidUnchangedInFail() {
		assertEquals("http://example.org/x/1",
			eval("fx:entity(<http://example.org/>, \"x/\", 1)", "fail").asResource().getURI());
	}

	// Unbound arguments

	@Test
	public void unboundArgumentDefaultIsUnbound() {
		assertNull(eval("fx:entity(\"http://example.org/\", ?missing)", null));
	}

	@Test
	public void unboundArgumentFailThrows() {
		Exception e = assertThrows(FXBuilderFunction.FXFunctionException.class,
			() -> eval("fx:entity(\"http://example.org/\", ?missing)", "fail"));
		assertTrue(e.getMessage().contains("<unbound"));
	}

	// fx:literal

	@Test
	public void literalBadSecondArgDefaultIsUnbound() {
		assertNull(eval("fx:literal(\"foo\", 1)", null));
	}

	@Test
	public void literalBadSecondArgFailThrows() {
		assertThrows(FXBuilderFunction.FXFunctionException.class,
			() -> eval("fx:literal(\"foo\", 1)", "fail"));
	}

	@Test
	public void literalEmptyTagFailThrows() {
		assertThrows(FXBuilderFunction.FXFunctionException.class,
			() -> eval("fx:literal(\"foo\", \"\")", "fail"));
	}

	@Test
	public void literalValidUnchangedInFail() {
		assertEquals("en", eval("fx:literal(\"foo\", \"en\")", "fail").asLiteral().getLanguage());
		assertEquals("http://www.w3.org/2001/XMLSchema#int",
			eval("fx:literal(\"1\", xsd:int)", "fail").asLiteral().getDatatypeURI());
	}

	// fx:bnode

	@Test
	public void bnodeUnboundArgFailThrows() {
		assertThrows(FXBuilderFunction.FXFunctionException.class,
			() -> eval("fx:bnode(?missing)", "fail"));
	}

	@Test
	public void bnodeValidUnchangedInFail() {
		assertTrue(eval("fx:bnode(\"x\")", "fail").isAnon());
	}

	// Control: Jena built-ins are not affected by fx.onerror

	@Test
	public void jenaIriUnaffected() {
		assertNull(eval("IRI(\"some space\")", "fail"));
	}
}
