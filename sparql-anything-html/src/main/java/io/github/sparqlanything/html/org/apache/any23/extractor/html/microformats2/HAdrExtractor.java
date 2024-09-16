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
import io.github.sparqlanything.html.org.apache.any23.extractor.html.microformats2.annotations.Includes;
import io.github.sparqlanything.html.org.apache.any23.vocab.VCard;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument;

/**
 * Extractor for the <a href="http://microformats.org/wiki/h-adr">h-adr</a> microformat.
 *
 * @author Nisala Nirmana
 */
@Includes(extractors = HGeoExtractor.class)
public class HAdrExtractor extends EntityBasedMicroformatExtractor {

    private static final VCard vVCARD = VCard.getInstance();

    private static final String[] addressFields = { "street-address", "extended-address", "locality", "region",
            "postal-code", "country-name", "geo" };

    private static final String[] geoFields = { "latitude", "longitude", "altitude" };

    protected String getBaseClassName() {
        return Microformats2Prefixes.CLASS_PREFIX + "adr";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        if (null == node)
            return false;
        final HTMLDocument document = new HTMLDocument(node);
        BNode adr = getBlankNodeFor(node);
        out.writeTriple(adr, RDF.TYPE, vVCARD.Address);
        final String extractorName = getDescription().getExtractorName();
        for (String field : addressFields) {
            HTMLDocument.TextField[] values = document
                    .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + field);
            for (HTMLDocument.TextField val : values) {
                if (!field.equals("geo")) {
                    conditionallyAddStringProperty(val.source(), adr, vVCARD.getProperty(field), val.value());
                } else {
                    String[] composed = val.value().split(";");
                    for (int counter = 0; counter < composed.length; counter++) {
                        conditionallyAddStringProperty(val.source(), adr, vVCARD.getProperty(geoFields[counter]),
                                composed[counter]);

                    }
                }
            }
        }
        addGeoAsUrlResource(adr, document);
        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot(document.getPathToLocalRoot(), adr, this.getClass());
        return true;
    }

    private void addGeoAsUrlResource(Resource card, HTMLDocument document) throws ExtractionException {
        HTMLDocument.TextField[] links = document.getPluralUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + "geo");
        for (HTMLDocument.TextField link : links) {
            conditionallyAddResourceProperty(card, vVCARD.geo, getHTMLDocument().resolveIRI(link.value()));
        }
    }

    @Override
    public ExtractorDescription getDescription() {
        return HAdrExtractorFactory.getDescriptionInstance();
    }

}
