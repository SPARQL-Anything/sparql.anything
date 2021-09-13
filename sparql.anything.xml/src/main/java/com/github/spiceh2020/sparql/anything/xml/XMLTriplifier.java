package com.github.spiceh2020.sparql.anything.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
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

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class XMLTriplifier implements Triplifier {

	private static final Logger log = LoggerFactory.getLogger(XMLTriplifier.class);

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		// TODO Not implemented yet
		return triplify(properties);
	}

	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {
		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return DatasetGraphFactory.create();

//		String namespace = properties.getProperty(IRIArgument.NAMESPACE.toString(), Triplifier.XYZ_NS);
		String namespace = Triplifier.getNamespaceArgument(properties);
//		String root = properties.getProperty(IRIArgument.ROOT.toString(), url.toString() + "#");
//		Charset charset = Triplifier.getCharsetArgument(properties);

		String root = Triplifier.getRootArgument(properties, url);

//		boolean blank_nodes = true;
//		if (properties.containsKey(IRIArgument.BLANK_NODES.toString())) {
//			blank_nodes = Boolean.parseBoolean(properties.getProperty(IRIArgument.BLANK_NODES.toString()));
//		}
		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);

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
//				path = path.substring(0, path.lastIndexOf('/'));

				// Collect data if available
				if (charBuilder != null) {
					String value = charBuilder.toString();
					log.trace("collecting char stream: {}", value);
					if (stack.peekLast() != null && value != null && !value.trim().equals("")) {
						Resource resource = stack.peekLast();
						if (!members.containsKey(resource)) {
							members.put(resource, 0);
						}
						int member = members.get(resource) + 1;
						resource.addProperty(RDF.li(member), value);
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
					Resource parent = stack.peekLast();
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
				Resource resource;
				if (blank_nodes == false) {
					if (isRoot) {
						resource = model.createResource(root);
					} else {
//						resource = model.createResource(namespace + event.asStartElement().hashCode());
						resource = model.createResource(String.join("", new String[] { namespace, path.substring(1) }));
					}
				} else {
					resource = model.createResource();
				}
				// If this is the root
				if (isRoot) {
					// Add type root
					resource.addProperty(RDF.type, ResourceFactory.createResource(Triplifier.FACADE_X_TYPE_ROOT));
					isRoot = false;
				}
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
				Iterator<Attribute> attributes = se.getAttributes();
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
