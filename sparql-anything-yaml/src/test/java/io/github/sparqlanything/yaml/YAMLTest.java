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

package io.github.sparqlanything.yaml;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class YAMLTest {

	private static final Logger logger = LoggerFactory.getLogger(YAMLTest.class);

	@Test
	public void test() throws TriplifierHTTPException, IOException, URISyntaxException {
		URL url = getClass().getClassLoader().getResource("./test-change-name.yaml");
		Triplifier t = new YAMLTriplifier();
		Properties properties = new Properties();
		properties.setProperty("location", url.toURI().toString());
		FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(properties);
		t.triplify(properties, b);
		DatasetGraph ds = b.getDatasetGraph();
		ExtendedIterator<Triple> triples = ds.getDefaultGraph().find();
		while(triples.hasNext()){
			logger.trace("{}",triples.next());
		}
		Iterator<Node> graphs = ds.listGraphNodes();
		while(graphs.hasNext()){
			logger.debug("{}", graphs.next());
		}
		Assert.assertTrue(ds.size() == 1);
		int s = ds.getDefaultGraph().size();
		Assert.assertTrue(ds.getDefaultGraph().size() == 16);
	}

}
