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

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

/**
 * Defines a document based triple handler.
 */
public interface TripleHandler extends AutoCloseable {

    void startDocument(IRI documentIRI) throws TripleHandlerException;

    /**
     * Informs the handler that a new context has been established. Contexts are not guaranteed to receive any triples,
     * so they might be closed without any triple.
     *
     * @param context
     *            an instantiated {@link io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext}
     *
     * @throws TripleHandlerException
     *             if there is an errr opening the {@link io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext}
     */
    void openContext(ExtractionContext context) throws TripleHandlerException;

    /**
     * Invoked with a currently open context, notifies the detection of a triple.
     *
     * @param s
     *            triple subject, cannot be <code>null</code>.
     * @param p
     *            triple predicate, cannot be <code>null</code>.
     * @param o
     *            triple object, cannot be <code>null</code>.
     * @param g
     *            triple graph, can be <code>null</code>.
     * @param context
     *            extraction context.
     *
     * @throws TripleHandlerException
     *             if there is an error receiving the triple.
     */
    void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context) throws TripleHandlerException;

    /**
     * Invoked with a currently open context, notifies the detection of a namespace.
     *
     * @param prefix
     *            namespace prefix.
     * @param uri
     *            namespace <i>IRI</i>.
     * @param context
     *            namespace context.
     *
     * @throws TripleHandlerException
     *             if there is an error receiving the namespace.
     */
    void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException;

    /**
     * Informs the handler that no more triples will come from a previously opened context. All contexts are guaranteed
     * to be closed before the final close(). The document context for each document is guaranteed to be closed after
     * all local contexts of that document.
     *
     * @param context
     *            the context to be closed.
     *
     * @throws TripleHandlerException
     *             if there is an error closing the {@link io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext}.
     */
    void closeContext(ExtractionContext context) throws TripleHandlerException;

    /**
     * Informs the handler that the end of the document has been reached.
     *
     * @param documentIRI
     *            document IRI.
     *
     * @throws TripleHandlerException
     *             if there is an error ending the document.
     */
    void endDocument(IRI documentIRI) throws TripleHandlerException;

    /**
     * Sets the length of the content to be processed.
     *
     * @param contentLength
     *            length of the content being processed.
     */
    void setContentLength(long contentLength);

    /**
     * Will be called last and exactly once.
     *
     * @throws TripleHandlerException
     *             if there is an error closing the {@link io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler} implementation.
     */
    void close() throws TripleHandlerException;

}
