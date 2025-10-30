/*
 * Copyright (c) 2025 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

/*
 */

package io.github.sparqlanything.json.test;

import io.github.sparqlanything.json.JSONTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class JSONNullValuesTest extends AbstractTriplifierTester {

	public JSONNullValuesTest() {
		super(new JSONTriplifier(), new Properties(), "json");
	}

	@Override
	protected void properties(Properties properties) {

		if (name.getMethodName().equals("testNullValues")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty(JSONTriplifier.PROPERTY_JSONINCLUDENULLVALUES.toString(), "true");
		}

		if (name.getMethodName().equals("testNullValues_2")) {
			properties.setProperty("blank-nodes", "false");
			properties.setProperty(JSONTriplifier.PROPERTY_JSONINCLUDENULLVALUES.toString(), "true");
		}
	}

	@Test
	public void testNullValues(){
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testNullValues_2(){
		assertResultIsIsomorphicWithExpected();
	}
}
