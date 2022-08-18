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

package com.github.sparqlanything.yaml;

import com.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class YAMLTriplifierTest extends AbstractTriplifierTester {

	public YAMLTriplifierTest() {
		super(new YAMLTriplifier(), new Properties(), "yaml");
	}

	@Override
	protected void properties(Properties properties) {
		if(name.getMethodName().equals("testFoo")){
			properties.setProperty("blank-nodes", "true");
		}
	}

	@Test
	public void testMap(){
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testFoo(){
		assertResultIsIsomorphicWithExpected();
	}
}
