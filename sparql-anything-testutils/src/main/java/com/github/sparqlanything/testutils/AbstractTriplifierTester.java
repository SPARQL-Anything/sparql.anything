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

package com.github.sparqlanything.testutils;

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
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class AbstractTriplifierTester {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractTriplifierTester.class);

	protected Triplifier triplifier;
	protected Properties properties;
	protected URL url;
	private String extension = null;

	protected Graph result;
	protected Graph expected;

	public AbstractTriplifierTester(Triplifier t, Properties p, String extension){
		this.triplifier = t;
		this.properties = p;
		this.extension = extension;
	}

	@Rule
	public TestName name = new TestName();

	private void prepare() throws URISyntaxException {
		logger.debug("{} (prepare)", name.getMethodName());
		// Root is Document
		String fileName = name.getMethodName().substring(4) + "." + extension;
		logger.debug("Input filename: {}", fileName);
		url = getClass().getClassLoader().getResource(fileName);
		properties.setProperty("location", url.toURI().toString());
		properties.setProperty("blank-nodes", "false");
		logger.debug("Input location: {}", url.toURI().toString());
		properties.setProperty("root", "http://www.example.org/document");
		//
		// RDF file name
		String rdfFileName = name.getMethodName().substring(4) + ".ttl";
		expected = RDFDataMgr.loadModel(getClass().getClassLoader().getResource(rdfFileName).toURI().toString()).getGraph();
	}

	protected void properties(Properties properties){}

	@Before
	public void run() throws URISyntaxException, TriplifierHTTPException, IOException {
		logger.info("{} (run)", name.getMethodName());
		// Template method
		prepare();
		//
		properties(properties);
		//
		perform();
		//
		inspect();
	}

	protected void inspect(){
		logger.debug("{} (inspect)", name.getMethodName());
		if(logger.isDebugEnabled()) {
			ExtendedIterator<Triple> it = expected.find();
			while (it.hasNext()) {
				Triple t = it.next();
				logger.trace("E>> {}", t);

				if(!result.contains(t)){
					logger.debug("{} not found in result", t);
					logger.debug("(T) {} {} {} {}", t.getSubject().getClass().getSimpleName(),  t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(), (t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null) ? t.getObject().getLiteralDatatypeURI():"");
				}
			}
			it = result.find();
			while (it.hasNext()) {
				Triple t = it.next();
				logger.trace("<<R {}", t);
				if(!expected.contains(t)){
					logger.debug("{} not found in expected", t);
					logger.debug("(T) {} {} {} {}", t.getSubject().getClass().getSimpleName(),  t.getPredicate().getClass().getSimpleName(), t.getObject().getClass().getSimpleName(), (t.getObject().isLiteral() && t.getObject().getLiteralDatatypeURI() != null) ? t.getObject().getLiteralDatatypeURI():"");
				}
			}
		}
	}

	protected void perform() throws TriplifierHTTPException, IOException, URISyntaxException {
		logger.debug("{} (perform)", name.getMethodName());
		logger.info("{}", properties);
		String graphName = Triplifier.getRootArgument(properties, url);
		logger.debug("Graph name: {}", graphName);
		this.result = triplifier.triplify(properties, new BaseFacadeXBuilder(graphName, properties)).getGraph(NodeFactory.createURI(graphName));
	}

	protected void assertResultIsIsomorphicWithExpected(){
		assertTrue(this.result.isIsomorphicWith(expected));
	}
}
