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
 * Extractor for the <a href="http://microformats.org/wiki/geo">Geo</a> microformat.
 *
 * @author Gabriele Renzi
 */
public class GeoExtractor extends EntityBasedMicroformatExtractor {

    private static final VCard vVCARD = VCard.getInstance();

    @Override
    public ExtractorDescription getDescription() {
        return GeoExtractorFactory.getDescriptionInstance();
    }

    protected String getBaseClassName() {
        return "geo";
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
        HTMLDocument.TextField latNode = document.getSingularTextField("latitude");
        HTMLDocument.TextField lonNode = document.getSingularTextField("longitude");
        String lat = latNode.value();
        String lon = lonNode.value();
        if ("".equals(lat) || "".equals(lon)) {
            String[] both = document.getSingularUrlField("geo").value().split(";");
            if (both.length != 2)
                return false;
            lat = both[0];
            lon = both[1];
        }
        BNode geo = getBlankNodeFor(node);
        out.writeTriple(geo, RDF.TYPE, vVCARD.Location);
        conditionallyAddStringProperty(latNode.source(), geo, vVCARD.latitude, lat);
        conditionallyAddStringProperty(lonNode.source(), geo, vVCARD.longitude, lon);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot(document.getPathToLocalRoot(), geo, this.getClass());

        return true;
    }

}
