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
 * The action module describes the vocabulary for representing actions in general. Actions are processes which are
 * performed by some agent (the actor of the action). This module does not commit itself to a particular theory on
 * thematic roles.
 * </p>
 *
 * @author lewismc
 *
 *
 */
public class LKIFCoreAction extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/action.owl#";

    private static LKIFCoreAction instance;

    public static LKIFCoreAction getInstance() {
        if (instance == null) {
            instance = new LKIFCoreAction();
        }
        return instance;
    }

    /////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/action.owl */
    /////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Action = createClass(NS, "Action");
    public final IRI Agent = createClass(NS, "Agent");
    public final IRI Artifact = createClass(NS, "Artifact");
    public final IRI Collaborative_Plan = createClass(NS, "Collaborative_Plan");
    public final IRI Creation_C = createClass(NS, "Creation");
    public final IRI Natural_Object = createClass(NS, "Natural_Object");
    public final IRI Organisation = createClass(NS, "Organisation");
    public final IRI Person = createClass(NS, "Person");
    public final IRI Personal_Plan = createClass(NS, "Personal_Plan");
    public final IRI Plan = createClass(NS, "Plan");
    public final IRI Reaction = createClass(NS, "Reaction");
    public final IRI Transaction = createClass(NS, "Transaction");
    public final IRI Mental_Object = createClass(NS, "Mental_Object");
    public final IRI Change = createClass(NS, "Change");
    public final IRI Physical_Object = createClass(NS, "Physical_Object");
    public final IRI Process = createClass(NS, "Process");

    // RESOURCES

    // PROPERTIES
    public final IRI actor = createProperty(NS, "actor");
    public final IRI actor_in = createProperty(NS, "actor_in");
    public final IRI direct_part = createProperty(NS, "direct_part");
    public final IRI member = createProperty(NS, "member");
    public final IRI part = createProperty(NS, "part");
    public final IRI creation_P = createProperty(NS, "creation");
    public final IRI participant = createProperty(NS, "participant");
    public final IRI participant_in = createProperty(NS, "participant_in");
    public final IRI result_of = createProperty(NS, "result_of");

    private LKIFCoreAction() {
        super(NS);
    }

}
