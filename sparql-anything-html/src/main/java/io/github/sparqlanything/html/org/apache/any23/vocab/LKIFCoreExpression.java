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
 * The expression module describes a vocabulary for describing, propositions and propositional attitudes (belief,
 * intention), qualifications, statements and media. It furthermore extends the role module with a number or epistemic
 * roles, and is the basis for the definition of norms.
 * </p>
 *
 * @author lewismc
 */
public class LKIFCoreExpression extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/expression.owl#";

    private static LKIFCoreExpression instance;

    public static LKIFCoreExpression getInstance() {
        if (instance == null) {
            instance = new LKIFCoreExpression();
        }
        return instance;
    }

    /////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/expression.owl */
    /////////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Argument = createClass(NS, "Argument");
    public final IRI Assertion = createClass(NS, "Assertion");
    public final IRI Assumption = createClass(NS, "Assumption");
    public final IRI Belief = createClass(NS, "Belief");
    public final IRI Cause = createClass(NS, "Cause");
    public final IRI Communicated_Attitude = createClass(NS, "Communicated_Attitude");
    public final IRI Declaration = createClass(NS, "Declaration");
    public final IRI Desire = createClass(NS, "Desire");
    public final IRI Document = createClass(NS, "Document");
    public final IRI Evaluative_Attitude = createClass(NS, "Evaluative_Attitude");
    public final IRI Evaluative_Proposition = createClass(NS, "Evaluative_Proposition");
    public final IRI Evidence = createClass(NS, "Evidence");
    public final IRI Exception = createClass(NS, "Exception");
    public final IRI Expectation = createClass(NS, "Expectation");
    public final IRI Expression = createClass(NS, "Expression");
    public final IRI Fact = createClass(NS, "Fact");
    public final IRI Intention = createClass(NS, "Intention");
    public final IRI Lie = createClass(NS, "Lie");
    public final IRI Medium = createClass(NS, "Medium");
    public final IRI Observation = createClass(NS, "Observation");
    public final IRI Problem = createClass(NS, "Problem");
    public final IRI Promise = createClass(NS, "Promise");
    public final IRI Proposition = createClass(NS, "Proposition");
    public final IRI Propositional_Attitude = createClass(NS, "Propositional_Attitude");
    public final IRI Qualification = createClass(NS, "Qualification");
    public final IRI Qualified = createClass(NS, "Qualified");
    public final IRI Reason = createClass(NS, "Reason");
    public final IRI Speech_Act = createClass(NS, "Speech_Act");
    public final IRI Statement_In_Writing = createClass(NS, "Statement_In_Writing");
    public final IRI Surprise = createClass(NS, "Surprise");

    // RESOURCES

    // PROPERTIES
    public final IRI addressee = createProperty(NS, "addressee");
    public final IRI asserted_by = createProperty(NS, "asserted_by");
    public final IRI asserts = createProperty(NS, "asserts");
    public final IRI attitude = createProperty(NS, "attitude");
    public final IRI author = createProperty(NS, "author");
    public final IRI bears = createProperty(NS, "bears");
    public final IRI believed_by = createProperty(NS, "believed_by");
    public final IRI believes = createProperty(NS, "believes");
    public final IRI declares = createProperty(NS, "declares");
    public final IRI declared_by = createProperty(NS, "declared_by");
    public final IRI evaluated_by = createProperty(NS, "evaluated_by");
    public final IRI evaluates = createProperty(NS, "evaluates");
    public final IRI evaluatively_comparable = createProperty(NS, "evaluatively_comparable");
    public final IRI held_by = createProperty(NS, "held_by");
    public final IRI holds = createProperty(NS, "holds");
    public final IRI intended_by = createProperty(NS, "intended_by");
    public final IRI intends = createProperty(NS, "intends");
    public final IRI medium = createProperty(NS, "medium");
    public final IRI observer = createProperty(NS, "observer");
    public final IRI observes = createProperty(NS, "observes");
    public final IRI promised_by = createProperty(NS, "promised_by");
    public final IRI promises = createProperty(NS, "promises");
    public final IRI qualified_by = createProperty(NS, "qualified_by");
    public final IRI qualifies = createProperty(NS, "qualifies");
    public final IRI qualitatively_comparable = createProperty(NS, "qualitatively_comparable");
    public final IRI stated_by = createProperty(NS, "stated_by");
    public final IRI states = createProperty(NS, "states");
    public final IRI towards = createProperty(NS, "towards");
    public final IRI utterer = createProperty(NS, "utterer");
    public final IRI utters = createProperty(NS, "utters");
    public final IRI creation = createProperty(NS, "creation");
    public final IRI counts_as = createProperty(NS, "counts_as");
    public final IRI imposed_on = createProperty(NS, "imposed_on");
    public final IRI played_by = createProperty(NS, "played_by");
    public final IRI plays = createProperty(NS, "plays");

    private LKIFCoreExpression() {
        super(NS);
    }

}
