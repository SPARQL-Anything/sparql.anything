package com.github.spiceh2020.sparql.anything.zip;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class FolderTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(FolderTriplifier.class);

	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return dg;

		String root = Triplifier.getRootArgument(properties, url);
		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);
		String matches = properties.getProperty(ZipTriplifier.MATCHES, ".*");

		logger.trace("BN nodes {}", blank_nodes);
		logger.trace("Matches {}", matches);

		Node rootResource;
		if (!blank_nodes) {
			if (root == null) {
				rootResource = NodeFactory.createURI(url.toString());
			} else {
				rootResource = NodeFactory.createURI(root);
			}
		} else {
			rootResource = NodeFactory.createBlankNode();
		}

		Graph g = GraphFactory.createDefaultGraph();
		g.add(new Triple(rootResource, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

		try {
			Path path = Paths.get(url.toURI());
			AtomicInteger i = new AtomicInteger(1);
			Files.walk(path).forEach(p -> {
				logger.trace("{} matches? {}", p.toString(), path.toString().matches(matches));
				if (p.toString().matches(matches)) {
					g.add(new Triple(rootResource, RDF.li(i.getAndIncrement()).asNode(),
							NodeFactory.createLiteral(p.toUri().toString())));
				}

			});

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		dg.setDefaultGraph(g);
		dg.addGraph(NodeFactory.createURI(url.toString()), g);

		return dg;
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet();
	}
}
