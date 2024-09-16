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

import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.DocumentContext;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.RdfaParser;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.Vocabulary;
import io.github.sparqlanything.html.org.semarglproject.ri.RIUtils;
import io.github.sparqlanything.html.org.semarglproject.ri.MalformedCurieException;
import io.github.sparqlanything.html.org.semarglproject.ri.MalformedIriException;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDFa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

final class EvalContext {

    // Initial context described in http://www.w3.org/2011/rdfa-context/rdfa-1.1.html
    private static final Map<String, String> RDFA11_INITIAL_CONTEXT = new HashMap<String, String>();
    private static final Pattern TERM_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+", Pattern.DOTALL);

    private static final String CAN_NOT_RESOLVE_TERM = "Can't resolve term ";

    private static final String XHTML_VOCAB = "http://www.w3.org/1999/xhtml/vocab#";
    private static final String POWDER_DESCRIBED_BY = "http://www.w3.org/2007/05/powder-s#describedby";

    private static final String[] XHTML_VOCAB_PROPS = {
        // XHTML Metainformation Vocabulary
        "alternate", "appendix", "bookmark", "cite", "chapter", "contents",
        "copyright", "first", "glossary", "help", "icon", "index", "itsRules",
        "last", "license", "meta", "next", "p3pv1", "prev", "previous", "role",
        "section", "stylesheet", "subsection", "start","top", "up",

        // Items from the XHTML Role Module
        "banner", "complementary", "contentinfo", "definition", "main",
        "navigation", "note", "search",

        // Items from the Accessible Rich Internet Applications Vocabulary
        "alert", "alertdialog", "application", "article", "button", "checkbox",
        "columnheader", "combobox", "dialog", "directory", "document", "form",
        "grid", "gridcell", "group", "heading", "img", "link", "list", "listbox",
        "listitem", "log", "marquee", "math", "menu", "menubar", "menuitem",
        "menuitemcheckbox", "menuitemradio", "option", "presentation",
        "progressbar", "radio", "radiogroup", "region", "row", "rowgroup",
        "rowheader", "scrollbar", "separator", "slider", "spinbutton", "status",
        "tab", "tablist", "tabpanel", "textbox", "timer", "toolbar", "tooltip",
        "tree", "treegrid", "treeitem"
    };

    static {
        // Vocabulary Prefixes of W3C Documents
        RDFA11_INITIAL_CONTEXT.put("owl", "http://www.w3.org/2002/07/owl#");
        RDFA11_INITIAL_CONTEXT.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        RDFA11_INITIAL_CONTEXT.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        RDFA11_INITIAL_CONTEXT.put("rdfa", "http://www.w3.org/ns/rdfa#");
        RDFA11_INITIAL_CONTEXT.put("xhv", "http://www.w3.org/1999/xhtml/vocab#");
        RDFA11_INITIAL_CONTEXT.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        RDFA11_INITIAL_CONTEXT.put("grddl", "http://www.w3.org/2003/g/data-view#");
        RDFA11_INITIAL_CONTEXT.put("ma", "http://www.w3.org/ns/ma-ont#");
        RDFA11_INITIAL_CONTEXT.put("rif", "http://www.w3.org/2007/rif#");
        RDFA11_INITIAL_CONTEXT.put("skos", "http://www.w3.org/2004/02/skos/core#");
        RDFA11_INITIAL_CONTEXT.put("skosxl", "http://www.w3.org/2008/05/skos-xl#");
        RDFA11_INITIAL_CONTEXT.put("wdr", "http://www.w3.org/2007/05/powder#");
        RDFA11_INITIAL_CONTEXT.put("void", "http://rdfs.org/ns/void#");
        RDFA11_INITIAL_CONTEXT.put("wdrs", "http://www.w3.org/2007/05/powder-s#");
        RDFA11_INITIAL_CONTEXT.put("xml", "http://www.w3.org/XML/1998/namespace");

        // Widely used Vocabulary prefixes
        RDFA11_INITIAL_CONTEXT.put("cc", "http://creativecommons.org/ns#");
        RDFA11_INITIAL_CONTEXT.put("ctag", "http://commontag.org/ns#");
        RDFA11_INITIAL_CONTEXT.put("dc", "http://purl.org/dc/terms/");
        RDFA11_INITIAL_CONTEXT.put("dcterms", "http://purl.org/dc/terms/");
        RDFA11_INITIAL_CONTEXT.put("foaf", "http://xmlns.com/foaf/0.1/");
        RDFA11_INITIAL_CONTEXT.put("gr", "http://purl.org/goodrelations/v1#");
        RDFA11_INITIAL_CONTEXT.put("ical", "http://www.w3.org/2002/12/cal/icaltzd#");
        RDFA11_INITIAL_CONTEXT.put("og", "http://ogp.me/ns#");
        RDFA11_INITIAL_CONTEXT.put("rev", "http://purl.org/stuff/rev#");
        RDFA11_INITIAL_CONTEXT.put("sioc", "http://rdfs.org/sioc/ns#");
        RDFA11_INITIAL_CONTEXT.put("v", "http://rdf.data-vocabulary.org/#");
        RDFA11_INITIAL_CONTEXT.put("vcard", "http://www.w3.org/2006/vcard/ns#");
        RDFA11_INITIAL_CONTEXT.put("schema", "http://schema.org/");
    }

    Map<String, String> iriMappings;
    String subject;
    String object;
    List<Object> incomplTriples;
    String lang;
    String objectLit;
    String objectLitDt;
    List<String> properties;
    boolean parsingLiteral;
    Map<String, List<String>> listMapping;

    private final DocumentContext documentContext;
    private io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.Vocabulary vocab;
    private String profile;

    private EvalContext(String lang, io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.Vocabulary vocab, String profile, DocumentContext documentContext) {
        this.subject = null;
        this.object = null;
        this.iriMappings = null;
        this.incomplTriples = new ArrayList<Object>();
        this.lang = lang;
        this.objectLit = null;
        this.objectLitDt = null;
        this.vocab = vocab;
        this.profile = profile;
        this.properties = null;
        this.parsingLiteral = false;
        this.listMapping = null;
        this.documentContext = documentContext;
    }

    static EvalContext createInitialContext(DocumentContext documentContext) {
        // RDFa Core 1.0 processing sequence step 1
        EvalContext initialContext = new EvalContext(null, null, null, documentContext);
        initialContext.subject = documentContext.base;
        initialContext.listMapping = new HashMap<String, List<String>>();
        initialContext.iriMappings = new TreeMap<String, String>();
        return initialContext;
    }

    EvalContext initChildContext(String profile, String vocab, String lang,
                                        Map<String, String> overwriteMappings) {
        // RDFa Core 1.0 processing sequence step 2
        EvalContext current = new EvalContext(this.lang, this.vocab, this.profile, documentContext);
        current.listMapping = this.listMapping;
        current.initPrefixMappings(iriMappings, overwriteMappings);

        if (documentContext.rdfaVersion > RDFa.VERSION_10) {
            if (profile != null) {
                String newProfile = profile + "#";
                if (current.profile == null) {
                    current.profile = newProfile;
                } else {
                    current.profile = newProfile + ' ' + current.profile;
                }
            }
            if (vocab != null) {
                if (vocab.length() == 0) {
                    current.vocab = null;
                } else {
                    current.vocab = documentContext.loadVocabulary(vocab);
                }
            }
        }

        // RDFa Core 1.0 processing sequence step 3
        if (lang != null) {
            current.lang = lang;
        }
        if (current.lang != null && current.lang.isEmpty()) {
            current.lang = null;
        }
        return current;
    }

    private void initPrefixMappings(Map<String, String> parentMappings, Map<String, String> overwriteMappings) {
        if (overwriteMappings.isEmpty()) {
            iriMappings = parentMappings;
        } else {
            iriMappings = new TreeMap<String, String>(parentMappings);
            iriMappings.putAll(overwriteMappings);
        }

        if (documentContext.rdfaVersion > RDFa.VERSION_10) {
            for (String prefix : overwriteMappings.keySet()) {
                String standardMapping = RDFA11_INITIAL_CONTEXT.get(prefix);
                String newMapping = overwriteMappings.get(prefix);
                if (standardMapping != null && !standardMapping.equals(newMapping)) {
                    documentContext.parser.warning(RDFa.PREFIX_REDEFINITION, "Standard prefix "
                            + prefix + ": redefined to <" + newMapping + '>');
                }
            }
        }
    }

    List<String> getMappingForIri(String iri) {
        if (!listMapping.containsKey(iri)) {
            listMapping.put(iri, new ArrayList<String>());
        }
        return listMapping.get(iri);
    }

    void addContent(String content) {
        objectLit += content;
    }

    void updateBase(String oldBase, String base) {
        if (object != null && object.equals(oldBase)) {
            object = base;
        }
        if (subject != null && subject.equals(oldBase)) {
            subject = base;
        }
    }

    /**
     * Resolves @predicate or @datatype according to RDFa Core 1.1 section 5
     *
     * @param value value of attribute
     * @return resource IRI
     * @throws MalformedIriException if IRI can not be resolved
     */
    String resolvePredOrDatatype(String value) throws MalformedIriException {
        if (value == null || value.isEmpty()) {
            throw new MalformedIriException("Empty predicate or datatype found");
        }
        if (value == io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.RdfaParser.AUTODETECT_DATE_DATATYPE) {
            return RdfaParser.AUTODETECT_DATE_DATATYPE;
        }
        return resolveTermOrCurieOrAbsIri(value);
    }

    /**
     * Resolves @about or @resource according to RDFa Core 1.1 section 5
     *
     * @param value value of attribute
     * @return resource IRI
     * @throws MalformedIriException if IRI can not be resolved
     */
    String resolveAboutOrResource(String value) throws MalformedIriException {
        String result = documentContext.resolveBNode(value);
        if (result != null) {
            return result;
        }
        return resolveCurieOrIri(value, false);
    }

    /**
     * Resolves @role according to Role Attribute 1.0 section 4
     * @param value value of attribute
     * @return role IRI
     * @throws MalformedIriException if role can not be resolved
     */
    String resolveRole(String value) throws MalformedIriException {
        if (TERM_PATTERN.matcher(value).matches()) {
            return XHTML_VOCAB + value;
        }
        return resolveCurieOrIri(value, true);
    }

    /**
     * Resolves TERMorCURIEorAbsIRI according to RDFa Core 1.1 section A
     * @param value value to be resolved
     * @return resource IRI
     * @throws MalformedIriException if IRI can not be resolved
     */
    private String resolveTermOrCurieOrAbsIri(String value) throws MalformedIriException {
        if (TERM_PATTERN.matcher(value).matches()) {
            if (vocab == null && documentContext.rdfaVersion > RDFa.VERSION_10 && "describedby".equals(value)) {
                return POWDER_DESCRIBED_BY;
            }
            String term;
            if (vocab != null) {
                term = vocab.resolveTerm(value);
            } else {
                term = resolveXhtmlTerm(value);
            }
            if (term == null) {
                documentContext.parser.warning(RDFa.UNRESOLVED_TERM, CAN_NOT_RESOLVE_TERM + value);
                throw new MalformedIriException(CAN_NOT_RESOLVE_TERM + value);
            }
            return term;
        }
        return resolveCurieOrIri(value, true);
    }

    Iterable<String> expand(String pred) {
        if (vocab == null) {
            return Collections.EMPTY_LIST;
        }
        return vocab.expand(pred);
    }

    private String resolveCurieOrIri(String curie, boolean ignoreRelIri) throws MalformedIriException {
        if (!ignoreRelIri && (curie == null || curie.isEmpty())) {
            return documentContext.resolveIri(curie);
        }
        boolean safeSyntax = curie.startsWith("[") && curie.endsWith("]");
        if (safeSyntax) {
            curie = curie.substring(1, curie.length() - 1);
        }

        int delimPos = curie.indexOf(':');
        if (delimPos == -1) {
            if (safeSyntax || ignoreRelIri) {
                throw new MalformedCurieException("CURIE with no prefix (" + curie + ") found");
            }
            return documentContext.resolveIri(curie);
        }

        String result = resolveMapping(curie, delimPos, safeSyntax);
        if (RIUtils.isIri(result)) {
            return result;
        }
        throw new MalformedIriException("Malformed IRI: " + curie);
    }

    private String resolveMapping(String curie, int delimPos, boolean safeSyntax) throws MalformedCurieException {
        String localName = curie.substring(delimPos + 1);
        String prefix = curie.substring(0, delimPos);

        if (prefix.equals("_")) {
            throw new MalformedCurieException("CURIE with invalid prefix (" + curie + ") found");
        }

        if (!iriMappings.containsKey(prefix)) {
            if (documentContext.rdfaVersion > RDFa.VERSION_10 && RDFA11_INITIAL_CONTEXT.containsKey(prefix)) {
                String nsUri = RDFA11_INITIAL_CONTEXT.get(prefix);
                iriMappings.put(prefix, nsUri);
                String result = nsUri + localName;
                if (RIUtils.isIri(result)) {
                    return result;
                }
                throw new MalformedCurieException("Malformed CURIE (" + curie + ")");
            }
            if (!safeSyntax && RIUtils.isIri(curie)) {
                return curie;
            }
            throw new MalformedCurieException("CURIE with unresolvable prefix found (" + curie + ")");
        }
        return iriMappings.get(prefix) + localName;
    }

    private static String resolveXhtmlTerm(String predicate) {
        for (String link : XHTML_VOCAB_PROPS) {
            if (link.equalsIgnoreCase(predicate)) {
                return XHTML_VOCAB + link;
            }
        }
        return null;
    }

}
