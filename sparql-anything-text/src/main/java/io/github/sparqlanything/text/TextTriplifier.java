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

package io.github.sparqlanything.text;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.sparqlanything.model.*;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.riot.other.G;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@io.github.sparqlanything.model.annotations.Triplifier
public class TextTriplifier implements Triplifier {


	@Example(resource = "https://sparql-anything.cc/examples/simple.txt", description = "Retrieving lines of the file.", query = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?line WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.txt> { fx:properties fx:txt.regex \".*\\\\n\" . ?s fx:anySlot ?line } }")
	@Option(description = "It tells SPARQL Anything to evaluate a regular expression on the data source. In this case the slots will be filled with the bindings of the regex.", validValues = "Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)")
	public static final IRIArgument REGEX = new IRIArgument("txt.regex");

	@Example(resource = "https://sparql-anything.cc/examples/simple.txt", description = "Retrieving the lines of the file and strips `\\n` out.", query = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?line WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.txt> { fx:properties fx:txt.regex \"(.*)\\\\n\" ; fx:txt.group 1 . ?s fx:anySlot ?line } }")
	@Option(description = "It tells SPARQL Anything to generate slots by using a specific group of the regular expression.", validValues = "Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)")
	public static final IRIArgument GROUP = new IRIArgument("txt.group", "-1");

	@Example(resource = "https://sparql-anything.cc/examples/simple.txt", description = "Retrieving the lines of the file by splitting by `\\n`", query = " PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?line WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.txt> { fx:properties fx:txt.split \"\\\\n\" . ?s fx:anySlot ?line } } ")
	@Option(description = "It tells SPARQL Anything to split the input around the matches of the give regular expression.", validValues = "Any valid regular expression according to the [Pattern class](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)")
	public static final IRIArgument SPLIT = new IRIArgument("txt.split");
	private static final Logger logger = LoggerFactory.getLogger(TextTriplifier.class);

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {

		String value;
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		String rootId = SPARQLAnythingConstants.ROOT_ID;
		value = IOUtils.toString(Triplifier.getInputStream(properties), Triplifier.getCharsetArgument(properties));

		if (logger.isTraceEnabled()) {
			logger.trace("Content:\n{}\n", value);
		}

		builder.addRoot(dataSourceId);

		Pattern pattern = null;
		String regexString = PropertyUtils.getStringProperty(properties, REGEX);
		if (regexString!=null) {
			logger.trace("Regex {}", regexString);
			try {
				pattern = Pattern.compile(regexString);
				// TODO flags
			} catch (Exception e) {
//				e.printStackTrace();
				logger.error(e.getMessage(), e);
				pattern = null;
			}

		}
		int group = PropertyUtils.getIntegerProperty(properties, GROUP);
		String splitStr = PropertyUtils.getStringProperty(properties, SPLIT);


		if (pattern != null) {
			logger.trace("Instantiating the matcher group {}", group);
			Matcher m = pattern.matcher(value);
			int count = 1;
			while (m.find()) {
				if (group >= 1) {
					logger.trace("Adding value from group {}: slot {} - {}", group, count, m.group(group));
					builder.addValue(dataSourceId, rootId, count, m.group(group));
				} else {
					logger.trace("Adding value {} {}", count, m.group());
					builder.addValue(dataSourceId, rootId, count, m.group());
				}
				count++;
			}
		} else {
			logger.trace("No pattern set");

			if (splitStr!=null) {

				logger.trace("Splitting regex: {}", splitStr);
				String[] split = value.split(splitStr);
				for (int i = 0; i < split.length; i++) {
					builder.addValue(dataSourceId, rootId, i + 1, split[i]);
				}

			} else {
				builder.addValue(dataSourceId, rootId, 1, value);
			}

		}
	}

//	private static String readFromURL(URL url, Properties properties) throws IOException, TriplifierHTTPException {
//		StringWriter sw = new StringWriter();
//		InputStream is = Triplifier.getInputStream(properties);
//		IOUtils.copy(is, sw, Triplifier.getCharsetArgument(properties));
//		return sw.toString();
//
//	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("text/plain");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("txt");
	}
}
