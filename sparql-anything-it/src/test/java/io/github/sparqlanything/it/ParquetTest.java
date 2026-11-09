package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Parquet files through the engine: format detection by extension, slicing, triple filtering and the schema in the
 * metadata graph.
 */
public class ParquetTest {

	private static final String PREFIXES = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> "
			+ "PREFIX fxe: <http://sparql.xyz/facade-x/engine/> "
			+ "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";

	private String location(String file) throws URISyntaxException {
		return Objects.requireNonNull(getClass().getClassLoader().getResource("parquet/" + file)).toURI().toString();
	}

	private static Set<String> select(String q, String var) {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Set<String> values = new HashSet<>();
		try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(PREFIXES + q), DatasetFactory.createGeneral())) {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) values.add(rs.next().get(var).toString());
		}
		return values;
	}

	private static boolean ask(String q) {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(PREFIXES + q), DatasetFactory.createGeneral())) {
			return qe.execAsk();
		}
	}

	private static final Set<String> EXPECTED = Set.of("Ann 1", "Bob 2");

	private Set<String> names(String options) throws URISyntaxException {
		return select("SELECT ?r { SERVICE <x-sparql-anything:location=" + location("simple.parquet") + options + "> { "
				+ "?root a fx:Root ; ?slot ?row . ?row xyz:name ?name ; xyz:value ?value . "
				+ "FILTER(DATATYPE(?value) = xsd:long) BIND(CONCAT(?name, \" \", STR(?value)) AS ?r) } }", "r");
	}

	@Test
	public void detectedByExtension() throws URISyntaxException {
		assertEquals(EXPECTED, names(""));
	}

	@Test
	public void withTripleFiltering() throws URISyntaxException {
		assertEquals(EXPECTED, names(",strategy=1"));
	}

	@Test
	public void withoutTripleFiltering() throws URISyntaxException {
		assertEquals(EXPECTED, names(",strategy=0"));
	}

	@Test
	public void sliced() throws URISyntaxException {
		assertEquals(EXPECTED, names(",slice=true"));
	}

	@Test
	public void nestedValues() throws URISyntaxException {
		Set<String> names = select("SELECT ?name { SERVICE <x-sparql-anything:location=" + location("Nested.parquet") + "> { "
				+ "?row xyz:people/fx:anySlot/xyz:name ?name } }", "name");
		assertEquals(Set.of("Ann", "Bob", "Cy"), names);
	}

	@Test
	public void schemaAndFileMetadataInTheMetadataGraph() throws URISyntaxException {
		// The engine's own metadata (file size) and the Parquet schema end up in the same graph
		assertTrue(ask("ASK { SERVICE <x-sparql-anything:location=" + location("simple.parquet") + ",metadata=true> { "
				+ "GRAPH xyz:metadata { ?file xyz:size ?size . ?root xyz:num-rows \"2\"^^xsd:long ; xyz:schema [ rdf:_2 [ xyz:name \"value\" ; xyz:physical-type \"INT64\" ] ] } } }"));
	}
}
