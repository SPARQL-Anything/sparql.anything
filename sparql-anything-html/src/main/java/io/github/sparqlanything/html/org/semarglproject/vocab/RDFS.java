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
 * Defines URIs for the RDFS vocabulary terms.
 */
public final class RDFS {

    public static final String NS = "http://www.w3.org/2000/01/rdf-schema#";

    public static final String DOMAIN = NS + "domain";
    public static final String RANGE = NS + "range";
    public static final String RESOURCE = NS + "Resource";
    public static final String LITERAL = NS + "Literal";
    public static final String DATATYPE = NS + "Datatype";
    public static final String CLASS = NS + "Class";
    public static final String SUB_CLASS_OF = NS + "subClassOf";
    public static final String SUB_PROPERTY_OF = NS + "subPropertyOf";
    public static final String MEMBER = NS + "member";
    public static final String CONTAINER = NS + "Container";
    public static final String CONTAINER_MEMBERSHIP_PROPERTY = NS + "ContainerMembershipProperty";
    public static final String COMMENT = NS + "comment";
    public static final String SEE_ALSO = NS + "seeAlso";
    public static final String IS_DEFINED_BY = NS + "isDefinedBy";
    public static final String LABEL = NS + "label";

    private RDFS() {
    }
}
