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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link TripleHandler} decorator useful to perform benchmarking.
 */
public class BenchmarkTripleHandler implements TripleHandler {

    /**
     * Decorated.
     */
    private TripleHandler underlyingHandler;

    /**
     * Collected statistics.
     */
    private final Map<String, StatObject> stats;

    /**
     * Constructor.
     *
     * @param tripleHandler
     *            a configured {@link TripleHandler}
     */
    public BenchmarkTripleHandler(TripleHandler tripleHandler) {
        if (tripleHandler == null) {
            throw new NullPointerException("tripleHandler cannot be null.");
        }
        underlyingHandler = tripleHandler;
        stats = new HashMap<String, StatObject>();
        stats.put("SUM", new StatObject());
    }

    /**
     * Returns the report as a human readable string.
     *
     * @return a human readable report.
     */
    public String report() {
        StringBuilder sb = new StringBuilder();
        StatObject sum = stats.get("SUM");

        sb.append("\n>Summary: ");
        sb.append("\n   -total calls: ").append(sum.methodCalls);
        sb.append("\n   -total triples: ").append(sum.triples);
        sb.append("\n   -total runtime: ").append(sum.runtime).append(" ms!");
        if (sum.runtime != 0)
            sb.append("\n   -tripls/ms: ").append(sum.triples.get() / sum.runtime);
        if (sum.methodCalls.get() != 0)
            sb.append("\n   -ms/calls: ").append(sum.runtime / sum.methodCalls.get());

        stats.remove("SUM");

        for (Entry<String, StatObject> ent : stats.entrySet()) {
            sb.append("\n>Extractor: ").append(ent.getKey());
            sb.append("\n   -total calls: ").append(ent.getValue().methodCalls);
            sb.append("\n   -total triples: ").append(ent.getValue().triples);
            sb.append("\n   -total runtime: ").append(ent.getValue().runtime).append(" ms!");
            if (ent.getValue().runtime != 0)
                sb.append("\n   -tripls/ms: ").append(ent.getValue().triples.get() / ent.getValue().runtime);
            if (ent.getValue().methodCalls.get() != 0)
                sb.append("\n   -ms/calls: ").append(ent.getValue().runtime / ent.getValue().methodCalls.get());

        }

        return sb.toString();
    }

    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        underlyingHandler.startDocument(documentIRI);
    }

    public void close() throws TripleHandlerException {
        underlyingHandler.close();
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        if (stats.containsKey(context.getExtractorName())) {
            stats.get(context.getExtractorName()).interimStop();
            stats.get("SUM").interimStop();
        }
        underlyingHandler.closeContext(context);
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        if (!stats.containsKey(context.getExtractorName())) {
            stats.put(context.getExtractorName(), new StatObject());
        }
        stats.get(context.getExtractorName()).methodCalls.incrementAndGet();
        stats.get(context.getExtractorName()).interimStart();
        stats.get("SUM").methodCalls.incrementAndGet();
        stats.get("SUM").interimStart();
        underlyingHandler.openContext(context);
    }

    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        if (!stats.containsKey(context.getExtractorName())) {
            stats.put(context.getExtractorName(), new StatObject());
        }
        stats.get(context.getExtractorName()).triples.incrementAndGet();
        stats.get("SUM").triples.incrementAndGet();
        underlyingHandler.receiveTriple(s, p, o, g, context);
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        underlyingHandler.receiveNamespace(prefix, uri, context);
    }

    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        underlyingHandler.endDocument(documentIRI);
    }

    public void setContentLength(long contentLength) {
        underlyingHandler.setContentLength(contentLength);
    }

    /**
     * A single statistics.
     */
    private static class StatObject {

        AtomicInteger methodCalls = new AtomicInteger(0);
        AtomicInteger triples = new AtomicInteger(0);
        long runtime = 0;
        long intStart = 0;

        /**
         * Takes the start time.
         */
        public void interimStart() {
            intStart = System.currentTimeMillis();
        }

        /**
         * Takes the stop time.
         */
        public void interimStop() {
            runtime += (System.currentTimeMillis() - intStart);
            intStart = 0;
        }
    }

}
