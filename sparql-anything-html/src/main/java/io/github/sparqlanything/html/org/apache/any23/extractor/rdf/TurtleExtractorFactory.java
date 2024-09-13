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

package io.github.sparqlanything.html.org.apache.any23.extractor.rdf;

import java.util.Arrays;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.SimpleExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.TurtleExtractor;
import io.github.sparqlanything.html.org.apache.any23.rdf.Prefixes;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class TurtleExtractorFactory extends SimpleExtractorFactory<io.github.sparqlanything.html.org.apache.any23.extractor.rdf.TurtleExtractor>
        implements ExtractorFactory<io.github.sparqlanything.html.org.apache.any23.extractor.rdf.TurtleExtractor> {

    public static final String NAME = "rdf-turtle";

    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new TurtleExtractorFactory();

    public TurtleExtractorFactory() {
        super(TurtleExtractorFactory.NAME, TurtleExtractorFactory.PREFIXES, Arrays.asList("text/turtle", "text/rdf+n3",
                "text/n3", "application/n3", "application/x-turtle", "application/turtle"), "example-turtle.ttl");
    }

    @Override
    public io.github.sparqlanything.html.org.apache.any23.extractor.rdf.TurtleExtractor createExtractor() {
        return new TurtleExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
