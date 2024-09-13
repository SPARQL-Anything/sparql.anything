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
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandlerException;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

/**
 * This class connects a {@link TripleHandler} to a {@link TripleWriter} by writing received data.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public abstract class TripleWriterHandler implements TripleHandler, TripleWriter {

    /**
     * Writers may override this method to handle a "receiveTriple" extraction event. The default implementation calls:
     *
     * <pre>
     *     {@code this.writeTriple(s, p, o, context == null || g != null ? g : context.getDocumentIRI())}
     * </pre>
     *
     * @param s
     *            the subject received
     * @param p
     *            the predicate received
     * @param o
     *            the object received
     * @param g
     *            the graph name received, or null
     * @param context
     *            the extraction context
     *
     * @throws TripleHandlerException
     *             if there was an error responding to a received triple
     */
    @Override
    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        writeTriple(s, p, o, context == null || g != null ? g : context.getDocumentIRI());
    }

    /**
     * Writers may override this method to handle a "receiveNamespace" extraction event. The default implementation
     * calls:
     *
     * <pre>
     *     {@code this.writeNamespace(prefix, uri)}
     * </pre>
     *
     * @param prefix
     *            namespace prefix.
     * @param uri
     *            namespace <i>IRI</i>.
     * @param context
     *            the extraction context
     *
     * @throws TripleHandlerException
     *             if there was an error responding to the received namepsace.
     */
    @Override
    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        writeNamespace(prefix, uri);
    }

    /**
     * Writers may override this method to handle a "startDocument" extraction event. The default implementation does
     * nothing.
     *
     * @param documentIRI
     *            the name of the document that was started
     *
     * @throws TripleHandlerException
     *             if an error occurred while responding to a "startDocument" extraction event.
     */
    @Override
    public void startDocument(IRI documentIRI) throws TripleHandlerException {
    }

    /**
     * Writers may override this method to handle an "openContext" extraction event. The default implementation does
     * nothing.
     *
     * @param context
     *            the context that was opened
     *
     * @throws TripleHandlerException
     *             if an error occurred while responding to a "startDocument" extraction event.
     */
    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
    }

    /**
     * Writers may override this method to handle a "closeContext" extraction event. The default implementation does
     * nothing.
     *
     * @param context
     *            the context to be closed.
     *
     * @throws TripleHandlerException
     *             if an error occurred while responding to a "closeContext" extraction event.
     */
    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
    }

    /**
     * Writers may override this method to handle an "endDocument" extraction event. The default implementation does
     * nothing.
     *
     * @param documentIRI
     *            the document IRI.
     *
     * @throws TripleHandlerException
     *             if an error occurred while responding to a "endDocument" extraction event.
     */
    @Override
    public void endDocument(IRI documentIRI) throws TripleHandlerException {
    }

    /**
     * Writers may override this method to handle a "setContentLength" extraction event. The default implementation does
     * nothing.
     *
     * @param contentLength
     *            length of the content being processed.
     */
    @Override
    public void setContentLength(long contentLength) {
    }

}
