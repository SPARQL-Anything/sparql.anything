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

package com.github.sparqlanything.bib.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.sparqlanything.bib.BibtexTriplifier;
import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;

public class BibtexTriplifierTest {

	@Test
	public void test1() {
		BibtexTriplifier jt = new BibtexTriplifier();

		try {
			Properties p1 = new Properties();
			p1.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./test1.bib").toString());
			DatasetGraph g1 = jt.triplify(p1, new BaseFacadeXBuilder("test", p1));

//			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");
			
			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			Node article = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), article));
			expectedGraph
					.add(new Triple(article, RDF.type.asNode(), NodeFactory.createURI(Triplifier.XYZ_NS + "article")));
			expectedGraph.add(new Triple(article, NodeFactory.createURI(Triplifier.XYZ_NS + "author"),
					NodeFactory.createLiteral("Donald E. Knuth")));
			expectedGraph.add(new Triple(article, NodeFactory.createURI(Triplifier.XYZ_NS + "journal"),
					NodeFactory.createLiteral("The Computer Journal")));
			expectedGraph.add(new Triple(article, NodeFactory.createURI(Triplifier.XYZ_NS + "number"),
					NodeFactory.createLiteral("2")));
			expectedGraph.add(new Triple(article, NodeFactory.createURI(Triplifier.XYZ_NS + "pages"),
					NodeFactory.createLiteral("97--111")));
			expectedGraph.add(new Triple(article, NodeFactory.createURI(Triplifier.XYZ_NS + "publisher"),
					NodeFactory.createLiteral("Oxford University Press")));
			expectedGraph.add(new Triple(article, NodeFactory.createURI(Triplifier.XYZ_NS + "title"),
					NodeFactory.createLiteral("Literate Programming")));
			expectedGraph.add(new Triple(article, NodeFactory.createURI(Triplifier.XYZ_NS + "volume"),
					NodeFactory.createLiteral("27")));
			expectedGraph.add(new Triple(article, NodeFactory.createURI(Triplifier.XYZ_NS + "year"),
					NodeFactory.createLiteral("1984")));

			assertTrue(expectedGraph.isIsomorphicWith(g1.getDefaultGraph()));

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
