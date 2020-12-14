package com.github.spiceh2020.sparql.anything.html;

import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class HTMLTriplifierTest {
    private HTMLTriplifier html2rdf = new HTMLTriplifier();

    @Rule
    public TestName name = new TestName();

    private String getTestLocation(String fileName) throws URISyntaxException {
        return getClass().getClassLoader().getResource(fileName + ".html").toURI().toString();
    }

    @Test
    public void test1() throws URISyntaxException, IOException {
        DatasetGraph dataset = html2rdf.triplify(new URL(getTestLocation(name.getMethodName())), new Properties());
        Iterator<Quad> iter = dataset.find(null,null,null,null);
        while(iter.hasNext()){
            Quad t = iter.next();
            System.err.println(t);
        }
    }
}
