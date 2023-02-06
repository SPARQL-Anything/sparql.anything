/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.html;

import com.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
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

public class HTMLTriplifierTest {
	public static final Logger log = LoggerFactory.getLogger(HTMLTriplifierTest.class);
	private HTMLTriplifier html2rdf = new HTMLTriplifier();

	@Rule
	public TestName name = new TestName();

	private String getTestLocation(String fileName) throws URISyntaxException {
		return getClass().getClassLoader().getResource(fileName + ".html").toURI().toString();
	}

	@Test
	public void test1() throws URISyntaxException, IOException, TriplifierHTTPException {
		Properties p = new Properties();
		URL url = new URL(getTestLocation(name.getMethodName()));
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(url.toString(), p);
		html2rdf.triplify(p, builder);
		DatasetGraph dataset = builder.getDatasetGraph();
//        Iterator<Quad> iter = dataset.find(null,null,null,null);
//        while(iter.hasNext()){
//            Quad t = iter.next();
//            System.err.println(t);
//        }
		Model m = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
		m.setNsPrefix("xhtml", "http://www.w3.org/1999/xhtml#");
		m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		m.write(System.out, "TTL");
	}

	@Test
	public void test2() throws URISyntaxException, IOException, TriplifierHTTPException {
		Properties p = new Properties();
		URL url = new URL(getTestLocation(name.getMethodName()));
		p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
		FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(url.toString(), p);
		html2rdf.triplify(p, builder);
		DatasetGraph dataset = builder.getDatasetGraph();
		Model m = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
		m.setNsPrefix("xhtml", "http://www.w3.org/1999/xhtml#");
		m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		m.write(System.out, "TTL");
	}

	@Test
	public void testBN() throws TriplifierHTTPException {

		HTMLTriplifier st = new HTMLTriplifier();
		Properties p = new Properties();
		p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
		DatasetGraph dg;
		try {
			URL spreadsheet = new URL(getTestLocation(name.getMethodName()));
			p.setProperty(IRIArgument.LOCATION.toString(), spreadsheet.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(spreadsheet.toString(), p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();

			dg.find(null, null, null, null).forEachRemaining(q -> {
				log.info("{} {} {}", q.getSubject(), q.getPredicate(), q.getObject());
				assertTrue(!q.getSubject().isBlank());
				assertTrue(!q.getPredicate().isBlank());
				assertTrue(!q.getObject().isBlank());
				assertTrue(!q.getGraph().isBlank());
			});

		} catch (IOException | URISyntaxException e) {
			log.error("",e);
		}
	}
}
