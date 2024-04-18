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

package io.github.sparqlanything.xml;

import io.github.sparqlanything.model.*;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@io.github.sparqlanything.model.annotations.Triplifier
public class XMLTriplifier implements Triplifier, Slicer {

	@Example(resource = "https://sparql-anything.cc/examples/simple-menu.xml", query = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> CONSTRUCT { ?s ?p ?o . } WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:location \"https://sparql-anything.cc/examples/simple-menu.xml\" ; fx:xml.path \"//food\" ; fx:blank-nodes false . ?s ?p ?o } }")
	@Option(description = "One or more XPath expressions as filters. E.g. `xml.path=value` or `xml.path.1`, `xml.path.2`,`...` to add multiple expressions.", validValues = "Any valid XPath")
	public static final IRIArgument PROPERTY_XPATH = new IRIArgument("xml.path");
	private static final Logger log = LoggerFactory.getLogger(XMLTriplifier.class);

	public void transformWithXPath(List<String> xpaths, Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {

		String dataSourceId = "";
		String rootId = SPARQLAnythingConstants.ROOT_ID;

		builder.addRoot(dataSourceId);
		try {
			VTDNav vn = buildVTDNav(properties);
			Iterator<Pair<VTDNav, Integer>> it = evaluateXPaths(vn, xpaths);
			int count = 1;
			while (it.hasNext()) {
				Pair<VTDNav, Integer> next = it.next();
				transformFromXPath(next.getKey(), next.getValue(), count, rootId, dataSourceId, builder);
				count++;
			}
			log.debug("XPath: {} matches", count);

		} catch (NavException | ParseException e) {
			log.error("Error while evaluating XPath expression");
			throw new IOException(e);
		}
	}

	public int transformFromXPath(VTDNav vn, int result, int child, String parentId, String dataSourceId, FacadeXGraphBuilder builder) throws NavException {
		log.trace(" -- index: {} type: {}", result, vn.getTokenType(result));
		switch (vn.getTokenType(result)) {
			case VTDNav.TOKEN_STARTING_TAG:
				String tag;
				tag = vn.toString(result);

				log.trace(" -- tag: {} ", tag);
				String childId = String.join("", parentId, "/", Integer.toString(child), ":", tag);
				builder.addContainer(dataSourceId, parentId, child, childId);
				builder.addType(dataSourceId, childId, tag);

				// Attributes
				int attrCount = vn.getAttrCount();
				log.trace(" -- attr count: {}", attrCount);
				int increment = 0;
				if (attrCount > 0) {
					for (int i = result + 1; i <= result + attrCount; i += 2) {
						// Not sure why but sometime attrCount is not reliable
						if (vn.getTokenType(i) != VTDNav.TOKEN_ATTR_NAME) {
							break;
						}
						String key = vn.toString(i);
						String value = vn.toString(i + 1);
						log.trace(" -- attr: {} = {}", key, value);
						builder.addValue(dataSourceId, childId, key, value);
						increment += 2;
					}
				}
				// Get the text
				int t = vn.getText(); // get the index of the text (char data or CDATA)
				if (t != -1) {
					String text = vn.toNormalizedString(t);
					log.trace(" -- text: {}", text);
					builder.addValue(dataSourceId, childId, 1, text);
				}

				// Iterate on Children until complete
				int tokenDepth = vn.getTokenDepth(result);
				int index = result + increment;
				int childc = 1;
				while (true) {
					index++;
					int type = vn.getTokenType(index);
					String s = vn.toString(index);
					int d = vn.getTokenDepth(index);
					// If type is element and depth is not greater than tokenDepth, break!
					if ((type == VTDNav.TOKEN_STARTING_TAG && d <= tokenDepth) || (type == VTDNav.TOKEN_STARTING_TAG && s.equals(""))) {
						break;
					}
					log.trace(" ...  index: {} depth: {} type: {} string: {}", index, d, type, s);
					index = transformFromXPath(vn, index, childc, childId, dataSourceId, builder);
					childc++;
				}
				return index - 1;
			case VTDNav.TOKEN_ATTR_NAME:
				// Attribute
				String name = vn.toString(result);
				String value = vn.toString(result + 1);
				log.trace("Attribute {} = {}", name, value);
				String attrChildId = String.join("", parentId, "/", Integer.toString(child), ":", name);
				builder.addContainer(dataSourceId, parentId, child, attrChildId);
				builder.addValue(dataSourceId, attrChildId, name, value);
				return result + 1;
			case VTDNav.TOKEN_ATTR_VAL:
				// Attribute value
				log.trace("Attribute value: {}", vn.toString(result));
				builder.addValue(dataSourceId, parentId, child, vn.toString(result));
				break;
			case VTDNav.TOKEN_CHARACTER_DATA:
				// Text
				String text = vn.toNormalizedString(result);
				log.trace("Text: {}", text);
				builder.addValue(dataSourceId, parentId, child, vn.toString(result));
				break;
			case VTDNav.TOKEN_DEC_ATTR_NAME:
				log.trace("Attribute (dec): {} = {}", vn.toString(result), vn.toString(result + 1));
				return result + 1;
			case VTDNav.TOKEN_DEC_ATTR_VAL:
				log.trace("Attribute value (dec) {}", vn.toString(result));
				break;
			default:
				log.warn("Ignored event: {} {}", vn.getTokenType(result), vn.toString(result));
		}
		return result;
	}

	public void transformSAX(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {

		String namespace = PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE);
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		String root = SPARQLAnythingConstants.ROOT_ID;

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// TODO allow users to configure XML parser via properties
		inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

		XMLEvent event = null;
		XMLEventReader eventReader;
		//
		Deque<String> stack = new ArrayDeque<>();
		Map<String, Integer> members = new HashMap<>();
		String path = "";
		StringBuilder charBuilder = null;
		//
		try {
			InputStream is = Triplifier.getInputStream(properties);
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
					if (stack.peekLast() != null && !value.trim().equals("")) {
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
				// Handle case where TEXT element is *between* parent and child tag -- See Issue 325
				// Collect data if available
				if (charBuilder != null) {
					String value = charBuilder.toString();
					log.trace("collecting char stream: {}", value);
					if (stack.peekLast() != null && !value.trim().equals("")) {
						String resourceId = stack.peekLast();
						if (!members.containsKey(resourceId)) {
							members.put(resourceId, 0);
						}
						int member = members.get(resourceId) + 1;
						builder.addValue(dataSourceId, resourceId, member, value);
					}
					charBuilder = null;
				}

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
				String resourceId = StringUtils.join("", root, path);
				// If this is the root
				if (isRoot) {
					// Add type root
					builder.addRoot(dataSourceId);
					resourceId = root;
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
					Attribute attribute = attributes.next();
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
	}

	private String toIRI(QName qname, String namespace) {
		String ns;
		if (qname.getNamespaceURI().equals("")) {
			ns = namespace;
		} else {
			if (qname.getNamespaceURI().endsWith("/") || qname.getNamespaceURI().endsWith("#")) {
				ns = qname.getNamespaceURI();
			} else {
				ns = qname.getNamespaceURI() + '#';
			}
		}
		return ns + qname.getLocalPart();
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {
		List<String> xpaths = PropertyUtils.getPropertyValues(properties, PROPERTY_XPATH);
		if (!xpaths.isEmpty()) {
			transformWithXPath(xpaths, properties, builder);
		} else {
			transformSAX(properties, builder);
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/xml");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("xml");
	}

	@Override
	public Iterable<Slice> slice(Properties properties) throws IOException, TriplifierHTTPException {
		final String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		List<String> xpaths = PropertyUtils.getPropertyValues(properties, PROPERTY_XPATH);

		try {
			VTDNav vn = buildVTDNav(properties);
			final Iterator<Pair<VTDNav, Integer>> it = evaluateXPaths(vn, xpaths);
			return () -> new Iterator<>() {
				int theCount = 1;

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Slice next() {
					Pair<VTDNav, Integer> pair = it.next();
					int c = theCount;
					theCount++;
					return XPathSlice.make(pair.getKey(), pair.getValue(), c, dataSourceId);
				}
			};
		} catch (Exception e) {
			throw new RuntimeException((e));
		}
	}

	private VTDNav buildVTDNav(Properties properties) throws TriplifierHTTPException, IOException, ParseException {
		VTDGen vg = new VTDGen();
		byte[] bytes = IOUtils.toByteArray(Triplifier.getInputStream(properties));
		vg.setDoc(bytes);
		// TODO Support namespaces
		vg.parse(false);
		return vg.getNav();
	}

	private Iterator<Pair<VTDNav, Integer>> evaluateXPaths(VTDNav vn, List<String> xpaths) {
		return new Iterator<>() {
			final Iterator<String> xit = xpaths.iterator();
			final AutoPilot ap = new AutoPilot(vn);
			Pair<VTDNav, Integer> next = null;
			String xpath = null;

			@Override
			public boolean hasNext() {
				try {
					if (next != null) {
						return true;
					}
					// If XPath is already loaded, move to the next result
					int result = -1;
					if (xpath != null) {
						result = ap.evalXPath();
						if (result == -1) {
							// No more results with this XPath
							xpath = null;
						}
					}
					// Look for next xpath with results
					while (xpath == null && xit.hasNext()) {
						// If XPath is available, load it and move to the next result
						xpath = xit.next();
						log.debug("Evaluating XPath: {}", xpath);
						ap.selectXPath(xpath);
						result = ap.evalXPath();
						if (result == -1) {
							xpath = null;
						} else {
							// Stop here for now
							break;
						}
					}

					if (result == -1) {
						// No more results
						return false;
					} else {
						// Prepare next result
						next = Pair.of(vn, result);
						return true;
					}
				} catch (XPathParseException | XPathEvalException | NavException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public Pair<VTDNav, Integer> next() {
				Pair<VTDNav, Integer> toReturn = next;
				next = null;
				return toReturn;
			}
		};
	}

	@Override
	public void triplify(Slice slice, Properties properties, FacadeXGraphBuilder builder) {
		builder.addRoot(slice.getDatasourceId());
		if (slice instanceof XPathSlice) {
			XPathSlice xs = (XPathSlice) slice;
			try {
				transformFromXPath(xs.get().getKey(), xs.get().getValue(), xs.iteration(), SPARQLAnythingConstants.ROOT_ID, slice.getDatasourceId(), builder);
			} catch (NavException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("Not the expected slice (" + XPathSlice.class + ")");
		}
	}
}
