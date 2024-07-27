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

package io.github.sparqlanything.binary.test;

import io.github.sparqlanything.binary.BinaryTriplifier;
import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class BinaryTriplifierTest {
	final static Logger logger = LoggerFactory.getLogger(BinaryTriplifierTest.class);

	@Test
	public void testBase64() throws MalformedURLException {
		BinaryTriplifier bt = new BinaryTriplifier();
		File f = new File("src/main/resources/testfile");
		URL url = f.toURI().toURL();
		try {
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			bt.triplify(p, builder);
			DatasetGraph dg = builder.getDatasetGraph();
			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n,RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(Triple.create(n, RDF.li(1).asNode(),
					NodeFactory.createLiteral("dGhpcyBpcyBhIHRlc3Q=", XSDDatatype.XSDbase64Binary)));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p))).isIsomorphicWith(expectedGraph));
		} catch (IOException e) {
			logger.error("",e);
		}
	}

}
