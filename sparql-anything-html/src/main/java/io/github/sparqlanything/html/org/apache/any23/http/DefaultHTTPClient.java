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

import io.github.sparqlanything.html.org.apache.any23.http.DefaultHTTPClientConfiguration;
import io.github.sparqlanything.html.org.apache.any23.http.HTTPClient;
import io.github.sparqlanything.html.org.apache.any23.http.HTTPClientConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Opens an {@link InputStream} on an HTTP IRI. Is configured with sane values for timeouts, default headers and so on.
 *
 * @author Paolo Capriotti
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class DefaultHTTPClient implements HTTPClient {

    private final PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();

    private HTTPClientConfiguration configuration;

    private HttpClient client = null;

    private long _contentLength = -1;

    private String actualDocumentIRI = null;

    private String contentType = null;

    /**
     * Creates a {@link DefaultHTTPClient} instance already initialized
     *
     * @return populated {@link io.github.sparqlanything.html.org.apache.any23.http.DefaultHTTPClient}
     */
    public static DefaultHTTPClient createInitializedHTTPClient() {
        final DefaultHTTPClient defaultHTTPClient = new DefaultHTTPClient();
        defaultHTTPClient.init(DefaultHTTPClientConfiguration.singleton());
        return defaultHTTPClient;
    }

    public void init(HTTPClientConfiguration configuration) {
        if (configuration == null)
            throw new NullPointerException("Illegal configuration, cannot be null.");
        this.configuration = configuration;
    }

    /**
     *
     * Opens an {@link InputStream} from a given IRI. It follows redirects.
     *
     * @param uri
     *            to be opened
     *
     * @return {@link InputStream}
     *
     * @throws IOException
     *             if there is an error opening the {@link InputStream} located at the URI.
     */
    public InputStream openInputStream(String uri) throws IOException {
        HttpGet method = null;
        try {
            ensureClientInitialized();
            HttpClientContext context = HttpClientContext.create();
            method = new HttpGet(uri);
            HttpResponse response = client.execute(method, context);
            List<URI> locations = context.getRedirectLocations();

            URI actualURI = locations == null || locations.isEmpty() ? method.getURI()
                    : locations.get(locations.size() - 1);
            actualDocumentIRI = actualURI.toString();

            final Header contentTypeHeader = response.getFirstHeader("Content-Type");
            contentType = contentTypeHeader == null ? null : contentTypeHeader.getValue();
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Failed to fetch " + uri + ": " + response.getStatusLine().getStatusCode() + " "
                        + response.getStatusLine().getReasonPhrase());
            }

            byte[] bytes = IOUtils.toByteArray(response.getEntity().getContent());
            _contentLength = bytes.length;
            return new ByteArrayInputStream(bytes);
        } finally {
            if (method != null) {
                method.reset();
            }
        }
    }

    /**
     * Shuts down the connection manager.
     */
    public void close() {
        manager.shutdown();
    }

    public long getContentLength() {
        return _contentLength;
    }

    public String getActualDocumentIRI() {
        return actualDocumentIRI;
    }

    public String getContentType() {
        return contentType;
    }

    protected int getConnectionTimeout() {
        return configuration.getDefaultTimeout();
    }

    protected int getSoTimeout() {
        return configuration.getDefaultTimeout();
    }

    private void ensureClientInitialized() {
        if (configuration == null)
            throw new IllegalStateException("client must be initialized first.");
        if (client != null)
            return;

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(getConnectionTimeout())
                .setSocketTimeout(getSoTimeout()).setRedirectsEnabled(true).build();

        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(getSoTimeout()).build();

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", configuration.getUserAgent()));
        if (configuration.getAcceptHeader() != null) {
            headers.add(new BasicHeader("Accept", configuration.getAcceptHeader()));
        }
        headers.add(new BasicHeader("Accept-Language", "en-us,en-gb,en,*;q=0.3")); // TODO: this must become parametric.
        // headers.add(new BasicHeader("Accept-Encoding", "x-gzip, gzip"));
        headers.add(new BasicHeader("Accept-Charset", "utf-8,iso-8859-1;q=0.7,*;q=0.5"));

        client = HttpClients.custom().setConnectionManager(manager).setDefaultRequestConfig(requestConfig)
                .setDefaultSocketConfig(socketConfig).setMaxConnTotal(configuration.getMaxConnections())
                .setDefaultHeaders(headers).build();
    }

}
