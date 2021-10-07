
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

package com.github.sparqlanything.xml;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;

public class XMLTriplifierTest {
	private XMLTriplifier triplifier = new XMLTriplifier();
	public static Logger log = LoggerFactory.getLogger(XMLTriplifierTest.class);

	@Test
	public void test1() throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("baseNamespace", "http://www.example.org#");

		URL xml1 = getClass().getClassLoader().getResource("./test1.xml");
		properties.setProperty(IRIArgument.LOCATION.toString(), xml1.toString());

		DatasetGraph graph = triplifier.triplify(properties);
		Iterator<Quad> iter = graph.find(null, null, RDF.type.asNode(),
				NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
		Assert.assertTrue(iter.hasNext());
	}

	@Test
	public void testBNodesFalse() throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("baseNamespace", "http://www.example.org#");
		properties.setProperty("blank-nodes", "false");
		URL xml1 = getClass().getClassLoader().getResource("./test1.xml");
		properties.setProperty(IRIArgument.LOCATION.toString(), xml1.toString());
		DatasetGraph graph = triplifier.triplify(properties);
//        ModelFactory.createModelForGraph(graph.getDefaultGraph()).write(System.out,"TTL");
		Iterator<Quad> iter = graph.find(null, null, null, null);
		while (iter.hasNext()) {
			Quad q = iter.next();
			log.info("{} {} {}", q.getSubject(), q.getPredicate(), q.getObject());
			Assert.assertFalse(q.getSubject().isBlank());
			Assert.assertFalse(q.getObject().isBlank());
		}

	}
}
