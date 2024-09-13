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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * This class provides utility methods for DOM manipulation. It is separated from {@link HTMLDocument} so that its
 * methods can be run on single DOM nodes without having to wrap them into an HTMLDocument.
 * <p>
 * We use a mix of XPath and DOM manipulation.
 * </p>
 * This is likely to be a performance bottleneck but at least everything is localized here.
 */
public class DomUtils {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();

    private DomUtils() {
    }

    /**
     * Given a node this method returns the index corresponding to such node within the list of the children of its
     * parent node.
     *
     * @param n
     *            the node of which returning the index.
     *
     * @return a non negative number.
     */
    public static int getIndexInParent(Node n) {
        Node parent = n.getParentNode();
        if (parent == null) {
            return 0;
        }
        NodeList nodes = parent.getChildNodes();
        int counter = -1;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node current = nodes.item(i);
            if (current.getNodeType() == n.getNodeType() && current.getNodeName().equals(n.getNodeName())) {
                counter++;
            }
            if (current.equals(n)) {
                return counter;
            }
        }
        throw new IllegalStateException("Cannot find a child within its parent node list.");
    }

    /**
     * Does a reverse walking of the DOM tree to generate a unique XPath expression leading to this node. The XPath
     * generated is the canonical one based on sibling index: /html[1]/body[1]/div[2]/span[3] etc..
     *
     * @param node
     *            the input node.
     *
     * @return the XPath location of node as String.
     */
    public static String getXPathForNode(Node node) {
        final StringBuilder sb = new StringBuilder();
        Node parent = node;
        while (parent != null && parent.getNodeType() != Node.DOCUMENT_NODE) {
            sb.insert(0, "]");
            sb.insert(0, getIndexInParent(parent) + 1);
            sb.insert(0, "[");
            sb.insert(0, parent.getNodeName());
            sb.insert(0, "/");
            parent = parent.getParentNode();
        }
        return sb.toString();
    }

    /**
     * Returns a list of tag names representing the path from the document root to the given node <i>n</i>.
     *
     * @param n
     *            the node for which retrieve the path.
     *
     * @return a sequence of HTML tag names.
     */
    public static String[] getXPathListForNode(Node n) {
        if (n == null) {
            return EMPTY_STRING_ARRAY;
        }
        List<String> ancestors = new ArrayList<String>();
        ancestors.add(String.format(Locale.ROOT, "%s[%s]", n.getNodeName(), getIndexInParent(n)));
        Node parent = n.getParentNode();
        while (parent != null) {
            ancestors.add(0, String.format(Locale.ROOT, "%s[%s]", parent.getNodeName(), getIndexInParent(parent)));
            parent = parent.getParentNode();
        }
        return ancestors.toArray(new String[ancestors.size()]);
    }

    /**
     * Returns the row/col location of the given node.
     *
     * @param n
     *            input node.
     *
     * @return an array of two elements of type
     *         <code>[&lt;begin-row&gt;, &lt;begin-col&gt;, &lt;end-row&gt; &lt;end-col&gt;]</code> or <code>null</code>
     *         if not possible to extract such data.
     */
    public static int[] getNodeLocation(Node n) {
        if (n == null)
            throw new NullPointerException("node cannot be null.");
        final TagSoupParser.ElementLocation elementLocation = (TagSoupParser.ElementLocation) n
                .getUserData(TagSoupParser.ELEMENT_LOCATION);
        if (elementLocation == null)
            return null;
        return new int[] { elementLocation.getBeginLineNumber(), elementLocation.getBeginColumnNumber(),
                elementLocation.getEndLineNumber(), elementLocation.getEndColumnNumber() };
    }

    /**
     * Checks whether a node is ancestor or same of another node.
     *
     * @param candidateAncestor
     *            the candidate ancestor node.
     * @param candidateSibling
     *            the candidate sibling node.
     * @param strict
     *            if <code>true</code> is not allowed that the ancestor and sibling can be the same node.
     *
     * @return <code>true</code> if <code>candidateSibling</code> is ancestor of <code>candidateSibling</code>,
     *         <code>false</code> otherwise.
     */
    public static boolean isAncestorOf(Node candidateAncestor, Node candidateSibling, boolean strict) {
        if (candidateAncestor == null)
            throw new NullPointerException("candidate ancestor cannot be null null.");
        if (candidateSibling == null)
            throw new NullPointerException("candidate sibling cannot be null null.");
        if (strict && candidateAncestor.equals(candidateSibling))
            return false;
        Node parent = candidateSibling;
        while (parent != null) {
            if (parent.equals(candidateAncestor))
                return true;
            parent = parent.getParentNode();
        }
        return false;
    }

    /**
     * Checks whether a node is ancestor or same of another node. As
     * {@link #isAncestorOf(Node, Node, boolean)} with <code>strict=false</code>.
     *
     * @param candidateAncestor
     *            the candidate ancestor node.
     * @param candidateSibling
     *            the candidate sibling node.
     *
     * @return <code>true</code> if <code>candidateSibling</code> is ancestor of <code>candidateSibling</code>,
     *         <code>false</code> otherwise.
     */
    public static boolean isAncestorOf(Node candidateAncestor, Node candidateSibling) {
        return isAncestorOf(candidateAncestor, candidateSibling, false);
    }

    /**
     * Finds all nodes that have a declared class. Note that the className is transformed to lower case before being
     * matched against the DOM.
     *
     * @param root
     *            the root node from which start searching.
     * @param className
     *            the name of the filtered class.
     *
     * @return list of matching nodes or an empty list.
     */
    public static List<Node> findAllByClassName(Node root, String className) {
        return findAllBy(root, null, "class", className.toLowerCase(Locale.ROOT));
    }

    /**
     * Finds all nodes that have a declared attribute. Note that the className is transformed to lower case before being
     * matched against the DOM.
     *
     * @param root
     *            the root node from which start searching.
     * @param attrName
     *            the name of the filtered attribue.
     *
     * @return list of matching nodes or an empty list.
     */
    public static List<Node> findAllByAttributeName(Node root, String attrName) {
        return findAllBy(root, null, attrName, null);
    }

    public static List<Node> findAllByAttributeContains(Node node, String attrName, String attrContains) {
        return findAllBy(node, null, attrName, attrContains);
    }

    public static List<Node> findAllByTag(Node root, String tagName) {
        return findAllBy(root, tagName, null, null);
    }

    public static List<Node> findAllByTagAndClassName(Node root, final String tagName, final String className) {
        return findAllBy(root, tagName, "class", className);
    }

    /**
     * Mimics the JS DOM API, or prototype's $()
     *
     * @param root
     *            the node to locate
     * @param id
     *            the id of the node to locate
     *
     * @return the {@link Node} if one exists
     */
    public static Node findNodeById(Node root, String id) {
        Node node;
        try {
            String xpath = "//*[@id='" + id + "']";
            node = (Node) xPathEngine.evaluate(xpath, root, XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            throw new RuntimeException("Should not happen", ex);
        }
        return node;
    }

    /**
     * Returns a NodeList composed of all the nodes that match an XPath expression, which must be valid.
     *
     * @param node
     *            the node object to locate
     * @param xpath
     *            an xpath expression
     *
     * @return a list of {@link Node}'s if they exists
     */
    public static List<Node> findAll(Node node, String xpath) {
        if (node == null) {
            throw new NullPointerException("node cannot be null.");
        }
        try {
            NodeList nodes = (NodeList) xPathEngine.evaluate(xpath, node, XPathConstants.NODESET);
            List<Node> result = new ArrayList<Node>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                result.add(nodes.item(i));
            }
            return result;
        } catch (XPathExpressionException ex) {
            throw new IllegalArgumentException("Illegal XPath expression: " + xpath, ex);
        }
    }

    /**
     * Gets the string value of an XPath expression.
     *
     * @param node
     *            the node object to locate
     * @param xpath
     *            an xpath expression
     *
     * @return a string xpath value
     */
    public static String find(Node node, String xpath) {
        try {
            String val = (String) xPathEngine.evaluate(xpath, node, XPathConstants.STRING);
            if (null == val)
                return "";
            return val;
        } catch (XPathExpressionException ex) {
            throw new IllegalArgumentException("Illegal XPath expression: " + xpath, ex);
        }
    }

    /**
     * Tells if an element has a class name <b>not checking the parents in the hierarchy</b> mimicking the <i>CSS</i>
     * .foo match.
     *
     * @param node
     *            the node object to locate
     * @param className
     *            the CSS class name
     *
     * @return true if the class name exists
     */
    public static boolean hasClassName(Node node, String className) {
        return hasAttribute(node, "class", className);
    }

    /**
     * Checks the presence of an attribute value in attributes that contain whitespace-separated lists of values. The
     * semantic is the CSS classes' ones: "foo" matches "bar foo", "foo" but not "foob"
     *
     * @param node
     *            the node object to locate
     * @param attributeName
     *            attribute value
     * @param className
     *            the CSS class name
     *
     * @return true if the class has the attribute name
     */
    public static boolean hasAttribute(Node node, String attributeName, String className) {
        // regex love, maybe faster but less easy to understand
        // Pattern pattern = Pattern.compile("(^|\\s+)"+className+"(\\s+|$)");
        String attr = readAttribute(node, attributeName);
        for (String c : attr.split("\\s+"))
            if (c.equalsIgnoreCase(className))
                return true;
        return false;
    }

    /**
     * Checks the presence of an attribute in the given <code>node</code>.
     *
     * @param node
     *            the node container.
     * @param attributeName
     *            the name of the attribute.
     *
     * @return true if the attribute is present
     */
    public static boolean hasAttribute(Node node, String attributeName) {
        return readAttribute(node, attributeName, null) != null;
    }

    /**
     * Verifies if the given target node is an element.
     *
     * @param target
     *            target node to check
     *
     * @return <code>true</code> if the element the node is an element, <code>false</code> otherwise.
     */
    public static boolean isElementNode(Node target) {
        return Node.ELEMENT_NODE == target.getNodeType();
    }

    /**
     * Reads the value of the specified <code>attribute</code>, returning the <code>defaultValue</code> string if not
     * present.
     *
     * @param node
     *            node to read the attribute.
     * @param attribute
     *            attribute name.
     * @param defaultValue
     *            the default value to return if attribute is not found.
     *
     * @return the attribute value or <code>defaultValue</code> if not found.
     */
    public static String readAttribute(Node node, String attribute, String defaultValue) {
        NamedNodeMap attributes = node.getAttributes();
        if (null == attributes)
            return defaultValue;
        Node attr = attributes.getNamedItem(attribute);
        if (null == attr)
            return defaultValue;
        return attr.getNodeValue();
    }

    /**
     * Reads the value of the first <i>attribute</i> which name matches with the specified <code>attributePrefix</code>.
     * Returns the <code>defaultValue</code> if not found.
     *
     * @param node
     *            node to look for attributes.
     * @param attributePrefix
     *            attribute prefix.
     * @param defaultValue
     *            default returned value.
     *
     * @return the value found or default.
     */
    public static String readAttributeWithPrefix(Node node, String attributePrefix, String defaultValue) {
        final NamedNodeMap attributes = node.getAttributes();
        if (null == attributes) {
            return defaultValue;
        }
        Node attribute;
        for (int a = 0; a < attributes.getLength(); a++) {
            attribute = attributes.item(a);
            if (attribute.getNodeName().startsWith(attributePrefix)) {
                return attribute.getNodeValue();
            }
        }
        return defaultValue;
    }

    /**
     * Reads the value of an <code>attribute</code>, returning the empty string if not present.
     *
     * @param node
     *            node to read the attribute.
     * @param attribute
     *            attribute name.
     *
     * @return the attribute value or <code>""</code> if not found.
     */
    public static String readAttribute(Node node, String attribute) {
        return readAttribute(node, attribute, "");
    }

    /**
     * Given a <i>DOM</i> {@link Node} produces the <i>XML</i> serialization omitting the <i>XML declaration</i>.
     *
     * @param node
     *            node to be serialized.
     * @param indent
     *            if <code>true</code> the output is indented.
     *
     * @return the XML serialization.
     *
     * @throws TransformerException
     *             if an error occurs during the serializator initialization and activation.
     * @throws IOException
     *             if there is an error locating the node
     */
    public static String serializeToXML(Node node, boolean indent) throws TransformerException, IOException {
        final DOMSource domSource = new DOMSource(node);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        if (indent) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        }
        final StringWriter sw = new StringWriter();
        final StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        sw.close();
        return sw.toString();
    }

    /**
     * High performance implementation of {@link #findAll(Node, String)}.
     *
     * @param root
     *            root node to start search.
     * @param tagName
     *            name of target tag.
     * @param attrName
     *            name of attribute filter.
     * @param attrContains
     *            expected content for attribute.
     *
     * @return a {@link List} of {@link Node}'s
     */
    private static List<Node> findAllBy(Node root, final String tagName, final String attrName, String attrContains) {
        DocumentTraversal documentTraversal = (DocumentTraversal) root.getOwnerDocument();
        if (documentTraversal == null) {
            documentTraversal = (DocumentTraversal) root;
        }

        final Pattern attrContainsPattern;
        if (attrContains != null && !attrContains.equals("*")) {
            attrContainsPattern = Pattern.compile("(^|\\s)" + attrContains + "(\\s|$)", Pattern.CASE_INSENSITIVE);
        } else {
            attrContainsPattern = null;
        }

        final List<Node> result = new ArrayList<Node>();
        NodeIterator nodeIterator = documentTraversal.createNodeIterator(root, NodeFilter.SHOW_ELEMENT,
                new NodeFilter() {
                    @Override
                    public short acceptNode(Node node) {
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            if (tagName != null && !tagName.equals("*") && !tagName.equals(node.getNodeName())) {
                                // tagName given but doesn't match.
                                return FILTER_ACCEPT;
                            }

                            if (attrName != null) {
                                Node attrNameNode = node.getAttributes().getNamedItem(attrName);
                                if (attrNameNode == null) {
                                    // attrName given but doesn't match
                                    return FILTER_ACCEPT;
                                }

                                if (attrContainsPattern != null
                                        && !attrContainsPattern.matcher(attrNameNode.getNodeValue()).find()) {
                                    // attrContains given but doesn't match
                                    return FILTER_ACCEPT;
                                }
                            }
                            result.add(node);
                        }
                        return FILTER_ACCEPT;
                    }
                }, false);

        // To populate result we only need to iterate...
        while (nodeIterator.nextNode() != null)
            ;

        // We have to explicitly declare we are done with this nodeIterator to free it's resources.
        nodeIterator.detach();

        return result;
    }

    /**
     * Given a {@link Document} this method will return an input stream representing that document.
     *
     * @param doc
     *            the input {@link Document}
     *
     * @return an {@link InputStream}
     */
    public static InputStream documentToInputStream(Document doc) {
        DOMSource source = new DOMSource(doc);
        StringWriter xmlAsWriter = new StringWriter();
        StreamResult result = new StreamResult(xmlAsWriter);
        try {
            TransformerFactory.newInstance().newTransformer().transform(source, result);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Error within Document to InputStream transformation configuration!");
        } catch (TransformerException e) {
            throw new RuntimeException("Error whilst transforming the Document to InputStream!");
        } catch (TransformerFactoryConfigurationError e) {
            throw new RuntimeException("Error within Document to InputStream transformation configuration factory!");
        }

        InputStream is = null;
        try {
            is = new ByteArrayInputStream(xmlAsWriter.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error obtaining data with \"UTF-8\" encoding!", e);
        }
        return is;
    }

    /**
     * Convert a w3c dom node to a InputStream
     *
     * @param node
     *            {@link Node} to convert
     *
     * @return the converted {@link InputStream}
     */
    public static InputStream nodeToInputStream(Node node) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result outputTarget = new StreamResult(outputStream);
        Transformer t = null;
        try {
            t = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Serious configuration error.", e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new RuntimeException("Serious configuration error.", e);
        }
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        try {
            t.transform(new DOMSource(node), outputTarget);
        } catch (TransformerException e) {
            throw new RuntimeException("Error whilst transforming the Node to InputStream!");
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
