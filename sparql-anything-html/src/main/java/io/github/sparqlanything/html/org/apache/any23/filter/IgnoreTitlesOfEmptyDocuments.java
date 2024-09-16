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
import io.github.sparqlanything.html.org.apache.any23.extractor.html.TitleExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.TitleExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.filter.ExtractionContextBlocker;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandlerException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

/**
 * A {@link TripleHandler} that suppresses output of the {@link TitleExtractor} unless some other triples could be
 * parsed from the document. This is used when we don't want to have single-triple RDF documents around that contain
 * only the title triple.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class IgnoreTitlesOfEmptyDocuments implements TripleHandler {

    private final io.github.sparqlanything.html.org.apache.any23.filter.ExtractionContextBlocker blocker;

    public IgnoreTitlesOfEmptyDocuments(TripleHandler wrapped) {
        blocker = new ExtractionContextBlocker(wrapped);
    }

    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        blocker.startDocument(documentIRI);
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        blocker.openContext(context);
        if (isTitleContext(context)) {
            blocker.blockContext(context);
        }
    }

    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        if (!isTitleContext(context)) {
            blocker.unblockDocument();
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

    private boolean isTitleContext(ExtractionContext context) {
        return context.getExtractorName().equals(TitleExtractorFactory.NAME);
    }

    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        blocker.endDocument(documentIRI);
    }

    public void setContentLength(long contentLength) {
        // Ignore.
    }
}
