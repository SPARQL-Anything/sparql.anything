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



package io.github.sparqlanything.cli;

import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.Assert;
import org.junit.Test;

public class Issue526Test {
	String rq = getClass().getClassLoader().getResource("./issue526.rq").getFile();
	String rq_1 = getClass().getClassLoader().getResource("./issue526-1.rq").getFile();
	String rq_2 = getClass().getClassLoader().getResource("./issue526-2.rq").getFile();
	String csv = getClass().getClassLoader().getResource("./issue526.csv").getFile();
	String rq_3 = getClass().getClassLoader().getResource("./issue526-3.rq").getFile();

	private void graphNotEmpty(String s) {
		Dataset dataset = RDFParser.fromString(s, Lang.TURTLE).toDataset();
		Assert.assertFalse(dataset.isEmpty());
	}

	private void csvNotEmpty(String s) {
		String[] lines = s.split("\r\n|\r|\n");
		Assert.assertNotEquals(1, lines.length);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void inline() throws Exception {
		csvNotEmpty(SPARQLAnything.callMain(new String[]{
			"-q",
			"SELECT * WHERE { ?x ?y ?z } LIMIT 10",
			"-c",
			"location=" + csv
		}));
	}

	@Test
	public void inlineWithEmptyService() throws Exception {
		csvNotEmpty(SPARQLAnything.callMain(new String[]{
			"-q",
			"SELECT * WHERE { SERVICE <x-sparql-anything:> {?x ?y ?z } } LIMIT 10",
			"-c",
			"location=" + csv
		}));
	}

	@Test
	public void rq() throws Exception {
		graphNotEmpty(SPARQLAnything.callMain(new String[]{
			"-q",
			rq,
			"-c",
			"location=" + csv
		}));
	}

	@Test
	public void rq_1() throws Exception {
		csvNotEmpty(SPARQLAnything.callMain(new String[]{
			"-q",
			rq_1,
			"-c",
			"location=" + csv
		}));
	}

	@Test
	public void rq_2() throws Exception {
		csvNotEmpty(SPARQLAnything.callMain(new String[]{
			"-q",
			rq_2,
			"-c",
			"location=" + csv
		}));
	}


	@Test
	public void rq_3() throws Exception {
		String r = SPARQLAnything.callMain(new String[]{
			"-q",
			rq_3,
			"-c",
			"csv.headers=false",
			"-c",
			"location=" + csv
		});
		Assert.assertTrue(r.contains("true"));
	}
}
