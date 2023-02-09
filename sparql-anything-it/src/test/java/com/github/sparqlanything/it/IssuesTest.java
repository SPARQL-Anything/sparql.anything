/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.it;

import com.github.sparqlanything.engine.FacadeX;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.tdb.TDB;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class IssuesTest {

	private static final Logger log = LoggerFactory.getLogger(IssuesTest.class);

	@Test
	public void issue75() throws URISyntaxException {
		String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
		log.debug("Location {}", location);
		Query query1 = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  {      "
						+ "SERVICE <x-sparql-anything:> { "
						+ " fx:properties fx:csv.headers true . fx:properties fx:location \"" + location + "\" . "
						+ " ?s rdf:_1 ?o . ?o xyz:A ?a }}");

		Query query2 = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  {      "
						+ "SERVICE <x-sparql-anything:> { " + "" + " fx:properties fx:location \"" + location + "\";"
						+ " fx:csv.headers true " + "." + " ?s rdf:_1 ?o . ?o xyz:A ?a }}");

		Dataset ds = DatasetFactory.createGeneral();

		System.out.println(query1.toString(Syntax.syntaxSPARQL_11));
		System.out.println(query2.toString(Syntax.syntaxSPARQL_11));

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
		String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
		log.debug("Location {}", location);

		Query query1 = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  { "
						+ "SERVICE <x-sparql-anything:csv.headers=true> { " + ""
						+ " fx:properties fx:location ?location ; " + " fx:csv.null-string \"\"" + "."
						+ " ?s rdf:_1 ?o . ?o xyz:A ?r  VALUES (?location) {(\"" + location + "\")} }}");

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

		Query query2 = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " + "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT *  { "
						+ "SERVICE <x-sparql-anything:csv.headers=true> { " + "" + " BIND (\"" + location
						+ "\" AS ?location ) fx:properties fx:location ?location ; " + " fx:csv.null-string \"\"" + "."
						+ " ?s rdf:_1 ?o . ?o xyz:A ?r  }}");

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

	// Root container cannot be object
	// Refers to #93
	@Test
	public void testIssue93() throws URISyntaxException, IOException {
		String qs = IOUtils.toString(getClass().getClassLoader().getResource("issue93.sparql"), StandardCharsets.UTF_8);
		qs = qs.replace("%%location%%", getClass().getClassLoader().getResource("issue93.html").toURI().toString());
		Query query = QueryFactory.create(qs);
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Dataset ds = DatasetFactory.createGeneral();
		Boolean rootInObject = QueryExecutionFactory.create(query, ds).execAsk();
		Assert.assertFalse(rootInObject);
	}

	@Test
	public void testIssue114() throws IOException, URISyntaxException {
		String location = getClass().getClassLoader().getResource("propertypath.json").toURI().toString();
		Query query = QueryFactory.create("PREFIX fx: <http://sparql.xyz/facade-x/ns/>  "
				+ "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT DISTINCT ?slot  {      "
				+ "SERVICE <x-sparql-anything:> { fx:properties fx:location \"" + location + "\" .   "
				+ "?s fx:anySlot/fx:anySlot ?slot . }}");

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
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/154
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue154() throws URISyntaxException, IOException {
		String queryStr = IOUtils.toString(getClass().getClassLoader().getResource("issues/e.sparql").toURI(),
				StandardCharsets.UTF_8);
		String location = getClass().getClassLoader().getResource("issues/a00002-1036.xml").toURI().toString();
		Query query = QueryFactory.create(queryStr.replace("%%LOCATION%%", location));
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		// XXX This process never ends!
		Model rs = QueryExecutionFactory.create(query, ds).execConstruct();
		rs.write(System.err, "TTL");
	}

	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/154
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void test2Issue154() throws URISyntaxException, IOException {
		String location = getClass().getClassLoader().getResource("issues/issue154.xml").toURI().toString();

		String queryStr = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue154.sparql").toURI(),
				StandardCharsets.UTF_8);

		Query query = QueryFactory.create(queryStr.replace("%%LOCATION%%", location));

		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Set<Set<String>> expectedResult = new HashSet<>();

		expectedResult.addAll(Sets.newHashSet(Sets.newHashSet("Randall, Cynthia", "Lover Birds"),
				Sets.newHashSet("Thurman, Paula", "Splish Splash"), Sets.newHashSet("Corets, Eva", "Oberon's Legacy"),
				Sets.newHashSet("Corets, Eva", "The Sundered Grail"), Sets.newHashSet("Ralls, Kim", "Midnight Rain"),
				Sets.newHashSet("Corets, Eva", "Maeve Ascendant"),
				Sets.newHashSet("Gambardella, Matthew", "XML Developer's Guide")));

		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		Set<Set<String>> actualResult = new HashSet<>();
		ResultSet rs = QueryExecutionFactory.create(query, ds).execSelect();
		while (rs.hasNext()) {
			QuerySolution querySolution = rs.next();
			actualResult.add(Sets.newHashSet(querySolution.getLiteral("authorString").getValue().toString(),
					querySolution.getLiteral("titleString").getValue().toString()));
		}

//		System.out.println(actualResult);

		assertEquals(expectedResult, actualResult);

	}

	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/175
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue175() throws URISyntaxException, IOException {
		String queryStr = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue175.sparql").toURI(),
				StandardCharsets.UTF_8);
		String queryStr2 = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue175-2.sparql").toURI(),
				StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		assertFalse(QueryExecutionFactory.create(query, ds).execSelect().next());
		assertFalse(QueryExecutionFactory.create(queryStr, ds).execSelect().hasNext());
		assertTrue(QueryExecutionFactory.create(queryStr2, ds).execSelect().hasNext());
	}

	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/173
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue173() throws URISyntaxException, IOException {
		String queryStr = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue173-1.sparql").toURI(),
				StandardCharsets.UTF_8);
		String queryStr2 = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue173-2.sparql").toURI(),
				StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(queryStr, ds).execSelect()));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(queryStr2, ds).execSelect()));
		assertTrue(QueryExecutionFactory.create(queryStr, ds).execSelect().hasNext());
		assertFalse(QueryExecutionFactory.create(queryStr2, ds).execSelect().hasNext());
	}

	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/194
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue194() throws URISyntaxException, IOException {
		String queryStr = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue194.sparql").toURI(),
				StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(queryStr, ds).execSelect()));
		assertTrue(QueryExecutionFactory.create(queryStr, ds).execSelect().hasNext());
	}

	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/255
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue255() throws URISyntaxException, IOException {
		String qs = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue255.sparql").toURI(),
				StandardCharsets.UTF_8);
		qs = qs.replace("%%location%%",
				getClass().getClassLoader().getResource("issues/issue255.json").toURI().toString());
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Model r = QueryExecutionFactory.create(qs, ds).execConstruct();
		assertEquals(4L, r.size());
	}

	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/260
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue260() throws URISyntaxException, IOException {
		String qs = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue260.sparql").toURI(),
				StandardCharsets.UTF_8);
		qs = qs.replace("%%location%%",
				getClass().getClassLoader().getResource("issues/issue260.csv").toURI().toString());
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
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/256
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue256() throws URISyntaxException, IOException {
		String qs = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue256.sparql").toURI(),
				StandardCharsets.UTF_8);
		Model expected = ModelFactory.createDefaultModel();
		RDFDataMgr.read(expected, getClass().getClassLoader().getResource("issues/issue256.ttl").toURI().toString());
		qs = qs.replace("%%location%%",
				getClass().getClassLoader().getResource("issues/issue256.json").toURI().toString());
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Model r = QueryExecutionFactory.create(qs, ds).execConstruct();
		assertTrue(expected.isIsomorphicWith(r));
	}

	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/264
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue264() throws URISyntaxException, IOException {
		String location = getClass().getClassLoader().getResource("issues/issue264.txt").toURI().toString();
		Query qs = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  SELECT * WHERE { SERVICE <x-sparql-anything:location=" + location + "> {fx:properties fx:txt.regex \"(.*)\" ; fx:txt.group 1 . ?s ?p ?o FILTER(ISLITERAL(?o))} }");
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
		String queryStr = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue241.sparql").toURI(),
				StandardCharsets.UTF_8);
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(queryStr, ds).execSelect()));
		// assertTrue(QueryExecutionFactory.create(queryStr,
		// ds).execSelect().hasNext());
	}

	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/280
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue280() throws URISyntaxException, IOException {
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.facadeiri", "ERROR");

		String location = getClass().getClassLoader().getResource("issues/issue280.json").toURI().toString();
		File TDBfile = new File("target/tdbIssue280/" );
		if(TDBfile.exists()){
			TDBfile.delete();
		}
		TDBfile.mkdirs();
		String TDBLocation = TDBfile.getAbsolutePath().toString();
		log.debug("TDB temp location: {}", TDBLocation);
		Query qs = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " +
						"PREFIX xyz: <http://sparql.xyz/facade-x/data/> " +
						"SELECT * WHERE { " +
						"SERVICE <x-sparql-anything:location=" + location + ",ondisk=" + TDBLocation + "> { " +
						" ?s xyz:name ?o }  }");

//		System.out.println(location);
		System.out.println(qs.toString(Syntax.defaultSyntax));
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
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/284
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue284() throws URISyntaxException, IOException {
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.facadeiri", "ERROR");

		String location = getClass().getClassLoader().getResource("issues/issue284.json").toURI().toString();
		String locationExpected = getClass().getClassLoader().getResource("issues/issue284.ttl").toURI().toString();

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


	/**
			* See https://github.com/SPARQL-Anything/sparql.anything/issues/291
			*
			* @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue291() throws URISyntaxException, IOException {
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.facadeiri", "ERROR");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		Model m;

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
		while (rs.hasNext()){
			QuerySolution qs = rs.next();
			if(qs.contains("backward3")){
				backward3.add(qs.getResource("backward3").getURI());
			}
			if(qs.contains("backward1")){
				backward1.add(qs.getResource("backward1").getURI());
			}
			if(qs.contains("previous")){
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
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/292
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testIssue295() throws URISyntaxException, IOException {
		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.facadeiri", "ERROR");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		File tmpTBDFolder = new File(getClass().getClassLoader().getResource(".").getPath(), "testIssue295");
		String queryStr = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue295.sparql").toURI(),
				StandardCharsets.UTF_8);
		String location = getClass().getClassLoader().getResource("issues/issue295.json").toURI().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", location);
		queryStr = queryStr.replace("%%%TDB_PATH%%%", tmpTBDFolder.getAbsolutePath());

		query = QueryFactory.create(queryStr);

		QueryExecution qExec = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec.execSelect();
		Set<String> expectedNames = Sets.newHashSet("Vincent", "Jules", "Beatrix");
		Set<String> actualNames = new HashSet<>();
		while(rs.hasNext()){
			QuerySolution qs = rs.next();
			actualNames.add(qs.get("name").asLiteral().getValue().toString());
		}
		FileUtils.deleteDirectory(tmpTBDFolder);
		ds.end();
		assertEquals(expectedNames,actualNames);
	}


	/**
	 * See https://github.com/SPARQL-Anything/sparql.anything/issues/292
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testIssue334() throws URISyntaxException, IOException {
		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything", "Trace");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.model.HTTPHelper", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.TriplifierRegister", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.engine.FacadeX", "ERROR");
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything.facadeiri", "ERROR");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		String queryStr = IOUtils.toString(getClass().getClassLoader().getResource("issues/issue334.sparql").toURI(),
				StandardCharsets.UTF_8);
		String location = getClass().getClassLoader().getResource("issues/issue334.tar").toURI().toString();
		queryStr = queryStr.replace("%%%LOCATION%%%", location);

		query = QueryFactory.create(queryStr);

		QueryExecution qExec = QueryExecutionFactory.create(query, ds);
		assertTrue(!qExec.execConstruct().isEmpty());


//		qExec.execConstruct().write(System.out, "TTL");
	}



}
