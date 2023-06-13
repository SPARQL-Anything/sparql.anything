/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.model;

import com.google.common.escape.UnicodeEscaper;
import com.google.common.net.PercentEscaper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public interface Triplifier {

	String METADATA_GRAPH_IRI = "http://sparql.xyz/facade-x/data/metadata";
	String AUDIT_GRAPH_IRI = "http://sparql.xyz/facade-x/data/audit";
	String XYZ_NS = "http://sparql.xyz/facade-x/data/";
	String FACADE_X_CONST_NAMESPACE_IRI = "http://sparql.xyz/facade-x/ns/";
	String FACADE_X_TYPE_ROOT = FACADE_X_CONST_NAMESPACE_IRI + "root";
	String FACADE_X_SLOT_KEY = FACADE_X_CONST_NAMESPACE_IRI + "slot-key";
	String FACADE_X_TYPE_PROPERTIES = FACADE_X_CONST_NAMESPACE_IRI + "properties";

	Logger log = LoggerFactory.getLogger(Triplifier.class);
	UnicodeEscaper basicEscaper = new PercentEscaper("_.-~", false);

	static boolean getSliceArgument(Properties properties) {
		boolean slice = false;
		if (properties.containsKey(IRIArgument.SLICE.toString())) {
			slice = Boolean.parseBoolean(properties.getProperty(IRIArgument.SLICE.toString()));
		}
		return slice;
	}

	static boolean useRDFsMember(Properties properties) {
		boolean use_rdfs_member = false;
		if (properties.containsKey(IRIArgument.USE_RDFS_MEMBER.toString())) {
			use_rdfs_member = Boolean.parseBoolean(properties.getProperty(IRIArgument.USE_RDFS_MEMBER.toString()));
		}
		return use_rdfs_member;
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

	static String getRootArgument(Properties properties) {
		String root = PropertyUtils.getStringProperty(properties, IRIArgument.ROOT, null);
		if (root != null && !root.trim().equals("")) return root;

		String location = getNormalisedLocation(properties);
		if (location != null) return location + "#";

		String content = PropertyUtils.getStringProperty(properties, IRIArgument.CONTENT, null);
		if (content != null) return XYZ_NS + DigestUtils.md5Hex(content) + "#";

		String command = PropertyUtils.getStringProperty(properties, IRIArgument.COMMAND, null);
		if (command != null) return XYZ_NS + DigestUtils.md5Hex(command) + "#";

		throw new RuntimeException("No location nor content nor command provided!");
	}

	static String getNormalisedLocation(Properties properties) {
		URL location = null;
		try {
			location = Triplifier.getLocation(properties);
			if (location == null) return null;
			if (location.getProtocol().equals("file"))
				return Path.of(location.toURI()).toUri().toString();
			return location.toString();
		} catch (MalformedURLException e) {
			log.warn("Malformed location");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	static URL getLocation(Properties properties) throws MalformedURLException {
		if (properties.containsKey(IRIArgument.LOCATION.toString())) {
			return instantiateURL(properties.getProperty(IRIArgument.LOCATION.toString()));
		}
		return null;
	}

	static URL instantiateURL(String urlLocation) throws MalformedURLException {
		log.trace("URL Location {}", urlLocation);
		URL url;
		try {
			url = new URL(urlLocation);
		} catch (MalformedURLException u) {
			log.trace("Malformed url interpreting as file");
			url = new File(urlLocation).toURI().toURL();
		}
		log.trace("Result {}", url);
		return url;
	}

	static String getNamespaceArgument(Properties properties) {
		return PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE);
	}

	static String toSafeURIString(String s) {
		// s = s.replaceAll("\\s", "_");
		return basicEscaper.escape(s);
	}

	/**
	 * Get all values from a property key. Supports single and multi-valued, e.g.
	 * <p>
	 * - key.name = value - key.name.0 = value0 - key.name.1 = value1
	 *
	 * @param properties
	 * @param prefix
	 * @return
	 */
	static List<String> getPropertyValues(Properties properties, String prefix) {
		List<String> values = new ArrayList<String>();
		if (properties.containsKey(prefix)) {
			values.add(properties.getProperty(prefix));
		}
		int i = 0; // Starts with 0
		String propName = prefix + "." + i;
		if (properties.containsKey(propName)) {
			values.add(properties.getProperty(propName));
		}
		i++;
		// ... or starts with 1
		propName = prefix + "." + i;

		while (properties.containsKey(propName)) {
			values.add(properties.getProperty(propName));
			i++;
			propName = prefix + "." + i;
		}
		return values;
	}

	static InputStream getInputStream(Properties properties) throws IOException, TriplifierHTTPException {
		return getInputStream(properties, getCharsetArgument(properties));
	}

	private static InputStream getInputStream(Properties properties, Charset charset) throws IOException, TriplifierHTTPException {

		if (properties.containsKey(IRIArgument.COMMAND.toString())) {
			String command = properties.getProperty(IRIArgument.COMMAND.toString());
			Runtime rt = Runtime.getRuntime();
			String[] commands;
			if (Utils.platform != Utils.OS.WINDOWS) {
				// allow shell pipelines and other useful shell functionality
				commands = new String[]{"bash", "-c", command};
			} else { // WINDOWS
				// Credit: https://stackoverflow.com/a/18893443/1035608
				commands = command.split("(?x)   " + "\\s          " + // Split on space
						"(?=        " + // Followed by
						"  (?:      " + // Start a non-capture group
						"    [^\"]* " + // 0 or more non-quote characters
						"    \"     " + // 1 quote
						"    [^\"]* " + // 0 or more non-quote characters
						"    \"     " + // 1 quote
						"  )*       " + // 0 or more repetition of non-capture group (multiple of 2 quotes will be even)
						"  [^\"]*   " + // Finally 0 or more non-quotes
						"  $        " + // Till the end (This is necessary, else every space will satisfy the condition)
						")          " // End look-ahead
				);
			}
			log.info("Running command: {}", String.join(" ", commands));
			Process proc = rt.exec(commands);
			InputStream is = proc.getInputStream();
			InputStream es = proc.getErrorStream();
			log.info("Command stderr: " + IOUtils.toString(es));
			return is;
		}

		if (properties.containsKey(IRIArgument.CONTENT.toString())) {
			return new ByteArrayInputStream(properties.get(IRIArgument.CONTENT.toString()).toString().getBytes());
		}

		if (!properties.containsKey(IRIArgument.FROM_ARCHIVE.toString())) {

			URL url = Triplifier.getLocation(properties);
			// If local throw exception
			if (url.getProtocol().equals("file")) {
				log.debug("Getting input stream from file");
				return url.openStream();
			} else

				// If HTTP
				if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
					CloseableHttpResponse response = HTTPHelper.getInputStream(url, properties);
					if (!HTTPHelper.isSuccessful(response)) {
						log.trace("Request unsuccesful: {}", response.getStatusLine().toString());
						log.trace("Response: {}", response);
						log.trace("Response body: {}", IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
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
			return ResourceManager.getInstance().getInputStreamFromArchive(urlArchive, properties.getProperty(IRIArgument.LOCATION.toString()), charset);
		} catch (ArchiveException e) {
			throw new IOException(e); // TODO i think we should throw a TriplifierHTTPException instead
			// to allow the silent keyword to be respected
		}
	}

	static Charset getCharsetArgument(Properties properties) {
		Charset charset = null;
		try {
			charset = Charset.forName(properties.getProperty(IRIArgument.CHARSET.toString(), "UTF-8"));
		} catch (Exception e) {
			log.warn("Unsupported charset format: '{}', using UTF-8.", properties.getProperty(IRIArgument.CHARSET.toString()));
			charset = StandardCharsets.UTF_8;
		}
		return charset;
	}

	static String getResourceId(Properties properties) {
		String resourceId = null;
		URL url = null;
		try {
			url = Triplifier.getLocation(properties);
		} catch (MalformedURLException e) {
			log.error("Malformed url", e);
		}
		if (url == null && properties.containsKey(IRIArgument.COMMAND.toString())) {
			log.trace("No location, use command: {}", properties.getProperty(IRIArgument.COMMAND.toString()));
			String id = Integer.toString(properties.getProperty(IRIArgument.CONTENT.toString(), "").hashCode());
			resourceId = "command:" + id;
		} else if (url == null && properties.containsKey(IRIArgument.CONTENT.toString())) {
			// XXX This method of passing content seems only supported by the
			// TextTriplifier.
			log.trace("No location, use content: {}", properties.getProperty(IRIArgument.CONTENT.toString()));
			String id = Integer.toString(properties.getProperty(IRIArgument.CONTENT.toString(), "").hashCode());
			resourceId = "content:" + id;
		} else if (url != null) {
			resourceId = url.toString();
		}
		return resourceId;
	}

	void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException;

	Set<String> getMimeTypes();

	Set<String> getExtensions();

}
