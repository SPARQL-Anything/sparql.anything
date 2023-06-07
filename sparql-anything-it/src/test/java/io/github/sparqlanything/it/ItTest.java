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

package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.Triplifier;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ItTest {
	private static final Logger log = LoggerFactory.getLogger(ItTest.class);

	@Test
	public void RegistryExtensionsTest() {
		for (String ext : new String[]{"json", "html", "xml", "csv", "bin", "png", "jpeg", "jpg", "bmp", "tiff",
				"tif", "ico", "txt", "xlsx", "xls", "rdf", "ttl", "nt", "jsonld", "owl", "trig", "nq", "trix", "trdf",
				"zip", "tar", "docx", "bib", "bibtex"}) {
			Assert.assertNotNull(ext, FacadeX.Registry.getTriplifierForExtension(ext));
		}
	}

	@Test
	public void RegistryMimeTypesTest() {
		for (String mt : new String[]{"application/json", "text/html", "application/xml", "text/csv",
				"application/octet-stream", "image/png", "image/jpeg", "image/bmp", "image/tiff",
				"image/vnd.microsoft.icon", "application/vnd.ms-excel",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/rdf+thrift",
				"application/trix+xml", "application/n-quads", "text/trig", "application/owl+xml", "text/turtle",
				"application/rdf+xml", "application/n-triples", "application/ld+json", "application/zip",
				"application/x-tar", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				"application/x-bibtex"}) {
			Assert.assertNotNull(mt, FacadeX.Registry.getTriplifierForMimeType(mt));
		}
	}

	@Test
	public void CSV1() throws URISyntaxException {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
		log.debug("{}", location);
		Query query = QueryFactory.create("SELECT distinct ?p WHERE { SERVICE <x-sparql-anything:location=" + location
				+ "> { ?s ?p ?o }} ORDER BY ?p");
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		Map<Integer, String> expected = new HashMap<Integer, String>();
		expected.put(1, "http://www.w3.org/1999/02/22-rdf-syntax-ns#_1");
		expected.put(2, "http://www.w3.org/1999/02/22-rdf-syntax-ns#_2");
		expected.put(3, "http://www.w3.org/1999/02/22-rdf-syntax-ns#_3");
		expected.put(4, "http://www.w3.org/1999/02/22-rdf-syntax-ns#_4");
		expected.put(5, RDF.type.getURI());
		while (rs.hasNext()) {
			int rowId = rs.getRowNumber() + 1;
			QuerySolution qs = rs.next();
			log.trace("{} {} {}", rowId, qs.get("p").toString(), expected.get(rowId));
			assertEquals(expected.get(rowId), qs.get("p").toString());
		}
	}

	@Test
	public void CSV1headers() throws URISyntaxException {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
		log.debug("{}", location);
		Query query = QueryFactory.create(
				"SELECT distinct ?p WHERE { SERVICE <x-sparql-anything:csv.headers=true,namespace=http://www.example.org/csv#,location="
						+ location + "> { ?s ?p ?o }} ORDER BY ?p");
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		Map<Integer, String> expected = new HashMap<Integer, String>();

		expected.put(1, "http://www.example.org/csv#A");
		expected.put(2, "http://www.example.org/csv#B");
		expected.put(3, "http://www.example.org/csv#C");
		expected.put(4, "http://www.example.org/csv#D");
		expected.put(5, "http://www.w3.org/1999/02/22-rdf-syntax-ns#_1");
		expected.put(6, "http://www.w3.org/1999/02/22-rdf-syntax-ns#_2");
		expected.put(7, RDF.type.getURI());
		while (rs.hasNext()) {
			int rowId = rs.getRowNumber() + 1;
			QuerySolution qs = rs.next();
			log.trace("{} {} {}", rowId, qs.get("p").toString(), expected.get(rowId));
			assertEquals(expected.get(rowId), qs.get("p").toString());
		}
	}

	@Test
	public void CSV1headersDefaultNS() throws URISyntaxException {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("test1.csv")).toURI().toString();
		log.debug("{}", location);
		Query query = QueryFactory
				.create("SELECT distinct ?p WHERE { SERVICE <x-sparql-anything:csv.headers=true,location=" + location
						+ "> { ?s ?p ?o }} ORDER BY ?p");
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs));
		Map<Integer, String> expected = new HashMap<Integer, String>();

		expected.put(1, "http://sparql.xyz/facade-x/data/A");
		expected.put(2, "http://sparql.xyz/facade-x/data/B");
		expected.put(3, "http://sparql.xyz/facade-x/data/C");
		expected.put(4, "http://sparql.xyz/facade-x/data/D");
		expected.put(5, "http://www.w3.org/1999/02/22-rdf-syntax-ns#_1");
		expected.put(6, "http://www.w3.org/1999/02/22-rdf-syntax-ns#_2");
		expected.put(7, RDF.type.getURI());

		while (rs.hasNext()) {
			int rowId = rs.getRowNumber() + 1;
			QuerySolution qs = rs.next();
//			System.out.println(rowId+" "+qs.get("p").toString()+" "+expected.get(rowId));
			log.trace("{} {} {}", rowId, qs.get("p").toString(), expected.get(rowId));
			assertEquals(expected.get(rowId), qs.get("p").toString());
		}
	}

	@Test
	public void JSON1() throws URISyntaxException {
		// a01009-14709.json
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
		Query query = QueryFactory.create(
				"SELECT DISTINCT ?p WHERE { SERVICE <x-sparql-anything:namespace=http://www.example.org#,location="
						+ location + "> { ?s ?p ?o }} order by ?p");
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		List<String> mustInclude = new ArrayList<String>(
				Arrays.asList("http://www.example.org#thumbnailUrl", "http://www.example.org#title",
						"http://www.w3.org/1999/02/22-rdf-syntax-ns#_1", "http://www.example.org#text",
						"http://www.example.org#subjects", "http://www.example.org#subjectCount"));
		while (rs.hasNext()) {
			int rowId = rs.getRowNumber() + 1;
			QuerySolution qs = rs.next();
			log.trace("{} {}", rowId, qs.get("p").toString());
			mustInclude.remove(qs.get("p").toString());
		}
		Assert.assertTrue(mustInclude.isEmpty());
	}

	@Test
	public void JPG1() throws URISyntaxException {
		// A01009_8.jpg
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String location = getClass().getClassLoader().getResource("tate-gallery/A01009_8.jpg").toURI().toString();
		Query query = QueryFactory
				.create("SELECT * WHERE { SERVICE <x-sparql-anything:location=" + location + "> { ?s ?p ?o }}");
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		// Should only contain 1 triple
		while (rs.hasNext()) {
//            int rowId = rs.getRowNumber() + 1;
			QuerySolution qs = rs.next();
			Assert.assertTrue(qs.get("s").asNode().isBlank());
			Assert.assertTrue(qs.get("p").toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#_1")
					|| qs.get("p").toString().equals(RDF.type.getURI()));
			Assert.assertTrue(qs.get("o").isLiteral() || qs.get("o").toString().equals(Triplifier.FACADE_X_TYPE_ROOT));
		}
	}

	@Test
	public void NestedTest1() throws URISyntaxException {
		String location = getClass().getClassLoader().getResource("tate-gallery/artwork_data.csv").toURI().toString();
		String queryStr = "" + "prefix ex: <http://www.example.org#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT ?accession ?thumbnail ?image {"
				+ "BIND (IRI(CONCAT(\"x-sparql-anything:\", ?thumbnail )) AS ?embed ) . "
				+ "SERVICE <x-sparql-anything:csv.headers=true,namespace=http://www.example.org#,location=" + location
				+ "> {" + "FILTER (?accession = \"A01009\") . "
				+ "[] ex:accession ?accession ; ex:thumbnailUrl ?thumbnail " + "}" + ""
				+ "SERVICE ?embed { [] rdf:_1 ?image } . " + "} LIMIT 1";
//		log.debug("\n{}\n", queryStr);

		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory.create(queryStr);
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			Assert.assertTrue(qs.get("accession").isLiteral());
			Assert.assertTrue(qs.get("thumbnail").isLiteral());
			Assert.assertTrue(qs.get("image").isLiteral());
			assertEquals("http://www.w3.org/2001/XMLSchema#base64Binary", qs.get("image").asNode().getLiteralDatatypeURI());
		}
	}

	@Test
	public void TriplifyTateGalleryArtworkData() throws IOException, URISyntaxException {
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("tate-gallery/artwork_data.csv")).toURI().toString();
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tate-gallery1.sparql")), StandardCharsets.UTF_8).replace("%%artwork_data%%", location);
		log.debug(queryStr);
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory.create(queryStr);
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			log.debug("{}}", qs);
		}
//        Model model = QueryExecutionFactory.create(query, kb).execConstruct();
//        log.debug("Produced {} triples", model.size());
//        // Write as Turtle via model.write
//        model.write(System.out, "TTL") ;
	}

	@Test
	public void TriplifyTateGalleryArtworkJSON() throws IOException, URISyntaxException {
		String location = getClass().getClassLoader().getResource("tate-gallery/a01003-14703.json").toURI().toString();
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tate-gallery2.sparql")), StandardCharsets.UTF_8).replace("%%artwork_json%%", location);
		log.debug(queryStr);
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory.create(queryStr);
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			log.trace("{}}", qs);
		}
//        Model model = QueryExecutionFactory.create(query, kb).execConstruct();
//        log.debug("Produced {} triples", model.size());
//        // Write as Turtle via model.write
//        model.write(System.out, "TTL") ;
	}

	@Test
	public void triplifySpreadsheet() throws IOException, URISyntaxException {
		String location = getClass().getClassLoader().getResource("Book1.xls").toURI().toString();
		Query query = QueryFactory.create("SELECT distinct ?p WHERE { SERVICE <x-sparql-anything:location=" + location
				+ "> { ?s ?p ?o }} ORDER BY ?p");
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		Assert.assertTrue(rs.hasNext());
	}

	@Test
	public void triplifyDocx() throws IOException, URISyntaxException {
		String location = getClass().getClassLoader().getResource("Document.docx").toURI().toString();
		Query query = QueryFactory.create("SELECT distinct ?p WHERE { SERVICE <x-sparql-anything:location=" + location
				+ "> { ?s ?p ?o }} ORDER BY ?p");
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		Assert.assertTrue(rs.hasNext());
	}

	@Test
	public void triplifyRDF() throws IOException, URISyntaxException {
		String location = getClass().getClassLoader().getResource("nquads.nq").toURI().toString();
		Query query = QueryFactory.create("ASK { SERVICE <x-sparql-anything:location=" + location
				+ "> { GRAPH <http://example.org/g> {<http://example.org/a> <http://example.org/b> <http://example.org/c>} }} ");
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Assert.assertTrue(QueryExecutionFactory.create(query, kb).execAsk());
	}

	@Ignore // Ignoring as we don't want HTTP dependent builds
	@Test
	public void triplifyRDFHTTP() throws IOException, URISyntaxException {
		String location = "https://sparql.xyz/facade-x/ns/index.ttl"; //getClass().getClassLoader().getResource("nquads.nq").toURI().toString();
		Query query = QueryFactory.create("ASK { SERVICE <x-sparql-anything:location=" + location
				+ "> {{?s ?p ?o}} }");
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Assert.assertTrue(QueryExecutionFactory.create(query, kb).execAsk());
	}

	@Test
	public void triplifyExternal() throws IOException, URISyntaxException {
		Query query = QueryFactory.create(
				"PREFIX xyz: <http://sparql.xyz/facade-x/data/>PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>SELECT *WHERE {    SERVICE <x-sparql-anything:csv.headers=true,location=https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale-20200409.csv> {        ?s ?p ?o    }}");
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		Assert.assertTrue(QueryExecutionFactory.create(query, kb).execAsk());
//		log.debug("\n{}", ResultSetFormatter.asText(QueryExecutionFactory.create(query, kb).execSelect()));
	}

	@Test
	public void testNoLocation() throws IOException, URISyntaxException {
		Query query = QueryFactory.create(
				"PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ASK {    SERVICE <x-sparql-anything:content=abcd,txt.regex=b> { ?r a <http://sparql.xyz/facade-x/ns/root>.  ?r <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> \"b\" }}");
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Assert.assertTrue(QueryExecutionFactory.create(query, kb).execAsk());
	}

	@Test
	public void testPropertiesAsBGP() throws IOException, URISyntaxException {
		Query query = QueryFactory.create(
				"PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX xyz: <http://sparql.xyz/facade-x/ns/> ASK {    SERVICE <x-sparql-anything:> {   xyz:properties xyz:txt.regex \"b\" ; xyz:content \"abcd\" .  ?r a <http://sparql.xyz/facade-x/ns/root> .  ?r <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> \"b\" }}");
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		System.out.println(QueryExecutionFactory.create(query, kb).execAsk());
		Assert.assertTrue(QueryExecutionFactory.create(query, kb).execAsk());

	}

	@Test
	public void testPropertiesOrder() throws IOException, URISyntaxException {
		Query query = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE {    SERVICE <x-sparql-anything:> {  fx:properties fx:location \"https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale-20200409.csv\" . fx:properties fx:csv.headers true .   ?s ?p ?o    }}");
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		Assert.assertTrue(QueryExecutionFactory.create(query, kb).execAsk());
//		log.debug("\n{}", ResultSetFormatter.asText(QueryExecutionFactory.create(query, kb).execSelect()));

		Query query2 = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE {    SERVICE <x-sparql-anything:> { fx:properties fx:csv.headers true .  fx:properties fx:location \"https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale-20200409.csv\" .    ?s ?p ?o    }}");
//		Assert.assertTrue(QueryExecutionFactory.create(query, kb).execAsk());
//		log.debug("\n{}", ResultSetFormatter.asText(QueryExecutionFactory.create(query2, kb).execSelect()));

	}

	@Test
	public void testVariablesInPropertyGraph() throws IOException, URISyntaxException {
		String loc = Objects.requireNonNull(getClass().getClassLoader().getResource("test-propbank.xml")).toURI().toString();

		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("query3.sparql")).toURI(), StandardCharsets.UTF_8);
		queryStr = queryStr.replace("%%%LOCATION%%%", loc);

		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		log.debug("Query: {}", QueryFactory.create(queryStr));
		ResultSet rs = QueryExecutionFactory.create(queryStr, kb).execSelect();
		Set<String> result = new HashSet<>();
		System.out.println(ResultSetFormatter.asText(rs));
//		while (rs.hasNext()) {
//			QuerySolution querySolution = (QuerySolution) rs.next();
////			querySolution.varNames().forEachRemaining(v -> {
////				System.out.println("v -> " + querySolution.get(v));
////			});
//			if (querySolution.contains("c")) {
//				result.add(querySolution.get("c").asLiteral().getValue().toString());
//			}
//		}
//		assertEquals(Sets.newHashSet("Quitting_a_place", "Departing"), result);
	}

	@Test
	public void testFromArchive() throws IOException, URISyntaxException {
		String archive = getClass().getClassLoader().getResource("test.tar").toURI().toString();

		String q = "PREFIX xyz: <http://sparql.xyz/facade-x/ns/> SELECT ?o {SERVICE <x-sparql-anything:> { xyz:properties xyz:from-archive ?archive . xyz:properties xyz:location \"test/test.txt\" .  ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?o} }";

		ParameterizedSparqlString pss = new ParameterizedSparqlString(q);
		pss.setIri("archive", archive);
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs = QueryExecutionFactory.create(pss.asQuery(), kb).execSelect();

		String res = null;

		if (rs.hasNext()) {
			res = rs.next().get("o").asLiteral().getValue().toString();
		}
		assertEquals("this is a test", res);

	}

	@Test
	public void testXIP() throws IOException, URISyntaxException {
		String archive = getClass().getClassLoader().getResource("test.tar").toURI().toString();

		//@f:off
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT * { "
				+ " SERVICE <x-sparql-anything:> { "
				+ "   fx:properties fx:location ?archive . "
				+ "   ?s ?p ?file .  FILTER isLiteral(?file) "
				+ "   FILTER(fx:isFacadeXExtension(?file)) "
				+ "   SERVICE <x-sparql-anything:> { "
				+ "     fx:properties fx:from-archive ?archive . "
				+ "     fx:properties fx:location ?file  .  "
				+ "     ?s1 ?p1 ?o1   FILTER isLiteral(?o1) "
				+ "   } \n"
				+ " } "
				+ "}";
		//@f:on

		ParameterizedSparqlString pss = new ParameterizedSparqlString(q);
		pss.setIri("archive", archive);
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs = QueryExecutionFactory.create(pss.asQuery(), kb).execSelect();

//		System.out.println(ResultSetFormatter.asText(rs));

		List<List<String>> actual = new ArrayList<>();

		while (rs.hasNext()) {
			QuerySolution qs = (QuerySolution) rs.next();
			if (qs.get("o1").isLiteral())
				actual.add(Lists.newArrayList(qs.get("file").asLiteral().getValue().toString(),
						qs.get("o1").asLiteral().getValue().toString()));
		}

		assertTrue(actual.contains(Lists.newArrayList("test/test.csv", "Year")));
		assertTrue(actual.contains(Lists.newArrayList("test/test.json", "Sword of Honour")));
		assertTrue(actual.contains(Lists.newArrayList("test/test.xml", "Computer")));
		assertTrue(actual.contains(Lists.newArrayList("test/test.txt", "this is a test")));

	}

	@Test
	public void testFolder() throws IOException, URISyntaxException {
		String archive = getClass().getClassLoader().getResource("test/").toURI().toString();

		//@f:off
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT * { "
				+ " SERVICE <x-sparql-anything:> { "
				+ "   fx:properties fx:location ?archive . "
				+ "   ?s ?p ?file .  FILTER isLiteral(?file) "
				+ "   FILTER(fx:isFacadeXExtension(?file)) "
				+ "   SERVICE <x-sparql-anything:> { "
				+ "     fx:properties fx:location ?file  .  "
				+ "     ?s1 ?p1 ?o1   FILTER isLiteral(?o1)"
				+ "   } \n"
				+ " } "
				+ "}";
		//@f:on

		ParameterizedSparqlString pss = new ParameterizedSparqlString(q);
		pss.setIri("archive", archive);
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs = QueryExecutionFactory.create(pss.asQuery(), kb).execSelect();

//		System.out.println(ResultSetFormatter.asText(rs));

		List<List<String>> actual = new ArrayList<>();

		while (rs.hasNext()) {
			QuerySolution qs = (QuerySolution) rs.next();
			if (qs.get("o1").isLiteral())
				actual.add(Lists.newArrayList(qs.get("file").asLiteral().getValue().toString(),
						qs.get("o1").asLiteral().getValue().toString()));
		}

		archive = archive.replace("file:/", "file:///");

		assertTrue(actual.contains(Lists.newArrayList(archive + "test.csv", "Year")));
		assertTrue(actual.contains(Lists.newArrayList(archive + "test.json", "Sword of Honour")));
		assertTrue(actual.contains(Lists.newArrayList(archive + "test.xml", "Computer")));
		assertTrue(actual.contains(Lists.newArrayList(archive + "test.txt", "this is a test")));

	}

	@Test
	public void testAnySlotMagicProperty() throws IOException, URISyntaxException {
		Query query = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  {      "
						+ "SERVICE <x-sparql-anything:content=abcd,txt.split=b> { " + "?r fx:anySlot ?slot   }}");

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();
		Set<String> slots = new HashSet<>();
		while (rs.hasNext()) {
			QuerySolution querySolution = (QuerySolution) rs.next();
			slots.add(querySolution.get("slot").asLiteral().getValue().toString());
		}

		assertEquals(Sets.newHashSet("a", "cd"), slots);

	}

	@Test
	public void testBibtex() throws IOException, URISyntaxException {
		Query query = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  "
				+ "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT ?title  {      "
				+ "SERVICE <x-sparql-anything:> { fx:properties fx:content \"@article{Knuth1984, title={Literate Programming}}\" . fx:properties fx:media-type \"application/x-bibtex\" ."
				+ "?s <http://sparql.xyz/facade-x/data/title> ?title  }}");

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();

//		System.out.println(ResultSetFormatter.asText(rs));
		String title = null;
		if (rs.hasNext()) {
			title = rs.next().get("title").asLiteral().getValue().toString();
		}
		assertEquals("Literate Programming", title);

	}

	@Test
	public void testAnySlotMagicPropertyPropertyPath() throws IOException, URISyntaxException {
		String location = getClass().getClassLoader().getResource("propertypath.json").toURI().toString();
		Query query = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  "
				+ "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT DISTINCT ?slot  {      "
				+ "SERVICE <x-sparql-anything:location=" + location + "> { " + "?s fx:anySlot/fx:anySlot ?slot . }}");

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();
		Set<String> slots = new HashSet<>();
		while (rs.hasNext()) {
			QuerySolution querySolution = (QuerySolution) rs.next();
			if (querySolution.get("slot").isLiteral()) {
				slots.add(querySolution.get("slot").asLiteral().getValue().toString());
			}
		}

		assertEquals(Sets.newHashSet("d", "c"), slots);

	}

	// This is waiting until issue #114 is resolved
	@Ignore
	@Test
	public void testAnySlotMagicPropertyPropertyPath2() throws IOException, URISyntaxException {
		String location = getClass().getClassLoader().getResource("anySlotPath.json").toURI().toString();
		String q = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX fx:   <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX xyz:  <http://sparql.xyz/facade-x/data/>\n"
				+ "PREFIX ont: <http://sparql.xyz/facade-x/example/>\n"
				+ "PREFIX xpath: <https://www.w3.org/TR/xpath-functions/>\n" + "SELECT ?placeId ?latitude ?longitude\n"
				+ "where {\n" + "  service <x-sparql-anything:> {\n" + "  \t  fx:properties fx:location \"" + location
				+ "\" ;\n" + "\t  \tfx:media-type \"application/json\" ;\n" + "\t\t  fx:blank-nodes false\n"
				+ "\t\t  .\n" + "\t  [] xyz:track%5Fid ?track_id ;\n" + "\t  \t ?pArtist ?artist ;\n"
				+ "\t\t ?pArtistForIri ?artistForIri ;\n" + "\t\t xyz:title ?title ;\n"
				+ "\t\t xyz:recording%5Fplaces ?places\n" + "\t\t .\n"
				+ "\t\t FILTER(REGEX(STR(?pArtist),\".*artist%5F[0-9]+$\")) .\n"
				+ "\t\t FILTER(REGEX(STR(?pArtistForIri),\".*artist%5Ffor%5Firi%5F[0-9]+$\")) .\n" + "\t\t \n"
				+ "\t\t ####\n" + "\t\t ?places fx:anySlot [\n"
				+ "\t\t \txyz:place [ xyz:id ?placeId ; xyz:coordinates [ xyz:latitude ?latitude ; xyz:longitude ?longitude ]]]\n"
				+
				// "\t\t ?places fx:anySlot/xyz:place [ xyz:id ?placeId ; xyz:coordinates [
				// xyz:latitude ?latitude ; xyz:longitude ?longitude ]]\n" +
				"}}";
//		System.out.println(q);
		Query query = QueryFactory.create(q);

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();
		Set<String> slots = new HashSet<>();
		while (rs.hasNext()) {
			QuerySolution querySolution = (QuerySolution) rs.next();
			if (querySolution.get("placeId").isLiteral()) {
				slots.add(querySolution.get("placeId").asLiteral().getValue().toString());
			}
		}
		System.err.println(slots);
		// assertEquals(Sets.newHashSet("b", "d", "c"), slots);

	}
}
