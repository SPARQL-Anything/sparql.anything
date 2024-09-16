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
 * The <a href="http://ramonantonio.net/doac/0.1/">Description Of A Career</a> vocabulary.
 */
public class DOAC extends Vocabulary {

    public static final String NS = "http://ramonantonio.net/doac/0.1/#";

    private static DOAC instance;

    public static DOAC getInstance() {
        if (instance == null) {
            instance = new DOAC();
        }
        return instance;
    }

    // Properties.
    public final IRI summary = createProperty(NS, "summary");
    public final IRI end_date = createProperty(NS, "end-date");
    public final IRI publication = createProperty(NS, "publication");
    public final IRI title = createProperty(NS, "title");
    public final IRI reference = createProperty(NS, "reference");
    public final IRI language = createProperty(NS, "language");
    public final IRI experience = createProperty(NS, "experience");
    public final IRI organization = createProperty(NS, "organization");
    public final IRI affiliation = createProperty(NS, "affiliation");
    public final IRI writes = createProperty(NS, "writes");
    public final IRI start_date = createProperty(NS, "start-date");
    public final IRI education = createProperty(NS, "education");
    public final IRI skill = createProperty(NS, "skill");
    public final IRI referer = createProperty(NS, "referer");
    public final IRI isco88_code = createProperty(NS, "isco88-code");
    public final IRI speaks = createProperty(NS, "speaks");
    public final IRI reads = createProperty(NS, "reads");
    public final IRI reference_type = createProperty(NS, "reference-type");

    // Resources.
    public final IRI Publication = createClass(NS, "Publication");
    public final IRI Education = createClass(NS, "Education");
    public final IRI OrganisationalSkill = createClass(NS, "OrganisationalSkill");
    public final IRI PrimarySchool = createClass(NS, "PrimarySchool");
    public final IRI Reference = createClass(NS, "Reference");
    public final IRI DrivingSkill = createClass(NS, "DrivingSkill");
    public final IRI Degree = createClass(NS, "Degree");
    public final IRI LanguageSkill = createClass(NS, "LanguageSkill");
    public final IRI Skill = createClass(NS, "Skill");
    public final IRI SecondarySchool = createClass(NS, "SecondarySchool");
    public final IRI Course = createClass(NS, "Course");
    public final IRI Experience = createClass(NS, "Experience");
    public final IRI SocialSkill = createClass(NS, "SocialSkill");
    public final IRI ComputerSkill = createClass(NS, "ComputerSkill");
    public final IRI LanguageLevel = createClass(NS, "LanguageLevel");

    private DOAC() {
        super(NS);
    }

}
