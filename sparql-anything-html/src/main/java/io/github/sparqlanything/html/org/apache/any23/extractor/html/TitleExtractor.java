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

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionParameters;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.rdf.Any23ValueFactoryWrapper;
import io.github.sparqlanything.html.org.apache.any23.vocab.DCTerms;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Extracts the value of the &lt;title&gt; element of an HTML or XHTML page.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TitleExtractor implements TagSoupDOMExtractor {

    private static final DCTerms vDCTERMS = DCTerms.getInstance();

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, Document in,
            ExtractionResult out) throws IOException, ExtractionException {
        final Any23ValueFactoryWrapper valueFactory = new Any23ValueFactoryWrapper(SimpleValueFactory.getInstance(),
                out, extractionContext.getDefaultLanguage());

        try {
            String title = DomUtils.find(in, "/HTML/HEAD/TITLE/text()").trim();
            if (title != null && (title.length() != 0)) {
                out.writeTriple(extractionContext.getDocumentIRI(), vDCTERMS.title, valueFactory.createLiteral(title));
            }
        } finally {
            valueFactory.setIssueReport(null);
        }
    }

    @Override
    public ExtractorDescription getDescription() {
        return TitleExtractorFactory.getDescriptionInstance();
    }

}
