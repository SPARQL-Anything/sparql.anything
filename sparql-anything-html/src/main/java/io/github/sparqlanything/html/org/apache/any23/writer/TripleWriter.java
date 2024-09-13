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

package io.github.sparqlanything.html.org.apache.any23.writer;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

/**
 * Base interface for triple writers that don't need an extraction context to write triples
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public interface TripleWriter extends AutoCloseable {

    /**
     * Writes a triple and, optionally, a graph resource name.
     *
     * @param s
     *            the subject to write
     * @param p
     *            the predicate to write
     * @param o
     *            the object to write
     * @param g
     *            the graph name to write, or null
     *
     * @throws TripleHandlerException
     *             if there is an error writing the triple
     */
    void writeTriple(Resource s, IRI p, Value o, Resource g) throws TripleHandlerException;

    /**
     * Writes a prefix-namespace mapping. <br>
     * <b>NOTE:</b> this method should be called <b>before</b> writing out any triples. Calling this method <b>after</b>
     * writing out a triple may result in the prefix-namespace mapping being ignored.
     *
     * @param prefix
     *            the namespace prefix
     * @param uri
     *            the namespace uri
     *
     * @throws TripleHandlerException
     *             if there was an error writing out the prefix-namespace mapping
     */
    void writeNamespace(String prefix, String uri) throws TripleHandlerException;

    /**
     * Releases resources associated with this {@link TripleWriter}, and flushes (but by default does not close) any
     * underlying {@link java.io.OutputStream}s. Future invocations of methods of this writer produce <b>undefined
     * behavior</b> after this method has been called.
     *
     * @throws TripleHandlerException
     *             if there was an error closing this {@link TripleWriter}
     */
    @Override
    void close() throws TripleHandlerException;

}
