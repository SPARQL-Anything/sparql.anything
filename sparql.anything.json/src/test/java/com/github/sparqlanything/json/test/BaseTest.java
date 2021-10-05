package com.github.sparqlanything.json.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.sparqlanything.json.JSONTriplifier;
import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;

public class BaseTest {

	@Test
	public void testContent() throws TriplifierHTTPException {

		Triplifier jt = new JSONTriplifier();

		try {
			Properties p1 = new Properties();
			p1.setProperty(IRIArgument.CONTENT.toString(), "{\"a\":\"b\"}");
			DatasetGraph g1 = jt.triplify(p1, new BaseFacadeXBuilder("test", p1));
//			RDFDataMgr.write(System.out, g1, RDFFormat.TRIG);
			Graph expected = GraphFactory.createDefaultGraph();
			Node n = NodeFactory.createBlankNode();
			expected.add(n, NodeFactory.createURI(Triplifier.XYZ_NS + "a"), NodeFactory.createLiteral("b"));
			expected.add(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
			
			assertTrue(g1.getDefaultGraph().isIsomorphicWith(expected));

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}
