package com.github.spiceh2020.sparql.anything.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
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

	public static final String REGEX = "txt.regex", GROUP = "txt.group", SPLIT = "txt.split";
//
//	public DatasetGraph triplify(String value, Properties properties, FacadeXGraphBuilder builder) throws IOException {
//		logger.trace("Triplifying \"{}\"", value);
//		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);
//		triplifyValue(builder, properties, blank_nodes, Integer.toString(value.hashCode()), value);
//		logger.trace("Number of triples: {}", g.size());
//
//		return builder.getDatasetGraph();
//	}

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
//		DatasetGraph dg = DatasetGraphFactory.create();

		String value;
		String root;
		String dataSourceId;
		URL url = Triplifier.getLocation(properties);
		if (url == null) {
			value = properties.getProperty(IRIArgument.CONTENT.toString(), "");
			root = Triplifier.getRootArgument(properties, Integer.toString(value.hashCode()));
			dataSourceId = builder.getMainGraphName().getURI();
		}else{
			value = readFromURL(url, properties);
			root = Triplifier.getRootArgument(properties, url.toString());
			dataSourceId = builder.getMainGraphName().getURI();
		}

		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);

		String rootResourceId = root;
//		if (!blank_nodes) {
//			if (root == null) {
//				rootResourceId = url.toString(); //NodeFactory.createURI(url.toString());
//			} else {
//				rootResourceId = NodeFactory.createURI(root);
//			}
//
//		} else {
//			rootResource = NodeFactory.createBlankNode();
//		}

//		triplifyValue(builder, properties, blank_nodes, rootResourceId, value);

//		dg.addGraph(NodeFactory.createURI(url.toString()), g);
//		dg.setDefaultGraph(g);
//
//		return dg;
//		return builder.getDatasetGraph();

		if(logger.isTraceEnabled()) {
			logger.trace("Content:\n{}\n", value);
		}

		builder.addRoot(dataSourceId, rootResourceId );
//		Graph g = GraphFactory.createGraphMem();
//		g.add(new Triple(rootResource, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

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
		if (properties.contains(GROUP) && pattern != null) {
			try {
				int gr = Integer.parseInt(properties.getProperty(GROUP));
				if (gr >= 0) {
					group = gr;
				} else {
					logger.warn("Group number is supposed to be a positive integer, using default (group 0)");
				}
			} catch (Exception e) {
//				e.printStackTrace();
				logger.error("",e);
			}
		}

		if (pattern != null) {
			Matcher m = pattern.matcher(value);
			int count = 1;
			while (m.find()) {
				if (group > 1) {
//					g.add(new Triple(rootResource, RDF.li(count).asNode(),
//							NodeFactory.createLiteralByValue(m.group(group), XSDDatatype.XSDstring)));
					builder.addValue(dataSourceId, rootResourceId, count, m.group(group));
				} else {
//					g.add(new Triple(rootResource, RDF.li(count).asNode(),
//							NodeFactory.createLiteralByValue(m.group(), XSDDatatype.XSDstring)));
					builder.addValue(dataSourceId, rootResourceId, count, m.group());
				}
				count++;
			}
		} else {
			logger.trace("No pattern set");

			if (properties.containsKey(SPLIT)) {

				logger.trace("Splitting regex: {}", properties.getProperty(SPLIT));
				System.out.println("split: " + properties.getProperty(SPLIT));
				String[] split = value.split(properties.getProperty(SPLIT));
				for (int i = 0; i < split.length; i++) {
//					System.out.println(" -> " + split[i]);
//					g.add(new Triple(rootResource, RDF.li(i + 1).asNode(),
//							NodeFactory.createLiteralByValue(split[i], XSDDatatype.XSDstring)));
//					System.out.println(dataSourceId + " -> " + rootResourceId +" -> " + (i + 1) + " -> " + split[i]);
					builder.addValue(dataSourceId, rootResourceId, i + 1, split[i]);
				}

			} else {
//				g.add(new Triple(rootResource, RDF.li(1).asNode(),
//						NodeFactory.createLiteralByValue(value, XSDDatatype.XSDstring)));
				builder.addValue(dataSourceId, rootResourceId, 1, value);
			}

		}
//		System.out.println("Empty? " + builder.getDatasetGraph().getDefaultGraph().isEmpty());
		return builder.getDatasetGraph();
	}

	private static String readFromURL(URL url, Properties properties)
			throws IOException {
		StringWriter sw = new StringWriter();
//		IOUtils.copy(url.openStream(), sw, Charset.forName(charset));
		InputStream is = Triplifier.getInputStream(url, properties);
		IOUtils.copy(is, sw, Triplifier.getCharsetArgument(properties));
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
