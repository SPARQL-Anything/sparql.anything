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
package io.github.sparqlanything.html.org.apache.any23.extractor.microdata;

import io.github.sparqlanything.html.org.apache.any23.extractor.html.DomUtils;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.MicrodataParserException;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.MicrodataParserReport;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.datatypes.XMLDatatypeUtil;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.jsoup.parser.Tag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class provides utility methods for handling <b>Microdata</b> nodes contained within a <i>DOM</i> document.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Hans Brende (hansbrende@apache.org)
 */
public class MicrodataParser {

    enum ErrorMode {
        /** This mode raises an exception at first encountered error. */
        STOP_AT_FIRST_ERROR,
        /** This mode produces a full error report. */
        FULL_REPORT
    }

    private final Document document;

    /**
     * This set holds the name of properties being dereferenced. The {@link #deferProperties(String...)} checks first if
     * the required dereference has been already asked, if so raises a loop detection error. This map works in
     * coordination with {@link #dereferenceRecursionCounter}, so that at the end of {@link #deferProperties(String...)}
     * call recursion the loopDetectorSet can be cleaned up.
     */
    private final Set<String> loopDetectorSet = new HashSet<>();

    /**
     * {@link io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope} cache.
     */
    private final Map<Node, io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope> itemScopes = new HashMap<>();

    /**
     * {@link io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue} cache.
     */
    private final Map<Node, io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue> itemPropValues = new HashMap<>();

    /**
     * Counts the recursive call of {@link #deferProperties(String...)}. It helps to cleanup the
     * {@link #loopDetectorSet} when recursion ends.
     */
    private int dereferenceRecursionCounter = 0;

    /**
     * Current error mode.
     */
    private ErrorMode errorMode = ErrorMode.FULL_REPORT;

    /**
     * List of collected errors. Used when {@link #errorMode} <code>==</code> {@link ErrorMode#FULL_REPORT}.
     */
    private final List<MicrodataParserException> errors = new ArrayList<>();

    public static final String ITEMSCOPE_ATTRIBUTE = "itemscope";
    public static final String ITEMPROP_ATTRIBUTE = "itemprop";
    private static final String REVERSE_ITEMPROP_ATTRIBUTE = "itemprop-reverse";

    /**
     * List of tags providing the <code>src</code> property.
     */
    public static final Set<String> SRC_TAGS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("audio",
            "embed", "frame", "iframe", "img", "source", "track", "video", "input", "layer", "script", "textarea")));

    /**
     * List of tags providing the <code>href</code> property.
     */
    public static final Set<String> HREF_TAGS = Collections
            .unmodifiableSet(new HashSet<String>(Arrays.asList("a", "area", "link")));

    public MicrodataParser(Document document) {
        if (document == null) {
            throw new NullPointerException("Document cannot be null.");
        }
        this.document = document;
    }

    /**
     * Returns all the <i>itemScope</i>s detected within the given root node.
     *
     * @param node
     *            root node to search in.
     *
     * @return list of detected items.
     */
    public static List<Node> getItemScopeNodes(Node node) {
        return DomUtils.findAllByAttributeName(node, ITEMSCOPE_ATTRIBUTE);
    }

    /**
     * Check whether a node is an <i>itemScope</i>.
     *
     * @param node
     *            node to check.
     *
     * @return <code>true</code> if the node is an <i>itemScope</i>., <code>false</code> otherwise.
     */
    public static boolean isItemScope(Node node) {
        return DomUtils.readAttribute(node, ITEMSCOPE_ATTRIBUTE, null) != null;
    }

    /**
     * Returns all the <i>itemProp</i>s detected within the given root node.
     *
     * @param node
     *            root node to search in.
     *
     * @return list of detected items.
     */
    public static List<Node> getItemPropNodes(Node node) {
        return DomUtils.findAllByAttributeName(node, ITEMPROP_ATTRIBUTE);
    }

    /**
     * Check whether a node is an <i>itemProp</i>.
     *
     * @param node
     *            node to check.
     *
     * @return <code>true</code> if the node is an <i>itemProp</i>., <code>false</code> otherwise.
     */
    public static boolean isItemProp(Node node) {
        return DomUtils.readAttribute(node, ITEMPROP_ATTRIBUTE, null) != null;
    }

    private static boolean isContainedInItemScope(Node node) {
        for (Node p = node.getParentNode(); p != null; p = p.getParentNode()) {
            NamedNodeMap attrs = p.getAttributes();
            if (attrs != null && attrs.getNamedItem(ITEMSCOPE_ATTRIBUTE) != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean isContainedInId(Node node, Set<String> ids) {
        do {
            String id = DomUtils.readAttribute(node, "id", null);
            if (id != null && ids.contains(id)) {
                return true;
            }
            node = node.getParentNode();
        } while (node != null);
        return false;
    }

    /**
     * Returns only the <i>itemScope</i>s that are top level items.
     *
     * @param node
     *            root node to search in.
     *
     * @return list of detected top item scopes.
     */
    public static List<Node> getTopLevelItemScopeNodes(Node node) {
        final List<Node> itemScopes = getItemScopeNodes(node);
        final List<Node> topLevelItemScopes = new ArrayList<>();
        final List<Node> possibles = new ArrayList<>();
        for (Node itemScope : itemScopes) {
            if (!isItemProp(itemScope) && DomUtils.readAttribute(itemScope, REVERSE_ITEMPROP_ATTRIBUTE, null) == null) {
                topLevelItemScopes.add(itemScope);
            } else if (!isContainedInItemScope(itemScope)) {
                possibles.add(itemScope);
            }
        }

        if (!possibles.isEmpty()) {
            Set<String> refIds = itemScopes.stream().flatMap(n -> Arrays.stream(itemrefIds(n)))
                    .collect(Collectors.toSet());

            for (Node itemScope : possibles) {
                if (!isContainedInId(itemScope, refIds)) {
                    topLevelItemScopes.add(itemScope);
                }
            }
        }

        return topLevelItemScopes;
    }

    /**
     * Returns all the <b>Microdata items</b> detected within the given <code>document</code>.
     *
     * @param document
     *            document to be processed.
     * @param errorMode
     *            error management policy.
     *
     * @return list of <b>itemscope</b> items.
     *
     * @throws MicrodataParserException
     *             if
     *             <code>errorMode == {@link io.github.sparqlanything.html.org.apache.any23.extractor.microdata.MicrodataParser.ErrorMode#STOP_AT_FIRST_ERROR}</code>
     *             and an error occurs.
     */
    public static MicrodataParserReport getMicrodata(Document document, ErrorMode errorMode)
            throws MicrodataParserException {
        final List<Node> itemNodes = getTopLevelItemScopeNodes(document);
        final List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope> items = new ArrayList<>();
        final MicrodataParser microdataParser = new MicrodataParser(document);
        microdataParser.setErrorMode(errorMode);
        for (Node itemNode : itemNodes) {
            items.add(microdataParser.getItemScope(itemNode));
        }
        return new MicrodataParserReport(items.toArray(new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope[items.size()]), microdataParser.getErrors());
    }

    /**
     * Returns all the <b>Microdata items</b> detected within the given <code>document</code>, works in full report
     * mode.
     *
     * @param document
     *            document to be processed.
     *
     * @return list of <b>itemscope</b> items.
     */
    public static MicrodataParserReport getMicrodata(Document document) {
        try {
            return getMicrodata(document, ErrorMode.FULL_REPORT);
        } catch (MicrodataParserException mpe) {
            throw new IllegalStateException("Unexpected exception.", mpe);
        }
    }

    /**
     * Returns a <i>JSON</i> containing the list of all extracted Microdata, as described at
     * <a href="http://www.w3.org/TR/microdata/#json">Microdata JSON Specification</a>.
     *
     * @param document
     *            document to be processed.
     * @param ps
     *            the {@link PrintStream} to write JSON to
     */
    public static void getMicrodataAsJSON(Document document, PrintStream ps) {
        final MicrodataParserReport report = getMicrodata(document);
        final io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope[] itemScopes = report.getDetectedItemScopes();
        final MicrodataParserException[] errors = report.getErrors();

        ps.append("{ ");

        // Results.
        ps.append("\"result\" : [");
        for (int i = 0; i < itemScopes.length; i++) {
            if (i > 0) {
                ps.print(", ");
            }
            ps.print(itemScopes[i].toJSON());
        }
        ps.append("] ");

        // Errors.
        if (errors != null && errors.length > 0) {
            ps.append(", ");
            ps.append("\"errors\" : [");
            for (int i = 0; i < errors.length; i++) {
                if (i > 0) {
                    ps.print(", ");
                }
                ps.print(errors[i].toJSON());
            }
            ps.append("] ");
        }

        ps.append("}");
    }

    public void setErrorMode(ErrorMode errorMode) {
        if (errorMode == null)
            throw new IllegalArgumentException("errorMode must be not null.");
        this.errorMode = errorMode;
    }

    public ErrorMode getErrorMode() {
        return this.errorMode;
    }

    public MicrodataParserException[] getErrors() {
        return errors == null ? new MicrodataParserException[0]
                : errors.toArray(new MicrodataParserException[errors.size()]);
    }

    /**
     * Reads the value of a <b>itemprop</b> node.
     *
     * @param node
     *            itemprop node.
     *
     * @return value detected within the given <code>node</code>.
     *
     * @throws MicrodataParserException
     *             if an error occurs while extracting a nested item scope.
     */
    public io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue getPropertyValue(Node node) throws MicrodataParserException {
        final io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue itemPropValue = itemPropValues.get(node);
        if (itemPropValue != null)
            return itemPropValue;

        if (isItemScope(node)) {
            return new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue(getItemScope(node), io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue.Type.Nested);
        }

        final String nodeName = node.getNodeName().toLowerCase(Locale.ROOT);

        // see http://w3c.github.io/microdata-rdf/#dfn-property-values
        if ("data".equals(nodeName) || "meter".equals(nodeName)) {
            String value = value(node, "value");
            Literal l;
            if (XMLDatatypeUtil.isValidInteger(value)) {
                l = RDFUtils.literal(value, XSD.INTEGER);
            } else if (XMLDatatypeUtil.isValidDouble(value)) {
                l = RDFUtils.literal(value, XSD.DOUBLE);
            } else {
                l = RDFUtils.literal(value);
            }
            return new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue(l);
        }
        if ("time".equals(nodeName)) {
            String dateTimeStr = value(node, "datetime");
            Literal l;
            if (XMLDatatypeUtil.isValidDate(dateTimeStr)) {
                l = RDFUtils.literal(dateTimeStr, XSD.DATE);
            } else if (XMLDatatypeUtil.isValidTime(dateTimeStr)) {
                l = RDFUtils.literal(dateTimeStr, XSD.TIME);
            } else if (XMLDatatypeUtil.isValidDateTime(dateTimeStr)) {
                l = RDFUtils.literal(dateTimeStr, XSD.DATETIME);
            } else if (XMLDatatypeUtil.isValidGYearMonth(dateTimeStr)) {
                l = RDFUtils.literal(dateTimeStr, XSD.GYEARMONTH);
            } else if (XMLDatatypeUtil.isValidGYear(dateTimeStr)) {
                l = RDFUtils.literal(dateTimeStr, XSD.GYEAR);
            } else if (XMLDatatypeUtil.isValidDuration(dateTimeStr)) {
                l = RDFUtils.literal(dateTimeStr, XSD.DURATION);
            } else {
                l = RDFUtils.literal(dateTimeStr, getLanguage(node));
            }
            return new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue(l);
        }

        if (SRC_TAGS.contains(nodeName)) {
            return link(node, "src");
        }
        if (HREF_TAGS.contains(nodeName)) {
            return link(node, "href");
        }

        if ("object".equals(nodeName)) {
            return link(node, "data");
        }

        String val = DomUtils.readAttribute(node, "content", null);
        if (val != null) {
            return new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue(RDFUtils.literal(val, getLanguage(node)));
        }

        Literal l = RDFUtils.literal(textContent(node), getLanguage(node));
        final io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue newItemPropValue = new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue(l);
        itemPropValues.put(node, newItemPropValue);
        return newItemPropValue;
    }

    private static String textContent(Node node) {
        StringBuilder content = new StringBuilder();
        appendFormatted(node, content, false);
        return content.toString();
    }

    private static boolean shouldSeparateWithNewline(CharSequence s0, CharSequence s1) {
        for (int i = 0, len = s1.length(); i < len; i++) {
            char ch = s1.charAt(i);
            if (ch == '\n' || ch == '\r') {
                return false;
            }
            if (!Character.isWhitespace(ch)) {
                break;
            }
        }
        for (int i = s0.length() - 1; i >= 0; i--) {
            char ch = s0.charAt(i);
            if (ch == '\n' || ch == '\r') {
                return false;
            }
            if (!Character.isWhitespace(ch)) {
                return true;
            }
        }
        return false;
    }

    private static boolean appendFormatted(Node node, StringBuilder sb, boolean needsNewline) {
        switch (node.getNodeType()) {
        case Node.TEXT_NODE:
            String text = node.getTextContent();
            if (text.isEmpty()) {
                return needsNewline;
            }
            if (needsNewline && shouldSeparateWithNewline(sb, text)) {
                sb.append('\n');
            }
            sb.append(text);
            return false;
        case Node.ELEMENT_NODE:
            final String nodeName = node.getNodeName().toLowerCase(Locale.ENGLISH);
            final boolean thisNeedsNewline = "br".equals(nodeName) || Tag.valueOf(nodeName).isBlock();
            final NodeList children = node.getChildNodes();
            boolean prevChildNeedsNewline = needsNewline || thisNeedsNewline;
            for (int i = 0, len = children.getLength(); i < len; i++) {
                prevChildNeedsNewline = appendFormatted(children.item(i), sb, prevChildNeedsNewline);
            }
            return prevChildNeedsNewline || thisNeedsNewline;
        default:
            return needsNewline;
        }
    }

    private static String content(Node node, String attrName) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node attr = attributes.getNamedItem("content");
            if (attr != null) {
                return attr.getNodeValue();
            }
            attr = attributes.getNamedItem(attrName);
            if (attr != null) {
                return attr.getNodeValue();
            }
        }
        return null;
    }

    private static String value(Node node, String attrName) {
        String content = content(node, attrName);
        return StringUtils.stripToEmpty(content != null ? content : node.getTextContent());
    }

    private static io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue link(Node node, String attrName) {
        String content = content(node, attrName);
        return content == null ? new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue(RDFUtils.literal(""))
                : new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue(content, io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue.Type.Link);
    }

    // see https://www.w3.org/TR/html52/dom.html#the-lang-and-xmllang-attributes
    private static String getLanguage(Node node) {
        String lang;
        do {
            lang = DomUtils.readAttribute(node, "xml:lang", null);
            if (StringUtils.isNotBlank(lang)) {
                return lang.trim();
            }
            lang = DomUtils.readAttribute(node, "lang", null);
            if (StringUtils.isNotBlank(lang)) {
                return lang.trim();
            }
            node = node.getParentNode();
        } while (node != null);
        return null;
    }

    /**
     * Returns all the <b>itemprop</b>s for the given <b>itemscope</b> node.
     *
     * @param scopeNode
     *            node representing the <b>itemscope</b>
     * @param skipRoot
     *            if <code>true</code> the given root <code>node</code> will be not read as a property, even if it
     *            contains the <b>itemprop</b> attribute.
     *
     * @return the list of <b>itemprop</b>s detected within the given <b>itemscope</b>.
     *
     * @throws MicrodataParserException
     *             if an error occurs while retrieving an property value.
     */
    public List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp> getItemProps(final Node scopeNode, boolean skipRoot) throws MicrodataParserException {
        final Set<Node> accepted = new LinkedHashSet<>();

        boolean skipRootChildren = false;
        if (!skipRoot) {
            NamedNodeMap attributes = scopeNode.getAttributes();
            if (attributes.getNamedItem(ITEMPROP_ATTRIBUTE) != null
                    || attributes.getNamedItem(REVERSE_ITEMPROP_ATTRIBUTE) != null) {
                accepted.add(scopeNode);
            }
            if (attributes.getNamedItem(ITEMSCOPE_ATTRIBUTE) != null) {
                skipRootChildren = true;
            }
        }

        if (!skipRootChildren) {
            // TreeWalker to walk DOM tree starting with the scopeNode. Nodes maybe visited multiple times.
            TreeWalker treeWalker = ((DocumentTraversal) scopeNode.getOwnerDocument()).createTreeWalker(scopeNode,
                    NodeFilter.SHOW_ELEMENT, new NodeFilter() {
                        @Override
                        public short acceptNode(Node node) {
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                NamedNodeMap attributes = node.getAttributes();
                                if ((attributes.getNamedItem(ITEMPROP_ATTRIBUTE) != null
                                        || attributes.getNamedItem(REVERSE_ITEMPROP_ATTRIBUTE) != null)
                                        && scopeNode != node) {
                                    accepted.add(node);
                                }

                                if (attributes.getNamedItem(ITEMSCOPE_ATTRIBUTE) != null) {
                                    // Don't visit descendants of nodes that define a new scope
                                    return FILTER_REJECT;
                                }
                            }
                            return FILTER_ACCEPT;
                        }
                    }, false);

            // To populate accepted we only need to walk the tree.
            while (treeWalker.nextNode() != null)
                ;
        }

        final List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp> result = new ArrayList<>();
        for (Node itemPropNode : accepted) {
            final String itemProp = DomUtils.readAttribute(itemPropNode, ITEMPROP_ATTRIBUTE, null);
            final String reverseProp = DomUtils.readAttribute(itemPropNode, REVERSE_ITEMPROP_ATTRIBUTE, null);

            boolean hasItemProp = StringUtils.isNotBlank(itemProp);
            boolean hasReverseProp = StringUtils.isNotBlank(reverseProp);

            if (!hasItemProp && !hasReverseProp) {
                manageError(new MicrodataParserException("invalid property name '" + itemProp + "'", itemPropNode));
                continue;
            }

            ItemPropValue itemPropValue;
            try {
                itemPropValue = getPropertyValue(itemPropNode);
            } catch (MicrodataParserException mpe) {
                manageError(mpe);
                continue;
            }
            if (hasItemProp) {
                for (String propertyName : itemProp.trim().split("\\s+")) {
                    result.add(
                            new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp(DomUtils.getXPathForNode(itemPropNode), propertyName, itemPropValue, false));
                }
            }
            if (hasReverseProp) {
                if (itemPropValue.literal != null) {
                    manageError(new MicrodataParserException(REVERSE_ITEMPROP_ATTRIBUTE + " cannot point to a literal",
                            itemPropNode));
                    continue;
                }
                for (String propertyName : reverseProp.trim().split("\\s+")) {
                    result.add(new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp(DomUtils.getXPathForNode(itemPropNode), propertyName, itemPropValue, true));
                }
            }
        }
        return result;
    }

    /**
     * Given a document and a list of <b>itemprop</b> names this method will return such <b>itemprops</b>.
     *
     * @param refs
     *            list of references.
     *
     * @return list of retrieved <b>itemprop</b>s.
     *
     * @throws MicrodataParserException
     *             if a loop is detected or a property name is missing.
     */
    public io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp[] deferProperties(String... refs) throws MicrodataParserException {
        Document document = this.document;
        dereferenceRecursionCounter++;
        final List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp> result = new ArrayList<>();
        try {
            for (String ref : refs) {
                if (loopDetectorSet.contains(ref)) {
                    throw new MicrodataParserException(String.format(Locale.ROOT,
                            "Loop detected with depth %d while dereferencing itemProp '%s' .",
                            dereferenceRecursionCounter - 1, ref), null);
                }
                loopDetectorSet.add(ref);
                Element element = document.getElementById(ref);
                if (element == null) {
                    manageError(new MicrodataParserException(
                            String.format(Locale.ROOT, "Unknown itemProp id '%s'", ref), null));
                    continue;
                }
                result.addAll(getItemProps(element, false));
            }
        } catch (MicrodataParserException mpe) {
            if (dereferenceRecursionCounter == 1)
                manageError(mpe);
            else
                throw mpe; // Recursion end, this the the top call.
        } finally {
            dereferenceRecursionCounter--;
            if (dereferenceRecursionCounter == 0) { // Recursion end, this the the top call.
                loopDetectorSet.clear();
            }
        }
        return result.toArray(new io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp[result.size()]);
    }

    private static final String[] EMPTY_STRINGS = new String[0];

    private static String[] itemrefIds(Node node) {
        String itemref = DomUtils.readAttribute(node, "itemref", null);
        return StringUtils.isBlank(itemref) ? EMPTY_STRINGS : itemref.trim().split("\\s+");
    }

    /**
     * Returns the {@link io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope} instance described within the specified <code>node</code>.
     *
     * @param node
     *            node describing an <i>itemscope</i>.
     *
     * @return instance of ItemScope object.
     *
     * @throws MicrodataParserException
     *             if an error occurs while dereferencing properties.
     */
    public io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope getItemScope(Node node) throws MicrodataParserException {
        final io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope itemScope = itemScopes.get(node);
        if (itemScope != null)
            return itemScope;

        final String id = DomUtils.readAttribute(node, "id", null);
        final String itemType = DomUtils.readAttribute(node, "itemtype", null);
        final String itemId = DomUtils.readAttribute(node, "itemid", null);

        final List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp> itemProps = getItemProps(node, true);
        final String[] itemrefIDs = itemrefIds(node);
        final io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp[] deferredProperties;
        try {
            deferredProperties = deferProperties(itemrefIDs);
        } catch (MicrodataParserException mpe) {
            mpe.setErrorNode(node);
            throw mpe;
        }
        for (io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp deferredProperty : deferredProperties) {
            if (itemProps.contains(deferredProperty)) {
                manageError(new MicrodataParserException(
                        String.format(Locale.ROOT, "Duplicated deferred itemProp '%s'.", deferredProperty.getName()),
                        node));
                continue;
            }
            itemProps.add(deferredProperty);
        }

        List<IRI> types;
        if (itemType == null) {
            types = Collections.emptyList();
        } else {
            types = new ArrayList<>();
            boolean canConcatWithPrev = false;
            for (String s : itemType.trim().split("\\s+")) {
                try {
                    canConcatWithPrev = types.addAll(io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope.stringToSingletonIRI(s));
                } catch (RuntimeException e) {
                    if (canConcatWithPrev) {
                        int lastInd = types.size() - 1;
                        try {
                            List<IRI> secondTry = io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope
                                    .stringToSingletonIRI(types.get(lastInd).stringValue() + " " + s);
                            types.remove(lastInd);
                            canConcatWithPrev = types.addAll(secondTry);
                        } catch (RuntimeException e2) {
                            manageError(new MicrodataParserException(e.getMessage(), node));
                            canConcatWithPrev = false;
                        }
                    } else {
                        manageError(new MicrodataParserException(e.getMessage(), node));
                    }
                }
            }
        }

        final io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope newItemScope = new ItemScope(DomUtils.getXPathForNode(node),
                itemProps.toArray(new ItemProp[itemProps.size()]), id, itemrefIDs, types, itemId);
        itemScopes.put(node, newItemScope);
        return newItemScope;
    }

    private void manageError(MicrodataParserException mpe) throws MicrodataParserException {
        switch (errorMode) {
        case FULL_REPORT:
            errors.add(mpe);
            break;
        case STOP_AT_FIRST_ERROR:
            throw mpe;
        default:
            throw new IllegalStateException("Unsupported mode " + errorMode);
        }
    }

}
