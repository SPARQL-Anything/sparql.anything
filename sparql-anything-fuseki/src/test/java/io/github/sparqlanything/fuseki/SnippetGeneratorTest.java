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

import io.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.ARQ;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
/**
 * Unit tests for SnippetGenerator.
 */
public class SnippetGeneratorTest {

	@Test
	public void testGenerateAllSnippets() throws Exception {
		// Initialize Facade-X to register functions
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		SnippetGenerator generator = new SnippetGenerator();
		List<SnippetSection> snippets = generator.generateAllSnippets();

		// Should generate snippets for formats, functions, and magic properties
		assertNotNull(snippets);
		assertFalse(snippets.isEmpty());

		// Verify at least one format snippet exists
		boolean hasFormatSnippet = snippets.stream()
				.anyMatch(s -> "FILE FORMATS".equals(s.getGroup()));
		assertTrue(hasFormatSnippet);

		// Verify at least one function snippet exists
		boolean hasFunctionSnippet = snippets.stream()
				.anyMatch(s -> "FUNCTIONS".equals(s.getGroup()));
		assertTrue(hasFunctionSnippet);

		// Verify at least one magic property snippet exists
		boolean hasMagicPropertySnippet = snippets.stream()
				.anyMatch(s -> "MAGIC PROPERTIES".equals(s.getGroup()));
		assertTrue(hasMagicPropertySnippet);
	}

	@Test
	public void testSnippetEscaping() {
		String label = "Test \"Quote\" and 'Single'";
		String code = "Line 1\nLine 2\tTab\\Backslash";
		String group = "TEST";

		SnippetSection snippet = new SnippetSection(label, code, group);

		String escapedLabel = snippet.getEscapedLabel();
		String escapedCode = snippet.getEscapedCode();

		// Should escape quotes
		assertTrue("Should escape double quotes in label", escapedLabel.contains("\\\""));

		// Should escape newlines and tabs
		assertTrue("Should escape newlines in code", escapedCode.contains("\\n"));
		assertTrue("Should escape tabs in code", escapedCode.contains("\\t"));
		assertTrue("Should escape backslashes in code", escapedCode.contains("\\\\"));

		// Should not contain literal newlines
		assertFalse("Should not contain literal newline", escapedCode.contains("\n"));
	}

	@Test
	public void testGenerateFormatSnippets() throws Exception {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		SnippetGenerator generator = new SnippetGenerator();
		List<SnippetSection> snippets = generator.generateFormatSnippets();

		assertNotNull(snippets);
		assertFalse(snippets.isEmpty());

		// All should be in FILE FORMATS group
		for (SnippetSection snippet : snippets) {
			assertEquals("FILE FORMATS", snippet.getGroup());
			assertNotNull(snippet.getLabel());
			assertNotNull(snippet.getCode());
			assertFalse(snippet.getCode().trim().isEmpty());
		}
	}

	@Test
	public void testGenerateFunctionSnippets() throws Exception {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		SnippetGenerator generator = new SnippetGenerator();
		List<SnippetSection> snippets = generator.generateFunctionSnippets();

		assertNotNull(snippets);

		// Check for specific annotated functions
		boolean hasCardinal = snippets.stream()
				.anyMatch(s -> s.getLabel().toLowerCase().contains("cardinal"));
		boolean hasSerial = snippets.stream()
				.anyMatch(s -> s.getLabel().toLowerCase().contains("serial"));

		assertTrue(hasCardinal);
		assertTrue(hasSerial);
	}

	@Test
	public void testGenerateMagicPropertySnippets() throws Exception {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		SnippetGenerator generator = new SnippetGenerator();
		List<SnippetSection> snippets = generator.generateMagicPropertySnippets();

		assertNotNull(snippets);

		// Check for AnySlot magic property
		boolean hasAnySlot = snippets.stream()
				.anyMatch(s -> s.getLabel().toLowerCase().contains("anyslot"));

		assertTrue(hasAnySlot);

		// All should be in MAGIC PROPERTIES group
		for (SnippetSection snippet : snippets) {
			assertEquals("MAGIC PROPERTIES", snippet.getGroup());
		}
	}

	@Test
	public void testReflectionFunctionsIncluded() throws Exception {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		SnippetGenerator generator = new SnippetGenerator();
		List<SnippetSection> snippets = generator.generateFunctionSnippets();

		assertNotNull(snippets);

		// Check for reflection-based String functions
		boolean hasStringTrim = snippets.stream()
				.anyMatch(s -> s.getLabel().contains("String.trim"));
		boolean hasStringSubstring = snippets.stream()
				.anyMatch(s -> s.getLabel().contains("String.substring"));

		assertTrue(hasStringTrim);
		assertTrue(hasStringSubstring);

		// Check for reflection-based hash functions
		boolean hasMd5 = snippets.stream()
				.anyMatch(s -> s.getLabel().contains("DigestUtils.md5Hex"));
		boolean hasSha256 = snippets.stream()
				.anyMatch(s -> s.getLabel().contains("DigestUtils.sha256Hex"));

		assertTrue(hasMd5);
		assertTrue(hasSha256);

		// Check for reflection-based distance functions
		boolean hasLevenshtein = snippets.stream()
				.anyMatch(s -> s.getLabel().contains("LevenshteinDistance"));

		assertTrue(hasLevenshtein);

		// Verify snippets have proper groups
		boolean hasStringGroup = snippets.stream()
				.anyMatch(s -> "STRING FUNCTIONS".equals(s.getGroup()));
		boolean hasHashGroup = snippets.stream()
				.anyMatch(s -> "HASH FUNCTIONS".equals(s.getGroup()));
		boolean hasSimilarityGroup = snippets.stream()
				.anyMatch(s -> "SIMILARITY FUNCTIONS".equals(s.getGroup()));

		assertTrue(hasStringGroup);
		assertTrue(hasHashGroup);
		assertTrue(hasSimilarityGroup);
	}
}
