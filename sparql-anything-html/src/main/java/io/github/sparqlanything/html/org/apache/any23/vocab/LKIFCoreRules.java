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
 * The rules &amp; argumentation module defines roles central to argumentation, and describes the vocabulary for LKIF
 * rules as defined in Deliverable 1.1, chapter 5. The module leaves room for further extension to complex argumentation
 * frameworks (AIF, Carneades).
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreRules extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/lkif-rules.owl#";

    private static LKIFCoreRules instance;

    public static LKIFCoreRules getInstance() {
        if (instance == null) {
            instance = new LKIFCoreRules();
        }
        return instance;
    }

    /////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/lkif-rules.owl */
    /////////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Valid_Rule = createClass(NS, "Valid_Rule");
    public final IRI Rule = createClass(NS, "Rule");

    // RESOURCES

    // PROPERTIES
    public final IRI rule_predicate = createProperty(NS, "rule_predicate");
    public final IRI prior = createProperty(NS, "prior");
    public final IRI excluded = createProperty(NS, "excluded");
    public final IRI applies = createProperty(NS, "applies");
    public final IRI rebuts = createProperty(NS, "rebuts");

    private LKIFCoreRules() {
        super(NS);
    }

}
