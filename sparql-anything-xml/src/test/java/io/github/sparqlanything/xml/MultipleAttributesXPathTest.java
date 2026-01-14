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

package io.github.sparqlanything.xml;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Test case for issue with multiple attributes on XML elements when using XPath.
 * This test verifies that all attributes of an element are added as direct properties,
 * not as separate containers with rdf:_1, rdf:_2, etc.
 */
public class MultipleAttributesXPathTest {
	private final XMLTriplifier triplifier = new XMLTriplifier();

	@Test
	public void testMultipleAttributesWithXPath() throws IOException, TriplifierHTTPException {
		URL xmlContent = getClass().getClassLoader().getResource("./MultiAttribute.xml");

		Properties properties = new Properties();
		assertNotNull(xmlContent);
		properties.setProperty(IRIArgument.LOCATION.toString(), xmlContent.toString());
		properties.setProperty(IRIArgument.MEDIA_TYPE.toString(), "application/xml");
		properties.setProperty("xml.path", "//switchIS");
		properties.setProperty(IRIArgument.BLANK_NODES.toString(), "true");
		
		FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(properties);
		triplifier.triplify(properties, builder);
		DatasetGraph dg = builder.getDatasetGraph();
		
		// Print the generated triples for debugging
		//Model model = ModelFactory.createModelForGraph(dg.getDefaultGraph());
		//log.info("Generated RDF:");
		//model.write(System.out, "TTL");
		
		// Find the 'name' element node
		Node nameType = NodeFactory.createURI("http://sparql.xyz/facade-x/data/name");
		Iterator<Quad> nameElements = dg.find(null, null, RDF.type.asNode(), nameType);
		assertTrue("Should find at least one 'name' element", nameElements.hasNext());
		
		Quad nameQuad = nameElements.next();
		Node nameNode = nameQuad.getSubject();
		
		// Check that all three attributes are direct properties of the name element
		Node labelPred = NodeFactory.createURI("http://sparql.xyz/facade-x/data/label");
		Node languagePred = NodeFactory.createURI("http://sparql.xyz/facade-x/data/language");
		Node descriptionPred = NodeFactory.createURI("http://sparql.xyz/facade-x/data/description");
		
		// Verify label attribute
		Iterator<Quad> labelQuads = dg.find(null, nameNode, labelPred, null);
		assertTrue("label attribute should be a direct property", labelQuads.hasNext());
		Quad labelQuad = labelQuads.next();
		assertEquals("A", labelQuad.getObject().getLiteralLexicalForm());
		
		// Verify language attribute
		Iterator<Quad> languageQuads = dg.find(null, nameNode, languagePred, null);
		assertTrue("language attribute should be a direct property", languageQuads.hasNext());
		Quad languageQuad = languageQuads.next();
		assertEquals("en", languageQuad.getObject().getLiteralLexicalForm());
		
		// Verify description attribute
		Iterator<Quad> descriptionQuads = dg.find(null, nameNode, descriptionPred, null);
		assertTrue("description attribute should be a direct property", descriptionQuads.hasNext());
		Quad descriptionQuad = descriptionQuads.next();
		assertEquals("My first switch", descriptionQuad.getObject().getLiteralLexicalForm());
		
		// Verify that there are NO rdf:_1 or rdf:_2 properties on the name element
		// (which would indicate the buggy behavior of creating containers for attributes)
		Iterator<Quad> rdf1Quads = dg.find(null, nameNode, RDF.li(1).asNode(), null);
		assertFalse("name element should NOT have rdf:_1 property (attributes should be direct properties)", rdf1Quads.hasNext());
		
		Iterator<Quad> rdf2Quads = dg.find(null, nameNode, RDF.li(2).asNode(), null);
		assertFalse("name element should NOT have rdf:_2 property (attributes should be direct properties)", rdf2Quads.hasNext());
	}

	@Test
	public void testMultipleAttributesWithXPathBlankNodesFalse() throws IOException, TriplifierHTTPException {
		String xmlContent = """
			<switchesIS>
			    <switchIS id="1">
			     <name label="A" language="en" description="My first switch"/>
			    </switchIS>
			</switchesIS>""";
		
		Properties properties = new Properties();
		properties.setProperty(IRIArgument.CONTENT.toString(), xmlContent);
		properties.setProperty(IRIArgument.MEDIA_TYPE.toString(), "application/xml");
		properties.setProperty("xml.path", "//switchIS");
		properties.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
		
		FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(properties);
		triplifier.triplify(properties, builder);
		DatasetGraph dg = builder.getDatasetGraph();
		
		// Print the generated triples for debugging
		//Model model = ModelFactory.createModelForGraph(dg.getDefaultGraph());
		//log.info("Generated RDF (blank-nodes=false):");
		//model.write(System.out, "TTL");
		
		// Find the 'name' element node
		Node nameType = NodeFactory.createURI("http://sparql.xyz/facade-x/data/name");
		Iterator<Quad> nameElements = dg.find(null, null, RDF.type.asNode(), nameType);
		assertTrue("Should find at least one 'name' element", nameElements.hasNext());
		
		Quad nameQuad = nameElements.next();
		Node nameNode = nameQuad.getSubject();
		
		// The name node should be a URI, not a blank node
		assertTrue("name node should be a URI when blank-nodes=false", nameNode.isURI());
		
		// Check that all three attributes are direct properties of the name element
		Node labelPred = NodeFactory.createURI("http://sparql.xyz/facade-x/data/label");
		Node languagePred = NodeFactory.createURI("http://sparql.xyz/facade-x/data/language");
		Node descriptionPred = NodeFactory.createURI("http://sparql.xyz/facade-x/data/description");
		
		// Verify all three attributes are present as direct properties
		assertTrue("label should be present", dg.find(null, nameNode, labelPred, null).hasNext());
		assertTrue("language should be present", dg.find(null, nameNode, languagePred, null).hasNext());
		assertTrue("description should be present", dg.find(null, nameNode, descriptionPred, null).hasNext());
		
		// Verify no container properties exist
		assertFalse("Should not have rdf:_1", dg.find(null, nameNode, RDF.li(1).asNode(), null).hasNext());
		assertFalse("Should not have rdf:_2", dg.find(null, nameNode, RDF.li(2).asNode(), null).hasNext());
	}
}
