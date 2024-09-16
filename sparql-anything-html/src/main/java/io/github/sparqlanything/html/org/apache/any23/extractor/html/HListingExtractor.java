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

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.vocab.FOAF;
import io.github.sparqlanything.html.org.apache.any23.vocab.HListing;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument.TextField;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hlisting">hListing</a> microformat.
 *
 * @author Gabriele Renzi
 */
public class HListingExtractor extends EntityBasedMicroformatExtractor {

    private static final HListing hLISTING = HListing.getInstance();
    private static final FOAF foaf = FOAF.getInstance();

    private static final Set<String> ActionClasses = new HashSet<String>() {
        {
            add("sell");
            add("rent");
            add("trade");
            add("meet");
            add("announce");
            add("offer");
            add("wanted");
            add("event");
            add("service");
        }
    };

    private static final List<String> validClassesForAddress = Arrays.asList("post-office-box", "extended-address",
            "street-address", "locality", "region", "postal-code", "country-name");

    private HTMLDocument fragment;

    @Override
    public ExtractorDescription getDescription() {
        return HListingExtractorFactory.getDescriptionInstance();
    }

    protected String getBaseClassName() {
        return "hlisting";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        this.fragment = new HTMLDocument(node);
        BNode listing = getBlankNodeFor(node);
        out.writeTriple(listing, RDF.TYPE, hLISTING.Listing);

        for (String action : findActions(fragment)) {
            out.writeTriple(listing, hLISTING.action, hLISTING.getClass(action));
        }
        out.writeTriple(listing, hLISTING.lister, addLister());
        addItem(listing);
        addDateTimes(listing);
        addPrice(listing);
        addDescription(listing);
        addSummary(listing);
        addPermalink(listing);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(DomUtils.getXPathListForNode(node), listing, this.getClass());

        return true;
    }

    private void addItem(Resource listing) throws ExtractionException {
        Node node = fragment.findMicroformattedObjectNode("*", "item");
        if (null == node)
            return;
        BNode blankItem = valueFactory.createBNode();
        addBNodeProperty(node, listing, hLISTING.item, blankItem);
        addIRIProperty(blankItem, RDF.TYPE, hLISTING.Item);

        HTMLDocument item = new HTMLDocument(node);

        addItemName(item, blankItem);
        addItemUrl(item, blankItem);
        // the format is specified with photo into item, but kelkoo has it into the top level
        addItemPhoto(fragment, blankItem);
        addItemAddresses(fragment, blankItem);
    }

    private void addItemAddresses(HTMLDocument doc, Resource blankItem) {
        final String extractorName = getDescription().getExtractorName();
        for (Node node : doc.findAll(".//*[contains(@class,'adr')]//*[@class]")) {
            String[] klasses = node.getAttributes().getNamedItem("class").getNodeValue().split("\\s+");
            for (String klass : klasses)
                if (validClassesForAddress.contains(klass)) {
                    String value = node.getNodeValue();
                    // do not use conditionallyAdd, it won't work cause of evaluation rules
                    if (!(null == value || "".equals(value))) {
                        IRI property = hLISTING.getPropertyCamelCase(klass);
                        conditionallyAddLiteralProperty(node, blankItem, property, valueFactory.createLiteral(value));
                    }
                }
        }
    }

    private void addPermalink(Resource listing) {
        String link = fragment.find(".//A[contains(@rel,'self') and contains(@rel,'bookmark')]/@href");
        conditionallyAddStringProperty(fragment.getDocument(), listing, hLISTING.permalink, link);
    }

    private void addPrice(Resource listing) {
        TextField price = fragment.getSingularTextField("price");
        conditionallyAddStringProperty(price.source(), listing, hLISTING.price, price.value());
    }

    private void addDescription(Resource listing) {
        TextField description = fragment.getSingularTextField("description");
        conditionallyAddStringProperty(description.source(), listing, hLISTING.description, description.value());
    }

    private void addSummary(Resource listing) {
        TextField summary = fragment.getSingularTextField("summary");
        conditionallyAddStringProperty(summary.source(), listing, hLISTING.summary, summary.value());
    }

    private void addDateTimes(Resource listing) {
        TextField listed = fragment.getSingularTextField("dtlisted");
        conditionallyAddStringProperty(listed.source(), listing, hLISTING.dtlisted, listed.value());
        HTMLDocument.TextField expired = fragment.getSingularTextField("dtexpired");
        conditionallyAddStringProperty(expired.source(), listing, hLISTING.dtexpired, expired.value());
    }

    private Resource addLister() throws ExtractionException {
        Resource blankLister = valueFactory.createBNode();
        addIRIProperty(blankLister, RDF.TYPE, hLISTING.Lister);
        Node node = fragment.findMicroformattedObjectNode("*", "lister");
        if (null == node)
            return blankLister;
        HTMLDocument listerNode = new HTMLDocument(node);
        addListerFn(listerNode, blankLister);
        addListerOrg(listerNode, blankLister);
        addListerEmail(listerNode, blankLister);
        addListerUrl(listerNode, blankLister);
        addListerTel(listerNode, blankLister);
        addListerLogo(listerNode, blankLister);
        return blankLister;
    }

    private void addListerTel(HTMLDocument doc, Resource blankLister) {
        HTMLDocument.TextField tel = doc.getSingularTextField("tel");
        conditionallyAddStringProperty(tel.source(), blankLister, hLISTING.tel, tel.value());
    }

    private void addListerUrl(HTMLDocument doc, Resource blankLister) throws ExtractionException {
        TextField url = doc.getSingularUrlField("url");
        conditionallyAddResourceProperty(blankLister, hLISTING.listerUrl, getHTMLDocument().resolveIRI(url.value()));
    }

    private void addListerEmail(HTMLDocument doc, Resource blankLister) {
        TextField email = doc.getSingularUrlField("email");
        conditionallyAddResourceProperty(blankLister, foaf.mbox, fixLink(email.value(), "mailto"));
    }

    private void addListerFn(HTMLDocument doc, Resource blankLister) {
        TextField fn = doc.getSingularTextField("fn");
        conditionallyAddStringProperty(fn.source(), blankLister, hLISTING.listerName, fn.value());
    }

    private void addListerLogo(HTMLDocument doc, Resource blankLister) throws ExtractionException {
        TextField logo = doc.getSingularUrlField("logo");
        conditionallyAddResourceProperty(blankLister, hLISTING.listerLogo, getHTMLDocument().resolveIRI(logo.value()));
    }

    private void addListerOrg(HTMLDocument doc, Resource blankLister) {
        TextField org = doc.getSingularTextField("org");
        conditionallyAddStringProperty(org.source(), blankLister, hLISTING.listerOrg, org.value());
    }

    private void addItemName(HTMLDocument item, Resource blankItem) {
        HTMLDocument.TextField fn = item.getSingularTextField("fn");
        conditionallyAddStringProperty(fn.source(), blankItem, hLISTING.itemName, fn.value());
    }

    private void addItemUrl(HTMLDocument item, Resource blankItem) throws ExtractionException {
        TextField url = item.getSingularUrlField("url");
        conditionallyAddResourceProperty(blankItem, hLISTING.itemUrl, getHTMLDocument().resolveIRI(url.value()));
    }

    private void addItemPhoto(HTMLDocument doc, Resource blankLister) throws ExtractionException {
        // as per spec
        String url = doc.findMicroformattedValue("*", "item", "A", "photo", "@href");
        conditionallyAddResourceProperty(blankLister, hLISTING.itemPhoto, getHTMLDocument().resolveIRI(url));
        url = doc.findMicroformattedValue("*", "item", "IMG", "photo", "@src");
        conditionallyAddResourceProperty(blankLister, hLISTING.itemPhoto, getHTMLDocument().resolveIRI(url));
        // as per kelkoo. Remember that contains(foo,'') is true in xpath
        url = doc.findMicroformattedValue("*", "photo", "IMG", "", "@src");
        conditionallyAddResourceProperty(blankLister, hLISTING.itemPhoto, getHTMLDocument().resolveIRI(url));
    }

    private List<String> findActions(HTMLDocument doc) {
        List<String> actions = new ArrayList<String>(0);
        // first check if values are inlined
        String[] classes = doc.readAttribute("class").split("\\s+");
        for (String klass : classes) {
            if (ActionClasses.contains(klass))
                actions.add(klass);
        }

        for (Node action : doc.findAll("./*[@class]/@class")) {
            for (String substring : action.getNodeValue().split("\\s+")) {
                if (ActionClasses.contains(substring))
                    actions.add(substring);
            }
        }
        return actions;
    }

}
