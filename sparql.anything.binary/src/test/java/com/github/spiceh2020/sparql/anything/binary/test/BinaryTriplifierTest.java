package com.github.spiceh2020.sparql.anything.binary.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.binary.BinaryTriplifier;

public class BinaryTriplifierTest {

	@Test
	public void testBase64() throws MalformedURLException {
		BinaryTriplifier bt = new BinaryTriplifier();
		File f = new File("src/main/resources/testfile");
		URL url = f.toURI().toURL();
		try {
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), url.toString());
			DatasetGraph dg = bt.triplify(p);
			Graph expectedGraph = GraphFactory.createGraphMem();
			Node n = NodeFactory.createBlankNode();
			expectedGraph.add(new Triple(n,RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
			expectedGraph.add(new Triple(n, RDF.li(1).asNode(),
					NodeFactory.createLiteral("dGhpcyBpcyBhIHRlc3Q=", XSDDatatype.XSDbase64Binary)));
			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
			assertTrue(dg.getGraph(NodeFactory.createURI(url.toString())).isIsomorphicWith(expectedGraph));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
