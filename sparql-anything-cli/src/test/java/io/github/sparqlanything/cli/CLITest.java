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

package io.github.sparqlanything.cli;

import io.github.sparqlanything.model.HTTPHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CLITest {

	@Test
	public void inlineQuery() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String q = "SELECT * {  ?s ?p ?o OPTIONAL {?s a ?c} }";
		query(f, q);
	}

	private static void query(String f, String q) throws Exception {
		query(new String[]{"-q", q, "-c", "location=" + f, "-f", "CSV"});
	}

	private static String query(String[] args) throws Exception {
		String out = SPARQLAnything.callMain(args);
		CSVParser parser = new CSVParser(new StringReader(out), CSVFormat.DEFAULT);
		Set<String> actualSet = new HashSet<>();
		for (CSVRecord record : parser) {
			actualSet.add(record.get(3));
		}
		Set<String> expectedSet = new HashSet<>();
		expectedSet.add("c");
		expectedSet.add("http://sparql.xyz/facade-x/ns/root");
		expectedSet.add("http://sparql.xyz/facade-x/data/catalog");
		expectedSet.add("http://sparql.xyz/facade-x/data/book");
		expectedSet.add("http://sparql.xyz/facade-x/data/author");
		expectedSet.add("http://sparql.xyz/facade-x/data/price");
		expectedSet.add("http://sparql.xyz/facade-x/data/title");
		expectedSet.add("http://sparql.xyz/facade-x/data/genre");
		expectedSet.add("http://sparql.xyz/facade-x/data/publish_date");
		Assert.assertEquals(expectedSet, actualSet);
		return out;
	}

	@Test
	public void infileQueryWithValues() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String queryFile = Objects.requireNonNull(getClass().getClassLoader().getResource("CLITestOnFileQuery1.sparql")).toURI().toString();
		query(new String[]{"-q", queryFile, "-v", "loc=" + f, "-f", "CSV"});
	}

	@Test
	public void infileQueryWithValuesAndPath() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String queryFile = Objects.requireNonNull(getClass().getClassLoader().getResource("CLITestOnFileQuery1.sparql")).getFile();
		System.out.println(queryFile);
		query(new String[]{"-q", queryFile, "-v", "loc=" + f, "-f", "CSV"});
	}

	@Test
	public void infileQuery() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String queryFile = Objects.requireNonNull(getClass().getClassLoader().getResource("CLITestOnFileQuery.sparql")).toURI().toString();
		query(f, queryFile);
	}

	@Test
	public void remoteQuery() throws Exception {
		Assume.assumeTrue(HTTPHelper.checkHostIsReachable("https://sparql-anything.cc"));
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String queryFile = "https://sparql-anything.cc/examples/CLITestOnFileQuery.sparql";
		query(f, queryFile);
	}

	@Test
	public void testRemoteLoad() throws Exception {
		Assume.assumeTrue(HTTPHelper.checkHostIsReachable("https://sparql-anything.cc"));
		String q = Objects.requireNonNull(getClass().getClassLoader().getResource("count-triples.sparql")).toString();
		String l = "https://sparql-anything.cc/examples/example.ttl";
		String out = SPARQLAnything.callMain(new String[]{
				"-q", q, "-l", l
		});
		Assert.assertTrue(out.contains("10"));
	}
}
