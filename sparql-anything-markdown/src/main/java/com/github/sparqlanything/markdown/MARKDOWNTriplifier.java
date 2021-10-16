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

package com.github.sparqlanything.markdown;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import com.google.common.collect.Sets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.sparql.core.DatasetGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MARKDOWNTriplifier extends AbstractVisitor implements Triplifier {

	private final static Logger logger = LoggerFactory.getLogger(MARKDOWNTriplifier.class);

	private String[] getDataSources(URL url) {
		return new String[] { url.toString() };
	}

	private String getRootId(URL url, String dataSourceId, Properties properties) {
		return Triplifier.getRootArgument(properties, url);
	}

	private URL url;
	private Properties properties;
	private FacadeXGraphBuilder builder;
	private String dataSourceId;
	private String rootId;
	private List<String> containers;

	private void before(URL url, Properties properties, FacadeXGraphBuilder builder, String dataSourceId, String rootId){
		this.url = url;
		this.properties = properties;
		this.builder = builder;
		this.containers = new ArrayList<String>();
	}

	private void after(){
		url = null;
		properties = null;
		builder = builder;
		dataSourceId = null;
		rootId = null;
	}

	private void transform(URL url, Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {

		final InputStream us = Triplifier.getInputStream(url, properties);
		final InputStreamReader reader = new InputStreamReader(us);
		Parser parser = Parser.builder().build();
		Node document = parser.parseReader(reader);

		try {
			// Only 1 data source expected
			String dataSourceId;
			if (properties.containsKey(IRIArgument.ROOT.toString())) {
				logger.trace("Setting Data source Id using Root argument");
				dataSourceId = properties.getProperty(IRIArgument.ROOT.toString());
			} else if (properties.containsKey(IRIArgument.CONTENT.toString())) {
				logger.trace("Setting Data source Id using Content argument");
				dataSourceId = Triplifier.XYZ_NS
						+ DigestUtils.md5Hex(properties.getProperty(IRIArgument.CONTENT.toString()));
			} else {
				dataSourceId = getDataSources(url)[0];
			}

			before(url, properties, builder, dataSourceId, rootId);
			document.accept(this);
		} finally {
			us.close();
			after();
		}
	}

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);

		transform(url, properties, builder);

//		if (logger.isDebugEnabled()) {
//			logger.debug("Number of triples: {} ", builder.getMainGraph().size());
//		}
		return builder.getDatasetGraph();
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("text/markdown");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("md");
	}

	@Override
	public void visit(Document document){
		// Document is the root container
		logger.trace("[Visiting {}] {}", document.getClass(), document);
		this.visitChildren(document);
	}

	@Override
	public void visit(Heading heading) {
		logger.trace("[Visiting {}] {}", heading.getClass(), heading);
		// Create container
		super.visit(heading);
	}

	@Override
	public void visit(Code code) {
		logger.trace("[Visiting {}] {}", code.getClass(), code);
		super.visit(code);
	}

	@Override
	public void visit(ListItem listItem) {
		logger.trace("[Visiting {}] {}", listItem.getClass(), listItem);
		super.visit(listItem);
	}

	@Override
	public void visit(OrderedList orderedList) {
		logger.trace("[Visiting {}] {}", orderedList.getClass(), orderedList);
		super.visit(orderedList);
	}

	@Override
	public void visit(Paragraph paragraph) {
		logger.trace("[Visiting {}] {}", paragraph.getClass(), paragraph);
		super.visit(paragraph);
	}
}
