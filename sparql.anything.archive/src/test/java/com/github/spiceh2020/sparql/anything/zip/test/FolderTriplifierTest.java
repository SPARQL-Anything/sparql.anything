package com.github.spiceh2020.sparql.anything.zip.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.zip.FolderTriplifier;

public class FolderTriplifierTest {

	@Test
	public void test1() throws MalformedURLException {
		FolderTriplifier tt = new FolderTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test").toURI().toURL();
			DatasetGraph dg = tt.triplify(url, new Properties());

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Set<String> expected = new HashSet<>();

			expected.add("");
			expected.add("test.csv");
			expected.add("test.json");
			expected.add("test.xml");
			expected.add("test.txt");

			Set<String> actual = new HashSet<>();
			dg.find(null, null, null, null).forEachRemaining(q -> {
				if (q.getObject().isLiteral()) {
					try {
						actual.add(q.getObject().getLiteralLexicalForm()
								.replace(Paths.get(url.toURI()).toUri().toString(), ""));
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			});

			assertEquals(expected, actual);

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBN() throws MalformedURLException {
		FolderTriplifier tt = new FolderTriplifier();
		try {
			URL url = getClass().getClassLoader().getResource("test").toURI().toURL();
			Properties p = new Properties();
			p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
			DatasetGraph dg = tt.triplify(url, p);

//			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");

			Set<String> expected = new HashSet<>();

			expected.add("");
			expected.add("test.csv");
			expected.add("test.json");
			expected.add("test.xml");
			expected.add("test.txt");

			Set<String> actual = new HashSet<>();
			dg.find(null, NodeFactory.createURI(url.toString() + "#"), null, null).forEachRemaining(q -> {
				if (q.getObject().isLiteral()) {
					try {
						actual.add(q.getObject().getLiteralLexicalForm()
								.replace(Paths.get(url.toURI()).toUri().toString(), ""));
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			});

			assertEquals(expected, actual);

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

//	@Test
//	public void testBNNODE() throws MalformedURLException {
//		ZipTriplifier tt = new ZipTriplifier();
//		try {
//			URL url = getClass().getClassLoader().getResource("test.zip").toURI().toURL();
//			Properties p = new Properties();
//			p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
//			DatasetGraph dg = tt.triplify(url, p);
//
////			ModelFactory.createModelForGraph(dg.getDefaultGraph()).write(System.out, "TTL");
//
//			Graph expectedGraph = GraphFactory.createGraphMem();
//			Node n = NodeFactory.createURI(
//					url.toString()+"#");
//			expectedGraph.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
//			expectedGraph.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteral(url + "#test.csv")));
//			expectedGraph.add(new Triple(n, RDF.li(2).asNode(), NodeFactory.createLiteral(url + "#test.json")));
//			expectedGraph.add(new Triple(n, RDF.li(3).asNode(), NodeFactory.createLiteral(url + "#test.xml")));
//			expectedGraph.add(new Triple(n, RDF.li(4).asNode(), NodeFactory.createLiteral(url + "#test.txt")));
//			assertTrue(dg.getDefaultGraph().isIsomorphicWith(expectedGraph));
//			assertTrue(dg.getGraph(NodeFactory.createURI(url.toString())).isIsomorphicWith(expectedGraph));
//
//		} catch (IOException | URISyntaxException e) {
//			e.printStackTrace();
//		}
//	}

}
