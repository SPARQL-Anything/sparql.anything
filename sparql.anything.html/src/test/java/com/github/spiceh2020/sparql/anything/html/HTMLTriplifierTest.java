package com.github.spiceh2020.sparql.anything.html;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import com.github.spiceh2020.sparql.anything.model.TriplifierHTTPException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;

public class HTMLTriplifierTest {
	public static final Logger log = LoggerFactory.getLogger(HTMLTriplifierTest.class);
	private HTMLTriplifier html2rdf = new HTMLTriplifier();

	@Rule
	public TestName name = new TestName();

	private String getTestLocation(String fileName) throws URISyntaxException {
		return getClass().getClassLoader().getResource(fileName + ".html").toURI().toString();
	}

	@Test
	public void test1() throws URISyntaxException, IOException, TriplifierHTTPException {
		Properties p = new Properties();
		p.setProperty(IRIArgument.LOCATION.toString(), new URL(getTestLocation(name.getMethodName())).toString());
		DatasetGraph dataset = html2rdf.triplify(p);
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
	public void test2() throws URISyntaxException, IOException, TriplifierHTTPException {
		Properties p = new Properties();
		p.setProperty(IRIArgument.LOCATION.toString(), new URL(getTestLocation(name.getMethodName())).toString());
		DatasetGraph dataset = html2rdf.triplify(p);
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
	public void testBN() throws TriplifierHTTPException {

		HTMLTriplifier st = new HTMLTriplifier();
		Properties p = new Properties();
		p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
		DatasetGraph dg;
		try {
			URL spreadsheet = new URL(getTestLocation(name.getMethodName()));
			p.setProperty(IRIArgument.LOCATION.toString(), spreadsheet.toString());
			dg = st.triplify(p);

			dg.find(null, null, null, null).forEachRemaining(q -> {
				log.info("{} {} {}", q.getSubject(), q.getPredicate(), q.getObject());
				assertTrue(!q.getSubject().isBlank());
				assertTrue(!q.getPredicate().isBlank());
				assertTrue(!q.getObject().isBlank());
				assertTrue(!q.getGraph().isBlank());
			});

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
