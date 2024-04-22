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

package io.github.sparqlanything.it;

import io.github.sparqlanything.cli.SPARQLAnything;
import io.github.sparqlanything.engine.FacadeX;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assume;
import io.github.sparqlanything.model.HTTPHelper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class IssuesTest {

	private static final Logger log = LoggerFactory.getLogger(IssuesTest.class);

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/280">Issue 280</a>
	 * <p>
	 */
	@Test
	public void testIssue280() throws URISyntaxException {
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue280.json")).toURI().toString();
		String TDBLocation = "target/tdbIssue280/";
		File TDBfile = new File(TDBLocation);
		if (TDBfile.exists()) {
			boolean isTDBFileDeleted = TDBfile.delete();
			log.trace("Has TDB folder been deleted? {}", isTDBFileDeleted);
		}
		boolean tdbFolderCreated = TDBfile.mkdirs();
		log.trace("Has TDB folder been deleted? {}", tdbFolderCreated);
		log.debug("TDB temp location: {}", TDBLocation);
		Query qs = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " +
						"PREFIX xyz: <http://sparql.xyz/facade-x/data/> " +
						"SELECT * WHERE { " +
						"SERVICE <x-sparql-anything:location=" + location + ",ondisk=" + TDBLocation + "> { " +
						" ?s xyz:name ?o }  }");

//		System.out.println(location);
//		System.out.println(qs.toString(Syntax.defaultSyntax));
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs = QueryExecutionFactory.create(qs, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs));
		Set<String> results = new HashSet<>();
		while (rs.hasNext()) {
			results.add(rs.next().get("o").asLiteral().getValue().toString());
		}
//		System.out.println(results);
		assertTrue(results.contains("Friends"));
		assertTrue(results.contains("Cougar Town"));


		qs = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  PREFIX xyz: <http://sparql.xyz/facade-x/data/> SELECT * WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:location \"" + location + "\" ; fx:ondisk \"" + TDBLocation + "\" .  " +
						" ?s xyz:name ?o }  }");


		rs = QueryExecutionFactory.create(qs, ds).execSelect();
		results = new HashSet<>();
		while (rs.hasNext()) {
			results.add(rs.next().get("o").asLiteral().getValue().toString());
		}
		assertTrue(results.contains("Friends"));
		assertTrue(results.contains("Cougar Town"));
	}


	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/284">...</a>
	 * <p>
	 */
	@Test
	public void testIssue284_2() throws URISyntaxException {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.facadeiri", "ERROR");

		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue284.json")).toURI().toString();
		String locationExpected = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue284.ttl")).toURI().toString();

//		Query qs = QueryFactory.create(
//				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " +
//						"PREFIX xyz: <http://sparql.xyz/facade-x/data/> " +
//						"SELECT ?id WHERE { " +
//						"SERVICE <x-sparql-anything:location=" + location + "> { " +
//						" ?s xyz:students/fx:anySlot/xyz:ID ?id }  }");

//		System.out.println(location);
//		System.out.println(qs.toString(Syntax.defaultSyntax));
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

//		ResultSet rs = QueryExecutionFactory.create(qs, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs));


		Query qs = QueryFactory.create("PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n" +
				"PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n" +
				"base  <http://example.com/base/> \n" +
				"\n" +
				"CONSTRUCT\n" +
				"  {\n" +
				"    ?subject0 <http://example.com/id> ?0 .\n" +
				" }\n" +
				"WHERE\n" +
				"  {\n" +
				"    SERVICE <x-sparql-anything:location=" + location + ">\n" +
				"      {\n" +
				"      \t?s0   xyz:students/fx:anySlot ?iterator0 . \n" +
				"        ?iterator0  xyz:ID    ?0;\n" +
				"        bind(fx:entity(\"http://example.com/\", ?0) as ?subject0)\n" +
				"      }\n" +
				"  }");

//		System.out.println(qs.toString());

		Model m = QueryExecutionFactory.create(qs, ds).execConstruct();

		Model expected = ModelFactory.createDefaultModel();
		RDFDataMgr.read(expected, locationExpected);

		assertTrue(m.isIsomorphicWith(expected));

//		Set<String> results = new HashSet<>();
//		while (rs.hasNext()) {
//			results.add(rs.next().get("o").asLiteral().getValue().toString());
//		}
////		System.out.println(results);
//		assertTrue(results.contains("Friends"));
//		assertTrue(results.contains("Cougar Town"));
	}

	@Test
	public void testIssue356() throws URISyntaxException, IOException {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
		String query = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue356.sparql")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();
		Assert.assertTrue(rs.hasNext());
	}

	@Test
	public void testIssue356CLI() throws Exception {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
		String query = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue356.sparql")).toURI(), StandardCharsets.UTF_8);
		String output = SPARQLAnything.callMain(new String[]{
				"-q", query
		});
		Assert.assertTrue(output.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://sparql.xyz/facade-x/ns/root"));
	}

	@Test
	public void issue75() throws URISyntaxException {
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("test1.csv")).toURI().toString();
		log.debug("Location {}", location);
		Query query1 = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> " + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  {      " + "SERVICE <x-sparql-anything:> { " + " fx:properties fx:csv.headers true . fx:properties fx:location \"" + location + "\" . " + " ?s rdf:_1 ?o . ?o xyz:A ?a }}");

		Query query2 = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> " + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  {      " + "SERVICE <x-sparql-anything:> { " + "" + " fx:properties fx:location \"" + location + "\";" + " fx:csv.headers true " + "." + " ?s rdf:_1 ?o . ?o xyz:A ?a }}");

		Dataset ds = DatasetFactory.createGeneral();

//		System.out.println(query1.toString(Syntax.syntaxSPARQL_11));
//		System.out.println(query2.toString(Syntax.syntaxSPARQL_11));

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs1 = QueryExecutionFactory.create(query1, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs1));
		List<String> list1 = new ArrayList<>();
		while (rs1.hasNext()) {
			QuerySolution querySolution = rs1.next();
//			System.out.println(querySolution);
			list1.add(querySolution.getLiteral("a").getValue().toString());
		}

		ResultSet rs2 = QueryExecutionFactory.create(query2, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs2));
		List<String> list2 = new ArrayList<>();
		while (rs2.hasNext()) {
			QuerySolution querySolution = rs2.next();
//			System.out.println(querySolution);
			list2.add(querySolution.getLiteral("a").getValue().toString());
		}

//		System.out.println(list1);
//		System.out.println(list2);

		assertEquals(list1, list2);

	}

	@Test
	public void testIssue83() throws URISyntaxException {
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("test1.csv")).toURI().toString();
		log.debug("Location {}", location);

		Query query1 = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> " + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  { " + "SERVICE <x-sparql-anything:csv.headers=true> { " + "" + " fx:properties fx:location ?location ; " + " fx:csv.null-string \"\"" + "." + " ?s rdf:_1 ?o . ?o xyz:A ?r  VALUES (?location) {(\"" + location + "\")} }}");

		Dataset ds = DatasetFactory.createGeneral();

//		System.out.println(query1.toString(Syntax.syntaxSPARQL_11));

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs1 = QueryExecutionFactory.create(query1, ds).execSelect();
		Set<String> results = new HashSet<>();
		while (rs1.hasNext()) {
			QuerySolution querySolution = rs1.next();
			results.add(querySolution.getLiteral("r").getValue().toString());
		}
		assertEquals(Sets.newHashSet("A1"), results);

		Query query2 = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> " + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  { " + "SERVICE <x-sparql-anything:csv.headers=true> { " + "" + " BIND (\"" + location + "\" AS ?location ) fx:properties fx:location ?location ; " + " fx:csv.null-string \"\"" + "." + " ?s rdf:_1 ?o . ?o xyz:A ?r  }}");

//		System.out.println(query2.toString(Syntax.syntaxSPARQL_11));
		ResultSet rs2 = QueryExecutionFactory.create(query2, ds).execSelect();

		results = new HashSet<>();
		while (rs2.hasNext()) {
			QuerySolution querySolution = rs2.next();
			results.add(querySolution.getLiteral("r").getValue().toString());
		}
		assertEquals(Sets.newHashSet("A1"), results);

//		System.out.println(ResultSetFormatter.asText(rs2));

	}

	// Root container cannot be an object
	// Refers to #93
	@Test
	public void testIssue93() throws URISyntaxException, IOException {
		String qs = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issue93.sparql")), StandardCharsets.UTF_8);
		qs = qs.replace("%%location%%", Objects.requireNonNull(getClass().getClassLoader().getResource("issue93.html")).toURI().toString());
		Query query = QueryFactory.create(qs);
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Dataset ds = DatasetFactory.createGeneral();
		boolean rootInObject = QueryExecutionFactory.create(query, ds).execAsk();
		Assert.assertFalse(rootInObject);
	}

	@Test
	public void testIssue114() throws URISyntaxException {
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("propertypath.json")).toURI().toString();
		Query query = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> " + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT DISTINCT ?slot  {      " + "SERVICE <x-sparql-anything:> { fx:properties fx:location \"" + location + "\" .   " + "?s fx:anySlot/fx:anySlot ?slot . }}");

//		query = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  "
//				+ "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
//				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT DISTINCT ?slot  {      "
//				+ "SERVICE <x-sparql-anything:" + location + "> {  " + "?s fx:anySlot/fx:anySlot ?slot . }}");

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs));
		Set<String> slots = new HashSet<>();
		while (rs.hasNext()) {
			QuerySolution querySolution = rs.next();
			if (querySolution.get("slot").isLiteral()) {
				slots.add(querySolution.get("slot").asLiteral().getValue().toString());
			}
		}

		assertEquals(Sets.newHashSet("d", "c"), slots);
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/154">...</a>
	 */
	@Test
	public void testIssue154() throws URISyntaxException, IOException {
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/e.sparql")).toURI(), StandardCharsets.UTF_8);
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/a00002-1036.xml")).toURI().toString();
		Query query = QueryFactory.create(queryStr.replace("%%LOCATION%%", location));
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		// XXX This process never ends!
		Model rs = QueryExecutionFactory.create(query, ds).execConstruct();
		Assert.assertTrue(rs.size() > 0);
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/154">...</a>
	 */
	@Test
	public void test2Issue154() throws URISyntaxException, IOException {
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue154.xml")).toURI().toString();

		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue154.sparql")).toURI(), StandardCharsets.UTF_8);

		Query query = QueryFactory.create(queryStr.replace("%%LOCATION%%", location));

		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Set<Set<String>> expectedResult = new HashSet<>(Sets.newHashSet(Sets.newHashSet("Randall, Cynthia", "Lover Birds"), Sets.newHashSet("Thurman, Paula", "Splish Splash"), Sets.newHashSet("Corets, Eva", "Oberon's Legacy"), Sets.newHashSet("Corets, Eva", "The Sundered Grail"), Sets.newHashSet("Ralls, Kim", "Midnight Rain"), Sets.newHashSet("Corets, Eva", "Maeve Ascendant"), Sets.newHashSet("Gambardella, Matthew", "XML Developer's Guide")));

//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		Set<Set<String>> actualResult = new HashSet<>();
		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();
		while (rs.hasNext()) {
			QuerySolution querySolution = rs.next();
			actualResult.add(Sets.newHashSet(querySolution.getLiteral("authorString").getValue().toString(), querySolution.getLiteral("titleString").getValue().toString()));
		}

//		System.out.println(actualResult);

		assertEquals(expectedResult, actualResult);

	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/175">...</a>
	 */
	@Test
	public void testIssue175() throws URISyntaxException, IOException {
		Assume.assumeTrue(HTTPHelper.checkHostIsReachable("https://sparql-anything.cc"));
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue175.sparql")).toURI(), StandardCharsets.UTF_8);
		String queryStr2 = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue175-2.sparql")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		assertFalse(QueryExecutionFactory.create(query, ds).execSelect().next());
		assertFalse(QueryExecutionFactory.create(queryStr, ds).execSelect().hasNext());
		assertTrue(QueryExecutionFactory.create(queryStr2, ds).execSelect().hasNext());
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/173">...</a>
	 */
	@Test
	public void testIssue173() throws URISyntaxException, IOException {
		Assume.assumeTrue(HTTPHelper.checkHostIsReachable("https://sparql-anything.cc"));


		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue173-1.sparql")).toURI(), StandardCharsets.UTF_8);
		String queryStr2 = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue173-2.sparql")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(queryStr, ds).execSelect()));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(queryStr2, ds).execSelect()));
		assertTrue(QueryExecutionFactory.create(queryStr, ds).execSelect().hasNext());
		assertFalse(QueryExecutionFactory.create(queryStr2, ds).execSelect().hasNext());
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/194">...</a>
	 */
	@Test
	public void testIssue194() throws URISyntaxException, IOException {
		Assume.assumeTrue(HTTPHelper.checkHostIsReachable("https://raw.githubusercontent.com"));

		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue194.sparql")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(queryStr, ds).execSelect()));
		assertTrue(QueryExecutionFactory.create(queryStr, ds).execSelect().hasNext());
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/255">...</a>
	 */
	@Test
	public void testIssue255() throws URISyntaxException, IOException {
		String qs = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue255.sparql")).toURI(), StandardCharsets.UTF_8);
		qs = qs.replace("%%location%%", Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue255.json")).toURI().toString());
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Model r = QueryExecutionFactory.create(qs, ds).execConstruct();
		assertEquals(4L, r.size());
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/260">...</a>
	 */
	@Test
	public void testIssue260() throws URISyntaxException, IOException {
		String qs = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue260.sparql")).toURI(), StandardCharsets.UTF_8);
		qs = qs.replace("%%location%%", Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue260.csv")).toURI().toString());
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		ResultSet rs = QueryExecutionFactory.create(qs, ds).execSelect();
		int numberOfResults = 0;
		while (rs.hasNext()) {
			numberOfResults++;
			rs.next();
		}
		assertEquals(1, numberOfResults);
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/256">...</a>
	 */
	@Test
	public void testIssue256() throws URISyntaxException, IOException {
		String qs = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue256.sparql")).toURI(), StandardCharsets.UTF_8);
		Model expected = ModelFactory.createDefaultModel();
		RDFDataMgr.read(expected, Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue256.ttl")).toURI().toString());
		qs = qs.replace("%%location%%", Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue256.json")).toURI().toString());
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Model r = QueryExecutionFactory.create(qs, ds).execConstruct();
		assertTrue(expected.isIsomorphicWith(r));
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/264">...</a>
	 */
	@Test
	public void testIssue264() throws URISyntaxException {
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue264.txt")).toURI().toString();
		Query qs = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  SELECT * WHERE { SERVICE <x-sparql-anything:location=" + location + "> {fx:properties fx:txt.regex \"(.*)\" ; fx:txt.group 1 . ?s ?p ?o FILTER(ISLITERAL(?o))} }");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		ResultSet rs = QueryExecutionFactory.create(qs, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs));
		Set<String> results = new HashSet<>();
		while (rs.hasNext()) {
			results.add(rs.next().get("o").asLiteral().getValue().toString());
		}
//		System.out.println(results);
		assertTrue(results.contains("Hello world!"));
	}

	/**
	 * TODO See #241 - Currently returns results but ends with a SOE
	 */
	@Ignore
	@Test
	public void testIssue241() throws Exception {
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue241.sparql")).toURI(), StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query q = QueryFactory.create(queryStr);
		Op op = Algebra.compile(q);
		System.out.println(op);
//		System.out.println(Algebra.optimize(op));
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(queryStr, ds).execSelect()));
//		 assertTrue(QueryExecutionFactory.create(queryStr, ds).execSelect().hasNext());

//		Dataset ds2 = DatasetFactory.createGeneral();
//		ds2.executeWrite(()->{
//			RDFDataMgr.read(ds2, "https://www.w3.org/1999/02/22-rdf-syntax-ns#");
//		});
//		Query q2 = QueryFactory.create("SELECT * { SERVICE <https://data.europa.eu/sparql> { SERVICE<http://dbpedia.org/sparql>{?s ?p ?o BIND(BNODE() AS ?bob )} ?ss ?pp ?oo } } LIMIT 10");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(q2, ds2).execSelect()));

//		QueryExecutionHTTPBuilder.service("http://dbpedia.org/sparql").query("SELECT * {?s ?p ?o}")
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/280">...</a>
	 * <p>
	 */
	@Test
	public void testIssue280_2() throws URISyntaxException {
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue280.json")).toURI().toString();
		String TDBLocation = "target/dbIssue280/";
		File TDBfile = new File(TDBLocation);
		if (TDBfile.exists()) {
			boolean isTDBFileDeleted = TDBfile.delete();
			log.trace("Has TDB Folder been deleted? {}", isTDBFileDeleted);
		}
		boolean hasTDBFolderCreated = TDBfile.mkdirs();
		//log.trace("Has TDB Folder been created? {}, {}", hasTDBFolderCreated, TDBfile.exists());

		String queryString = "PREFIX fx: <http://sparql.xyz/facade-x/ns/>  PREFIX xyz: <http://sparql.xyz/facade-x/data/> SELECT * WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:location \""+location+"\" ; fx:ondisk \""+TDBLocation+"\" .  ?s xyz:name ?o }  }";
		//log.debug("TDB temp location: {}", TDBLocation);
		//log.debug("Query string\n{}", queryString);
		Query qs = QueryFactory.create(queryString);

//		System.out.println(location);
//		System.out.println(qs.toString(Syntax.defaultSyntax));
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs = QueryExecutionFactory.create(qs, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs));
		Set<String> results = new HashSet<>();
		while (rs.hasNext()) {
			results.add(rs.next().get("o").asLiteral().getValue().toString());
		}
//		System.out.println(results);
		assertTrue(results.contains("Friends"));
		assertTrue(results.contains("Cougar Town"));


		qs = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  PREFIX xyz: <http://sparql.xyz/facade-x/data/> SELECT * WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:location \"" + location + "\" ; fx:ondisk \"" + TDBLocation + "\" .  " + " ?s xyz:name ?o }  }");


		rs = QueryExecutionFactory.create(qs, ds).execSelect();
		results = new HashSet<>();
		while (rs.hasNext()) {
			results.add(rs.next().get("o").asLiteral().getValue().toString());
		}
		assertTrue(results.contains("Friends"));
		assertTrue(results.contains("Cougar Town"));
	}


	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/284">...</a>
	 * <p>
	 */
	@Test
	public void testIssue284() throws URISyntaxException {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.facadeiri", "ERROR");

		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue284.json")).toURI().toString();
		String locationExpected = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue284.ttl")).toURI().toString();

//		Query qs = QueryFactory.create(
//				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " +
//						"PREFIX xyz: <http://sparql.xyz/facade-x/data/> " +
//						"SELECT ?id WHERE { " +
//						"SERVICE <x-sparql-anything:location=" + location + "> { " +
//						" ?s xyz:students/fx:anySlot/xyz:ID ?id }  }");

//		System.out.println(location);
//		System.out.println(qs.toString(Syntax.defaultSyntax));
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

//		ResultSet rs = QueryExecutionFactory.create(qs, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs));


		Query qs = QueryFactory.create("PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n" + "PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n" + "base  <http://example.com/base/> \n" + "\n" + "CONSTRUCT\n" + "  {\n" + "    ?subject0 <http://example.com/id> ?0 .\n" + " }\n" + "WHERE\n" + "  {\n" + "    SERVICE <x-sparql-anything:location=" + location + ">\n" + "      {\n" + "      \t?s0   xyz:students/fx:anySlot ?iterator0 . \n" + "        ?iterator0  xyz:ID    ?0;\n" + "        bind(fx:entity(\"http://example.com/\", ?0) as ?subject0)\n" + "      }\n" + "  }");

//		System.out.println(qs.toString());

		Model m = QueryExecutionFactory.create(qs, ds).execConstruct();

		Model expected = ModelFactory.createDefaultModel();
		RDFDataMgr.read(expected, locationExpected);

		assertTrue(m.isIsomorphicWith(expected));

//		Set<String> results = new HashSet<>();
//		while (rs.hasNext()) {
//			results.add(rs.next().get("o").asLiteral().getValue().toString());
//		}
////		System.out.println(results);
//		assertTrue(results.contains("Friends"));
//		assertTrue(results.contains("Cougar Town"));
	}


	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/291">...</a>
	 * <p>
	 */
	@Test
	public void testIssue291() {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.facadeiri", "ERROR");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;

		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?slot ?p (fx:backward(?p, 3) AS ?backward3) (fx:backward(?p, 1) AS ?backward1) (fx:previous(?p) AS ?previous) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p ?slot  . FILTER(?p != rdf:type)} }");
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.setNsPrefix("rdfs", RDFS.uri);
//		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
//		m.write(System.out, "TTL");
		Set<String> backward3 = new HashSet<>();
		Set<String> backward1 = new HashSet<>();
		Set<String> previous = new HashSet<>();

//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			if (qs.contains("backward3")) {
				backward3.add(qs.getResource("backward3").getURI());
			}
			if (qs.contains("backward1")) {
				backward1.add(qs.getResource("backward1").getURI());
			}
			if (qs.contains("previous")) {
				previous.add(qs.getResource("previous").getURI());
			}
		}

//		System.out.println(backward3);
//		System.out.println(backward1);
//		System.out.println(previous);
		assertEquals(Sets.newHashSet(), backward3);
		assertEquals(Sets.newHashSet("http://www.w3.org/1999/02/22-rdf-syntax-ns#_1", "http://www.w3.org/1999/02/22-rdf-syntax-ns#_2"), backward1);
		assertEquals(Sets.newHashSet("http://www.w3.org/1999/02/22-rdf-syntax-ns#_1", "http://www.w3.org/1999/02/22-rdf-syntax-ns#_2"), previous);


	}


	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/295">...</a>
	 * <p>
	 */
	@Test
	public void testIssue295() throws URISyntaxException, IOException {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.facadeiri", "ERROR");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String TDBLocation = "tmp/testIssue295";
		File tmpTBDFolder = new File(TDBLocation);
		if(tmpTBDFolder.exists()){
			FileUtils.deleteDirectory(tmpTBDFolder);
		}
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue295.sparql")).toURI(), StandardCharsets.UTF_8);
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue295.json")).toURI().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", location);
		queryStr = queryStr.replace("%%%TDB_PATH%%%", TDBLocation);

		query = QueryFactory.create(queryStr);

		QueryExecution qExec = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec.execSelect();
		Set<String> expectedNames = Sets.newHashSet("Vincent", "Jules", "Beatrix");
		Set<String> actualNames = new HashSet<>();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			actualNames.add(qs.get("name").asLiteral().getValue().toString());
		}
		ds.end();
		assertEquals(expectedNames, actualNames);
		try {
			FileUtils.deleteDirectory(tmpTBDFolder);
		}catch(IOException e){
			log.warn("Unable to delete {}, delete it once the program terminates.",tmpTBDFolder.getAbsolutePath());
		}
	}


	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/334">...</a> <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/335">...</a>
	 * <p>
	 */
	@Test
	public void testIssue334() throws URISyntaxException, IOException {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue334.sparql")).toURI(), StandardCharsets.UTF_8);
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue334.tar")).toURI().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", location);

		query = QueryFactory.create(queryStr);

		QueryExecution qExec = QueryExecutionFactory.create(query, ds);
		assertFalse(qExec.execConstruct().isEmpty());


//		qExec.execConstruct().write(System.out, "TTL");
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/197">...</a>
	 * <p>
	 */
	@Test
	public void testIssue197() throws URISyntaxException, IOException {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.facadeiri", "ERROR");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue197.sparql")).toURI(), StandardCharsets.UTF_8);

		query = QueryFactory.create(queryStr);

		QueryExecution qExec = QueryExecutionFactory.create(query, ds);

//		System.out.println(ResultSetFormatter.asText(qExec.execSelect()));

		ResultSet rs = qExec.execSelect();
		Set<String> results = new HashSet<>();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
//			System.out.println(qs);
			if (qs.contains("material") && qs.get("material").isLiteral()) {
				results.add(qs.get("material").asLiteral().getValue().toString());

			}
		}
		assertTrue(results.contains(" Matita colorata"));
		assertTrue(results.contains(" Grafite"));


	}


	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/351">...</a>
	 */
	@Test
	public void testIssue351() throws URISyntaxException, IOException {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue351.sparql")).toURI(), StandardCharsets.UTF_8);
		String loc = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue351.xls")).toURI()).toUri().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", loc);

		query = QueryFactory.create(queryStr);

		QueryExecution qExec = QueryExecutionFactory.create(query, ds);

		Assert.assertTrue(qExec.execSelect().hasNext());

	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/352">...</a>
	 */
	@Test
	public void testIssue352() throws URISyntaxException, IOException {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;

		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue352-xls.sparql")).toURI(), StandardCharsets.UTF_8);
		String loc = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue352.xls")).toURI()).toUri().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", loc);

		query = QueryFactory.create(queryStr);

		QueryExecution qExec = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec.execSelect();

		assertTrue(rs.hasNext());
		QuerySolution qs = rs.next();
		Assert.assertEquals("fred", qs.getLiteral("fred").getString());
		Assert.assertEquals("sally", qs.getLiteral("sally").getString());


		queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue352-csv.sparql")).toURI(), StandardCharsets.UTF_8);
		loc = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue352.csv")).toURI()).toUri().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", loc);

		query = QueryFactory.create(queryStr);
		qExec = QueryExecutionFactory.create(query, ds);
 		rs = qExec.execSelect();

		assertTrue(rs.hasNext());
		qs = rs.next();
		Assert.assertEquals("fred", qs.getLiteral("fred").getString());
		Assert.assertEquals("sally", qs.getLiteral("sally").getString());
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/180">...</a>
	 */
	@Test
	public void testIssue180() throws URISyntaxException, IOException {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;

		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue180-xls.sparql")).toURI(), StandardCharsets.UTF_8);
		String loc = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue180.xls")).toURI()).toUri().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", loc);

		query = QueryFactory.create(queryStr);

		QueryExecution qExec = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec.execSelect();

		assertFalse(rs.hasNext());

		queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue180-csv.sparql")).toURI(), StandardCharsets.UTF_8);
		loc = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue180.csv")).toURI()).toUri().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", loc);

		query = QueryFactory.create(queryStr);
		qExec = QueryExecutionFactory.create(query, ds);
		rs = qExec.execSelect();

		assertFalse(rs.hasNext());
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/386">...</a>
	 */
	@Test
	public void testIssue386() throws URISyntaxException, IOException {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue386.sparql")).toURI(), StandardCharsets.UTF_8);
		query = QueryFactory.create(queryStr);
		QueryExecution qExec = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec.execSelect();
		assertTrue(rs.hasNext());
		assertEquals("http://example.org/document",rs.next().get("root").asResource().getURI());
		assertFalse(rs.hasNext());
	}

	/**
	 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/371">...</a>
	 */
	@Ignore
	@Test
	public void testIssue371() throws URISyntaxException, IOException {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything.engine.DatasetGraphCreator", "Trace");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue371.sparql")).toURI(), StandardCharsets.UTF_8);
		String loc = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue371.xls")).toURI()).toUri().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", loc);
//		System.out.println(queryStr);
		query = QueryFactory.create(queryStr);

		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
//		QueryExecution qExec2 = QueryExecutionFactory.create(query, ds);
		System.out.println(ResultSetFormatter.asText(qExec1.execSelect()));
		System.out.println(query.toString(Syntax.defaultSyntax));

//		Model m1 = qExec1.execConstruct();
//		m1.write(System.out, "TTL");
//		Model m2 = qExec2.execConstruct();
//		m2.write(System.out, "TTL");
//		Assert.assertFalse(m1.isIsomorphicWith(m2));

//		Assert.assertTrue(qExec.execSelect().hasNext());
	}


	@Test
	public void testIssue421() throws URISyntaxException, IOException {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue421.sparql")).toURI(), StandardCharsets.UTF_8);
		query = QueryFactory.create(queryStr);

//		System.out.println(Algebra.compile(query));
//		System.out.println(query.toString(Syntax.defaultSyntax));

		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
//		System.out.println(ResultSetFormatter.asText(qExec1.execSelect()));
		Set<String> result = new HashSet<>();
		ResultSet rs = qExec1.execSelect();
		while (rs.hasNext()){
			result.add(rs.next().get("a").asLiteral().toString());
		}
		assertEquals(Sets.newHashSet("abc", "cde"), result);

	}

	@Test
	public void testIssue330() throws URISyntaxException, IOException {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource("issues/issue330.sparql")).toURI(), StandardCharsets.UTF_8);
		query = QueryFactory.create(queryStr);

//		System.out.println(Algebra.compile(query));
//		System.out.println(query.toString(Syntax.defaultSyntax));

		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
//		System.out.println(ResultSetFormatter.asText(qExec1.execSelect()));
		Set<String> result = new HashSet<>();
		ResultSet rs = qExec1.execSelect();
		while (rs.hasNext()){
			result.add(rs.next().get("a").asLiteral().toString());
		}
		assertEquals(Sets.newHashSet("abc", "cde"), result);

//		Graph g = GraphFactory.createGraphMem();
//		g.add(NodeFactory.createBlankNode(), RDF.li(1).asNode(), NodeFactory.createLiteral("abc;cde"));
//		Query qq = QueryFactory.create("PREFIX apf: <http://jena.apache.org/ARQ/property#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?a { ?s rdf:_1 ?o . ?a apf:strSplit(?o \";\") }");
//		System.out.println(qq.toString(Syntax.syntaxSPARQL_11));
//		Op op = Algebra.compile(qq);
//		System.out.println(op);
//		OpBGP bgp = (OpBGP) Algebra.parse("(bgp\n" +
//				"    (triple ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?o)\n" +
//				"    (triple ?a <http://jena.apache.org/ARQ/property#strSplit> ??0)\n" +
//				"    (triple ??0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> ?o)\n" +
//				"    (triple ??0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> ??1)\n" +
//				"    (triple ??1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> \";\")\n" +
//				"    (triple ??1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>)\n" +
//				"  )");
//		System.out.println(TransformPropertyFunction.transform(bgp, ARQ.getContext()));
//		QueryIterator qi = Algebra.exec(op, g);
//		System.out.println(Utils.queryIteratorToString(qi));

	}


}
