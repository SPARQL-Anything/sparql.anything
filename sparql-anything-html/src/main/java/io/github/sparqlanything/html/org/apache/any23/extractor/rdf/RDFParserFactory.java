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

import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdf.RDFHandlerAdapter;
import io.github.sparqlanything.html.org.apache.any23.rdf.Any23ValueFactoryWrapper;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.ParseErrorListener;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.RDFaParserSettings;
import org.eclipse.rdf4j.rio.helpers.RDFaVersion;
import org.eclipse.rdf4j.rio.helpers.XMLParserSettings;
import org.eclipse.rdf4j.rio.turtle.TurtleParser;
import org.semanticweb.owlapi.rio.OWLAPIRDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;

/**
 * This factory provides a common logic for creating and configuring correctly any <i>RDF</i> parser used within the
 * library.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFParserFactory {

    private static final Logger logger = LoggerFactory.getLogger(RDFParserFactory.class);

    private static class InstanceHolder {
        private static final RDFParserFactory instance = new RDFParserFactory();
    }

    public static RDFParserFactory getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Returns a new instance of a configured TurtleParser.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured Turtle parser.
     */
    public RDFParser getTurtleParserInstance(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        if (extractionResult == null) {
            throw new NullPointerException("extractionResult cannot be null.");
        }
        final TurtleParser parser = new ExtendedTurtleParser();
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured RDFaParser, set to RDFa-1.0 compatibility mode.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured RDFXML parser.
     */
    public RDFParser getRDFa10Parser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(RDFFormat.RDFA);
        parser.getParserConfig().set(RDFaParserSettings.RDFA_COMPATIBILITY, RDFaVersion.RDFA_1_0);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured RDFaParser, set to RDFa-1.1 compatibility mode.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured RDFXML parser.
     */
    public RDFParser getRDFa11Parser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(RDFFormat.RDFA);
        parser.getParserConfig().set(RDFaParserSettings.RDFA_COMPATIBILITY, RDFaVersion.RDFA_1_1);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured RDFXMLParser.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured RDFXML parser.
     */
    public RDFParser getRDFXMLParser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(RDFFormat.RDFXML);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured NTriplesParser.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured NTriples parser.
     */
    public RDFParser getNTriplesParser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured NQuadsParser.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured NQuads parser.
     */
    public RDFParser getNQuadsParser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(RDFFormat.NQUADS);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured ManchesterSyntaxParser.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured Manchester Syntax parser.
     */
    public RDFParser getManchesterSyntaxParser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(OWLAPIRDFFormat.MANCHESTER_OWL);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured FunctionalSyntaxParser.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured Functional Syntax parser.
     */
    public RDFParser getFunctionalSyntaxParser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(OWLAPIRDFFormat.OWL_FUNCTIONAL);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured TriXParser.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured TriX parser.
     */
    public RDFParser getTriXParser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(RDFFormat.TRIX);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured <i>SesameJSONLDParser</i>.
     *
     * @param verifyDataType
     *            data verification enable if <code>true</code>.
     * @param stopAtFirstError
     *            the parser stops at first error if <code>true</code>.
     * @param extractionContext
     *            the extraction context where the parser is used.
     * @param extractionResult
     *            the output extraction result.
     *
     * @return a new instance of a configured JSONLDParser parser.
     */
    public RDFParser getJSONLDParser(final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        final RDFParser parser = Rio.createParser(RDFFormat.JSONLD);
        configureParser(parser, verifyDataType, stopAtFirstError, extractionContext, extractionResult);
        return parser;
    }

    /**
     * Configures the given parser on the specified extraction result setting the policies for data verification and
     * error handling.
     *
     * @param parser
     *            the parser to be configured.
     * @param verifyDataType
     *            enables the data verification.
     * @param stopAtFirstError
     *            enables the tolerant error handling.
     * @param extractionContext
     *            the extraction context in which the parser is used.
     * @param extractionResult
     *            the extraction result used to collect the parsed data.
     */
    // TODO: what about passing just default language and ErrorReport to configureParser() ?
    private void configureParser(final RDFParser parser, final boolean verifyDataType, final boolean stopAtFirstError,
            final ExtractionContext extractionContext, final ExtractionResult extractionResult) {
        parser.getParserConfig().setNonFatalErrors(
                stopAtFirstError ? Collections.emptySet() : new HashSet<>(parser.getSupportedSettings()));
        parser.getParserConfig().set(XMLParserSettings.LOAD_EXTERNAL_DTD, false);
        parser.set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, verifyDataType);
        parser.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, verifyDataType);

        parser.setParseErrorListener(new InternalParseErrorListener(extractionResult));
        parser.setValueFactory(new Any23ValueFactoryWrapper(SimpleValueFactory.getInstance(), extractionResult,
                extractionContext.getDefaultLanguage()));
        parser.setRDFHandler(new RDFHandlerAdapter(extractionResult));
    }

    /**
     * Internal listener used to trace <i>RDF</i> parse errors.
     */
    private static class InternalParseErrorListener implements ParseErrorListener {

        private final IssueReport extractionResult;

        public InternalParseErrorListener(IssueReport er) {
            extractionResult = er;
        }

        @Override
        public void warning(String msg, long lineNo, long colNo) {
            try {
                extractionResult.notifyIssue(IssueReport.IssueLevel.WARNING, msg, lineNo, colNo);
            } catch (Exception e) {
                notifyExceptionInNotification(e);
            }
        }

        @Override
        public void error(String msg, long lineNo, long colNo) {
            try {
                extractionResult.notifyIssue(IssueReport.IssueLevel.ERROR, msg, lineNo, colNo);
            } catch (Exception e) {
                notifyExceptionInNotification(e);
            }
        }

        @Override
        public void fatalError(String msg, long lineNo, long colNo) {
            try {
                extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, msg, lineNo, colNo);
            } catch (Exception e) {
                notifyExceptionInNotification(e);
            }
        }

        private void notifyExceptionInNotification(Exception e) {
            if (logger != null) {
                logger.error("An exception occurred while notifying an error.", e);
            }
        }
    }

    /**
     * This extended Turtle parser sets the default namespace to the base IRI before the parsing.
     */
    private static class ExtendedTurtleParser extends TurtleParser {
        @Override
        public void parse(Reader reader, String baseIRI) throws IOException, RDFParseException, RDFHandlerException {
            setNamespace("", baseIRI);
            super.parse(reader, baseIRI);
        }

        @Override
        public void parse(InputStream in, String baseIRI) throws IOException, RDFParseException, RDFHandlerException {
            setNamespace("", baseIRI);
            super.parse(in, baseIRI);
        }
    }
}
