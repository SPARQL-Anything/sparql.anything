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

package io.github.sparqlanything.html.org.apache.any23;

import io.github.sparqlanything.html.org.apache.any23.Any23;
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class contains some statistics and general information about an extraction.
 *
 * @see Any23
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class ExtractionReport {

    private final List<Extractor> matchingExtractors;

    private final String encoding;

    private final String detectedMimeType;

    private final ValidationReport validationReport;

    private final Map<String, Collection<IssueReport.Issue>> extractorIssues;

    public ExtractionReport(final List<Extractor> matchingExtractors, String encoding, String detectedMimeType,
            ValidationReport validationReport, Map<String, Collection<IssueReport.Issue>> extractorIssues) {
        if (matchingExtractors == null)
            throw new NullPointerException("list of matching extractors cannot be null.");
        if (encoding == null)
            throw new NullPointerException("encoding cannot be null.");
        // if(detectedMimeType == null) throw new NullPointerException("detected mime type cannot be null.");
        if (validationReport == null)
            throw new NullPointerException("validation report cannot be null.");

        this.matchingExtractors = Collections.unmodifiableList(matchingExtractors);
        this.encoding = encoding;
        this.detectedMimeType = detectedMimeType;
        this.validationReport = validationReport;
        this.extractorIssues = Collections.unmodifiableMap(extractorIssues);
    }

    /**
     * @return <code>true</code> if the extraction has activated at least an extractor, <code>false</code> otherwise.
     */
    public boolean hasMatchingExtractors() {
        return matchingExtractors.size() > 0;
    }

    /**
     * @return the (unmodifiable) list of matching extractors.
     */
    public List<Extractor> getMatchingExtractors() {
        return matchingExtractors;
    }

    /**
     * @return the detected encoding for the source stream.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @return the tetected mimetype for the input stream.
     */
    public String getDetectedMimeType() {
        return detectedMimeType;
    }

    /**
     * @return the validation report applied to the processed document.
     */
    public ValidationReport getValidationReport() {
        return validationReport;
    }

    /**
     * @param extractorName
     *            name of the extractor.
     *
     * @return the (unmodifiable) map of issues per extractor.
     */
    public Collection<IssueReport.Issue> getExtractorIssues(String extractorName) {
        final Collection<IssueReport.Issue> errors = extractorIssues.get(extractorName);
        return errors == null ? Collections.<IssueReport.Issue> emptyList()
                : Collections.unmodifiableCollection(errors);
    }

}
