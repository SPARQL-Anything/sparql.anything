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
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument;
import io.github.sparqlanything.html.org.apache.any23.vocab.HCard;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.DomUtils;

import java.util.List;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hcard">h-Card</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HCardExtractor extends EntityBasedMicroformatExtractor {

    private static final HCard vCARD = HCard.getInstance();

    private static final String[] cardFields = { "name", "honorific-prefix", "given-name", "additional-name",
            "family-name", "sort-string", "honorific-suffix", "nickname", "email", "logo", "photo", "url", "uid",
            "category", "tel", "note", "bday", "key", "org", "job-title", "role", "impp", "sex", "gender-identity",
            "anniversary", "adr", "geo" };

    private static final String[] addressFields = { "street-address", "extended-address", "locality", "region",
            "postal-code", "country-name", "geo" };

    private static final String[] geoFields = { "latitude", "longitude", "altitude" };

    @Override
    public ExtractorDescription getDescription() {
        return HCardExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected String getBaseClassName() {
        return Microformats2Prefixes.CLASS_PREFIX + "card";
    }

    @Override
    protected void resetExtractor() {
        // empty
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        final BNode card = getBlankNodeFor(node);
        conditionallyAddResourceProperty(card, RDF.TYPE, vCARD.Card);
        final HTMLDocument fragment = new HTMLDocument(node);
        addName(fragment, card);
        addHonorificPrefix(fragment, card);
        addGivenName(fragment, card);
        addAdditionalName(fragment, card);
        addFamilyName(fragment, card);
        addSortString(fragment, card);
        addHonorificSuffix(fragment, card);
        addNickname(fragment, card);
        addEmails(fragment, card);
        addLogo(fragment, card);
        addPhoto(fragment, card);
        addURLs(fragment, card);
        addUID(fragment, card);
        addCategories(fragment, card);
        addTelephones(fragment, card);
        addNotes(fragment, card);
        addBday(fragment, card);
        addKey(fragment, card);
        addOrg(fragment, card);
        addJobTitle(fragment, card);
        addRole(fragment, card);
        addImpp(fragment, card);
        addSex(fragment, card);
        addGenderIdentity(fragment, card);
        addAnniversary(fragment, card);
        addGeo(fragment, card);
        addAdr(fragment, card);
        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(DomUtils.getXPathListForNode(node), card, this.getClass());
        return true;
    }

    public Resource extractEntityAsEmbeddedProperty(HTMLDocument fragment, BNode card, ExtractionResult out)
            throws ExtractionException {
        this.setCurrentExtractionResult(out);
        addName(fragment, card);
        addHonorificPrefix(fragment, card);
        addGivenName(fragment, card);
        addAdditionalName(fragment, card);
        addFamilyName(fragment, card);
        addSortString(fragment, card);
        addHonorificSuffix(fragment, card);
        addNickname(fragment, card);
        addEmails(fragment, card);
        addLogo(fragment, card);
        addPhoto(fragment, card);
        addURLs(fragment, card);
        addUID(fragment, card);
        addCategories(fragment, card);
        addTelephones(fragment, card);
        addNotes(fragment, card);
        addBday(fragment, card);
        addKey(fragment, card);
        addOrg(fragment, card);
        addJobTitle(fragment, card);
        addRole(fragment, card);
        addImpp(fragment, card);
        addSex(fragment, card);
        addGenderIdentity(fragment, card);
        addAnniversary(fragment, card);
        addGeo(fragment, card);
        addAdr(fragment, card);
        return card;
    }

    private void mapFieldWithProperty(HTMLDocument fragment, BNode card, String fieldClass, IRI property) {
        HTMLDocument.TextField title = fragment.getSingularTextField(fieldClass);
        conditionallyAddStringProperty(title.source(), card, property, title.value());
    }

    private void addName(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[0], vCARD.name);
    }

    private void addHonorificPrefix(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[1],
                vCARD.honorific_prefix);
    }

    private void addGivenName(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[2], vCARD.given_name);
    }

    private void addAdditionalName(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[3],
                vCARD.additional_name);
    }

    private void addFamilyName(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[4], vCARD.family_name);
    }

    private void addSortString(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[5], vCARD.sort_string);
    }

    private void addHonorificSuffix(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[6],
                vCARD.honorific_suffix);
    }

    private void addNickname(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[7], vCARD.nickname);
    }

    private void addEmails(HTMLDocument fragment, BNode card) throws ExtractionException {
        final HTMLDocument.TextField[] emails = fragment
                .getPluralUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + cardFields[8]);
        for (HTMLDocument.TextField email : emails) {
            addIRIProperty(card, vCARD.email, fragment.resolveIRI(email.value()));

        }
    }

    private void addLogo(HTMLDocument fragment, BNode card) throws ExtractionException {
        final HTMLDocument.TextField logo = fragment
                .getSingularUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + cardFields[9]);
        if (logo.source() == null)
            return;
        addIRIProperty(card, vCARD.logo, fragment.resolveIRI(logo.value()));
    }

    private void addPhoto(HTMLDocument fragment, BNode card) throws ExtractionException {
        final HTMLDocument.TextField photo = fragment
                .getSingularUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + cardFields[10]);
        if (photo.source() == null)
            return;
        addIRIProperty(card, vCARD.photo, fragment.resolveIRI(photo.value()));
    }

    private void addURLs(HTMLDocument fragment, BNode card) throws ExtractionException {
        final HTMLDocument.TextField[] urls = fragment
                .getPluralUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + cardFields[11]);
        for (HTMLDocument.TextField url : urls) {
            addIRIProperty(card, vCARD.url, fragment.resolveIRI(url.value()));

        }
    }

    private void addUID(HTMLDocument fragment, BNode card) throws ExtractionException {
        final HTMLDocument.TextField uid = fragment
                .getSingularUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + cardFields[12]);
        if (uid.source() == null)
            return;
        addIRIProperty(card, vCARD.uid, fragment.resolveIRI(uid.value()));
    }

    private void addCategories(HTMLDocument fragment, BNode entry) {
        final HTMLDocument.TextField[] categories = fragment
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + cardFields[13]);
        for (HTMLDocument.TextField category : categories) {
            conditionallyAddStringProperty(category.source(), entry, vCARD.category, category.value());
        }
    }

    private void addTelephones(HTMLDocument fragment, BNode card) {
        final HTMLDocument.TextField[] telephones = fragment
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + cardFields[14]);
        for (HTMLDocument.TextField tel : telephones) {
            Node attribute = tel.source().getAttributes().getNamedItem("value");
            if (attribute == null) {
                conditionallyAddStringProperty(tel.source(), card, vCARD.tel, tel.value());
            } else {
                conditionallyAddStringProperty(tel.source(), card, vCARD.tel, attribute.getNodeValue());
            }
        }
    }

    private void addNotes(HTMLDocument fragment, BNode entry) {
        final HTMLDocument.TextField[] categories = fragment
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + cardFields[15]);
        for (HTMLDocument.TextField category : categories) {
            conditionallyAddStringProperty(category.source(), entry, vCARD.note, category.value());
        }
    }

    private void addBday(HTMLDocument fragment, BNode card) {
        final HTMLDocument.TextField bday = fragment
                .getSingularTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + cardFields[16]);
        if (bday.source() == null)
            return;

        Node attribute = bday.source().getAttributes().getNamedItem("datetime");
        if (attribute == null) {
            conditionallyAddStringProperty(bday.source(), card, vCARD.bday, bday.value());
        } else {
            conditionallyAddStringProperty(bday.source(), card, vCARD.bday, attribute.getNodeValue());

        }
    }

    private void addKey(HTMLDocument fragment, BNode card) throws ExtractionException {
        final HTMLDocument.TextField uid = fragment
                .getSingularTextField(Microformats2Prefixes.URL_PROPERTY_PREFIX + cardFields[17]);
        if (uid.source() == null)
            return;
        addIRIProperty(card, vCARD.key, fragment.resolveIRI(uid.value()));
    }

    private void addOrg(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[18], vCARD.org);
    }

    private void addJobTitle(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[19], vCARD.job_title);
    }

    private void addRole(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[20], vCARD.role);
    }

    private void addImpp(HTMLDocument fragment, BNode card) throws ExtractionException {
        final HTMLDocument.TextField impp = fragment
                .getSingularTextField(Microformats2Prefixes.URL_PROPERTY_PREFIX + cardFields[21]);
        if (impp.source() == null)
            return;
        addIRIProperty(card, vCARD.impp, fragment.resolveIRI(impp.value()));
    }

    private void addSex(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[22], vCARD.sex);
    }

    private void addGenderIdentity(HTMLDocument fragment, BNode card) {
        mapFieldWithProperty(fragment, card, Microformats2Prefixes.PROPERTY_PREFIX + cardFields[23],
                vCARD.gender_identity);
    }

    private void addAnniversary(HTMLDocument fragment, BNode card) {
        final HTMLDocument.TextField anniversary = fragment
                .getSingularTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + cardFields[24]);
        if (anniversary.source() == null)
            return;

        Node attribute = anniversary.source().getAttributes().getNamedItem("datetime");
        if (attribute == null) {
            conditionallyAddStringProperty(anniversary.source(), card, vCARD.bday, anniversary.value());
        } else {
            conditionallyAddStringProperty(anniversary.source(), card, vCARD.bday, attribute.getNodeValue());

        }
    }

    private void addAdr(HTMLDocument doc, Resource card) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + cardFields[25]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + cardFields[25]);
        if (nodes.isEmpty())
            return;
        for (Node node : nodes) {
            BNode location = valueFactory.createBNode();
            addIRIProperty(location, RDF.TYPE, vCARD.Address);
            HTMLDocument fragment = new HTMLDocument(node);
            for (String field : addressFields) {
                HTMLDocument.TextField[] values = fragment
                        .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + field);
                for (HTMLDocument.TextField val : values) {
                    if (!field.equals("geo")) {
                        conditionallyAddStringProperty(val.source(), location, vCARD.getProperty(field), val.value());
                    } else {
                        addGeo(new HTMLDocument(node), card);
                    }
                }
            }
        }
    }

    private void addGeo(HTMLDocument doc, Resource card) throws ExtractionException {
        List<Node> nodes = doc.findAllByClassName(Microformats2Prefixes.PROPERTY_PREFIX + cardFields[26]
                + Microformats2Prefixes.SPACE_SEPARATOR + Microformats2Prefixes.CLASS_PREFIX + cardFields[26]);
        if (nodes.isEmpty())
            return;
        for (Node node : nodes) {
            BNode location = valueFactory.createBNode();
            addIRIProperty(location, RDF.TYPE, vCARD.Geo);
            HTMLDocument fragment = new HTMLDocument(node);
            for (String field : geoFields) {
                HTMLDocument.TextField[] values = fragment
                        .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + field);
                for (HTMLDocument.TextField val : values) {
                    Node attribute = val.source().getAttributes().getNamedItem("title");
                    if (attribute == null) {
                        conditionallyAddStringProperty(val.source(), location, vCARD.getProperty(field), val.value());
                    } else {
                        conditionallyAddStringProperty(val.source(), location, vCARD.getProperty(field),
                                attribute.getNodeValue());
                    }
                }
            }
        }
    }

}
