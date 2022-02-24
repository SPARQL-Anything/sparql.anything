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
package com.github.sparqlanything.html;

import com.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class HTMLMicrodataRemoteTest extends AbstractTriplifierTester {

	public HTMLMicrodataRemoteTest() {
		super(new HTMLTriplifier(), new Properties(), "html", "nq");
		this.setPrintWholeGraph(true);
	}

	@Test
	public void testMicrodata1() {
		this.assertResultIsIsomorphicWithExpected();
	}

	protected void properties(Properties properties) {
		properties.setProperty(HTMLTriplifier.PROPERTY_METADATA, "true");
	}

	protected void prepare() throws URISyntaxException {
		logger.debug("{} (prepare)", name.getMethodName());
		// Root is Document
		try {
			url = new URL(
					"https://raw.githubusercontent.com/SPARQL-Anything/sparql-anything.cc-site/main/examples/Microdata1.html");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		logger.debug("Input filename: {}", url.toString());
		properties.setProperty("location", url.toURI().toString());
		properties.setProperty("blank-nodes", "false");
		logger.debug("Input location: {}", url.toURI().toString());
		properties.setProperty("root", "http://www.example.org/document");
		//
		// RDF file name
		String rdfFileName = name.getMethodName().substring(4) + "." + expectedExtension;
		if (!useDatasetGraph) {
			expected = RDFDataMgr.loadModel(getClass().getClassLoader().getResource(rdfFileName).toURI().toString())
					.getGraph();
		} else {
			expectedDatasetGraph = super.replaceLocation(RDFDataMgr
					.loadDatasetGraph(getClass().getClassLoader().getResource(rdfFileName).toURI().toString()));
		}

	}

}
