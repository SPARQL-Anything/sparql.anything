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

package com.github.sparqlanything.spreadsheet.test;

import com.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.spreadsheet.SpreadsheetTriplifier;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.*;

public class TestTriplifier {

	@Test
	public void test1() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book1.xls");
		Properties p = new Properties();
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), spreadsheet.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(spreadsheet.toString(), p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();

			String root = spreadsheet + "#";

			Graph gs1 = GraphFactory.createGraphMem();
			Node s1Root = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

//			RDFDataMgr.write(System.out, dg, RDFFormat.TRIG_PRETTY);

			Node s1Row1 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(1).asNode(), s1Row1));
			gs1.add(new Triple(s1Row1, RDF.li(1).asNode(), NodeFactory.createLiteral("A")));
			gs1.add(new Triple(s1Row1, RDF.li(2).asNode(), NodeFactory.createLiteral("B")));
			gs1.add(new Triple(s1Row1, RDF.li(3).asNode(), NodeFactory.createLiteral("C")));

			Node s1Row2 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(2).asNode(), s1Row2));
			gs1.add(new Triple(s1Row2, RDF.li(1).asNode(), NodeFactory.createLiteral("A1")));
			gs1.add(new Triple(s1Row2, RDF.li(2).asNode(), NodeFactory.createLiteral("B1")));
			gs1.add(new Triple(s1Row2, RDF.li(3).asNode(), NodeFactory.createLiteral("C1")));

			Node s1Row3 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(3).asNode(), s1Row3));
			gs1.add(new Triple(s1Row3, RDF.li(1).asNode(), NodeFactory.createLiteral("A2")));
			gs1.add(new Triple(s1Row3, RDF.li(2).asNode(), NodeFactory.createLiteral("B2")));
			gs1.add(new Triple(s1Row3, RDF.li(3).asNode(), NodeFactory.createLiteral("C2")));

			Graph gs2 = GraphFactory.createGraphMem();
			Node s2Root = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

			Node s2Row1 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(1).asNode(), s2Row1));
			gs2.add(new Triple(s2Row1, RDF.li(1).asNode(), NodeFactory.createLiteral("A1")));
			gs2.add(new Triple(s2Row1, RDF.li(2).asNode(), NodeFactory.createLiteral("B1")));
			gs2.add(new Triple(s2Row1, RDF.li(3).asNode(), NodeFactory.createLiteral("C1")));

			Node s2Row2 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(2).asNode(), s2Row2));
			gs2.add(new Triple(s2Row2, RDF.li(1).asNode(), NodeFactory.createLiteral("A11")));
			gs2.add(new Triple(s2Row2, RDF.li(2).asNode(), NodeFactory.createLiteral("B11")));
			gs2.add(new Triple(s2Row2, RDF.li(3).asNode(), NodeFactory.createLiteral("C11")));

			Node s2Row3 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(3).asNode(), s2Row3));
			gs2.add(new Triple(s2Row3, RDF.li(1).asNode(), NodeFactory.createLiteral("A12")));
			gs2.add(new Triple(s2Row3, RDF.li(2).asNode(), NodeFactory.createLiteral("B12")));
			gs2.add(new Triple(s2Row3, RDF.li(3).asNode(), NodeFactory.createLiteral("C12")));

//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);
//			ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet1"))).write(System.out,"TTL");
//			ModelFactory.createModelForGraph(gs1).difference(ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet1")))).write(System.out,"TTL");

//			ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet2"))).write(System.out,"TTL");
//			ModelFactory.createModelForGraph(gs2).write(System.out, "TTL");
//			ModelFactory.createModelForGraph(gs2).difference(ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet2")))).write(System.out,"TTL");
//			System.out.println(ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet1"))).write(System.err, "TTL"));
			assertTrue(dg.getGraph(NodeFactory.createURI(root + "Sheet1")).isIsomorphicWith(gs1));
			assertTrue(dg.getGraph(NodeFactory.createURI(root + "Sheet2")).isIsomorphicWith(gs2));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book1.xls");
		Properties p = new Properties();
		p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
		DatasetGraph dg;
		try {

			p.setProperty(IRIArgument.LOCATION.toString(), spreadsheet.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(spreadsheet.toString(), p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();
			dg.find(null, null, null, null).forEachRemaining(q -> {
				assertFalse(q.getSubject().isBlank());
				assertFalse(q.getPredicate().isBlank());
				assertFalse(q.getObject().isBlank());
				assertFalse(q.getGraph().isBlank());
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book1.xls");
		Properties p = new Properties();
		String namespace = "http://example.org/namespace/";
		p.setProperty(IRIArgument.NAMESPACE.toString(), namespace);
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), spreadsheet.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(spreadsheet.toString(), p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();

			dg.find(null, null, null, null).forEachRemaining(q -> {
				boolean condition = q.getPredicate().getURI().startsWith(namespace) || q.getPredicate().getURI().startsWith(RDF.getURI());
				assertTrue(condition);
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book1.xls");
		Properties p = new Properties();
		p.setProperty(SpreadsheetTriplifier.PROPERTY_HEADERS, "true");
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), spreadsheet.toString());
			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(spreadsheet.toString(), p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();

			String root = spreadsheet + "#";

			Graph gs1 = GraphFactory.createGraphMem();
			Node s1Root = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

			Node s1Row2 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(1).asNode(), s1Row2));
			gs1.add(new Triple(s1Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "A"), NodeFactory.createLiteral("A1")));
			gs1.add(new Triple(s1Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "B"), NodeFactory.createLiteral("B1")));
			gs1.add(new Triple(s1Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "C"), NodeFactory.createLiteral("C1")));

			Node s1Row3 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(2).asNode(), s1Row3));
			gs1.add(new Triple(s1Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "A"), NodeFactory.createLiteral("A2")));
			gs1.add(new Triple(s1Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "B"), NodeFactory.createLiteral("B2")));
			gs1.add(new Triple(s1Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "C"), NodeFactory.createLiteral("C2")));

			Graph gs2 = GraphFactory.createGraphMem();
			Node s2Root = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

			Node s2Row2 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(1).asNode(), s2Row2));
			gs2.add(new Triple(s2Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "A1"), NodeFactory.createLiteral("A11")));
			gs2.add(new Triple(s2Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "B1"), NodeFactory.createLiteral("B11")));
			gs2.add(new Triple(s2Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "C1"), NodeFactory.createLiteral("C11")));

			Node s2Row3 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(2).asNode(), s2Row3));
			gs2.add(new Triple(s2Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "A1"), NodeFactory.createLiteral("A12")));
			gs2.add(new Triple(s2Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "B1"), NodeFactory.createLiteral("B12")));
			gs2.add(new Triple(s2Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "C1"), NodeFactory.createLiteral("C12")));

//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);
//			ModelFactory.createModelForGraph(gs1).write(System.out, "TTL");
//			ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet1"))).write(System.out,
//					"TTL");
//			ModelFactory.createModelForGraph(gs1).difference(ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet1")))).write(System.out,"TTL");
//			ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet1"))).difference(ModelFactory.createModelForGraph(gs1)).write(System.out,"TTL");

//			ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet2"))).write(System.out,"TTL");
//			ModelFactory.createModelForGraph(gs2).write(System.out, "TTL");
//			ModelFactory.createModelForGraph(gs2).difference(ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet2")))).write(System.out,"TTL");

			assertTrue(dg.getGraph(NodeFactory.createURI(root + "Sheet1")).isIsomorphicWith(gs1));
			assertTrue(dg.getGraph(NodeFactory.createURI(root + "Sheet2")).isIsomorphicWith(gs2));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test5() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book1.xlsx");
		Properties p = new Properties();
		DatasetGraph dg;
		try {

			p.setProperty(IRIArgument.LOCATION.toString(), spreadsheet.toString());

			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(spreadsheet.toString(), p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();

			String root = spreadsheet + "#";

			Graph gs1 = GraphFactory.createGraphMem();
			Node s1Root = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

			Node s1Row1 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(1).asNode(), s1Row1));
			gs1.add(new Triple(s1Row1, RDF.li(1).asNode(), NodeFactory.createLiteral("A")));
			gs1.add(new Triple(s1Row1, RDF.li(2).asNode(), NodeFactory.createLiteral("B")));
			gs1.add(new Triple(s1Row1, RDF.li(3).asNode(), NodeFactory.createLiteral("C")));

			Node s1Row2 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(2).asNode(), s1Row2));
			gs1.add(new Triple(s1Row2, RDF.li(1).asNode(), NodeFactory.createLiteral("A1")));
			gs1.add(new Triple(s1Row2, RDF.li(2).asNode(), NodeFactory.createLiteral("B1")));
			gs1.add(new Triple(s1Row2, RDF.li(3).asNode(), NodeFactory.createLiteral("C1")));

			Node s1Row3 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(3).asNode(), s1Row3));
			gs1.add(new Triple(s1Row3, RDF.li(1).asNode(), NodeFactory.createLiteral("A2")));
			gs1.add(new Triple(s1Row3, RDF.li(2).asNode(), NodeFactory.createLiteral("B2")));
			gs1.add(new Triple(s1Row3, RDF.li(3).asNode(), NodeFactory.createLiteral("C2")));

			Graph gs2 = GraphFactory.createGraphMem();
			Node s2Root = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

			Node s2Row1 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(1).asNode(), s2Row1));
			gs2.add(new Triple(s2Row1, RDF.li(1).asNode(), NodeFactory.createLiteral("A1")));
			gs2.add(new Triple(s2Row1, RDF.li(2).asNode(), NodeFactory.createLiteral("B1")));
			gs2.add(new Triple(s2Row1, RDF.li(3).asNode(), NodeFactory.createLiteral("C1")));

			Node s2Row2 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(2).asNode(), s2Row2));
			gs2.add(new Triple(s2Row2, RDF.li(1).asNode(), NodeFactory.createLiteral("A11")));
			gs2.add(new Triple(s2Row2, RDF.li(2).asNode(), NodeFactory.createLiteral("B11")));
			gs2.add(new Triple(s2Row2, RDF.li(3).asNode(), NodeFactory.createLiteral("C11")));

			Node s2Row3 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(3).asNode(), s2Row3));
			gs2.add(new Triple(s2Row3, RDF.li(1).asNode(), NodeFactory.createLiteral("A12")));
			gs2.add(new Triple(s2Row3, RDF.li(2).asNode(), NodeFactory.createLiteral("B12")));
			gs2.add(new Triple(s2Row3, RDF.li(3).asNode(), NodeFactory.createLiteral("C12")));

//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);
//			ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet1"))).write(System.out,"TTL");
//			ModelFactory.createModelForGraph(gs1).difference(ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet1")))).write(System.out,"TTL");

//			ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet2"))).write(System.out,"TTL");
//			ModelFactory.createModelForGraph(gs2).write(System.out, "TTL");
//			ModelFactory.createModelForGraph(gs2).difference(ModelFactory.createModelForGraph(dg.getGraph(NodeFactory.createURI(root + "Sheet2")))).write(System.out,"TTL");

			assertTrue(dg.getGraph(NodeFactory.createURI(root + "Sheet1")).isIsomorphicWith(gs1));
			assertTrue(dg.getGraph(NodeFactory.createURI(root + "Sheet2")).isIsomorphicWith(gs2));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void testCellLink() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book2.xlsx");
		Properties p = new Properties();
		p.setProperty(SpreadsheetTriplifier.PROPERTY_COMPOSITE_VALUES, "true");
		DatasetGraph dg;
		try {

			p.setProperty(IRIArgument.LOCATION.toString(), spreadsheet.toString());

			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(spreadsheet.toString(), p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();

//			RDFDataMgr.write(System.out, dg, Lang.TRIG);

			String prefixes = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
			String[] asks = new String[]{" ASK {[ a xyz:STRING ; rdf:_1 \"C12\" ; xyz:address \"http://www.example.org/C12\" ; xyz:label \"C12\" ]}", " ASK {[ a xyz:STRING ; rdf:_1 \"B\" ]}", " ASK {[ a xyz:STRING ; rdf:_1 \"B12\" ; xyz:author ?author ; xyz:threadedComment ?tc  ] FILTER(REGEX(?tc, \"This is a comment\"))  }",};

			for (String ask : asks) {
				try {
					assertTrue(QueryExecutionFactory.create(QueryFactory.create(prefixes + ask), dg).execAsk());
				} catch (Exception e) {
					fail(prefixes + ask);
					fail(e.getMessage());
				}
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
