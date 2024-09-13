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
 * Class modeling the <a href="http://microformats.org/wiki/hlisting-proposal">hListing</a> vocabulary.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 *
 */
public class HListing extends Vocabulary {

    public static final String NS = "http://sindice.com/hlisting/0.1/";

    private static HListing instance;

    public static HListing getInstance() {
        if (instance == null) {
            instance = new HListing();
        }
        return instance;
    }

    // Resources.
    public final IRI Listing = createClass("Listing");
    public final IRI Lister = createClass("Lister"); // isa FOAF.Person
    public final IRI Item = createClass("Item"); // isa ?

    // Properties.
    public final IRI action = createProperty("action");
    public final IRI lister = createProperty("lister"); // ranges over Lister
    public final IRI item = createProperty("item");

    public final IRI sell = createClass("sell");
    public final IRI rent = createClass("rent");
    public final IRI trade = createClass("trade");
    public final IRI meet = createClass("meet");
    public final IRI announce = createClass("announce");
    public final IRI offer = createClass("offer");
    public final IRI wanted = createClass("wanted");
    public final IRI event = createClass("event");
    public final IRI service = createClass("service");

    public final IRI tel = VCard.getInstance().tel;
    public final IRI dtlisted = createProperty("dtlisted");
    public final IRI dtexpired = createProperty("dtexpired");
    public final IRI price = createProperty("price");

    public final IRI description = createProperty("description");
    public final IRI summary = createProperty("summary");
    public final IRI permalink = createProperty("permalink");

    public final IRI region = VCard.getInstance().region;
    public final IRI postOfficeBox = VCard.getInstance().post_office_box;
    public final IRI locality = VCard.getInstance().locality;
    public final IRI extendedAddress = VCard.getInstance().extended_address;
    public final IRI streetAddress = VCard.getInstance().street_address;
    public final IRI postalCode = VCard.getInstance().postal_code;
    public final IRI countryName = VCard.getInstance().country_name;

    public final IRI listerUrl = createProperty("listerUrl");
    public final IRI listerName = createProperty("listerName");
    public final IRI itemName = createProperty("itemName");
    public final IRI itemUrl = createProperty("itemUrl");
    public final IRI itemPhoto = createProperty("itemPhoto");
    public final IRI listerOrg = createProperty("listerOrg");
    public final IRI listerLogo = createProperty("listerLogo");

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    private IRI createClass(String localName) {
        return createClass(NS, localName);
    }

    private HListing() {
        super(NS);
    }

}
