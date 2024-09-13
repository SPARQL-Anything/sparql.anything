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

package io.github.sparqlanything.html.org.apache.any23.mime.purifier;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link Purifier} that removes all the eventual blank characters at the header of a file that might
 * prevents its <i>MIME Type</i> detection.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class WhiteSpacesPurifier implements Purifier {

    /**
     * {@inheritDoc}
     */
    public void purify(InputStream inputStream) throws IOException {
        if (!inputStream.markSupported())
            throw new IllegalArgumentException("Provided InputStream does not support marks");

        // mark the current position
        inputStream.mark(Integer.MAX_VALUE);
        int byteRead = inputStream.read();
        char charRead = (char) byteRead;
        while (isBlank(charRead) && (byteRead != -1)) {
            // if here means that the previos character must be removed, so mark.
            inputStream.mark(Integer.MAX_VALUE);
            byteRead = inputStream.read();
            charRead = (char) byteRead;
        }
        // if exit go back to the last valid mark.
        inputStream.reset();
    }

    private boolean isBlank(char c) {
        return c == '\t' || c == '\n' || c == ' ' || c == '\r' || c == '\b' || c == '\f';
    }
}
