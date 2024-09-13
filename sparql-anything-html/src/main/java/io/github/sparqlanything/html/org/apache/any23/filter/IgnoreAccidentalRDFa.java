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
import io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.RDFaExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.filter.ExtractionContextBlocker;
import io.github.sparqlanything.html.org.apache.any23.vocab.XHTML;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandlerException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

/**
 * A {@link TripleHandler} that suppresses output of the RDFa parser if the document only contains "accidental" RDFa,
 * like stylesheet links and other non-RDFa uses of HTML's
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class IgnoreAccidentalRDFa implements TripleHandler {

    private static final XHTML vXHTML = XHTML.getInstance();

    private final ExtractionContextBlocker blocker;

    private final boolean alwaysSuppressCSSTriples;

    /**
     * Constructor.
     *
     * @param wrapped
     *            the decorated triple handler.
     * @param alwaysSuppressCSSTriples
     *            if <code>true</code> the <i>CSS</i> triples will be always suppressed even if the document is not
     *            empty. If <code>false</code> then the <i>CSS</i> triples will be suppressed only if document is empty.
     */
    public IgnoreAccidentalRDFa(TripleHandler wrapped, boolean alwaysSuppressCSSTriples) {
        this.blocker = new ExtractionContextBlocker(wrapped);
        this.alwaysSuppressCSSTriples = alwaysSuppressCSSTriples;
    }

    public IgnoreAccidentalRDFa(TripleHandler wrapped) {
        this(wrapped, false);
    }

    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        blocker.startDocument(documentIRI);
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        blocker.openContext(context);
        if (isRDFaContext(context)) {
            blocker.blockContext(context);
        }
    }

    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        // Suppress stylesheet triples.
        if (alwaysSuppressCSSTriples && p.stringValue().equals(vXHTML.stylesheet.stringValue())) {
            return;
        }
        if (isRDFaContext(context)) {
            blocker.unblockContext(context);
        }
        blocker.receiveTriple(s, p, o, g, context);
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        blocker.receiveNamespace(prefix, uri, context);
    }

    public void closeContext(ExtractionContext context) {
        blocker.closeContext(context);
    }

    public void close() throws TripleHandlerException {
        blocker.close();
    }

    private boolean isRDFaContext(ExtractionContext context) {
        return context.getExtractorName().equals(RDFaExtractorFactory.NAME);
    }

    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        blocker.endDocument(documentIRI);
    }

    public void setContentLength(long contentLength) {
        // Ignore.
    }
}
