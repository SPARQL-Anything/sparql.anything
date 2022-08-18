/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.parser.test;

import com.github.sparqlanything.facadeiri.FacadeIRIParser;
import com.github.sparqlanything.model.IRIArgument;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class FacadeIRIParserTest {

	@Test
	public void test1() {
		String uri = "x-sparql-anything:mimeType=application/json,location=file://myfile.json";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertTrue(((Properties) p).containsKey("mimeType"));
		Assert.assertTrue(((Properties) p).containsKey("location"));
	}

	@Test
	public void testArgsInLocation1() {
		String uri = "x-sparql-anything:mimeType=application/json,location=http://myfile.json?foo=bar";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertTrue(((Properties) p).containsKey("location"));
		Assert.assertFalse(((Properties) p).containsKey("foo"));
		Assert.assertEquals(p.get("location"), "http://myfile.json?foo=bar");
	}

	@Test
	public void testArgsInLocation2() {
		String uri = "x-sparql-anything:mimeType=application/json,location=http://myfile.json?foo=bar&tab=goal";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("foo"));
		Assert.assertFalse(((Properties) p).containsKey("tab"));
		Assert.assertEquals(p.get("location"), "http://myfile.json?foo=bar&tab=goal");
	}

	@Test
	public void testArgsInLocation3() {
		String uri = "x-sparql-anything:mimeType=application/json,location=http://myfile.json?foo=bar&tab=goal,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertEquals(p.get("same"), "other");
		Assert.assertEquals(p.get("location"), "http://myfile.json?foo=bar&tab=goal");
	}

	@Test
	public void testArgsInLocation4() {
		String uri = "x-sparql-anything:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("foo"));
	}

	@Test
	public void testArgsInLocation5() {
		String uri = "x-sparql-anything:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("tab"));
	}

	@Test
	public void testArgsInLocation6() {
		String uri = "x-sparql-anything:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertEquals(p.get("same"), "other");
	}

	@Test
	public void testArgsInLocation7() {
		String uri = "x-sparql-anything:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertTrue(p.size() == 3);
	}

	@Test
	public void testArgsInLocation8() {
		String uri = "x-sparql-anything:mimeType=application/json,location=https://myfile.json?fo\\,o=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertTrue(p.size() == 3);
		Assert.assertEquals(p.get("location"), "https://myfile.json?fo,o=bar&tab=goal#hack");
	}

	@Test
	public void specialCharsArgs() {
		String uri = "x-sparql-anything:mimeType=application/json,location=https://myfile.json?fo\\,o=bar&tab=goal#hack,same=汉字";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertEquals(p.get("location"), "https://myfile.json?fo,o=bar&tab=goal#hack");
		Assert.assertEquals(p.get("same"), "汉字");
	}

	@Test
	public void specialCharsArgsFromFile() throws IOException {
		String uri = IOUtils
				.toString(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("./exampleIRI.txt")));
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertEquals(p.get("location"), "https://myfile.json?fo,o=bar&tab=goal#hack");
		Assert.assertEquals(p.get("same"), "汉字");
	}

	@Test
	public void testLocation() {
		String uri = "x-sparql-anything:example.json";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertEquals("example.json", p.get(IRIArgument.LOCATION.toString()));
	}
}
