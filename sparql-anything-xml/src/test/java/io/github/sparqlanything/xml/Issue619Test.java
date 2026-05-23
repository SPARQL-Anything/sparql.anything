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



package io.github.sparqlanything.xml;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;
import java.util.Properties;

public class Issue619Test {

	final static Logger logger = LoggerFactory.getLogger(Issue619Test.class);

	@Test
	public void newlinesAndWhitespacePreservedWhenTrimStringsIsFalse()
		throws TriplifierHTTPException, IOException {
		Properties properties = new Properties();
		URL xml = getClass().getClassLoader().getResource("./Issue619.xml");
		properties.setProperty(IRIArgument.LOCATION.toString(),
			Objects.requireNonNull(xml).toString());
		properties.setProperty(IRIArgument.TRIM_STRINGS.toString(), "false");

		FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(properties);
		XMLTriplifier triplifier = new XMLTriplifier();
		triplifier.triplify(properties, builder);

		DatasetGraph graph = builder.getDatasetGraph();
		logger.debug("{}", graph);

		String expected = "Line 1\n        Line 2";
		Iterator<Quad> it = graph.find(null, null, RDF.li(1).asNode(),
			NodeFactory.createLiteralString(expected));
		Assert.assertTrue(
			"Expected literal preserving the newline and indentation between 'Line 1' and 'Line 2'",
			it.hasNext());
	}
}
