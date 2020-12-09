package com.github.spiceh2020.sparql.anything.text;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
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

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class TextTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(TextTriplifier.class);

	public static final String REGEX = "regex", GROUP = "group", TOKENS = "tokens";

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();
		Graph g = GraphFactory.createGraphMem();

		String root = null;

		if (properties.contains(IRIArgument.ROOT.toString())) {
			root = properties.getProperty(IRIArgument.ROOT.toString());
			if (root.trim().length() == 0) {
				logger.warn("Unsupported parameter value for 'root', using default (no value).");
				root = null;
			}
		}

		boolean blank_nodes = true;
		if (properties.containsKey(IRIArgument.BLANK_NODES.toString())) {
			blank_nodes = Boolean.parseBoolean(properties.getProperty(IRIArgument.BLANK_NODES.toString()));
		}

		String charset = properties.getProperty(IRIArgument.CHARSET.toString(), "UTF-8");

		Node n;
		if (!blank_nodes) {
			if (root == null) {
				n = NodeFactory.createURI(url.toString());
			} else {
				n = NodeFactory.createURI(root);
			}

		} else {
			n = NodeFactory.createBlankNode();
		}

		String value = readFromURL(url, charset);

		// TODO Regex
		// TODO Tokenizer

		g.add(new Triple(n, RDF.value.asNode(), NodeFactory.createLiteralByValue(value, XSDDatatype.XSDstring)));

		dg.addGraph(NodeFactory.createURI(url.toString()), g);
		dg.setDefaultGraph(g);

		return dg;
	}

	private static String readFromURL(URL url, String charset) throws IOException {
		StringWriter sw = new StringWriter();
		IOUtils.copy(url.openStream(), sw, Charset.forName(charset));
		return sw.toString();

	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/octet-stream");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("bin", "dat");
	}
}
