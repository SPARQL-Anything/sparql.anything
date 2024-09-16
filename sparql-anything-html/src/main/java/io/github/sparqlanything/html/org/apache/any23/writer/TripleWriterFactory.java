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
import org.eclipse.rdf4j.common.lang.FileFormat;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.OutputStream;

/**
 * Base interface for constructors of {@link TripleHandler} implementations that write to an {@link OutputStream} using
 * a particular {@link FileFormat}.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public interface TripleWriterFactory extends BaseWriterFactory<OutputStream> {

    /**
     * @deprecated since 2.3. Use {@link #getTripleFormat()} instead.
     */
    @Override
    @Deprecated
    default RDFFormat getRdfFormat() {
        return getTripleFormat().toRDFFormat();
    }

    /**
     * @return the format used to write to {@link OutputStream}s
     */
    TripleFormat getTripleFormat();

    /**
     * @deprecated since 2.3. Use {@link #getTripleFormat()}.{@link TripleFormat#getMimeType() getMimeType()} instead.
     */
    @Override
    @Deprecated
    default String getMimeType() {
        return getTripleFormat().getMimeType();
    }

    /**
     * @deprecated since 2.3. Use {@link #getTripleWriter(OutputStream, Settings)} instead.
     */
    @Override
    @Deprecated
    default FormatWriter getRdfWriter(OutputStream os) {
        TripleHandler th = getTripleWriter(os, Settings.of());
        return th instanceof FormatWriter ? (FormatWriter) th : new FormatWriter() {
            @Override
            public boolean isAnnotated() {
                return false;
            }

            @Override
            public void setAnnotated(boolean f) {
            }

            @Override
            public void startDocument(IRI documentIRI) throws TripleHandlerException {
                th.startDocument(documentIRI);
            }

            @Override
            public void openContext(ExtractionContext context) throws TripleHandlerException {
                th.openContext(context);
            }

            @Override
            public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
                    throws TripleHandlerException {
                th.receiveTriple(s, p, o, g, context);
            }

            @Override
            public void receiveNamespace(String prefix, String uri, ExtractionContext context)
                    throws TripleHandlerException {
                th.receiveNamespace(prefix, uri, context);
            }

            @Override
            public void closeContext(ExtractionContext context) throws TripleHandlerException {
                th.closeContext(context);
            }

            @Override
            public void endDocument(IRI documentIRI) throws TripleHandlerException {
                th.endDocument(documentIRI);
            }

            @Override
            public void setContentLength(long contentLength) {
                th.setContentLength(contentLength);
            }

            @Override
            public void close() throws TripleHandlerException {
                th.close();
            }
        };
    }

    /**
     *
     * @return the settings supported by writers produced by this factory
     */
    @Override
    Settings getSupportedSettings();

    /**
     * @param out
     *            the {@link OutputStream} to write to
     * @param settings
     *            the settings with which to configure the writer
     *
     * @return a {@link TripleHandler} which writes to the specified {@link OutputStream}
     *
     * @throws NullPointerException
     *             if the output stream or settings is null
     * @throws IllegalArgumentException
     *             if the settings are not correctly configured
     */
    @Override
    TripleHandler getTripleWriter(OutputStream out, Settings settings);

}
