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

package io.github.sparqlanything.html.org.apache.any23.mime;

import java.io.InputStream;

/**
 * This detector is able to estimate the <code>MIME</code> type of some given raw data.
 */
public interface MIMETypeDetector {

    /**
     * Estimates the <code>MIME</code> type of the content of input file.
     *
     * @param fileName
     *            name of the file.
     * @param input
     *            content of the file.
     * @param mimeTypeFromMetadata
     *            mimetype declared in metadata.
     *
     * @return the supposed mime type or <code>null</code> if nothing appropriate found.
     */
    public MIMEType guessMIMEType(String fileName, InputStream input, MIMEType mimeTypeFromMetadata);

}
