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

import org.eclipse.rdf4j.model.IRI;

/**
 * This class provides the context for the processing of a single {@link Extractor}.
 */
public class ExtractionContext {

    public static final String ROOT_EXTRACTION_RESULT_ID = "root-extraction-result-id";

    /**
     * Name of the extractor.
     */
    private final String extractorName;

    /**
     * IRI of the document.
     */
    private final IRI documentIRI;

    /**
     * The document default language.
     */
    private String defaultLanguage;

    /**
     * ID identifying the document.
     */
    private final String uniqueID;

    public ExtractionContext(String extractorName, IRI documentIRI, String defaultLanguage, String localID) {
        checkNotNull(extractorName, "extractor name");
        checkNotNull(documentIRI, "document IRI");
        this.extractorName = extractorName;
        this.documentIRI = documentIRI;
        this.defaultLanguage = defaultLanguage;
        this.uniqueID = "urn:x-any23:" + extractorName + ":" + (localID == null ? "" : localID) + ":" + documentIRI;
    }

    public ExtractionContext(String extractorName, IRI documentIRI, String defaultLanguage) {
        this(extractorName, documentIRI, defaultLanguage, ROOT_EXTRACTION_RESULT_ID);
    }

    public ExtractionContext(String extractorName, IRI documentIRI) {
        this(extractorName, documentIRI, null);
    }

    public ExtractionContext copy(String localID) {
        return new ExtractionContext(getExtractorName(), getDocumentIRI(), getDefaultLanguage(), localID);
    }

    public String getExtractorName() {
        return extractorName;
    }

    public IRI getDocumentIRI() {
        return documentIRI;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public int hashCode() {
        return uniqueID.hashCode();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ExtractionContext))
            return false;
        return ((ExtractionContext) other).uniqueID.equals(uniqueID);
    }

    public String toString() {
        return "ExtractionContext(" + uniqueID + ")";
    }

    private void checkNotNull(Object data, String desc) {
        if (data == null)
            throw new NullPointerException(desc + " cannot be null.");
    }

}
