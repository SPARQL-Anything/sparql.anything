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

package com.github.sparqlanything.markdown;

import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Graph;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class MARKDOWNTriplifierTest {

	private static final Logger logger = LoggerFactory.getLogger(MARKDOWNTriplifierTest.class);

	@Rule
	public TestName name = new TestName();

//	@Test
//	public void test() throws TriplifierHTTPException, IOException, URISyntaxException {
//
//		URL url = getClass().getClassLoader().getResource("./simple.md");
//		Triplifier t = new MARKDOWNTriplifier();
//		Properties properties = new Properties();
//		properties.setProperty("location", url.toURI().toString());
//		DatasetGraph ds = t.triplify(properties, new BaseFacadeXBuilder("simple.md", properties));
//		ExtendedIterator<Triple> triples = ds.getDefaultGraph().find();
//		while(triples.hasNext()){
//			logger.trace("{}",triples.next());
//		}
//		Iterator<Node> graphs = ds.listGraphNodes();
//		while(graphs.hasNext()){
//			System.out.println(graphs.next());
//		}
////		Assert.assertTrue(ds.size() == 2);
////		Assert.assertTrue(ds.getDefaultGraph().size() == 11);
//	}

	private Graph result;
	private Graph expected;

	@Before
	public void before() throws TriplifierHTTPException, IOException, URISyntaxException {
		// Root is Document
		String fileName = name.getMethodName().substring(4) + ".md";
		logger.debug("Input filename: {}", fileName);
		URL url = getClass().getClassLoader().getResource(fileName);
		Properties properties = new Properties();
		properties.setProperty("location", url.toURI().toString());
		properties.setProperty("blank-nodes", "false");
		logger.debug("Input location: {}", url.toURI().toString());
		properties.setProperty("root", "http://www.example.org/document");
		Triplifier tr = new MARKDOWNTriplifier();
		String graphName = Triplifier.getRootArgument(properties, url);
		logger.debug("Graph name: {}", graphName);
		this.result = tr.triplify(properties, new BaseFacadeXBuilder(graphName, properties)).getGraph(NodeFactory.createURI(graphName));

		// RDF file name
		String rdfFileName = name.getMethodName().substring(4) + ".ttl";
		expected = RDFDataMgr.loadModel(getClass().getClassLoader().getResource(rdfFileName).toURI().toString()).getGraph();

		if(logger.isWarnEnabled()) {
			ExtendedIterator<Triple> it = expected.find();
			while (it.hasNext()) {
				Triple t = it.next();
				logger.trace("E>> {}", t);

				if(!result.contains(t)){
					logger.warn("{} not found in result", t);
					logger.warn("(T) {} {} {} {}", t.getSubject().getClass().getSimpleName(),  t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(), (t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null) ? t.getObject().getLiteralDatatypeURI():null);
				}
			}
			it = result.find();
			while (it.hasNext()) {
				Triple t = it.next();
				logger.trace("<<R {}", t);
				if(!expected.contains(t)){
					logger.warn("{} not found in expected", t);
					logger.warn("(T) {} {} {} {}", t.getSubject().getClass().getSimpleName(),  t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(), (t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null) ? t.getObject().getLiteralDatatypeURI():null);
				}
			}
		}
	}

	@Test
	public void testDocument() {
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testDocument_2() {
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testHeading(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testParagraph(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

//
	@Test
	public void testCode(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testLink(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testLink_2(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testBlockQuote(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testText(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testEmphasis(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testStrongEmphasis(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testIndentedCodeBlock(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}


	@Test
	public void testListItem(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

	@Test
	public void testBulletList(){
		logger.debug("Test: {}", name.getMethodName());
		assertTrue(this.result.isIsomorphicWith(expected));
	}

//
//	@Test
//	public void testOrderedList(){}
//
//	@Test
//	public void testBulletList(){}
//
//
//
//	@Test
//	public void testFencedCodeBlock(){}
//
//	@Test
//	public void testHardLineBreak(){}
//
//	@Test
//	public void testThematicBreak(){}
//
//	@Test
//	public void testHtmlInline(){}
//
//	@Test
//	public void testHtmlBlock(){}
//
//	@Test
//	public void testIndentedCodeBlock(){}
//
//	@Test
//	public void testImage(){}
//
//	@Test
//	public void testSoftLineBreak(){}
//
//	@Test
//	public void testLinkReferenceDefinition(){}
//
//
//	@Test
//	public void testCustomBlock(){}
//
//	@Test
//	public void testCustomNode(){}

}
