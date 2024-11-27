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

import com.google.common.collect.Sets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sys.JenaSystem;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class OptionsViaCommandLineTest {

	public OptionsViaCommandLineTest (){
		JenaSystem.init();
	}



	@Test
	public void noServiceModeWithOptional() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String q = "SELECT * {  ?s ?p ?o OPTIONAL {?s a ?c} } ";
//		System.out.println(Algebra.compile(QueryFactory.create(q)));
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "location="+f});
//		System.out.println(out);
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


	}

	@Ignore
	@Test
	public void test() throws Exception {
		String q = "SELECT ?v { SERVICE <x-sparql-anything:> { ?root a <http://sparql.xyz/facade-x/ns/root> ;  <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?v } }";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "content=abc"});
		Assert.assertTrue(out.contains("abc"));
	}

	@Ignore
	@Test
	public void testOverride() throws Exception {
		String q = "SELECT ?v { SERVICE <x-sparql-anything:content=cde> { ?root a <http://sparql.xyz/facade-x/ns/root> ;  <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?v } }";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "content=abc"});
		Assert.assertTrue(out.contains("cde"));
	}

	@Test
	public void testWithoutService() throws Exception {
		String q = "SELECT ?v {  ?root a <http://sparql.xyz/facade-x/ns/root> ;  <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?v } ";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "content=abc"});
		Assert.assertTrue(out.contains("abc"));
	}

	@Ignore
	@Test
	public void testOverrideConfigurationWithBGP() throws Exception {
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?v {  ?root a fx:root ;   <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?v  . fx:properties fx:content \"cde\" } ";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "content=abc"});
		System.out.println(out);
		Assert.assertTrue(out.contains("cde"));
	}

	@Test
	public void testWithMagicProperties() throws Exception {
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?v {  ?root a fx:root ;   fx:anySlot ?v  . } ";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "content=abc"});
//		System.out.println(out);
		Assert.assertTrue(out.contains("abc"));
	}
}
