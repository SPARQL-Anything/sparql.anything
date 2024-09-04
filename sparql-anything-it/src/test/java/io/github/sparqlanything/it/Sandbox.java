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

import io.github.sparqlanything.engine.FacadeX;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;

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
}
