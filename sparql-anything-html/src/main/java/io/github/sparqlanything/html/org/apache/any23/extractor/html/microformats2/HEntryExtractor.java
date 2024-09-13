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

package io.github.sparqlanything.html.org.apache.any23.extractor.html.microformats2;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument;
import io.github.sparqlanything.html.org.apache.any23.vocab.HEntry;
import io.github.sparqlanything.html.org.apache.any23.vocab.VCard;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;
import org.eclipse.rdf4j.model.Resource;

import java.util.List;

/**
 * Extractor for the <a href="http://microformats.org/wiki/h-entry">h-entry</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HEntryExtractor extends EntityBasedMicroformatExtractor {

    private static final HEntry vEntry = HEntry.getInstance();
    private static final VCard vVCARD = VCard.getInstance();

    private static final String[] entryFields = { "name", "summary", "content", "published", "updated", "category",
            "url", "uid", "syndication", "in-reply-to", "author", "location",

    };

    private static final String[] geoFields = { "latitude", "longitude", "altitude" };

    @Override
    public ExtractorDescription getDescription() {
        return HEntryExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected String getBaseClassName() {
        return Microformats2Prefixes.CLASS_PREFIX + "entry";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        final BNode entry = getBlankNodeFor(node);
        conditionallyAddResourceProperty(entry, RDF.TYPE, vEntry.Entry);
        final HTMLDocument fragment = new HTMLDocument(node);
        addName(fragment, entry);
        addSummary(fragment, entry);
        addContent(fragment, entry);
        addPublished(fragment, entry);
        addUpdated(fragment, entry);
        addCategories(fragment, entry);
        addURLs(fragment, entry);
        addUID(fragment, entry);
        addSyndications(fragment, entry);
        addInReplyTo(fragment, entry);
        addLocations(fragment, entry);
        addAuthors(fragment, entry);
        return true;
    }

    private void addAuthors(HTMLDocument doc, Resource entry) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + entryFields[10]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + "card");
        if (nodes.isEmpty())
            return;
        HCardExtractorFactory factory = new HCardExtractorFactory();
        HCardExtractor extractor = factory.createExtractor();
        for (Node node : nodes) {
            BNode author = valueFactory.createBNode();
            addIRIProperty(author, RDF.TYPE, vEntry.author);
            extractor.extractEntityAsEmbeddedProperty(new HTMLDocument(node), author, getCurrentExtractionResult());
        }
    }

    private void mapFieldWithProperty(HTMLDocument fragment, BNode entry, String fieldClass, IRI property) {
        HTMLDocument.TextField title = fragment.getSingularTextField(fieldClass);
        conditionallyAddStringProperty(title.source(), entry, property, title.value());
    }

    private void addName(HTMLDocument fragment, BNode entry) {
        mapFieldWithProperty(fragment, entry, Microformats2Prefixes.PROPERTY_PREFIX + entryFields[0], vEntry.name);
    }

    private void addSummary(HTMLDocument fragment, BNode entry) {
        mapFieldWithProperty(fragment, entry, Microformats2Prefixes.PROPERTY_PREFIX + entryFields[1], vEntry.summary);
    }

    private void addContent(HTMLDocument fragment, BNode entry) {
        mapFieldWithProperty(fragment, entry, Microformats2Prefixes.EMBEDDED_PROPERTY_PREFIX + entryFields[2],
                vEntry.content);
    }

    private void addPublished(HTMLDocument fragment, BNode entry) {
        final HTMLDocument.TextField[] durations = fragment
                .getPluralTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + entryFields[3]);
        for (HTMLDocument.TextField duration : durations) {
            Node attribute = duration.source().getAttributes().getNamedItem("datetime");
            if (attribute == null) {
                conditionallyAddStringProperty(duration.source(), entry, vEntry.published, duration.value());
            } else {
                conditionallyAddStringProperty(duration.source(), entry, vEntry.published, attribute.getNodeValue());
            }
        }
    }

    private void addUpdated(HTMLDocument fragment, BNode entry) {
        final HTMLDocument.TextField[] durations = fragment
                .getPluralTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + entryFields[4]);
        for (HTMLDocument.TextField duration : durations) {
            Node attribute = duration.source().getAttributes().getNamedItem("datetime");
            if (attribute == null) {
                conditionallyAddStringProperty(duration.source(), entry, vEntry.updated, duration.value());
            } else {
                conditionallyAddStringProperty(duration.source(), entry, vEntry.updated, attribute.getNodeValue());
            }
        }
    }

    private void addCategories(HTMLDocument fragment, BNode entry) {
        final HTMLDocument.TextField[] categories = fragment
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + entryFields[5]);
        for (HTMLDocument.TextField category : categories) {
            conditionallyAddStringProperty(category.source(), entry, vEntry.category, category.value());
        }
    }

    private void addURLs(HTMLDocument fragment, BNode entry) throws ExtractionException {
        final HTMLDocument.TextField[] urls = fragment
                .getPluralUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + entryFields[6]);
        for (HTMLDocument.TextField url : urls) {
            addIRIProperty(entry, vEntry.url, fragment.resolveIRI(url.value()));
        }
    }

    private void addUID(HTMLDocument fragment, BNode entry) throws ExtractionException {
        final HTMLDocument.TextField uid = fragment
                .getSingularTextField(Microformats2Prefixes.URL_PROPERTY_PREFIX + entryFields[7]);
        if (uid.source() == null)
            return;
        addIRIProperty(entry, vEntry.uid, fragment.resolveIRI(uid.value()));
    }

    private void addSyndications(HTMLDocument fragment, BNode entry) throws ExtractionException {
        final HTMLDocument.TextField[] syndications = fragment
                .getPluralUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + entryFields[8]);
        for (HTMLDocument.TextField syndication : syndications) {
            addIRIProperty(entry, vEntry.syndication, fragment.resolveIRI(syndication.value()));
        }
    }

    private void addInReplyTo(HTMLDocument fragment, BNode entry) throws ExtractionException {
        final HTMLDocument.TextField inReplyTo = fragment
                .getSingularTextField(Microformats2Prefixes.URL_PROPERTY_PREFIX + entryFields[9]);
        if (inReplyTo.source() == null)
            return;
        addIRIProperty(entry, vEntry.in_reply_to, fragment.resolveIRI(inReplyTo.value()));
    }

    private void addLocations(HTMLDocument doc, Resource entry) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + entryFields[11]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + "geo");
        if (nodes.isEmpty())
            return;
        for (Node node : nodes) {
            BNode location = valueFactory.createBNode();
            addIRIProperty(location, RDF.TYPE, vEntry.location);
            HTMLDocument fragment = new HTMLDocument(node);
            for (String field : geoFields) {
                HTMLDocument.TextField[] values = fragment
                        .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + field);
                for (HTMLDocument.TextField val : values) {
                    Node attribute = val.source().getAttributes().getNamedItem("title");
                    if (attribute == null) {
                        conditionallyAddStringProperty(val.source(), location, vVCARD.getProperty(field), val.value());
                    } else {
                        conditionallyAddStringProperty(val.source(), location, vVCARD.getProperty(field),
                                attribute.getNodeValue());
                    }
                }
            }
        }
    }
}
