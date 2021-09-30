package com.github.spiceh2020.sparql.anything.docs.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.docs.DocxTriplifier;
import com.github.spiceh2020.sparql.anything.model.BaseFacadeXBuilder;
import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class TestTriplifier {

	@Test
	public void test1() {
		DocxTriplifier st = new DocxTriplifier();
		URL doc = st.getClass().getClassLoader().getResource("./testResources/doc1.docx");
		Properties p = new Properties();
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), doc.toString());
			dg = st.triplify(p, new BaseFacadeXBuilder(Triplifier.getLocation(p).toString(), p));

//			RDFDataMgr.write(System.out, dg, RDFFormat.NQ);

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral(
					"Title 11Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.2Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Title 23Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
					XSDDatatype.XSDstring)));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(doc.toString())).isIsomorphicWith(expectedGraph));

			p.setProperty(DocxTriplifier.KEEP_PARAGRAPH, "true");

			dg = st.triplify(p, new BaseFacadeXBuilder(Triplifier.getLocation(p).toString(), p));
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
			assertTrue(dg.getGraph(NodeFactory.createURI(doc.toString())).isIsomorphicWith(expectedGraph));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}