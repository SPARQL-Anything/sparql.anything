package com.github.spiceh2020.sparql.anything.html;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

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
//        Iterator<Quad> iter = dataset.find(null,null,null,null);
//        while(iter.hasNext()){
//            Quad t = iter.next();
//            System.err.println(t);
//        }
		Model m = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
		m.setNsPrefix("xhtml", "http://www.w3.org/1999/xhtml#");
		m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		m.write(System.out, "TTL");
	}

	@Test
	public void test2() throws URISyntaxException, IOException {
		DatasetGraph dataset = html2rdf.triplify(new URL(getTestLocation(name.getMethodName())), new Properties());
//        Iterator<Quad> iter = dataset.find(null,null,null,null);
//        while(iter.hasNext()){
//            Quad t = iter.next();
//            System.err.println(t);
//        }
		Model m = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
		m.setNsPrefix("xhtml", "http://www.w3.org/1999/xhtml#");
		m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		m.write(System.out, "TTL");
	}
}
