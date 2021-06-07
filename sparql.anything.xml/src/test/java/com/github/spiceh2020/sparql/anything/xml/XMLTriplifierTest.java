
package com.github.spiceh2020.sparql.anything.xml;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class XMLTriplifierTest {
	private XMLTriplifier triplifier = new XMLTriplifier();
	public static Logger log = LoggerFactory.getLogger(XMLTriplifierTest.class);

	@Test
	public void test1() throws IOException {
		Properties properties = new Properties();
		properties.setProperty("baseNamespace", "http://www.example.org#");

		URL xml1 = getClass().getClassLoader().getResource("./test1.xml");
		properties.setProperty(IRIArgument.LOCATION.toString(), xml1.toString());

		DatasetGraph graph = triplifier.triplify(properties);
		Iterator<Quad> iter = graph.find(null, null, RDF.type.asNode(),
				NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
		Assert.assertTrue(iter.hasNext());
	}

	@Test
	public void testBNodesFalse() throws IOException {
		Properties properties = new Properties();
		properties.setProperty("baseNamespace", "http://www.example.org#");
		properties.setProperty("blank-nodes", "false");
		URL xml1 = getClass().getClassLoader().getResource("./test1.xml");
		properties.setProperty(IRIArgument.LOCATION.toString(), xml1.toString());
		DatasetGraph graph = triplifier.triplify(properties);
//        ModelFactory.createModelForGraph(graph.getDefaultGraph()).write(System.out,"TTL");
		Iterator<Quad> iter = graph.find(null, null, null, null);
		while (iter.hasNext()) {
			Quad q = iter.next();
			log.info("{} {} {}", q.getSubject(), q.getPredicate(), q.getObject());
			Assert.assertFalse(q.getSubject().isBlank());
			Assert.assertFalse(q.getObject().isBlank());
		}

	}
}
