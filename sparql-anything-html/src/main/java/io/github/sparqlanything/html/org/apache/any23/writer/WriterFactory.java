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

import java.io.OutputStream;

import io.github.sparqlanything.html.org.apache.any23.configuration.Settings;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * The superinterface of all {@link TripleHandler} factory interfaces. Do not implement this interface directly.
 * Instead, implement one of the subinterfaces {@link TripleWriterFactory} or {@link DecoratingWriterFactory}.
 *
 * @author Peter Ansell (p_ansell@yahoo.com)
 * @author Hans Brende (hansbrende@apache.org)
 */
public interface WriterFactory {

    /**
     * @deprecated since 2.3. Use {@link TripleWriterFactory#getTripleFormat()} instead.
     *
     * @return the {@link org.eclipse.rdf4j.rio.RDFFormat} being handled
     */
    @Deprecated
    RDFFormat getRdfFormat();

    String getIdentifier();

    /**
     * @deprecated since 2.3. Use {@link TripleWriterFactory#getTripleFormat()}.{@link TripleFormat#getMimeType()
     *             getMimeType()} instead.
     *
     * @return a String representing the Mimetype being handled in this Writer
     */
    @Deprecated
    String getMimeType();

    /**
     * @deprecated since 2.3. Use  instead.
     *
     * @param os
     *            a {@link OutputStream} to be written to the FormatWriter handler
     *
     * @return a {@link io.github.sparqlanything.html.org.apache.any23.writer.FormatWriter} ready to be implemented
     */
    @Deprecated
    FormatWriter getRdfWriter(OutputStream os);
}

interface BaseWriterFactory<Output> extends WriterFactory {

    Settings getSupportedSettings();

    TripleHandler getTripleWriter(Output output, Settings settings);

    @Override
    @Deprecated
    default FormatWriter getRdfWriter(OutputStream os) {
        throw new UnsupportedOperationException("this class does not support getRdfWriter()");
    }

    @Override
    @Deprecated
    default String getMimeType() {
        throw new UnsupportedOperationException("this class does not support getMimeType()");
    }

    @Override
    @Deprecated
    default RDFFormat getRdfFormat() {
        throw new UnsupportedOperationException("this class does not support getRdfFormat()");
    }
}
