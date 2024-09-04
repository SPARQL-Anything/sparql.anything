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

import org.apache.jena.sys.JenaSystem;
import org.junit.Assert;
import org.junit.Test;

public class OptionsViaCommandLineTest {

	public OptionsViaCommandLineTest (){
		JenaSystem.init();
	}

	@Test
	public void test() throws Exception {
		String q = "SELECT ?v { SERVICE <x-sparql-anything:> { ?root a <http://sparql.xyz/facade-x/ns/root> ;  <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?v } }";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "content=abc"});
		Assert.assertTrue(out.contains("abc"));
	}

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

	@Test
	public void testOverrideConfigurationWithBGP() throws Exception {
		String q = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?v {  ?root a fx:root ;   <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?v  . fx:properties fx:content \"cde\" } ";
//		System.out.println(Algebra.compile(QueryFactory.create(q)));
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "content=abc"});
//		System.out.println(out);
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
