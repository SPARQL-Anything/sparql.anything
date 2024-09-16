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
 * The process module extends the LKIF top ontology module with a definition of changes, processes (being causal
 * changes) and physical objects. It introduces a limited set of properties for describing participant roles of
 * processes.
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreProcess extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/process.owl#";

    private static LKIFCoreProcess instance;

    public static LKIFCoreProcess getInstance() {
        if (instance == null) {
            instance = new LKIFCoreProcess();
        }
        return instance;
    }

    //////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/process.owl */
    //////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Change = createClass(NS, "Change");
    public final IRI Continuation = createClass(NS, "Continuation");
    public final IRI Initiation = createClass(NS, "Initiation");
    public final IRI Mental_Process = createClass(NS, "Mental_Process");
    public final IRI Physical_Object = createClass(NS, "Physical_Object");
    public final IRI Physical_Process = createClass(NS, "Physical_Process");
    public final IRI Process = createClass(NS, "Process");
    public final IRI Termination = createClass(NS, "Termination");

    // RESOURCES

    // PROPERTIES
    public final IRI created_by = createProperty(NS, "created_by");
    public final IRI creation = createProperty(NS, "creation");
    public final IRI participant = createProperty(NS, "participant");
    public final IRI participant_in = createProperty(NS, "participant_in");
    public final IRI requirement = createProperty(NS, "requirement");
    public final IRI requirement_of = createProperty(NS, "requirement_of");
    public final IRI resource = createProperty(NS, "resource");
    public final IRI resource_for = createProperty(NS, "resource_for ");
    public final IRI result = createProperty(NS, "result");
    public final IRI result_of = createProperty(NS, "result_of");

    private LKIFCoreProcess() {
        super(NS);
    }

}
