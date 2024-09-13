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

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.vocab.VCard;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument;

import java.util.ArrayList;

/**
 * Extractor for the <a href="http://microformats.org/wiki/h-geo">h-geo</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HGeoExtractor extends EntityBasedMicroformatExtractor {

    private static final VCard vVCARD = VCard.getInstance();

    private static final String[] geoFields = { "latitude", "longitude", "altitude" };

    @Override
    public ExtractorDescription getDescription() {
        return HGeoExtractorFactory.getDescriptionInstance();
    }

    protected String getBaseClassName() {
        return Microformats2Prefixes.CLASS_PREFIX + "geo";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    protected boolean extractEntity(Node node, ExtractionResult out) {
        if (null == node)
            return false;
        final HTMLDocument document = new HTMLDocument(node);
        BNode geo = getBlankNodeFor(node);
        out.writeTriple(geo, RDF.TYPE, vVCARD.Location);
        final String extractorName = getDescription().getExtractorName();
        ArrayList<HTMLDocument.TextField> geoNodes = new ArrayList<HTMLDocument.TextField>();
        for (String field : geoFields) {
            geoNodes.add(document.getSingularTextField(Microformats2Prefixes.PROPERTY_PREFIX + field));
        }
        if (geoNodes.get(0).source() == null) {
            String[] composed = document.getSingularUrlField(Microformats2Prefixes.CLASS_PREFIX + "geo").value()
                    .split(";");
            for (int counter = 0; counter < composed.length; counter++) {
                conditionallyAddStringProperty(
                        document.getSingularUrlField(Microformats2Prefixes.CLASS_PREFIX + "geo").source(), geo,
                        vVCARD.getProperty(geoFields[counter]), composed[counter]);
            }
        } else {
            for (int counter = 0; counter < geoNodes.size(); counter++) {
                conditionallyAddStringProperty(geoNodes.get(counter).source(), geo,
                        vVCARD.getProperty(geoFields[counter]), geoNodes.get(counter).value());
            }
        }
        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot(document.getPathToLocalRoot(), geo, this.getClass());
        return true;
    }

}
