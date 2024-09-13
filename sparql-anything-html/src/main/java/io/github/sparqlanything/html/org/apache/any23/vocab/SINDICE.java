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
 * This class models an internal <i>Sindice</i> Vocabulary to describe resource domains and Microformat nesting
 * relationships.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class SINDICE extends Vocabulary {

    public static final String DOMAIN = "domain";

    public static final String NESTING = "nesting";

    public static final String NESTING_ORIGINAL = "nesting_original";

    public static final String NESTING_STRUCTURED = "nesting_structured";

    public static final String SIZE = "size";

    public static final String DATE = "date";

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://vocab.sindice.net/any23#";

    private static SINDICE instance;

    public static SINDICE getInstance() {
        if (instance == null) {
            instance = new SINDICE();
        }
        return instance;
    }

    /**
     * The namespace of the vocabulary as a IRI.
     */
    public final IRI NAMESPACE = createIRI(NS);

    /**
     * This property expresses the DNS domain of the resource on which it is applied. It is intended to be used to keep
     * track of the domain provenance of each resource.
     */
    public final IRI domain = createProperty(DOMAIN);

    /**
     * This property links a resource with a <i>blank node</i> that represents a nested <i>Microformat</i> node.
     */
    public final IRI nesting = createProperty(NESTING);

    /**
     * This property is used to keep track of the original nested <i>RDF property</i>.
     */
    public final IRI nesting_original = createProperty(NESTING_ORIGINAL);

    /**
     * This property links the resource with a <i>node</i> representing the nested <i>Microformat</i>
     *
     */
    public final IRI nesting_structured = createProperty(NESTING_STRUCTURED);

    /**
     * Size meta property indicating the number of triples within the returned dataset.
     */
    public final IRI size = createProperty(SIZE);

    /**
     * Date meta property indicating the data generation time.
     */
    public final IRI date = createProperty(DATE);

    private IRI createClass(String localName) {
        return createClass(NS, localName);
    }

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    private SINDICE() {
        super(NS);
    }

}
