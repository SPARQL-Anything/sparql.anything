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
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A {@link TripleHandler} multi decorator, that wraps zero or more other triple handlers and dispatches all events to
 * each of them.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CompositeTripleHandler implements TripleHandler {

    private Collection<TripleHandler> children = new ArrayList<TripleHandler>();

    /**
     * Constructor with empty decorated list.
     */
    public CompositeTripleHandler() {
        this(Collections.<TripleHandler> emptyList());
    }

    /**
     * Constructor with initial list of decorated handlers.
     *
     * @param children
     *            list of decorated handlers.
     */
    public CompositeTripleHandler(Collection<TripleHandler> children) {
        this.children.addAll(children);
    }

    /**
     * Adds a decorated handler.
     *
     * @param child
     *            the decorated handler.
     */
    public void addChild(TripleHandler child) {
        children.add(child);
    }

    public Collection<TripleHandler> getChilds() {
        return children;
    }

    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        for (TripleHandler handler : children) {
            handler.startDocument(documentIRI);
        }
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        for (TripleHandler handler : children) {
            handler.openContext(context);
        }
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        for (TripleHandler handler : children) {
            handler.closeContext(context);
        }
    }

    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        for (TripleHandler handler : children) {
            handler.receiveTriple(s, p, o, g, context);
        }
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        for (TripleHandler handler : children) {
            handler.receiveNamespace(prefix, uri, context);
        }
    }

    public void close() throws TripleHandlerException {
        for (TripleHandler handler : children) {
            handler.close();
        }
    }

    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        for (TripleHandler handler : children) {
            handler.endDocument(documentIRI);
        }
    }

    public void setContentLength(long contentLength) {
        for (TripleHandler handler : children) {
            handler.setContentLength(contentLength);
        }
    }

}
