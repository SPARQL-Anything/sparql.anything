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

import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.SingleDocumentExtraction;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;

import java.util.Collection;
import java.util.Map;

/**
 * This class provides the report for a {@link io.github.sparqlanything.html.org.apache.any23.extractor.SingleDocumentExtraction} run.
 *
 * @see SingleDocumentExtraction
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SingleDocumentExtractionReport {

    private final ValidationReport validationReport;

    private final Map<String, Collection<IssueReport.Issue>> extractorToIssues;

    public SingleDocumentExtractionReport(ValidationReport validationReport,
            Map<String, Collection<IssueReport.Issue>> extractorToIssues) {
        if (validationReport == null)
            throw new NullPointerException("validation report cannot be null.");
        if (extractorToIssues == null)
            throw new NullPointerException("extractor issues map cannot be null.");
        this.validationReport = validationReport;
        this.extractorToIssues = extractorToIssues;
    }

    public ValidationReport getValidationReport() {
        return validationReport;
    }

    public Map<String, Collection<IssueReport.Issue>> getExtractorToIssues() {
        return extractorToIssues;
    }

}
