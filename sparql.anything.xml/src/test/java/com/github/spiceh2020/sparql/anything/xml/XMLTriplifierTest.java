
package com.github.spiceh2020.sparql.anything.xml;


import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class XMLTriplifierTest {
    private XMLTriplifier triplifier = new XMLTriplifier();

    @Test
    public void test1() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("baseNamespace", "http://www.example.org#");
        URL xml1 = getClass().getClassLoader().getResource("./test1.xml");
        DatasetGraph graph = triplifier.triplify(xml1, properties);
        Iterator<Quad> iter = graph.find(null,null, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
        Assert.assertTrue(iter.hasNext());
    }


    @Test
    public void testBNodesFalse() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("baseNamespace", "http://www.example.org#");
        properties.setProperty("blank-nodes", "false");
        URL xml1 = getClass().getClassLoader().getResource("./test1.xml");
        DatasetGraph graph = triplifier.triplify(xml1, properties);
//        ModelFactory.createModelForGraph(graph.getDefaultGraph()).write(System.out,"TTL");
        Iterator<Quad> iter = graph.find(null,null, null, null);
        while(iter.hasNext()){
            Assert.assertFalse(iter.next().getSubject().isBlank());
        }

    }
}
