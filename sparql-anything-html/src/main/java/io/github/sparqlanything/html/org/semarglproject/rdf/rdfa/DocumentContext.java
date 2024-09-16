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

import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.RdfaParser;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.Vocabulary;
import io.github.sparqlanything.html.org.semarglproject.ri.RIUtils;
import io.github.sparqlanything.html.org.semarglproject.ri.MalformedIriException;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDF;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDFa;

import java.util.HashMap;
import java.util.Map;

final class DocumentContext {

    static final short FORMAT_UNKNOWN = 0;
    static final short FORMAT_HTML4 = 1;
    static final short FORMAT_HTML5 = 2;
    static final short FORMAT_XML = 3;
    static final short FORMAT_SVG = 4;

    private static final String RDFA_10_STRING = "rdfa 1.0";

    private static final String HTML_ROOT_ELEMENT = "html";
    private static final String HTML_BASE = "base";
    private static final String SVG_ROOT_ELEMENT = "svg";

    short documentFormat;
    short rdfaVersion;

    final io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.RdfaParser parser;

    String base;
    String originUri;

    private Map<String, String> bnodeMapping = new HashMap<String, String>();
    private int nextBnodeId;

    DocumentContext(RdfaParser parser) {
        this.parser = parser;
        nextBnodeId = 0;
        clear();
    }

    String resolveBNode(String value) {
        if (value.startsWith(RDF.BNODE_PREFIX) || value.startsWith('[' + RDF.BNODE_PREFIX)
                && value.charAt(value.length() - 1) == ']') {
            String name;
            if (value.charAt(0) == '[') {
                name = value.substring(RDF.BNODE_PREFIX.length() + 1, value.length() - 1);
            } else {
                name = value.substring(RDF.BNODE_PREFIX.length());
            }
            if (!bnodeMapping.containsKey(name)) {
                bnodeMapping.put(name, createBnode(false));
            }
            return bnodeMapping.get(name);
        }
        return null;
    }

    void detectFormat(String localName, String qName, String version) {
        if (documentFormat == FORMAT_UNKNOWN) {
            if (localName.equals(SVG_ROOT_ELEMENT)) {
                documentFormat = FORMAT_SVG;
            } else if (localName.equalsIgnoreCase(HTML_ROOT_ELEMENT)) {
                documentFormat = FORMAT_HTML4;
            } else {
                documentFormat = FORMAT_XML;
            }
        }
        if (qName.equalsIgnoreCase(HTML_ROOT_ELEMENT) && version != null
                && version.toLowerCase().contains(RDFA_10_STRING)) {
            rdfaVersion = RDFa.VERSION_10;
        }
    }

    void detectBase(String qName, String xmlBase, String hRef) {
        boolean xmlBaseF = (documentFormat == FORMAT_XML || documentFormat == FORMAT_SVG) && xmlBase != null;
        if (xmlBaseF || qName.equalsIgnoreCase(HTML_BASE) && hRef != null) {
            base = (xmlBaseF ? xmlBase : hRef).replaceAll("#.*", "");
        }
    }

    String createBnode(boolean shortenable) {
        if (shortenable) {
            return RDF.BNODE_PREFIX + 'n' + (nextBnodeId++) + RDF.SHORTENABLE_BNODE_SUFFIX;
        }
        return RDF.BNODE_PREFIX + 'n' + nextBnodeId++;
    }

    void processDtd(String name, String publicId, String systemId) {
        if (publicId == null) {
            if (HTML_ROOT_ELEMENT.equalsIgnoreCase(name)) {
                documentFormat = FORMAT_HTML5;
            }
        } else {
            String publicIdLower = publicId.toLowerCase();
            if (publicIdLower.contains(HTML_ROOT_ELEMENT)) {
                documentFormat = FORMAT_HTML4;
            }
            if (publicIdLower.contains(RDFA_10_STRING)) {
                rdfaVersion = RDFa.VERSION_10;
            }
        }
    }

    String resolveIri(String iri) throws MalformedIriException {
        return RIUtils.resolveIri(base, iri);
    }

    void clear() {
        rdfaVersion = parser.getRdfaVersion();
        documentFormat = FORMAT_UNKNOWN;
        bnodeMapping = new HashMap<String, String>();
        base = null;
        originUri = null;
    }

    io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.Vocabulary loadVocabulary(String vocabUrl) {
        return parser.loadVocabulary(vocabUrl);
    }

    void setBaseUri(String baseUri) {
        if (base == null) {
            originUri = baseUri;
        }
        this.base = baseUri;
    }
}
