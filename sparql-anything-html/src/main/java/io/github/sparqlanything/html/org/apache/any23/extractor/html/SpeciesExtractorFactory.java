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

package io.github.sparqlanything.html.org.apache.any23.extractor.html;

import java.util.Arrays;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.SimpleExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.rdf.PopularPrefixes;
import io.github.sparqlanything.html.org.apache.any23.rdf.Prefixes;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class SpeciesExtractorFactory extends SimpleExtractorFactory<SpeciesExtractor>
        implements ExtractorFactory<SpeciesExtractor> {

    public static final String NAME = "html-mf-species";

    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "wo");

    private static final ExtractorDescription descriptionInstance = new SpeciesExtractorFactory();

    public SpeciesExtractorFactory() {
        super(SpeciesExtractorFactory.NAME, SpeciesExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"), "example-mf-species.html");
    }

    @Override
    public SpeciesExtractor createExtractor() {
        return new SpeciesExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
