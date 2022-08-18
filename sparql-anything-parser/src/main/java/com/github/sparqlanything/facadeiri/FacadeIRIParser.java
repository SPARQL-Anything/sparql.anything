/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.facadeiri;

import com.github.sparqlanything.facadeiri.antlr.FacadeIRILexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacadeIRIParser {

	private String tupleURL;
	public final static String SPARQL_ANYTHING_URI_SCHEMA = "x-sparql-anything:";
	private final static Pattern key = Pattern.compile("^[a-zA-Z0-9-]+");
	private static final Logger log = LoggerFactory.getLogger(FacadeIRIParser.class);

	public FacadeIRIParser(String tupleURL) {
		super();
		this.tupleURL = tupleURL;
	}

	public String getTupleURL() {
		return tupleURL;
	}

	public void setTupleURL(String tupleURL) {
		this.tupleURL = tupleURL;
	}

	public Properties getProperties() {
		FacadeIRILexer lexer = new FacadeIRILexer(
				CharStreams.fromString(escape(tupleURL.substring(SPARQL_ANYTHING_URI_SCHEMA.length()))));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		com.github.sparqlanything.facadeiri.antlr.FacadeIRIParser parser = new com.github.sparqlanything.facadeiri.antlr.FacadeIRIParser(
				tokens);
		ParseTree tree = parser.basicURL();
		ParseTreeWalker walker = new ParseTreeWalker();
		ParameterListener listener = new ParameterListener();
		walker.walk(listener, tree);
		return listener.getProperties();
	}

	private String escape(String s) {
		Matcher m = key.matcher(s);
		log.trace("Input escape {}",s);
		if (m.find() && m.end() < s.length() && s.charAt(m.end()) != '=') {
			log.trace("Unescape");
			// it is an URI => unescape
			return s;
		}
		boolean lookingForEqual = false;
		boolean lookingForComma = true;
		StringBuilder sb = new StringBuilder();
		sb.append(s.subSequence(0, m.end() + 1));
		for (int i = m.end() + 1; i < s.length(); i++) {
			if (lookingForComma && s.charAt(i) == '=') {
				// Escape =
				sb.append('\\');
				sb.append('=');
			} else if (lookingForComma && s.charAt(i) == ',' && s.charAt(i - 1) != '\\') {
				sb.append(s.charAt(i));
				lookingForEqual = true;
				lookingForComma = false;
			} else if (lookingForComma && s.charAt(i) != ',') {
				sb.append(s.charAt(i));
			} else if (lookingForEqual && s.charAt(i) == '=') {
				sb.append(s.charAt(i));
				lookingForComma = true;
			} else if (lookingForEqual && s.charAt(i) != '=') {
				sb.append(s.charAt(i));
			}
		}
		log.trace("Escaped {}", sb.toString());
		return sb.toString();
	}

}
