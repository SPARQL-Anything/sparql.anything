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

package io.github.sparqlanything.html.org.apache.any23.extractor.rdfa;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import javax.xml.transform.TransformerException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.DomUtils;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.RDFa11ParserException;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This parser is able to extract <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa 1.0</a> and
 * <a href="http://www.w3.org/TR/rdfa-core/">RDFa 1.1</a> statements from any <i>(X)HTML</i> document.
 *
 * @deprecated since 2.3 the {@link org.eclipse.rdf4j.rio.Rio} implementations are used to parse RDFa. Look at
 *             {@link io.github.sparqlanything.html.org.apache.any23.extractor.rdf.RDFParserFactory#getRDFa10Parser} and
 *             {@link io.github.sparqlanything.html.org.apache.any23.extractor.rdf.RDFParserFactory#getRDFa11Parser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Deprecated
public class RDFa11Parser {

    private static final Logger logger = LoggerFactory.getLogger(RDFa11Parser.class);

    public static final String CURIE_SEPARATOR = ":";
    public static final char IRI_PREFIX_SEPARATOR = ':';
    public static final String IRI_SCHEMA_SEPARATOR = "://";
    public static final String IRI_PATH_SEPARATOR = "/";

    public static final String HEAD_TAG = "HEAD";
    public static final String BODY_TAG = "BODY";

    public static final String XMLNS_ATTRIBUTE = "xmlns";
    public static final String XML_LANG_ATTRIBUTE = "xml:lang";

    public static final String REL_ATTRIBUTE = "rel";
    public static final String REV_ATTRIBUTE = "rev";

    public static final String ABOUT_ATTRIBUTE = "about";
    public static final String RESOURCE_ATTRIBUTE = "resource";
    public static final String SRC_ATTRIBUTE = "src";
    public static final String HREF_ATTRIBUTE = "href";

    public static final String TYPE_ATTRIBUTE = "type";
    public static final String ATTRIBUTE_CSS = "text/css";

    public static final String[] SUBJECT_ATTRIBUTES = { ABOUT_ATTRIBUTE, SRC_ATTRIBUTE, RESOURCE_ATTRIBUTE,
            HREF_ATTRIBUTE };

    public static final String PREFIX_ATTRIBUTE = "prefix";
    public static final String TYPEOF_ATTRIBUTE = "typeof";
    public static final String PROPERTY_ATTRIBUTE = "property";
    public static final String DATATYPE_ATTRIBUTE = "datatype";
    public static final String CONTENT_ATTRIBUTE = "content";
    public static final String VOCAB_ATTRIBUTE = "vocab";
    // TODO: introduce support for RDFa profiles. (http://www.w3.org/TR/rdfa-core/#s_profiles)
    public static final String PROFILE_ATTRIBUTE = "profile";

    public static final String XML_LITERAL_DATATYPE = "rdf:XMLLiteral";

    public static final String XMLNS_DEFAULT = "http://www.w3.org/1999/xhtml";

    private IssueReport issueReport;

    private URL documentBase;

    private final Stack<IRIMapping> IRIMappingStack = new Stack<>();

    private final Stack<Vocabulary> vocabularyStack = new Stack<>();

    private final List<IncompleteTriple> listOfIncompleteTriples = new ArrayList<>();

    private final Stack<EvaluationContext> evaluationContextStack = new Stack<>();

    public RDFa11Parser() {
        // default constructor
    }

    protected static URL getDocumentBase(URL documentURL, Document document) throws MalformedURLException {
        String base;
        base = DomUtils.find(document, "/HTML/HEAD/BASE/@href"); // Non XHTML documents.
        if (!"".equals(base))
            return new URL(base);
        base = DomUtils.find(document, "//*/h:head/h:base[position()=1]/@href"); // XHTML documents.
        if (!"".equals(base))
            return new URL(base);
        return documentURL;
    }

    /**
     * Given a prefix declaration returns a list of <code>prefixID:prefixURL</code> strings normalizing blanks where
     * present.
     *
     * @param prefixesDeclaration
     *            input prefix
     *
     * @return list of extracted prefixes.
     */
    protected static String[] extractPrefixSections(String prefixesDeclaration) {
        final String[] parts = prefixesDeclaration.split("\\s");
        final List<String> out = new ArrayList<>();
        int i = 0;
        while (i < parts.length) {
            final String part = parts[i];
            if (part.length() == 0) {
                i++;
                continue;
            }
            if (part.charAt(part.length() - 1) == IRI_PREFIX_SEPARATOR) {
                i++;
                while (i < parts.length && parts[i].length() == 0)
                    i++;
                out.add(part + (i < parts.length ? parts[i] : ""));
                i++;
            } else {
                out.add(parts[i]);
                i++;
            }
        }
        return out.toArray(new String[out.size()]);
    }

    protected static boolean isAbsoluteIRI(String iri) {
        return iri.contains(IRI_SCHEMA_SEPARATOR);
    }

    protected static boolean isCURIE(String curie) {
        if (curie == null) {
            throw new NullPointerException("curie string cannot be null.");
        }
        if (curie.trim().length() == 0)
            return false;

        // '[' PREFIX ':' VALUE ']'
        if (curie.charAt(0) != '[' || curie.charAt(curie.length() - 1) != ']')
            return false;
        int separatorIndex = curie.indexOf(CURIE_SEPARATOR);
        return separatorIndex > 0 && curie.indexOf(CURIE_SEPARATOR, separatorIndex + 1) == -1;
    }

    protected static boolean isCURIEBNode(String curie) {
        return isCURIE(curie) && curie.substring(1, curie.length() - 1).split(CURIE_SEPARATOR)[0].equals("_");
    }

    protected static boolean isRelativeNode(Node node) {
        if (ATTRIBUTE_CSS.equals(DomUtils.readAttribute(node, TYPE_ATTRIBUTE)))
            return false;
        return DomUtils.hasAttribute(node, REL_ATTRIBUTE) || DomUtils.hasAttribute(node, REV_ATTRIBUTE);
    }

    // RDFa1.0[5.5.9.2]
    protected static Literal getAsPlainLiteral(Node node, String currentLanguage) {
        final String content = DomUtils.readAttribute(node, CONTENT_ATTRIBUTE, null);
        if (content != null)
            return RDFUtils.literal(content, currentLanguage);

        if (!node.hasChildNodes())
            return RDFUtils.literal("", currentLanguage);

        final String nodeTextContent = node.getTextContent();
        return nodeTextContent == null ? null : RDFUtils.literal(nodeTextContent.trim(), currentLanguage);
    }

    protected static Literal getAsXMLLiteral(Node node) throws IOException, TransformerException {
        final String datatype = DomUtils.readAttribute(node, DATATYPE_ATTRIBUTE, null);
        if (!XML_LITERAL_DATATYPE.equals(datatype))
            return null;

        final String xmlSerializedNode = DomUtils.serializeToXML(node, false);
        return RDFUtils.literal(xmlSerializedNode, RDF.XMLLITERAL);
    }

    protected static boolean isXMLNSDeclared(Document document) {
        final String attributeValue = document.getDocumentElement().getAttribute(XMLNS_ATTRIBUTE);
        if (attributeValue.length() == 0)
            return false;
        return XMLNS_DEFAULT.equals(attributeValue);
    }

    /**
     * <a href="http://www.w3.org/TR/rdfa-syntax/#s_model">RDFa Syntax - Processing Model</a>.
     *
     * @param documentURL
     *            {@link URL} of the document to process
     * @param extractionResult
     *            a {@link ExtractionResult} to populate
     * @param document
     *            the {@link Document} to populate with parse content
     *
     * @throws RDFa11ParserException
     *             if there is an error parsing the document
     */
    public void processDocument(URL documentURL, Document document, ExtractionResult extractionResult)
            throws RDFa11ParserException {
        try {
            this.issueReport = extractionResult;

            // Check RDFa1.0[4.1.3] : default XMLNS declaration.
            if (!isXMLNSDeclared(document)) {
                reportError(document.getDocumentElement(),
                        String.format(Locale.ROOT,
                                "The default %s namespace is expected to be declared and equal to '%s' .",
                                XMLNS_ATTRIBUTE, XMLNS_DEFAULT));
            }

            try {
                documentBase = getDocumentBase(documentURL, document);
            } catch (MalformedURLException murle) {
                throw new RDFa11ParserException("Invalid document base URL.", murle);
            }

            // RDFa1.0[5.5.1]
            pushContext(document, new EvaluationContext(documentBase));

            depthFirstNode(document, extractionResult);

            assert listOfIncompleteTriples
                    .isEmpty() : "The list of incomplete triples is expected to be empty at the end of processing.";
        } finally {
            reset();
        }
    }

    /**
     * Resets the parser to the original state.
     */
    public void reset() {
        issueReport = null;
        documentBase = null;
        IRIMappingStack.clear();
        listOfIncompleteTriples.clear();
        evaluationContextStack.clear();
    }

    /**
     * Updates the vocabulary context with possible <em>@vocab</em> declarations.
     *
     * @param currentNode
     *            the current node.
     */
    protected void updateVocabulary(Node currentNode) {
        final String vocabularyStr = DomUtils.readAttribute(currentNode, VOCAB_ATTRIBUTE, null);
        if (vocabularyStr == null)
            return;
        try {
            pushVocabulary(currentNode, RDFUtils.iri(vocabularyStr));
        } catch (Exception e) {
            reportError(currentNode,
                    String.format(Locale.ROOT, "Invalid vocabulary [%s], must be a IRI.", vocabularyStr));
        }
    }

    /**
     * Updates the IRI mapping with the XMLNS attributes declared in the current node.
     *
     * @param node
     *            input node.
     */
    protected void updateIRIMapping(Node node) {
        final NamedNodeMap attributes = node.getAttributes();
        if (null == attributes)
            return;

        Node attribute;
        final List<PrefixMap> prefixMapList = new ArrayList<PrefixMap>();
        final String namespacePrefix = XMLNS_ATTRIBUTE + IRI_PREFIX_SEPARATOR;
        for (int a = 0; a < attributes.getLength(); a++) {
            attribute = attributes.item(a);
            if (attribute.getNodeName().startsWith(namespacePrefix)) {
                prefixMapList.add(new PrefixMap(attribute.getNodeName().substring(namespacePrefix.length()),
                        resolveIRI(attribute.getNodeValue())));
            }
        }

        extractPrefixes(node, prefixMapList);

        if (prefixMapList.size() == 0)
            return;
        pushMappings(node, prefixMapList);
    }

    /**
     * Returns a IRI mapping for a given prefix.
     *
     * @param prefix
     *            input prefix.
     *
     * @return IRI mapping.
     */
    protected IRI getMapping(String prefix) {
        for (IRIMapping IRIMapping : IRIMappingStack) {
            final IRI mapping = IRIMapping.map.get(prefix);
            if (mapping != null) {
                return mapping;
            }
        }
        return null;
    }

    /**
     * Resolves a <em>whitelist</em> separated list of <i>CURIE</i> or <i>URI</i>.
     *
     * @param n
     *            current node.
     * @param curieOrIRIList
     *            list of CURIE/URI.
     * @param termAllowed
     *            determine whether the term should be whitelisted.
     *
     * @return list of resolved URIs.
     *
     * @throws URISyntaxException
     *             if there is an error processing CURIE or URL
     */
    protected IRI[] resolveCIRIeOrIRIList(Node n, String curieOrIRIList, boolean termAllowed)
            throws URISyntaxException {
        if (curieOrIRIList == null || curieOrIRIList.trim().length() == 0)
            return new IRI[0];

        final String[] curieOrIRIListParts = curieOrIRIList.split("\\s");
        final List<IRI> result = new ArrayList<>();
        Resource curieOrIRI;
        for (String curieORIRIListPart : curieOrIRIListParts) {
            curieOrIRI = resolveCURIEOrIRI(curieORIRIListPart, termAllowed);
            if (curieOrIRI != null && curieOrIRI instanceof IRI) {
                result.add((IRI) curieOrIRI);
            } else {
                reportError(n, String.format(Locale.ROOT, "Invalid CURIE '%s' : expected IRI, found BNode.",
                        curieORIRIListPart));
            }
        }
        return result.toArray(new IRI[result.size()]);
    }

    /**
     * Resolves a IRI string as IRI.
     *
     * @param iriStr
     *            (partial) IRI string to be resolved.
     *
     * @return the resolved IRI.
     */
    protected IRI resolveIRI(String iriStr) {
        return isAbsoluteIRI(iriStr) ? RDFUtils.iri(iriStr) : RDFUtils.iri(this.documentBase.toExternalForm(), iriStr);
    }

    /**
     * Resolves a <i>CURIE</i> or <i>IRI</i> string.
     *
     * @param curieOrIRI
     *            individual of CURIE/URI to resolve
     * @param termAllowed
     *            if <code>true</code> the resolution can be a term.
     *
     * @return the resolved resource.
     */
    protected Resource resolveCURIEOrIRI(String curieOrIRI, boolean termAllowed) {
        if (isCURIE(curieOrIRI)) {
            return resolveNamespacedIRI(curieOrIRI.substring(1, curieOrIRI.length() - 1), ResolutionPolicy.NSRequired);
        }
        if (isAbsoluteIRI(curieOrIRI))
            return resolveIRI(curieOrIRI);
        return resolveNamespacedIRI(curieOrIRI,
                termAllowed ? ResolutionPolicy.TermAllowed : ResolutionPolicy.NSNotRequired);
    }

    /**
     * Pushes a context whiting the evaluation context stack, associated to tha given generation node.
     *
     * @param current
     * @param ec
     */
    private void pushContext(Node current, EvaluationContext ec) {
        ec.node = current;
        evaluationContextStack.push(ec);
    }

    /**
     * @return the peek evaluation context.
     */
    private EvaluationContext getContext() {
        return evaluationContextStack.peek();
    }

    /**
     * Pops out the peek evaluation context if ancestor of current node.
     *
     * @param current
     *            current node.
     */
    private void popContext(Node current) {
        final Node peekNode = evaluationContextStack.peek().node;
        if (DomUtils.isAncestorOf(peekNode, current)) {
            evaluationContextStack.pop();
        }
    }

    /**
     * Pushes a new vocabulary definition.
     *
     * @param currentNode
     *            node proving the vocabulary.
     * @param vocab
     *            the vocabulary IRI.
     */
    private void pushVocabulary(Node currentNode, IRI vocab) {
        vocabularyStack.push(new Vocabulary(currentNode, vocab));
    }

    /**
     * @return the current peek vocabulary.
     */
    private IRI getVocabulary() {
        if (vocabularyStack.isEmpty())
            return null;
        return vocabularyStack.peek().prefix;
    }

    /**
     * Pops out the vocabulary definition.
     *
     * @param current
     */
    private void popVocabulary(Node current) {
        if (vocabularyStack.isEmpty())
            return;
        if (DomUtils.isAncestorOf(current, vocabularyStack.peek().originatingNode)) {
            vocabularyStack.pop();
        }
    }

    /**
     * Purge all incomplete triples originated from a node that is descendant of <code>current</code>.
     *
     * @param current
     */
    private void purgeIncompleteTriples(Node current) {
        final List<IncompleteTriple> toBePurged = new ArrayList<>();
        for (IncompleteTriple incompleteTriple : listOfIncompleteTriples) {
            if (DomUtils.isAncestorOf(current, incompleteTriple.originatingNode, true)) {
                toBePurged.add(incompleteTriple);
            }
        }
        listOfIncompleteTriples.removeAll(toBePurged);
        toBePurged.clear();
    }

    /**
     * Reports an error to the error reporter.
     *
     * @param n
     *            originating node.
     * @param msg
     *            human readable message.
     */
    private void reportError(Node n, String msg) {
        final String errorMsg = String.format(Locale.ROOT, "Error while processing node [%s] : '%s'",
                DomUtils.getXPathForNode(n), msg);
        final int[] errorLocation = DomUtils.getNodeLocation(n);
        this.issueReport.notifyIssue(IssueReport.IssueLevel.WARNING, errorMsg,
                errorLocation == null ? -1 : errorLocation[0], errorLocation == null ? -1 : errorLocation[1]);
    }

    /**
     * Performs a <i>deep-first</i> tree visit on the given root node.
     *
     * @param node
     *            root node.
     * @param extractionResult
     */
    private void depthFirstNode(Node node, ExtractionResult extractionResult) {
        try {
            processNode(node, extractionResult);
        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.debug("Error while processing node.", e);
            reportError(node, e.getMessage());
        }
        depthFirstChildren(node.getChildNodes(), extractionResult);
        purgeIncompleteTriples(node);
    }

    /**
     * Performs a <i>deep-first</i> children list visit.
     *
     * @param nodeList
     * @param extractionResult
     */
    private void depthFirstChildren(NodeList nodeList, ExtractionResult extractionResult) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node child = nodeList.item(i);
            depthFirstNode(child, extractionResult);
            popMappings(child);
            popVocabulary(child);
            popContext(child);
        }
    }

    /**
     * Writes a triple on the extraction result.
     *
     * @param s
     * @param p
     * @param o
     * @param extractionResult
     */
    private void writeTriple(Resource s, IRI p, Value o, ExtractionResult extractionResult) {
        assert s != null : "subject   is null.";
        assert p != null : "predicate is null.";
        assert o != null : "object    is null.";
        extractionResult.writeTriple(s, p, o);
    }

    /**
     * Processes the current node on the extraction algorithm. All the steps of this algorithm are annotated with the
     * specification and section which describes it. The annotation is at form
     * <em>RDFa&lt;spec-version%gt;[&lt;section&gt;]</em>
     *
     * @param currentElement
     * @param extractionResult
     *
     * @throws Exception
     */
    // TODO: add references to the RDFa 1.1 algorithm.
    private void processNode(Node currentElement, ExtractionResult extractionResult) throws Exception {
        final EvaluationContext currentEvaluationContext = getContext();
        try {
            if (currentElement.getNodeType() != Node.DOCUMENT_NODE && currentElement.getNodeType() != Node.ELEMENT_NODE)
                return;

            // RDFa1.1[7.5.3]
            updateVocabulary(currentElement);

            // RDFa1.0[5.5.2] / RDFa1.1[7.5.4]
            // Node currentElement = node;
            updateIRIMapping(currentElement);

            // RDFa1.0[5.5.3] / RDFa1.1[7.5.5]
            updateLanguage(currentElement, currentEvaluationContext);

            if (!isRelativeNode(currentElement)) {
                // RDFa1.0[5.5.4] / RDFa1.1[7.5.6]
                establishNewSubject(currentElement, currentEvaluationContext);
            } else {
                // RDFa1.0[5.5.5] / RDFa1.1[7.5.7]
                establishNewSubjectCurrentObjectResource(currentElement, currentEvaluationContext);
            }

            /*
             * if(currentEvaluationContext.newSubject == null) { currentEvaluationContext.newSubject =
             * resolveIRI(documentBase.toExternalForm()); } assert currentEvaluationContext.newSubject != null :
             * "newSubject must be not null.";
             */
            if (currentEvaluationContext.newSubject == null)
                return;
            if (logger.isDebugEnabled())
                logger.debug("newSubject: " + currentEvaluationContext.newSubject);

            // RDFa1.0[5.5.6] / RDFa1.1[7.5.8]
            final IRI[] types = getTypes(currentElement);
            for (IRI type : types) {
                writeTriple(currentEvaluationContext.newSubject, RDF.TYPE, type, extractionResult);
            }

            // RDFa1.0[5.5.7] / RDFa1.1[7.5.9]
            final IRI[] rels = getRels(currentElement);
            final IRI[] revs = getRevs(currentElement);
            if (currentEvaluationContext.currentObjectResource != null) {
                for (IRI rel : rels) {
                    writeTriple(currentEvaluationContext.newSubject, rel,
                            currentEvaluationContext.currentObjectResource, extractionResult);
                }
                for (IRI rev : revs) {
                    writeTriple(currentEvaluationContext.currentObjectResource, rev,
                            currentEvaluationContext.newSubject, extractionResult);
                }
            } else { // RDFa1.0[5.5.8] / RDFa1.1[7.5.10]
                for (IRI rel : rels) {
                    listOfIncompleteTriples.add(new IncompleteTriple(currentElement,
                            currentEvaluationContext.newSubject, rel, IncompleteTripleDirection.Forward));
                }
                for (IRI rev : revs) {
                    listOfIncompleteTriples.add(new IncompleteTriple(currentElement,
                            currentEvaluationContext.newSubject, rev, IncompleteTripleDirection.Reverse));
                }
            }

            // RDFa1.0[5.5.9] / RDFa1.1[7.5.11]
            final Value currentObject = getCurrentObject(currentElement);
            final IRI[] predicates = getPredicate(currentElement);
            if (currentObject != null && predicates != null) {
                for (IRI predicate : predicates) {
                    writeTriple(currentEvaluationContext.newSubject, predicate, currentObject, extractionResult);
                }
            }

            // RDFa1.0[5.5.10] / RDFa1.1[7.5.12]
            if (!currentEvaluationContext.skipElem && currentEvaluationContext.newSubject != null) {
                for (IncompleteTriple incompleteTriple : listOfIncompleteTriples) {
                    incompleteTriple.produceTriple(currentElement, currentEvaluationContext.newSubject,
                            extractionResult);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            // RDFa1.0[5.5.11] / RDFa1.1[7.5.13]
            if (currentEvaluationContext.recourse) {
                EvaluationContext newEvaluationContext = new EvaluationContext(currentEvaluationContext.base);
                if (currentEvaluationContext.skipElem) {
                    newEvaluationContext.language = currentEvaluationContext.language;
                } else {
                    newEvaluationContext.base = currentEvaluationContext.base;

                    if (currentEvaluationContext.newSubject != null) {
                        newEvaluationContext.parentSubject = currentEvaluationContext.newSubject;
                    } else {
                        newEvaluationContext.parentSubject = currentEvaluationContext.parentSubject;
                    }

                    if (currentEvaluationContext.currentObjectResource != null) {
                        newEvaluationContext.parentObject = currentEvaluationContext.currentObjectResource;
                    } else if (currentEvaluationContext.newSubject != null) {
                        newEvaluationContext.parentObject = currentEvaluationContext.newSubject;
                    } else {
                        newEvaluationContext.parentObject = currentEvaluationContext.parentSubject;
                    }

                    newEvaluationContext.language = currentEvaluationContext.language;
                }
                pushContext(currentElement, newEvaluationContext);
            }
        }
    }

    /**
     * Extract IRI namespaces (prefixes) from the current node.
     *
     * @param node
     * @param prefixMapList
     */
    private void extractPrefixes(Node node, List<PrefixMap> prefixMapList) {
        final String prefixAttribute = DomUtils.readAttribute(node, PREFIX_ATTRIBUTE, null);
        if (prefixAttribute == null)
            return;
        final String[] prefixParts = extractPrefixSections(prefixAttribute);
        for (String prefixPart : prefixParts) {
            int splitPoint = prefixPart.indexOf(IRI_PREFIX_SEPARATOR);
            final String prefix = prefixPart.substring(0, splitPoint);
            if (prefix.length() == 0) {
                reportError(node,
                        String.format(Locale.ROOT, "Invalid prefix length in prefix attribute '%s'", prefixAttribute));
                continue;
            }
            final IRI iri;
            final String iriStr = prefixPart.substring(splitPoint + 1);
            try {
                iri = resolveIRI(iriStr);
            } catch (Exception e) {
                reportError(node, String.format(Locale.ROOT, "Resolution of prefix '%s' defines an invalid IRI: '%s'",
                        prefixAttribute, iriStr));
                continue;
            }
            prefixMapList.add(new PrefixMap(prefix, iri));
        }
    }

    /**
     * Updates the current language.
     *
     * @param node
     * @param currentEvaluationContext
     */
    private void updateLanguage(Node node, EvaluationContext currentEvaluationContext) {
        final String candidateLanguage = DomUtils.readAttribute(node, XML_LANG_ATTRIBUTE, null);
        if (candidateLanguage != null)
            currentEvaluationContext.language = candidateLanguage;
    }

    /**
     * Establish the new subject for the current recursion. See <i>RDFa 1.0 Specification section 5.5.4</i>, <i>RDFa 1.1
     * Specification section 7.5.6</i>.
     *
     * @param node
     * @param currentEvaluationContext
     *
     * @throws URISyntaxException
     */
    private void establishNewSubject(Node node, EvaluationContext currentEvaluationContext) throws URISyntaxException {
        String candidateIRIOrCURIE;
        for (String subjectAttribute : SUBJECT_ATTRIBUTES) {
            candidateIRIOrCURIE = DomUtils.readAttribute(node, subjectAttribute, null);
            if (candidateIRIOrCURIE != null) {
                currentEvaluationContext.newSubject = resolveCURIEOrIRI(candidateIRIOrCURIE, false);
                return;
            }
        }

        if (node.getNodeName().equalsIgnoreCase(HEAD_TAG) || node.getNodeName().equalsIgnoreCase(BODY_TAG)) {
            currentEvaluationContext.newSubject = resolveIRI(currentEvaluationContext.base.toString());
            return;
        }

        if (DomUtils.hasAttribute(node, TYPEOF_ATTRIBUTE)) {
            currentEvaluationContext.newSubject = RDFUtils.bnode();
            return;
        }

        if (DomUtils.hasAttribute(node, PROPERTY_ATTRIBUTE)) {
            currentEvaluationContext.skipElem = true;
        }
        if (currentEvaluationContext.parentObject != null) {
            currentEvaluationContext.newSubject = (Resource) currentEvaluationContext.parentObject;
            return;
        }

        currentEvaluationContext.newSubject = null;
    }

    /**
     * Establishes the new subject and the current object resource.
     *
     * See <i>RDFa 1.0 Specification section 5.5.5</i>, <i>RDFa 1.1 Specification section 7.5.7</i>.
     *
     * @param node
     * @param currentEvaluationContext
     *
     * @throws URISyntaxException
     */
    private void establishNewSubjectCurrentObjectResource(Node node, EvaluationContext currentEvaluationContext)
            throws URISyntaxException {
        // Subject.
        String candidateIRIOrCURIE;
        candidateIRIOrCURIE = DomUtils.readAttribute(node, ABOUT_ATTRIBUTE, null);
        if (candidateIRIOrCURIE != null) {
            currentEvaluationContext.newSubject = resolveCURIEOrIRI(candidateIRIOrCURIE, false);
        } else {
            candidateIRIOrCURIE = DomUtils.readAttribute(node, SRC_ATTRIBUTE, null);
            if (candidateIRIOrCURIE != null) {
                currentEvaluationContext.newSubject = resolveIRI(candidateIRIOrCURIE);
            } else {
                if (node.getNodeName().equalsIgnoreCase(HEAD_TAG) || node.getNodeName().equalsIgnoreCase(BODY_TAG)) {
                    currentEvaluationContext.newSubject = resolveIRI(currentEvaluationContext.base.toString());
                } else {
                    if (DomUtils.hasAttribute(node, TYPEOF_ATTRIBUTE)) {
                        currentEvaluationContext.newSubject = RDFUtils.bnode();
                    } else {
                        if (currentEvaluationContext.parentObject != null) {
                            currentEvaluationContext.newSubject = (Resource) currentEvaluationContext.parentObject;
                        }
                    }
                }
            }
        }

        // Object.
        candidateIRIOrCURIE = DomUtils.readAttribute(node, RESOURCE_ATTRIBUTE, null);
        if (candidateIRIOrCURIE != null) {
            currentEvaluationContext.currentObjectResource = resolveCURIEOrIRI(candidateIRIOrCURIE, false);
            return;
        }

        candidateIRIOrCURIE = DomUtils.readAttribute(node, HREF_ATTRIBUTE, null);
        if (candidateIRIOrCURIE != null) {
            currentEvaluationContext.currentObjectResource = resolveIRI(candidateIRIOrCURIE);
            return;
        }
        currentEvaluationContext.currentObjectResource = null;
    }

    private IRI[] getTypes(Node node) throws URISyntaxException {
        final String typeOf = DomUtils.readAttribute(node, TYPEOF_ATTRIBUTE, null);
        return resolveCIRIeOrIRIList(node, typeOf, true);
    }

    private IRI[] getRels(Node node) throws URISyntaxException {
        final String rel = DomUtils.readAttribute(node, REL_ATTRIBUTE, null);
        return resolveCIRIeOrIRIList(node, rel, true);
    }

    private IRI[] getRevs(Node node) throws URISyntaxException {
        final String rev = DomUtils.readAttribute(node, REV_ATTRIBUTE, null);
        return resolveCIRIeOrIRIList(node, rev, true);
    }

    private IRI[] getPredicate(Node node) throws URISyntaxException {
        final String candidateIRI = DomUtils.readAttribute(node, PROPERTY_ATTRIBUTE, null);
        if (candidateIRI == null)
            return null;
        return resolveCIRIeOrIRIList(node, candidateIRI, true);
    }

    /**
     * Establishes the new object value. See <i>RDFa 1.0 Specification section 5.5.9</i>, <i>RDFa 1.1 Specification
     * section 7.5.11</i>.
     *
     * @param node
     *
     * @return
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws TransformerException
     */
    private Value getCurrentObject(Node node) throws URISyntaxException, IOException, TransformerException {
        final String candidateObject = DomUtils.readAttribute(node, HREF_ATTRIBUTE, null);
        if (candidateObject != null) {
            return resolveIRI(candidateObject);
        } else {
            return gerCurrentObjectLiteral(node);
        }
    }

    private Literal gerCurrentObjectLiteral(Node node) throws URISyntaxException, IOException, TransformerException {
        final EvaluationContext currentEvaluationContext = getContext();
        Literal literal;

        literal = getAsTypedLiteral(node);
        if (literal != null)
            return literal;

        literal = getAsXMLLiteral(node);
        if (literal != null) {
            currentEvaluationContext.recourse = false;
            return literal;
        }

        literal = getAsPlainLiteral(node, currentEvaluationContext.language);
        if (literal != null)
            return literal;

        return null;
    }

    private static String getNodeContent(Node node) {
        final String candidateContent = DomUtils.readAttribute(node, CONTENT_ATTRIBUTE, null);
        if (candidateContent != null)
            return candidateContent;
        return node.getTextContent();
    }

    /**
     * Extracts the current typed literal from the given node. See <i>RDFa 1.0 Specification section 5.5.9.1</i>.
     *
     * @param node
     *
     * @return
     *
     * @throws URISyntaxException
     */
    private Literal getAsTypedLiteral(Node node) throws URISyntaxException {
        final String datatype = DomUtils.readAttribute(node, DATATYPE_ATTRIBUTE, null);
        if (datatype == null || datatype.trim().length() == 0 || XML_LITERAL_DATATYPE.equals(datatype.trim())) {
            return null;
        }
        final Resource curieOrIRI = resolveCURIEOrIRI(datatype, true);
        return RDFUtils.literal(getNodeContent(node), curieOrIRI instanceof IRI ? (IRI) curieOrIRI : null);
    }

    private void pushMappings(Node sourceNode, List<PrefixMap> prefixMapList) {

        final Map<String, IRI> mapping = new HashMap<>();
        for (PrefixMap prefixMap : prefixMapList) {
            mapping.put(prefixMap.prefix, prefixMap.IRI);
        }
        IRIMappingStack.push(new IRIMapping(sourceNode, mapping));
    }

    private void popMappings(Node node) {
        if (IRIMappingStack.isEmpty())
            return;
        final IRIMapping peek = IRIMappingStack.peek();
        if (!DomUtils.isAncestorOf(peek.sourceNode, node)) {
            IRIMappingStack.pop();
        }
    }

    /**
     * Resolve a namespaced IRI, if <code>safe</code> is <code>true</code> then the mapping must define a prefix,
     * otherwise it is considered relative.
     *
     * @param mapping
     * @param resolutionPolicy
     *
     * @return
     */
    private Resource resolveNamespacedIRI(String mapping, ResolutionPolicy resolutionPolicy) {
        if (mapping.indexOf(IRI_PATH_SEPARATOR) == 0) { // Begins with '/'
            mapping = mapping.substring(1);
        }

        final int prefixSeparatorIndex = mapping.indexOf(':');
        if (prefixSeparatorIndex == -1) { // there is no prefix separator.
            if (resolutionPolicy == ResolutionPolicy.NSRequired) {
                throw new IllegalArgumentException(
                        String.format(Locale.ROOT, "Invalid mapping string [%s], must declare a prefix.", mapping));
            }
            if (resolutionPolicy == ResolutionPolicy.TermAllowed) {
                final IRI currentVocabulary = getVocabulary();
                // Mapping is a TERM.
                if (currentVocabulary != null) {
                    return resolveIRI(currentVocabulary.toString() + mapping);
                }
            }
            return resolveIRI(documentBase.toString() + mapping);
        }

        final String prefix = mapping.substring(0, prefixSeparatorIndex);
        final IRI curieMapping = getMapping(prefix);
        if (curieMapping == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Cannot map prefix '%s'", prefix));
        }
        final String candidateCURIEStr = curieMapping.toString() + mapping.substring(prefixSeparatorIndex + 1);
        final java.net.URI candidateCURIE;
        try {
            candidateCURIE = new java.net.URI(candidateCURIEStr);
        } catch (URISyntaxException IRIse) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Invalid CURIE '%s'", candidateCURIEStr));
        }
        return resolveIRI(candidateCURIE.isAbsolute() ? candidateCURIE.toString()
                : documentBase.toString() + candidateCURIE.toString());
    }

    /**
     * The resolution policy provided to the method {@link #resolveNamespacedIRI(String, ResolutionPolicy)}.
     */
    enum ResolutionPolicy {
        NSNotRequired, NSRequired, TermAllowed
    }

    /**
     * Defines an evaluation context.
     */
    private class EvaluationContext {
        private Node node;
        private URL base;
        private Resource parentSubject;
        private Value parentObject;
        private String language;
        private boolean recourse;
        private boolean skipElem;
        private Resource newSubject;
        private Resource currentObjectResource;

        /**
         * Sections <em>RDFa1.0[5.5]</em>, <em>RDFa1.0[5.5.1]</em>, <em>RDFa1.1[7.5.1]</em> .
         *
         * @param base
         */
        EvaluationContext(URL base) {
            this.base = base;
            this.parentSubject = resolveIRI(base.toExternalForm());
            this.parentObject = null;
            this.language = null;
            this.recourse = true;
            this.skipElem = false;
            this.newSubject = null;
            this.currentObjectResource = null;
        }
    }

    /**
     * Defines a prefix mapping.
     */
    private static class PrefixMap {
        final String prefix;
        final IRI IRI;

        public PrefixMap(String prefix, IRI IRI) {
            this.prefix = prefix;
            this.IRI = IRI;
        }
    }

    /**
     * Defines a IRI mapping.
     */
    private static class IRIMapping {
        final Node sourceNode;
        final Map<String, IRI> map;

        public IRIMapping(Node sourceNode, Map<String, IRI> map) {
            this.sourceNode = sourceNode;
            this.map = map;
        }
    }

    /**
     * Defines the direction of an {@link IncompleteTriple}.
     */
    private enum IncompleteTripleDirection {
        Forward, Reverse
    }

    /**
     * Defines an incomplete triple.
     */
    private static class IncompleteTriple {
        final Node originatingNode;
        final Resource subject;
        final IRI predicate;
        final IncompleteTripleDirection direction;

        public IncompleteTriple(Node originatingNode, Resource subject, IRI predicate,
                IncompleteTripleDirection direction) {
            if (originatingNode == null || subject == null || predicate == null || direction == null)
                throw new IllegalArgumentException();

            this.originatingNode = originatingNode;
            this.subject = subject;
            this.predicate = predicate;
            this.direction = direction;
        }

        public boolean produceTriple(Node resourceNode, Resource r, ExtractionResult extractionResult) {
            if (!DomUtils.isAncestorOf(originatingNode, resourceNode, true))
                return false;

            if (r == null)
                throw new IllegalArgumentException();
            switch (direction) {
            case Forward:
                extractionResult.writeTriple(subject, predicate, r);
                break;
            case Reverse:
                extractionResult.writeTriple(r, predicate, subject);
                break;
            default:
                throw new IllegalStateException();
            }
            return true;
        }
    }

    /**
     * Defines a vocabulary object.
     */
    private static class Vocabulary {
        final Node originatingNode;
        final IRI prefix;

        public Vocabulary(Node originatingNode, IRI prefix) {
            this.originatingNode = originatingNode;
            this.prefix = prefix;
        }
    }

}
