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
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionParameters;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import org.eclipse.rdf4j.rio.RDFParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Base class for a generic <i>RDF</i> {@link io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.ContentExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Hans Brende (hansbrende@apache.org)
 */
public abstract class BaseRDFExtractor implements Extractor.ContentExtractor {

    private boolean verifyDataType;
    private boolean stopAtFirstError;

    public BaseRDFExtractor() {
        this(false, false);
    }

    /**
     * Constructor, allows to specify the validation and error handling policies.
     *
     * @param verifyDataType
     *            if <code>true</code> the data types will be verified, if <code>false</code> will be ignored.
     * @param stopAtFirstError
     *            if <code>true</code> the parser will stop at first parsing error, if <code>false</code> will ignore
     *            non blocking errors.
     */
    public BaseRDFExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        this.verifyDataType = verifyDataType;
        this.stopAtFirstError = stopAtFirstError;
    }

    protected abstract RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult);

    public boolean isVerifyDataType() {
        return verifyDataType;
    }

    public void setVerifyDataType(boolean verifyDataType) {
        this.verifyDataType = verifyDataType;
    }

    public boolean isStopAtFirstError() {
        return stopAtFirstError;
    }

    @Override
    public void setStopAtFirstError(boolean b) {
        stopAtFirstError = b;
    }

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, InputStream in,
            ExtractionResult extractionResult) throws IOException, ExtractionException {
        try {
            final RDFParser parser = getParser(extractionContext, extractionResult);
            parser.parse(in, extractionContext.getDocumentIRI().stringValue());
        } catch (Exception ex) {
            extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, toString(ex), -1, -1);
        }
    }

    // keep package-access to avoid backwards compatibility woes if protected (may move around later)
    static String toString(Throwable th) {
        StringWriter writer = new StringWriter();
        try (PrintWriter pw = new PrintWriter(writer)) {
            th.printStackTrace(pw);
        }
        String string = writer.toString();
        if (string.length() > 1024) {
            return string.substring(0, 1021) + "...";
        }
        return string;
    }

}
