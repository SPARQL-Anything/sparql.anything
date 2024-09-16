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

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.BaseRDFExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.NQuadsExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.RDFParserFactory;
import org.eclipse.rdf4j.rio.RDFParser;

/**
 * Concrete implementation of {@link io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.ContentExtractor} handling
 * <a href="http://sw.deri.org/2008/07/n-quads/">N-Quads</a> format.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsExtractor extends BaseRDFExtractor {

    public NQuadsExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    public NQuadsExtractor() {
        this(false, false);
    }

    @Override
    public ExtractorDescription getDescription() {
        return NQuadsExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getNQuadsParser(isVerifyDataType(), isStopAtFirstError(),
                extractionContext, extractionResult);
    }

}
