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

package io.github.sparqlanything.docs.test;

import io.github.sparqlanything.docs.DocxTriplifier;
import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Triplifier;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestTriplifier {
	final static Logger logger = LoggerFactory.getLogger(TestTriplifier.class);
	@Ignore
	@Test
	public void test1() {
		DocxTriplifier st = new DocxTriplifier();
		URL doc = st.getClass().getClassLoader().getResource("./testResources/doc1.docx");
		Properties p = new Properties();
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), Objects.requireNonNull(doc).toString());
			FacadeXGraphBuilder b =new BaseFacadeXGraphBuilder(p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();
//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(Triple.create(n, RDF.li(1).asNode(), NodeFactory.createLiteralString(
					"Title 11Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.2Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Title 23Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")));
			Iterator<Node> graphNodes = dg.listGraphNodes();
			while (graphNodes.hasNext()) {
				System.err.println(graphNodes.next());
			}
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p)))
					.isIsomorphicWith(expectedGraph));

			p.setProperty(DocxTriplifier.MERGE_PARAGRAPHS.toString(), "true");

			b = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();
//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			expectedGraph = GraphFactory.createGraphMem();
			n = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(
					Triple.create(n, RDF.li(1).asNode(), NodeFactory.createLiteralString("Title 1")));
			expectedGraph.add(Triple.create(n, RDF.li(2).asNode(), NodeFactory.createLiteralString(
					"1Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")));
			expectedGraph.add(Triple.create(n, RDF.li(3).asNode(), NodeFactory.createLiteralString(
					"2Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")));
			expectedGraph.add(
					Triple.create(n, RDF.li(4).asNode(), NodeFactory.createLiteralString("Title 2")));
			expectedGraph.add(Triple.create(n, RDF.li(5).asNode(), NodeFactory.createLiteralString(
					"3Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")));
			expectedGraph.add(Triple.create(n, RDF.li(6).asNode(), NodeFactory.createLiteralString("")));

			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p)))
					.isIsomorphicWith(expectedGraph));

		} catch (IOException e) {
			logger.error("",e);
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
			p.setProperty(IRIArgument.LOCATION.toString(), Objects.requireNonNull(doc).toString());
			p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");

			FacadeXGraphBuilder b =new BaseFacadeXGraphBuilder(p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();
			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			dg.find(null, null, null, null).forEachRemaining(q -> {
				assertFalse(q.getGraph().isBlank());
				assertFalse(q.getSubject().isBlank());
				assertFalse(q.getPredicate().isBlank());
				assertFalse(q.getObject().isBlank());
			});

//			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
//			assertTrue(dg.getGraph(NodeFactory.createURI(doc.toString())).isIsomorphicWith(expectedGraph));

		} catch (IOException e) {
			logger.error("",e);
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

			p.setProperty(IRIArgument.LOCATION.toString(), Objects.requireNonNull(doc).toString());

			FacadeXGraphBuilder b =new BaseFacadeXGraphBuilder(p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();

			RDFDataMgr.write(System.out, dg, RDFFormat.TRIG_PRETTY);

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(Triple.create(n, RDF.li(1).asNode(),
					NodeFactory.createLiteralString("Title 1Paragraph1Title 2Paragraph2")));

			Node table = NodeFactory.createBlankNode();

			expectedGraph.add(Triple.create(n, RDF.li(2).asNode(), table));

			Node row1 = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(table, RDF.li(1).asNode(), row1));
			expectedGraph
					.add(Triple.create(row1, RDF.li(1).asNode(), NodeFactory.createLiteralString("11")));
			expectedGraph
					.add(Triple.create(row1, RDF.li(2).asNode(), NodeFactory.createLiteralString("12")));
			expectedGraph
					.add(Triple.create(row1, RDF.li(3).asNode(), NodeFactory.createLiteralString("13")));
			expectedGraph
					.add(Triple.create(row1, RDF.li(4).asNode(), NodeFactory.createLiteralString("14")));
			expectedGraph
					.add(Triple.create(row1, RDF.li(5).asNode(), NodeFactory.createLiteralString("15")));

			Node row2 = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(table, RDF.li(2).asNode(), row2));
			expectedGraph
					.add(Triple.create(row2, RDF.li(1).asNode(), NodeFactory.createLiteralString("21")));
			expectedGraph
					.add(Triple.create(row2, RDF.li(2).asNode(), NodeFactory.createLiteralString("22")));
			expectedGraph
					.add(Triple.create(row2, RDF.li(3).asNode(), NodeFactory.createLiteralString("23")));
			expectedGraph
					.add(Triple.create(row2, RDF.li(4).asNode(), NodeFactory.createLiteralString("24")));
			expectedGraph
					.add(Triple.create(row2, RDF.li(5).asNode(), NodeFactory.createLiteralString("25")));

			Node row3 = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(table, RDF.li(3).asNode(), row3));
			expectedGraph
					.add(Triple.create(row3, RDF.li(1).asNode(), NodeFactory.createLiteralString("31")));
			expectedGraph
					.add(Triple.create(row3, RDF.li(2).asNode(), NodeFactory.createLiteralString("32")));
			expectedGraph
					.add(Triple.create(row3, RDF.li(3).asNode(), NodeFactory.createLiteralString("33")));
			expectedGraph
					.add(Triple.create(row3, RDF.li(4).asNode(), NodeFactory.createLiteralString("34")));
			expectedGraph
					.add(Triple.create(row3, RDF.li(5).asNode(), NodeFactory.createLiteralString("35")));

			Node row4 = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(table, RDF.li(4).asNode(), row4));
			expectedGraph
					.add(Triple.create(row4, RDF.li(1).asNode(), NodeFactory.createLiteralString("41")));
			expectedGraph
					.add(Triple.create(row4, RDF.li(2).asNode(), NodeFactory.createLiteralString("42")));
			expectedGraph
					.add(Triple.create(row4, RDF.li(3).asNode(), NodeFactory.createLiteralString("43")));
			expectedGraph
					.add(Triple.create(row4, RDF.li(4).asNode(), NodeFactory.createLiteralString("44")));
			expectedGraph
					.add(Triple.create(row4, RDF.li(5).asNode(), NodeFactory.createLiteralString("45")));

			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p)))
					.isIsomorphicWith(expectedGraph));

			if (!dg.getDefaultGraph().isIsomorphicWith(expectedGraph)) {
				ModelFactory.createModelForGraph(expectedGraph)
						.difference(ModelFactory.createModelForGraph(dg.getDefaultGraph())).write(System.out, "TTL");
			}

		} catch (IOException e) {
			logger.error("",e);
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
			p.setProperty(IRIArgument.LOCATION.toString(), Objects.requireNonNull(doc).toString());
			p.setProperty(DocxTriplifier.TABLE_HEADERS.toString(), "true");
			FacadeXGraphBuilder b =new BaseFacadeXGraphBuilder(p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();

//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(Triple.create(n, RDF.li(1).asNode(),
					NodeFactory.createLiteralString("Title 1Paragraph1Title 2Paragraph2")));

			Node table = NodeFactory.createBlankNode();

			expectedGraph.add(Triple.create(n, RDF.li(2).asNode(), table));

//			NodeFactory.createURI(Triplifier.XYZ_NS+"11")

			Node row2 = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(table, RDF.li(1).asNode(), row2));
			expectedGraph.add(Triple.create(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "11"),
					NodeFactory.createLiteralString("21")));
			expectedGraph.add(Triple.create(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "12"),
					NodeFactory.createLiteralString("22")));
			expectedGraph.add(Triple.create(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "13"),
					NodeFactory.createLiteralString("23")));
			expectedGraph.add(Triple.create(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "14"),
					NodeFactory.createLiteralString("24")));
			expectedGraph.add(Triple.create(row2, NodeFactory.createURI(Triplifier.XYZ_NS + "15"),
					NodeFactory.createLiteralString("25")));

			Node row3 = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(table, RDF.li(2).asNode(), row3));

			expectedGraph.add(Triple.create(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "11"),
					NodeFactory.createLiteralString("31")));
			expectedGraph.add(Triple.create(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "12"),
					NodeFactory.createLiteralString("32")));
			expectedGraph.add(Triple.create(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "13"),
					NodeFactory.createLiteralString("33")));
			expectedGraph.add(Triple.create(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "14"),
					NodeFactory.createLiteralString("34")));
			expectedGraph.add(Triple.create(row3, NodeFactory.createURI(Triplifier.XYZ_NS + "15"),
					NodeFactory.createLiteralString("35")));

			Node row4 = NodeFactory.createBlankNode();
			expectedGraph.add(Triple.create(table, RDF.li(3).asNode(), row4));
			expectedGraph.add(Triple.create(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "11"),
					NodeFactory.createLiteralString("41")));
			expectedGraph.add(Triple.create(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "12"),
					NodeFactory.createLiteralString("42")));
			expectedGraph.add(Triple.create(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "13"),
					NodeFactory.createLiteralString("43")));
			expectedGraph.add(Triple.create(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "14"),
					NodeFactory.createLiteralString("44")));
			expectedGraph.add(Triple.create(row4, NodeFactory.createURI(Triplifier.XYZ_NS + "15"),
					NodeFactory.createLiteralString("45")));

			if (!dg.getDefaultGraph().isIsomorphicWith(expectedGraph)) {
				ModelFactory.createModelForGraph(expectedGraph)
						.difference(ModelFactory.createModelForGraph(dg.getDefaultGraph())).write(System.out, "TTL");
			}

			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(Triplifier.getRootArgument(p)))
					.isIsomorphicWith(expectedGraph));

		} catch (IOException e) {
			logger.error("",e);
		}
	}

}
