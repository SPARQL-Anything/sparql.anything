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
import io.github.sparqlanything.html.org.apache.any23.writer.TripleFormat;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleWriterFactory;
import io.github.sparqlanything.html.org.apache.any23.writer.URIListWriter;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * @author Hans Brende (hansbrende@apache.org)
 */
public class URIListWriterFactory implements TripleWriterFactory {

    public static final String MIME_TYPE = "text/plain";
    public static final String IDENTIFIER = "uri";

    /**
     *
     */
    public URIListWriterFactory() {
    }

    @Override
    public TripleFormat getTripleFormat() {
        return io.github.sparqlanything.html.org.apache.any23.writer.URIListWriter.FORMAT;
    }

    @Override
    public Settings getSupportedSettings() {
        return Settings.of();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public TripleHandler getTripleWriter(OutputStream os, Settings settings) {
        return new URIListWriter(os);
    }

}
