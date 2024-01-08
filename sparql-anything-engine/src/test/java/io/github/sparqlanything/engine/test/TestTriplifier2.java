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

package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.Triplifier;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.graph.GraphFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

public class TestTriplifier2 implements Triplifier {

	public TestTriplifier2() {
//		System.err.println(getClass().getName());
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		Graph g = GraphFactory.createGraphMem();

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return;

		String content = IOUtils.toString(url, Charset.defaultCharset());

		builder.add(NodeFactory.createURI(TriplifierRegistryTest.PREFIX + "g"), NodeFactory.createURI(TriplifierRegistryTest.PREFIX + "s"), NodeFactory.createURI(TriplifierRegistryTest.PREFIX + "p"),
				NodeFactory.createLiteral(content));
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("test-mime2");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("test2");
	}
}
