
package com.github.spiceh2020.sparql.anything.xml;


import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class XMLTriplifierTest {
    private XMLTriplifier triplifier = new XMLTriplifier();

    @Test
    public void test1() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("baseNamespace", "http://www.example.org#");
//        properties.setProperty("uriRoot", "http://www.example.org#");
        URL xml1 = getClass().getClassLoader().getResource("./test1.xml");
        DatasetGraph graph = triplifier.triplify(xml1, properties);
        Iterator<Quad> iter = graph.find(null,null,null,null);
        while(iter.hasNext()){
            Quad t = iter.next();
            System.err.println(t);
        }
    }
}
