/**
 * Copyright 2012-2013 the Semargl contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.sparqlanything.html.org.semarglproject.sink;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;

/**
 * Base sink interface.
 */
public interface DataSink {

    /**
     * Sets document base URI. Must be called befor start stream event.
     * @param baseUri base URI
     */
    void setBaseUri(String baseUri);

    /**
     * Callback for start stream event.
     * @throws ParseException
     */
    void startStream() throws ParseException;

    /**
     * Callback for end stream event.
     * @throws ParseException
     */
    void endStream() throws ParseException;

    /**
     * Key-value based settings. Property settings are passed to child sinks.
     * @param key property key
     * @param value property value
     * @return true if at least one sink understands specified property, false otherwise
     */
    boolean setProperty(String key, Object value);
}
