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
 * The legal action module extends the action module with a number of legal concepts related to action and agent, such
 * as public acts, public bodies, legal person, natural person etc.
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreLegalAction extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/legal-action.owl#";

    private static LKIFCoreLegalAction instance;

    public static LKIFCoreLegalAction getInstance() {
        if (instance == null) {
            instance = new LKIFCoreLegalAction();
        }
        return instance;
    }

    ////////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/legal-action.owl# */
    ////////////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Limited_Company = createClass(NS, "Limited_Company");
    public final IRI Private_Legal_Person = createClass(NS, "Private_Legal_Person");
    public final IRI Society = createClass(NS, "Society");
    public final IRI Natural_Person = createClass(NS, "Natural_Person");
    public final IRI Mandate = createClass(NS, "Mandate");
    public final IRI Corporation = createClass(NS, "Corporation");
    public final IRI Legal_Person = createClass(NS, "Legal_Person");
    public final IRI Public_Body = createClass(NS, "Public_Body");
    public final IRI Foundation = createClass(NS, "Foundation");
    public final IRI Co_operative = createClass(NS, "Co-operative");
    public final IRI Legislative_Body = createClass(NS, "Legislative_Body");
    public final IRI Delegation = createClass(NS, "Delegation");
    public final IRI Legal_Speech_Act = createClass(NS, "Legal_Speech_Act");
    public final IRI Public_Act = createClass(NS, "Public_Act");
    public final IRI Company = createClass(NS, "Company");
    public final IRI Decision = createClass(NS, "Decision");
    public final IRI Public_Limited_Company = createClass(NS, "Public_Limited_Company");
    public final IRI Incorporated = createClass(NS, "Incorporated");
    public final IRI Act_of_Law = createClass(NS, "Act_of_Law");
    public final IRI Association = createClass(NS, "Association");
    public final IRI Assignment = createClass(NS, "Assignment");
    public final IRI Unincorporated = createClass(NS, "Unincorporated");

    // RESOURCES

    // PROPERTIES

    private LKIFCoreLegalAction() {
        super(NS);
    }

}
