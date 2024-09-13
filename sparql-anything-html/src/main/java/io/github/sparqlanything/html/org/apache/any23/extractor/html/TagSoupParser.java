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

import io.github.sparqlanything.html.org.apache.any23.validator.DefaultValidator;
import io.github.sparqlanything.html.org.apache.any23.validator.Validator;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

/**
 * <p>
 * Parses an {@link InputStream} into an <i>HTML DOM</i> tree.
 * </p>
 * <p>
 * <strong>Note:</strong> The resulting <i>DOM</i> tree will not be namespace aware, and all element names will be upper
 * case, while attributes will be lower case. This is because the HTML parser uses the
 * <a href="http://xerces.apache.org/xerces2-j/dom.html">Xerces HTML DOM</a> implementation, which doesn't support
 * namespaces and forces uppercase element names. This works with the <i>RDFa XSLT Converter</i> and with <i>XPath</i>,
 * so we left it this way.
 * </p>
 *
 * @author Richard Cyganiak (richard at cyganiak dot de)
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */

public class TagSoupParser {

    public static final String ELEMENT_LOCATION = "Element-Location";

    private final static Logger logger = LoggerFactory.getLogger(TagSoupParser.class);

    private final InputStream input;

    private final String documentIRI;

    private final String encoding;

    private final TagSoupParsingConfiguration config;

    private Document result = null;

    public TagSoupParser(InputStream input, String documentIRI) {
        this.input = input;
        this.documentIRI = documentIRI;
        this.encoding = null;

        config = TagSoupParsingConfiguration.getDefault();
    }

    public TagSoupParser(InputStream input, String documentIRI, String encoding) {
        if (encoding != null && !Charset.isSupported(encoding))
            throw new UnsupportedCharsetException(String.format(Locale.ROOT, "Charset %s is not supported", encoding));

        this.input = input;
        this.documentIRI = documentIRI;
        this.encoding = encoding;

        config = TagSoupParsingConfiguration.getDefault();
    }

    /**
     * Returns the DOM of the given document IRI.
     *
     * @return the <i>HTML</i> DOM.
     *
     * @throws IOException
     *             if there is an error whilst accessing the DOM
     */
    public Document getDOM() throws IOException {
        if (result == null) {
            long startTime = System.currentTimeMillis();
            try {
                result = config.parse(input, documentIRI, encoding);
            } finally {
                long elapsed = System.currentTimeMillis() - startTime;
                logger.debug("Parsed " + documentIRI + " with " + config.name() + ", " + elapsed + "ms");
            }
        }
        result.setDocumentURI(documentIRI);
        return result;
    }

    /**
     * Returns the validated DOM and applies fixes on it if <i>applyFix</i> is set to <code>true</code>.
     *
     * @param applyFix
     *            whether to apply fixes to the DOM
     *
     * @return a report containing the <i>HTML</i> DOM that has been validated and fixed if <i>applyFix</i> if
     *         <code>true</code>. The reports contains also information about the activated rules and the the detected
     *         issues.
     *
     * @throws IOException
     *             if there is an error accessing the DOM
     * @throws io.github.sparqlanything.html.org.apache.any23.validator.ValidatorException
     *             if there is an error validating the DOM
     */
    public DocumentReport getValidatedDOM(boolean applyFix) throws IOException, ValidatorException {
        final URI dIRI;
        try {
            dIRI = new URI(documentIRI);
        } catch (IllegalArgumentException | URISyntaxException urise) {
            throw new ValidatorException("Error while performing validation, invalid document IRI.", urise);
        }
        Validator validator = new DefaultValidator();
        Document document = getDOM();
        return new DocumentReport(validator.validate(dIRI, document, applyFix), document);
    }

    /**
     * Describes a <i>DOM Element</i> location.
     */
    public static class ElementLocation {

        private int beginLineNumber;
        private int beginColumnNumber;
        private int endLineNumber;
        private int endColumnNumber;

        private ElementLocation(int beginLineNumber, int beginColumnNumber, int endLineNumber, int endColumnNumber) {
            this.beginLineNumber = beginLineNumber;
            this.beginColumnNumber = beginColumnNumber;
            this.endLineNumber = endLineNumber;
            this.endColumnNumber = endColumnNumber;
        }

        public int getBeginLineNumber() {
            return beginLineNumber;
        }

        public int getBeginColumnNumber() {
            return beginColumnNumber;
        }

        public int getEndLineNumber() {
            return endLineNumber;
        }

        public int getEndColumnNumber() {
            return endColumnNumber;
        }
    }

}
