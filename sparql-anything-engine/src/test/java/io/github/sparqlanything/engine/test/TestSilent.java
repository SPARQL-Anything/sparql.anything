/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Test;

public class TestSilent {

	public ResultSet execute(String queryString) {

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Dataset kb = DatasetFactory.createGeneral();
		Query q = QueryFactory.create(queryString);
		ResultSet result = QueryExecutionFactory.create(q, kb).execSelect();
		return result;
	}

	@Test
	public void testSilent() {
		boolean failed = false;
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/>\n" +
				"select * where {" +
				"service silent <x-sparql-anything:> {" +
				"fx:properties fx:location \"http://www.example.org2562456294865\";" +
				"	fx:media-type \"application/json\"." +
				" ?s ?p ?o" +
				"}" +
				"}";
		try {
			execute(q);
		} catch (Exception e) {
			failed = true;
		}
		Assert.assertFalse(failed);
	}

	@Test
	public void testNoSilent() {
		boolean failed = true;
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/>\n" +
				"select * where {" +
				"service <x-sparql-anything:> {" +
				"fx:properties fx:location \"http://www.example.org2562456294865\";" +
				"	fx:media-type \"application/json\"." +
				" ?s ?p ?o" +
				"}" +
				"}";
		try {
			execute(q);
		} catch (Exception e) {
			failed = false;
		}
		Assert.assertFalse(failed);
	}

	@Test
	public void test404NoSilent() {
		boolean raisesException = false;
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/>\n" +
				"select * where {" +
				"service <x-sparql-anything:> {" +
				"fx:properties fx:location \"https://sparql.xyz/qewrqetqert\";" +
				"	fx:media-type \"application/json\"." +
				" ?s ?p ?o" +
				"}" +
				"}";
		try {
			execute(q);
		} catch (Exception e) {
			//e.printStackTrace();
			raisesException = true;
		}
		Assert.assertTrue(raisesException);
	}

	@Test
	public void test404Silent() {
		boolean raisesException = false;
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/>\n" +
				"select * where {" +
				"service silent <x-sparql-anything:> {" +
				"fx:properties fx:location \"https://sparql.xyz/qewrqetqert\";" +
				"	fx:media-type \"application/json\"." +
				" ?s ?p ?o" +
				"}" +
				"}";
		try {
			execute(q);
		} catch (Exception e) {
			raisesException = true;
		}
		Assert.assertFalse(raisesException);
	}
}
