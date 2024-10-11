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

import io.github.sparqlanything.model.IRIArgument;
import org.apache.jena.sys.JenaSystem;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StandardInTest {

	@Test
	public void test() throws Exception {
		JenaSystem.init();
		InputStream fakeIn = new ByteArrayInputStream("abc".getBytes());
		System.setIn(fakeIn);

		String q = "SELECT ?v { ?root a <http://sparql.xyz/facade-x/ns/root> ;  <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> ?v } ";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", IRIArgument.READ_FROM_STD_IN.toString().concat("=true")});
		Assert.assertEquals("v\r\nabc\r\n",out);
	}
}
