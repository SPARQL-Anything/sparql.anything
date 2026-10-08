package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * #681: fx:root (renamed fx:Root in #606) is still accepted in queries, with a deprecation warning.
 */
public class LegacyRootTypeTest {

	private static final String JSON = "{\\\"features\\\":[{\\\"id\\\":\\\"UKC\\\"},{\\\"id\\\":\\\"UKD\\\"}]}";
	private static final Set<String> EXPECTED = Set.of("UKC", "UKD");

	private static Set<String> run(String options, String rootPattern) {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> "
			+ "PREFIX fxe: <http://sparql.xyz/facade-x/engine/> "
			+ "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
			+ "SELECT ?id { SERVICE <x-sparql-anything:> { "
			+ "fxe:properties fxe:content \"" + JSON + "\" ; fxe:media-type \"application/json\" " + options + " . "
			+ rootPattern
			+ " ?root xyz:features/fx:anySlot/xyz:id ?id } }";
		Set<String> ids = new HashSet<>();
		try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), DatasetFactory.createGeneral())) {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) ids.add(rs.next().get("id").asLiteral().getLexicalForm());
		}
		return ids;
	}

	@Test
	public void newTerm() {
		assertEquals(EXPECTED, run("", "?root a fx:Root ."));
	}

	@Test
	public void legacyTermStillWorks() {
		assertEquals(EXPECTED, run("", "?root a fx:root ."));
	}

	@Test
	public void legacyTermInFilter() {
		assertEquals(EXPECTED, run("", "?root a ?t . FILTER(?t = fx:root)"));
	}

	/** fx:root as the deprecated form of the root option (predicate) must not be rewritten (see #386). */
	@Test
	public void legacyRootOptionAndLegacyRootType() {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> "
			+ "SELECT ?root { SERVICE <x-sparql-anything:> { "
			+ "fx:properties fx:content \"<root><child>child1</child></root>\" ; fx:media-type \"application/xml\" ; "
			+ "fx:blank-nodes false ; fx:root \"http://example.org/document\" . "
			+ "?root a fx:root } }";
		Set<String> roots = new HashSet<>();
		try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), DatasetFactory.createGeneral())) {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) roots.add(rs.next().get("root").asResource().getURI());
		}
		assertEquals(Set.of("http://example.org/document"), roots);
	}

	@Test
	public void legacyTermWithTripleFiltering() {
		assertEquals(EXPECTED, run("; fxe:strategy \"1\"", "?root a fx:root ."));
	}

	@Test
	public void legacyTermWithoutTripleFiltering() {
		assertEquals(EXPECTED, run("; fxe:strategy \"0\"", "?root a fx:root ."));
	}
}
