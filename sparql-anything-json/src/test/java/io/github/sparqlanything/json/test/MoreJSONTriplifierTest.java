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

package io.github.sparqlanything.json.test;

import io.github.sparqlanything.json.JSONTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class MoreJSONTriplifierTest extends AbstractTriplifierTester {

	static private final Logger logger = LoggerFactory.getLogger(MoreJSONTriplifierTest.class);

	public MoreJSONTriplifierTest() {
		super(new JSONTriplifier(), new Properties(), "json");
	}

	@Override
	protected void properties(Properties properties) {

		// SliceArray
		if (name.getMethodName().equals("testSliceArray$1")) {
			properties.setProperty("blank-nodes", "false");
		} else if (name.getMethodName().equals("testSliceArray$2")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
		} else if (name.getMethodName().equals("testSliceArray$3")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
			properties.setProperty("json.path", "$.*");
		} else if (name.getMethodName().equals("testSliceArray$4")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("json.path", "$.*");
		}

		// SliceArray_2
		if (name.getMethodName().equals("testSliceArray_2$1")) {
			properties.setProperty("blank-nodes", "false");
		} else if (name.getMethodName().equals("testSliceArray_2$2")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
		} else if (name.getMethodName().equals("testSliceArray_2$3")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
			properties.setProperty("json.path", "$.*");
		} else if (name.getMethodName().equals("testSliceArray_2$4")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("json.path", "$.*");
		}

		// ValueTypes
		if (name.getMethodName().equals("testValueTypes_1$1")) {
			properties.setProperty("blank-nodes", "false");
			//properties.setProperty("slice", "true");
		} else if (name.getMethodName().equals("testValueTypes_1$2")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
		} else if (name.getMethodName().equals("testValueTypes_1$3")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
			properties.setProperty("json.path", "$.*");
		}

		// Object
		if (name.getMethodName().equals("testObject$1")) {
			properties.setProperty("blank-nodes", "false");
//			properties.setProperty("slice", "true");
		} else if (name.getMethodName().equals("testObject$2")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true"); // --> Throws an exception!!!
			throwsException = true;
		} else if (name.getMethodName().equals("testObject$3")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
			properties.setProperty("json.path", "$");
		}

		// SliceObject
		if (name.getMethodName().equals("testSliceObject$3")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty("slice", "true");
			properties.setProperty("json.path", "$.content");
		}

		// testMultiJsonPath$1
		if (name.getMethodName().equals("testMultiJsonPath$1")) {
			properties.setProperty("blank-nodes", "false");
			// properties.setProperty("slice", "true");
			properties.setProperty("json.path.1", "$..[?(@.letter == 'A')]");
			properties.setProperty("json.path.2", "$..[?(@.number == 2)]");
		}
	}

	@Test
	public void testSliceArray$1() {
		logger.debug("Test simple array (one go)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray$2() {
		logger.debug("Test simple array (slicing)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray$3() {
		logger.debug("Test simple array (slicing + JsonPath)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray$4() {
		logger.debug("Test simple array (one go + JsonPath)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray_2$1() {
		logger.debug("Test array of objects (one go)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray_2$2() {
		logger.debug("Test array of objects (slicing)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray_2$3() {
		logger.debug("Test array of objects (slicing + JsonPath)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testSliceArray_2$4() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testValueTypes_1$1() {
		logger.debug("Test json value types (one go)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testValueTypes_1$2() {
		logger.debug("Test json value types (slicing)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testValueTypes_1$3() {
		logger.debug("Test json value types (slicing + JsonPath)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testValueTypes_1$4() {
		logger.debug("Test json value types (one go + JsonPath)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testObject$1() {
		logger.debug("Test simple Json object (one go)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testObject$2() {
		logger.debug("Test simple Json object (slicing) --> Should throw an exception");
		Assert.assertEquals("Not a JSON array", resultException.getMessage());
	}


	@Test
	public void testSliceObject$3() {
		logger.debug("Test simple Json object (slicing + JsonPath)");
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testMultiJsonPath$1() {
		logger.debug("Test multiple json paths (one go + JsonPath)");
		Assert.assertEquals(13, result.size());
	}
}
