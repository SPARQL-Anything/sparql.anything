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

package io.github.sparqlanything.html.org.apache.any23.rdf;

import io.github.sparqlanything.html.org.apache.any23.rdf.Prefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * This class act as a container for various well-known and adopted <i>RDF</i> Vocabulary prefixes.
 */
public class PopularPrefixes {

    private static final Logger logger = LoggerFactory.getLogger(PopularPrefixes.class);

    private static final String RESOURCE_NAME = "/org/apache/any23/prefixes/prefixes.properties";

    private static final Prefixes popularPrefixes = getPrefixes();

    private static Prefixes getPrefixes() {
        Prefixes prefixes = new Prefixes();
        Properties properties = new Properties();
        try {
            logger.trace(String.format(Locale.ROOT, "Loading prefixes from %s", RESOURCE_NAME));
            properties.load(getResourceAsStream());
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "Error while loading prefixes from %s", RESOURCE_NAME), e);
            throw new RuntimeException(
                    String.format(Locale.ROOT, "Error while loading prefixes from %s", RESOURCE_NAME));
        }
        for (Map.Entry entry : properties.entrySet()) {
            if (testIRICompliance((String) entry.getValue())) {
                prefixes.add((String) entry.getKey(), (String) entry.getValue());
            } else {
                logger.warn(String.format(Locale.ROOT, "Prefixes entry '%s' is not a well-formad IRI. Skipped.",
                        entry.getValue()));
            }
        }
        return prefixes;
    }

    /**
     * This method perform a prefix lookup. Given a set of prefixes it returns {@link Prefixes} bag class containing
     * them.
     *
     * @param prefixes
     *            the input prefixes where perform the lookup
     *
     * @return a {@link Prefixes} containing all the prefixes mathing the input parameter
     */
    public static Prefixes createSubset(String... prefixes) {
        return popularPrefixes.createSubset(prefixes);
    }

    /**
     * @return a {@link Prefixes} with a set of well-known prefixes
     */
    public static Prefixes get() {
        return popularPrefixes;
    }

    /**
     * Checks the compliance of the <i>IRI</i>.
     *
     * @param stringUri
     *            the string of the IRI to be checked
     *
     * @return <code>true</code> if <i> stringUri</i> is a valid IRI, <code>false</code> otherwise.
     */
    private static boolean testIRICompliance(String stringUri) {
        try {
            new URI(stringUri);
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    /**
     * Loads the prefixes list configuration file.
     *
     * @return the input stream containing the configuration.
     */
    private static InputStream getResourceAsStream() {
        InputStream result;
        result = PopularPrefixes.class.getResourceAsStream(RESOURCE_NAME);
        if (result == null) {
            result = PopularPrefixes.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
            if (result == null) {
                result = ClassLoader.getSystemResourceAsStream(RESOURCE_NAME);
            }
        }
        return result;
    }

}
