/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.github.sparqlanything.html.org.semarglproject.source;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.sink.CharSink;

import java.io.*;
import java.nio.charset.StandardCharsets;

final class CharSource extends AbstractSource<CharSink> {

    CharSource(CharSink sink) {
        super(sink);
    }

    @Override
    public void process(Reader reader, String mimeType, String baseUri) throws ParseException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            sink.setBaseUri(baseUri);
            char[] buffer = new char[512];
            int read;
            while ((read = bufferedReader.read(buffer)) != -1) {
                sink.process(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new ParseException(e);
        } finally {
            BaseStreamProcessor.closeQuietly(bufferedReader);
        }
    }

    @Override
    public void process(InputStream inputStream, String mimeType, String baseUri) throws ParseException {
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try {
            process(reader, mimeType, baseUri);
        } finally {
            BaseStreamProcessor.closeQuietly(reader);
        }
    }

}
