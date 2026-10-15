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



package io.github.sparqlanything.rdf;

import com.google.common.collect.Sets;
import io.github.sparqlanything.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.sparql.core.DatasetGraph;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

@io.github.sparqlanything.model.annotations.Triplifier
public class RDFTriplifier implements Triplifier {

//	private static Logger logger = LoggerFactory.getLogger(RDFTriplifier.class);

	/**
	 * Jena's mapping, plus the media types this triplifier declares (getMimeTypes) that Jena does
	 * not register: text/trig (Jena: application/trig) and application/owl+xml (read as RDF/XML,
	 * like the .owl extension). Parameters such as ";charset=utf-8" are ignored.
	 */
	private static Lang contentTypeToLang(String contentType) {
		if (contentType == null) {
			return null;
		}
		String ct = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
		switch (ct) {
			case "text/trig":
				return Lang.TRIG;
			case "application/owl+xml":
				return Lang.RDFXML;
			default:
				return RDFLanguages.contentTypeToLang(ct);
		}
	}

	private static Lang getRDFLang(Properties properties, String url, Header contentType) {
		Lang lang = null;

		// Hard configuration comes first
		if (properties.containsKey(IRIArgument.MEDIA_TYPE.toString())) {
			lang = contentTypeToLang(PropertyUtils.getStringProperty(properties, IRIArgument.MEDIA_TYPE));
		}

		// Version from HTTP content type response
		if (lang == null && contentType != null) {
			// After issue https://github.com/SPARQL-Anything/sparql.anything/issues/317
			lang = contentTypeToLang(contentType.getValue());
		}
		
		// Version from expected content type (HTTP accept header)
		if (lang == null && properties.containsKey(HTTPHelper.HTTPHEADER_PREFIX + "accept")) {
			lang = contentTypeToLang(properties.getProperty(HTTPHelper.HTTPHEADER_PREFIX + "accept"));
		}

		if (lang == null && url != null) {
			// Version from location file extension
			lang = RDFLanguages.filenameToLang(url);
		}
		if (lang == null) {
			log.warn("Failed to determine RDF lang");
		}
		// XXX The CLI registers JSON with the JSON-LD parser to support cases where
		// the served content-type is application/json but the expected content is RDF
		// However, the JSON is not a proper LANG, therefore, we rewrite it here
		// See #356
		// See io.github.sparqlanything.cli.SPARQLAnything.initSPARQLAnythingEngine()
		if(lang!=null && lang.getLabel().equals("JSON")){
			lang = Lang.JSONLD;
		}
		log.trace("Lang {}", lang);
		return lang;
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		URL url = Triplifier.getLocation(properties);

		DatasetGraph dg = builder.getDatasetGraph();
		log.trace("URL {}", url);
		try {
			InputStream is;
			Header contentType;
			// The graph is named after the location, or the root (e.g. md5 of the content) without one
			String graphName = url != null ? url.toString() : Triplifier.getRootArgument(properties);
			if (url != null && (url.getProtocol().equals("http") || url.getProtocol().equals("https"))) {
				CloseableHttpResponse response = HTTPHelper.getInputStream(url, properties);
				if (!HTTPHelper.isSuccessful(response)) {
					log.warn("Request unsuccessful: {}", response.getStatusLine().toString());
					if (log.isTraceEnabled()) {
						log.trace("Response: {}", response);
						log.trace("Response body: {}", IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
					}
					throw new TriplifierHTTPException(url, response);
				}
				is = response.getEntity().getContent();
				contentType = response.getFirstHeader(HTTP.CONTENT_TYPE);
			} else {
				// location, content, command or standard input
				is = Triplifier.getInputStream(properties);
				contentType = null;
			}
			Lang l = getRDFLang(properties, url != null ? url.toString() : null, contentType);
			if (l == null) {
				throw new IOException("Cannot determine the RDF syntax of " + graphName + ": set the media-type option");
			}

			if (Sets.newHashSet(Lang.RDFXML, Lang.TTL, Lang.NT, Lang.JSONLD, Lang.JSONLD11, Lang.RDFJSON).contains(l)) {
				Graph g = dg.getDefaultGraph();
				RDFDataMgr.read(g, is, l);
				dg.addGraph(NodeFactory.createURI(graphName), g);
			} else {
				RDFDataMgr.read(dg, is, l);
			}
		} catch (TriplifierHTTPException e) {
			log.error("{}", e.getMessage());
			throw new IOException(e);
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/rdf+thrift", "application/trix+xml", "application/n-quads", "text/trig", "application/owl+xml", "text/turtle", "application/rdf+xml", "application/n-triples", "application/ld+json");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("rdf", "ttl", "nt", "jsonld", "owl", "trig", "nq", "trix", "trdf");
	}

}
