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

import io.github.sparqlanything.html.org.apache.any23.http.HTTPClient;
import io.github.sparqlanything.html.org.apache.any23.source.DocumentSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Http implementation of {@link DocumentSource}.
 */
public class HTTPDocumentSource implements DocumentSource {

    private static final Logger LOG = LoggerFactory.getLogger(HTTPDocumentSource.class);

    private final HTTPClient client;

    private String uri;

    private InputStream unusedInputStream = null;

    private boolean loaded = false;

    public HTTPDocumentSource(HTTPClient client, String uri) throws URISyntaxException {
        this.client = client;
        this.uri = normalize(uri);
    }

    private String normalize(String uri) throws URISyntaxException {
        try {
            URI normalized = new URI(uri).normalize();
            return normalized.toString();
        } catch (URISyntaxException e) {
            LOG.warn("Invalid uri: {}", uri);
            LOG.error("Can not convert URL", e);
            throw e;
        }
    }

    private void ensureOpen() throws IOException {
        if (loaded)
            return;
        loaded = true;
        unusedInputStream = client.openInputStream(uri);
        if (client.getActualDocumentIRI() != null) {
            uri = client.getActualDocumentIRI();
        }
    }

    public InputStream openInputStream() throws IOException {
        ensureOpen();
        if (unusedInputStream != null) {
            InputStream temp = unusedInputStream;
            unusedInputStream = null;
            return temp;
        }
        return client.openInputStream(uri);
    }

    public long getContentLength() {
        return client.getContentLength();
    }

    public String getDocumentIRI() {
        return uri;
    }

    public String getContentType() {
        return client.getContentType();
    }

    public boolean isLocal() {
        return false;
    }

}
