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
 * Defines URIs for the RDFa vocabulary terms and RDFa attributes and versions.
 */
public final class RDFa {

    public static final String NS = "http://www.w3.org/ns/rdfa#";

    public static final short VERSION_10 = 1;
    public static final short VERSION_11 = 2;

    public static final String ABOUT_ATTR = "about";
    public static final String CONTENT_ATTR = "content";
    public static final String DATATYPE_ATTR = "datatype";
    public static final String HREF_ATTR = "href";
    public static final String ID_ATTR = "id";
    public static final String INLIST_ATTR = "inlist";
    public static final String PREFIX_ATTR = "prefix";
    public static final String PROFILE_ATTR = "profile";
    public static final String PROPERTY_ATTR = "property";
    public static final String REL_ATTR = "rel";
    public static final String RESOURCE_ATTR = "resource";
    public static final String REV_ATTR = "rev";
    public static final String ROLE_ATTR = "role";
    public static final String SRC_ATTR = "src";
    public static final String TYPEOF_ATTR = "typeof";
    public static final String VOCAB_ATTR = "vocab";

    public static final String CONTEXT = NS + "context";
    public static final String WARNING = NS + "Warning";
    public static final String PREFIX_REDEFINITION = NS + "PrefixRedefinition";
    public static final String UNRESOLVED_CURIE = NS + "UnresolvedCURIE";
    public static final String UNRESOLVED_TERM = NS + "UnresolvedTerm";
    public static final String ERROR = NS + "Error";
    public static final String USES_VOCABULARY = NS + "usesVocabulary";

    public static final String COPY = NS + "copy";
    public static final String PATTERN = NS + "Pattern";

    private RDFa() {
    }
}
