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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Mathematical utility functions.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class MathUtils {

    private MathUtils() {
    }

    /**
     * <p>
     * Create a MD5 <b>weak hash</b> for a given string.
     * </p>
     * <p>
     * <b>N.B. This method MUST never be used in a sensitive context</b>. Examples of such usage include (i)
     * User-password storage, (ii) Security token generation (used to confirm e-mail when registering on a website,
     * reset password, etc...), (iii) To compute some message integrity.
     * </p>
     * Current usage is limited to {@link io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils#getBNode(String)} which is fine for the creation
     * of blank node(s).
     *
     * @param s
     *            input string to create an MD5 hash for.
     *
     * @return a string representation of a MD5 {@link MessageDigest}
     */
    public static final String md5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(s.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md5.digest();
            StringBuffer result = new StringBuffer();
            for (byte b : digest) {
                result.append(Integer.toHexString(0xFF & b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Should never happen, MD5 is supported", e);
        }
    }

}
