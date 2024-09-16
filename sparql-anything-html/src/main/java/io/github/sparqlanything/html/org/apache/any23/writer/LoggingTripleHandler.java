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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import io.github.sparqlanything.html.org.apache.any23.util.StringUtils;

/**
 * Triple handler decorator useful for logging purposes.
 */
public class LoggingTripleHandler implements TripleHandler {

    /**
     * Decorated.
     */
    private final TripleHandler underlyingHandler;

    private final Map<String, Integer> contextTripleMap = new HashMap<String, Integer>();
    private long startTime = 0;
    private long contentLength = 0;
    private final PrintWriter destination;

    public LoggingTripleHandler(TripleHandler tripleHandler, PrintWriter destination) {
        if (tripleHandler == null) {
            throw new NullPointerException("tripleHandler cannot be null.");
        }
        if (destination == null) {
            throw new NullPointerException("destination cannot be null.");
        }
        underlyingHandler = tripleHandler;
        this.destination = destination;

        printHeader(destination);
    }

    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        underlyingHandler.startDocument(documentIRI);
        startTime = System.currentTimeMillis();
    }

    public void close() throws TripleHandlerException {
        underlyingHandler.close();
        destination.flush();
        destination.close();
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        underlyingHandler.closeContext(context);
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        underlyingHandler.openContext(context);
    }

    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        underlyingHandler.receiveTriple(s, p, o, g, context);
        Integer i = contextTripleMap.get(context.getExtractorName());
        if (i == null)
            i = 0;
        contextTripleMap.put(context.getExtractorName(), (i + 1));
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        underlyingHandler.receiveNamespace(prefix, uri, context);
    }

    @Override
    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        underlyingHandler.endDocument(documentIRI);
        long elapsedTime = System.currentTimeMillis() - startTime;
        final AtomicBoolean success = new AtomicBoolean(true);

        StringBuilder sb = new StringBuilder("[ ");
        String[] parsers = contextTripleMap.entrySet().stream().map(e -> {
            if (e.getValue() > 0) {
                success.set(true);
            }
            return String.format(Locale.ROOT, "%s:%d", e.getKey(), e.getValue());
        }).collect(Collectors.toList()).toArray(new String[] {});
        sb.append(StringUtils.join(", ", parsers));
        sb.append(" ]");
        destination.println(
                documentIRI + "\t" + contentLength + "\t" + elapsedTime + "\t" + success.get() + "\t" + sb.toString());
        contextTripleMap.clear();
    }

    public void setContentLength(long contentLength) {
        underlyingHandler.setContentLength(contentLength);
        this.contentLength = contentLength;
    }

    private void printHeader(PrintWriter writer) {
        writer.println("# Document-IRI\tContent-Length\tElapsed-Time\tSuccess\tExtractors:Triples");
    }
}
