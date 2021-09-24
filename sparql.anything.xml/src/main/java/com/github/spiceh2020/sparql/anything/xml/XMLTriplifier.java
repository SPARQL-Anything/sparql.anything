package com.github.spiceh2020.sparql.anything.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.github.spiceh2020.sparql.anything.model.TriplifierHTTPException;
import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import org.apache.jena.ext.com.google.common.collect.Sets;
//import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.apache.jena.rdf.model.Property;
//import org.apache.jena.rdf.model.Resource;
//import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class XMLTriplifier implements Triplifier {

	private static final Logger log = LoggerFactory.getLogger(XMLTriplifier.class);

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return DatasetGraphFactory.create();

		String namespace = Triplifier.getNamespaceArgument(properties);
		String dataSourceId = url.toString();
		String root = Triplifier.getRootArgument(properties, url);

		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// TODO allow users to configure XML parser via properties
		inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

		XMLEvent event = null;
		XMLEventReader eventReader;
		//
		Deque<String> stack = new ArrayDeque<String>();
		Map<String, Integer> members = new HashMap<String, Integer>();
		String path = "";
		StringBuilder charBuilder = null;
		//
		try {
			InputStream is = Triplifier.getInputStream(url, properties);
			eventReader = inputFactory.createXMLEventReader(is);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
		boolean isRoot = true;
		while (eventReader.hasNext()) {

			if (event != null && event.isEndElement()) {
				log.trace("element close: {} [{}]", path, stack.size());
				// Remove the last path
				path = path.substring(0, path.lastIndexOf('/'));

				// Collect data if available
				if (charBuilder != null) {
					String value = charBuilder.toString();
					log.trace("collecting char stream: {}", value);
					if (stack.peekLast() != null && value != null && !value.trim().equals("")) {
						String resourceId = stack.peekLast();
						if (!members.containsKey(resourceId)) {
							members.put(resourceId, 0);
						}
						int member = members.get(resourceId) + 1;
						builder.addValue(dataSourceId, resourceId, member, value);
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
				String name;
				if (se.getName().getPrefix().equals("")) {
					name = se.getName().getLocalPart();
				} else {
					name = se.getName().getPrefix() + ":" + se.getName().getLocalPart();
				}
				int member = 0;
				if (stack.size() > 0) {
					String parent = stack.peekLast();
					member = members.get(parent) + 1;
					members.put(parent, member);
				}

				if (path.equals("")) {
					path = String.join("", "/", name);
				} else {
					path = String.join("", path, "/", Integer.toString(member), ":", name);
				}
				log.trace("element open: {} [{}]", path, stack.size());

				// XXX Create an RDF resource
				String resourceId = path.substring(1);
				// If this is the root
				if (isRoot) {
					// Add type root
					builder.addRoot(dataSourceId, resourceId);
					isRoot = false;
				}
				try {
					builder.addType(dataSourceId, resourceId, new URI(toIRI(se.getName(), namespace)));
				} catch (URISyntaxException e) {
					throw new IOException(e);
				}
				if (!members.containsKey(resourceId)) {
					members.put(resourceId, 0);
				}
				// Link it with the container membership property
				if (stack.size() > 0) {
					String parent = stack.peekLast();
					builder.addContainer(dataSourceId, parent, member, resourceId);
				}
				// Attributes
				Iterator<Attribute> attributes = se.getAttributes();
				while (attributes.hasNext()) {
					Attribute attribute = (Attribute) attributes.next();
					log.trace("attribute: {}", attribute);
					try {
						builder.addValue(dataSourceId, resourceId, new URI(toIRI(attribute.getName(), namespace)), attribute.getValue());
					} catch (URISyntaxException e) {
						throw new IOException(e);
					}
				}
				stack.add(resourceId);
			} else if (event.isCharacters()) {
				// Characters
				log.trace("character: {}", event);
				if (charBuilder == null) {
					charBuilder = new StringBuilder();
				}
				charBuilder.append(event.asCharacters().getData().trim());
			}
		}
		return builder.getDatasetGraph();
	}

	private String toIRI(QName qname, String namespace) {
		String ns;
		if(qname.getNamespaceURI().equals("")){
			ns = namespace;
		}else{
			if(qname.getNamespaceURI().endsWith("/") || qname.getNamespaceURI().endsWith("#") ){
				ns = qname.getNamespaceURI();
			}else{
				ns = qname.getNamespaceURI() + '#';
			}
		}
		return ns + qname.getLocalPart();
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
