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
 * The legal role module extends the role module with a small number of legal concepts related to roles, legal
 * professions etc.
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreLegalRole extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/legal-role.owl#";

    private static LKIFCoreLegalRole instance;

    public static LKIFCoreLegalRole getInstance() {
        if (instance == null) {
            instance = new LKIFCoreLegalRole();
        }
        return instance;
    }

    //////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/legal-role.owl# */
    //////////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Social_Legal_Role = createClass(NS, "Social_Legal_Role");
    public final IRI Legal_Role = createClass(NS, "Legal_Role");
    public final IRI Professional_Legal_Role = createClass(NS, "Professional_Legal_Role");

    // RESOURCES

    // PROPERTIES

    private LKIFCoreLegalRole() {
        super(NS);
    }

}
