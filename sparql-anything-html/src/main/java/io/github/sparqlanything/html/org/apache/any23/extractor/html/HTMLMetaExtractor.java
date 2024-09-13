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
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import io.github.sparqlanything.html.org.apache.any23.vocab.SINDICE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This extractor represents the <i>HTML META</i> tag values according the
 * <a href="http://www.w3.org/TR/html401/struct/global.html#h-7.4.4">HTML4 specification</a>.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class HTMLMetaExtractor implements Extractor.TagSoupDOMExtractor {

    private static final SINDICE vSINDICE = SINDICE.getInstance();

    private IRI profile;

    private Map<String, IRI> prefixes = new HashMap<>();

    private String documentLang;

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

        final IRI documentIRI = extractionContext.getDocumentIRI();
        Set<Meta> metas = extractMetaElement(in, baseProfile);
        for (Meta meta : metas) {
            String lang = documentLang;
            if (meta.getLang() != null) {
                lang = meta.getLang();
            }
            if (meta.isPragmaDirective) {
                if (lang != null) {
                    out.writeTriple(documentIRI, meta.getHttpEquiv(),
                            SimpleValueFactory.getInstance().createLiteral(meta.getContent(), lang));
                } else {
                    out.writeTriple(documentIRI, meta.getHttpEquiv(),
                            SimpleValueFactory.getInstance().createLiteral(meta.getContent()));
                }
            } else {
                if (lang != null) {
                    out.writeTriple(documentIRI, meta.getName(),
                            SimpleValueFactory.getInstance().createLiteral(meta.getContent(), lang));
                } else {
                    out.writeTriple(documentIRI, meta.getName(),
                            SimpleValueFactory.getInstance().createLiteral(meta.getContent()));
                }
            }
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

    private Set<Meta> extractMetaElement(Document in, String baseProfile) {
        List<Node> metaNodes = DomUtils.findAll(in, "/HTML/HEAD/META");
        Set<Meta> result = new HashSet<>();
        for (Node metaNode : metaNodes) {
            NamedNodeMap attributes = metaNode.getAttributes();
            Node nameAttribute = attributes.getNamedItem("name");
            Node httpEquivAttribute = attributes.getNamedItem("http-equiv");
            Node contentAttribute = attributes.getNamedItem("content");
            if (nameAttribute == null && httpEquivAttribute == null)
                continue; // support HTML5 meta element nodes that do not have both name and http-equiv
            if (nameAttribute != null || httpEquivAttribute != null) {
                if (contentAttribute == null) {
                    continue;
                }
            }
            boolean isPragmaDirective = (httpEquivAttribute != null) ? true : false;
            if (isPragmaDirective) {
                String httpEquiv = httpEquivAttribute.getTextContent();
                String content = contentAttribute.getTextContent();
                String xpath = DomUtils.getXPathForNode(metaNode);
                IRI httpEquivAsIRI = getPrefixIfExists(httpEquiv);
                if (httpEquivAsIRI == null) {
                    httpEquivAsIRI = SimpleValueFactory.getInstance().createIRI(baseProfile + httpEquiv);
                }
                Meta meta = new Meta(xpath, content, httpEquivAsIRI);
                result.add(meta);
            } else {
                String name = nameAttribute.getTextContent();
                String content = contentAttribute.getTextContent();
                String xpath = DomUtils.getXPathForNode(metaNode);
                IRI nameAsIRI = getPrefixIfExists(name);
                if (nameAsIRI == null) {
                    nameAsIRI = SimpleValueFactory.getInstance().createIRI(baseProfile + name);
                }
                Meta meta = new Meta(xpath, nameAsIRI, content);
                result.add(meta);
            }
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
        return HTMLMetaExtractorFactory.getDescriptionInstance();
    }

    private static class Meta {

        private String xpath;

        private IRI name;

        private IRI httpEquiv;

        private String lang;

        private String content;

        private boolean isPragmaDirective;

        public Meta(String xpath, String content, IRI httpEquiv) {
            this.xpath = xpath;
            this.content = content;
            this.httpEquiv = httpEquiv;
            this.setPragmaDirective(true);
        }

        @SuppressWarnings("unused")
        public Meta(String xpath, String content, IRI httpEquiv, String lang) {
            this(xpath, content, httpEquiv);
            this.lang = lang;
        }

        public Meta(String xpath, IRI name, String content) {
            this.xpath = xpath;
            this.name = name;
            this.content = content;
        }

        @SuppressWarnings("unused")
        public Meta(String xpath, IRI name, String content, String lang) {
            this(xpath, name, content);
            this.lang = lang;
        }

        private void setPragmaDirective(boolean value) {
            this.isPragmaDirective = value;
        }

        public IRI getHttpEquiv() {
            return httpEquiv;
        }

        public IRI getName() {
            return name;
        }

        public String getLang() {
            return lang;
        }

        public String getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Meta meta = (Meta) o;

            if (xpath != null ? !xpath.equals(meta.xpath) : meta.xpath != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return xpath != null ? xpath.hashCode() : 0;
        }
    }

}
