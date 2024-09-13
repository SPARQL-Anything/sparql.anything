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

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionParameters;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.JSONLDExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.JSONLDExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import io.github.sparqlanything.html.org.apache.any23.vocab.SINDICE;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This extractor represents the HTML script tags used to embed blocks of data in documents. This way, JSON-LD content
 * can be easily embedded in HTML by placing it in a script element with the type attribute set to application/ld+json
 * according the <a href="http://www.w3.org/TR/json-ld/#embedding-json-ld-in-html-documents" >JSON-LD specification</a>.
 *
 */
public class EmbeddedJSONLDExtractor implements Extractor.TagSoupDOMExtractor {

    private static final SINDICE vSINDICE = SINDICE.getInstance();

    private IRI profile;

    private Map<String, IRI> prefixes = new HashMap<>();

    private String documentLang;

    private JSONLDExtractor extractor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, Document in,
            ExtractionResult out) throws IOException, ExtractionException {
        profile = extractProfile(in);
        documentLang = getDocumentLanguage(in);
        extractLinkDefinedPrefixes(in);

        String baseProfile = vSINDICE.NS;
        if (profile != null) {
            baseProfile = profile.toString();
        }

        extractionContext.getDocumentIRI();
        Set<JSONLDScript> jsonldScripts = extractJSONLDScript(in, baseProfile, extractionParameters, extractionContext,
                out);
        for (JSONLDScript jsonldScript : jsonldScripts) {
            // String lang = documentLang;
            // if (jsonldScript.getLang() != null) {
            // lang = jsonldScript.getLang();
            // }
            // out.writeTriple(documentIRI, jsonldScript.getName(),
            // SimpleValueFactory.getInstance().createLiteral(jsonldScript.getContent(), lang));
        }
    }

    /**
     * Returns the {@link Document} language if declared, <code>null</code> otherwise.
     *
     * @param in
     *            a instance of {@link Document}.
     *
     * @return the language declared, could be <code>null</code>.
     */
    private String getDocumentLanguage(Document in) {
        String lang = DomUtils.find(in, "string(/HTML/@lang)");
        if ("".equals(lang)) {
            return null;
        }
        return lang;
    }

    private IRI extractProfile(Document in) {
        String profile = DomUtils.find(in, "string(/HTML/@profile)");
        if ("".equals(profile)) {
            return null;
        }
        return SimpleValueFactory.getInstance().createIRI(profile);
    }

    /**
     * It extracts prefixes defined in the <i>LINK</i> meta tags.
     *
     * @param in
     */
    private void extractLinkDefinedPrefixes(Document in) {
        List<Node> linkNodes = DomUtils.findAll(in, "/HTML/HEAD/LINK");
        for (Node linkNode : linkNodes) {
            NamedNodeMap attributes = linkNode.getAttributes();
            Node relNode = attributes.getNamedItem("rel");
            String rel = relNode == null ? null : relNode.getTextContent();
            Node hrefNode = attributes.getNamedItem("href");
            String href = hrefNode == null ? null : hrefNode.getTextContent();
            if (rel != null && href != null && RDFUtils.isAbsoluteIRI(href)) {
                prefixes.put(rel, SimpleValueFactory.getInstance().createIRI(href));
            }
        }
    }

    private Set<JSONLDScript> extractJSONLDScript(Document in, String baseProfile,
            ExtractionParameters extractionParameters, ExtractionContext extractionContext, ExtractionResult out)
            throws IOException, ExtractionException {
        List<Node> scriptNodes = DomUtils.findAll(in, "//SCRIPT");
        Set<JSONLDScript> result = new HashSet<>();
        extractor = new JSONLDExtractorFactory().createExtractor();
        for (Node jsonldNode : scriptNodes) {
            NamedNodeMap attributes = jsonldNode.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                if ("application/ld+json".equalsIgnoreCase(attributes.item(i).getTextContent())) {
                    extractor.run(extractionParameters, extractionContext,
                            IOUtils.toInputStream(jsonldNode.getTextContent(), StandardCharsets.UTF_8), out);
                }
            }
            Node nameAttribute = attributes.getNamedItem("name");
            Node contentAttribute = attributes.getNamedItem("content");
            if (nameAttribute == null || contentAttribute == null) {
                continue;
            }
            String name = nameAttribute.getTextContent();
            String content = contentAttribute.getTextContent();
            String xpath = DomUtils.getXPathForNode(jsonldNode);
            IRI nameAsIRI = getPrefixIfExists(name);
            if (nameAsIRI == null) {
                nameAsIRI = SimpleValueFactory.getInstance().createIRI(baseProfile + name);
            }
            JSONLDScript jsonldScript = new JSONLDScript(xpath, nameAsIRI, content);
            result.add(jsonldScript);
        }
        return result;
    }

    private IRI getPrefixIfExists(String name) {
        String[] split = name.split("\\.");
        if (split.length == 2 && prefixes.containsKey(split[0])) {
            return SimpleValueFactory.getInstance().createIRI(prefixes.get(split[0]) + split[1]);
        }
        return null;
    }

    @Override
    public ExtractorDescription getDescription() {
        return EmbeddedJSONLDExtractorFactory.getDescriptionInstance();
    }

    private static class JSONLDScript {

        private String xpath;

        public JSONLDScript(String xpath, IRI name, String content) {
            this.xpath = xpath;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!(o instanceof JSONLDScript)) {
                return false;
            }

            JSONLDScript meta = (JSONLDScript) o;

            if (xpath != null ? !xpath.equals(meta.xpath) : meta.xpath != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return xpath != null ? xpath.hashCode() : 0;
        }
    }

}
