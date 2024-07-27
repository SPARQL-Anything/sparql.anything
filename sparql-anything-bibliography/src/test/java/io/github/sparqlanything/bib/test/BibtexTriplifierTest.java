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

package io.github.sparqlanything.bib.test;

import io.github.sparqlanything.bib.BibtexTriplifier;
import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class BibtexTriplifierTest {
	final static Logger logger = LoggerFactory.getLogger(BibtexTriplifierTest.class);

	@Test
	public void test1() {
		BibtexTriplifier jt = new BibtexTriplifier();

		try {
			Properties p1 = new Properties();
			p1.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./test1.bib").toString());
//			DatasetGraph g1 = jt.triplify(p1, new BaseFacadeXGraphBuilder("test", p1));
			FacadeXGraphBuilder b =  new BaseFacadeXGraphBuilder(p1);
			jt.triplify(p1, b);
			DatasetGraph g1 = b.getDatasetGraph();

//			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");
			
			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			Node article = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n, RDF.li(1).asNode(), article));
			expectedGraph
					.add(Triple.create(article, RDF.type.asNode(), NodeFactory.createURI(Triplifier.XYZ_NS + "article")));
			expectedGraph.add(Triple.create(article, NodeFactory.createURI(Triplifier.XYZ_NS + "author"),
					NodeFactory.createLiteralString("Donald E. Knuth")));
			expectedGraph.add(Triple.create(article, NodeFactory.createURI(Triplifier.XYZ_NS + "journal"),
					NodeFactory.createLiteralString("The Computer Journal")));
			expectedGraph.add(Triple.create(article, NodeFactory.createURI(Triplifier.XYZ_NS + "number"),
					NodeFactory.createLiteralString("2")));
			expectedGraph.add(Triple.create(article, NodeFactory.createURI(Triplifier.XYZ_NS + "pages"),
					NodeFactory.createLiteralString("97--111")));
			expectedGraph.add(Triple.create(article, NodeFactory.createURI(Triplifier.XYZ_NS + "publisher"),
					NodeFactory.createLiteralString("Oxford University Press")));
			expectedGraph.add(Triple.create(article, NodeFactory.createURI(Triplifier.XYZ_NS + "title"),
					NodeFactory.createLiteralString("Literate Programming")));
			expectedGraph.add(Triple.create(article, NodeFactory.createURI(Triplifier.XYZ_NS + "volume"),
					NodeFactory.createLiteralString("27")));
			expectedGraph.add(Triple.create(article, NodeFactory.createURI(Triplifier.XYZ_NS + "year"),
					NodeFactory.createLiteralString("1984")));

			assertTrue(expectedGraph.isIsomorphicWith(g1.getDefaultGraph()));

		} catch (IOException e1) {
			logger.error("",e1);
		}
	}

}
