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
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.FunctionalSyntaxExtractor;
import io.github.sparqlanything.html.org.apache.any23.rdf.Prefixes;
import org.semanticweb.owlapi.rio.OWLAPIRDFFormat;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class FunctionalSyntaxExtractorFactory extends SimpleExtractorFactory<io.github.sparqlanything.html.org.apache.any23.extractor.rdf.FunctionalSyntaxExtractor>
        implements ExtractorFactory<io.github.sparqlanything.html.org.apache.any23.extractor.rdf.FunctionalSyntaxExtractor> {

    public static final String NAME = "owl-functional";

    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new FunctionalSyntaxExtractorFactory();

    public FunctionalSyntaxExtractorFactory() {
        super(FunctionalSyntaxExtractorFactory.NAME, FunctionalSyntaxExtractorFactory.PREFIXES,
                Arrays.asList(OWLAPIRDFFormat.OWL_FUNCTIONAL.getDefaultMIMEType()), "example-functionalsyntax.ofn");
    }

    @Override
    public io.github.sparqlanything.html.org.apache.any23.extractor.rdf.FunctionalSyntaxExtractor createExtractor() {
        return new FunctionalSyntaxExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
