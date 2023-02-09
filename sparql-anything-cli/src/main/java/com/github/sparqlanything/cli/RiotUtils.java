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

import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.lang.LangJSONLD10;
import org.apache.jena.riot.lang.LangJSONLD11;
import org.apache.jena.riot.system.ParserProfile;

public class RiotUtils {
	static class ReaderRIOTFactoryJSONLD implements ReaderRIOTFactory {
		@Override
		public ReaderRIOT create(Lang language, ParserProfile profile) {
			if ( !JSON.equals(language) )
				throw new InternalErrorException("Attempt to parse " + language + " as JSON-LD");
			return new LangJSONLD11(language, profile, profile.getErrorHandler());
		}
	}

	/**
	 * We add a lang JSON to attempt to load JSON files as JSON-LD
	 * See #339
	 */
	public static Lang JSON;

	static {
		JSON = LangBuilder.create("JSON", "application/ld+json")
				.addAltNames("JSON")
				.addFileExtensions("json")
				.build();
	}
}
