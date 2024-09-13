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

import io.github.sparqlanything.html.org.apache.any23.configuration.Settings;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import io.github.sparqlanything.html.org.apache.any23.writer.*;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleWriterHandler;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterFactory;
import org.eclipse.rdf4j.rio.WriterConfig;

import java.io.BufferedWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Optional;

/**
 * A {@link TripleHandler} that writes triples to a Sesame {@link RDFWriter}, eg for serialization
 * using one of Sesame's writers.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Hans Brende (hansbrende@apache.org)
 */
public abstract class RDFWriterTripleHandler extends TripleWriterHandler implements FormatWriter {

    private RDFWriter _writer;
    private boolean writerStarted;
    private final Flushable out;
    private final TripleFormat format;

    /**
     * The annotation flag.
     */
    private boolean annotated = false;

    static TripleFormat format(RDFWriterFactory rdf4j) {
        return TripleFormat.of(rdf4j.getRDFFormat());
    }

    RDFWriterTripleHandler(RDFWriterFactory rdf4j, TripleFormat format, OutputStream out, Settings settings) {
        this.format = format;
        Optional<Charset> charset = format.getCharset();
        RDFWriter w;
        if (!charset.isPresent()) {
            this.out = out;
            w = _writer = rdf4j.getWriter(out);
        } else {
            // use buffered writer if format supports encoding
            BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(out, charset.get()));
            this.out = buf;
            w = _writer = rdf4j.getWriter(buf);
        }
        configure(w.getWriterConfig(), settings);
    }

    abstract void configure(WriterConfig config, Settings settings);

    RDFWriter writer() throws TripleHandlerException {
        RDFWriter w = _writer;
        if (w == null) {
            throw new TripleHandlerException("writer has been closed!");
        }
        if (!writerStarted) {
            writerStarted = true;
            try {
                w.startRDF();
            } catch (RDFHandlerException e) {
                throw new TripleHandlerException("Error while starting document", e);
            }
        }
        return w;
    }

    /**
     * If <code>true</code> then the produced <b>RDF</b> is annotated with the extractors used to generate the specific
     * statements.
     *
     * @return the annotation flag value.
     */
    @Override
    public boolean isAnnotated() {
        return annotated;
    }

    /**
     * Sets the <i>annotation</i> flag.
     *
     * @param f
     *            If <code>true</code> then the produced <b>RDF</b> is annotated with the extractors used to generate
     *            the specific statements.
     */
    @Override
    public void setAnnotated(boolean f) {
        annotated = f;
    }

    @Override
    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        handleComment("OUTPUT FORMAT: " + format);
    }

    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
        handleComment("BEGIN: " + context);
    }

    @Override
    public void writeTriple(Resource s, IRI p, Value o, Resource g) throws TripleHandlerException {
        try {
            writer().handleStatement(RDFUtils.quad(s, p, o, g));
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(
                    String.format(Locale.ROOT, "Error while receiving triple: %s %s %s %s", s, p, o, g), ex);
        }
    }

    @Override
    public void writeNamespace(String prefix, String uri) throws TripleHandlerException {
        try {
            writer().handleNamespace(prefix, uri);
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(
                    String.format(Locale.ROOT, "Error while receiving namespace: %s:%s", prefix, uri), ex);
        }
    }

    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        handleComment("END: " + context);
    }

    @Override
    public void close() throws TripleHandlerException {
        RDFWriter writer = _writer;
        if (writer == null) {
            return;
        }
        _writer = null;
        try {
            if (!writerStarted) {
                writer.startRDF();
            }
            writer.endRDF(); // calls flush()
        } catch (RDFHandlerException e) {
            throw new TripleHandlerException("Error closing writer", e);
        }
    }

    @Override
    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        try {
            out.flush();
        } catch (IOException e) {
            throw new TripleHandlerException("Error ending document", e);
        }
    }

    private void handleComment(String comment) throws TripleHandlerException {
        if (!annotated)
            return;
        try {
            writer().handleComment(comment);
        } catch (RDFHandlerException rdfhe) {
            throw new TripleHandlerException("Error while handing comment.", rdfhe);
        }
    }
}
