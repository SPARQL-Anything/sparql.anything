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

package io.github.sparqlanything.html.org.semarglproject.sink;

import io.github.sparqlanything.html.org.semarglproject.sink.TripleSink;

/**
 * Interface for quad consuming
 */
public interface QuadSink extends TripleSink {

    /**
     * Callback for handling triples with non literal object
     * @param subj subject's IRI or BNode name
     * @param pred predicate's IRI
     * @param obj object's IRI or BNode name
     * @param graph graph's IRI
     */
    void addNonLiteral(String subj, String pred, String obj, String graph);

    /**
     * Callback for handling triples with plain literal objects
     * @param subj subject's IRI or BNode name
     * @param pred predicate's IRI
     * @param content unescaped string representation of content
     * @param lang content's lang, can be null if no language specified
     * @param graph graph's IRI
     */
    void addPlainLiteral(String subj, String pred, String content, String lang, String graph);

    /**
     * Callback for handling triples with typed literal objects
     * @param subj subject's IRI or BNode name
     * @param pred predicate's IRI
     * @param content unescaped string representation of content
     * @param type literal datatype's IRI
     * @param graph graph's IRI
     */
    void addTypedLiteral(String subj, String pred, String content, String type, String graph);

}
