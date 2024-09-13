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

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionParameters;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.*;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.JSONLDJavaSink;
import io.github.sparqlanything.html.org.apache.any23.rdf.Any23ValueFactoryWrapper;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Concrete implementation of {@link io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.ContentExtractor} handling
 * <a href="http://www.w3.org/TR/json-ld/">JSON-LD</a> format.
 *
 */
public class JSONLDExtractor extends BaseRDFExtractor {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER).disable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .disable(JsonReadFeature.ALLOW_MISSING_VALUES).enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
            .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS).disable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
            .disable(JsonReadFeature.ALLOW_TRAILING_COMMA).enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES).disable(JsonReadFeature.ALLOW_YAML_COMMENTS)
            .enable(StreamReadFeature.IGNORE_UNDEFINED).enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
            .disable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();

    /**
     * @deprecated since 2.4. This extractor has never supported these settings. Use {@link #JSONLDExtractor()} instead.
     *
     * @param verifyDataType
     *            has no effect
     * @param stopAtFirstError
     *            has no effect
     */
    @Deprecated
    public JSONLDExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    public JSONLDExtractor() {
        super(false, false);
    }

    @Override
    public ExtractorDescription getDescription() {
        return JSONLDExtractorFactory.getDescriptionInstance();
    }

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, InputStream in,
            ExtractionResult extractionResult) throws IOException, ExtractionException {
        io.github.sparqlanything.html.org.apache.any23.extractor.rdf.JSONLDJavaSink handler = new io.github.sparqlanything.html.org.apache.any23.extractor.rdf.JSONLDJavaSink(extractionResult, new Any23ValueFactoryWrapper(
                SimpleValueFactory.getInstance(), extractionResult, extractionContext.getDefaultLanguage()));

        JsonLdOptions options = new JsonLdOptions(extractionContext.getDocumentIRI().stringValue());
        options.useNamespaces = true;

        try {
            Object json = JsonUtils
                    .fromJsonParser(OBJECT_MAPPER.getFactory().createParser(new JsonCleaningInputStream(in)));
            JsonLdProcessor.toRDF(json, handler, options);
        } catch (JsonProcessingException e) {
            JsonLocation loc = e.getLocation();
            if (loc == null) {
                extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, e.getOriginalMessage(), -1L, -1L);
            } else {
                extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, e.getOriginalMessage(), loc.getLineNr(),
                        loc.getColumnNr());
            }
        } catch (Exception e) {
            // ANY23-420: jsonld-java can sometimes throw IllegalArgumentException
            extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, toString(e), -1, -1);
        }
    }

    /* DEPRECATED METHODS */

    /**
     * @deprecated since 2.4. This extractor has never supported this setting. Do not use.
     *
     * @param stopAtFirstError
     *            has no effect
     */
    @Deprecated
    @Override
    public void setStopAtFirstError(boolean stopAtFirstError) {
        super.setStopAtFirstError(stopAtFirstError);
    }

    /**
     * @deprecated since 2.4. This extractor has never supported this setting. Do not use.
     *
     * @param verifyDataType
     *            has no effect
     */
    @Deprecated
    @Override
    public void setVerifyDataType(boolean verifyDataType) {
        super.setVerifyDataType(verifyDataType);
    }

    /**
     * @deprecated since 2.4. This extractor no longer wraps an RDF4J {@link RDFParser}. Do not use this method.
     *
     * @param extractionContext
     *            the extraction context
     * @param extractionResult
     *            the extraction result
     *
     * @return a {@link RDFParser}
     */
    @Deprecated
    @Override
    protected RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getJSONLDParser(isVerifyDataType(), isStopAtFirstError(),
                extractionContext, extractionResult);
    }

}
