/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
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
	static final String FACADE_X_TYPE_PROPERTIES = FACADE_X_CONST_NAMESPACE_IRI + "properties";

	static final Logger log = LoggerFactory.getLogger(Triplifier.class);

	@Deprecated
	default public DatasetGraph triplify(Properties properties) throws IOException, TriplifierHTTPException {
		return triplify(properties, new BaseFacadeXBuilder(Triplifier.getLocation(properties).toString(), properties));
	}

	DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException;

	public Set<String> getMimeTypes();

	public Set<String> getExtensions();

	static boolean getBlankNodeArgument(Properties properties) {
		boolean blank_nodes = true;
		if (properties.containsKey(IRIArgument.BLANK_NODES.toString())) {
			blank_nodes = Boolean.parseBoolean(properties.getProperty(IRIArgument.BLANK_NODES.toString()));
		}
		return blank_nodes;
	}

	static boolean getTrimStringsArgument(Properties properties) {
		boolean trim_strings = false;
		if (properties.containsKey(IRIArgument.TRIM_STRINGS.toString())) {
			trim_strings = Boolean.parseBoolean(properties.getProperty(IRIArgument.TRIM_STRINGS.toString()));
		}
		return trim_strings;
	}

	static String getNullStringArgument(Properties properties) {
		String null_string = null;
		if (properties.containsKey(IRIArgument.NULL_STRING.toString())) {
			null_string = properties.getProperty(IRIArgument.NULL_STRING.toString());
		}
		return null_string;
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

	@Deprecated
	static String getRootArgument(Properties properties, URL url) {
		if (url != null) {
			return getRootArgument(properties, url.toString());
		} else {
			return getRootArgument(properties, (String) null);
		}
	}

	static String getRootArgument(Properties properties) {
		try {
			return getRootArgument(properties, Triplifier.getLocation(properties));
		} catch (MalformedURLException e) {
			log.error("Malformed url", e);
			return getRootArgument(properties, (String) null);
		}
	}

	/**
	 * Implementation to be moved to getRootArgument(Properties)
	 *
	 * @param properties
	 * @param url
	 * @return
	 */
	@Deprecated
	static String getRootArgument(Properties properties, String url) {
		if (url != null) {
			String root = null;
			try {
				root = properties.getProperty(IRIArgument.ROOT.toString());
				if (root != null && !root.trim().equals("")) {
					return root;
				}
			} catch (Exception e) {
				log.warn("Unsupported parameter value for 'root': '{}', using default (location + '#').", root);
			}
			return url.toString() + "#";
		} else if (properties.containsKey(IRIArgument.ROOT.toString())
				&& properties.containsKey(IRIArgument.CONTENT.toString())) {
			String root = null;
			try {
				root = properties.getProperty(IRIArgument.ROOT.toString());
				if (root != null && !root.trim().equals("")) {
					return root;
				}
			} catch (Exception e) {
				log.warn("Unsupported parameter value for 'root': '{}', using default (md5hex(content) + '#').", root);
			}
			return XYZ_NS + DigestUtils.md5Hex(properties.getProperty(IRIArgument.CONTENT.toString())) + "#";

		} else if (properties.containsKey(IRIArgument.CONTENT.toString())) {
			return XYZ_NS + DigestUtils.md5Hex(properties.getProperty(IRIArgument.CONTENT.toString())) + "#";
		}
		throw new RuntimeException("No location nor content provided!");
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

	public static URL instantiateURL(String urlLocation) throws MalformedURLException {
		URL url;
		try {
			url = new URL(urlLocation);
		} catch (MalformedURLException u) {
			log.trace("Malformed url interpreting as file");
			url = new File(urlLocation).toURI().toURL();
		}
		return url;
	}

	static URL getLocation(Properties properties) throws MalformedURLException {
		if (properties.containsKey(IRIArgument.LOCATION.toString())) {
			return instantiateURL(properties.getProperty(IRIArgument.LOCATION.toString()));
		}
		return null;
	}

	private static InputStream getInputStream(URL url, Properties properties, Charset charset)
			throws IOException, TriplifierHTTPException {

		if (properties.containsKey(IRIArgument.CONTENT.toString())) {
			return new ByteArrayInputStream(properties.get(IRIArgument.CONTENT.toString()).toString().getBytes());
		}

		if (!properties.containsKey(IRIArgument.FROM_ARCHIVE.toString())) {

			// If local throw exception
			if (url.getProtocol().equals("file")) {
				log.debug("Getting input stream from file");
				return url.openStream();
			} else

			// If HTTP
			if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
				CloseableHttpResponse response = HTTPHelper.getInputStream(url, properties);
				if (!HTTPHelper.isSuccessful(response)) {
					log.error("Request unsuccesful: {}", response.getStatusLine().toString());
					log.error("Response: {}", response.toString());
					log.error("Response body: {}",
							IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
					throw new TriplifierHTTPException(response.getStatusLine().toString());
				}
				return response.getEntity().getContent();
			}

			// If other protocol, try URL and Connection
			log.debug("Other protocol: {}", url.getProtocol());
			return url.openStream();
		}
		// Handle archives differently
		URL urlArchive = instantiateURL(properties.getProperty(IRIArgument.FROM_ARCHIVE.toString()));
		try {
			return ResourceManager.getInstance().getInputStreamFromArchive(urlArchive,
					properties.getProperty(IRIArgument.LOCATION.toString()), charset);
		} catch (ArchiveException e) {
			throw new IOException(e); // TODO i think we should throw a TriplifierHTTPException instead
										// to allow the silent keyword to be respected
		}
	}

	public static InputStream getInputStream(URL url, Properties properties)
			throws IOException, TriplifierHTTPException {
		return getInputStream(url, properties, getCharsetArgument(properties));
	}
	
	public static String getResourceId(Properties properties) {
		String resourceId = null;
		URL url = null;
		try {
			url = Triplifier.getLocation(properties);
		} catch (MalformedURLException e) {
			log.error("Malformed url", e);
		}
		if (url == null && properties.containsKey(IRIArgument.CONTENT.toString())) {
			// XXX This method of passing content seems only supported by the
			// TextTriplifier.
			log.trace("No location, use content: {}", properties.getProperty(IRIArgument.CONTENT.toString()));
			String id = Integer.toString(properties.getProperty(IRIArgument.CONTENT.toString(), "").toString().hashCode());
			resourceId = "content:" + id;
		}
		return resourceId;
	}

}
