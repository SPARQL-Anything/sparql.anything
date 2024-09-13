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
import io.github.sparqlanything.html.org.apache.any23.writer.RDFWriterTripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleFormat;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleWriter;
import org.eclipse.rdf4j.rio.WriterConfig;

import java.io.OutputStream;

/**
 * <a href="http://www.w3.org/2004/03/trix/">TriX</a> {@link TripleWriter} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Hans Brende (hansbrende@apache.org)
 */
public class TriXWriter extends RDFWriterTripleHandler {

    static class Internal {
        private static final org.eclipse.rdf4j.rio.trix.TriXWriterFactory rdf4j = new org.eclipse.rdf4j.rio.trix.TriXWriterFactory();

        static final TripleFormat FORMAT = format(rdf4j);

        static final Settings SUPPORTED_SETTINGS = Settings.of();
    }

    @Override
    void configure(WriterConfig config, Settings settings) {
    }

    public TriXWriter(OutputStream os) {
        this(os, Settings.of());
    }

    public TriXWriter(OutputStream os, Settings settings) {
        super(Internal.rdf4j, Internal.FORMAT, os, settings);
    }

}
