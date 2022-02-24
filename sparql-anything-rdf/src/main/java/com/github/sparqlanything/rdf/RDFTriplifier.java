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
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
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
		RDFDataMgr.read(dg, url.toString());
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
