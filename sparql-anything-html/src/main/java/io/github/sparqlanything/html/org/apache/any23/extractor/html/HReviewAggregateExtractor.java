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

import java.util.List;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument.TextField;
import io.github.sparqlanything.html.org.apache.any23.vocab.Review;
import io.github.sparqlanything.html.org.apache.any23.vocab.ReviewAggregate;
import io.github.sparqlanything.html.org.apache.any23.vocab.VCard;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hreview-aggregate">hReview-aggregate</a> microformat.
 *
 * @author Sebastien Richard
 */
public class HReviewAggregateExtractor extends EntityBasedMicroformatExtractor {
    private static final Review vREVIEW = Review.getInstance();
    private static final ReviewAggregate vREVIEWAGG = ReviewAggregate.getInstance();
    private static final VCard vVCARD = VCard.getInstance();

    @Override
    public ExtractorDescription getDescription() {
        return HReviewAggregateExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected String getBaseClassName() {
        return "hreview-aggregate";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        BNode rev = getBlankNodeFor(node);
        out.writeTriple(rev, RDF.TYPE, vREVIEWAGG.ReviewAggregate);
        final HTMLDocument fragment = new HTMLDocument(node);
        addRating(fragment, rev);
        addWorst(fragment, rev);
        addBest(fragment, rev);
        addAverage(fragment, rev);
        addSummary(fragment, rev);
        addType(fragment, rev);
        addItem(fragment, rev);
        addCount(fragment, rev);
        addVotes(fragment, rev);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(DomUtils.getXPathListForNode(node), rev, this.getClass());

        return true;
    }

    private void addType(HTMLDocument doc, Resource rev) {
        TextField value = doc.getSingularTextField("type");
        conditionallyAddStringProperty(value.source(), rev, vREVIEW.type, value.value());
    }

    private void addItem(HTMLDocument root, BNode rev) throws ExtractionException {
        List<Node> nodes = root.findAllByClassName("item");
        for (Node node : nodes) {
            Resource item = findDummy(new HTMLDocument(node));
            addBNodeProperty(node, item, vREVIEW.hasReview, rev);
        }
    }

    private Resource findDummy(HTMLDocument item) throws ExtractionException {
        Resource blank = getBlankNodeFor(item.getDocument());
        TextField val = item.getSingularTextField("fn");
        conditionallyAddStringProperty(val.source(), blank, vVCARD.fn, val.value());
        final TextField url = item.getSingularUrlField("url");
        conditionallyAddResourceProperty(blank, vVCARD.url, getHTMLDocument().resolveIRI(url.value()));
        TextField pics[] = item.getPluralUrlField("photo");
        for (TextField pic : pics) {
            addIRIProperty(blank, vVCARD.photo, getHTMLDocument().resolveIRI(pic.value()));
        }
        return blank;
    }

    private void addRating(HTMLDocument doc, Resource rev) {
        HTMLDocument.TextField value = doc.getSingularTextField("rating");
        conditionallyAddStringProperty(value.source(), rev, vREVIEW.rating, value.value());
    }

    private void addWorst(HTMLDocument doc, Resource rev) {
        HTMLDocument.TextField value = doc.getSingularTextField("worst");
        conditionallyAddStringProperty(value.source(), rev, vREVIEWAGG.worst, value.value());
    }

    private void addBest(HTMLDocument doc, Resource rev) {
        HTMLDocument.TextField value = doc.getSingularTextField("best");
        conditionallyAddStringProperty(value.source(), rev, vREVIEWAGG.best, value.value());
    }

    private void addAverage(HTMLDocument doc, Resource rev) {
        HTMLDocument.TextField value = doc.getSingularTextField("average");
        conditionallyAddStringProperty(value.source(), rev, vREVIEWAGG.average, value.value());
    }

    private void addCount(HTMLDocument doc, Resource rev) {
        HTMLDocument.TextField value = doc.getSingularTextField("count");
        conditionallyAddStringProperty(value.source(), rev, vREVIEWAGG.count, value.value());
    }

    private void addVotes(HTMLDocument doc, Resource rev) {
        HTMLDocument.TextField value = doc.getSingularTextField("votes");
        conditionallyAddStringProperty(value.source(), rev, vREVIEWAGG.votes, value.value());
    }

    private void addSummary(HTMLDocument doc, Resource rev) {
        TextField value = doc.getSingularTextField("summary");
        conditionallyAddStringProperty(value.source(), rev, vREVIEW.title, value.value());
    }
}
