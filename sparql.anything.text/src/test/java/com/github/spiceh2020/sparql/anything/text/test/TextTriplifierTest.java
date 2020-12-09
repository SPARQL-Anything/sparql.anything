package com.github.spiceh2020.sparql.anything.text.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.text.TextTriplifier;

public class TextTriplifierTest {

	@Test
	public void test1() throws MalformedURLException {
		TextTriplifier tt = new TextTriplifier();
		File f = new File("src/main/resources/testfile");
		URL url = f.toURI().toURL();
		try {
			DatasetGraph dg = tt.triplify(url, new Properties());
			Graph expectedGraph = GraphFactory.createGraphMem();
			expectedGraph.add(new Triple(NodeFactory.createBlankNode(), RDF.value.asNode(),
					NodeFactory.createLiteral("this is a test", XSDDatatype.XSDstring)));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(url.toString())).isIsomorphicWith(expectedGraph));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
