/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jena.query.TxnType;

import com.github.sparqlanything.model.IRIArgument;

public class CSVTriplifierTest {
	private CSVTriplifier triplifier = new CSVTriplifier();
	public static final Logger log = LoggerFactory.getLogger(CSVTriplifierTest.class);

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


	@Test
	public void testTab() throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
		properties.setProperty("csv.delimiter", "\t");
		properties.setProperty("csv.headers", "true");
		properties.setProperty("blank-nodes", "false");
		//        properties.setProperty("uriRoot", "http://www.example.org#");

		URL csv1 = getClass().getClassLoader().getResource("./test.tsv");
		properties.setProperty(IRIArgument.LOCATION.toString(), csv1.toString());
		DatasetGraph graph = triplifier.triplify(properties);
		Iterator<Quad> iter = graph.find(null, null, null, null);
		while (iter.hasNext()) {
			Quad t = iter.next();
			System.err.println(t);
		}
	}

	@Test
	public void testWithOnDiskGraph1 () throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
		properties.setProperty("ondisk", "/tmp");
		URL csv1 = getClass().getClassLoader().getResource("./test3.csv");
		properties.setProperty(IRIArgument.LOCATION.toString(), csv1.toString());
		BasicPattern bp = new BasicPattern();
		bp.add(new Triple(NodeFactory.createVariable("s"), NodeFactory.createVariable("p"),
					NodeFactory.createVariable("o")));
		DatasetGraph graph = triplifier.triplify(properties, new BaseFacadeXBuilder(csv1.toString(), properties));

		// end the write txn because triplifiers don't do that, FacadeXOpExecutor does
		graph.commit();
		graph.end();

		graph.begin(TxnType.READ);
		Iterator<Quad> iter = graph.find(null, null, null, null);
		Integer count=0 ;
		while (iter.hasNext()) {
			count++;
			Quad t = iter.next();
		}
		if(count!=21){
			fail("expected 21 quads but found " + count);
		}
		graph.end();
	}

	@Test
	public void testWithOnDiskGraph2 () throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
		properties.setProperty("ondisk", "/tmp");
		URL csv1 = getClass().getClassLoader().getResource("./test1.csv");
		properties.setProperty(IRIArgument.LOCATION.toString(), csv1.toString());
		BasicPattern bp = new BasicPattern();
		bp.add(new Triple(NodeFactory.createVariable("s"), NodeFactory.createVariable("p"),
					NodeFactory.createVariable("o")));
		DatasetGraph graph = triplifier.triplify(properties, new BaseFacadeXBuilder(csv1.toString(), properties));
		// end the write txn because triplifiers don't do that, FacadeXOpExecutor does
		graph.commit();
		graph.end();

		graph.begin(TxnType.READ);
		Iterator<Quad> iter = graph.find(null, null, null, null);
		Integer count=0 ;
		while (iter.hasNext()) {
			count++;
			Quad t = iter.next();
		}
		if(count!=13){
			fail("expected 13 quads but found " + count);
		}
		graph.end();
	}
}
