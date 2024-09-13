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
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.vocab.HResume;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.DomUtils;
import java.util.List;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hresume">hResume</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HResumeExtractor extends EntityBasedMicroformatExtractor {

    private static final HResume vResume = HResume.getInstance();

    private static final String[] resumeFields = { "name", "summary", "contact", "education", "experience", "skill",
            "affiliation" };

    @Override
    public ExtractorDescription getDescription() {
        return HResumeExtractorFactory.getDescriptionInstance();
    }

    @Override
    public String getBaseClassName() {
        return Microformats2Prefixes.CLASS_PREFIX + "resume";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        if (null == node)
            return false;
        BNode person = getBlankNodeFor(node);
        out.writeTriple(person, RDF.TYPE, vResume.Resume);
        final HTMLDocument fragment = new HTMLDocument(node);

        addName(fragment, person);
        addSummary(fragment, person);
        addSkills(fragment, person);

        addExperiences(fragment, person);
        addEducations(fragment, person);

        addAffiliations(fragment, person);
        addContacts(fragment, person);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(DomUtils.getXPathListForNode(node), person, this.getClass());

        return true;
    }

    private void addContacts(HTMLDocument doc, Resource entry) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + resumeFields[2]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + "card");
        if (nodes.isEmpty())
            return;
        HCardExtractorFactory factory = new HCardExtractorFactory();
        HCardExtractor extractor = factory.createExtractor();
        for (Node node : nodes) {
            BNode contact = valueFactory.createBNode();
            addIRIProperty(contact, RDF.TYPE, vResume.contact);
            extractor.extractEntityAsEmbeddedProperty(new HTMLDocument(node), contact, getCurrentExtractionResult());
        }
    }

    private void addAffiliations(HTMLDocument doc, Resource entry) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + resumeFields[6]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + "card");
        if (nodes.isEmpty())
            return;
        HCardExtractorFactory factory = new HCardExtractorFactory();
        HCardExtractor extractor = factory.createExtractor();
        for (Node node : nodes) {
            BNode affiliation = valueFactory.createBNode();
            addIRIProperty(affiliation, RDF.TYPE, vResume.affiliation);
            extractor.extractEntityAsEmbeddedProperty(new HTMLDocument(node), affiliation,
                    getCurrentExtractionResult());
        }
    }

    private void addName(HTMLDocument doc, Resource person) {
        HTMLDocument.TextField name = doc.getSingularTextField(Microformats2Prefixes.PROPERTY_PREFIX + resumeFields[0]);
        conditionallyAddStringProperty(name.source(), person, vResume.name, name.value());
    }

    private void addSummary(HTMLDocument doc, Resource person) {
        HTMLDocument.TextField summary = doc
                .getSingularTextField(Microformats2Prefixes.PROPERTY_PREFIX + resumeFields[1]);
        conditionallyAddStringProperty(summary.source(), person, vResume.summary, summary.value());
    }

    private void addSkills(HTMLDocument doc, Resource person) {
        final HTMLDocument.TextField[] skills = doc
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + resumeFields[5]);
        for (HTMLDocument.TextField skill : skills) {
            conditionallyAddStringProperty(skill.source(), person, vResume.skill, skill.value());
        }

    }

    private void addExperiences(HTMLDocument doc, Resource person) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + resumeFields[4]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + "event");
        if (nodes.isEmpty())
            return;
        HEventExtractorFactory factory = new HEventExtractorFactory();
        HEventExtractor extractor = factory.createExtractor();
        for (Node node : nodes) {
            BNode event = valueFactory.createBNode();
            addIRIProperty(event, RDF.TYPE, vResume.experience);
            extractor.extractEntityAsEmbeddedProperty(new HTMLDocument(node), event, getCurrentExtractionResult());
        }
    }

    private void addEducations(HTMLDocument doc, Resource person) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + resumeFields[3]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + "event");
        if (nodes.isEmpty())
            return;
        HEventExtractorFactory factory = new HEventExtractorFactory();
        HEventExtractor extractor = factory.createExtractor();
        for (Node node : nodes) {
            BNode event = valueFactory.createBNode();
            addIRIProperty(event, RDF.TYPE, vResume.education);
            extractor.extractEntityAsEmbeddedProperty(new HTMLDocument(node), event, getCurrentExtractionResult());
        }
    }
}
