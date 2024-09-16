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

package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.engine.TriplifierRegister;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Test;

public class TestSilent {


	public ResultSet execute(String queryString) {

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Dataset kb = DatasetFactory.createGeneral();
		Query q = QueryFactory.create(queryString);
		return QueryExecutionFactory.create(q, kb).execSelect();
	}


	@Test
	public void test404Silent() {
		boolean raisesException = false;

		String q = "SELECT DISTINCT ?g ?s ?p ?o WHERE { SERVICE SILENT <x-sparql-anything:media-type=test-mime2,location=https://sparql.xyz/qewrqetqert> {GRAPH ?g {?s ?p ?o}}}";

		try {
			TriplifierRegister.getInstance().registerTriplifier("io.github.sparqlanything.engine.test.TestTriplifier2", new String[]{"test2"}, new String[]{"test-mime2"});
			execute(q);
		} catch (Exception e) {
			raisesException = true;
		}
		Assert.assertFalse(raisesException);
	}

	@Test
	public void test404NOSilent() {
		boolean raisesException = false;

		String q = "SELECT DISTINCT ?g ?s ?p ?o WHERE { SERVICE <x-sparql-anything:media-type=test-mime2,location=https://sparql.xyz/qewrqetqert> {GRAPH ?g {?s ?p ?o}}}";

		try {
			TriplifierRegister.getInstance().registerTriplifier("io.github.sparqlanything.engine.test.TestTriplifier2", new String[]{"test2"}, new String[]{"test-mime2"});
			execute(q);
		} catch (Exception e) {
			raisesException = true;
		}
		Assert.assertTrue(raisesException);
	}
}
