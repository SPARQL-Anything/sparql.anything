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

package io.github.sparqlanything.html.org.apache.any23.extractor;

import io.github.sparqlanything.html.org.apache.any23.rdf.Prefixes;

/**
 *
 * It defines a minimal signature for an {@link Extractor} description.
 *
 */
public interface ExtractorDescription {

    /**
     * Returns the name of the extractor.
     *
     * @return a name.
     */
    String getExtractorName();

    /**
     * Returns the label for extractors created from this factory.
     *
     * @return A string label describing the type of extractors created from this factory.
     */
    String getExtractorLabel();

    /**
     * An instance defining the prefixes supported by this extractor.
     *
     * @return prefixes instance.
     */
    Prefixes getPrefixes();

}
