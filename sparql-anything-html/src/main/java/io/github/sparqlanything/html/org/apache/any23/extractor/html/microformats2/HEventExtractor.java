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
import io.github.sparqlanything.html.org.apache.any23.vocab.HEvent;
import io.github.sparqlanything.html.org.apache.any23.vocab.VCard;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument;

import java.util.List;

import static io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument.TextField;

/**
 * Extractor for the <a href="http://microformats.org/wiki/h-event">h-event</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HEventExtractor extends EntityBasedMicroformatExtractor {

    private static final HEvent vEvent = HEvent.getInstance();
    private static final VCard vVCARD = VCard.getInstance();

    private String[] eventFields = { "name", "summary", "start", "end", "duration", "description", "url", "category",
            "location", "attendee" };

    private static final String[] geoFields = { "latitude", "longitude", "altitude" };

    @Override
    public ExtractorDescription getDescription() {
        return HEventExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected String getBaseClassName() {
        return Microformats2Prefixes.CLASS_PREFIX + "event";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        final BNode event = getBlankNodeFor(node);
        conditionallyAddResourceProperty(event, RDF.TYPE, vEvent.event);
        final HTMLDocument fragment = new HTMLDocument(node);
        addName(fragment, event);
        addSummary(fragment, event);
        addStart(fragment, event);
        addEnd(fragment, event);
        addDuration(fragment, event);
        addDescription(fragment, event);
        addURLs(fragment, event);
        addCategories(fragment, event);
        addLocations(fragment, event);
        addAttendees(fragment, event);
        return true;
    }

    public Resource extractEntityAsEmbeddedProperty(HTMLDocument fragment, BNode event, ExtractionResult out)
            throws ExtractionException {
        this.setCurrentExtractionResult(out);
        addName(fragment, event);
        addSummary(fragment, event);
        addStart(fragment, event);
        addEnd(fragment, event);
        addDuration(fragment, event);
        addDescription(fragment, event);
        addURLs(fragment, event);
        addCategories(fragment, event);
        addLocations(fragment, event);
        addAttendees(fragment, event);
        return event;
    }

    private void addAttendees(HTMLDocument doc, Resource entry) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + eventFields[9]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + "card");
        if (nodes.isEmpty())
            return;
        HCardExtractorFactory factory = new HCardExtractorFactory();
        HCardExtractor extractor = factory.createExtractor();
        for (Node node : nodes) {
            BNode attendee = valueFactory.createBNode();
            addIRIProperty(attendee, RDF.TYPE, vEvent.attendee);
            extractor.extractEntityAsEmbeddedProperty(new HTMLDocument(node), attendee, getCurrentExtractionResult());
        }
    }

    private void mapFieldWithProperty(HTMLDocument fragment, BNode recipe, String fieldClass, IRI property) {
        TextField title = fragment.getSingularTextField(fieldClass);
        conditionallyAddStringProperty(title.source(), recipe, property, title.value());
    }

    private void addName(HTMLDocument fragment, BNode event) {
        mapFieldWithProperty(fragment, event, Microformats2Prefixes.PROPERTY_PREFIX + eventFields[0], vEvent.name);
    }

    private void addSummary(HTMLDocument fragment, BNode event) {
        mapFieldWithProperty(fragment, event, Microformats2Prefixes.PROPERTY_PREFIX + eventFields[1], vEvent.summary);
    }

    private void addStart(HTMLDocument fragment, BNode event) {
        final TextField start = fragment
                .getSingularTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + eventFields[2]);
        if (start.source() == null)
            return;
        Node attribute = start.source().getAttributes().getNamedItem("datetime");
        if (attribute == null) {
            conditionallyAddStringProperty(start.source(), event, vEvent.start, start.value());
        } else {
            conditionallyAddStringProperty(start.source(), event, vEvent.start, attribute.getNodeValue());
        }
    }

    private void addEnd(HTMLDocument fragment, BNode event) {
        final TextField end = fragment
                .getSingularTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + eventFields[3]);
        if (end.source() == null)
            return;
        Node attribute = end.source().getAttributes().getNamedItem("datetime");
        if (attribute == null) {
            conditionallyAddStringProperty(end.source(), event, vEvent.end, end.value());
        } else {
            conditionallyAddStringProperty(end.source(), event, vEvent.end, attribute.getNodeValue());
        }
    }

    private void addDuration(HTMLDocument fragment, BNode event) {
        final TextField duration = fragment
                .getSingularTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + eventFields[4]);
        if (duration.source() == null)
            return;
        Node attribute = duration.source().getAttributes().getNamedItem("datetime");
        if (attribute == null) {
            conditionallyAddStringProperty(duration.source(), event, vEvent.duration, duration.value());
        } else {
            conditionallyAddStringProperty(duration.source(), event, vEvent.duration, attribute.getNodeValue());
        }
    }

    private void addDescription(HTMLDocument fragment, BNode event) {
        mapFieldWithProperty(fragment, event, Microformats2Prefixes.PROPERTY_PREFIX + eventFields[5],
                vEvent.description);
    }

    private void addURLs(HTMLDocument fragment, BNode event) throws ExtractionException {
        final TextField[] urls = fragment
                .getPluralUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + eventFields[6]);
        for (TextField url : urls) {
            addIRIProperty(event, vEvent.url, fragment.resolveIRI(url.value()));
        }
    }

    private void addCategories(HTMLDocument fragment, BNode event) {
        final TextField[] categories = fragment
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + eventFields[7]);
        for (TextField category : categories) {
            conditionallyAddStringProperty(category.source(), event, vEvent.category, category.value());
        }
    }

    private void addLocations(HTMLDocument doc, Resource entry) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + eventFields[8]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + "geo");
        if (nodes.isEmpty())
            return;
        for (Node node : nodes) {
            BNode location = valueFactory.createBNode();
            addIRIProperty(location, RDF.TYPE, vEvent.location);
            HTMLDocument fragment = new HTMLDocument(node);
            for (String field : geoFields) {
                TextField[] values = fragment
                        .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + field);
                for (TextField val : values) {
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
