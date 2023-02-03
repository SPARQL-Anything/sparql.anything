/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.cli;

import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.lang.JsonLDReader;
import org.apache.jena.riot.system.ParserProfile;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JsonldTest {
	public static final Logger L = LoggerFactory.getLogger(JsonldTest.class);

	static class ReaderRIOTFactoryJSONLD implements ReaderRIOTFactory {
		@Override
		public ReaderRIOT create(Lang language, ParserProfile profile) {
			if ( !buildJSONLang().equals(language) )
				throw new InternalErrorException("Attempt to parse " + language + " as JSON-LD");
			return new JsonLDReader(language, profile, profile.getErrorHandler());
		}
	}

	static Lang buildJSONLang(){
		Lang JSON    = LangBuilder.create("JSON", "application/ld+json")
				.addAltNames("JSON")
				.addFileExtensions("json")
				.build();
		return JSON;
	}

	// Strategy to fix issue #339
	@Test
	public void test() throws Exception {
		ReaderRIOTFactory parserFactoryJsonLD    = new ReaderRIOTFactoryJSONLD();
		RDFParserRegistry.registerLangTriples(buildJSONLang(), parserFactoryJsonLD);
		String q = getClass().getClassLoader().getResource("./JsonldTest.sparql").toString();
		File loadSource = new File(getClass().getClassLoader().getResource("./JsonldTest.json").toURI());
		L.info("{}", loadSource.exists());
		String d = IOUtils.toString(getClass().getClassLoader().getResource("./JsonldTest.json").toURI());
		SPARQLAnything sa = new SPARQLAnything();
		String out = sa.callMain(new String[]{
			"-q", q, "-l", f
		});
		L.info("{}", out);
	}
}
