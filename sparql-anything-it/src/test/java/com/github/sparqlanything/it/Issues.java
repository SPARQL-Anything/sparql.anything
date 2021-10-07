/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.sparqlanything.it;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sparqlanything.engine.FacadeX;

public class Issues {

	private static final Logger log = LoggerFactory.getLogger(Issues.class);

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
			QuerySolution querySolution = (QuerySolution) rs1.next();
//			System.out.println(querySolution);
			list1.add(querySolution.getLiteral("a").getValue().toString());
		}

		ResultSet rs2 = QueryExecutionFactory.create(query2, ds).execSelect();
//		System.out.println(ResultSetFormatter.asText(rs2));
		List<String> list2 = new ArrayList<>();
		while (rs2.hasNext()) {
			QuerySolution querySolution = (QuerySolution) rs2.next();
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
			QuerySolution querySolution = (QuerySolution) rs1.next();
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
			QuerySolution querySolution = (QuerySolution) rs2.next();
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
			QuerySolution querySolution = (QuerySolution) rs.next();
			if (querySolution.get("slot").isLiteral()) {
				slots.add(querySolution.get("slot").asLiteral().getValue().toString());
			}
		}

		assertEquals(Sets.newHashSet("b", "d", "c"), slots);
	}

}
