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

public class Issue325Test {

	final static Logger logger = LoggerFactory.getLogger(Issue325Test.class);

	@Test
	public void test() throws TriplifierHTTPException, IOException {
		Properties properties = new Properties();
		URL xml1 = getClass().getClassLoader().getResource("./Issue325.xml");
		properties.setProperty(IRIArgument.LOCATION.toString(), Objects.requireNonNull(xml1).toString());
		FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(properties);
		XMLTriplifier triplifier = new XMLTriplifier();
		triplifier.triplify(properties, builder);
		DatasetGraph graph = builder.getDatasetGraph();
		logger.debug("{}", graph);

		Iterator<Quad> iter = graph.find(null, null, RDF.li(1).asNode(),
				NodeFactory.createLiteralString("THIS_TEXT_IS_INSIDE_SUBJECT"));
		Assert.assertTrue(iter.hasNext());
		Iterator<Quad> iter2 = graph.find(null, null, RDF.li(1).asNode(),
				NodeFactory.createLiteralString("THIS_TEXT_IS_OUTSIDE_SUBJECT"));
		Assert.assertTrue(iter2.hasNext());
	}
}
