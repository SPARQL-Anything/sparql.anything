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

package io.github.sparqlanything.html.org.apache.any23.vocab;

import org.eclipse.rdf4j.model.IRI;

/**
 * Vocabulary definitions from vcard.owl
 */
public class VCard extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.w3.org/2006/vcard/ns#";

    private static VCard instance;

    public static VCard getInstance() {
        if (instance == null) {
            instance = new VCard();
        }
        return instance;
    }

    /**
     * The namespace of the vocabulary as a IRI.
     */
    public final IRI NAMESPACE = createIRI(NS);

    /**
     * An additional part of a person's name.
     */
    public final IRI additional_name = createProperty("additional-name");

    /**
     * A postal or street address of a person.
     */
    public final IRI adr = createProperty("adr");

    /**
     * A person that acts as one's agent.
     */
    public final IRI agent = createProperty("agent");

    /**
     * The altitude of a geographic location.
     */
    public final IRI altitude = createProperty("altitude");

    /**
     * The birthday of a person.
     */
    public final IRI bday = createProperty("bday");

    /**
     * A category of a vCard.
     */
    public final IRI category = createProperty("category");

    /**
     * A class (e.g., public, private, etc.) of a vCard.
     */
    public final IRI class_ = createProperty("class");

    /**
     * The country of a postal address.
     */
    public final IRI country_name = createProperty("country-name");

    /**
     * An email address.
     */
    public final IRI email = createProperty("email");

    /**
     * The extended address of a postal address.
     */
    public final IRI extended_address = createProperty("extended-address");

    /**
     * A family name part of a person's name.
     */
    public final IRI family_name = createProperty("family-name");

    /**
     * A fax number of a person.
     */
    public final IRI fax = createProperty("fax");

    /**
     * A formatted name of a person.
     */
    public final IRI fn = createProperty("fn");

    /**
     * A geographic location associated with a person.
     */
    public final IRI geo = createProperty("geo");

    /**
     * A given name part of a person's name.
     */
    public final IRI given_name = createProperty("given-name");

    /**
     * A home address of a person.
     */
    public final IRI homeAdr = createProperty("homeAdr");

    /**
     * A home phone number of a person.
     */
    public final IRI homeTel = createProperty("homeTel");

    /**
     * An honorific prefix part of a person's name.
     */
    public final IRI honorific_prefix = createProperty("honorific-prefix");

    /**
     * An honorific suffix part of a person's name.
     */
    public final IRI honorific_suffix = createProperty("honorific-suffix");

    /**
     * A key (e.g, PKI key) of a person.
     */
    public final IRI key = createProperty("key");

    /**
     * The formatted version of a postal address (a string with embedded line breaks, punctuation, etc.).
     */
    public final IRI label = createProperty("label");

    /**
     * The latitude of a geographic location.
     */
    public final IRI latitude = createProperty("latitude");

    /**
     * The locality (e.g., city) of a postal address.
     */
    public final IRI locality = createProperty("locality");

    /**
     * A logo associated with a person or their organization.
     */
    public final IRI logo = createProperty("logo");

    /**
     * The longitude of a geographic location.
     */
    public final IRI longitude = createProperty("longitude");

    /**
     * A mailer associated with a vCard.
     */
    public final IRI mailer = createProperty("mailer");

    /**
     * A mobile email address of a person.
     */
    public final IRI mobileEmail = createProperty("mobileEmail");

    /**
     * A mobile phone number of a person.
     */
    public final IRI mobileTel = createProperty("mobileTel");

    /**
     * The components of the name of a person.
     */
    public final IRI n = createProperty("n");

    /**
     * The nickname of a person.
     */
    public final IRI nickname = createProperty("nickname");

    /**
     * Notes about a person on a vCard.
     */
    public final IRI note = createProperty("note");

    /**
     * An organization associated with a person.
     */
    public final IRI org = createProperty("org");

    /**
     * The name of an organization.
     */
    public final IRI organization_name = createProperty("organization-name");

    /**
     * The name of a unit within an organization.
     */
    public final IRI organization_unit = createProperty("organization-unit");

    /**
     * An email address unaffiliated with any particular organization or employer; a personal email address.
     */
    public final IRI personalEmail = createProperty("personalEmail");

    /**
     * A photograph of a person.
     */
    public final IRI photo = createProperty("photo");

    /**
     * The post office box of a postal address.
     */
    public final IRI post_office_box = createProperty("post-office-box");

    /**
     * The postal code (e.g., U.S. ZIP code) of a postal address.
     */
    public final IRI postal_code = createProperty("postal-code");

    /**
     * The region (e.g., state or province) of a postal address.
     */
    public final IRI region = createProperty("region");

    /**
     * The timestamp of a revision of a vCard.
     */
    public final IRI rev = createProperty("rev");

    /**
     * A role a person plays within an organization.
     */
    public final IRI role = createProperty("role");

    /**
     * A version of a person's name suitable for collation.
     */
    public final IRI sort_string = createProperty("sort-string");

    /**
     * A sound (e.g., a greeting or pronounciation) of a person.
     */
    public final IRI sound = createProperty("sound");

    /**
     * The street address of a postal address.
     */
    public final IRI street_address = createProperty("street-address");

    /**
     * A telephone number of a person.
     */
    public final IRI tel = createProperty("tel");

    /**
     * A person's title.
     */
    public final IRI title = createProperty("title");

    /**
     * A timezone associated with a person.
     */
    public final IRI tz = createProperty("tz");

    /**
     * A UID of a person's vCard.
     */
    public final IRI uid = createProperty("uid");

    /**
     * An (explicitly) unlabeled address of a person.
     */
    public final IRI unlabeledAdr = createProperty("unlabeledAdr");

    /**
     * An (explicitly) unlabeled email address of a person.
     */
    public final IRI unlabeledEmail = createProperty("unlabeledEmail");

    /**
     * An (explicitly) unlabeled phone number of a person.
     */
    public final IRI unlabeledTel = createProperty("unlabeledTel");

    /**
     * A URL associated with a person.
     */
    public final IRI url = createProperty("url");

    /**
     * A work address of a person.
     */
    public final IRI workAdr = createProperty("workAdr");

    /**
     * A work email address of a person.
     */
    public final IRI workEmail = createProperty("workEmail");

    /**
     * A work phone number of a person.
     */
    public final IRI workTel = createProperty("workTel");

    /**
     * Resources that are vCard (postal) addresses.
     */
    public final IRI Address = createIRI("http://www.w3.org/2006/vcard/ns#Address");

    public final IRI addressType = createProperty("addressType");

    /**
     * Resources that are vCard Telephones.
     */
    public final IRI Telephone = createIRI("http://www.w3.org/2006/vcard/ns#Address");

    /**
     * Resources that are vCard geographic locations.
     */
    public final IRI Location = createIRI("http://www.w3.org/2006/vcard/ns#Location");

    /**
     * Resources that are vCard personal names.
     */
    public final IRI Name = createIRI("http://www.w3.org/2006/vcard/ns#Name");

    /**
     * Resources that are vCard organizations.
     */
    public final IRI Organization = createIRI("http://www.w3.org/2006/vcard/ns#Organization");

    /**
     * Resources that are vCards
     */
    public final IRI VCard = createIRI("http://www.w3.org/2006/vcard/ns#VCard");

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    public VCard() {
        super(NS);
    }
}
