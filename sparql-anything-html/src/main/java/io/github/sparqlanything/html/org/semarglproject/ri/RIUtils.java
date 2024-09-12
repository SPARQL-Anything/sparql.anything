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
package io.github.sparqlanything.html.org.semarglproject.ri;

import io.github.sparqlanything.html.org.semarglproject.ri.MalformedIriException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Utility class. Provides methods related to resource identifiers.
 */
public final class RIUtils {

    private static final Pattern ABS_OPAQUE_IRI_PATTERN = Pattern.compile(
            // scheme
            "[a-zA-Z][a-zA-Z0-9+.-]*:"
            // opaque part
            + "[^#/][^#]*",
            Pattern.DOTALL);

    private static final Pattern ABS_HIER_IRI_PATTERN = Pattern.compile(
            // scheme
            "[a-zA-Z][a-zA-Z0-9+.-]*:"
            // user
            + "/{1,3}(([^/?#@]*)@)?"
            // host
            + "(\\[[^@/?#]+\\]|([^@/?#:]+))"
            // port
            + "(:([^/?#]*))?"
            // path
            + "([^#?]*)?"
            // query
            + "(\\?([^#]*))?"
            // fragment
            + "(#[^#]*)?",
            Pattern.DOTALL);

    private static final Pattern URN_PATTERN = Pattern.compile("urn:[a-zA-Z0-9][a-zA-Z0-9-]{1,31}:.+");

    private RIUtils() {
    }

    /**
     * Resolves specified IRI. Absolute IRI are returned unmodified
     * @param base base to resolve against
     * @param iri IRI to be resolved
     * @return resolved absolute IRI
     * @throws MalformedIriException
     */
    public static String resolveIri(String base, String iri) throws MalformedIriException {
        if (iri == null) {
            return null;
        }
        if (isIri(iri) || isUrn(iri)) {
            return iri;
        } else {
            if (iri.startsWith("?") || iri.isEmpty()) {
                if (base.endsWith("#")) {
                    return base.substring(0, base.length() - 1) + iri;
                }
                return base + iri;
            }
            String result;
            try {
                URL basePart = new URL(base);
                result = new URL(basePart, iri).toString();
            } catch (MalformedURLException e) {
                result = base + iri;
            }
            if (isIri(result)) {
                return result;
            }
            throw new MalformedIriException("Malformed IRI: " + iri);
        }
    }

    /**
     * Checks if specified string is IRI
     * @param value value to check
     * @return true if value is IRI
     */
    public static boolean isIri(String value) {
        return ABS_HIER_IRI_PATTERN.matcher(value).matches() || ABS_OPAQUE_IRI_PATTERN.matcher(value).matches();
    }

    /**
     * Checks if specified string is absolute IRI
     * @param value value to check
     * @return true if value is absolute IRI
     */
    public static boolean isAbsoluteIri(String value) {
        return ABS_HIER_IRI_PATTERN.matcher(value).matches();
    }

    /**
     * Checks if specified string is URN
     * @param value value to check
     * @return true if value is URN
     */
    public static boolean isUrn(String value) {
        return URN_PATTERN.matcher(value).matches();
    }

}
