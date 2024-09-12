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

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.RdfaParser;
import io.github.sparqlanything.html.org.semarglproject.sink.TripleSink;
import io.github.sparqlanything.html.org.semarglproject.source.StreamProcessor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RDFaSandbox {
	private static final Logger logger = LoggerFactory.getLogger(TestRDFaParser.class);
	@Test
	public void RDFa() throws ParseException {

		TripleSink ts = new TripleSink() {
			@Override
			public void addNonLiteral(String subj, String pred, String obj) {
				logger.trace("add non literal {} {} {}", subj, pred, obj);
			}

			@Override
			public void addPlainLiteral(String subj, String pred, String content, String lang) {
				logger.trace("addPlainLiteral {} {} {} {}", subj, pred, content, lang);
			}

			@Override
			public void addTypedLiteral(String subj, String pred, String content, String type) {
				logger.trace("addTypedLiteral {} {} {} {}", subj, pred, content, type);
			}

			@Override
			public void setBaseUri(String baseUri) {
				logger.trace("setBaseURI {}", baseUri);

			}

			@Override
			public void startStream() throws ParseException {
				logger.trace("start stream");
			}

			@Override
			public void endStream() throws ParseException {
				logger.trace("end stream");
			}

			@Override
			public boolean setProperty(String key, Object value) {
				logger.trace("set property {} {}", key,value);
				return false;
			}
		};

		logger.trace("test logger");

		StreamProcessor streamProcessor = new StreamProcessor(RdfaParser.connect(ts));
		streamProcessor.process(new File("/Users/lgu/workspace/SPARQLAnything/sparql.anything/sparql-anything-html/src/test/resources/RDFa.html"));
	}
}
