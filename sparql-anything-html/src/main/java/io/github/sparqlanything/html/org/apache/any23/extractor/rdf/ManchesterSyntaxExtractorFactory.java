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
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.ManchesterSyntaxExtractor;
import io.github.sparqlanything.html.org.apache.any23.rdf.Prefixes;
import org.semanticweb.owlapi.rio.OWLAPIRDFFormat;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class ManchesterSyntaxExtractorFactory extends SimpleExtractorFactory<ManchesterSyntaxExtractor>
        implements ExtractorFactory<ManchesterSyntaxExtractor> {

    public static final String NAME = "owl-manchester";

    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new ManchesterSyntaxExtractorFactory();

    public ManchesterSyntaxExtractorFactory() {
        super(ManchesterSyntaxExtractorFactory.NAME, ManchesterSyntaxExtractorFactory.PREFIXES,
                Arrays.asList(OWLAPIRDFFormat.MANCHESTER_OWL.getDefaultMIMEType()), "example-manchestersyntax.omn");
    }

    @Override
    public io.github.sparqlanything.html.org.apache.any23.extractor.rdf.ManchesterSyntaxExtractor createExtractor() {
        return new ManchesterSyntaxExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
