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

package io.github.sparqlanything.html.org.apache.any23.filter;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandlerException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A wrapper around a {@link TripleHandler} that can block and unblock calls to the handler, either for the entire
 * document, or for individual {@link ExtractionContext}s. A document is initially blocked and must be explicitly
 * unblocked. Contexts are initially unblocked and must be explicitly blocked. Unblocking a document unblocks all
 * contexts as well. This class it thread-safe.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExtractionContextBlocker implements TripleHandler {

    private TripleHandler wrapped;

    private Map<String, ValvedTriplePipe> contextQueues = new HashMap<String, ValvedTriplePipe>();

    private boolean documentBlocked;

    public ExtractionContextBlocker(TripleHandler wrapped) {
        this.wrapped = wrapped;
    }

    public boolean isDocBlocked() {
        return documentBlocked;
    }

    public synchronized void blockContext(ExtractionContext context) {
        if (!documentBlocked)
            return;
        try {
            contextQueues.get(context.getUniqueID()).block();
        } catch (ValvedTriplePipeException e) {
            throw new RuntimeException("Error while blocking context", e);
        }
    }

    public synchronized void unblockContext(ExtractionContext context) {
        try {
            contextQueues.get(context.getUniqueID()).unblock();
        } catch (ValvedTriplePipeException e) {
            throw new RuntimeException("Error while unblocking context", e);
        }
    }

    public synchronized void startDocument(IRI documentIRI) throws TripleHandlerException {
        wrapped.startDocument(documentIRI);
        documentBlocked = true;
    }

    public synchronized void openContext(ExtractionContext context) throws TripleHandlerException {
        contextQueues.put(context.getUniqueID(), new ValvedTriplePipe(context));
    }

    public synchronized void closeContext(ExtractionContext context) {
        // Empty. We'll close all contexts when the document is finished.
    }

    public synchronized void unblockDocument() {
        if (!documentBlocked)
            return;
        documentBlocked = false;
        for (ValvedTriplePipe pipe : contextQueues.values()) {
            try {
                pipe.unblock();
            } catch (ValvedTriplePipeException e) {
                throw new RuntimeException("Error while unblocking context", e);
            }
        }
    }

    public synchronized void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        try {
            contextQueues.get(context.getUniqueID()).receiveTriple(s, p, o, g);
        } catch (ValvedTriplePipeException e) {
            throw new TripleHandlerException(
                    String.format(Locale.ROOT, "Error while receiving triple %s %s %s", s, p, o), e);
        }
    }

    public synchronized void receiveNamespace(String prefix, String uri, ExtractionContext context)
            throws TripleHandlerException {
        try {
            contextQueues.get(context.getUniqueID()).receiveNamespace(prefix, uri);
        } catch (ValvedTriplePipeException e) {
            throw new TripleHandlerException(
                    String.format(Locale.ROOT, "Error while receiving namespace %s:%s", prefix, uri), e);
        }
    }

    public synchronized void close() throws TripleHandlerException {
        closeDocument();
        wrapped.close();
    }

    public synchronized void endDocument(IRI documentIRI) throws TripleHandlerException {
        closeDocument();
        wrapped.endDocument(documentIRI);
    }

    public void setContentLength(long contentLength) {
        // Empty.
    }

    private void closeDocument() {
        for (ValvedTriplePipe pipe : contextQueues.values()) {
            try {
                pipe.close();
            } catch (ValvedTriplePipeException e) {
                throw new RuntimeException("Error closing document", e);
            }
        }
        contextQueues.clear();
    }

    private static class ValvedTriplePipeException extends Exception {

        private ValvedTriplePipeException(String s) {
            super(s);
        }

        private ValvedTriplePipeException(Throwable throwable) {
            super(throwable);
        }

        private ValvedTriplePipeException(String s, Throwable throwable) {
            super(s, throwable);
        }

    }

    private class ValvedTriplePipe {

        private final ExtractionContext context;

        private final List<Resource> subjects = new ArrayList<Resource>();

        private final List<IRI> predicates = new ArrayList<IRI>();

        private final List<Value> objects = new ArrayList<Value>();

        private final List<IRI> graphs = new ArrayList<IRI>();

        private final List<String> prefixes = new ArrayList<String>();

        private final List<String> uris = new ArrayList<String>();

        private boolean blocked = false;

        private boolean hasReceivedTriples = false;

        ValvedTriplePipe(ExtractionContext context) {
            this.context = context;
        }

        void receiveTriple(Resource s, IRI p, Value o, IRI g) throws ValvedTriplePipeException {
            if (blocked) {
                subjects.add(s);
                predicates.add(p);
                objects.add(o);
                graphs.add(g);
            } else {
                sendTriple(s, p, o, g);
            }
        }

        void receiveNamespace(String prefix, String uri) throws ValvedTriplePipeException {
            if (blocked) {
                prefixes.add(prefix);
                uris.add(uri);
            } else {
                sendNamespace(prefix, uri);
            }
        }

        void block() throws ValvedTriplePipeException {
            if (blocked)
                return;
            blocked = true;
        }

        void unblock() throws ValvedTriplePipeException {
            if (!blocked)
                return;
            blocked = false;
            for (int i = 0; i < prefixes.size(); i++) {
                sendNamespace(prefixes.get(i), uris.get(i));
            }
            for (int i = 0; i < subjects.size(); i++) {
                sendTriple(subjects.get(i), predicates.get(i), objects.get(i), graphs.get(i));
            }
        }

        void close() throws ValvedTriplePipeException {
            if (hasReceivedTriples) {
                try {
                    wrapped.closeContext(context);
                } catch (TripleHandlerException e) {
                    throw new ValvedTriplePipeException("Error while closing the triple hanlder", e);
                }
            }
        }

        private void sendTriple(Resource s, IRI p, Value o, IRI g) throws ValvedTriplePipeException {
            if (!hasReceivedTriples) {
                try {
                    wrapped.openContext(context);
                } catch (TripleHandlerException e) {
                    throw new ValvedTriplePipeException("Error while opening the triple handler", e);
                }
                hasReceivedTriples = true;
            }
            try {
                wrapped.receiveTriple(s, p, o, g, context);
            } catch (TripleHandlerException e) {
                throw new ValvedTriplePipeException("Error while opening the triple handler", e);
            }
        }

        private void sendNamespace(String prefix, String uri) throws ValvedTriplePipeException {
            if (!hasReceivedTriples) {
                try {
                    wrapped.openContext(context);
                } catch (TripleHandlerException e) {
                    throw new ValvedTriplePipeException("Error while sending the namespace", e);
                }
                hasReceivedTriples = true;
            }
            try {
                wrapped.receiveNamespace(prefix, uri, context);
            } catch (TripleHandlerException e) {
                throw new ValvedTriplePipeException("Error while receiving the namespace", e);
            }
        }
    }

}
