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
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Test;

public class CacheTest {

	@Test
	public void test1(){
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		String queryStr = "SELECT * { SERVICE <x-sparql-anything:content=abc,txt.split=b,audit=true> {GRAPH ?g { ?s ?p ?o} }}";
		Query query = QueryFactory.create(queryStr);
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query,ds).execSelect()));

		System.err.println("--- new exec ---");
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query,ds).execSelect()));

	}
}
