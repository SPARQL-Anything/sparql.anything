/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import io.github.sparqlanything.model.resources.ResourceService;
import io.github.sparqlanything.model.resources.annotations.TargetOption;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public interface Triplifier {

	String XYZ_NS = "http://sparql.xyz/facade-x/data/";
	String METADATA_GRAPH_IRI = XYZ_NS + "metadata";
	String AUDIT_GRAPH_IRI = XYZ_NS + "audit";
	String XYZ_NULL = XYZ_NS + "null";
	String FACADE_X_CONST_NAMESPACE_IRI = "http://sparql.xyz/facade-x/ns/";
	String FACADE_X_TYPE_ROOT = FACADE_X_CONST_NAMESPACE_IRI + "root";
	String FACADE_X_SLOT_KEY = FACADE_X_CONST_NAMESPACE_IRI + "slot-key";
	String FACADE_X_TYPE_PROPERTIES = FACADE_X_CONST_NAMESPACE_IRI + "properties";
	String FACADE_X_CACHED_GRAPH = FACADE_X_CONST_NAMESPACE_IRI + "cachedGraph";
	String FACADE_X_CACHED_GRAPH_CREATION = FACADE_X_CONST_NAMESPACE_IRI + "cachedGraphCreation";
	String FACADE_X_SPARQL_ALGEBRA = FACADE_X_CONST_NAMESPACE_IRI + "sparqlAlgebra";
	Node XYZ_NULL_NODE = NodeFactory.createURI(XYZ_NULL);
	String INVALID_URL_CHAR_REGEX = ".*[^\\p{L}\\p{N}\\-_.~:/?#\\[\\]@!$&'()*+,;=%].*";

	Logger log = LoggerFactory.getLogger(Triplifier.class);
	UnicodeEscaper basicEscaper = new PercentEscaper("_.-~", false);

	static String getRootArgument(Properties properties) {
		String root = PropertyUtils.getStringProperty(properties, IRIArgument.ROOT, null);
		if (root != null && !root.trim().isEmpty()) return root;

		String location = getNormalisedLocation(properties);
		if (location != null) return location;//+ "#";

		String content = PropertyUtils.getStringProperty(properties, IRIArgument.CONTENT, null);
		if (content != null) return XYZ_NS + DigestUtils.md5Hex(content);//+ "#";

		String command = PropertyUtils.getStringProperty(properties, IRIArgument.COMMAND, null);
		if (command != null) return XYZ_NS + DigestUtils.md5Hex(command);//+ "#";

		String s3Endpoint = PropertyUtils.getStringProperty(properties, IRIArgument.S3_ENDPOINT, null);
		if (s3Endpoint != null) return s3Endpoint;//+ "#";

		throw new RuntimeException("No location nor content nor command provided!");
	}

	static String getNormalisedLocation(Properties properties) {
		URL location;
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
			return Utils.instantiateURL(properties.getProperty(IRIArgument.LOCATION.toString()));
		}
		return null;
	}

	static String toSafeURIString(String s) {

		if (s.matches(INVALID_URL_CHAR_REGEX)) {
			s = basicEscaper.escape(s);
		}
		return s;
	}

	static Map<String, ResourceService> loadResourceServicesAll() {
		Map<String, ResourceService> registry = new LinkedHashMap<>();

		ServiceLoader<ResourceService> loader = ServiceLoader.load(ResourceService.class);

		for (ServiceLoader.Provider<ResourceService> provider : loader.stream().toList()) {
			Class<? extends ResourceService> implClass = provider.type();
			TargetOption annotation = implClass.getAnnotation(TargetOption.class);

			if (annotation == null) {
				System.err.println("Skipping " + implClass.getName() + ": missing @TargetOption annotation");
				continue;
			}

			String key = annotation.value();
			ResourceService instance = provider.get();

			ResourceService previous = registry.put(key, instance);
			if (previous != null) {
				throw new IllegalStateException(
					"Duplicate @TargetOption(\"" + key + "\") found for "
						+ previous.getClass().getName() + " and " + implClass.getName());
			}
		}

		return registry;
	}

	static InputStream getInputStream(Properties properties) throws IOException, TriplifierHTTPException {

		Map<String, ResourceService> resourceServiceMap = loadResourceServicesAll();

		IRIArgument[] options = {IRIArgument.S3_ENDPOINT, IRIArgument.COMMAND, IRIArgument.CONTENT, IRIArgument.LOCATION};
		for (IRIArgument option : options) {
			if (properties.containsKey(option.toString())) {
				ResourceService service = resourceServiceMap.get(option.toString());
				return service.getInputStream(properties);
			}
		}

		throw new RuntimeException("No input defined! None of the following options are provided:" + Arrays.toString(options));
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
