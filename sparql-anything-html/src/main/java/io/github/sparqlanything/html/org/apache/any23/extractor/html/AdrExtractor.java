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
import io.github.sparqlanything.html.org.apache.any23.vocab.VCard;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;

/**
 * Extractor for the <a href="http://microformats.org/wiki/adr">adr</a> microformat.
 *
 * @author Gabriele Renzi
 */
public class AdrExtractor extends EntityBasedMicroformatExtractor {

    private static final VCard vVCARD = VCard.getInstance();

    private static final String[] addressFields = { "post-office-box", "extended-address", "street-address", "locality",
            "region", "country-name", "postal-code" };

    protected String getBaseClassName() {
        return "adr";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    protected boolean extractEntity(Node node, ExtractionResult out) {
        if (null == node)
            return false;
        // try lat & lon
        final HTMLDocument document = new HTMLDocument(node);
        BNode adr = getBlankNodeFor(node);
        out.writeTriple(adr, RDF.TYPE, vVCARD.Address);
        final String extractorName = getDescription().getExtractorName();
        for (String field : addressFields) {
            HTMLDocument.TextField[] values = document.getPluralTextField(field);
            for (HTMLDocument.TextField val : values) {
                conditionallyAddStringProperty(val.source(), adr, vVCARD.getProperty(field), val.value());
            }
        }
        HTMLDocument.TextField[] types = document.getPluralTextField("type");
        for (HTMLDocument.TextField val : types) {
            conditionallyAddStringProperty(val.source(), adr, vVCARD.addressType, val.value());
        }

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot(document.getPathToLocalRoot(), adr, this.getClass());

        return true;
    }

    @Override
    public ExtractorDescription getDescription() {
        return AdrExtractorFactory.getDescriptionInstance();
    }

}
