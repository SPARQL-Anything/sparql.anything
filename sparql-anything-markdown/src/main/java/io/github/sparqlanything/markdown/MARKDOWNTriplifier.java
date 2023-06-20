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

package io.github.sparqlanything.markdown;

import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierHTTPException;
import com.google.common.collect.Sets;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.LinkReferenceDefinition;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MARKDOWNTriplifier extends AbstractVisitor implements Triplifier {

	private final static Logger logger = LoggerFactory.getLogger(MARKDOWNTriplifier.class);

//	private URL url;
	private Properties properties;
	private FacadeXGraphBuilder builder;
	private String dataSourceId;
	private String rootId;
	private Map<Node, String> containers;
	private Map<Class<? extends Node>, Integer> typeCounter;
	private int lastLevel = 0;
	private Map<String,Integer> lastSlot;

	private void before(Properties properties, FacadeXGraphBuilder builder, String dataSourceId, String rootId){
//		this.url = url;
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
//		url = null;
		properties = null;
		builder = null;
		dataSourceId = null;
		rootId = null;
		this.containers = null;
		this.lastLevel = 0;
		this.lastSlot = null;
	}

	private void transform(Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {

		final InputStream us = Triplifier.getInputStream(properties);
		final InputStreamReader reader = new InputStreamReader(us);
		List<Extension> extensions = Arrays.asList(TablesExtension.create(), YamlFrontMatterGathererExtension.create());
		Parser parser = Parser.builder().extensions(extensions).build();
		Node document = parser.parseReader(reader);

		try {
			// Only 1 data source expected
			String dataSourceId = "";
			rootId =  Triplifier.getRootArgument(properties);
			logger.trace("ds {} root {}", dataSourceId, rootId);
			before(properties, builder, dataSourceId, rootId);
			document.accept(this);
		} finally {
			us.close();
			after();
		}
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);
		transform(properties, builder);
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
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
		// Document is the root container
		String containerId = rootId ;
		try {
			this.builder.addType(dataSourceId, containerId, new URI(XYZ_NS + node.getClass().getSimpleName()));
		} catch (URISyntaxException e) {
			logger.error("This should never happen", e);
		}
//		this.lastSlot.put(parentId, slot);
		this.containers.put(node, containerId);
		this.lastSlot.put(containerId, 0);
		this.builder.addRoot(dataSourceId, rootId);
		this.visitChildren(node);
	}

	@Override
	public void visit(Heading node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
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
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		logger.trace("[Spans {}]", node.getSourceSpans());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(OrderedList node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		logger.trace("[Delimiter] {}", node.getDelimiter());
		logger.trace("[Start number] {}", node.getStartNumber());

		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(BulletList node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(Paragraph node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		logger.trace("[SourceSpans] {}", node.getSourceSpans());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(Text node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
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
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
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
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	@Override
	public void visit(Emphasis node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
//		logger.trace("[toString {}] {}", node.getClass(), node.toString());
		if(logger.isTraceEnabled() && node.getFirstChild()!=null) {
			logger.trace("[First child {}] {}", node.getFirstChild().getClass(), node.getFirstChild());
		}
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
		if(node.getTitle()!=null)
			builder.addValue(dataSourceId, containers.get(node), "title", node.getTitle());
		if(node.getDestination()!=null)
			builder.addValue(dataSourceId, containers.get(node), "destination", node.getDestination());
		if(node.getLabel()!=null)
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
		if(node instanceof YamlFrontMatter){
			builder.addValue(dataSourceId, containers.get(node), 1, ((YamlFrontMatter) node).getContent() );
		}
		super.visit(node);
	}

	@Override
	public void visit(CustomNode node) {
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		if(node instanceof TableHead){
			visit((TableHead) node);
		}else if(node instanceof TableRow){
			visit((TableRow) node);
		}else if(node instanceof TableCell){
			visit((TableCell) node);
		}else if(node instanceof TableBody){
			visit((TableBody) node);
		}else {
			logger.trace("[ignoring] {}", node.toString());
		}
//		super.visit(node);
	}

	public void visit(YamlFrontMatter node){

	}

	public void visit(TableHead node){
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	public void visit(TableRow node){
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	public void visit(TableBody node){
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		super.visit(node);
	}

	public void visit(TableCell node){
		logger.trace("[Visiting {}] {}", node.getClass(), node);
		logger.trace("[Parent {}] {}", node.getParent().getClass(), node.getParent());
		handleContainer(node);
		if(node.getAlignment() != null){
			builder.addValue(dataSourceId, containers.get(node), "alignment", node.getAlignment().name());
		}
		super.visit(node);
	}

	@Override
	protected void visitChildren(Node parent) {
		super.visitChildren(parent);
	}
}
