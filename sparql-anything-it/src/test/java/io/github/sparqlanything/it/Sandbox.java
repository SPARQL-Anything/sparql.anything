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



package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.sparql.util.IsoMatcher;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public class Sandbox {

	private static void executeQuery(String queryStr) throws IOException, URISyntaxException {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory.create(queryStr);

		System.out.println(query.toString(Syntax.defaultSyntax));

		System.out.println("\n\n======\n\n");

		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
		if (query.isSelectType()) {
			ResultSet rs = qExec1.execSelect();
			Assert.assertTrue(rs.hasNext());
			System.out.println(ResultSetFormatter.asText(rs));
		} else {
			qExec1.execConstruct().write(System.out, "TTL");
		}

	}


	@Test
	public void generatePredicateLabels() throws IOException, URISyntaxException {
		executeQuery("PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>\n" +
			"PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>\n" +
			"PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"\n" +
			"CONSTRUCT \n" +
			"  { \n" +
			"    ?s ?p ?o .\n" +
			"  }\n" +
			"WHERE\n" +
			"  { SERVICE <x-sparql-anything:>\n" +
			"      { fx:properties\n" +
			"                  fx:content            \"<Element1 attr=\\\"value\\\"/> \" ;\n" +
			"                  fx:generate-predicate-labels  true ;\n" +
			"                  fx:media-type         \"application/xml\" .\n" +
			"        ?s        ?p                    ?o\n" +
			"      }\n" +
			"  }");
	}

	@Test
	public void loadReifiedDataset() {

		System.out.println("G1");
		String g1string = "<< <https://example.org/a> <https://example.org/b> <https://example.org/c> >> <https://example.org/p>  <https://example.org/o> ";
		ByteArrayInputStream bais = new ByteArrayInputStream(g1string.getBytes());
		Graph g1 = GraphFactory.createGraphMem();
		RDFDataMgr.read(g1, bais, Lang.TTL);
		RDFDataMgr.write(System.out, g1, RDFFormat.TTL);
		System.out.println("Size g1: "+ g1.size());

		String ns = "https://example.org/";
		System.out.println("G2");
		Graph g2 = GraphFactory.createGraphMem();
		Node r = NodeFactory.createTripleTerm(NodeFactory.createURI(ns + "a"), NodeFactory.createURI(ns + "b"), NodeFactory.createURI(ns + "c"));
		g2.add(r, NodeFactory.createURI(ns + "p"), NodeFactory.createURI(ns + "o"));
		RDFDataMgr.write(System.out, g2, RDFFormat.TTL);
		System.out.println("Size g2: "+ g2.size());

		System.out.println("Is g1 isomorphic with g2? " + g1.isIsomorphicWith(g2));
		System.out.println("Is g1 isomorphic with g2? " + IsoMatcher.isomorphic(g1, g2));



	}


	@Ignore
	@Test
	public void test() throws IOException {

		String queryStr = IOUtils.toString(new File("/Users/lgu/workspace/SPARQL-Anything/experiments/gtfs-stream/q").toURI(), Charset.defaultCharset());

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Dataset ds = DatasetFactory.createGeneral();
		Query query = QueryFactory.create(queryStr);
		System.out.println(Algebra.compile(query));
		QueryExecution qExec1 = QueryExecutionFactory.create(query, ds);
		ResultSet rs = qExec1.execSelect();
		System.out.println(ResultSetFormatter.asText(rs));


	}
}
