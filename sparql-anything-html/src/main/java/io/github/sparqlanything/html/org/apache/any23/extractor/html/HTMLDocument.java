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

package io.github.sparqlanything.html.org.apache.any23.extractor.html;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.rdf.Any23ValueFactoryWrapper;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper around the DOM representation of an HTML document. Provides convenience access to various parts of the
 * document.
 *
 * @author Gabriele Renzi
 * @author Michele Mostarda
 */
public class HTMLDocument {

    private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();
    private final static Logger log = LoggerFactory.getLogger(HTMLDocument.class);

    private Node document;
    private java.net.URI baseIRI;

    private final Any23ValueFactoryWrapper valueFactory = new Any23ValueFactoryWrapper(
            SimpleValueFactory.getInstance());

    /**
     * Reads a text field from the given node adding the content to the given <i>res</i> list.
     *
     * @param node
     *            the node from which read the content.
     *
     * @return a valid TextField
     */
    public static TextField readTextField(Node node) {
        TextField result;
        final String name = node.getNodeName();
        final NamedNodeMap attributes = node.getAttributes();
        // excess of safety check, should be impossible
        if (attributes == null) {
            return new TextField(node.getTextContent(), node);
        }
        // first check if there are values inside
        List<Node> values = DomUtils.findAllByClassName(node, "value");
        if (!values.isEmpty()) {
            StringBuilder val = new StringBuilder();
            for (Node n : values)
                val.append(n.getTextContent());
            return new TextField(val.toString().trim(), node);
        }
        if ("ABBR".equals(name) && (null != attributes.getNamedItem("title"))) {
            result = new TextField(attributes.getNamedItem("title").getNodeValue(), node);
        } else if ("A".equals(name)) {
            if (DomUtils.hasAttribute(node, "rel", "tag")) {
                String href = extractRelTag(attributes);
                result = new TextField(href, node);
            } else
                result = new TextField(node.getTextContent(), node);
        } else if (("IMG".equals(name) || "AREA".equals(name)) && (null != attributes.getNamedItem("alt"))) {
            result = new TextField(attributes.getNamedItem("alt").getNodeValue(), node);
        } else {
            result = new TextField(node.getTextContent(), node);
        }
        return result;
    }

    /**
     * Reads an URL field from the given node adding the content to the given <i>res</i> list.
     *
     * @param res
     *            {@link List} of {@link io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument.TextField}
     * @param node
     *            the node to read
     */
    public static void readUrlField(List<TextField> res, Node node) {
        String name = node.getNodeName();
        NamedNodeMap attributes = node.getAttributes();
        if (null == attributes) {
            res.add(new TextField(node.getTextContent(), node));
            return;
        }
        if ("A".equals(name) || "AREA".equals(name)) {
            Node n = attributes.getNamedItem("href");
            if (n != null) {
                res.add(new TextField(n.getNodeValue(), n));
            }
        } else if ("ABBR".equals(name)) {
            Node n = attributes.getNamedItem("title");
            if (n != null) {
                res.add(new TextField(n.getNodeValue(), n));
            }
        } else if ("IMG".equals(name)) {
            Node n = attributes.getNamedItem("src");
            if (n != null) {
                res.add(new TextField(n.getNodeValue(), n));
            } else {
                n = attributes.getNamedItem("srcset");
                if (n != null) {
                    res.add(new TextField(n.getNodeValue().split("[\\s,]+")[0], n));
                }
            }
        } else if ("OBJECT".equals(name)) {
            Node n = attributes.getNamedItem("data");
            if (n != null) {
                res.add(new TextField(n.getNodeValue(), n));
            }
        } else {
            res.add(new TextField(extractHCardTextContent(node), node));
        }
    }

    private static String extractHCardTextContent(Node node) {
        StringBuilder sb = new StringBuilder();
        NodeList nodes = node.getChildNodes();
        // if at least one element with 'value' class, concatenate all text in value
        if (extractTextInValue(nodes, sb) == 0) {
            // otherwise, concatenate all text not in elements with 'type' class
            extractTextNotInType(nodes, sb);
        }
        return sb.toString();
    }

    private static int extractTextInValue(NodeList nodes, StringBuilder b) {
        int count = 0;
        for (int i = 0, len = nodes.getLength(); i < len; i++) {
            Node n = nodes.item(i);
            if (DomUtils.hasClassName(n, "value")) {
                count++;
                b.append(n.getTextContent().trim());
            } else {
                count += extractTextInValue(n.getChildNodes(), b);
            }
        }
        return count;
    }

    private static void extractTextNotInType(NodeList nodes, StringBuilder b) {
        for (int i = 0, len = nodes.getLength(); i < len; i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                b.append(n.getNodeValue().trim());
            } else if (!DomUtils.hasClassName(n, "type")) {
                extractTextNotInType(n.getChildNodes(), b);
            }
        }
    }

    /**
     * Extracts the href specific rel-tag string. See the <a href="http://microformats.org/wiki/rel-tag">rel-tag</a>
     * specification.
     *
     * @param hrefAttributeContent
     *            the content of the <i>href</i> attribute.
     *
     * @return the rel-tag specification.
     */
    public static String extractRelTag(String hrefAttributeContent) {
        String[] all = hrefAttributeContent.split("[#?]");
        // Cleanup spurious segments.
        String path = all[0];
        int pathLenghtMin1 = path.length() - 1;
        if ('/' == path.charAt(pathLenghtMin1)) {
            path = path.substring(0, pathLenghtMin1);
        }
        return path;
    }

    /**
     * Extracts the href specific rel-tag string. See the <a href="http://microformats.org/wiki/rel-tag">rel-tag</a>
     * specification.
     *
     * @param attributes
     *            the list of attributes of a node.
     *
     * @return the rel-tag specification.
     */
    public static String extractRelTag(NamedNodeMap attributes) {
        return extractRelTag(attributes.getNamedItem("href").getNodeValue());
    }

    /**
     * Reads the text content of the given node and returns it. If the <code>prettify</code> flag is <code>true</code>
     * the text is cleaned up.
     *
     * @param node
     *            node to read content.
     * @param prettify
     *            if <code>true</code> blank chars will be removed.
     *
     * @return the read text.
     */
    public static String readNodeContent(Node node, boolean prettify) {
        final String content = node.getTextContent();
        return prettify ? content.trim().replaceAll("\\n", " ").replaceAll(" +", " ") : content;
    }

    /**
     * Constructor accepting the root node.
     *
     * @param document
     *            a {@link Node}
     */
    public HTMLDocument(Node document) {
        if (null == document)
            throw new IllegalArgumentException("node cannot be null when constructing an HTMLDocument");
        this.document = document;
    }

    /**
     * @param uri
     *            string to resolve to {@link IRI}
     *
     * @return An absolute IRI, or null if the IRI is not fixable
     *
     * @throws io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException
     *             If the base IRI is invalid
     */
    public IRI resolveIRI(String uri) throws ExtractionException {
        return valueFactory.resolveIRI(uri, getBaseIRI());
    }

    public String find(String xpath) {
        return DomUtils.find(getDocument(), xpath);
    }

    public Node findNodeById(String id) {
        return DomUtils.findNodeById(getDocument(), id);
    }

    public List<Node> findAll(String xpath) {
        return DomUtils.findAll(getDocument(), xpath);
    }

    public String findMicroformattedValue(String objectTag, String object, String fieldTag, String field, String key) {
        Node node = findMicroformattedObjectNode(objectTag, object);
        if (null == node)
            return "";
        // try to check if it is inline
        if (DomUtils.hasClassName(node, field))
            return node.getTextContent();

        // failed, try to find it in a child
        try {
            String xpath = ".//" + fieldTag + "[contains(@class, '" + field + "')]/" + key;
            String value = (String) xPathEngine.evaluate(xpath, node, XPathConstants.STRING);
            if (null == value) {
                return "";
            }
            return value;
        } catch (XPathExpressionException ex) {
            throw new RuntimeException("Should not happen, XPath expression is built locally", ex);
        }

    }

    public Node getDocument() {
        return document;
    }

    /**
     * Returns a singular text field.
     *
     * @param className
     *            name of class containing text.
     *
     * @return if multiple values are found just the first is returned, if we want to check that there are no n-ary
     *         values use plural finder
     */
    public TextField getSingularTextField(String className) {
        TextField[] res = getPluralTextField(className);
        if (res.length == 0)
            return new TextField("", null);
        return res[0];
    }

    /**
     * Returns a plural text field.
     *
     * @param className
     *            name of class node containing text.
     *
     * @return list of fields.
     */
    public TextField[] getPluralTextField(String className) {
        List<TextField> res = new ArrayList<TextField>();
        List<Node> nodes = DomUtils.findAllByClassName(getDocument(), className);
        for (Node node : nodes) {
            res.add(readTextField(node));
        }
        return res.toArray(new TextField[res.size()]);
    }

    /**
     * Returns the URL associated to the field marked with class <i>className</i>.
     *
     * @param className
     *            name of node class containing the URL field.
     *
     * @return if multiple values are found just the first is returned, if we want to check that there are no n-ary
     *         values use plural finder
     */
    public TextField getSingularUrlField(String className) {
        TextField[] res = getPluralUrlField(className);
        if (res.length < 1)
            return new TextField("", null);
        return res[0];
    }

    /**
     * Returns the list of URLs associated to the fields marked with class <i>className</i>.
     *
     * @param className
     *            name of node class containing the URL field.
     *
     * @return the list of {@link TextField} found.
     */
    public TextField[] getPluralUrlField(String className) {
        List<TextField> res = new ArrayList<TextField>();
        List<Node> nodes = DomUtils.findAllByClassName(getDocument(), className);
        for (Node node : nodes)
            readUrlField(res, node);
        return res.toArray(new TextField[res.size()]);
    }

    public Node findMicroformattedObjectNode(String objectTag, String name) {
        List<Node> nodes = DomUtils.findAllByTagAndClassName(getDocument(), objectTag, name);
        if (nodes.isEmpty())
            return null;
        return nodes.get(0);
    }

    /**
     * Read an attribute avoiding NullPointerExceptions, if the attr is missing it just returns an empty string.
     *
     * @param attribute
     *            the attribute name.
     *
     * @return the string representing the attribute.
     */
    public String readAttribute(String attribute) {
        return DomUtils.readAttribute(getDocument(), attribute);
    }

    /**
     * Finds all the nodes by class name.
     *
     * @param clazz
     *            the class name.
     *
     * @return list of matching nodes.
     */
    public List<Node> findAllByClassName(String clazz) {
        return DomUtils.findAllByClassName(getDocument(), clazz);
    }

    /**
     * Returns the text contained inside a node if leaf, <code>null</code> otherwise.
     *
     * @return the text of a leaf node.
     */
    public String getText() {
        NodeList children = getDocument().getChildNodes();
        if (children.getLength() == 1 && children.item(0) instanceof Text) {
            return children.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Returns the document default language.
     *
     * @return default language if any, <code>null</code> otherwise.
     */
    public String getDefaultLanguage() {
        final String xpathLanguageSelector = "/HTML";
        Node html;
        try {
            html = (Node) xPathEngine.evaluate(xpathLanguageSelector, document, XPathConstants.NODE);
        } catch (XPathExpressionException xpeee) {
            throw new IllegalStateException();
        }
        if (html == null) {
            return null;
        }
        Node langAttribute = html.getAttributes().getNamedItem("xml:lang");
        return langAttribute == null ? null : langAttribute.getTextContent();
    }

    /**
     * Returns the sequence of ancestors from the document root to the local root (document).
     *
     * @return a sequence of node names.
     */
    public String[] getPathToLocalRoot() {
        return DomUtils.getXPathListForNode(document);
    }

    /**
     * Extracts all the <code>rel</code> tag nodes.
     *
     * @return list of rel tag nodes.
     */
    public TextField[] extractRelTagNodes() {
        final List<Node> relTagNodes = DomUtils.findAllByAttributeName(getDocument(), "rel");
        final List<TextField> result = new ArrayList<TextField>();
        for (Node relTagNode : relTagNodes) {
            readUrlField(result, relTagNode);
        }
        return result.toArray(new TextField[result.size()]);
    }

    private java.net.URI getBaseIRI() throws ExtractionException {
        if (baseIRI == null) {
            // document.getBaseURI() returns null for document URIs with
            // special characters, e.g., http://semanticweb.org/wiki/Knud_Möller
            // It also does *not* take html "base" elements into account.
            // (But it does take into account urls specified by the attribute "xml:base".)

            // So, for now, let's use getDocumentURI() instead.
            // TODO: Make this approach better.

            Document doc = document instanceof Document ? (Document) document : document.getOwnerDocument();

            if (doc == null) {
                throw new ExtractionException(
                        "Node " + document.getNodeName() + " was not associated with a document.");
            }

            String uri = doc.getDocumentURI();

            if (uri == null) {
                throw new ExtractionException("document URI is null, this should not happen");
            }

            try {
                baseIRI = new java.net.URI(RDFUtils.fixAbsoluteIRI(uri));
            } catch (IllegalArgumentException ex) {
                throw new ExtractionException("Error in base IRI: " + uri, ex);
            } catch (URISyntaxException ex) {
                throw new ExtractionException("Error in base IRI: " + uri, ex);
            }
        }
        return baseIRI;
    }

    /**
     * This class represents a text extracted from the <i>HTML</i> DOM related to the node from which such test has been
     * retrieved.
     */
    public static class TextField {
        private String value;
        private Node source;

        public TextField(String value, Node source) {
            this.value = value;
            this.source = source;
        }

        public String value() {
            return value;
        }

        public Node source() {
            return source;
        }
    }

}
