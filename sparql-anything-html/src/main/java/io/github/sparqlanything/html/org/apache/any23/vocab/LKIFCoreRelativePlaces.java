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
 * The place module partially implements the theory of relative places (Donnelly, 2005) in OWL DL.
 * </p>
 *
 * @author lewismc
 *
 */
public class LKIFCoreRelativePlaces extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/relative-places.owl#";

    private static LKIFCoreRelativePlaces instance;

    public static LKIFCoreRelativePlaces getInstance() {
        if (instance == null) {
            instance = new LKIFCoreRelativePlaces();
        }
        return instance;
    }

    //////////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/relative-places.owl */
    //////////////////////////////////////////////////////////////////
    // CLASSES
    public final IRI Absolute_Place = createClass(NS, "Absolute_Place");
    public final IRI Comprehensive_Place = createClass(NS, "Comprehensive_Place");
    public final IRI Location_Complex = createClass(NS, "Location_Complex");
    public final IRI Place = createClass(NS, "Place");
    public final IRI Relative_Place = createClass(NS, "Relative_Place");

    // RESOURCES

    // PROPERTIES
    public final IRI abut = createProperty(NS, "abut");
    public final IRI connect = createProperty(NS, "connect");
    public final IRI cover = createProperty(NS, "cover");
    public final IRI covered_by = createProperty(NS, "covered_by");
    public final IRI exactly_coincide = createProperty(NS, "exactly_coincide");
    public final IRI externally_connect = createProperty(NS, "externally_connect");
    public final IRI in = createProperty(NS, "in");
    public final IRI location_complex = createProperty(NS, "location_complex");
    public final IRI location_complex_for = createProperty(NS, "location_complex_for");
    public final IRI meet = createProperty(NS, "meet");
    public final IRI overlap = createProperty(NS, "overlap");
    public final IRI partially_coincide = createProperty(NS, "partially_coincide");
    public final IRI relatively_fixed = createProperty(NS, "relatively_fixed");
    public final IRI spatial_reference = createProperty(NS, "spatial_reference");
    public final IRI spatial_relation = createProperty(NS, "spatial_relation");

    private LKIFCoreRelativePlaces() {
        super(NS);
    }

}
