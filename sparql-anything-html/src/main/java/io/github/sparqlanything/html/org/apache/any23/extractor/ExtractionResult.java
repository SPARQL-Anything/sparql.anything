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

package io.github.sparqlanything.html.org.apache.any23.extractor;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

/**
 * Interface defining the methods that a representation of an extraction result must have.
 */
public interface ExtractionResult extends IssueReport {

    /**
     * Writes a triple. Parameters can be null, then the triple will be silently ignored.
     *
     * @param s
     *            subject
     * @param p
     *            predicate
     * @param o
     *            object
     * @param g
     *            graph
     */
    void writeTriple(Resource s, IRI p, Value o, IRI g);

    /**
     * Write a triple. Parameters can be null, then the triple will be silently ignored.
     *
     * @param s
     *            subject
     * @param p
     *            predicate
     * @param o
     *            object
     */
    void writeTriple(Resource s, IRI p, Value o);

    /**
     * Write a namespace.
     *
     * @param prefix
     *            the prefix of the namespace
     * @param IRI
     *            the long IRI identifying the namespace
     */
    void writeNamespace(String prefix, String IRI);

    /**
     * <p>
     * Close the result.
     * </p>
     * Extractors should close their results as soon as possible, but don't have to, the environment will close any
     * remaining ones. Implementations should be robust against multiple close() invocations.
     */
    void close();

    /**
     * Open a result nested in the current one.
     *
     * @param extractionContext
     *            the context to be used to open the sub result.
     *
     * @return the instance of the nested extraction result.
     */
    ExtractionResult openSubResult(ExtractionContext extractionContext);

}
