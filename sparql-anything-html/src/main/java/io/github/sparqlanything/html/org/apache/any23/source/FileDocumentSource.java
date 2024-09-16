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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * File implementation of {@link DocumentSource}.
 */
public class FileDocumentSource implements DocumentSource {

    private final File file;

    private final String uri;

    public FileDocumentSource(File file) {
        this.file = file;
        this.uri = file.toURI().toString();
    }

    public FileDocumentSource(File file, String baseIRI) {
        this.file = file;
        this.uri = baseIRI;
    }

    public InputStream openInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    public long getContentLength() {
        return file.length();
    }

    public String getDocumentIRI() {
        return uri;
    }

    public String getContentType() {
        return null;
    }

    public boolean isLocal() {
        return true;
    }

    public String readStream() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = openInputStream();
        try {
            int c;
            while ((c = is.read()) != -1) {
                baos.write(c);
            }
        } finally {
            is.close();
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
