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
 * @author Nisala Nirmana
 *
 */
public class HResume extends Vocabulary {

    public static final String NS = SINDICE.NS + "hresume/";

    private static HResume instance;

    public static HResume getInstance() {
        if (instance == null) {
            instance = new HResume();
        }
        return instance;
    }

    public IRI Resume = createClass(NS, "Resume");
    public IRI education = createClass(NS, "education");
    public IRI experience = createClass(NS, "experience");
    public IRI contact = createClass(NS, "contact");
    public IRI affiliation = createClass(NS, "affiliation");

    public IRI name = createProperty(NS, "name");
    public IRI summary = createProperty(NS, "summary");
    public IRI skill = createProperty(NS, "skill");

    private HResume() {
        super(NS);
    }
}
