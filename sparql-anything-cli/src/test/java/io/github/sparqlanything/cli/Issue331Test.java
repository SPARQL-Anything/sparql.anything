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
import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class Issue331Test {

	@Test
	public void test() throws Exception {
		JenaSystem.init();
		InputStream fakeIn = new ByteArrayInputStream("abc".getBytes());
		System.setIn(fakeIn);

		String dir = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getFile();
		String f = dir.concat("out.ttl");
		String f2 = dir.concat("out-1.ttl");
		System.out.println(f);
		File fileOut = new File(f);
		File fileOut2 = new File(f2);

		if(fileOut.exists()){
			fileOut.delete();
		}

		String q = "CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:> { ?s ?p ?o } }";
		SPARQLAnything.callMain(new String[]{"-q", q,"-f", "ttl", "-o", f, "-c", IRIArgument.READ_FROM_STD_IN.toString().concat("=true")});

		Assert.assertTrue(fileOut.exists());
		Assert.assertFalse(fileOut2.exists());
	}
}
