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

package com.github.sparqlanything.model.test;

import com.github.sparqlanything.model.Triplifier;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TriplifierGetPropertyValuesTest {

	@Test
	public void test(){
		Map<String,String> testSet = new HashMap<String,String>();
		testSet.put("key.name", "Value no index");
		testSet.put("key.name.0", "Value 0");
		testSet.put("key.name.1", "Value 1");
		testSet.put("key.name.2", "Value 2");
		Properties p = new Properties();
		for(Map.Entry en: testSet.entrySet()){
			p.put(en.getKey(), en.getValue());
		}

		List<String> values = Triplifier.getPropertyValues(p, "key.name");
		for(Map.Entry en: testSet.entrySet()){
			Assert.assertTrue(values.contains(en.getValue()));
		}

		Assert.assertTrue(values.size() == 4);
	}

	@Test
	public void test1(){
		Map<String,String> testSet = new HashMap<String,String>();
		testSet.put("key.name", "Value no index");
		testSet.put("key.name.1", "Value 0");
		testSet.put("key.name.2", "Value 1");
		testSet.put("key.name.3", "Value 2");
		Properties p = new Properties();
		for(Map.Entry en: testSet.entrySet()){
			p.put(en.getKey(), en.getValue());
		}

		List<String> values = Triplifier.getPropertyValues(p, "key.name");
		for(Map.Entry en: testSet.entrySet()){
			Assert.assertTrue(values.contains(en.getValue()));
		}

		Assert.assertTrue(values.size() == 4);
	}

	@Test
	public void test2(){
		Map<String,String> testSet = new HashMap<String,String>();
//		testSet.put("key.name", "Value no index");
		testSet.put("key.name.1", "Value 0");
		testSet.put("key.name.2", "Value 1");
		testSet.put("key.name.3", "Value 2");
		Properties p = new Properties();
		for(Map.Entry en: testSet.entrySet()){
			p.put(en.getKey(), en.getValue());
		}

		List<String> values = Triplifier.getPropertyValues(p, "key.name");
		for(Map.Entry en: testSet.entrySet()){
			Assert.assertTrue(values.contains(en.getValue()));
		}

		Assert.assertTrue(values.size() == 3);
	}
}
