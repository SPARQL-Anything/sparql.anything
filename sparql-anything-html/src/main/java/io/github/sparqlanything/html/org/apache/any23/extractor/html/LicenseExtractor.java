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
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.vocab.XHTML;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.eclipse.rdf4j.model.IRI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Locale;

/**
 * Extractor for the <a href="http://microformats.org/wiki/rel-license">rel-license</a> microformat.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak
 */
public class LicenseExtractor implements TagSoupDOMExtractor {

    private static final XHTML vXHTML = XHTML.getInstance();

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, Document in,
            ExtractionResult out) throws IOException, ExtractionException {
        HTMLDocument document = new HTMLDocument(in);
        final IRI documentIRI = extractionContext.getDocumentIRI();
        for (Node node : DomUtils.findAll(in, "//A[@rel='license']/@href")) {
            String link = node.getNodeValue();
            if ("".equals(link)) {
                out.notifyIssue(IssueReport.IssueLevel.WARNING, String.format(Locale.ROOT,
                        "Invalid license link detected within document %s.", documentIRI.toString()), 0, 0);
                continue;
            }
            out.writeTriple(documentIRI, vXHTML.license, document.resolveIRI(link));
        }
    }

    @Override
    public ExtractorDescription getDescription() {
        return LicenseExtractorFactory.getDescriptionInstance();
    }

}
