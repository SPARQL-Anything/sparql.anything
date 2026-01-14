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

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for documenting functions created via ReflectionFunctionFactory.
 * These functions cannot be annotated directly since they're created dynamically.
 */
public class ReflectionFunctionDocRegistry {

	private static final Map<String, FunctionDocEntry> REGISTRY = new HashMap<>();

	static {
		// String manipulation functions
		register("String.trim", "STRING FUNCTIONS",
				"Remove leading and trailing whitespace from a string",
				"BIND(fx:String.trim(\"  hello world  \") AS ?result)\n# Result: \"hello world\"");

		register("String.substring", "STRING FUNCTIONS",
				"Extract a substring from a string",
				"BIND(fx:String.substring(\"hello world\", 0, 5) AS ?result)\n# Result: \"hello\"");

		register("String.indexOf", "STRING FUNCTIONS",
				"Find the first occurrence of a substring",
				"BIND(fx:String.indexOf(\"hello world\", \"world\") AS ?index)\n# Result: 6");

		register("String.startsWith", "STRING FUNCTIONS",
				"Check if string starts with a prefix",
				"FILTER(fx:String.startsWith(?value, \"http://\"))");

		register("String.endsWith", "STRING FUNCTIONS",
				"Check if string ends with a suffix",
				"FILTER(fx:String.endsWith(?filename, \".json\"))");

		register("String.replace", "STRING FUNCTIONS",
				"Replace all occurrences of a substring",
				"BIND(fx:String.replace(?text, \"old\", \"new\") AS ?result)");

		register("String.strip", "STRING FUNCTIONS",
				"Remove leading and trailing whitespace (Unicode-aware)",
				"BIND(fx:String.strip(?value) AS ?cleaned)");

		register("String.stripLeading", "STRING FUNCTIONS",
				"Remove leading whitespace",
				"BIND(fx:String.stripLeading(\"  hello\") AS ?result)\n# Result: \"hello\"");

		register("String.stripTrailing", "STRING FUNCTIONS",
				"Remove trailing whitespace",
				"BIND(fx:String.stripTrailing(\"hello  \") AS ?result)\n# Result: \"hello\"");

		register("String.lastIndexOf", "STRING FUNCTIONS",
				"Find the last occurrence of a substring",
				"BIND(fx:String.lastIndexOf(\"hello world world\", \"world\") AS ?index)\n# Result: 12");

		register("String.toLowerCase", "STRING FUNCTIONS",
				"Convert string to lowercase",
				"BIND(fx:String.toLowerCase(\"HELLO\") AS ?result)\n# Result: \"hello\"");

		register("String.toUpperCase", "STRING FUNCTIONS",
				"Convert string to uppercase",
				"BIND(fx:String.toUpperCase(\"hello\") AS ?result)\n# Result: \"HELLO\"");

		// Cryptographic hash functions
		register("DigestUtils.md2Hex", "HASH FUNCTIONS",
				"Calculate MD2 hash (hex string)",
				"BIND(fx:DigestUtils.md2Hex(\"hello\") AS ?hash)");

		register("DigestUtils.md5Hex", "HASH FUNCTIONS",
				"Calculate MD5 hash (hex string)",
				"BIND(fx:DigestUtils.md5Hex(?content) AS ?hash)");

		register("DigestUtils.sha1Hex", "HASH FUNCTIONS",
				"Calculate SHA-1 hash (hex string)",
				"BIND(fx:DigestUtils.sha1Hex(?content) AS ?hash)");

		register("DigestUtils.sha256Hex", "HASH FUNCTIONS",
				"Calculate SHA-256 hash (hex string)",
				"BIND(fx:DigestUtils.sha256Hex(?content) AS ?hash)");

		register("DigestUtils.sha384Hex", "HASH FUNCTIONS",
				"Calculate SHA-384 hash (hex string)",
				"BIND(fx:DigestUtils.sha384Hex(?content) AS ?hash)");

		register("DigestUtils.sha512Hex", "HASH FUNCTIONS",
				"Calculate SHA-512 hash (hex string)",
				"BIND(fx:DigestUtils.sha512Hex(?content) AS ?hash)");

		// Text processing functions
		register("WordUtils.capitalize", "TEXT PROCESSING",
				"Capitalize first letter of each word",
				"BIND(fx:WordUtils.capitalize(\"hello world\") AS ?result)\n# Result: \"Hello World\"");

		register("WordUtils.capitalizeFully", "TEXT PROCESSING",
				"Capitalize first letter of each word, lowercase the rest",
				"BIND(fx:WordUtils.capitalizeFully(\"hELLo WoRLD\") AS ?result)\n# Result: \"Hello World\"");

		register("WordUtils.initials", "TEXT PROCESSING",
				"Extract initials from a string",
				"BIND(fx:WordUtils.initials(\"John Fitzgerald Kennedy\") AS ?initials)\n# Result: \"JFK\"");

		register("WordUtils.swapCase", "TEXT PROCESSING",
				"Swap uppercase and lowercase characters",
				"BIND(fx:WordUtils.swapCase(\"Hello World\") AS ?result)\n# Result: \"hELLO wORLD\"");

		register("WordUtils.uncapitalize", "TEXT PROCESSING",
				"Uncapitalize first letter of each word",
				"BIND(fx:WordUtils.uncapitalize(\"Hello World\") AS ?result)\n# Result: \"hello world\"");

		// URL encoding/decoding
		register("URLEncoder.encode", "URL FUNCTIONS",
				"URL-encode a string with specified charset",
				"BIND(fx:URLEncoder.encode(\"hello world\", \"UTF-8\") AS ?encoded)\n# Result: \"hello+world\"");

		register("URLDecoder.decode", "URL FUNCTIONS",
				"URL-decode a string with specified charset",
				"BIND(fx:URLDecoder.decode(\"hello+world\", \"UTF-8\") AS ?decoded)\n# Result: \"hello world\"");

		// String distance/similarity functions
		register("LevenshteinDistance", "SIMILARITY FUNCTIONS",
				"Calculate Levenshtein edit distance between two strings",
				"BIND(fx:LevenshteinDistance(\"kitten\", \"sitting\") AS ?distance)\n# Result: 3.0");

		register("JaccardDistance", "SIMILARITY FUNCTIONS",
				"Calculate Jaccard distance between two strings",
				"BIND(fx:JaccardDistance(?str1, ?str2) AS ?distance)");

		register("JaroWinklerDistance", "SIMILARITY FUNCTIONS",
				"Calculate Jaro-Winkler distance between two strings",
				"BIND(fx:JaroWinklerDistance(?str1, ?str2) AS ?distance)");

		register("LongestCommonSubsequenceDistance", "SIMILARITY FUNCTIONS",
				"Calculate longest common subsequence distance",
				"BIND(fx:LongestCommonSubsequenceDistance(?str1, ?str2) AS ?distance)");

		register("HammingDistance", "SIMILARITY FUNCTIONS",
				"Calculate Hamming distance (for equal-length strings)",
				"BIND(fx:HammingDistance(\"karolin\", \"kathrin\") AS ?distance)\n# Result: 3.0");

		register("QGramDistance", "SIMILARITY FUNCTIONS",
				"Calculate Q-gram distance between two strings",
				"BIND(fx:QGramDistance(?str1, ?str2) AS ?distance)");

		register("CosineDistance", "SIMILARITY FUNCTIONS",
				"Calculate cosine distance between two strings",
				"BIND(fx:CosineDistance(?str1, ?str2) AS ?distance)");
	}

	/**
	 * Register a function with its documentation.
	 */
	private static void register(String functionName, String group, String description, String example) {
		REGISTRY.put(functionName, new FunctionDocEntry(functionName, group, description, example));
	}

	/**
	 * Get documentation for a function by its name (without the fx: prefix).
	 */
	public static FunctionDocEntry getDocumentation(String functionName) {
		return REGISTRY.get(functionName);
	}

	/**
	 * Check if a function is registered.
	 */
	public static boolean isRegistered(String functionName) {
		return REGISTRY.containsKey(functionName);
	}

	/**
	 * Get all registered function names.
	 */
	public static Iterable<String> getAllFunctionNames() {
		return REGISTRY.keySet();
	}

	/**
	 * Documentation entry for a reflection-based function.
	 */
	public static class FunctionDocEntry {
		private final String functionName;
		private final String group;
		private final String description;
		private final String example;

		public FunctionDocEntry(String functionName, String group, String description, String example) {
			this.functionName = functionName;
			this.group = group;
			this.description = description;
			this.example = example;
		}

		public String getFunctionName() {
			return functionName;
		}

		public String getGroup() {
			return group;
		}

		public String getDescription() {
			return description;
		}

		public String getExample() {
			return example;
		}

		public String getLabel() {
			return "fx:" + functionName;
		}
	}
}
