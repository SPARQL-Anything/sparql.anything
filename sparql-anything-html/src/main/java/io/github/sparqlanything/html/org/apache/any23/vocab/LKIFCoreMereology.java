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
 * The mereology module defines mereological concepts such as parts and wholes, and typical mereological relations such
 * as part of, component of, containment, membership etc.
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreMereology extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/mereology.owl#";

    private static LKIFCoreMereology instance;

    public static LKIFCoreMereology getInstance() {
        if (instance == null) {
            instance = new LKIFCoreMereology();
        }
        return instance;
    }

    /////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/mereology.owl */
    /////////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Atom = createClass(NS, "Atom");
    public final IRI Composition = createClass(NS, "Composition");
    public final IRI Pair = createClass(NS, "Pair");
    public final IRI Part = createClass(NS, "Part");
    public final IRI Whole = createClass(NS, "Whole");

    // RESOURCES

    // PROPERTIES
    public final IRI component = createProperty(NS, "component");
    public final IRI component_of = createProperty(NS, "component_of");
    public final IRI composed_of = createProperty(NS, "composed_of");
    public final IRI composes = createProperty(NS, "composes");
    public final IRI contained_in = createProperty(NS, "contained_in");
    public final IRI contains = createProperty(NS, "contains");
    public final IRI direct_part = createProperty(NS, "direct_part");
    public final IRI direct_part_of = createProperty(NS, "direct_part_of");
    public final IRI member = createProperty(NS, "member");
    public final IRI member_of = createProperty(NS, "member_of");
    public final IRI part = createProperty(NS, "part");
    public final IRI part_of = createProperty(NS, "part_of");
    public final IRI strict_part = createProperty(NS, "strict_part");
    public final IRI strict_part_of = createProperty(NS, "strict_part_of");

    private LKIFCoreMereology() {
        super(NS);
    }

}
