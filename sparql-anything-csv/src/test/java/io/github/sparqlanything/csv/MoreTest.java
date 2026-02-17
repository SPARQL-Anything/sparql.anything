package io.github.sparqlanything.csv;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import static org.junit.Assert.fail;

public class MoreTest {
	private final CSVTriplifier triplifier = new CSVTriplifier();
	@Test
	public void issue612() throws TriplifierHTTPException, IOException {
		Properties properties = new Properties();
		URL csv1 = getClass().getClassLoader().getResource("./other.csv");
		assert csv1 != null;
		properties.setProperty(IRIArgument.LOCATION.toString(), csv1.toString());
		properties.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
		BasicPattern bp = new BasicPattern();
		bp.add(Triple.create(NodeFactory.createVariable("s"), NodeFactory.createVariable("p"),
			NodeFactory.createVariable("o")));
		FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(properties);
		triplifier.triplify(properties, b);
		DatasetGraph graph = b.getDatasetGraph();
		// with csv.null-string set to nullString we should not see any quads with nullString in the object position
		Iterator<Triple> tripleIterator = graph.getDefaultGraph().find(Node.ANY, Node.ANY, Node.ANY);
		while(tripleIterator.hasNext()) {
			System.err.println(tripleIterator.next());
		}

	}
}
