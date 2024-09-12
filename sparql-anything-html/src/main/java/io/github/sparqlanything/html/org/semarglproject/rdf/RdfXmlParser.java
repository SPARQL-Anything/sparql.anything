/**
 * Copyright 2012-2013 the Semargl contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.sparqlanything.html.org.semarglproject.rdf;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler;
import io.github.sparqlanything.html.org.semarglproject.ri.MalformedIriException;
import io.github.sparqlanything.html.org.semarglproject.ri.RIUtils;
import io.github.sparqlanything.html.org.semarglproject.sink.Pipe;
import io.github.sparqlanything.html.org.semarglproject.sink.XmlSink;
import io.github.sparqlanything.html.org.semarglproject.sink.TripleSink;
import io.github.sparqlanything.html.org.semarglproject.source.StreamProcessor;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDF;
import io.github.sparqlanything.html.org.semarglproject.xml.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import java.util.*;

/**
 * Implementation of streaming <a href="http://www.w3.org/TR/2004/REC-rdf-syntax-grammar-20040210/">RDF/XML</a> parser.
 * <br>
 *     List of supported options:
 *     <ul>
 *         <li>{@link StreamProcessor#PROCESSOR_GRAPH_HANDLER_PROPERTY}</li>
 *         <li>{@link StreamProcessor#ENABLE_ERROR_RECOVERY}</li>
 *     </ul>
 */
public final class RdfXmlParser extends Pipe<TripleSink> implements XmlSink {

    /**
     * Class URI for errors produced by a parser
     */
    public static final String ERROR = "http://semarglproject.org/ntriples/Error";

    private static final String IS_NOT_ALLOWED_HERE = " is not allowed here";

    // processing modes
    private static final short INSIDE_OF_PROPERTY = 1;
    private static final short INSIDE_OF_RESOURCE = 2;
    private static final short PARSE_TYPE_LITERAL = 3;
    private static final short PARSE_TYPE_COLLECTION = 4;
    private static final short PARSE_TYPE_RESOURCE = 5;
    private static final short ERROR_RECOVERY = 6;

    private static final String ID_ATTR = "ID";
    private static final String NODE_ID_ATTR = "nodeID";
    private static final String ABOUT_ATTR = "about";

    private static final String PARSE_LITERAL_VALUE = "Literal";
    private static final String PARSE_RESOURCE_VALUE = "Resource";
    private static final String PARSE_COLLECTION_VALUE = "Collection";

    private short mode = 0;

    private String baseUri = "";

    private final Stack<Short> modeStack = new Stack<Short>();
    private final Stack<String> langStack = new Stack<String>();
    private final Stack<String> baseStack = new Stack<String>();
    private final Stack<String> subjStack = new Stack<String>();
    private final Stack<Integer> subjLiIndexStack = new Stack<Integer>();
    private final Map<String, String> nsMappings = new HashMap<String, String>();

    private final Set<String> processedIDs = new HashSet<String>();

    private int bnodeId = 0;

    // IRI or bnode
    private String subjRes = null;

    // tail node of parseType="Collection"
    private String seqTailRes = null;

    // predicate IRI
    private String predIri = null;

    // typed literal datatype IRI
    private String datatypeIri = null;

    private String reifyIri = null;
    private boolean captureLiteral = false;

    private int parseDepth = 0;
    private StringBuilder parse = new StringBuilder();

    private io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler processorGraphHandler = null;
    private boolean ignoreErrors = false;

    // holds data for triples which addition depends on XML node contents (blank or not)
    private List<String> pendingTriples = new ArrayList<String>();

    private RdfXmlParser(TripleSink sink) {
        super(sink);
    }

    /**
     * Creates instance of RdfXmlParser connected to specified sink.
     * @param sink sink to be connected to
     * @return instance of RdfXmlParser
     */
    public static XmlSink connect(TripleSink sink) {
        return new RdfXmlParser(sink);
    }

    private void error(String msg) throws SAXException {
        if (processorGraphHandler != null) {
            processorGraphHandler.error(ERROR, msg);
        }
        if (ignoreErrors) {
            modeStack.push(mode);
            mode = ERROR_RECOVERY;
        } else {
            throw new SAXException(new io.github.sparqlanything.html.org.semarglproject.rdf.ParseException(msg));
        }
    }

    @SuppressWarnings("deprecation")
    private boolean violatesSchema(String nodeIri) {
        return nodeIri == null || nodeIri.isEmpty() || nodeIri.equals(RDF.PARSE_TYPE)
                || nodeIri.equals(RDF.ABOUT_EACH) || nodeIri.equals(RDF.DATATYPE)
                || nodeIri.equals(RDF.BAG_ID) || nodeIri.equals(RDF.ABOUT)
                || nodeIri.equals(RDF.RESOURCE) || nodeIri.equals(RDF.NODEID)
                || nodeIri.equals(RDF.ID) || nodeIri.equals(RDF.ABOUT_EACH_PREFIX);
    }

    @Override
    public void startElement(String nsUri, String lname, String qname, Attributes attrs) throws SAXException {
        processPendingTriples(true);

        modeStack.push(mode);

        if (parseDepth > 0) {
            parseDepth++;
            if (mode == PARSE_TYPE_LITERAL) {
                parse.append(XmlUtils.serializeOpenTag(nsUri, qname, nsMappings, attrs, true));
                nsMappings.clear();
                return;
            }
        }

        if (mode == ERROR_RECOVERY) {
            return;
        }

        processLangAndBase(attrs);

        String iri = nsUri + lname;
        if (subjRes == null && (nsUri == null || nsUri.isEmpty()) || iri.equals(RDF.RDF)) {
            return;
        }
        if (violatesSchema(iri)) {
            error(qname + IS_NOT_ALLOWED_HERE);
        }

        switch (mode) {
            case PARSE_TYPE_COLLECTION:
            case INSIDE_OF_PROPERTY: {
                subjRes = getSubject(attrs);
                if (subjRes == null) {
                    // error during subject processing was ignored so we need to skip next steps
                    return;
                }

                if (mode != PARSE_TYPE_COLLECTION && !subjStack.isEmpty()) {
                    processNonLiteralTriple(subjStack.peek(), predIri, subjRes);
                }

                if (!iri.equals(RDF.DESCRIPTION)) {
                    if (iri.equals(RDF.LI)) {
                        error(qname + IS_NOT_ALLOWED_HERE);
                    } else {
                        sink.addNonLiteral(subjRes, RDF.TYPE, iri);
                    }
                }

                processResourceAttrs(qname, attrs);

                subjStack.push(subjRes);
                subjLiIndexStack.push(1);
                if (mode == INSIDE_OF_PROPERTY) {
                    mode = INSIDE_OF_RESOURCE;
                }
                break;
            }
            case PARSE_TYPE_RESOURCE:
            case INSIDE_OF_RESOURCE: {
                int liIndex = subjLiIndexStack.pop();

                boolean correctProperty = checkPropertyForErrors(qname, iri, attrs);

                if (!correctProperty) {
                    // error during property processing was ignored so we need to skip next steps
                    return;
                }

                predIri = iri;
                if (predIri.equals(RDF.LI)) {
                    predIri = RDF.NS + "_" + liIndex++;
                }
                subjLiIndexStack.push(liIndex);

                String nodeId = attrs.getValue(RDF.NS, ID_ATTR);
                if (nodeId != null) {
                    reifyIri = resolveIRINoResolve(baseStack.peek(), nodeId);
                }

                captureLiteral = true;
                mode = INSIDE_OF_PROPERTY;
                processPropertyAttrs(nsUri, attrs);
                if (captureLiteral) {
                    parse = new StringBuilder();
                }
                break;
            }
            default:
                throw new IllegalStateException("Unknown mode = " + mode);
        }
    }

    private void processPendingTriples(boolean forceNewBNode) {
        Iterator<String> iterator = pendingTriples.iterator();
        while (iterator.hasNext()) {
            String propRes = iterator.next();
            String attr = iterator.next();
            String value = iterator.next();
            if (forceNewBNode || propRes == null) {
                String bnode = newBnode();
                processNonLiteralTriple(subjRes, predIri, bnode);
                sink.addPlainLiteral(bnode, attr, value, langStack.peek());
            } else {
                sink.addPlainLiteral(propRes, attr, value, langStack.peek());
            }
        }
        pendingTriples.clear();
    }

    private boolean checkPropertyForErrors(String qname, String iri, Attributes attrs) throws SAXException {
        if (iri.equals(RDF.NIL) || iri.equals(RDF.DESCRIPTION)) {
            error(qname + IS_NOT_ALLOWED_HERE);
            return false;
        }
        if (!RIUtils.isIri(iri)) {
            error("Invalid property IRI");
            return false;
        }

        if (attrs.getValue(RDF.NS, "resource") != null && attrs.getValue(RDF.NS, NODE_ID_ATTR) != null) {
            error("Both rdf:resource and rdf:nodeID are present");
            return false;
        }
        if (attrs.getValue(RDF.NS, "parseType") != null && !isAttrsValidForParseType(attrs)) {
            error("rdf:parseType conflicts with other attributes");
            return false;
        }
        return true;
    }

    private void processResourceAttrs(String qname, Attributes attrs) throws SAXException {
        for (int i = 0; i < attrs.getLength(); i++) {
            String tag = attrs.getURI(i) + attrs.getLocalName(i);
            if (tag.equals(RDF.NODEID) || tag.equals(RDF.ABOUT) || tag.equals(RDF.ID)
                    || attrs.getQName(i).startsWith(XMLConstants.XML_NS_PREFIX)) {
                continue;
            }
            String value = attrs.getValue(i);
            if (tag.equals(RDF.TYPE)) {
                sink.addNonLiteral(subjRes, RDF.TYPE, value);
            } else {
                if (violatesSchema(tag) || tag.equals(RDF.LI)) {
                    error(qname + IS_NOT_ALLOWED_HERE);
                } else {
                    sink.addPlainLiteral(subjRes, tag, value, langStack.peek());
                }
            }
        }
    }

    private void processPropertyAttrs(String nsUri, Attributes attrs) throws SAXException {
        // process resource first
        int resIdx = attrs.getIndex(RDF.NS, "resource");
        String propertyRes = null;
        if (resIdx >= 0) {
            propertyRes = processPropertyRes(attrs.getValue(resIdx));
        }

        for (int i = 0; i < attrs.getLength(); i++) {
            if (i == resIdx) {
                continue;
            }
            String attr = attrs.getURI(i) + attrs.getLocalName(i);
            if (attrs.getQName(i).startsWith(XMLConstants.XML_NS_PREFIX) || attr.equals(RDF.ID)) {
                continue;
            }
            processPropertyTagAttr(nsUri, attr, attrs.getValue(i), propertyRes);
        }
    }

    private void processLangAndBase(Attributes attrs) throws SAXException {
        String lang = langStack.peek();
        if (attrs.getValue(XmlUtils.XML_LANG) != null) {
            lang = attrs.getValue(XmlUtils.XML_LANG);
        }
        langStack.push(lang);

        String base = baseStack.peek();
        if (attrs.getValue(XmlUtils.XML_BASE) != null) {
            base = attrs.getValue(XmlUtils.XML_BASE);
            if (base.contains("#")) {
                base = base.substring(0, base.lastIndexOf('#'));
            }
            base += '#';
            if (!RIUtils.isAbsoluteIri(base)) {
                error("Invalid base IRI");
                base = baseStack.peek();
            }
        }
        baseStack.push(base);
    }

    private String processPropertyRes(String value) throws SAXException {
        String propertyRes = resolveIRI(baseStack.peek(), value);
        if (propertyRes != null) {
            processNonLiteralTriple(subjRes, predIri, propertyRes);
            captureLiteral = false;
        }
        return propertyRes;
    }

    private void processPropertyTagAttr(String nsUri, String attr, String value,
                                        String propertyRes) throws SAXException {
        if (attr.equals(RDF.DATATYPE)) {
            datatypeIri = resolveIRINoResolve(nsUri, value);
        } else if (attr.equals(RDF.PARSE_TYPE)) {
            parseDepth = 1;
            if (value.equalsIgnoreCase(PARSE_LITERAL_VALUE)) {
                parse = new StringBuilder();
                mode = PARSE_TYPE_LITERAL;
            } else if (value.equalsIgnoreCase(PARSE_RESOURCE_VALUE)) {
                String bnode = newBnode();
                processNonLiteralTriple(subjRes, predIri, bnode);
                subjRes = bnode;
                subjStack.push(subjRes);
                subjLiIndexStack.push(1);
                mode = PARSE_TYPE_RESOURCE;
            } else if (value.equalsIgnoreCase(PARSE_COLLECTION_VALUE)) {
                String bnode = newBnode();
                sink.addNonLiteral(subjRes, predIri, bnode);
                subjRes = bnode;
                seqTailRes = null;
                subjStack.push(bnode);
                subjLiIndexStack.push(1);
                mode = PARSE_TYPE_COLLECTION;
            }
            captureLiteral = false;
        } else if (attr.equals(RDF.NODEID)) {
            if (!XmlUtils.isValidNCName(value)) {
                error("Invalid nodeID");
            } else {
                String id = RDF.BNODE_PREFIX + 'n' + value.hashCode();
                processNonLiteralTriple(subjRes, predIri, id);
                captureLiteral = false;
            }
        } else {
            if (violatesSchema(attr) || attr.equals(RDF.NIL)) {
                error(attr + IS_NOT_ALLOWED_HERE);
            } else {
                pendingTriples.add(propertyRes);
                pendingTriples.add(attr);
                pendingTriples.add(value);
                captureLiteral = false;
            }
        }
    }

    @Override
    public void endElement(String namespaceUri, String lname, String qname) throws SAXException {
        processPendingTriples(false);
        if (parseDepth > 0) {
            parseDepth--;
            if (mode == PARSE_TYPE_LITERAL && parseDepth > 0) {
                parse.append("</").append(qname).append(">");
                return;
            }
        }
        if (subjStack.isEmpty()) {
            return;
        }

        switch (mode) {
            case PARSE_TYPE_RESOURCE:
            case INSIDE_OF_RESOURCE: {
                subjStack.pop();
                if (!subjStack.isEmpty()) {
                    subjRes = subjStack.peek();
                }
                subjLiIndexStack.pop();
                if (mode == INSIDE_OF_RESOURCE) {
                    mode = INSIDE_OF_PROPERTY;
                } else {
                    mode = INSIDE_OF_RESOURCE;
                }
                break;
            }
            case PARSE_TYPE_COLLECTION: {
                subjStack.pop();
                subjLiIndexStack.pop();
                if (parseDepth > 0) {
                    if (seqTailRes == null) {
                        seqTailRes = subjStack.peek();
                        sink.addNonLiteral(seqTailRes, RDF.FIRST, subjRes);
                    } else {
                        String bnode = newBnode();
                        sink.addNonLiteral(seqTailRes, RDF.REST, bnode);
                        sink.addNonLiteral(bnode, RDF.FIRST, subjRes);
                        seqTailRes = bnode;
                    }
                } else {
                    sink.addNonLiteral(seqTailRes, RDF.REST, RDF.NIL);
                    if (!subjStack.isEmpty()) {
                        subjRes = subjStack.peek();
                    }
                    mode = INSIDE_OF_RESOURCE;
                }
                break;
            }
            case INSIDE_OF_PROPERTY: {
                if (captureLiteral) {
                    String value = parse.toString();
                    if (datatypeIri != null) {
                        processLiteralTriple(subjRes, predIri, value, datatypeIri, true);
                    } else {
                        processLiteralTriple(subjRes, predIri, value, langStack.peek(), false);
                    }
                    captureLiteral = false;
                }
                mode = INSIDE_OF_RESOURCE;
                break;
            }
            case PARSE_TYPE_LITERAL: {
                processLiteralTriple(subjRes, predIri, parse.toString(), RDF.XML_LITERAL, true);
                mode = INSIDE_OF_RESOURCE;
                break;
            }
            case ERROR_RECOVERY: {
                mode = modeStack.pop();
                return;
            }
            default:
                throw new IllegalStateException("Unknown mode = " + mode);
        }
        langStack.pop();
        baseStack.pop();
        // TODO: fix modeStack
        short savedMode = modeStack.pop();
        if (savedMode == PARSE_TYPE_RESOURCE) {
            mode = savedMode;
        }
    }

    private boolean isAttrsValidForParseType(Attributes attrs) {
        for (int i = 0; i < attrs.getLength(); i++) {
            if (attrs.getQName(i).startsWith("xml")) {
                continue;
            }
            String uri = attrs.getURI(i) + attrs.getLocalName(i);
            if (uri.equals(RDF.PARSE_TYPE) || uri.equals(RDF.ID)) {
                continue;
            }
            return false;
        }
        return true;
    }

    private void processNonLiteralTriple(String subj, String pred, String obj) {
        sink.addNonLiteral(subj, pred, obj);
        if (reifyIri != null) {
            sink.addNonLiteral(reifyIri, RDF.TYPE, RDF.STATEMENT);
            sink.addNonLiteral(reifyIri, RDF.SUBJECT, subj);
            sink.addNonLiteral(reifyIri, RDF.PREDICATE, pred);
            sink.addNonLiteral(reifyIri, RDF.OBJECT, obj);
            reifyIri = null;
        }
    }

    private void processLiteralTriple(String subj, String pred, String value, String langOrDt, boolean typed) {
        if (typed) {
            sink.addTypedLiteral(subj, pred, value, langOrDt);
        } else {
            sink.addPlainLiteral(subj, pred, value, langOrDt);
        }
        if (reifyIri != null) {
            sink.addNonLiteral(reifyIri, RDF.TYPE, RDF.STATEMENT);
            sink.addNonLiteral(reifyIri, RDF.SUBJECT, subj);
            sink.addNonLiteral(reifyIri, RDF.PREDICATE, pred);
            if (typed) {
                sink.addTypedLiteral(reifyIri, RDF.OBJECT, value, langOrDt);
            } else {
                sink.addPlainLiteral(reifyIri, RDF.OBJECT, value, langOrDt);
            }
            reifyIri = null;
        }
    }

    private String getSubject(Attributes attrs) throws SAXException {
        int count = 0;
        String result = null;
        String attrValue = attrs.getValue(RDF.NS, ABOUT_ATTR);
        if (attrValue != null) {
            result = resolveIRI(baseStack.peek(), attrValue);
            if (result != null) {
                count++;
            }
        }
        attrValue = attrs.getValue(RDF.NS, ID_ATTR);
        if (attrValue != null) {
            result = resolveIRINoResolve(baseStack.peek(), attrValue);
            if (result != null) {
                if (processedIDs.contains(result)) {
                    error("Duplicate definition for resource ID = " + result);
                    return null;
                }
                processedIDs.add(result);
                count++;
            }
        }
        attrValue = attrs.getValue(RDF.NS, NODE_ID_ATTR);
        if (attrValue != null) {
            result = RDF.BNODE_PREFIX + 'n' + attrValue.hashCode();
            count++;
        }
        if (count == 0) {
            return newBnode();
        }
        if (count > 1) {
            error("Ambiguous identifier definition");
            return null;
        }
        return result;
    }

    private String newBnode() {
        bnodeId++;
        return RDF.BNODE_PREFIX + 'n' + bnodeId;
    }

    /**
     * Resolves specified IRI ignoring special cases
     * @param baseIri base to resolve against
     * @param iri IRI to resolve
     * @return resolved IRI or null on error
     * @throws SAXException
     */
    private String resolveIRINoResolve(String baseIri, String iri) throws SAXException {
        if (RIUtils.isAbsoluteIri(iri)) {
            return iri;
        }
        if (!XmlUtils.isValidNCName(iri)) {
            error("Vocab term must be a valid NCName");
            return null;
        }
        String result = baseIri + iri;
        if (RIUtils.isAbsoluteIri(result)) {
            return result;
        }
        error("Malformed IRI: " + iri);
        return null;
    }

    /**
     * Resolves specified IRI
     * @param baseIri base to resolve against
     * @param iri IRI to resolve
     * @return resolved IRI or null on error
     * @throws SAXException
     */
    private String resolveIRI(String baseIri, String iri) throws SAXException {
        try {
            return RIUtils.resolveIri(baseIri, iri);
        } catch (MalformedIriException e) {
            error(e.getMessage());
            return null;
        }
    }

    @Override
    public void startDocument() throws SAXException {
        mode = INSIDE_OF_PROPERTY;
        sink.setBaseUri(baseUri);
        baseStack.push(baseUri);
        langStack.push(null);
        captureLiteral = false;
        subjRes = null;
        seqTailRes = null;
        predIri = null;
        datatypeIri = null;
        reifyIri = null;
        parseDepth = 0;
    }

    @Override
    public void endDocument() throws SAXException {
        langStack.clear();
        baseStack.clear();
        subjStack.clear();
        modeStack.clear();
        subjLiIndexStack.clear();
        nsMappings.clear();
        processedIDs.clear();
        parse = new StringBuilder();
        pendingTriples.clear();
    }

    @Override
    public void characters(char[] buffer, int offset, int length) throws SAXException {
        processPendingTriples(true);
        if (mode == PARSE_TYPE_LITERAL || captureLiteral) {
            parse.append(String.copyValueOf(buffer, offset, length));
        }
    }

    @Override
    public void ignorableWhitespace(char[] buffer, int offset, int length) throws SAXException {
        characters(buffer, offset, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        processPendingTriples(true);
        if (parseDepth > 0 && mode == PARSE_TYPE_LITERAL) {
            parse.append("<?").append(target).append(" ").append(data).append("?>");
        }
    }

    @Override
    public void comment(char[] buffer, int offset, int length) throws SAXException {
        processPendingTriples(true);
        if (parseDepth > 0 && mode == PARSE_TYPE_LITERAL) {
            parse.append("<!--");
            parse.append(String.copyValueOf(buffer, offset, length));
            parse.append("-->");
        }
    }

    @Override
    public void startPrefixMapping(String abbr, String uri) throws SAXException {
        if (mode == PARSE_TYPE_LITERAL) {
            nsMappings.put(abbr, uri);
        }
    }

    @Override
    public void setBaseUri(String baseUri) {
        if (baseUri != null && !baseUri.isEmpty() && Character.isLetter(baseUri.charAt(baseUri.length() - 1))) {
            this.baseUri = baseUri + "#";
        } else {
            this.baseUri = baseUri == null ? "" : baseUri;
        }
    }

    @Override
    public void setDocumentLocator(Locator arg0) {
    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String arg0) throws SAXException {
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void endEntity(String arg0) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
    }

    @Override
    public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
    }

    @Override
    public void startEntity(String arg0) throws SAXException {
    }

    @Override
    public io.github.sparqlanything.html.org.semarglproject.rdf.ParseException processException(SAXException e) {
        Throwable cause = e.getCause();
        if (cause instanceof io.github.sparqlanything.html.org.semarglproject.rdf.ParseException) {
            return (ParseException) cause;
        }
        return new ParseException(e);
    }

    @Override
    protected boolean setPropertyInternal(String key, Object value) {
        if (StreamProcessor.PROCESSOR_GRAPH_HANDLER_PROPERTY.equals(key) && value instanceof io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler) {
            processorGraphHandler = (ProcessorGraphHandler) value;
        } else if (StreamProcessor.ENABLE_ERROR_RECOVERY.equals(key) && value instanceof Boolean) {
            ignoreErrors = (Boolean) value;
        }
        return false;
    }
}
