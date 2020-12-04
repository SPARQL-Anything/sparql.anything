package com.github.spiceh2020.sparql.anything.xml;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.*;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLTriplifier implements Triplifier {

	private static final Logger log = LoggerFactory.getLogger(XMLTriplifier.class);

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		String namespace = properties.getProperty(IRIArgument.NAMESPACE.toString(), url.toString() + "#");
		String root = properties.getProperty(IRIArgument.ROOT.toString(), url.toString() + "#");

		Model model = ModelFactory.createDefaultModel();
		//
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEvent event = null;
		XMLEventReader eventReader;
		//
		Deque<Resource> stack = new ArrayDeque<Resource>();
		Map<Resource, Integer> members = new HashMap<Resource, Integer>();
		String path = "";
		StringBuilder charBuilder = null;
		//
		try {
			eventReader = inputFactory.createXMLEventReader(url.openStream());
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}

		while (eventReader.hasNext()) {

			if (event != null && event.isEndElement()) {
				log.trace("element close: {} [{}]", path, stack.size());
				path = path.substring(0, path.lastIndexOf('/'));

				// Collect data if available
				if (charBuilder != null) {
					String value = charBuilder.toString();
					log.trace("collecting char stream: {}", value);
					if (stack.peekLast() != null && value != null && !value.trim().equals("")) {
						stack.peekLast().addProperty(RDF.value, value);
					}
					charBuilder = null;
				}

				// reset current resource
				stack.removeLast();
			}
			try {
				event = eventReader.nextEvent();
			} catch (XMLStreamException e) {
				throw new IOException("Journey interrupted.", e);
			}
			log.trace("event: {}", event);
			if (event.isStartElement()) {
				StartElement se = event.asStartElement();
				String name = se.getName().getPrefix() + ":" + se.getName().getLocalPart();
				path += "/" + name;
				log.trace("element open: {} [{}]", path, stack.size());

				int member = 0;
				if (stack.size() > 0) {
					Resource parent = stack.peekLast();
					member = members.get(parent) + 1;
					members.put(parent, member);
				}
				// XXX Create an RDF resource
				Resource resource;
				resource = model.createResource();
				// XXX Type is element name
				resource.addProperty(RDF.type, ResourceFactory.createResource(toIRI(se.getName(), namespace)));
				if (!members.containsKey(resource)) {
					members.put(resource, 0);
				}
				// Link it with the container membership property
				if (stack.size() > 0) {
					Resource parent = stack.peekLast();
					Property property = RDF.li(member);
					parent.addProperty(property, resource);
				}
				// Attributes
				Iterator attributes = se.getAttributes();
				while (attributes.hasNext()) {
					Attribute attribute = (Attribute) attributes.next();
					log.trace("attribute: {}", attribute);
					Property property = model.createProperty(toIRI(attribute.getName(), namespace));
					resource.addProperty(property, attribute.getValue());
				}
				stack.add(resource);
			} else if (event.isCharacters()) {
				// Characters
				log.trace("character: {}", event);
				if (charBuilder == null) {
					charBuilder = new StringBuilder();
				}
				charBuilder.append(event.asCharacters().getData().trim());
			}
		}
		DatasetGraph dg = DatasetFactory.create(model).asDatasetGraph();
		dg.addGraph(NodeFactory.createURI(url.toString()), model.getGraph());
		return dg;
	}

	private String toIRI(QName qname, String namespace) {
		return (qname.getNamespaceURI().equals("") ? namespace : qname.getNamespaceURI()) + qname.getLocalPart();
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/xml");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("xml");
	}
}
