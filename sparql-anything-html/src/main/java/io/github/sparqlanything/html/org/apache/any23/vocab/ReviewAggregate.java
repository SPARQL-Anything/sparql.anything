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

public class ReviewAggregate extends Vocabulary {
    private static ReviewAggregate instance;

    public static ReviewAggregate getInstance() {
        if (instance == null) {
            instance = new ReviewAggregate();
        }
        return instance;
    }

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://purl.org/stuff/revagg#";

    /**
     * The namespace of the vocabulary as a IRI.
     */
    public final IRI NAMESPACE = createIRI(NS);

    /**
     * Number of usefulness votes (integer).
     */
    public final IRI votes = createProperty("votes");

    /**
     * Number of usefulness reviews (integer).
     */
    public final IRI count = createProperty("count");

    /**
     * Optional
     */
    public final IRI average = createProperty("average");

    public final IRI worst = createProperty("worst");

    public final IRI best = createProperty("best");

    /**
     * An agg review of a work.
     */
    public final IRI ReviewAggregate = createProperty("ReviewAggregate");

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    private ReviewAggregate() {
        super(NS);
    }
}
