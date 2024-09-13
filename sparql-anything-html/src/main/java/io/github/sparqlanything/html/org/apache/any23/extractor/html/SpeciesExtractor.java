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

import java.util.Locale;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.vocab.WO;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;

/**
 * Extractor able to extract the <a href="http://microformats.org/wiki/species">Species Microformat</a>. The data are
 * represented using the <a href="http://www.bbc.co.uk/ontologies/wildlife/2010-02-22.shtml">BBC Wildlife Ontology</a>.
 *
 * @see io.github.sparqlanything.html.org.apache.any23.vocab.WO
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class SpeciesExtractor extends EntityBasedMicroformatExtractor {

    private static final WO vWO = WO.getInstance();

    private static final String[] classes = { "kingdom", "phylum", "order", "family", "genus", "species", "class", };

    /**
     * Returns the description of this extractor.
     *
     * @return a human readable description.
     */
    @Override
    public ExtractorDescription getDescription() {
        return SpeciesExtractorFactory.getDescriptionInstance();
    }

    /**
     * Returns the base class name for the extractor.
     *
     * @return a string containing the base of the extractor.
     */
    @Override
    protected String getBaseClassName() {
        return "biota";
    }

    /**
     * Resets the internal status of the extractor to prepare it to a new extraction section.
     */
    @Override
    protected void resetExtractor() {
        // empty
    }

    /**
     * Extracts an entity from a <i>DOM</i> node.
     *
     * @param node
     *            the DOM node.
     * @param out
     *            the extraction result collector.
     *
     * @return <code>true</code> if the extraction has produces something, <code>false</code> otherwise.
     *
     * @throws io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException
     *             if there is an error during extraction
     *
     */
    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        BNode biota = getBlankNodeFor(node);
        conditionallyAddResourceProperty(biota, RDF.TYPE, vWO.species);

        final HTMLDocument fragment = new HTMLDocument(node);
        addNames(fragment, biota);
        addClasses(fragment, biota);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(DomUtils.getXPathListForNode(node), biota, this.getClass());

        return true;
    }

    private void addNames(HTMLDocument doc, Resource biota) throws ExtractionException {
        HTMLDocument.TextField binomial = doc.getSingularTextField("binomial");
        conditionallyAddStringProperty(binomial.source(), biota, vWO.scientificName, binomial.value());
        HTMLDocument.TextField vernacular = doc.getSingularTextField("vernacular");
        conditionallyAddStringProperty(vernacular.source(), biota, vWO.speciesName, vernacular.value());
    }

    private void addClassesName(HTMLDocument doc, Resource biota) throws ExtractionException {
        for (String clazz : classes) {
            HTMLDocument.TextField classTextField = doc.getSingularTextField(clazz);
            conditionallyAddStringProperty(classTextField.source(), biota, resolvePropertyName(clazz),
                    classTextField.value());
        }
    }

    private void addClasses(HTMLDocument doc, Resource biota) throws ExtractionException {
        for (String clazz : classes) {
            HTMLDocument.TextField classTextField = doc.getSingularUrlField(clazz);
            if (classTextField.source() != null) {
                BNode classBNode = getBlankNodeFor(classTextField.source());
                addBNodeProperty(biota, vWO.getProperty(clazz), classBNode);
                conditionallyAddResourceProperty(classBNode, RDF.TYPE, resolveClassName(clazz));
                HTMLDocument fragment = new HTMLDocument(classTextField.source());
                addClassesName(fragment, classBNode);
            }
        }
    }

    private IRI resolvePropertyName(String clazz) {
        return vWO.getProperty(String.format(Locale.ROOT, "%sName", clazz));
    }

    private IRI resolveClassName(String clazz) {
        String upperCaseClass = clazz.substring(0, 1);
        return vWO.getClass(
                String.format(Locale.ROOT, "%s%s", upperCaseClass.toUpperCase(Locale.ROOT), clazz.substring(1)));
    }
}
