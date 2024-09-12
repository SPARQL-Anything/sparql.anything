/**
 * Copyright 2012-2013 the Semargl contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.sparqlanything.html.org.semarglproject.vocab;

/**
 * Defines URIs for the OWL vocabulary terms.
 */
public final class OWL {
    public static final String NS = "http://www.w3.org/2002/07/owl#";

    // OWL 2 RDF-Based Vocabulary

    public static final String ALL_DIFFERENT = NS + "AllDifferent";
    public static final String ALL_DISJOINT_CLASSES = NS + "AllDisjointClasses";
    public static final String ALL_DISJOINT_PROPERTIES = NS + "AllDisjointProperties";
    public static final String ALL_VALUES_FROM = NS + "allValuesFrom";
    public static final String ANNOTATED_PROPERTY = NS + "annotatedProperty";
    public static final String ANNOTATED_SOURCE = NS + "annotatedSource";
    public static final String ANNOTATED_TARGET = NS + "annotatedTarget";
    public static final String ANNOTATION = NS + "Annotation";
    public static final String ANNOTATION_PROPERTY = NS + "AnnotationProperty";
    public static final String ASSERTION_PROPERTY = NS + "assertionProperty";
    public static final String ASYMMETRIC_PROPERTY = NS + "AsymmetricProperty";
    public static final String AXIOM = NS + "Axiom";
    public static final String BACKWARD_COMPATIBLE_WITH = NS + "backwardCompatibleWith";
    public static final String BOTTOM_DATA_PROPERTY = NS + "bottomDataProperty";
    public static final String BOTTOM_OBJECT_PROPERTY = NS + "bottomObjectProperty";
    public static final String CARDINALITY = NS + "cardinality";
    public static final String CLASS = NS + "Class";
    public static final String COMPLEMENT_OF = NS + "complementOf";
    public static final String DATA_RANGE = NS + "DataRange";
    public static final String DATATYPE_COMPLEMENT_OF = NS + "datatypeComplementOf";
    public static final String DATATYPE_PROPERTY = NS + "DatatypeProperty";
    public static final String DEPRECATED = NS + "deprecated";
    public static final String DEPRECATED_CLASS = NS + "DeprecatedClass";
    public static final String DEPRECATED_PROPERTY = NS + "DeprecatedProperty";
    public static final String DIFFERENT_FROM = NS + "differentFrom";
    public static final String DISJOINT_UNION_OF = NS + "disjointUnionOf";
    public static final String DISJOINT_WITH = NS + "disjointWith";
    public static final String DISTINCT_MEMBERS = NS + "distinctMembers";
    public static final String EQUIVALENT_CLASS = NS + "equivalentClass";
    public static final String EQUIVALENT_PROPERTY = NS + "equivalentProperty";
    public static final String FUNCTIONAL_PROPERTY = NS + "FunctionalProperty";
    public static final String HAS_KEY = NS + "hasKey";
    public static final String HAS_SELF = NS + "hasSelf";
    public static final String HAS_VALUE = NS + "hasValue";
    public static final String IMPORTS = NS + "imports";
    public static final String INCOMPATIBLE_WITH = NS + "incompatibleWith";
    public static final String INTERSECTION_OF = NS + "intersectionOf";
    public static final String INVERSE_FUNCTIONAL_PROPERTY = NS + "InverseFunctionalProperty";
    public static final String INVERSE_OF = NS + "inverseOf";
    public static final String IRREFLEXIVE_PROPERTY = NS + "IrreflexiveProperty";
    public static final String MAX_CARDINALITY = NS + "maxCardinality";
    public static final String MAX_QUALIFIED_CARDINALITY = NS + "maxQualifiedCardinality";
    public static final String MEMBERS = NS + "members";
    public static final String MIN_CARDINALITY = NS + "minCardinality";
    public static final String MIN_QUALIFIED_CARDINALITY = NS + "minQualifiedCardinality";
    public static final String NAMED_INDIVIDUAL = NS + "NamedIndividual";
    public static final String NEGATIVE_PROPERTY_ASSERTION = NS + "NegativePropertyAssertion";
    public static final String NOTHING = NS + "Nothing";
    public static final String OBJECT_PROPERTY = NS + "ObjectProperty";
    public static final String ON_CLASS = NS + "onClass";
    public static final String ON_DATA_RANGE = NS + "onDataRange";
    public static final String ON_DATATYPE = NS + "onDatatype";
    public static final String ONE_OF = NS + "oneOf";
    public static final String ON_PROPERTY = NS + "onProperty";
    public static final String ON_PROPERTIES = NS + "onProperties";
    public static final String ONTOLOGY = NS + "Ontology";
    public static final String ONTOLOGY_PROPERTY = NS + "OntologyProperty";
    public static final String PRIOR_VERSION = NS + "priorVersion";
    public static final String PROPERTY_CHAIN_AXIOM = NS + "propertyChainAxiom";
    public static final String PROPERTY_DISJOINT_WITH = NS + "propertyDisjointWith";
    public static final String QUALIFIED_CARDINALITY = NS + "qualifiedCardinality";
    public static final String REFLEXIVE_PROPERTY = NS + "ReflexiveProperty";
    public static final String RESTRICTION = NS + "Restriction";
    public static final String SAME_AS = NS + "sameAs";
    public static final String SOME_VALUES_FROM = NS + "someValuesFrom";
    public static final String SOURCE_INDIVIDUAL = NS + "sourceIndividual";
    public static final String SYMMETRIC_PROPERTY = NS + "SymmetricProperty";
    public static final String TARGET_INDIVIDUAL = NS + "targetIndividual";
    public static final String TARGET_VALUE = NS + "targetValue";
    public static final String THING = NS + "Thing";
    public static final String TOP_DATA_PROPERTY = NS + "topDataProperty";
    public static final String TOP_OBJECT_PROPERTY = NS + "topObjectProperty";
    public static final String TRANSITIVE_PROPERTY = NS + "TransitiveProperty";
    public static final String UNION_OF = NS + "unionOf";
    public static final String VERSION_INFO = NS + "versionInfo";
    public static final String VERSION_IRI = NS + "versionIRI";
    public static final String WITH_RESTRICTIONS = NS + "withRestrictions";

    // Datatypes of the OWL 2 RDF-Based Semantics

    public static final String RATIONAL = NS + "rational";
    public static final String REAL = NS + "real";

    private OWL() {
    }
}
