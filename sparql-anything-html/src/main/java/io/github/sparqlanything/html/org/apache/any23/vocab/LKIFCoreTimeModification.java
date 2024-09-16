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
 * The modification module is both an extension of the time module and the legal action module. The time module is
 * extended with numerous intervals and moments describing the efficacy and being in force of legal documents. The
 * action module is extended with a typology of modifications. These concepts are described in further detail in
 * Deliverable 3.2 of the ESTRELLA project.
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreTimeModification extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/time-modification.owl#";

    private static LKIFCoreTimeModification instance;

    public static LKIFCoreTimeModification getInstance() {
        if (instance == null) {
            instance = new LKIFCoreTimeModification();
        }
        return instance;
    }

    ////////////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/time-modification.owl */
    ////////////////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Semantic_Annotation = createClass(NS, "Semantic_Annotation");
    public final IRI Modification = createClass(NS, "Modification");
    public final IRI Transposition = createClass(NS, "Transposition");
    public final IRI Ultractivity = createClass(NS, "Ultractivity");
    public final IRI Annulment = createClass(NS, "Annulment");
    public final IRI End_efficacy = createClass(NS, "End_efficacy");
    public final IRI Efficacy_Modification = createClass(NS, "Efficacy_Modification ");
    public final IRI Modification_of_System = createClass(NS, "Modification_of_System ");
    public final IRI Dynamic_Temporal_Entity = createClass(NS, "Dynamic_Temporal_Entity ");
    public final IRI Remaking = createClass(NS, "Remaking ");
    public final IRI Application = createClass(NS, "Application ");
    public final IRI Ratification = createClass(NS, "Ratification ");
    public final IRI Textual_Modification = createClass(NS, "Textual_Modification ");
    public final IRI Prorogation_in_Force = createClass(NS, "Prorogation_in_Force ");
    public final IRI Application_Date = createClass(NS, "Application_Date ");
    public final IRI Retroactivity = createClass(NS, "Retroactivity ");
    public final IRI Modification_of_Term = createClass(NS, "Modification_of_Term ");
    public final IRI Efficacy_Interval = createClass(NS, "Efficacy_Interval ");
    public final IRI Start_Efficacy = createClass(NS, "Start_Efficacy ");
    public final IRI Substitution = createClass(NS, "Substitution ");
    public final IRI Temporal_Modification = createClass(NS, "Temporal_Modification ");
    public final IRI Suspension = createClass(NS, "Suspension ");
    public final IRI In_Force_Modification = createClass(NS, "In_Force_Modification ");
    public final IRI Publication_Date = createClass(NS, "Publication_Date ");
    public final IRI Exception = createClass(NS, "Exception ");
    public final IRI Modification_of_Meaning = createClass(NS, "Modification_of_Meaning ");
    public final IRI Static_Temporal_Entity = createClass(NS, "Static_Temporal_Entity ");
    public final IRI End_in_Force = createClass(NS, "End_in_Force ");
    public final IRI Start_in_Force = createClass(NS, "Start_in_Force ");
    public final IRI Integration = createClass(NS, "Integration ");
    public final IRI Application_Interval = createClass(NS, "Application_Interval ");
    public final IRI Interpretation = createClass(NS, "Interpretation ");
    public final IRI Deregulation = createClass(NS, "Deregulation ");
    public final IRI In_Force_Interval = createClass(NS, "In_Force_Interval ");
    public final IRI Repeal = createClass(NS, "Repeal ");
    public final IRI Modification_of_Scope = createClass(NS, "Modification_of_Scope ");
    public final IRI Delivery_Date = createClass(NS, "Delivery_Date ");
    public final IRI Enter_in_Force_Date = createClass(NS, "Enter_in_Force_Date ");
    public final IRI Variation = createClass(NS, "Variation ");
    public final IRI Existence_Date = createClass(NS, "Existence_Date ");
    public final IRI Relocation = createClass(NS, "Relocation ");
    public final IRI Prorogation_Efficacy = createClass(NS, "Prorogation_Efficacy ");
    public final IRI Extension = createClass(NS, "Extension ");
    public final IRI Renewal = createClass(NS, "Renewal ");

    // RESOURCES

    // PROPERTIES
    public final IRI initial_date = createProperty(NS, "initial_date");
    public final IRI in_force = createProperty(NS, "in_force");
    public final IRI final_date_of = createProperty(NS, "final_date_of");
    public final IRI efficacy = createProperty(NS, "efficacy");
    public final IRI initial_date_of = createProperty(NS, "initial_date_of");
    public final IRI produce_efficacy_modification = createProperty(NS, "produce_efficacy_modification");
    public final IRI duration = createProperty(NS, "duration");
    public final IRI final_date = createProperty(NS, "final_date");
    public final IRI application = createProperty(NS, "application");
    public final IRI date = createProperty(NS, "date");
    public final IRI produce_textual_modification = createProperty(NS, "produce_textual_modification");
    public final IRI produce_inforce_modification = createProperty(NS, "produce_inforce_modification");

    private LKIFCoreTimeModification() {
        super(NS);
    }

}
