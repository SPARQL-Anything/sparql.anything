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
 * The norm module is an extension primarily on the expression module where norms are defined as qualifications. Please
 * refer to Deliverable 1.1 for a more in-depth description of the underlying theory. It furthermore defines a number of
 * legal sources, e.g. legal documents, customary law etc., and a typology of rights and powers, cf. Sartor (2006),
 * Rubino et al. (2006)
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreNorm extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/norm.owl#";

    private static LKIFCoreNorm instance;

    public static LKIFCoreNorm getInstance() {
        if (instance == null) {
            instance = new LKIFCoreNorm();
        }
        return instance;
    }

    ///////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/norm.owl */
    ///////////////////////////////////////////////////////
    // CLASSES
    public final IRI Hohfeldian_Power = createClass(NS, "Hohfeldian_Power");
    public final IRI Normatively_Qualified = createClass(NS, "Normatively_Qualified");
    public final IRI Code_of_Conduct = createClass(NS, "Code_of_Conduct");
    public final IRI Regulation = createClass(NS, "Regulation");
    public final IRI Soft_Law = createClass(NS, "Soft_Law");
    public final IRI Strictly_Disallowed = createClass(NS, "Strictly_Disallowed");
    public final IRI Permissive_Right = createClass(NS, "Permissive_Right");
    public final IRI Proclamation = createClass(NS, "Proclamation");
    public final IRI Legal_Expression = createClass(NS, "Legal_Expression");
    public final IRI Qualificatory_Expression = createClass(NS, "Qualificatory_Expression");
    public final IRI Enabling_Power = createClass(NS, "Enabling_Power");
    public final IRI Existential_Expression = createClass(NS, "Existential_Expression");
    public final IRI Persuasive_Precedent = createClass(NS, "Persuasive_Precedent");
    public final IRI Belief_In_Violation = createClass(NS, "Belief_In_Violation");
    public final IRI Strictly_Allowed = createClass(NS, "Strictly_Allowed");
    public final IRI Legal_Doctrine = createClass(NS, "Legal_Doctrine");
    public final IRI Resolution = createClass(NS, "Resolution");
    public final IRI Evaluative_Expression = createClass(NS, "Evaluative_Expression");
    public final IRI Liberty_Right = createClass(NS, "Liberty_Right");
    public final IRI Declarative_Power = createClass(NS, "Declarative_Power");
    public final IRI Contract = createClass(NS, "Contract");
    public final IRI Custom = createClass(NS, "Custom");
    public final IRI Exclusionary_Right = createClass(NS, "Exclusionary_Right");
    public final IRI International_Agreement = createClass(NS, "International_Agreement");
    public final IRI Customary_Law = createClass(NS, "Customary_Law");
    public final IRI Action_Power = createClass(NS, "Action_Power");
    public final IRI Legal_Source = createClass(NS, "Legal_Source");
    public final IRI Statute = createClass(NS, "Statute");
    public final IRI International_Arbitration = createClass(NS, "International_Arbitration");
    public final IRI Immunity = createClass(NS, "Immunity");
    public final IRI Treaty = createClass(NS, "Treaty");
    public final IRI Mandatory_Precedent = createClass(NS, "Mandatory_Precedent");
    public final IRI Code = createClass(NS, "Code");
    public final IRI Allowed = createClass(NS, "Allowed");
    public final IRI Observation_of_Violation = createClass(NS, "Observation_of_Violation");
    public final IRI Legal_Document = createClass(NS, "Legal_Document");
    public final IRI Potestative_Expression = createClass(NS, "Potestative_Expression");
    public final IRI Norm = createClass(NS, "Norm");
    public final IRI Potestative_Right = createClass(NS, "Potestative_Right");
    public final IRI Allowed_And_Disallowed = createClass(NS, "Allowed_And_Disallowed");
    public final IRI Obligation = createClass(NS, "Obligation");
    public final IRI Disallowed_Intention = createClass(NS, "Disallowed_Intention");
    public final IRI Permission = createClass(NS, "Permission");
    public final IRI Liability_Right = createClass(NS, "Liability_Right");
    public final IRI Right = createClass(NS, "Right");
    public final IRI Obliged = createClass(NS, "Obliged");
    public final IRI Non_binding_International_Agreement = createClass(NS, "Non-binding_International_Agreement");
    public final IRI Directive = createClass(NS, "Directive");
    public final IRI Disallowed = createClass(NS, "Disallowed");
    public final IRI Definitional_Expression = createClass(NS, "Definitional_Expression");
    public final IRI Prohibition = createClass(NS, "Prohibition");
    public final IRI Precedent = createClass(NS, "Precedent");
    public final IRI Obligative_Right = createClass(NS, "Obligative_Right");

    // RESOURCES

    // PROPERTIES
    public final IRI normatively_comparable = createProperty(NS, "normatively_comparable");
    public final IRI normatively_equivalent_or_better = createProperty(NS, "normatively_equivalent_or_better");
    public final IRI disallows = createProperty(NS, "disallows");
    public final IRI normatively_strictly_worse = createProperty(NS, "normatively_strictly_worse");
    public final IRI normatively_not_equivalent = createProperty(NS, "normatively_not_equivalent");
    public final IRI normatively_strictly_better = createProperty(NS, "normatively_strictly_better");
    public final IRI allowed_by = createProperty(NS, "allowed_by");
    public final IRI disallowed_by = createProperty(NS, "disallowed_by");
    public final IRI allows = createProperty(NS, "allows");
    public final IRI normatively_equivalent_or_worse = createProperty(NS, "normatively_equivalent_or_worse");
    public final IRI commands = createProperty(NS, "commands");
    public final IRI commanded_by = createProperty(NS, "commanded_by");
    public final IRI strictly_equivalent = createProperty(NS, "strictly_equivalent");

    private LKIFCoreNorm() {
        super(NS);
    }

}
