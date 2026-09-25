/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
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
package io.github.sparqlanything.csv;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultDelimiterTest {

	private static Properties props(String... kv) {
		Properties p = new Properties();
		for (int i = 0; i < kv.length; i += 2) p.setProperty(kv[i], kv[i + 1]);
		return p;
	}

	@Test
	public void extensions() {
		assertTrue(CSVTriplifier.isTabSeparated(props("location", "file:///data/x.tsv")));
		assertTrue(CSVTriplifier.isTabSeparated(props("location", "file:///data/x.TAB")));
		assertTrue(CSVTriplifier.isTabSeparated(props("location", "https://example.org/x.tsv?raw=true")));
		assertFalse(CSVTriplifier.isTabSeparated(props("location", "file:///data/x.csv")));
	}

	@Test
	public void mediaType() {
		assertTrue(CSVTriplifier.isTabSeparated(props("media-type", "text/tab-separated-values; charset=utf-8")));
		assertFalse(CSVTriplifier.isTabSeparated(props("media-type", "text/csv", "location", "file:///data/x.tsv")));
	}

	@Test
	public void precedence() throws Exception {
		Properties p = props("location", "file:///data/x.tsv");
		assertTrue(String.valueOf(CSVTriplifier.buildFormat(p).getDelimiter()).equals("\t"));
		p.setProperty("csv.delimiter", ";");
		assertTrue(String.valueOf(CSVTriplifier.buildFormat(p).getDelimiter()).equals(";"));
		Properties f = props("location", "file:///data/x.csv", "csv.format", "TDF");
		assertTrue(String.valueOf(CSVTriplifier.buildFormat(f).getDelimiter()).equals("\t"));
		assertTrue(String.valueOf(CSVTriplifier.buildFormat(props("location", "file:///data/x.csv")).getDelimiter()).equals(","));
	}
}
