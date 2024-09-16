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

package io.github.sparqlanything.html.org.apache.any23.encoding;

import java.io.IOException;
import java.io.InputStream;

/**
 * Defines a detector for <i>charset encoding</i>.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 */
public interface EncodingDetector {

    /**
     * Guesses the data encoding.
     *
     * @param input
     *            the input stream containing the data.
     *
     * @return a string compliant to <a href="http://www.iana.org/assignments/character-sets">IANA Charset
     *         Specification</a>.
     *
     * @throws IOException
     *             if there is an error whilst guessing the encoding.
     */
    String guessEncoding(InputStream input) throws IOException;

    /**
     * Guesses the data encoding.
     *
     * @param input
     *            the input stream containing the data.
     * @param contentType
     *            the declared content type of the data.
     *
     * @return a string compliant to <a href="http://www.iana.org/assignments/character-sets">IANA Charset
     *         Specification</a>.
     *
     * @throws IOException
     *             if there is an error whilst guessing the encoding.
     */
    default String guessEncoding(InputStream input, String contentType) throws IOException {
        return guessEncoding(input);
    }

}
