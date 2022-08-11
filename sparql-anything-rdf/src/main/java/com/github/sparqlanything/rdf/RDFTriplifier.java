/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.sparqlanything.rdf;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.HTTPHelper;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.lang.LangEngine;
import org.apache.jena.sparql.core.DatasetGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

public class RDFTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(RDFTriplifier.class);

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return ;

		DatasetGraph dg = builder.getDatasetGraph();
		logger.info("URL {}", url.toString());
		try {
			InputStream is;
			Header contentType;
			if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
				CloseableHttpResponse response = HTTPHelper.getInputStream(url, properties);
				if (!HTTPHelper.isSuccessful(response)) {
					log.warn("Request unsuccessful: {}", response.getStatusLine().toString());
					if(log.isTraceEnabled()){
						log.trace("Response: {}", response.toString());
						log.trace("Response body: {}",IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
					}
					throw new TriplifierHTTPException(response.getStatusLine().toString());
				}
				is = response.getEntity().getContent();
				contentType = response.getFirstHeader(HTTP.CONTENT_TYPE);
			} else {
				is = Triplifier.getInputStream(properties);
				contentType = null;
			}
			RDFDataMgr.read(dg, is, getRDFLang(properties, url.toString(), contentType));
		} catch (TriplifierHTTPException e) {
			logger.error("", e.getMessage());
			throw new IOException(e);
		}
	}
	public static Lang getRDFLang(Properties properties, String url, Header contentType){
		Lang lang = null;
		// Version from HTTP content type response
		if(contentType != null){
			lang = RDFLanguages.contentTypeToLang(contentType.getValue().substring(0,contentType.getValue().indexOf(';') ));
		}
		// Version from expected content type (HTTP accept header)
		if(lang == null && properties.containsKey(HTTPHelper.HTTPHEADER_PREFIX + "accept")){
			lang = RDFLanguages.contentTypeToLang(properties.getProperty(HTTPHelper.HTTPHEADER_PREFIX + "accept"));
		}
		if(lang == null) {
			// Version from location file extension
			lang = RDFLanguages.filenameToLang(url);
		}
		if(lang == null){
			log.warn("Failed to determine RDF lang");
		}
		return lang;
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/rdf+thrift", "application/trix+xml", "application/n-quads", "text/trig",
				"application/owl+xml", "text/turtle", "application/rdf+xml", "application/n-triples",
				"application/ld+json");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("rdf", "ttl", "nt", "jsonld", "owl", "trig", "nq", "trix", "trdf");
	}

}
