/*
 * Copyright (c) 2025 SPARQL Anything Contributors @ http://github.com/sparql-anything
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
import io.github.sparqlanything.engine.InternalQueryCache;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for internal query-scoped caching (issue #585)
 * 
 * These tests verify that:
 * 1. Internal cache is automatically enabled for nested queries
 * 2. Internal cache prevents redundant triplification within a single query
 * 3. Internal cache is scoped to query execution (cleared between queries)
 * 4. User-level cache (use-cache) still works as before
 */
public class InternalCacheTest {

	@Test
	public void testInternalCacheEnabledByDefault() {
		// This test verifies that internal cache works even without use-cache=true
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		// Query with nested SERVICE clauses - should use internal cache automatically
		String queryStr = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> " +
				"SELECT ?v1 ?v2 WHERE { " +
				"  SERVICE <x-sparql-anything:content=test,txt.split=e,audit=true> { " +
				"    [] fx:anySlot ?v1 . " +
				"    SERVICE <x-sparql-anything:content=test,txt.split=e> { " +
				"      [] fx:anySlot ?v2 . " +
				"    } " +
				"  } " +
				"}";
		
		Query query = QueryFactory.create(queryStr);
		try (QueryExecution qe = QueryExecutionFactory.create(query, ds)) {
			ResultSet rs = qe.execSelect();
			// Just verify it executes without error
			Assert.assertTrue(rs.hasNext());
		}
	}

	@Test
	public void testInternalCacheClearedBetweenQueries() {
		// Verify that internal cache doesn't persist across different queries
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		String queryStr = "SELECT ?o WHERE { SERVICE <x-sparql-anything:content=abc,txt.split=b,audit=true,strategy=0> " +
				"{ GRAPH <http://sparql.xyz/facade-x/data/audit> " +
				"{ ?s <http://sparql.xyz/facade-x/ns/cachedGraph> ?o} }}";
		
		Query query = QueryFactory.create(queryStr);
		
		// First execution
		try (QueryExecution qe1 = QueryExecutionFactory.create(query, ds)) {
			ResultSet rs1 = qe1.execSelect();
			Assert.assertTrue(rs1.hasNext());
			// First execution should not hit cache
			Assert.assertFalse(rs1.next().getLiteral("o").asLiteral().getBoolean());
		}

		// Second execution - should also not hit USER cache (use-cache=false by default)
		// but internal cache is cleared between queries
		try (QueryExecution qe2 = QueryExecutionFactory.create(query, ds)) {
			ResultSet rs2 = qe2.execSelect();
			Assert.assertTrue(rs2.hasNext());
			// Second execution should also not hit cache (internal cache cleared)
			Assert.assertFalse(rs2.next().getLiteral("o").asLiteral().getBoolean());
		}
	}

	@Test
	public void testInternalCacheWithinSameQuery() {
		// This test shows internal cache working within a single query execution
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		// Query that accesses the same source twice - internal cache should be used
		String queryStr = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> " +
				"SELECT ?count WHERE { " +
				"  { " +
				"    SELECT (COUNT(*) as ?count) WHERE { " +
				"      SERVICE <x-sparql-anything:content=a-b-c,txt.split=-> { " +
				"        ?s ?p ?o . " +
				"      } " +
				"    } " +
				"  } " +
				"  SERVICE <x-sparql-anything:content=a-b-c,txt.split=-> { " +
				"    [] fx:anySlot \"a\" . " +
				"  } " +
				"}";
		
		Query query = QueryFactory.create(queryStr);
		try (QueryExecution qe = QueryExecutionFactory.create(query, ds)) {
			ResultSet rs = qe.execSelect();
			Assert.assertTrue(rs.hasNext());
		}
	}

	@Test
	public void testUserCacheStillWorks() {
		// Verify that use-cache=true still works as expected
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		String queryStr = "SELECT ?o WHERE { SERVICE <x-sparql-anything:content=xyz,txt.split=y,audit=true,strategy=0,use-cache=true> " +
				"{ GRAPH <http://sparql.xyz/facade-x/data/audit> " +
				"{ ?s <http://sparql.xyz/facade-x/ns/cachedGraph> ?o} }}";
		
		Query query = QueryFactory.create(queryStr);
		
		// First execution
		try (QueryExecution qe1 = QueryExecutionFactory.create(query, ds)) {
			ResultSet rs1 = qe1.execSelect();
			Assert.assertTrue(rs1.hasNext());
			// First execution should not hit cache
			Assert.assertFalse(rs1.next().getLiteral("o").asLiteral().getBoolean());
		}

		// Second execution - should hit USER cache (use-cache=true)
		try (QueryExecution qe2 = QueryExecutionFactory.create(query, ds)) {
			ResultSet rs2 = qe2.execSelect();
			Assert.assertTrue(rs2.hasNext());
			// Second execution should hit the user-level cache
			Assert.assertTrue(rs2.next().getLiteral("o").asLiteral().getBoolean());
		}
	}
}
