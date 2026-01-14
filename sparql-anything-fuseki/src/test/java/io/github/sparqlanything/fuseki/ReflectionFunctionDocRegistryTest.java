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

package io.github.sparqlanything.fuseki;

import org.junit.Assert;
import org.junit.Test;

public class ReflectionFunctionDocRegistryTest {

	@Test
	public void testStringFunctionsRegistered() {
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("String.trim"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("String.substring"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("String.indexOf"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("String.toLowerCase"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("String.toUpperCase"));
	}

	@Test
	public void testHashFunctionsRegistered() {
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("DigestUtils.md5Hex"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("DigestUtils.sha256Hex"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("DigestUtils.sha512Hex"));
	}

	@Test
	public void testWordUtilsFunctionsRegistered() {
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("WordUtils.capitalize"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("WordUtils.capitalizeFully"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("WordUtils.initials"));
	}

	@Test
	public void testURLFunctionsRegistered() {
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("URLEncoder.encode"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("URLDecoder.decode"));
	}

	@Test
	public void testDistanceFunctionsRegistered() {
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("LevenshteinDistance"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("JaccardDistance"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("HammingDistance"));
		Assert.assertTrue(ReflectionFunctionDocRegistry.isRegistered("CosineDistance"));
	}

	@Test
	public void testGetDocumentation() {
		ReflectionFunctionDocRegistry.FunctionDocEntry entry = 
			ReflectionFunctionDocRegistry.getDocumentation("String.trim");
		
		Assert.assertNotNull(entry);
		Assert.assertEquals("String.trim", entry.getFunctionName());
		Assert.assertEquals("STRING FUNCTIONS", entry.getGroup());
		Assert.assertEquals("fx:String.trim", entry.getLabel());
		Assert.assertNotNull(entry.getDescription());
		Assert.assertNotNull(entry.getExample());
	}

	@Test
	public void testUnregisteredFunction() {
		Assert.assertFalse(ReflectionFunctionDocRegistry.isRegistered("NonExistent.function"));
		Assert.assertNull(ReflectionFunctionDocRegistry.getDocumentation("NonExistent.function"));
	}

	@Test
	public void testAllFunctionsHaveDocumentation() {
		for (String functionName : ReflectionFunctionDocRegistry.getAllFunctionNames()) {
			ReflectionFunctionDocRegistry.FunctionDocEntry entry = 
				ReflectionFunctionDocRegistry.getDocumentation(functionName);
			
			Assert.assertNotNull("Function " + functionName + " should have documentation", entry);
			Assert.assertNotNull("Function " + functionName + " should have description", entry.getDescription());
			Assert.assertNotNull("Function " + functionName + " should have example", entry.getExample());
			Assert.assertNotNull("Function " + functionName + " should have group", entry.getGroup());
			Assert.assertFalse("Function " + functionName + " description should not be empty", 
				entry.getDescription().isEmpty());
			Assert.assertFalse("Function " + functionName + " example should not be empty", 
				entry.getExample().isEmpty());
		}
	}
}
