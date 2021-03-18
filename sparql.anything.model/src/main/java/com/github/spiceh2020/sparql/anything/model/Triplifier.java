package com.github.spiceh2020.sparql.anything.model;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.escape.UnicodeEscaper;
import com.google.common.net.PercentEscaper;

public interface Triplifier {
	static final String METADATA_GRAPH_IRI = "http://sparql.xyz/facade-x/data/metadata";
	static final String AUDIT_GRAPH_IRI = "http://sparql.xyz/facade-x/data/audit";
	static final String XYZ_NS = "http://sparql.xyz/facade-x/data/";
	static final String FACADE_X_CONST_NAMESPACE_IRI = "http://sparql.xyz/facade-x/ns/";
	static final String FACADE_X_TYPE_ROOT = FACADE_X_CONST_NAMESPACE_IRI + "root";

	static final Logger log = LoggerFactory.getLogger(Triplifier.class);

	public DatasetGraph triplify(URL url, Properties properties) throws IOException;

	default public DatasetGraph triplify(URL url, Properties properties, Op subOp) throws IOException {
		return triplify(url, properties);
	}

	public Set<String> getMimeTypes();

	public Set<String> getExtensions();

	static boolean getBlankNodeArgument(Properties properties) {
		boolean blank_nodes = true;
		if (properties.containsKey(IRIArgument.BLANK_NODES.toString())) {
			blank_nodes = Boolean.parseBoolean(properties.getProperty(IRIArgument.BLANK_NODES.toString()));
		}
		return blank_nodes;
	}

	static Charset getCharsetArgument(Properties properties) {
		Charset charset = null;
		try {
			charset = Charset.forName(properties.getProperty(IRIArgument.CHARSET.toString(), "UTF-8"));
		} catch (Exception e) {
			log.warn("Unsupported charset format: '{}', using UTF-8.",
					properties.getProperty(IRIArgument.CHARSET.toString()));
			charset = StandardCharsets.UTF_8;
		}
		return charset;
	}

	static String getRootArgument(Properties properties, URL url) {
		return getRootArgument(properties, url.toString());
	}

	static String getRootArgument(Properties properties, String url) {
		String root = null;
		try {
			root = properties.getProperty(IRIArgument.ROOT.toString());
			if (root != null && !root.trim().equals("")) {
				return root;
			}
		} catch (Exception e) {
			log.warn("Unsupported parameter value for 'root': '{}', using default (location + '#').", root);
		}
		return url + "#";
	}

	static String getNamespaceArgument(Properties properties) {
		String namespace = null;
		try {
			namespace = properties.getProperty(IRIArgument.NAMESPACE.toString());
			if (namespace != null && !namespace.trim().equals("")) {
				return namespace;
			}
		} catch (Exception e) {
			log.warn("Unsupported parameter value for 'namespace': '{}', using default ({}}).", namespace, XYZ_NS);
		}
		return XYZ_NS;
	}

	static UnicodeEscaper basicEscaper = new PercentEscaper("%", false);

	public static String toSafeURIString(String s) {
		return basicEscaper.escape(s);
	}

}
