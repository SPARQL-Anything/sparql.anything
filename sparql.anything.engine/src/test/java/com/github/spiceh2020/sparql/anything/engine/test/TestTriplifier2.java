/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.spiceh2020.sparql.anything.engine.test;

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
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
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		// TODO Not tested yet
		return triplify(properties);
	}

	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {

		DatasetGraph dg = DatasetGraphFactory.create();
		Graph g = GraphFactory.createGraphMem();

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return DatasetGraphFactory.create();

		String content = IOUtils.toString(url, Charset.defaultCharset());

		g.add(new Triple(NodeFactory.createURI(TriplifierRegistryTest.PREFIX + "s"), NodeFactory.createURI(TriplifierRegistryTest.PREFIX + "p"),
				NodeFactory.createLiteral(content)));
		dg.addGraph(NodeFactory.createURI(TriplifierRegistryTest.PREFIX + "g"), g);
		dg.setDefaultGraph(g);
		return dg;
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
