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

package com.github.sparqlanything.json.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import com.github.sparqlanything.model.*;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.sparqlanything.json.JSONTriplifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {
	private static final Logger L = LoggerFactory.getLogger(BaseTest.class);
	@Test
	public void testContent() throws TriplifierHTTPException {

		Triplifier jt = new JSONTriplifier();

		try {
			Properties p1 = new Properties();
			p1.setProperty(IRIArgument.CONTENT.toString(), "{\"a\":\"b\"}");
			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", p1);
			jt.triplify(p1, b);
			DatasetGraph g1 = b.getDatasetGraph();
//			RDFDataMgr.write(System.out, g1, RDFFormat.TRIG);
			Graph expected = GraphFactory.createDefaultGraph();
			Node n = NodeFactory.createBlankNode();
			expected.add(n, NodeFactory.createURI(Triplifier.XYZ_NS + "a"), NodeFactory.createLiteral("b"));
			expected.add(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
			
			assertTrue(g1.getDefaultGraph().isIsomorphicWith(expected));

		} catch (IOException e1) {
			L.error("",e1);
		}

	}

}
