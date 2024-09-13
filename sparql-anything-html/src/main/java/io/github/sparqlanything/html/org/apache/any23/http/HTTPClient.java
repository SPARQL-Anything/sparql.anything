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

package io.github.sparqlanything.html.org.apache.any23.http;

import io.github.sparqlanything.html.org.apache.any23.http.HTTPClientConfiguration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction for opening an {@link InputStream} on an HTTP IRI.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface HTTPClient {

    /**
     * Initializes the HTTP client.
     *
     * @param configuration
     *            configuration for the HTTP Client.
     */
    void init(HTTPClientConfiguration configuration);

    /**
     * Opens the input stream for the given target IRI.
     *
     * @param uri
     *            target IRI.
     *
     * @return input stream to access IRI content.
     *
     * @throws IOException
     *             if any error occurs while reading the IRI content.
     */
    InputStream openInputStream(String uri) throws IOException;

    /**
     * Release all static resources help by the instance. Call this method only if you are sure you will not use it
     * again in your application, like for example when shutting down a servlet context.
     */
    void close();

    /**
     * The value of the Content-Type header reported by the server. Can be <code>null</code>.
     *
     * @return the content type as string.
     */
    String getContentType();

    /**
     * @return content length in bytes.
     */
    long getContentLength();

    /**
     * Returns the actual IRI from which the document was fetched. This might differ from the IRI passed to
     * openInputStream() if a redirect was performed. A return value of <code>null</code> means that the IRI is
     * unchanged and the original IRI was used.
     *
     * @return actual document IRI.
     */
    String getActualDocumentIRI();

}
