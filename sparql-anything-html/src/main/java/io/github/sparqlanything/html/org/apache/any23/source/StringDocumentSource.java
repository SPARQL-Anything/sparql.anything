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

package io.github.sparqlanything.html.org.apache.any23.source;

import io.github.sparqlanything.html.org.apache.any23.source.DocumentSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * String implementation of {@link DocumentSource}.
 */
public class StringDocumentSource implements DocumentSource {

    private final String in;

    private final String contentType;

    private final String encoding;

    private final String uri;

    public StringDocumentSource(String in, String uri) {
        this(in, uri, null, null);
    }

    public StringDocumentSource(String in, String uri, String contentType) {
        this(in, uri, contentType, null);
    }

    public StringDocumentSource(String in, String uri, String contentType, String encoding) {
        this.in = in;
        this.uri = uri;
        this.contentType = contentType;
        this.encoding = encoding;
    }

    public InputStream openInputStream() throws IOException {
        if (encoding == null) {
            return new ByteArrayInputStream(in.getBytes(StandardCharsets.UTF_8));
        }
        return new ByteArrayInputStream(in.getBytes(encoding));
    }

    public long getContentLength() {
        return in.length();
    }

    public String getDocumentIRI() {
        return uri;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isLocal() {
        return true;
    }

}
