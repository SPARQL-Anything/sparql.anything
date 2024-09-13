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
 * <p>
 * An implementation of the <a href="https://github.com/RinkeHoekstra/lkif-core">lkif-core</a> vocabulary which is a
 * library of ontologies relevant for the legal domain. The library consists of 15 modules, each of which describes a
 * set of closely related concepts from both legal and commonsense domains.
 * </p>
 *
 * <p>
 * The LKIF top ontology is largely based on the top-level of LRI-Core but has less ontological commitment in the sense
 * that it imposes less restrictions on subclasses of the top categories.
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreTop extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/lkif-top.owl#";

    private static LKIFCoreTop instance;

    public static LKIFCoreTop getInstance() {
        if (instance == null) {
            instance = new LKIFCoreTop();
        }
        return instance;
    }

    /////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/lkif-top.owl */
    /////////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Abstract_Entity = createClass(NS, "Abstract_Entity");
    public final IRI Mental_Entity = createClass(NS, "Mental_Entity");
    public final IRI Mental_Object = createClass(NS, "Mental_Object");
    public final IRI Occurrence = createClass(NS, "Occurrence");
    public final IRI Physical_Entity = createClass(NS, "Physical_Entity");
    public final IRI Spatio_Temporal_Occurrence = createClass(NS, "Spatio_Temporal_Occurrence");

    // RESOURCES

    // PROPERTIES

    private LKIFCoreTop() {
        super(NS);
    }

}
