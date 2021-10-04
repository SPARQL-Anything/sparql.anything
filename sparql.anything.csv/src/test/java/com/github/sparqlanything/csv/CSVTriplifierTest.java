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

package com.github.sparqlanything.csv;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.Test;
import static org.junit.Assert.fail;

import com.github.sparqlanything.model.IRIArgument;

public class CSVTriplifierTest {
	private CSVTriplifier triplifier = new CSVTriplifier();

	@Test
	public void testCsvNullStrings() throws IOException, TriplifierHTTPException {
		testCsvNullString("");
		testCsvNullString("N/A");
		testCsvNullString(" ");
	}

	public void testCsvNullString(String nullString) throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
		properties.setProperty("csv.null-string", nullString);
		URL csv1 = getClass().getClassLoader().getResource("./test3.csv");
		properties.setProperty(IRIArgument.LOCATION.toString(), csv1.toString());
		BasicPattern bp = new BasicPattern();
		bp.add(new Triple(NodeFactory.createVariable("s"), NodeFactory.createVariable("p"),
					NodeFactory.createVariable("o")));
		DatasetGraph graph = triplifier.triplify(properties, new BaseFacadeXBuilder(csv1.toString(), properties));
		// with csv.null-string set to nullString we should not see any quads with nullString in the object position
		if(graph.find(Node.ANY,Node.ANY,Node.ANY,NodeFactory.createLiteral(nullString)).hasNext()){
			fail("csv.null-string didn't work for: \"" +  nullString + "\"");
		}
	}

	@Test
	public void test() throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
		URL csv1 = getClass().getClassLoader().getResource("./test1.csv");
		properties.setProperty(IRIArgument.LOCATION.toString(), csv1.toString());
		BasicPattern bp = new BasicPattern();
		bp.add(new Triple(NodeFactory.createVariable("s"), NodeFactory.createVariable("p"),
					NodeFactory.createVariable("o")));
		DatasetGraph graph = triplifier.triplify(properties, new BaseFacadeXBuilder(csv1.toString(), properties));

		Graph expected = GraphFactory.createGraphMem();


	}

	@Test
	public void testBNodesFalse() throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
		properties.setProperty("blank-nodes", "false");
		//        properties.setProperty("uriRoot", "http://www.example.org#");

		URL csv1 = getClass().getClassLoader().getResource("./test1.csv");
		properties.setProperty(IRIArgument.LOCATION.toString(), csv1.toString());
		DatasetGraph graph = triplifier.triplify(properties);
		Iterator<Quad> iter = graph.find(null, null, null, null);
		while (iter.hasNext()) {
			Quad t = iter.next();
			System.err.println(t);
		}
	}
}
