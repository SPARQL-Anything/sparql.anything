/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.xml;

import com.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class MoreXMLTriplifierTest extends AbstractTriplifierTester {
	private final Logger L = LoggerFactory.getLogger(MoreXMLTriplifierTest.class);
	public MoreXMLTriplifierTest() {
		super(new XMLTriplifier(), new Properties(), "xml", "ttl");
	}

	@Override
	protected void properties(Properties properties) {
		if(name.getMethodName().equals("testSimple$1")){
			properties.put("blank-nodes", "false");
		}else
		if(name.getMethodName().equals("testBooks$1")){
			properties.put("blank-nodes", "false");
		}else
		if(name.getMethodName().equals("testBooks_1$1")){
			properties.put("blank-nodes", "false");
			properties.put("xml.path", "//book");
		}else
		if(name.getMethodName().equals("testSliceBooks$1")){
			properties.put("blank-nodes", "false");
			properties.put("slice", "true");
			properties.put("xml.path", "//book");
		}else if(name.getMethodName().equals("testBooks_2$1")){
			properties.put("blank-nodes", "false");
			properties.put("xml.path", "//book");
		}
	}

	@Test
	public void testSimple$1(){
		L.info("Test XML (one go)");
		//RDFDataMgr.write(System.err, result, Lang.TTL);
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testBooks$1(){
		L.info("Test XML books (one go)");
//		RDFDataMgr.write(System.err, result, Lang.TTL);
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testBooks_1$1(){
		L.info("Test XML books (XPath //book, one go)");
		//RDFDataMgr.write(System.err, result, Lang.TTL);
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testBooks_2$1(){
		L.info("Test XML books (XPath //book, one go) -- different input but same output as _1");
		//RDFDataMgr.write(System.err, result, Lang.TTL);
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceBooks$1(){
		L.info("Test XML books (XPath //book, with slicing)");
		//RDFDataMgr.write(System.err, result, Lang.TTL);
		assertResultIsIsomorphicWithExpected();
	}
}
