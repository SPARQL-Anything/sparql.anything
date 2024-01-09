/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.rdf;

import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.HTTPHelper;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.jena.ext.com.google.common.collect.Sets;
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
import java.util.Properties;
import java.util.Set;

@io.github.sparqlanything.model.annotations.Triplifier
public class RDFTriplifier implements Triplifier {

//	private static Logger logger = LoggerFactory.getLogger(RDFTriplifier.class);

	public static Lang getRDFLang(Properties properties, String url, Header contentType) {
		Lang lang = null;

		// Version from HTTP content type response
		if (contentType != null) {
			// After issue https://github.com/SPARQL-Anything/sparql.anything/issues/317
			if (contentType.getValue().indexOf(';') != -1) {
				lang = RDFLanguages.contentTypeToLang(contentType.getValue().substring(0, contentType.getValue().indexOf(';')));
			} else {
				lang = RDFLanguages.contentTypeToLang(contentType.getValue());
			}
		}
		// Version from expected content type (HTTP accept header)
		if (lang == null && properties.containsKey(HTTPHelper.HTTPHEADER_PREFIX + "accept")) {
			lang = RDFLanguages.contentTypeToLang(properties.getProperty(HTTPHelper.HTTPHEADER_PREFIX + "accept"));
		}
		if (lang == null) {
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
		if(lang.getLabel().equals("JSON")){
			lang = Lang.JSONLD;
		}
		log.trace("Lang {}", lang);
		return lang;
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		URL url = Triplifier.getLocation(properties);

		if (url == null) return;

		DatasetGraph dg = builder.getDatasetGraph();
		log.trace("URL {}", url);
		try {
			InputStream is;
			Header contentType;
			if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
				CloseableHttpResponse response = HTTPHelper.getInputStream(url, properties);
				if (!HTTPHelper.isSuccessful(response)) {
					log.warn("Request unsuccessful: {}", response.getStatusLine().toString());
					if (log.isTraceEnabled()) {
						log.trace("Response: {}", response);
						log.trace("Response body: {}", IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
					}
					throw new TriplifierHTTPException(response.getStatusLine().toString());
				}
				is = response.getEntity().getContent();
				contentType = response.getFirstHeader(HTTP.CONTENT_TYPE);
			} else {
				is = Triplifier.getInputStream(properties);
				contentType = null;
			}
			Lang l = getRDFLang(properties, url.toString(), contentType);

			if (Sets.newHashSet(Lang.RDFXML, Lang.TTL, Lang.NT, Lang.JSONLD, Lang.JSONLD10, Lang.JSONLD11, Lang.RDFJSON).contains(l)) {
				Graph g = dg.getDefaultGraph();
				RDFDataMgr.read(g, is, l);
				dg.addGraph(NodeFactory.createURI(url.toString()), g);
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
