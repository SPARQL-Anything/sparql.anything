package com.github.spiceh2020.sparql.anything.text;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class TextTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(TextTriplifier.class);

	public static final String REGEX = "txt.regex", GROUP = "txt.group";

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();
		Graph g = GraphFactory.createGraphMem();

//		String root = null;
//
//		if (properties.contains(IRIArgument.ROOT.toString())) {
//			root = properties.getProperty(IRIArgument.ROOT.toString());
//			if (root.trim().length() == 0) {
//				logger.warn("Unsupported parameter value for 'root', using default (no value).");
//				root = null;
//			}
//		}
//
//		boolean blank_nodes = true;
//		if (properties.containsKey(IRIArgument.BLANK_NODES.toString())) {
//			blank_nodes = Boolean.parseBoolean(properties.getProperty(IRIArgument.BLANK_NODES.toString()));
//		}
//
//		String charset = properties.getProperty(IRIArgument.CHARSET.toString(), "UTF-8");
		
		String root = Triplifier.getRootArgument(properties, url);
		Charset charset = Triplifier.getCharsetArgument(properties);
		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);

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
		g.add(new Triple(rootResource, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

		String value = readFromURL(url, charset.toString());

		Pattern pattern = null;
		if (properties.containsKey(REGEX)) {
			String regexString = properties.getProperty(REGEX);
			try {
				pattern = Pattern.compile(regexString);
				// TODO flags
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				pattern = null;
			}

		}

		int group = -1;
		if (properties.contains(GROUP)) {
			try {
				int gr = Integer.parseInt(properties.getProperty(GROUP));
				if (gr >= 0) {
					group = gr;
				} else {
					logger.warn("Group number is supposed to be a positive integer, using default (group 0)");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}

		if (pattern != null) {
			Matcher m = pattern.matcher(value);
			int count = 1;
			while (m.find()) {
				if (group > 1) {
					g.add(new Triple(rootResource, RDF.li(count).asNode(),
							NodeFactory.createLiteralByValue(m.group(group), XSDDatatype.XSDstring)));
				} else {
					g.add(new Triple(rootResource, RDF.li(count).asNode(),
							NodeFactory.createLiteralByValue(m.group(), XSDDatatype.XSDstring)));

				}
				count++;
			}
		} else {
			g.add(new Triple(rootResource, RDF.li(1).asNode(),
					NodeFactory.createLiteralByValue(value, XSDDatatype.XSDstring)));
		}

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
		return Sets.newHashSet("text/plain");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("txt");
	}
}
