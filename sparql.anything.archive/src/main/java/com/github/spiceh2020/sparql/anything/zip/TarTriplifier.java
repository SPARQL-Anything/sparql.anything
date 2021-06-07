package com.github.spiceh2020.sparql.anything.zip;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
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

public class TarTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(TarTriplifier.class);

	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return dg;

		String root = Triplifier.getRootArgument(properties, url);
		Charset charset = Triplifier.getCharsetArgument(properties);
		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);

		logger.trace("BN nodes {}", blank_nodes);

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
			TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
					.createArchiveInputStream("tar", url.openStream(), charset.toString());
			int i = 1;
			TarArchiveEntry entry = null;
			while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {
				g.add(new Triple(rootResource, RDF.li(i).asNode(), NodeFactory.createLiteral(entry.getName())));
				i++;
			}

		} catch (ArchiveException e) {
			e.printStackTrace();
		}

		dg.setDefaultGraph(g);
		dg.addGraph(NodeFactory.createURI(url.toString()), g);

		return dg;
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/x-tar");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("tar");
	}
}
