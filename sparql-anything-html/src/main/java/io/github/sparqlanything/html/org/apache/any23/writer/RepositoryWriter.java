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

import java.util.Locale;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandlerException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * An <i>RDF4J repository</i> triple writer.
 *
 * @see org.eclipse.rdf4j.repository.Repository
 */
public class RepositoryWriter implements TripleHandler {

    private final RepositoryConnection conn;
    private final Resource overrideContext;

    public RepositoryWriter(RepositoryConnection conn) {
        this(conn, null);
    }

    public RepositoryWriter(RepositoryConnection conn, Resource overrideContext) {
        this.conn = conn;
        this.overrideContext = overrideContext;
    }

    @Override
    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        // ignore
    }

    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
        // ignore
    }

    @Override
    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        try {
            conn.add(conn.getValueFactory().createStatement(s, p, o, g), getContextResource(context.getDocumentIRI()));
        } catch (RepositoryException ex) {
            throw new TripleHandlerException(
                    String.format(Locale.ROOT, "Error while receiving triple: %s %s %s", s, p, o), ex);
        }
    }

    @Override
    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        try {
            conn.setNamespace(prefix, uri);
        } catch (RepositoryException ex) {
            throw new TripleHandlerException(
                    String.format(Locale.ROOT, "Error while receiving namespace: %s:%s", prefix, uri), ex);
        }
    }

    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        // ignore
    }

    @Override
    public void close() throws TripleHandlerException {
        // ignore
    }

    @Override
    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        // ignore
    }

    @Override
    public void setContentLength(long contentLength) {
        // ignore
    }

    private Resource getContextResource(Resource fromExtractor) {
        if (overrideContext != null) {
            return overrideContext;
        }
        return fromExtractor;
    }
}
