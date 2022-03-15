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
import org.junit.Test;

import java.util.Properties;

public class MoreJSONTriplifierTest extends AbstractTriplifierTester {
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
		}
	}
	@Test
	public void testSliceArray$1(){
		assertResultIsIsomorphicWithExpected();
	}
	@Test
	public void testSliceArray$2(){
		assertResultIsIsomorphicWithExpected();
	}
}
