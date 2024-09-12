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
package io.github.sparqlanything.html.org.semarglproject.rdf.rdfa;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler;
import io.github.sparqlanything.html.org.semarglproject.rdf.RdfXmlParser;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.DocumentContext;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.EvalContext;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.VocabManager;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.Vocabulary;
import io.github.sparqlanything.html.org.semarglproject.ri.MalformedCurieException;
import io.github.sparqlanything.html.org.semarglproject.ri.MalformedIriException;
import io.github.sparqlanything.html.org.semarglproject.ri.RIUtils;
import io.github.sparqlanything.html.org.semarglproject.sink.Pipe;
import io.github.sparqlanything.html.org.semarglproject.sink.TripleSink;
import io.github.sparqlanything.html.org.semarglproject.sink.XmlSink;
import io.github.sparqlanything.html.org.semarglproject.source.StreamProcessor;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDF;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDFa;
import io.github.sparqlanything.html.org.semarglproject.vocab.XSD;
import io.github.sparqlanything.html.org.semarglproject.xml.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Implementation of streaming RDFa (<a href="http://www.w3.org/TR/2008/REC-rdfa-syntax-20081014/">1.0</a> and
 * <a href="http://www.w3.org/TR/2012/REC-rdfa-core-20120607/">1.1</a>) parser. Supports HTML4, HTML5, XHTML1,
 * XHTML5, XML and SVG inputs. Provides RDFa version and document syntax autodetection.
 *
 * <br>
 *     List of supported options:
 *     <ul>
 *         <li>{@link #RDFA_VERSION_PROPERTY}</li>
 *         <li>{@link StreamProcessor#PROCESSOR_GRAPH_HANDLER_PROPERTY}</li>
 *         <li>{@link #ENABLE_OUTPUT_GRAPH}</li>
 *         <li>{@link #ENABLE_PROCESSOR_GRAPH}</li>
 *         <li>{@link #ENABLE_VOCAB_EXPANSION}</li>
 *     </ul>
 */
public final class RdfaParser extends Pipe<TripleSink> implements XmlSink, TripleSink, ProcessorGraphHandler {

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * RDFa version compatibility. Allowed values are {@link RDFa#VERSION_10} and {@link RDFa#VERSION_11}.
     */
    public static final String RDFA_VERSION_PROPERTY =
            "http://semarglproject.org/rdfa/properties/version";

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * Enables or disables generation of triples from output graph.
     */
    public static final String ENABLE_OUTPUT_GRAPH =
            "http://semarglproject.org/rdfa/properties/enable-output-graph";

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * Enables or disables generation of triples from processor graph.
     * ProcessorGraphHandler will receive events regardless of this option.
     */
    public static final String ENABLE_PROCESSOR_GRAPH =
            "http://semarglproject.org/rdfa/properties/enable-processor-graph";

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * Enables or disables <a href="http://www.w3.org/TR/2012/REC-rdfa-core-20120607/#s_vocab_expansion">vocabulary
     * expansion</a> feature.
     */
    public static final String ENABLE_VOCAB_EXPANSION =
            "http://semarglproject.org/rdfa/properties/enable-vocab-expansion";

    static final String AUTODETECT_DATE_DATATYPE = "AUTODETECT_DATE_DATATYPE";

    private static final ThreadLocal<VocabManager> VOCAB_MANAGER = new ThreadLocal<VocabManager>() {
        @Override
        protected VocabManager initialValue() {
            return new VocabManager();
        }
    };

    // flag used in incomplTriple list to indicate that following element should be
    // treated as having @rev relation instead of @rel
    private static final String REVERSED_TRIPLE_FLAG = null;
    // flag used in listMapping list to indicate that following two elements represent literal object
    // that allows to save some GC time and avoid creating literal objects hierarchy with following instanceof checks
    private static final String LITERAL_OBJECT_FLAG = null;

    private static final String BODY = "body";
    private static final String HEAD = "head";
    private static final String VERSION = "version";
    private static final String METADATA = "metadata";

    private static final String PLAIN_LITERAL = "";
    private static final String XHTML_DEFAULT_XMLNS = "http://www.w3.org/1999/xhtml";

    private static final String XHTML_VOCAB = "http://www.w3.org/1999/xhtml/vocab#";

    // html5 support
    private static final String DATETIME_ATTR = "datetime";
    private static final String TIME_QNAME = "time";
    private static final String VALUE_ATTR = "value";
    private static final String DATA_ATTR = "data";
    private static final String XML_BASE = "xml:base";

    // keys for coalesce method
    private static final String BASE_IF_HEAD_OR_BODY = "bihob";
    private static final String BASE_IF_ROOT_NODE = "birn";
    private static final String PARENT_OBJECT = "poie";
    private static final String BNODE_IF_TYPEOF = RDFa.TYPEOF_ATTR;

    private Deque<EvalContext> contextStack = null;

    private StringBuilder xmlString = null;
    private List<String> xmlStringPred = null;
    private String xmlStringSubj = null;

    private Short forcedRdfaVersion = null;
    private boolean sinkOutputGraph;
    private boolean sinkProcessorGraph;

    private boolean expandVocab;
    private final DocumentContext dh;
    private final Splitter splitter;
    private Locator locator = null;

    private ProcessorGraphHandler processorGraphHandler = null;

    private boolean rdfXmlInline = false;
    private XmlSink rdfXmlParser = null;

    private Map<String, List<String>> patternProps = new HashMap<String, List<String>>();
    private List<String> copyingPairs = new ArrayList<String>();

    private final Map<String, String> overwriteMappings = new HashMap<String, String>();

    private RdfaParser(TripleSink sink) {
        super(sink);
        contextStack = new LinkedList<EvalContext>();
        dh = new DocumentContext(this);
        splitter = new Splitter();
        sinkProcessorGraph = true;
        sinkOutputGraph = true;
        expandVocab = false;
    }

    /**
     * Creates instance of RdfaParser connected to specified sink
     * @param sink sink to be connected to
     * @return instance of RdfaParser
     */
    public static XmlSink connect(TripleSink sink) {
        return new RdfaParser(sink);
    }

    @Override
    public void startDocument() {
        EvalContext initialContext = EvalContext.createInitialContext(dh);
        initialContext.iriMappings.put("", XHTML_VOCAB);
        contextStack.push(initialContext);

        xmlString = null;
        xmlStringPred = null;
        xmlStringSubj = null;

        rdfXmlInline = false;
        rdfXmlParser = null;
    }

    @Override
    public void endDocument() throws SAXException {
        if (sinkOutputGraph) {
            Iterator<String> iterator = copyingPairs.iterator();
            while (iterator.hasNext()) {
                String subj = iterator.next();
                String pattern = iterator.next();
                if (patternProps.containsKey(pattern)) {
                    copyProps(subj, patternProps.get(pattern));
                }
            }

            iterator = copyingPairs.iterator();
            while (iterator.hasNext()) {
                iterator.next();
                String pattern = iterator.next();
                patternProps.remove(pattern);
            }
            for (String pattern : patternProps.keySet()) {
                addNonLiteralInternal(pattern, RDF.TYPE, RDFa.PATTERN);
                copyProps(pattern, patternProps.get(pattern));
            }
        }

        dh.clear();
        contextStack.clear();
        patternProps.clear();
        copyingPairs.clear();
    }

    @Override
    public void startElement(String nsUri, String localName, String qName, Attributes attrs) throws SAXException {
        if (rdfXmlInline) {
            rdfXmlParser.startElement(nsUri, localName, qName, attrs);
            return;
        } else if (dh.documentFormat == DocumentContext.FORMAT_SVG && localName.equals(METADATA)) {
            if (rdfXmlParser == null) {
                rdfXmlParser = RdfXmlParser.connect(this);
                rdfXmlParser.setBaseUri(dh.base);
                rdfXmlParser.startDocument();
            }
            rdfXmlInline = true;
            return;
        }

        if (contextStack.size() < 4) {
            String oldBase = dh.base;
            dh.detectFormat(localName, qName, attrs.getValue(VERSION));
            dh.detectBase(qName, attrs.getValue(XML_BASE), attrs.getValue(RDFa.HREF_ATTR));
            if (!dh.base.equals(oldBase)) {
                for (EvalContext ctx : contextStack) {
                    ctx.updateBase(oldBase, dh.base);
                }
            }
        }

        EvalContext parent = contextStack.peek();
        if (parent.parsingLiteral) {
            xmlString.append(XmlUtils.serializeOpenTag(nsUri, qName, parent.iriMappings, attrs, false));
        }

        if (dh.rdfaVersion > RDFa.VERSION_10 && attrs.getValue(RDFa.PREFIX_ATTR) != null) {
            for (Iterator<String> iterator = splitter.split(attrs.getValue(RDFa.PREFIX_ATTR)); iterator.hasNext(); ) {
                String prefix = iterator.next();
                int prefixLength = prefix.length();
                if (prefixLength < 2 || prefix.charAt(prefixLength - 1) != ':' || !iterator.hasNext()) {
                    continue;
                }
                String uri = iterator.next();
                startPrefixMapping(prefix.substring(0, prefixLength - 1), uri);
            }
        }

        String lang = attrs.getValue(XmlUtils.XML_LANG);
        if (lang == null) {
            lang = attrs.getValue(XmlUtils.LANG);
        }
        EvalContext current = parent.initChildContext(attrs.getValue(RDFa.PROFILE_ATTR),
                attrs.getValue(RDFa.VOCAB_ATTR), lang, overwriteMappings);
        overwriteMappings.clear();

        boolean skipTerms = dh.rdfaVersion > RDFa.VERSION_10 && attrs.getValue(RDFa.PROPERTY_ATTR) != null
                && (dh.documentFormat == DocumentContext.FORMAT_HTML4
                || dh.documentFormat == DocumentContext.FORMAT_HTML5);
        List<String> rels = convertRelRevToList(attrs.getValue(RDFa.REL_ATTR), skipTerms);
        List<String> revs = convertRelRevToList(attrs.getValue(RDFa.REV_ATTR), skipTerms);
        boolean noRelsAndRevs = rels == null && revs == null;

        boolean skipElement = findSubjectAndObject(qName, attrs, noRelsAndRevs, current, parent);

        // don't fill parent list if subject was changed at this
        // or previous step by current.parentObject
        if (dh.rdfaVersion > RDFa.VERSION_10 && current.subject != null && (!current.subject.equals(parent.object)
                || parent.subject != null && !parent.subject.equals(parent.object))) {
            // RDFa Core 1.1 processing sequence step 8
            current.listMapping = new HashMap<String, List<String>>();
        }

        processRels(attrs, rels, current);
        processRevs(revs, current);

        if (current.object == null && !noRelsAndRevs) {
            current.object = dh.createBnode(false);
        }

        processPropertyAttr(qName, attrs, current, parent, noRelsAndRevs);

        if (dh.rdfaVersion > RDFa.VERSION_10) {
            processRoleAttribute(attrs.getValue(RDFa.ID_ATTR), attrs.getValue(RDFa.ROLE_ATTR), current);
        }

        if (!skipElement) {
            // RDFa Core 1.0 processing sequence step 10
            // RDFa Core 1.1 processing sequence step 12
            processIncompleteTriples(current, parent);
        }

        // RDFa Core 1.0 processing sequence step 11
        // RDFa Core 1.1 processing sequence step 13
        pushContext(current, parent, skipElement);
    }

    /**
     * Splits @rel or @rev attribute value to list of predicates. Terms can be optionally ignored.
     * @param propertyVal value of @rel or @rev attribute
     * @param skipTerms is terms should be skipped
     * @return list of predicates
     */
    private List<String> convertRelRevToList(String propertyVal, boolean skipTerms) {
        if (propertyVal == null) {
            return null;
        }
        List<String> result = new ArrayList<String>();
        Iterator<String> iterator = splitter.split(propertyVal);
        while (splitter.hasNext()) {
            String pred = iterator.next();
            if (skipTerms && pred.indexOf(':') == -1) {
                continue;
            }
            result.add(pred);
        }
        if (skipTerms && result.isEmpty()) {
            result = null;
        }
        return result;
    }

    /**
     * Generates triples related to @role attribute
     * @param id value of @id attribute
     * @param roleVal value of @role attribute
     * @param current current context
     */
    private void processRoleAttribute(String id, String roleVal, EvalContext current) {
        if (roleVal == null) {
            return;
        }
        String subject;
        if (id != null) {
            subject = dh.base + '#' + id;
        } else {
            subject = dh.createBnode(true);
        }
        Iterator<String> iterator = splitter.split(roleVal);
        while (splitter.hasNext()) {
            try {
                String role = current.resolveRole(iterator.next());
                addNonLiteral(subject, XHTML_VOCAB + "role", role);
            } catch (MalformedIriException e) {
                // do nothing
            }
        }
    }

    /**
     * Determines object and subject for current context
     * @param qName node's qName
     * @param attrs node's attributes
     * @param noRelAndRev is no @rel and @rev attributes specified
     * @param current current context
     * @param parent parent context
     * @return skip element flag
     */
    private boolean findSubjectAndObject(String qName, Attributes attrs, boolean noRelAndRev, EvalContext current,
                                         EvalContext parent) {
        String newSubject = null;
        try {
            if (dh.rdfaVersion > RDFa.VERSION_10) {
                if (noRelAndRev) {
                    // RDFa Core 1.1 processing sequence step 5
                    if (attrs.getValue(RDFa.PROPERTY_ATTR) != null && attrs.getValue(RDFa.CONTENT_ATTR) == null
                            && attrs.getValue(VALUE_ATTR) == null && attrs.getValue(RDFa.DATATYPE_ATTR) == null) {
                        // RDFa Core 1.1 processing sequence step 5.1
                        current.subject = coalesce(qName, attrs, parent, current, RDFa.ABOUT_ATTR,
                                BASE_IF_ROOT_NODE, PARENT_OBJECT);

                        if (attrs.getValue(RDFa.TYPEOF_ATTR) != null) {
                            current.object = coalesce(qName, attrs, parent, current, RDFa.ABOUT_ATTR, BASE_IF_ROOT_NODE,
                                    RDFa.RESOURCE_ATTR, DATA_ATTR, RDFa.HREF_ATTR, RDFa.SRC_ATTR, BNODE_IF_TYPEOF);
                            newSubject = current.object;
                        }
                    } else {
                        // RDFa Core 1.1 processing sequence step 5.2
                        current.subject = coalesce(qName, attrs, parent, current, RDFa.ABOUT_ATTR,
                                RDFa.RESOURCE_ATTR, DATA_ATTR, RDFa.HREF_ATTR, RDFa.SRC_ATTR, BASE_IF_ROOT_NODE,
                                BNODE_IF_TYPEOF, PARENT_OBJECT);
                        if (attrs.getValue(RDFa.TYPEOF_ATTR) != null) {
                            newSubject = current.subject;
                        }
                    }
                } else {
                    // RDFa Core 1.1 processing sequence step 6
                    current.object = coalesce(qName, attrs, parent, current, RDFa.RESOURCE_ATTR, DATA_ATTR,
                            RDFa.HREF_ATTR, RDFa.SRC_ATTR);
                    current.subject = coalesce(qName, attrs, parent, current, RDFa.ABOUT_ATTR,
                            BASE_IF_ROOT_NODE, PARENT_OBJECT);
                    if (attrs.getValue(RDFa.TYPEOF_ATTR) != null) {
                        if (attrs.getValue(RDFa.ABOUT_ATTR) != null) {
                            newSubject = current.subject;
                        } else {
                            if (current.object == null) {
                                current.object = dh.createBnode(noRelAndRev);
                            }
                            newSubject = current.object;
                        }
                    }
                }
            } else {
                if (noRelAndRev) {
                    // RDFa Core 1.0 processing sequence step 4
                    current.subject = coalesce(qName, attrs, parent, current, RDFa.ABOUT_ATTR, RDFa.SRC_ATTR,
                            RDFa.RESOURCE_ATTR, RDFa.HREF_ATTR, BASE_IF_HEAD_OR_BODY, BNODE_IF_TYPEOF, PARENT_OBJECT);
                } else {
                    // RDFa Core 1.0 processing sequence step 5
                    current.subject = coalesce(qName, attrs, parent, current, RDFa.ABOUT_ATTR, RDFa.SRC_ATTR,
                            BASE_IF_HEAD_OR_BODY, BNODE_IF_TYPEOF, PARENT_OBJECT);
                    current.object = coalesce(qName, attrs, parent, current, RDFa.RESOURCE_ATTR, RDFa.HREF_ATTR);
                }
                if (attrs.getValue(RDFa.TYPEOF_ATTR) != null) {
                    newSubject = current.subject;
                }
            }
        }  catch (MalformedIriException e) {
            warning(RDFa.WARNING, e.getMessage());
            pushContextNoLiteral(current, parent);
        }

        if (newSubject != null) {
            // RDFa Core 1.0 processing sequence step 6
            // RDFa Core 1.1 processing sequence step 7
            Iterator<String> iterator = splitter.split(attrs.getValue(RDFa.TYPEOF_ATTR));
            while (splitter.hasNext()) {
                try {
                    String iri = current.resolvePredOrDatatype(iterator.next());
                    addNonLiteral(newSubject, RDF.TYPE, iri);
                } catch (MalformedIriException e) {
                    // do nothing
                }
            }
        }
        return noRelAndRev && attrs.getValue(RDFa.PROPERTY_ATTR) == null
                && (current.subject == null && parent.object == null || current.subject.equals(parent.object));
    }

    /**
     * Iterates through attribute names list and returns first not null
     * value of attribute with such name. Also processes special cases
     * if no such attributes found:
     * <ul>
     *     <li>{@link #BNODE_IF_TYPEOF} - returns new bnode if typeof attr found</li>
     *     <li>{@link #PARENT_OBJECT} - returns parent.object</li>
     *     <li>{@link #BASE_IF_HEAD_OR_BODY} - returns base if processing head or body node in HTML</li>
     * </ul>
     *
     * @param tagName name of processed element
     * @param attrs attribute list
     * @param parent parent context
     * @param current current context
     * @param attrNames prioritized list of attributes
     * @throws MalformedIriException
     */
    private String coalesce(String tagName, Attributes attrs, EvalContext parent,
                            EvalContext current, String... attrNames) throws MalformedIriException {
        for (String attr : attrNames) {
            if (attrs.getValue(attr) != null) {
                if (attr.equals(RDFa.ABOUT_ATTR) || attr.equals(RDFa.RESOURCE_ATTR)) {
                    String val = attrs.getValue(attr);
                    if (val.equals("[]")) {
                        continue;
                    }
                    try {
                        return current.resolveAboutOrResource(val);
                    } catch (MalformedCurieException e) {
                        warning(RDFa.UNRESOLVED_CURIE, e.getMessage());
                        return null;
                    }
                } else if (attr.equals(RDFa.HREF_ATTR) || attr.equals(RDFa.SRC_ATTR) || attr.equals(DATA_ATTR)) {
                    return dh.resolveIri(attrs.getValue(attr));
                } else if (attr.equals(BNODE_IF_TYPEOF)) {
                    return dh.createBnode(false);
                }
            } else if (attr.equals(PARENT_OBJECT) && parent.object != null) {
                return parent.object;
            } else {
                boolean isHeadOrBody = tagName.equals(HEAD) || tagName.equals(BODY);
                boolean isRoot = contextStack.size() == 1 || attrs.getValue(RDFa.TYPEOF_ATTR) != null && isHeadOrBody;
                if (isHeadOrBody && attr.equals(BASE_IF_HEAD_OR_BODY) || isRoot && attr.equals(BASE_IF_ROOT_NODE)) {
                    return dh.base;
                }
            }
        }
        return null;
    }

    /**
     * Generates [incompleted] triples with predicates from @rel attribute
     * @param attrs node's attributes
     * @param rels list of predicates from @rel attribute
     * @param current current context
     */
    private void processRels(Attributes attrs, List<String> rels, EvalContext current) {
        if (rels != null) {
            boolean inList = dh.rdfaVersion > RDFa.VERSION_10 && attrs.getValue(RDFa.INLIST_ATTR) != null;
            // RDFa Core 1.1 processing sequence steps 9 and 10
            // RDFa Core 1.0 processing sequence steps 7 and 8
            for (String predicate : rels) {
                String iri;
                try {
                    iri = current.resolvePredOrDatatype(predicate);
                } catch (MalformedIriException e) {
                    continue;
                }
                if (inList) {
                    List<String> list = current.getMappingForIri(iri);
                    if (current.object != null) {
                        list.add(current.object);
                    } else {
                        current.incomplTriples.add(list);
                    }
                } else {
                    if (current.object != null) {
                        addNonLiteral(current.subject, iri, current.object);
                    } else {
                        current.incomplTriples.add(iri);
                    }
                }
            }
        }
    }

    /**
     * Generates [incompleted] triples with predicates from @rev attribute
     * @param revs list of predicates from @rev attribute
     * @param current current context
     */
    private void processRevs(List<String> revs, EvalContext current) {
        if (revs != null) {
            for (String predicate : revs) {
                // RDFa Core 1.1 processing sequence steps 9 and 10
                try {
                    String iri = current.resolvePredOrDatatype(predicate);
                    if (current.object != null) {
                        addNonLiteral(current.object, iri, current.subject);
                    } else {
                        current.incomplTriples.add(REVERSED_TRIPLE_FLAG);
                        current.incomplTriples.add(iri);
                    }
                } catch (MalformedIriException e) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Processes @property attribute of specified node
     * @param qName node's QName
     * @param attrs node's attributes
     * @param current current context
     * @param parent parent context
     * @param noRelsAndRevs are on @rel and @rev attributes specified
     */
    private void processPropertyAttr(String qName, Attributes attrs, EvalContext current,
                                     EvalContext parent, boolean noRelsAndRevs) {
        if (attrs.getValue(RDFa.PROPERTY_ATTR) == null) {
            current.parsingLiteral = false;
            return;
        }

        // RDFa Core 1.0 processing sequence step 9
        // RDFa Core 1.1 processing sequence step 11
        parseLiteralObject(qName, attrs, current, parent, noRelsAndRevs);

        // noinspection StringEquality
        current.parsingLiteral = current.objectLitDt == RDF.XML_LITERAL;
        if (current.properties == null) {
            current.objectLitDt = null;
            current.parsingLiteral = false;
        }
    }

    /**
     * Determines literal object for specified node. Can change objectLitDt in current context
     * @param qName node's QName
     * @param attrs node's attributes
     * @param current current context
     * @param parent parent context
     * @param noRelsAndRevs are on @rel and @rev attributes specified
     */
    private void parseLiteralObject(String qName, Attributes attrs, EvalContext current,
                                    EvalContext parent, boolean noRelsAndRevs) {
        String content = parseContent(attrs);
        String langOrDt = parseDatatype(qName, attrs, current);

        if (langOrDt != null && !RDF.XML_LITERAL.equals(langOrDt)) {
            // RDFa Core 1.0 processing sequence step 9, typed literal case
            // RDFa Core 1.1 processing sequence step 11, typed literal case
            if (content != null) {
                langOrDt = resolveLangOrDt(content, langOrDt, current);
            } else {
                current.objectLitDt = langOrDt;
                langOrDt = null;
            }
        } else if (content != null) {
            // RDFa Core 1.0 processing sequence step 9, plain literal case
            // RDFa Core 1.1 processing sequence step 11, plain literal using @content case
            langOrDt = current.lang;
        } else if (langOrDt == null && dh.rdfaVersion > RDFa.VERSION_10) {
            if (attrs.getValue(RDFa.CONTENT_ATTR) == null && attrs.getValue(VALUE_ATTR) == null && noRelsAndRevs) {
                // RDFa Core 1.1 processing sequence step 11, no rel or rev or content case
                try {
                    langOrDt = coalesce(qName, attrs, parent, current,
                            RDFa.RESOURCE_ATTR, DATA_ATTR, RDFa.HREF_ATTR, RDFa.SRC_ATTR);
                } catch (MalformedIriException e) {
                    warning(RDFa.WARNING, e.getMessage());
                    pushContextNoLiteral(current, parent);
                }
            }
            if (langOrDt == null) {
                if (attrs.getValue(RDFa.ABOUT_ATTR) == null && attrs.getValue(RDFa.TYPEOF_ATTR) != null) {
                    // RDFa Core 1.1 processing sequence step 11, @typeof present and @about is not case
                    langOrDt = current.object;
                    if (current.object == null) {
                        // RDFa Core 1.1 processing sequence step 11, last plain literal case
                        current.objectLitDt = PLAIN_LITERAL;
                    }
                } else {
                    // RDFa Core 1.1 processing sequence step 11, last plain literal case
                    current.objectLitDt = PLAIN_LITERAL;
                }
            }
        } else {
            if (langOrDt == null || langOrDt.length() > 0) {
                // RDFa Core 1.0 processing sequence step 9, xml literal case
                // RDFa Core 1.1 processing sequence step 11, xml literal case
                current.objectLitDt = RDF.XML_LITERAL;
            } else {
                // RDFa Core 1.0 processing sequence step 9, plain literal case
                // RDFa Core 1.1 processing sequence step 11, plain literal case
                current.objectLitDt = PLAIN_LITERAL;
            }
            langOrDt = null;
        }
        boolean inList = attrs.getValue(RDFa.INLIST_ATTR) != null;
        processPropertyPredicate(attrs, content, langOrDt, current, inList);
    }

    /**
     * Extracts content for specified node with respect of HTML5 attributes
     * @param attrs node's attributes
     * @return content
     */
    private String parseContent(Attributes attrs) {
        String content = attrs.getValue(RDFa.CONTENT_ATTR);
        if (content == null && dh.documentFormat == DocumentContext.FORMAT_HTML5) {
            if (attrs.getValue(VALUE_ATTR) != null) {
                content = attrs.getValue(VALUE_ATTR);
            }
            if (attrs.getValue(DATETIME_ATTR) != null) {
                content = attrs.getValue(DATETIME_ATTR);
            }
        }
        return content;
    }

    /**
     * Extracts datatype uri for specified node
     * @param qName node's QName
     * @param attrs node's attributes
     * @param current current context
     * @return datatype URI or {@link #AUTODETECT_DATE_DATATYPE} if datatype should be detected at validation phase
     */
    private String parseDatatype(String qName, Attributes attrs, EvalContext current) {
        String datatype = attrs.getValue(RDFa.DATATYPE_ATTR);
        if (dh.documentFormat == DocumentContext.FORMAT_HTML5) {
            if (attrs.getValue(DATETIME_ATTR) != null) {
                if (datatype == null) {
                    datatype = AUTODETECT_DATE_DATATYPE;
                }
            } else if (qName.equals(TIME_QNAME) && datatype == null) {
                datatype = AUTODETECT_DATE_DATATYPE;
            }
        }
        try {
            if (datatype != null && datatype.length() > 0) {
                datatype = current.resolvePredOrDatatype(datatype);
            }
        } catch (MalformedIriException e) {
            datatype = null;
        }
        return datatype;
    }

    /**
     * Generates triples corresponding to specified object and predicates from @property attribute
     * @param attrs node's attributes
     * @param content objects's content
     * @param langOrUri object's content lang or datatype (if literal) or object's URI
     * @param current current context
     * @param inList is inlist property presented
     */
    private void processPropertyPredicate(Attributes attrs, String content, String langOrUri,
                                          EvalContext current, boolean inList) {
        Iterator<String> iterator = splitter.split(attrs.getValue(RDFa.PROPERTY_ATTR));
        while (splitter.hasNext()) {
            String iri;
            try {
                iri = current.resolvePredOrDatatype(iterator.next());
            } catch (MalformedIriException e) {
                continue;
            }
            if (content != null || langOrUri != null) {
                if (dh.rdfaVersion > RDFa.VERSION_10 && inList) {
                    List<String> list = current.getMappingForIri(iri);
                    if (content != null) {
                        list.add(LITERAL_OBJECT_FLAG);
                        list.add(content);
                        list.add(langOrUri);
                    } else {
                        list.add(langOrUri);
                    }
                } else {
                    if (content != null) {
                        addLiteralTriple(current.subject, iri, content, langOrUri);
                    } else {
                        addNonLiteral(current.subject, iri, langOrUri);
                    }
                }
            } else if (current.properties == null) {
                current.properties = new ArrayList<String>();
                if (dh.rdfaVersion > RDFa.VERSION_10 && inList) {
                    current.properties.add(RDFa.INLIST_ATTR);
                }
                current.properties.add(iri);
            } else {
                current.properties.add(iri);
            }
        }
    }

    private String resolveLangOrDt(String content, String dt, EvalContext current) {
        if (dt == null) {
            return current.lang;
        }
        if (dt.equals(RdfaParser.AUTODETECT_DATE_DATATYPE)) {
            try {
                if (content.matches("-?P\\d+Y\\d+M\\d+DT\\d+H\\d+M\\d+(\\.\\d+)?S")) {
                    return XSD.DURATION;
                }
                if (content.indexOf(':') != -1) {
                    if (content.indexOf('T') != -1) {
                        DatatypeConverter.parseDateTime(content);
                        return XSD.DATE_TIME;
                    }
                    DatatypeConverter.parseTime(content);
                    return XSD.TIME;
                }
                if (content.matches("-?\\d{4,}")) {
                    return XSD.G_YEAR;
                }
                if (content.matches("-?\\d{4,}-(0[1-9]|1[0-2])")) {
                    return XSD.G_YEAR_MONTH;
                }
                DatatypeConverter.parseDate(content);
                return XSD.DATE;
            } catch (IllegalArgumentException e) {
                return current.lang;
            }
        }
        if (dt.indexOf(':') == -1) {
            return current.lang;
        }
        return dt;
    }

    /**
     * Generates triples from parent's incompleted triples list
     * @param current current context
     * @param parent parent context
     */
    private void processIncompleteTriples(EvalContext current, EvalContext parent) {
        if (current.subject == null) {
            return;
        }
        String subject = parent.subject;
        for (Iterator iti = parent.incomplTriples.iterator(); iti.hasNext(); ) {
            Object predicateOrList = iti.next();
            if (predicateOrList == REVERSED_TRIPLE_FLAG) {
                addNonLiteral(current.subject, (String) iti.next(), subject);
            } else if (predicateOrList instanceof String) {
                addNonLiteral(subject, (String) predicateOrList, current.subject);
            } else {
                @SuppressWarnings("unchecked")
                Collection<String> list = (Collection<String>) predicateOrList;
                list.add(current.subject);
            }
        }
    }

    /**
     * Pushes current context to stack before processing child nodes
     * @param current current context
     * @param parent parent context
     */
    private void pushContext(EvalContext current, EvalContext parent, boolean skipElement) {
        if (current.parsingLiteral) {
            xmlString = new StringBuilder();
            xmlStringPred = current.properties;
            xmlStringSubj = current.subject == null ? parent.subject : current.subject;
        }
        if (current.parsingLiteral || skipElement) {
            current.subject = parent.subject;
            current.object = parent.object;
            current.incomplTriples = parent.incomplTriples;
            current.objectLit = null;
            current.objectLitDt = parent.objectLitDt;
            if (current.objectLitDt != null) {
                current.objectLit = "";
            }
            current.properties = null;
            contextStack.push(current);
        } else {
            pushContextNoLiteral(current, parent);
        }
    }

    /**
     * Pushes current context to stack before processing child nodes when no literals are parsed
     * @param current current context
     * @param parent parent context
     */
    private void pushContextNoLiteral(EvalContext current, EvalContext parent) {
        if (current.subject == null) {
            current.subject = parent.subject;
        }
        if (current.object == null) {
            current.object = current.subject;
        }
        if (current.objectLitDt != null || parent.objectLitDt != null) {
            current.objectLit = "";
        }
        contextStack.push(current);
    }

    @Override
    public void endElement(String nsUri, String localName, String qName) throws SAXException {
        if (rdfXmlInline) {
            // delegate parsing to RDF/XML parser
            if (dh.documentFormat == DocumentContext.FORMAT_SVG && localName.equals(METADATA)) {
                rdfXmlParser.endDocument();
                rdfXmlParser = null;
                rdfXmlInline = false;
            } else {
                rdfXmlParser.endElement(nsUri, localName, qName);
            }
            return;
        }

        EvalContext current = contextStack.pop();
        processXmlString(current);

        // serialize close tag if parsing literal
        if (xmlString != null) {
            xmlString.append("</").append(qName).append('>');
        }

        if (contextStack.isEmpty()) {
            return;
        }

        EvalContext parent = contextStack.peek();
        processContent(current, parent);

        // noinspection ObjectEquality
        if (parent.listMapping != current.listMapping) {
            // current mapping isn't inherited from parent
            // RDFa Core 1.0 processing sequence step 14
            processListMappings(current);
        }
    }

    /**
     * Generates triples for parsed literal if it present
     * @param current current context
     */
    private void processXmlString(EvalContext current) {
        if (current.parsingLiteral && xmlString != null) {
            String content = xmlString.toString();
            xmlString = null;
            if (dh.rdfaVersion == RDFa.VERSION_10 && content.indexOf('<') == -1) {
                for (String pred : xmlStringPred) {
                    addPlainLiteral(xmlStringSubj, pred, content, current.lang);
                }
            } else {
                for (String pred : xmlStringPred) {
                    addTypedLiteral(xmlStringSubj, pred, content, RDF.XML_LITERAL);
                }
            }
        }
    }

    /**
     * Generates triples for node content
     * @param current current context
     * @param parent parent context
     */
    private void processContent(EvalContext current, EvalContext parent) {
        String content = current.objectLit;
        if (content == null) {
            return;
        }
        if (!parent.parsingLiteral && parent.objectLit != null) {
            parent.objectLit += content;
        }
        if (current.properties == null) {
            return;
        }

        String dt = current.objectLitDt;
        boolean inlist = RDFa.INLIST_ATTR.equals(current.properties.get(0));

        if (inlist) {
            String langOrDt = resolveLangOrDt(content, dt, current);
            current.properties.remove(0);
            for (String predIri : current.properties) {
                List<String> mappingForIri = current.getMappingForIri(predIri);
                mappingForIri.add(LITERAL_OBJECT_FLAG);
                mappingForIri.add(content);
                mappingForIri.add(langOrDt);
            }
        } else {
            for (String predIri : current.properties) {
                dt = resolveLangOrDt(content, dt, current);
                addLiteralTriple(current.subject, predIri, content, dt);
            }
        }
    }

    /**
     * Generates triples from list mappings on node close event
     * @param current current context
     */
    private void processListMappings(EvalContext current) {
        Map<String, List<String>> list = current.listMapping;
        for (String pred : list.keySet()) {
            String prev = null;
            String start = null;
            for (Iterator<String> iterator = list.get(pred).iterator(); iterator.hasNext(); ) {
                String res = iterator.next();
                String child = dh.createBnode(false);
                // noinspection StringEquality
                if (res == LITERAL_OBJECT_FLAG) {
                    String content = iterator.next();
                    String langOrDt = iterator.next();
                    addLiteralTriple(child, RDF.FIRST, content, langOrDt);
                } else {
                    addNonLiteral(child, RDF.FIRST, res);
                }
                if (prev == null) {
                    start = child;
                } else {
                    addNonLiteral(prev, RDF.REST, child);
                }
                prev = child;
            }
            if (start == null) {
                addNonLiteral(current.subject, pred, RDF.NIL);
            } else {
                addNonLiteral(prev, RDF.REST, RDF.NIL);
                addNonLiteral(current.subject, pred, start);
            }
        }
        list.clear();
    }

    @Override
    public void characters(char[] buffer, int start, int length) throws SAXException {
        if (rdfXmlInline) {
            rdfXmlParser.characters(buffer, start, length);
            return;
        }
        EvalContext parent = contextStack.peek();
        if (xmlString != null) {
            xmlString.append(buffer, start, length);
        }
        if (parent.objectLit != null) {
            parent.addContent(String.copyValueOf(buffer, start, length));
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (rdfXmlInline) {
            rdfXmlParser.ignorableWhitespace(ch, start, length);
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        dh.processDtd(name, publicId, systemId);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (rdfXmlInline) {
            rdfXmlParser.startPrefixMapping(prefix, uri);
            return;
        }
        // TODO: check for valid prefix
        if (prefix.length() == 0 && XHTML_DEFAULT_XMLNS.equalsIgnoreCase(uri)) {
            overwriteMappings.put(prefix, XHTML_VOCAB);
        } else {
            try {
                overwriteMappings.put(prefix, RIUtils.resolveIri(dh.originUri, uri));
            } catch (MalformedIriException e) {
                // do nothing
            }
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if (rdfXmlInline) {
            rdfXmlParser.endPrefixMapping(prefix);
        }
    }

    @Override
    public boolean setPropertyInternal(String key, Object value) {
        if (ENABLE_OUTPUT_GRAPH.equals(key) && value instanceof Boolean) {
            sinkOutputGraph = (Boolean) value;
        } else if (getRdfaVersion() != RDFa.VERSION_10 && ENABLE_PROCESSOR_GRAPH.equals(key)
                && value instanceof Boolean) {
            sinkProcessorGraph = (Boolean) value;
            forcedRdfaVersion = RDFa.VERSION_11;
        } else if (getRdfaVersion() != RDFa.VERSION_10 && ENABLE_VOCAB_EXPANSION.equals(key)
                && value instanceof Boolean) {
            expandVocab = (Boolean) value;
            forcedRdfaVersion = RDFa.VERSION_11;
//        } else if (sinkProcessorGraph || expandVocab) {
//            forcedRdfaVersion = RDFa.VERSION_11;
        } else if (RDFA_VERSION_PROPERTY.equals(key) && value instanceof Short) {
            short rdfaVersion = (Short) value;
            if (rdfaVersion < RDFa.VERSION_10 || rdfaVersion > RDFa.VERSION_11) {
                throw new IllegalArgumentException("Unsupported RDFa version");
            }
            forcedRdfaVersion = rdfaVersion;
            dh.rdfaVersion = forcedRdfaVersion;
            if (rdfaVersion < RDFa.VERSION_11) {
                sinkProcessorGraph = false;
                expandVocab = false;
            } else {
                sinkProcessorGraph = true;
                expandVocab = true;
            }
        } else if (StreamProcessor.PROCESSOR_GRAPH_HANDLER_PROPERTY.equals(key)
                && value instanceof ProcessorGraphHandler) {
            processorGraphHandler = (ProcessorGraphHandler) value;
            return false;
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void setBaseUri(String baseUri) {
        dh.setBaseUri(baseUri);
    }

    /**
     * Loads vocabulary from specified URL. Vocabulary will not contain terms in case when
     * vocabulary expansion is disabled.
     *
     * @param vocabUrl URL to load from
     * @return loaded vocabulary (can be cached)
     */
    Vocabulary loadVocabulary(String vocabUrl) {
        if (sinkOutputGraph) {
            sink.addNonLiteral(dh.base, RDFa.USES_VOCABULARY, vocabUrl);
        }
        return VOCAB_MANAGER.get().findVocab(vocabUrl, expandVocab);
    }

    // error handling

    @Override
    public void info(String infoClass, String message) {
        addProcessorGraphRecord(infoClass, message);
        if (processorGraphHandler != null) {
            processorGraphHandler.info(infoClass, message);
        }
    }

    @Override
    public void warning(String warningClass, String message) {
        addProcessorGraphRecord(warningClass, message);
        if (processorGraphHandler != null) {
            processorGraphHandler.warning(warningClass, message);
        }
    }

    @Override
    public void error(String errorClass, String message) {
        addProcessorGraphRecord(errorClass, message);
        if (processorGraphHandler != null) {
            processorGraphHandler.error(errorClass, message);
        }
    }

    private void addProcessorGraphRecord(String recordClass, String recordContext) {
        if (dh.rdfaVersion > RDFa.VERSION_10 && sinkProcessorGraph) {
            String errorNode = dh.createBnode(true);
            String location = "";
            if (locator != null) {
                location = " at " + locator.getLineNumber() + ':' + locator.getColumnNumber();
            }
            sink.addNonLiteral(errorNode, RDF.TYPE, recordClass);
            sink.addPlainLiteral(errorNode, RDFa.CONTEXT, recordContext + location, null);
        }
    }

    @Override
    public ParseException processException(SAXException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ParseException) {
            error(RDFa.ERROR, cause.getMessage());
            return (ParseException) cause;
        }
        error(RDFa.ERROR, e.getMessage());
        return new ParseException(e);
    }

    private void copyProps(String subj, List<String> props) {
        Iterator<String> iterator = props.iterator();
        while (iterator.hasNext()) {
            String type = iterator.next();
            if (type == null) {
                addNonLiteralInternal(subj, iterator.next(), iterator.next());
            } else if (type.isEmpty()) {
                addPlainLiteralInternal(subj, iterator.next(), iterator.next(), iterator.next());
            } else {
                addTypedLiteralInternal(subj, iterator.next(), iterator.next(), type);
            }
        }
    }

    // proxying TripleSink calls to filter output graph

    private void addLiteralTriple(String subject, String pred, String content, String langOrDt) {
        if (langOrDt == null || langOrDt.length() < 6 || langOrDt.indexOf(':') == -1) {
            addPlainLiteral(subject, pred, content, langOrDt);
        } else {
            addTypedLiteral(subject, pred, content, langOrDt);
        }
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj) {
        if (!sinkOutputGraph) {
            return;
        }
        if (obj.equals(RDFa.PATTERN)) {
            if (!patternProps.containsKey(subj)) {
                patternProps.put(subj, new ArrayList<String>());
            }
            return;
            // TODO: check vocab expansion
        } else if (pred.equals(RDFa.COPY)) {
            if (patternProps.containsKey(obj)) {
                copyProps(subj, patternProps.get(obj));
            } else {
                copyingPairs.add(subj);
                copyingPairs.add(obj);
            }
            return;
        } else if (patternProps.containsKey(subj)) {
            List<String> props = patternProps.get(subj);
            props.add(null);
            props.add(pred);
            props.add(obj);
            return;
        }
        addNonLiteralInternal(subj, pred, obj);
    }

    private void addNonLiteralInternal(String subj, String pred, String obj) {
        if (!expandVocab) {
            sink.addNonLiteral(subj, pred, obj);
            return;
        }
        addNonLiteralWithObjExpansion(subj, pred, obj);
        for (String predSynonym : contextStack.peek().expand(pred)) {
            addNonLiteralWithObjExpansion(subj, predSynonym, obj);
        }
    }

    private void addNonLiteralWithObjExpansion(String subj, String pred, String obj) {
        if (obj.startsWith(RDF.BNODE_PREFIX)) {
            sink.addNonLiteral(subj, pred, obj);
            return;
        }
        sink.addNonLiteral(subj, pred, obj);
        for (String objSynonym : contextStack.peek().expand(obj)) {
            sink.addNonLiteral(subj, pred, objSynonym);
        }
    }

    @Override
    public void addPlainLiteral(String subj, String pred, String content, String lang) {
        if (!sinkOutputGraph) {
            return;
        }
        if (patternProps.containsKey(subj)) {
            List<String> props = patternProps.get(subj);
            props.add("");
            props.add(pred);
            props.add(content);
            props.add(lang);
            return;
        }
        addPlainLiteralInternal(subj, pred, content, lang);
    }

    private void addPlainLiteralInternal(String subj, String pred, String content, String lang) {
        sink.addPlainLiteral(subj, pred, content, lang);
        for (String predSynonym : contextStack.peek().expand(pred)) {
            sink.addPlainLiteral(subj, predSynonym, content, lang);
        }
    }

    @Override
    public void addTypedLiteral(String subj, String pred, String content, String type) {
        if (!sinkOutputGraph) {
            return;
        }
        if (patternProps.containsKey(subj)) {
            List<String> props = patternProps.get(subj);
            props.add(type);
            props.add(pred);
            props.add(content);
            return;
        }
        addTypedLiteralInternal(subj, pred, content, type);
    }

    private void addTypedLiteralInternal(String subj, String pred, String content, String type) {
        sink.addTypedLiteral(subj, pred, content, type);
        for (String predSynonym : contextStack.peek().expand(pred)) {
            sink.addTypedLiteral(subj, predSynonym, content, type);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    // ignored events

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void startEntity(String s) throws SAXException {
    }

    @Override
    public void endEntity(String s) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void comment(char[] chars, int i, int i1) throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    short getRdfaVersion() {
        if (forcedRdfaVersion == null) {
            return RDFa.VERSION_11;
        }
        return forcedRdfaVersion;
    }

    private static final class Splitter implements Iterator<String> {
        private int pos = -1;
        private int length = -1;
        private String string = null;

        private Iterator<String> split(String string) {
            this.string = string;
            length = string.length();
            pos = 0;
            while (pos < length && XmlUtils.WHITESPACE.get(string.charAt(pos))) {
                pos++;
            }
            return this;
        }

        @Override
        public boolean hasNext() {
            return pos < length;
        }

        @Override
        public String next() {
            int start = pos;
            while (pos < length && !XmlUtils.WHITESPACE.get(string.charAt(pos))) {
                pos++;
            }
            if (start == pos) {
                throw new NoSuchElementException();
            }
            String result = string.substring(start, pos);
            while (pos < length && XmlUtils.WHITESPACE.get(string.charAt(pos))) {
                pos++;
            }
            if (pos == length) {
                string = null;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
