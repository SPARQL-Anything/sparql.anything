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

package com.github.sparqlanything.docs.test;

import com.github.sparqlanything.docs.DocxTriplifier;
import com.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class TestTriplifier {

	@Ignore
	@Test
	public void test1() {
		DocxTriplifier st = new DocxTriplifier();
		URL doc = st.getClass().getClassLoader().getResource("./testResources/doc1.docx");
		Properties p = new Properties();
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), doc.toString());
			FacadeXGraphBuilder b =new BaseFacadeXGraphBuilder(Triplifier.getLocation(p).toString(), p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();
//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral(
					"Title 11Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.2Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Title 23Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
					XSDDatatype.XSDstring)));
			Iterator<Node> graphNodes = dg.listGraphNodes();
			while (graphNodes.hasNext()) {
				System.err.println(graphNodes.next());
			}
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p)))
					.isIsomorphicWith(expectedGraph));

			p.setProperty(DocxTriplifier.MERGE_PARAGRAPHS, "true");

			b = new BaseFacadeXGraphBuilder(Triplifier.getLocation(p).toString(), p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();
//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			expectedGraph = GraphFactory.createGraphMem();
			n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(
					new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral("Title 1", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral(
					"1Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
					XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral(
					"2Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
					XSDDatatype.XSDstring)));
			expectedGraph.add(
					new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral("Title 2", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(n, RDF.li(5).asNode(), NodeFactory.createLiteral(
					"3Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
					XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(n, RDF.li(6).asNode(), NodeFactory.createLiteral("", XSDDatatype.XSDstring)));

			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p)))
					.isIsomorphicWith(expectedGraph));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testNoBlankNodes() {
		DocxTriplifier st = new DocxTriplifier();
		URL doc = st.getClass().getClassLoader().getResource("./testResources/doc1.docx");
		Properties p = new Properties();
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), doc.toString());
			p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");

			FacadeXGraphBuilder b =new BaseFacadeXGraphBuilder(Triplifier.getLocation(p).toString(), p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();
			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			dg.find(null, null, null, null).forEachRemaining(q -> {
				assertTrue(!q.getGraph().isBlank());
				assertTrue(!q.getSubject().isBlank());
				assertTrue(!q.getPredicate().isBlank());
				assertTrue(!q.getObject().isBlank());
			});

//			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
//			assertTrue(dg.getGraph(NodeFactory.createURI(doc.toString())).isIsomorphicWith(expectedGraph));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void test2() {
		DocxTriplifier st = new DocxTriplifier();
		URL doc = st.getClass().getClassLoader().getResource("./testResources/doc2.docx");
		Properties p = new Properties();
		DatasetGraph dg;
		try {

			p.setProperty(IRIArgument.LOCATION.toString(), doc.toString());

			FacadeXGraphBuilder b =new BaseFacadeXGraphBuilder(Triplifier.getLocation(p).toString(), p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();

			RDFDataMgr.write(System.out, dg, RDFFormat.TRIG_PRETTY);

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(),
					NodeFactory.createLiteral("Title 1Paragraph1Title 2Paragraph2", XSDDatatype.XSDstring)));

			Node table = NodeFactory.createBlankNode();

			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), table));

			Node row1 = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(table, RDF.li(1).asNode(), row1));
			expectedGraph
					.add(new Triple(row1, RDF.li(1).asNode(), NodeFactory.createLiteral("11", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row1, RDF.li(2).asNode(), NodeFactory.createLiteral("12", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row1, RDF.li(3).asNode(), NodeFactory.createLiteral("13", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row1, RDF.li(4).asNode(), NodeFactory.createLiteral("14", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row1, RDF.li(5).asNode(), NodeFactory.createLiteral("15", XSDDatatype.XSDstring)));

			Node row2 = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(table, RDF.li(2).asNode(), row2));
			expectedGraph
					.add(new Triple(row2, RDF.li(1).asNode(), NodeFactory.createLiteral("21", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row2, RDF.li(2).asNode(), NodeFactory.createLiteral("22", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row2, RDF.li(3).asNode(), NodeFactory.createLiteral("23", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row2, RDF.li(4).asNode(), NodeFactory.createLiteral("24", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row2, RDF.li(5).asNode(), NodeFactory.createLiteral("25", XSDDatatype.XSDstring)));

			Node row3 = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(table, RDF.li(3).asNode(), row3));
			expectedGraph
					.add(new Triple(row3, RDF.li(1).asNode(), NodeFactory.createLiteral("31", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row3, RDF.li(2).asNode(), NodeFactory.createLiteral("32", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row3, RDF.li(3).asNode(), NodeFactory.createLiteral("33", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row3, RDF.li(4).asNode(), NodeFactory.createLiteral("34", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row3, RDF.li(5).asNode(), NodeFactory.createLiteral("35", XSDDatatype.XSDstring)));

			Node row4 = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(table, RDF.li(4).asNode(), row4));
			expectedGraph
					.add(new Triple(row4, RDF.li(1).asNode(), NodeFactory.createLiteral("41", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row4, RDF.li(2).asNode(), NodeFactory.createLiteral("42", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row4, RDF.li(3).asNode(), NodeFactory.createLiteral("43", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row4, RDF.li(4).asNode(), NodeFactory.createLiteral("44", XSDDatatype.XSDstring)));
			expectedGraph
					.add(new Triple(row4, RDF.li(5).asNode(), NodeFactory.createLiteral("45", XSDDatatype.XSDstring)));

			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p)))
					.isIsomorphicWith(expectedGraph));

			if (!dg.getDefaultGraph().isIsomorphicWith(expectedGraph)) {
				ModelFactory.createModelForGraph(expectedGraph)
						.difference(ModelFactory.createModelForGraph(dg.getDefaultGraph())).write(System.out, "TTL");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void test3() {
		DocxTriplifier st = new DocxTriplifier();
		URL doc = st.getClass().getClassLoader().getResource("./testResources/doc2.docx");
		Properties p = new Properties();
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), doc.toString());
			p.setProperty(DocxTriplifier.TABLE_HEADERS.toString(), "true");
			FacadeXGraphBuilder b =new BaseFacadeXGraphBuilder(Triplifier.getLocation(p).toString(), p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();

//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(),
					NodeFactory.createLiteral("Title 1Paragraph1Title 2Paragraph2", XSDDatatype.XSDstring)));

			Node table = NodeFactory.createBlankNode();

			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), table));

//			NodeFactory.createURI(Triplifier.XYZ_NS+"11")

			Node row2 = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(table, RDF.li(1).asNode(), row2));
			expectedGraph.add(new Triple(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "11"),
					NodeFactory.createLiteral("21", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "12"),
					NodeFactory.createLiteral("22", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "13"),
					NodeFactory.createLiteral("23", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "14"),
					NodeFactory.createLiteral("24", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "15"),
					NodeFactory.createLiteral("25", XSDDatatype.XSDstring)));

			Node row3 = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(table, RDF.li(2).asNode(), row3));

			expectedGraph.add(new Triple(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "11"),
					NodeFactory.createLiteral("31", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "12"),
					NodeFactory.createLiteral("32", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "13"),
					NodeFactory.createLiteral("33", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "14"),
					NodeFactory.createLiteral("34", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "15"),
					NodeFactory.createLiteral("35", XSDDatatype.XSDstring)));

			Node row4 = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(table, RDF.li(3).asNode(), row4));
			expectedGraph.add(new Triple(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "11"),
					NodeFactory.createLiteral("41", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "12"),
					NodeFactory.createLiteral("42", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "13"),
					NodeFactory.createLiteral("43", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "14"),
					NodeFactory.createLiteral("44", XSDDatatype.XSDstring)));
			expectedGraph.add(new Triple(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "15"),
					NodeFactory.createLiteral("45", XSDDatatype.XSDstring)));

			if (!dg.getDefaultGraph().isIsomorphicWith(expectedGraph)) {
				ModelFactory.createModelForGraph(expectedGraph)
						.difference(ModelFactory.createModelForGraph(dg.getDefaultGraph())).write(System.out, "TTL");
			}

			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p)))
					.isIsomorphicWith(expectedGraph));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
