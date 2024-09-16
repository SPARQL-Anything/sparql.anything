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

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.vocab.DOAC;
import io.github.sparqlanything.html.org.apache.any23.vocab.FOAF;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hresume">hResume</a> microformat.
 *
 * @author Gabriele Renzi
 */
public class HResumeExtractor extends EntityBasedMicroformatExtractor {

    private static final FOAF vFOAF = FOAF.getInstance();
    private static final DOAC vDOAC = DOAC.getInstance();

    @Override
    public ExtractorDescription getDescription() {
        return HResumeExtractorFactory.getDescriptionInstance();
    }

    @Override
    public String getBaseClassName() {
        return "hresume";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) {
        if (null == node)
            return false;
        BNode person = getBlankNodeFor(node);
        // we have a person, at least
        out.writeTriple(person, RDF.TYPE, vFOAF.Person);
        final HTMLDocument fragment = new HTMLDocument(node);
        addSummary(fragment, person);
        addContact(fragment, person);
        addExperiences(fragment, person);
        addEducations(fragment, person);
        addAffiliations(fragment, person);
        addSkills(fragment, person);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(DomUtils.getXPathListForNode(node), person, this.getClass());

        return true;
    }

    private void addSummary(HTMLDocument doc, Resource person) {
        HTMLDocument.TextField summary = doc.getSingularTextField("summary");
        conditionallyAddStringProperty(summary.source(), person, vDOAC.summary, summary.value());
    }

    private void addContact(HTMLDocument doc, Resource person) {
        List<Node> nodes = doc.findAllByClassName("contact");
        if (nodes.size() > 0)
            addBNodeProperty(nodes.get(0), person, vFOAF.isPrimaryTopicOf, getBlankNodeFor(nodes.get(0)));
    }

    private void addExperiences(HTMLDocument doc, Resource person) {
        List<Node> nodes = doc.findAllByClassName("experience");
        for (Node node : nodes) {
            BNode exp = valueFactory.createBNode();
            if (addExperience(exp, new HTMLDocument(node)))
                addBNodeProperty(node, person, vDOAC.experience, exp);
        }
    }

    private boolean addExperience(Resource exp, HTMLDocument document) {
        final Node documentNode = document.getDocument();
        String check = "";

        HTMLDocument.TextField value = document.getSingularTextField("title");
        check += value;
        conditionallyAddStringProperty(value.source(), exp, vDOAC.title, value.value().trim());

        value = document.getSingularTextField("dtstart");
        check += value;
        conditionallyAddStringProperty(documentNode, exp, vDOAC.start_date, value.value().trim());

        value = document.getSingularTextField("dtend");
        check += value;
        conditionallyAddStringProperty(documentNode, exp, vDOAC.end_date, value.value().trim());

        value = document.getSingularTextField("summary");
        check += value;
        conditionallyAddStringProperty(documentNode, exp, vDOAC.organization, value.value().trim());

        return !"".equals(check);
    }

    private void addEducations(HTMLDocument doc, Resource person) {
        List<Node> nodes = doc.findAllByClassName("education");
        for (Node node : nodes) {
            BNode exp = valueFactory.createBNode();
            if (addExperience(exp, new HTMLDocument(node)))
                addBNodeProperty(node, person, vDOAC.education, exp);
        }
    }

    private void addAffiliations(HTMLDocument doc, Resource person) {
        List<Node> nodes = doc.findAllByClassName("affiliation");
        for (Node node : nodes) {
            addBNodeProperty(node, person, vDOAC.affiliation, getBlankNodeFor(node));
        }
    }

    private void addSkills(HTMLDocument doc, Resource person) {
        List<Node> nodes;

        // Extracting data from single node.
        nodes = doc.findAllByClassName("skill");
        for (Node node : nodes) {
            conditionallyAddStringProperty(node, person, vDOAC.skill, extractSkillValue(node));
        }
        // Extracting from enlisting node.
        nodes = doc.findAllByClassName("skills");
        for (Node node : nodes) {
            String nodeText = node.getTextContent();
            String[] skills = nodeText.split(",");
            for (String skill : skills) {
                conditionallyAddStringProperty(node, person, vDOAC.skill, skill.trim());
            }
        }
    }

    private String extractSkillValue(Node n) {
        String name = n.getNodeName();
        String skill = null;
        if ("A".equals(name) && DomUtils.hasAttribute(n, "rel", "tag")) {
            skill = n.getAttributes().getNamedItem("href").getTextContent();
        } else {
            skill = n.getTextContent();
        }
        return skill;
    }

}
