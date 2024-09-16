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
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.rdf.Any23ValueFactoryWrapper;
import io.github.sparqlanything.html.org.apache.any23.vocab.FOAF;
import io.github.sparqlanything.html.org.apache.any23.vocab.XFN;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Locale;

/**
 * Extractor for the <a href="http://microformats.org/wiki/xfn">XFN</a> microformat.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XFNExtractor implements TagSoupDOMExtractor {

    private static final FOAF vFOAF = FOAF.getInstance();
    private static final XFN vXFN = XFN.getInstance();

    private final static Any23ValueFactoryWrapper factoryWrapper = new Any23ValueFactoryWrapper(
            SimpleValueFactory.getInstance());

    private HTMLDocument document;
    private ExtractionResult out;

    @Override
    public ExtractorDescription getDescription() {
        return XFNExtractorFactory.getDescriptionInstance();
    }

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, Document in,
            ExtractionResult out) throws IOException, ExtractionException {
        factoryWrapper.setIssueReport(out);
        try {
            document = new HTMLDocument(in);
            this.out = out;

            BNode subject = factoryWrapper.createBNode();
            boolean foundAnyXFN = false;
            final IRI documentIRI = extractionContext.getDocumentIRI();
            for (Node link : document.findAll("//A[@rel][@href]")) {
                foundAnyXFN |= extractLink(link, subject, documentIRI);
            }
            if (!foundAnyXFN)
                return;
            out.writeTriple(subject, RDF.TYPE, vFOAF.Person);
            out.writeTriple(subject, vXFN.mePage, documentIRI);
        } finally {
            factoryWrapper.setIssueReport(null);
        }
    }

    private boolean extractLink(Node firstLink, BNode subject, IRI documentIRI) throws ExtractionException {
        Node hrefNodeItem = firstLink.getAttributes().getNamedItem("href");
        Node relNodeItem = firstLink.getAttributes().getNamedItem("rel");
        if (hrefNodeItem == null || relNodeItem == null) {
            return false;
        }
        String href = hrefNodeItem.getNodeValue();
        String rel = relNodeItem.getNodeValue();

        String[] rels = rel.split("\\s+");
        IRI link = document.resolveIRI(href);
        if (containsRelMe(rels)) {
            if (containsXFNRelExceptMe(rels)) {
                return false; // "me" cannot be combined with any other XFN values
            }
            out.writeTriple(subject, vXFN.mePage, link);
            out.writeTriple(documentIRI, vXFN.getExtendedProperty("me"), link);
        } else {
            BNode person2 = factoryWrapper.createBNode();
            boolean foundAnyXFNRel = false;
            for (String aRel : rels) {
                foundAnyXFNRel |= extractRel(aRel, subject, documentIRI, person2, link);
            }
            if (!foundAnyXFNRel) {
                return false;
            }
            out.writeTriple(person2, RDF.TYPE, vFOAF.Person);
            out.writeTriple(person2, vXFN.mePage, link);
        }
        return true;
    }

    private boolean containsRelMe(String[] rels) {
        for (String rel : rels) {
            if ("me".equals(rel.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsXFNRelExceptMe(String[] rels) {
        for (String rel : rels) {
            if (!"me".equals(rel.toLowerCase(Locale.ROOT)) && vXFN.isXFNLocalName(rel)) {
                return true;
            }
        }
        return false;
    }

    private boolean extractRel(String rel, BNode person1, IRI uri1, BNode person2, IRI uri2) {
        IRI peopleProp = vXFN.getPropertyByLocalName(rel);
        IRI hyperlinkProp = vXFN.getExtendedProperty(rel);
        if (peopleProp == null) {
            return false;
        }
        out.writeTriple(person1, peopleProp, person2);
        out.writeTriple(uri1, hyperlinkProp, uri2);
        return true;
    }

}
