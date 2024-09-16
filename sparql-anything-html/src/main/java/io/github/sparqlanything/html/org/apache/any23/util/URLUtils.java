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

package io.github.sparqlanything.html.org.apache.any23.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Contains general utility functions for handling URLs.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class URLUtils {

    /**
     * Verifies if the specified URL is reachable online.
     *
     * @param url
     *            input URL.
     *
     * @return <code>true</code> if the resource can be accessed, <code>false</code> otherwise.
     *
     * @throws MalformedURLException
     *             if <code>url</code> is malformed.
     */
    public static boolean isOnline(String url) throws MalformedURLException {
        try {
            final URLConnection connection = new URL(url).openConnection();
            connection.getInputStream().close();
            return true;
        } catch (IOException ioe) {
            if (ioe instanceof MalformedURLException) {
                throw (MalformedURLException) ioe;
            }
            return false;
        }
    }

    private URLUtils() {
    }

}
