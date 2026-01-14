package io.github.sparqlanything.cli;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class RiotUtilsTest {

	@Test
	public void datasetToResultSetTest() throws UnsupportedEncodingException {
		DatasetGraph dsg = DatasetGraphFactory.create();
		dsg.add(NodeFactory.createURI("http://example.org/graph1"), NodeFactory.createURI("http://example.org/something1a"), RDF.type.asNode(), RDFS.Resource.asNode());
		dsg.add(NodeFactory.createURI("http://example.org/graph1"), NodeFactory.createURI("http://example.org/something1b"), RDF.type.asNode(), RDFS.Resource.asNode());
		dsg.add(NodeFactory.createURI("http://example.org/graph2"), NodeFactory.createURI("http://example.org/something2"), RDF.type.asNode(), RDFS.Resource.asNode());
		ResultSet rs = RiotUtils.asResultSet(dsg);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final String utf8 = StandardCharsets.UTF_8.name();
		try (PrintStream ps = new PrintStream(baos, true, utf8)) {
			ResultSetFormatter.outputAsCSV(ps, rs);
		}
		String data = baos.toString(utf8);
		int count = data.length() - data.replace(",", "").length();
		// There must be 12 commas
		Assert.assertEquals(12, count);
	}

}
