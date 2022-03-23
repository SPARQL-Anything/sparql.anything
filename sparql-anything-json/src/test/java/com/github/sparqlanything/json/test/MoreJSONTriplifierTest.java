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

package com.github.sparqlanything.json.test;

import com.github.sparqlanything.json.JSONTriplifier;
import com.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class MoreJSONTriplifierTest extends AbstractTriplifierTester {
	static private Logger L = LoggerFactory.getLogger(MoreJSONTriplifierTest.class);
	public MoreJSONTriplifierTest() {
		super(new JSONTriplifier(), new Properties(), "json");
	}
	@Override
	protected void properties(Properties properties) {
		if(name.getMethodName().equals("testSliceArray$1")){
			properties.setProperty("blank-nodes", "false");
//			properties.setProperty("slice", "true");
		}else if(name.getMethodName().equals("testSliceArray$2")){
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
		}else if(name.getMethodName().equals("testSliceArray$3")){
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
			properties.setProperty("json.path", "$.*");
		}else if(name.getMethodName().equals("testSliceArray_2$1")){
			properties.setProperty("blank-nodes", "false");
//			properties.setProperty("slice", "true");
		}else if(name.getMethodName().equals("testSliceArray_2$2")){
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
		}else if(name.getMethodName().equals("testSliceArray_2$3")){
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
			properties.setProperty("json.path", "$.*");
		}else if(name.getMethodName().equals("testValueTypes_1$1")){
			properties.setProperty("blank-nodes", "false");
			//properties.setProperty("slice", "true");
		}else if(name.getMethodName().equals("testValueTypes_1$2")){
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
		}else if(name.getMethodName().equals("testValueTypes_1$3")){
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
			properties.setProperty("json.path", "$.*");
		}
	}

	@Test
	public void testSliceArray$1(){
		L.info("Test simple array (one go)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray$2(){
		L.info("Test simple array (slicing)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray$3(){
		L.info("Test simple array (slicing + JsonPath)");
		// RDFDataMgr.write(System.err, result, Lang.N3);
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray_2$1(){
		L.info("Test array of objects (one go)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray_2$2(){
		L.info("Test array of objects (slicing)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray_2$3(){
		L.info("Test array of objects (slicing + JsonPath)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testValueTypes_1$1(){
		L.info("Test json value types (one go)");
		// RDFDataMgr.write(System.err, result, Lang.N3);
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testValueTypes_1$2(){
		L.info("Test json value types (slicing)");
		//RDFDataMgr.write(System.err, result, Lang.N3);
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testValueTypes_1$3(){
		L.info("Test json value types (slicing + JsonPath)");
		//RDFDataMgr.write(System.err, result, Lang.N3);
		assertResultIsIsomorphicWithExpected();
	}
}
