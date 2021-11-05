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
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;

public class MARKDOWNTriplifier extends AbstractVisitor implements Triplifier {

	private final static Logger logger = LoggerFactory.getLogger(MARKDOWNTriplifier.class);

	private URL url;
	private Properties properties;
	private FacadeXGraphBuilder builder;
	private String dataSourceId;
	private String rootId;
	private Map<Node, String> containers;
	private Map<Class<? extends Node>, Integer> typeCounter;
	private int lastLevel = 0;
	private Map<String,Integer> lastSlot;

	private void before(URL url, Properties properties, FacadeXGraphBuilder builder, String dataSourceId, String rootId){
		this.url = url;
		this.properties = properties;
		this.builder = builder;
		this.dataSourceId = dataSourceId;
		this.rootId = rootId;
		this.containers = new HashMap<Node,String>();
		this.lastLevel = 0;
		this.lastSlot = new HashMap<String,Integer>();
		this.typeCounter = new HashMap<Class<? extends Node>,Integer>();
	}

	private void after(){
		url = null;
		properties = null;
		builder = null;
		dataSourceId = null;
		rootId = null;
		this.containers = null;
		this.lastLevel = 0;
		this.lastSlot = null;
	}

	private void transform(URL url, Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {

		final InputStream us = Triplifier.getInputStream(url, properties);
		final InputStreamReader reader = new InputStreamReader(us);
		Parser parser = Parser.builder().build();
		Node document = parser.parseReader(reader);

		try {
			// Only 1 data source expected
			String dataSourceId = Triplifier.getRootArgument(properties, url);
			rootId = dataSourceId;
			logger.trace("ds {} root {}", dataSourceId, rootId);
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

	private String handleContainer(Node node){
		String parentId = this.containers.get(node.getParent());
		int slot = lastSlot.get(parentId) + 1;
		if(!typeCounter.containsKey(node.getClass())){
			typeCounter.put(node.getClass(), 0);
		}
		int counter = typeCounter.get(node.getClass()) + 1;
		String containerId = String.join("/", parentId , node.getClass().getSimpleName().toLowerCase() , Integer.toString(counter));
		this.typeCounter.put(node.getClass(), counter);
		this.builder.addContainer(dataSourceId, parentId, slot, containerId);
		this.builder.addType(dataSourceId, containerId, XYZ_NS + node.getClass().getSimpleName());
		this.lastSlot.put(parentId, slot);
		this.containers.put(node, containerId);
		this.lastSlot.put(containerId, 0);
		return containerId;
	}

	private void handleLiteral(Text node){
		// We directly link the text, we will see what happens later with links etc...
		String parentId = this.containers.get(node.getParent());
		int slot = lastSlot.get(parentId) + 1;
		this.builder.addValue(dataSourceId,parentId, slot, node.getLiteral());
		this.lastSlot.put(parentId, slot);
	}

	@Override
	public void visit(Document node){
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		// Document is the root container
		String containerId = rootId ;
		this.builder.addType(dataSourceId, containerId, XYZ_NS + node.getClass().getSimpleName());
//		this.lastSlot.put(parentId, slot);
		this.containers.put(node, containerId);
		this.lastSlot.put(containerId, 0);
		this.builder.addRoot(dataSourceId, rootId);
		this.visitChildren(node);
	}

	@Override
	public void visit(Heading node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());

		handleContainer(node);
		// specify level
		builder.addValue(dataSourceId, containers.get(node), "level", node.getLevel());
		super.visit(node);
	}

	@Override
	public void visit(Code node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		String containerId = handleContainer(node);
		this.builder.addValue(dataSourceId, containerId, 1, node.getLiteral());
		super.visit(node);
	}

	@Override
	public void visit(ListItem node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		logger.trace("[Spans {}]", node.getSourceSpans());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(OrderedList node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		logger.trace("[Delimiter] {}", node.getDelimiter());
		logger.trace("[Start number] {}", node.getStartNumber());

		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(BulletList node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(Paragraph node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		logger.trace("[SourceSpans] {}", node.getSourceSpans());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(Text node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		//logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		logger.trace("[Literal] {}", node.getLiteral());
		logger.trace("[SourceSpans] {}", node.getSourceSpans());
		handleLiteral(node);
		super.visit(node);
	}

	@Override
	public void visit(Link node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[toString {}] {}", node.getClass(), node.toString());
		//logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		if(node.getTitle() != null) {
			builder.addValue(dataSourceId, containers.get(node), "title", node.getTitle());
		}
		if(node.getDestination() != null) {
			builder.addValue(dataSourceId, containers.get(node), "destination", node.getDestination());
		}
		super.visit(node);
	}

	@Override
	public void visit(BlockQuote node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(Emphasis node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
//		logger.trace("[toString {}] {}", node.getClass(), node.toString());
		//logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(FencedCodeBlock node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		String containerId = handleContainer(node);
		this.builder.addValue(dataSourceId, containerId, 1, node.getLiteral());
		super.visit(node);
	}

	@Override
	public void visit(HardLineBreak node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(ThematicBreak node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(HtmlInline node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		String containerId = handleContainer(node);
		this.builder.addValue(dataSourceId, containerId, 1, node.getLiteral());
		super.visit(node);
	}

	@Override
	public void visit(HtmlBlock node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		String containerId = handleContainer(node);
		this.builder.addValue(dataSourceId, containerId, 1, node.getLiteral());
		super.visit(node);
	}

	@Override
	public void visit(IndentedCodeBlock node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		String containerId = handleContainer(node);
		this.builder.addValue(dataSourceId, containerId, 1, node.getLiteral());
		super.visit(node);
	}

	@Override
	public void visit(Image node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		String containerId = handleContainer(node);
		if(node.getTitle()!=null) {
			this.builder.addValue(dataSourceId, containerId, "title", node.getTitle());
		}
		this.builder.addValue(dataSourceId, containerId, "destination", node.getDestination());
		super.visit(node);
	}

	@Override
	public void visit(SoftLineBreak node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(LinkReferenceDefinition node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[toString {}] {}", node.getClass(), node.toString());
		//logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		builder.addValue(dataSourceId, containers.get(node), "title", node.getTitle());
		builder.addValue(dataSourceId, containers.get(node), "destination", node.getDestination());
		builder.addValue(dataSourceId, containers.get(node), "label", node.getLabel());
		super.visit(node);
	}

	@Override
	public void visit(StrongEmphasis node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[toString {}] {}", node.getClass(), node.toString());
		//logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(CustomBlock node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(CustomNode node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		logger.trace("[ignoring]");
//		String parentId = this.containers.get(node.getParent());
//		String containerId = String.join("/",parentId , node.getClass().getSimpleName() , Integer.toString(node.hashCode())) ;
//		handleContainer(node, containerId);
		super.visit(node);
	}

	@Override
	protected void visitChildren(Node parent) {
		super.visitChildren(parent);
	}
}
