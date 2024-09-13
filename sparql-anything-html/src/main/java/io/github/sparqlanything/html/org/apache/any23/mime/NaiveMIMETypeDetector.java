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

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic implementation of {@link MIMETypeDetector} based on file extensions.
 */
public class NaiveMIMETypeDetector implements MIMETypeDetector {

    private final static Map<String, String> extensions = new HashMap<String, String>() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            // extension -> mime type
            put("html", "text/html");
            put("htm", "text/html");
            put("xhtml", "application/xhtml+xml");
            put("xht", "application/xhtml+xml");
            put("xrdf", "application/rdf+xml");
            put("rdfx", "application/rdf+xml");
            put("owl", "application/rdf+xml");
            put("txt", "text/plain");
        }
    };

    private final static Pattern extensionRegex = Pattern.compile(".*\\.([a-z0-9]+)");

    public MIMEType guessMIMEType(String fileName, InputStream input,

            MIMEType mimeTypeFromMetadata) {
        if (mimeTypeFromMetadata != null) {
            return mimeTypeFromMetadata;
        }

        final Optional<RDFFormat> parserFormatForFileName = Rio.getParserFormatForFileName(fileName);
        if (parserFormatForFileName.isPresent()) {
            return MIMEType.parse(parserFormatForFileName.get().getDefaultMIMEType());
        }

        String extension = getExtension(fileName);
        if (extension == null) {
            // Assume index file on web server.
            extension = "html";
        }
        if (extensions.containsKey(extension)) {
            return MIMEType.parse(extensions.get(extension));
        }
        return null;
    }

    private String getExtension(String filename) {
        Matcher m = extensionRegex.matcher(filename);
        if (!m.matches())
            return null;
        return m.group(1);
    }

}
