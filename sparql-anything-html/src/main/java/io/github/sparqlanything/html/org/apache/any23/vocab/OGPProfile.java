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
 * The <a href="http://ogp.me/">Open Graph Protocol Profile Type</a> vocabulary.
 */
public class OGPProfile extends Vocabulary {

    private OGPProfile() {
        super(NS);
    }

    public static final String NS = "http://ogp.me/ns/profile#";

    /* BEGIN: http://ogp.me/#type_profile */

    /** A name normally given to an individual by a parent or self-chosen. */
    public static final String PROFILE__FIRST_NAME = "profile:first_name";

    /** A name inherited from a family or marriage and by which the individual is commonly known. */
    public static final String PROFILE__LAST_NAME = "profile:last_name";

    /** A short unique string to identify them. */
    public static final String PROFILE__USERNAME = "profile:username";

    /** Their gender. */
    public static final String PROFILE__GENDER = "profile:gender";

    /* END: http://ogp.me/#type_profile */

    private static OGPProfile instance;

    public static OGPProfile getInstance() {
        if (instance == null) {
            instance = new OGPProfile();
        }
        return instance;
    }

    public final IRI NAMESPACE = createIRI(NS);

    public final IRI profileFirstName = createProperty(PROFILE__FIRST_NAME);
    public final IRI profileLastName = createProperty(PROFILE__LAST_NAME);
    public final IRI profileUsername = createProperty(PROFILE__USERNAME);
    public final IRI profileGender = createProperty(PROFILE__GENDER);

    @SuppressWarnings("unused")
    private IRI createClass(String localName) {
        return createClass(NS, localName);
    }

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

}
