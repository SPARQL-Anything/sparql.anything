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
 * Vocabulary to map the <a href="http://microformats.org/wiki/hcard">h-card</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HCard extends Vocabulary {
    public static final String NS = SINDICE.NS + "hcard/";

    private static HCard instance;

    public static HCard getInstance() {
        if (instance == null) {
            instance = new HCard();
        }
        return instance;
    }

    public IRI Card = createClass(NS, "Card");
    public IRI Address = createClass(NS, "Address");
    public IRI Geo = createClass(NS, "Geo");

    public IRI name = createProperty(NS, "name");
    public IRI honorific_prefix = createProperty(NS, "honorific-prefix");
    public IRI given_name = createProperty(NS, "given-name");
    public IRI additional_name = createProperty(NS, "additional-name");
    public IRI family_name = createProperty(NS, "family-name");
    public IRI sort_string = createProperty(NS, "sort-string");
    public IRI honorific_suffix = createProperty(NS, "honorific-suffix");
    public IRI nickname = createProperty(NS, "nickname");
    public IRI email = createProperty(NS, "email");
    public IRI logo = createProperty(NS, "logo");
    public IRI photo = createProperty(NS, "photo");
    public IRI url = createProperty(NS, "url");
    public IRI uid = createProperty(NS, "uid");
    public IRI category = createProperty(NS, "category");
    public IRI tel = createProperty(NS, "tel");
    public IRI note = createProperty(NS, "note");
    public IRI bday = createProperty(NS, "bday");
    public IRI key = createProperty(NS, "key");
    public IRI org = createProperty(NS, "org");
    public IRI job_title = createProperty(NS, "job-title");
    public IRI role = createProperty(NS, "role");
    public IRI impp = createProperty(NS, "impp");
    public IRI sex = createProperty(NS, "sex");
    public IRI gender_identity = createProperty(NS, "gender-identity");
    public IRI anniversary = createProperty(NS, "anniversary");
    public IRI geo = createProperty(NS, "geo");
    public IRI adr = createProperty(NS, "adr");

    public IRI street_address = createProperty(NS, "street-address");
    public IRI extended_address = createProperty(NS, "extended-address");
    public IRI locality = createProperty(NS, "locality");
    public IRI region = createProperty(NS, "region");
    public IRI postal_code = createProperty(NS, "postal-code");
    public IRI country_name = createProperty(NS, "country-name");

    public IRI latitude = createProperty(NS, "latitude");
    public IRI longitude = createProperty(NS, "longitude");
    public IRI altitude = createProperty(NS, "altitude");

    private HCard() {
        super(NS);
    }
}
