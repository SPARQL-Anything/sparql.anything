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

package io.github.sparqlanything.html.org.apache.any23.extractor.microdata;

import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.MicrodataParser;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.MicrodataParserException;

/**
 * This class describes the report of the {@link MicrodataParser}. Such report contains the detected {@link io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope}s
 * and errors.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataParserReport {

    private static final MicrodataParserException[] NO_ERRORS = new MicrodataParserException[0];

    private final io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope[] detectedItemScopes;

    private final MicrodataParserException[] errors;

    public MicrodataParserReport(io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope[] detectedItemScopes, MicrodataParserException[] errors) {
        if (detectedItemScopes == null) {
            throw new NullPointerException("detected item scopes list cannot be null.");
        }
        this.detectedItemScopes = detectedItemScopes;
        this.errors = errors == null ? NO_ERRORS : errors;
    }

    public MicrodataParserReport(io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope[] detectedItemScopes) {
        this(detectedItemScopes, null);
    }

    public ItemScope[] getDetectedItemScopes() {
        return detectedItemScopes;
    }

    public MicrodataParserException[] getErrors() {
        return errors;
    }

}
