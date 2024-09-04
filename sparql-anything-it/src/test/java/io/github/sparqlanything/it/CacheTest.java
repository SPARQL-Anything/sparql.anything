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

import com.sun.source.tree.AssertTree;
import io.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Test;

public class CacheTest {

	@Test
	public void testDefaultSetting(){
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		String queryStr = "SELECT ?o { SERVICE <x-sparql-anything:content=abc,txt.split=b,audit=true,strategy=0> {GRAPH <http://sparql.xyz/facade-x/data/audit> { ?s  <http://sparql.xyz/facade-x/ns/cachedGraph>  ?o} }}";
		Query query = QueryFactory.create(queryStr);
		// System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query,ds).execSelect()));
		ResultSet rs1 = QueryExecutionFactory.create(query,ds).execSelect();
		Assert.assertTrue(rs1.hasNext());
		Assert.assertFalse(rs1.next().getLiteral("o").asLiteral().getBoolean());

		ResultSet rs2 = QueryExecutionFactory.create(query,ds).execSelect();
		Assert.assertTrue(rs2.hasNext());
		Assert.assertTrue(rs2.next().getLiteral("o").asLiteral().getBoolean());


	}


	@Test
	public void testNoCache(){
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		String queryStr = "SELECT ?o { SERVICE <x-sparql-anything:content=abc,txt.split=b,audit=true,strategy=0,no-cache=true> {GRAPH <http://sparql.xyz/facade-x/data/audit> { ?s  <http://sparql.xyz/facade-x/ns/cachedGraph>  ?o} }}";
		Query query = QueryFactory.create(queryStr);
		// System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query,ds).execSelect()));
		ResultSet rs1 = QueryExecutionFactory.create(query,ds).execSelect();
		Assert.assertTrue(rs1.hasNext());
		Assert.assertFalse(rs1.next().getLiteral("o").asLiteral().getBoolean());

		ResultSet rs2 = QueryExecutionFactory.create(query,ds).execSelect();
		Assert.assertTrue(rs2.hasNext());
		Assert.assertFalse(rs2.next().getLiteral("o").asLiteral().getBoolean());


	}
}
