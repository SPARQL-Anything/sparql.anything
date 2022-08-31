/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.it;

import com.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class IssuesTest2 {

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

		Query qs = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  " +
						"PREFIX xyz: <http://sparql.xyz/facade-x/data/> " +
						"SELECT * WHERE { " +
						"SERVICE <x-sparql-anything:location=" + location + ",ondisk=/tmp> { " +
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
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  PREFIX xyz: <http://sparql.xyz/facade-x/data/> SELECT * WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:location \"" + location + "\" ; fx:ondisk \"/tmp\" .  " +
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


}
