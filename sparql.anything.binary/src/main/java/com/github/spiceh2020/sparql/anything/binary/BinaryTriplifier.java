package com.github.spiceh2020.sparql.anything.binary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
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

public class BinaryTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(BinaryTriplifier.class);

	public static final String ENCODING = "bin.encoding";

	public static enum Encoding {
		BASE64
	}

	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return dg;

		Graph g = GraphFactory.createGraphMem();

		Encoding encoding = Encoding.BASE64;

		if (properties.contains(ENCODING)) {
			try {
				encoding = Encoding.valueOf(properties.getProperty(ENCODING).toUpperCase());
			} catch (java.lang.IllegalArgumentException e) {
				logger.warn("{} - Using default encoding (Base64)", e.getMessage());
			}
		} else {
			logger.warn("Using default encoding (Base64)");
		}

		String root = Triplifier.getRootArgument(properties, url);
//		Charset charset = getCharsetArgument(properties);
		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);
//		String namespace = url.toString() + "#";

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

		String value;
		byte[] file = downloadUrl(url);

		switch (encoding) {
		case BASE64:
			value = Base64.encodeBase64String(file);
			break;
		default:
			value = Base64.encodeBase64String(file);
			break;
		}
		// Add root
		g.add(new Triple(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
		// Add content
		g.add(new Triple(n, RDF.li(1).asNode(), NodeFactory.createLiteralByValue(value, XSDDatatype.XSDbase64Binary)));

		dg.addGraph(NodeFactory.createURI(url.toString()), g);
		dg.setDefaultGraph(g);

		return dg;
	}

	private byte[] downloadUrl(URL toDownload) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			byte[] chunk = new byte[4096];
			int bytesRead;
			InputStream stream = toDownload.openStream();

			while ((bytesRead = stream.read(chunk)) > 0) {
				outputStream.write(chunk, 0, bytesRead);
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return outputStream.toByteArray();
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
