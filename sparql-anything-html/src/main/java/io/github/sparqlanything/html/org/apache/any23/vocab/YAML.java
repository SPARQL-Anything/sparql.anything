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
 * This vocabulary describes model of the yaml file.
 *
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class YAML extends Vocabulary {

    /*
     * Namespace of YAML vocabulary
     */
    public static final String NS = "http://yaml.org/spec/1.2/spec.html#";

    public static final String PREFIX = "yaml";

    public static final String ROOT = "Root";

    public static final String DOCUMENT = "Document";

    public static final String NODE = "Node";

    public static final String SEQUENCE = "Sequence";

    public static final String MAPPING = "Mapping";

    public static final String CONTAINS = "contains";

    public static final String NULL = "Null";

    private static final YAML _instance = new YAML();

    private YAML() {
        super(NS);
    }

    public static YAML getInstance() {
        return _instance;
    }

    // Resources
    /**
     * <p>
     * The root node. Representation of the YAML file. NB: one file may contain more than one documents represented by
     * nodes; e.g.
     * </p>
     * <p>
     * <code>
     * %YAML 1.2
     * ---
     * - data1
     * - data2
     * ---
     * - data3
     * </code>
     * </p>
     * Contains two documents.
     */
    public final IRI root = createClass(NS, ROOT);

    public final IRI document = createClass(NS, DOCUMENT);

    public final IRI node = createClass(NS, NODE);

    public final IRI sequence = createClass(NS, SEQUENCE);

    public final IRI mapping = createClass(NS, MAPPING);
    // property
    public final IRI contains = createProperty(NS, CONTAINS);

    public final IRI nullValue = createProperty(NS, NULL);
}
