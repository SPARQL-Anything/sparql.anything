package com.github.spiceh2020.sparql.anything.zip.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
import com.github.spiceh2020.sparql.anything.zip.TarTriplifier;

public class TarTriplifierTest {

	@Test
	public void test1() throws MalformedURLException {
		TarTriplifier tt = new TarTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.tar").toURI().toURL();
			DatasetGraph dg = tt.triplify(url, new Properties());

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral(url + "#test/test.csv")));
			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral(url + "#test/test.json")));
			expectedGraph.add(new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral(url + "#test/test.xml")));
			expectedGraph.add(new Triple(n, RDF.li(5).asNode(), NodeFactory.createLiteral(url + "#test/test.txt")));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral(url + "#test/")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(url.toString())).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBNNODE() throws MalformedURLException {
		TarTriplifier tt = new TarTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test.tar").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
			DatasetGraph dg = tt.triplify(url, p);

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createURI(url.toString() + "#");
			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral(url + "#test/test.csv")));
			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral(url + "#test/test.json")));
			expectedGraph.add(new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral(url + "#test/test.xml")));
			expectedGraph.add(new Triple(n, RDF.li(5).asNode(), NodeFactory.createLiteral(url + "#test/test.txt")));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral(url + "#test/")));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(url.toString())).isIsomorphicWith(expectedGraph));

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
