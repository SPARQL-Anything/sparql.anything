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
 * The role module defines a typology of roles (epistemic roles, functions, person roles, organisation roles) and the
 * plays-property for relating a role filler to a role.
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreRole extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/role.owl#";

    private static LKIFCoreRole instance;

    public static LKIFCoreRole getInstance() {
        if (instance == null) {
            instance = new LKIFCoreRole();
        }
        return instance;
    }

    ///////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/role.owl */
    ///////////////////////////////////////////////////////
    // CLASSES
    public final IRI Epistemic_Role = createClass(NS, "Epistemic_Role");
    public final IRI Function = createClass(NS, "Function");
    public final IRI Organisation_Role = createClass(NS, "Organisation_Role");
    public final IRI Person_Role = createClass(NS, "Person_Role");
    public final IRI Role = createClass(NS, "Role");
    public final IRI Social_Role = createClass(NS, "Social_Role");
    public final IRI Subjective_Entity = createClass(NS, "Subjective_Entity ");

    // RESOURCES

    // PROPERTIES
    public final IRI context = createProperty(NS, "context");
    public final IRI counts_as = createProperty(NS, "counts_as");
    public final IRI imposed_on = createProperty(NS, "imposed_on");
    public final IRI played_by = createProperty(NS, "played_by");
    public final IRI plays = createProperty(NS, "plays");

    private LKIFCoreRole() {
        super(NS);
    }

}
