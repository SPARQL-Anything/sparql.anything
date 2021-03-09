package com.github.spiceh2020.sparql.anything.spreadsheet.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import com.github.spiceh2020.sparql.anything.spreadsheet.SpreadsheetTriplifier;

public class TestTriplifier {

	@Test
	public void test1() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book1.xls");
		Properties p = new Properties();
		DatasetGraph dg;
		try {
			dg = st.triplify(spreadsheet, p);

			String root = spreadsheet.toString() + "#";

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
	public void test2() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book1.xls");
		Properties p = new Properties();
		p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
		DatasetGraph dg;
		try {
			dg = st.triplify(spreadsheet, p);

			dg.find(null, null, null, null).forEachRemaining(q -> {
				assertTrue(!q.getSubject().isBlank());
				assertTrue(!q.getPredicate().isBlank());
				assertTrue(!q.getObject().isBlank());
				assertTrue(!q.getGraph().isBlank());
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
			dg = st.triplify(spreadsheet, p);

			dg.find(null, null, null, null).forEachRemaining(q -> {
				boolean condition = q.getPredicate().getURI().startsWith(namespace)
						|| q.getPredicate().getURI().startsWith(RDF.getURI());
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
			dg = st.triplify(spreadsheet, p);

			String root = spreadsheet.toString() + "#";

			Graph gs1 = GraphFactory.createGraphMem();
			Node s1Root = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

			Node s1Row2 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(1).asNode(), s1Row2));
			gs1.add(new Triple(s1Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "A"),
					NodeFactory.createLiteral("A1")));
			gs1.add(new Triple(s1Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "B"),
					NodeFactory.createLiteral("B1")));
			gs1.add(new Triple(s1Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "C"),
					NodeFactory.createLiteral("C1")));

			Node s1Row3 = NodeFactory.createBlankNode();
			gs1.add(new Triple(s1Root, RDF.li(2).asNode(), s1Row3));
			gs1.add(new Triple(s1Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "A"),
					NodeFactory.createLiteral("A2")));
			gs1.add(new Triple(s1Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "B"),
					NodeFactory.createLiteral("B2")));
			gs1.add(new Triple(s1Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "C"),
					NodeFactory.createLiteral("C2")));

			Graph gs2 = GraphFactory.createGraphMem();
			Node s2Root = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

			Node s2Row2 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(1).asNode(), s2Row2));
			gs2.add(new Triple(s2Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "A1"),
					NodeFactory.createLiteral("A11")));
			gs2.add(new Triple(s2Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "B1"),
					NodeFactory.createLiteral("B11")));
			gs2.add(new Triple(s2Row2, NodeFactory.createURI(Triplifier.XYZ_NS + "C1"),
					NodeFactory.createLiteral("C11")));

			Node s2Row3 = NodeFactory.createBlankNode();
			gs2.add(new Triple(s2Root, RDF.li(2).asNode(), s2Row3));
			gs2.add(new Triple(s2Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "A1"),
					NodeFactory.createLiteral("A12")));
			gs2.add(new Triple(s2Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "B1"),
					NodeFactory.createLiteral("B12")));
			gs2.add(new Triple(s2Row3, NodeFactory.createURI(Triplifier.XYZ_NS + "C1"),
					NodeFactory.createLiteral("C12")));

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
			dg = st.triplify(spreadsheet, p);

			String root = spreadsheet.toString() + "#";

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
}
