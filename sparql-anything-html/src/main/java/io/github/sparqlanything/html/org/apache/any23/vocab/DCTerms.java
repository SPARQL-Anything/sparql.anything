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
 * The <i>DCTERMS</i> vocabulary. See <a href="http://dublincore.org/">Dublin Core</a>.
 */
public class DCTerms extends Vocabulary {

    public static final String NS = "http://purl.org/dc/terms/";

    // Properties
    public final IRI license = createProperty(NS, "license");
    public final IRI title = createProperty(NS, "title");
    public final IRI creator = createProperty(NS, "creator");
    public final IRI related = createProperty(NS, "related");
    public final IRI date = createProperty(NS, "date");
    public final IRI source = createProperty(NS, "source");

    private DCTerms() {
        super(NS);
    }

    private static DCTerms instance;

    public static DCTerms getInstance() {
        if (instance == null) {
            instance = new DCTerms();
        }
        return instance;
    }
}
