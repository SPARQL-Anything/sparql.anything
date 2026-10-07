package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EngineNamespaceTest {

	private static final Set<String> EXPECTED = Set.of("a", "cd");

	private static Set<String> run(String serviceIri, String subject, String options) {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> "
			+ "PREFIX fxe: <http://sparql.xyz/facade-x/engine/> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "SELECT ?slot { SERVICE <" + serviceIri + "> { " + subject + " " + options + " . ?r rdfs:member ?slot } }";
		Set<String> slots = new HashSet<>();
		try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), DatasetFactory.createGeneral())) {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) slots.add(rs.next().get("slot").asLiteral().getLexicalForm());
		}
		return slots;
	}

	@Test
	public void engineNamespace() {
		assertEquals(EXPECTED, run("x-sparql-anything:", "fxe:properties",
			"fxe:content \"abcd\" ; fxe:media-type \"text/plain\" ; fxe:txt.split \"b\""));
	}

	@Test
	public void legacyNamespaceStillWorks() {
		assertEquals(EXPECTED, run("x-sparql-anything:", "fx:properties",
			"fx:content \"abcd\" ; fx:media-type \"text/plain\" ; fx:txt.split \"b\""));
	}

	@Test
	public void mixedSubjectAndPredicateNamespaces() {
		assertEquals(EXPECTED, run("x-sparql-anything:", "fx:properties",
			"fxe:content \"abcd\" ; fxe:media-type \"text/plain\" ; fxe:txt.split \"b\""));
		assertEquals(EXPECTED, run("x-sparql-anything:", "fxe:properties",
			"fx:content \"abcd\" ; fx:media-type \"text/plain\" ; fx:txt.split \"b\""));
	}

	@Test
	public void engineNamespaceWinsOverLegacy() {
		assertEquals(EXPECTED, run("x-sparql-anything:", "fxe:properties",
			"fxe:content \"abcd\" ; fx:content \"zzzz\" ; fxe:media-type \"text/plain\" ; fxe:txt.split \"b\""));
		assertEquals(EXPECTED, run("x-sparql-anything:", "fxe:properties",
			"fx:content \"zzzz\" ; fxe:content \"abcd\" ; fxe:media-type \"text/plain\" ; fxe:txt.split \"b\""));
	}

	@Test
	public void legacyTripleStillOverridesServiceIri() {
		assertEquals(EXPECTED, run("x-sparql-anything:content=zzzz", "fx:properties",
			"fx:content \"abcd\" ; fxe:media-type \"text/plain\" ; fxe:txt.split \"b\""));
	}
}
