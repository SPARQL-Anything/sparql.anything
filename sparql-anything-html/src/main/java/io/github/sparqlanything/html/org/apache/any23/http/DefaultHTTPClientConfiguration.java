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

import io.github.sparqlanything.html.org.apache.any23.configuration.DefaultConfiguration;
import io.github.sparqlanything.html.org.apache.any23.http.HTTPClientConfiguration;

/**
 * Default implementation of {@link io.github.sparqlanything.html.org.apache.any23.http.HTTPClientConfiguration}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultHTTPClientConfiguration implements HTTPClientConfiguration {

    private static DefaultHTTPClientConfiguration instance;

    public static DefaultHTTPClientConfiguration singleton() {
        if (instance == null) {
            instance = new DefaultHTTPClientConfiguration();
        }
        return instance;
    }

    private String userAgent;
    private int defaultTimeout;
    private int maxConnections;
    private String acceptHeader;

    /**
     * Constructor.
     *
     * @param userAgent
     *            the user agent descriptor string.
     * @param defaultTimeout
     *            the default timeout, cannot be <code>&lt;&#61; to 0</code>
     * @param maxConnections
     *            the default max connections, cannot be <code>&lt;&#61; to 0</code>
     * @param acceptHeader
     *            the accept header string, can be <code>null</code>.
     */
    public DefaultHTTPClientConfiguration(String userAgent, int defaultTimeout, int maxConnections,
            String acceptHeader) {
        if (userAgent == null)
            throw new IllegalArgumentException("userAgent cannot be null.");
        if (defaultTimeout <= 0)
            throw new IllegalArgumentException("defaultTimeout cannot be <= 0 .");
        if (maxConnections <= 0)
            throw new IllegalArgumentException("maxConnections cannot be <= 0 .");
        this.userAgent = userAgent;
        this.defaultTimeout = defaultTimeout;
        this.maxConnections = maxConnections;
        this.acceptHeader = acceptHeader;
    }

    /**
     * Constructor. initialized with default {@link DefaultConfiguration} parameters
     *
     * @param acceptHeader
     *            the value to initialize <code>acceptHeader</code>.
     */
    public DefaultHTTPClientConfiguration(String acceptHeader) {
        this(DefaultConfiguration.singleton().getPropertyOrFail("any23.http.user.agent.default"),
                DefaultConfiguration.singleton().getPropertyIntOrFail("any23.http.client.timeout"),
                DefaultConfiguration.singleton().getPropertyIntOrFail("any23.http.client.max.connections"),
                acceptHeader);
    }

    /**
     * Constructor. initialized with default {@link DefaultConfiguration} parameters and <code>acceptHeader=null</code>.
     */
    public DefaultHTTPClientConfiguration() {
        this(null);
    }

    public String getUserAgent() {
        return userAgent;
    }

    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getAcceptHeader() {
        return acceptHeader;
    }

}
