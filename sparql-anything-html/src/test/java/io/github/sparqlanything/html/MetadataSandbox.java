/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.html;

import io.github.sparqlanything.html.org.apache.any23.Any23;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorRegistry;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorRegistryImpl;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.MicrodataExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.RDFa11ExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.RDFaExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.source.DocumentSource;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandlerException;
import io.github.sparqlanything.html.org.apache.any23.writer.TurtleWriter;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;


public class MetadataSandbox {

	@Test
	public void metadataTest() throws URISyntaxException, IOException {
			Any23 runner = new Any23();
			runner.setHTTPUserAgent("test-user-agent");
			DocumentSource source = runner.createDocumentSource("file:///Users/lgu/workspace/SPARQLAnything/sparql.anything/sparql-anything-html/src/test/resources/Microdata1.html");

			try (TripleHandler handler = new TurtleWriter(System.out)) {
				runner.extract(source, handler);
			} catch (TripleHandlerException | ExtractionException e) {
				throw new RuntimeException(e);
			}

	}
}
