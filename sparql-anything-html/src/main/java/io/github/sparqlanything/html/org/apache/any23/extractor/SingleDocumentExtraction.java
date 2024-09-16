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

import io.github.sparqlanything.html.org.apache.any23.configuration.Configuration;
import io.github.sparqlanything.html.org.apache.any23.configuration.DefaultConfiguration;
import io.github.sparqlanything.html.org.apache.any23.encoding.EncodingDetector;
import io.github.sparqlanything.html.org.apache.any23.encoding.TikaEncodingDetector;
import io.github.sparqlanything.html.org.apache.any23.extractor.*;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResultImpl;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.DocumentReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.MicroformatExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.TagSoupParser;
import io.github.sparqlanything.html.org.apache.any23.mime.MIMEType;
import io.github.sparqlanything.html.org.apache.any23.mime.MIMETypeDetector;
import io.github.sparqlanything.html.org.apache.any23.rdf.Any23ValueFactoryWrapper;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import io.github.sparqlanything.html.org.apache.any23.source.DocumentSource;
import io.github.sparqlanything.html.org.apache.any23.source.LocalCopyFactory;
import io.github.sparqlanything.html.org.apache.any23.source.MemCopyFactory;
import io.github.sparqlanything.html.org.apache.any23.validator.EmptyValidationReport;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidatorException;
import io.github.sparqlanything.html.org.apache.any23.vocab.SINDICE;
import io.github.sparqlanything.html.org.apache.any23.writer.CompositeTripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.CountingTripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandlerException;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.BlindExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.ContentExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.apache.tika.mime.MimeTypes;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult.PropertyPath;
import static io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult.ResourceRoot;

/**
 * This class acts as a facade where all extractors (for a given MIMEType) can be called on a single document.
 * Extractors are automatically filtered by MIMEType.
 */
public class SingleDocumentExtraction {

    private static final SINDICE vSINDICE = SINDICE.getInstance();

    private static final Logger log = LoggerFactory.getLogger(SingleDocumentExtraction.class);

    private final Configuration configuration;

    private final DocumentSource in;

    private IRI documentIRI;

    private final ExtractorGroup extractors;

    private final TripleHandler output;

    private final EncodingDetector encoderDetector;

    private LocalCopyFactory copyFactory = null;

    private DocumentSource localDocumentSource = null;

    private MIMETypeDetector detector = null;

    private ExtractorGroup matchingExtractors = null;

    private MIMEType detectedMIMEType = null;

    private DocumentReport documentReport = null;

    private ExtractionParameters tagSoupDOMRelatedParameters = null;

    private String parserEncoding = null;

    /**
     * Builds an extractor by the specification of document source, list of extractors and output triple handler.
     *
     * @param configuration
     *            configuration applied during extraction.
     * @param in
     *            input document source.
     * @param extractors
     *            list of extractors to be applied.
     * @param output
     *            output triple handler.
     */
    public SingleDocumentExtraction(Configuration configuration, DocumentSource in, ExtractorGroup extractors,
            TripleHandler output) {
        if (configuration == null)
            throw new NullPointerException("configuration cannot be null.");
        if (in == null)
            throw new NullPointerException("in cannot be null.");
        this.configuration = configuration;
        this.in = in;
        this.extractors = extractors;

        List<TripleHandler> tripleHandlers = new ArrayList<>();
        tripleHandlers.add(output);
        tripleHandlers.add(new CountingTripleHandler());
        this.output = new CompositeTripleHandler(tripleHandlers);
        this.encoderDetector = new TikaEncodingDetector();
    }

    /**
     * Builds an extractor by the specification of document source, extractors factory and output triple handler.
     *
     * @param configuration
     *            configuration applied during extraction.
     * @param in
     *            input document source.
     * @param factory
     *            the extractors factory.
     * @param output
     *            output triple handler.
     */
    public SingleDocumentExtraction(Configuration configuration, DocumentSource in, ExtractorFactory<?> factory,
            TripleHandler output) {
        this(configuration, in, new ExtractorGroup(Collections.<ExtractorFactory<?>> singletonList(factory)), output);
        this.setMIMETypeDetector(null);
    }

    /**
     * Builds an extractor by the specification of document source, extractors factory and output triple handler, using
     * the {@link DefaultConfiguration}.
     *
     * @param in
     *            input document source.
     * @param factory
     *            the extractors factory.
     * @param output
     *            output triple handler.
     */
    public SingleDocumentExtraction(DocumentSource in, ExtractorFactory<?> factory, TripleHandler output) {
        this(DefaultConfiguration.singleton(), in,
                new ExtractorGroup(Collections.<ExtractorFactory<?>> singletonList(factory)), output);
        this.setMIMETypeDetector(null);
    }

    /**
     * Sets the internal factory for generating the document local copy, if <code>null</code> the
     * {@link MemCopyFactory} will be used.
     *
     * @param copyFactory
     *            local copy factory.
     *
     * @see DocumentSource
     */
    public void setLocalCopyFactory(LocalCopyFactory copyFactory) {
        this.copyFactory = copyFactory;
    }

    /**
     * Sets the internal mime type detector, if <code>null</code> mimetype detection will be skipped and all extractors
     * will be activated.
     *
     * @param detector
     *            detector instance.
     */
    public void setMIMETypeDetector(MIMETypeDetector detector) {
        this.detector = detector;
    }

    /**
     * Triggers the execution of all the {@link Extractor} registered to this class using the specified extraction
     * parameters.
     *
     * @param extractionParameters
     *            the parameters applied to the run execution.
     *
     * @return the report generated by the extraction.
     *
     * @throws ExtractionException
     *             if an error occurred during the data extraction.
     * @throws IOException
     *             if an error occurred during the data access.
     */
    public SingleDocumentExtractionReport run(ExtractionParameters extractionParameters)
            throws ExtractionException, IOException {
        if (extractionParameters == null) {
            extractionParameters = ExtractionParameters.newDefault(configuration);
        }

        final String contextIRI = extractionParameters
                .getProperty(ExtractionParameters.EXTRACTION_CONTEXT_IRI_PROPERTY);
        ensureHasLocalCopy();
        try {
            this.documentIRI = new Any23ValueFactoryWrapper(SimpleValueFactory.getInstance())
                    .createIRI("?".equals(contextIRI) ? in.getDocumentIRI() : contextIRI);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid IRI: " + in.getDocumentIRI(), ex);
        }
        if (log.isDebugEnabled()) {
            log.debug("Processing " + this.documentIRI);
        }
        filterExtractorsByMIMEType();

        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Extractors ");
            for (ExtractorFactory<?> factory : matchingExtractors) {
                sb.append(factory.getExtractorName());
                sb.append(' ');
            }
            sb.append("match ").append(documentIRI);
            log.debug(sb.toString());
        }

        final List<ResourceRoot> resourceRoots = new ArrayList<>();
        final List<PropertyPath> propertyPaths = new ArrayList<>();
        final Map<String, Collection<IssueReport.Issue>> extractorToIssues = new HashMap<>();

        // Invoke all extractors.
        try {
            output.startDocument(documentIRI);
        } catch (TripleHandlerException e) {
            log.error(String.format(Locale.ROOT, "Error starting document with IRI %s", documentIRI));
            throw new ExtractionException(
                    String.format(Locale.ROOT, "Error starting document with IRI %s", documentIRI), e);
        }
        try {
            output.setContentLength(in.getContentLength());
            // Create the document context.
            final String documentLanguage;
            try {
                documentLanguage = extractDocumentLanguage(extractionParameters);
                ArrayList<ExtractorFactory<?>> filteredList = new ArrayList<>(matchingExtractors.getNumOfExtractors());
                final boolean mimeTypeIsTooGeneric = isTooGeneric(detectedMIMEType);
                ArrayList<String> intersectionOfRdfMimetypes = null;
                for (ExtractorFactory<?> factory : matchingExtractors) {
                    final Extractor<?> extractor = factory.createExtractor();
                    final SingleExtractionReport er = runExtractor(extractionParameters, documentLanguage, extractor);
                    // Fix for ANY23-415:
                    if (mimeTypeIsTooGeneric) {
                        List<String> rdfMimetypes = factory.getSupportedMIMETypes().stream()
                                .filter(mt -> !isTooGeneric(mt)).map(MIMEType::getFullType)
                                .collect(Collectors.toList());
                        if (er.touched) {
                            // If detected mimetype is too generic, but we find extractors matching
                            // this mimetype that are capable of producing RDF triples from this resource,
                            // and these extractors are also associated with more specific RDF mimetypes,
                            // then we can simply take the intersection of these more specific mimetypes
                            // to narrow down the generic, non-RDF mimetype to a specific RDF mimetype.
                            if (intersectionOfRdfMimetypes == null) {
                                intersectionOfRdfMimetypes = new ArrayList<>(rdfMimetypes);
                            } else {
                                intersectionOfRdfMimetypes.retainAll(rdfMimetypes);
                            }
                        } else if (!rdfMimetypes.isEmpty()) {
                            // If detected mimetype is too generic, and this extractor matches both the
                            // generic mimetype and a more specific mimetype, but did not produce any RDF
                            // triples, then we can safely assume that this extractor is not actually a
                            // match for the type of file we are parsing (e.g., a "humans.txt" file).
                            continue;
                        }
                    }
                    resourceRoots.addAll(er.resourceRoots);
                    propertyPaths.addAll(er.propertyPaths);
                    filteredList.add(factory);
                    extractorToIssues.put(factory.getExtractorName(), er.issues);
                }
                matchingExtractors = new ExtractorGroup(filteredList);
                if (intersectionOfRdfMimetypes != null && !intersectionOfRdfMimetypes.isEmpty()) {
                    // If the detected mimetype is a generic, non-RDF mimetype, and the intersection
                    // of specific RDF mimetypes across all triple-producing extractors is non-empty,
                    // simply replace the generic mimetype with a specific RDF mimetype in that intersection.
                    detectedMIMEType = MIMEType.parse(intersectionOfRdfMimetypes.get(0));
                }
            } catch (ValidatorException ve) {
                throw new ExtractionException("An error occurred during the validation phase.", ve);
            }

            // Resource consolidation.
            final boolean addDomainTriples = extractionParameters
                    .getFlag(ExtractionParameters.METADATA_DOMAIN_PER_ENTITY_FLAG);
            final ExtractionContext consolidationContext;
            if (extractionParameters.getFlag(ExtractionParameters.METADATA_NESTING_FLAG)) {
                // Consolidation with nesting.
                consolidationContext = consolidateResources(resourceRoots, propertyPaths, addDomainTriples, output,
                        documentLanguage);
            } else {
                consolidationContext = consolidateResources(resourceRoots, addDomainTriples, output, documentLanguage);
            }

            // Adding time/size meta triples.
            if (extractionParameters.getFlag(ExtractionParameters.METADATA_TIMESIZE_FLAG)) {
                try {
                    addExtractionTimeSizeMetaTriples(consolidationContext);
                } catch (TripleHandlerException e) {
                    throw new ExtractionException(
                            String.format(Locale.ROOT,
                                    "Error while adding extraction metadata triples document with IRI %s", documentIRI),
                            e);
                }
            }
        } finally {
            try {
                output.endDocument(documentIRI);
            } catch (TripleHandlerException e) {
                log.error(String.format(Locale.ROOT, "Error ending document with IRI %s", documentIRI));
                throw new ExtractionException(
                        String.format(Locale.ROOT, "Error ending document with IRI %s", documentIRI), e);
            }
        }

        return new SingleDocumentExtractionReport(
                documentReport == null ? EmptyValidationReport.getInstance() : documentReport.getReport(),
                extractorToIssues);
    }

    private static boolean isTooGeneric(MIMEType type) {
        if (type == null || type.isAnySubtype()) {
            return true;
        }
        String mt = type.getFullType();
        return mt.equals(MimeTypes.PLAIN_TEXT) || mt.equals(MimeTypes.OCTET_STREAM) || mt.equals(MimeTypes.XML);
    }

    /**
     * Triggers the execution of all the {@link Extractor} registered to this class using the <i>default</i> extraction
     * parameters.
     *
     * @throws IOException
     *             if there is an error reading input from the document source
     * @throws ExtractionException
     *             if there is an error duing distraction
     *
     * @return the extraction report.
     */
    public SingleDocumentExtractionReport run() throws IOException, ExtractionException {
        return run(ExtractionParameters.newDefault(configuration));
    }

    /**
     * Returns the detected mimetype for the given {@link DocumentSource}.
     *
     * @return string containing the detected mimetype.
     *
     * @throws IOException
     *             if an error occurred while accessing the data.
     */
    public String getDetectedMIMEType() throws IOException {
        filterExtractorsByMIMEType();
        return detectedMIMEType == null ? null : detectedMIMEType.toString();
    }

    /**
     * Check whether the given {@link DocumentSource} content activates of not at least an
     * extractor.
     *
     * @return <code>true</code> if at least an extractor is activated, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error locating matching extractors
     */
    public boolean hasMatchingExtractors() throws IOException {
        filterExtractorsByMIMEType();
        return !matchingExtractors.isEmpty();
    }

    /**
     * @return the list of all the activated extractors for the given {@link DocumentSource}.
     */
    @SuppressWarnings("rawtypes")
    public List<Extractor> getMatchingExtractors() {
        final List<Extractor> extractorsList = new ArrayList<>();
        for (ExtractorFactory extractorFactory : matchingExtractors) {
            extractorsList.add(extractorFactory.createExtractor());
        }
        return extractorsList;
    }

    /**
     * @return the configured parsing encoding.
     */
    public String getParserEncoding() {
        if (this.parserEncoding == null) {
            this.parserEncoding = detectEncoding();
        }
        return this.parserEncoding;
    }

    /**
     * Sets the document parser encoding.
     *
     * @param encoding
     *            parser encoding.
     */
    public void setParserEncoding(String encoding) {
        this.parserEncoding = encoding;
        documentReport = null;
    }

    /**
     * Chech whether the given {@link DocumentSource} is an <b>HTML</b> document.
     *
     * @return <code>true</code> if the document source is an HTML document.
     *
     * @throws IOException
     *             if an error occurs while accessing data.
     */
    private boolean isHTMLDocument() throws IOException {
        filterExtractorsByMIMEType();
        return !matchingExtractors.filterByMIMEType(MIMEType.parse("text/html")).isEmpty();
    }

    /**
     * Extracts the document language where possible.
     *
     * @param extractionParameters
     *            extraction parameters to be applied to determine the document language.
     *
     * @return the document language if any, <code>null</code> otherwise.
     *
     * @throws IOException
     *             if an error occurs during the document analysis.
     * @throws ValidatorException
     */
    private String extractDocumentLanguage(ExtractionParameters extractionParameters)
            throws IOException, ValidatorException {
        if (!isHTMLDocument()) {
            return null;
        }
        final HTMLDocument document;
        try {
            document = new HTMLDocument(getTagSoupDOM(extractionParameters).getDocument());
        } catch (IOException ioe) {
            log.debug("Cannot extract language from document.", ioe);
            return null;
        }
        return document.getDefaultLanguage();
    }

    /**
     * Generates a list of extractors that can be applied to the given document.
     *
     * @throws IOException
     */
    private void filterExtractorsByMIMEType() throws IOException {
        if (matchingExtractors != null)
            return; // has already been run.

        if (detector == null || extractors.allExtractorsSupportAllContentTypes()) {
            matchingExtractors = extractors;
            return;
        }
        ensureHasLocalCopy();
        // detect MIME based on the real file IRI rather than based on given base namespace
        detectedMIMEType = detector.guessMIMEType(java.net.URI.create(in.getDocumentIRI()).getPath(),
                localDocumentSource.openInputStream(), MIMEType.parse(localDocumentSource.getContentType()));
        log.debug("detected media type: " + detectedMIMEType);
        matchingExtractors = extractors.filterByMIMEType(detectedMIMEType);
    }

    /**
     * Triggers the execution of a specific {@link Extractor}.
     *
     * @param extractionParameters
     *            the parameters used for the extraction.
     * @param extractor
     *            the {@link Extractor} to be executed.
     *
     * @throws ExtractionException
     *             if an error specific to an extractor happens.
     * @throws IOException
     *             if an IO error occurs during the extraction.
     *
     * @return the roots of the resources that have been extracted.
     *
     * @throws ValidatorException
     *             if an error occurs during validation.
     */
    private SingleExtractionReport runExtractor(final ExtractionParameters extractionParameters,
            final String documentLanguage, final Extractor<?> extractor)
            throws ExtractionException, IOException, ValidatorException {
        if (log.isDebugEnabled()) {
            log.debug("Running {} on {}", extractor.getDescription().getExtractorName(), documentIRI);
        }
        long startTime = System.currentTimeMillis();
        final ExtractionContext extractionContext = new ExtractionContext(extractor.getDescription().getExtractorName(),
                documentIRI, documentLanguage);
        final io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResultImpl extractionResult = new ExtractionResultImpl(extractionContext, extractor, output);
        try {
            if (extractor instanceof BlindExtractor) {
                final BlindExtractor blindExtractor = (BlindExtractor) extractor;
                blindExtractor.run(extractionParameters, extractionContext, documentIRI, extractionResult);
            } else if (extractor instanceof ContentExtractor) {
                ensureHasLocalCopy();
                final ContentExtractor contentExtractor = (ContentExtractor) extractor;
                contentExtractor.run(extractionParameters, extractionContext, localDocumentSource.openInputStream(),
                        extractionResult);
            } else if (extractor instanceof TagSoupDOMExtractor) {
                final TagSoupDOMExtractor tagSoupDOMExtractor = (TagSoupDOMExtractor) extractor;
                final DocumentReport documentReport = getTagSoupDOM(extractionParameters);
                tagSoupDOMExtractor.run(extractionParameters, extractionContext, documentReport.getDocument(),
                        extractionResult);
            } else {
                throw new IllegalStateException("Extractor type not supported: " + extractor.getClass());
            }
            return new SingleExtractionReport(extractionResult.getIssues(),
                    new ArrayList<ResourceRoot>(extractionResult.getResourceRoots()),
                    new ArrayList<PropertyPath>(extractionResult.getPropertyPaths()), extractionResult.wasTouched());
        } catch (ExtractionException ex) {
            if (log.isDebugEnabled()) {
                log.debug(extractor.getDescription().getExtractorName() + ": " + ex.getMessage());
            }
            throw ex;
        } finally {
            // Logging result error report.
            if (log.isDebugEnabled() && extractionResult.hasIssues()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                extractionResult.printReport(new PrintStream(baos, true, "UTF-8"));
                log.debug(baos.toString("UTF-8"));
            }
            extractionResult.close();

            long elapsed = System.currentTimeMillis() - startTime;
            if (log.isDebugEnabled()) {
                log.debug("Completed " + extractor.getDescription().getExtractorName() + ", " + elapsed + "ms");
            }
        }
    }

    /**
     * Forces the retrieval of the document data.
     *
     * @throws IOException
     */
    private void ensureHasLocalCopy() throws IOException {
        if (localDocumentSource != null)
            return;
        if (in.isLocal()) {
            localDocumentSource = in;
            return;
        }
        if (copyFactory == null) {
            copyFactory = new MemCopyFactory();
        }
        localDocumentSource = copyFactory.createLocalCopy(in);
    }

    /**
     * Returns the DOM of the given document source (that must be an HTML stream) and the report of eventual fixes
     * applied on it.
     *
     * @param extractionParameters
     *            parameters to be used during extraction.
     *
     * @return document report.
     *
     * @throws IOException
     *             if an error occurs during data access.
     * @throws ValidatorException
     *             if an error occurs during validation.
     */
    private DocumentReport getTagSoupDOM(ExtractionParameters extractionParameters)
            throws IOException, ValidatorException {
        if (documentReport == null || !extractionParameters.equals(tagSoupDOMRelatedParameters)) {
            ensureHasLocalCopy();
            final InputStream is = new BufferedInputStream(localDocumentSource.openInputStream());
            is.mark(Integer.MAX_VALUE);
            final String candidateEncoding = getParserEncoding();
            is.reset();
            final TagSoupParser tagSoupParser = new TagSoupParser(is, documentIRI.stringValue(), candidateEncoding);
            if (extractionParameters.isValidate()) {
                documentReport = tagSoupParser.getValidatedDOM(extractionParameters.isFix());
            } else {
                documentReport = new DocumentReport(EmptyValidationReport.getInstance(), tagSoupParser.getDOM());
            }
            tagSoupDOMRelatedParameters = extractionParameters;
        }
        return documentReport;
    }

    /**
     * Detects the encoding of the local document source input stream.
     *
     * @return a valid encoding value.
     */
    private String detectEncoding() {
        try {
            ensureHasLocalCopy();
            InputStream is = new BufferedInputStream(localDocumentSource.openInputStream());
            String encoding = this.encoderDetector.guessEncoding(is, localDocumentSource.getContentType());
            is.close();
            return encoding;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while trying to detect the input encoding.", e);
        }
    }

    /**
     * This function verifies if the <i>candidateSub</i> list of strings is a prefix of <i>list</i>.
     *
     * @param list
     *            a list of strings.
     * @param candidateSub
     *            a list of strings.
     *
     * @return <code>true</code> if <i>candidateSub</i> is a sub path of <i>list</i>, <code>false</code> otherwise.
     */
    private boolean subPath(String[] list, String[] candidateSub) {
        if (candidateSub.length > list.length) {
            return false;
        }
        for (int i = 0; i < candidateSub.length; i++) {
            if (!candidateSub[i].equals(list[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds for every resource root node a page domain triple.
     *
     * @param resourceRoots
     *            list of resource roots.
     * @param context
     *            extraction context to produce triples.
     *
     * @throws ExtractionException
     */
    private void addDomainTriplesPerResourceRoots(List<ResourceRoot> resourceRoots, ExtractionContext context)
            throws ExtractionException {
        try {
            // Add source Web domains to every resource root.
            String domain;
            try {
                domain = new java.net.URI(in.getDocumentIRI()).getHost();
            } catch (URISyntaxException urise) {
                throw new IllegalArgumentException("An error occurred while extracting the host from the document IRI.",
                        urise);
            }
            if (domain != null) {
                for (ResourceRoot resourceRoot : resourceRoots) {
                    output.receiveTriple(resourceRoot.getRoot(), vSINDICE.getProperty(SINDICE.DOMAIN),
                            SimpleValueFactory.getInstance().createLiteral(domain), null, context);
                }
            }
        } catch (TripleHandlerException e) {
            throw new ExtractionException("Error while writing triple triple.", e);
        } finally {
            try {
                output.closeContext(context);
            } catch (TripleHandlerException e) {
                throw new ExtractionException("Error while closing context.", e);
            }
        }
    }

    /**
     * @return an extraction context specific for consolidation triples.
     */
    private ExtractionContext createExtractionContext(String defaultLanguage) {
        return new ExtractionContext("consolidation-extractor", documentIRI, defaultLanguage,
                UUID.randomUUID().toString());
    }

    /**
     * Detect the nesting relationship among different Microformats and explicit them adding connection triples.
     *
     * @param resourceRoots
     * @param propertyPaths
     * @param context
     *
     * @throws TripleHandlerException
     */
    private void addNestingRelationship(List<ResourceRoot> resourceRoots, List<PropertyPath> propertyPaths,
            ExtractionContext context) throws TripleHandlerException {
        ResourceRoot currentResourceRoot;
        PropertyPath currentPropertyPath;
        for (int r = 0; r < resourceRoots.size(); r++) {
            currentResourceRoot = resourceRoots.get(r);
            for (int p = 0; p < propertyPaths.size(); p++) {
                currentPropertyPath = propertyPaths.get(p);
                Class<? extends MicroformatExtractor> currentResourceRootExtractor = currentResourceRoot.getExtractor();
                Class<? extends MicroformatExtractor> currentPropertyPathExtractor = currentPropertyPath.getExtractor();
                // Avoid wrong nesting relationships.
                if (currentResourceRootExtractor.equals(currentPropertyPathExtractor)) {
                    continue;
                }
                // Avoid self declaring relationships
                if (MicroformatExtractor.includes(currentPropertyPathExtractor, currentResourceRootExtractor)) {
                    continue;
                }
                if (subPath(currentResourceRoot.getPath(), currentPropertyPath.getPath())) {
                    createNestingRelationship(currentPropertyPath, currentResourceRoot, output, context);
                }
            }
        }
    }

    /**
     * This method consolidates the graphs extracted from the same document. In particular it adds:
     * <ul>
     * <li>for every microformat root node a triple indicating the original Web page domain;</li>
     * <li>triples indicating the nesting relationship among a microformat root and property paths of other nested
     * microformats.</li>
     * </ul>
     *
     * @param resourceRoots
     *            list of RDF nodes representing roots of extracted microformat graphs and the corresponding HTML paths.
     * @param propertyPaths
     *            list of RDF nodes representing property subjects, property IRIs and the HTML paths from which such
     *            properties have been extracted.
     * @param addDomainTriples
     * @param output
     *            a triple handler event collector.
     *
     * @return
     *
     * @throws ExtractionException
     */
    private ExtractionContext consolidateResources(List<ResourceRoot> resourceRoots, List<PropertyPath> propertyPaths,
            boolean addDomainTriples, TripleHandler output, String defaultLanguage) throws ExtractionException {
        final ExtractionContext context = createExtractionContext(defaultLanguage);

        try {
            output.openContext(context);
        } catch (TripleHandlerException e) {
            throw new ExtractionException(
                    String.format(Locale.ROOT, "Error starting document with IRI %s", documentIRI), e);
        }

        try {
            if (addDomainTriples) {
                addDomainTriplesPerResourceRoots(resourceRoots, context);
            }
            addNestingRelationship(resourceRoots, propertyPaths, context);
        } catch (TripleHandlerException the) {
            throw new ExtractionException("Error while writing triple triple.", the);
        } finally {
            try {
                output.closeContext(context);
            } catch (TripleHandlerException e) {
                throw new ExtractionException("Error while closing context.", e);
            }
        }

        return context;
    }

    /**
     * This method consolidates the graphs extracted from the same document. In particular it adds:
     * <ul>
     * <li>for every microformat root node a triple indicating the original Web page domain;</li>
     * </ul>
     *
     * @param resourceRoots
     *            list of RDF nodes representing roots of extracted microformat graphs and the corresponding HTML paths.
     *            from which such properties have been extracted.
     * @param addDomainTriples
     * @param output
     *            a triple handler event collector.
     *
     * @return
     *
     * @throws ExtractionException
     */
    private ExtractionContext consolidateResources(List<ResourceRoot> resourceRoots, boolean addDomainTriples,
            TripleHandler output, String defaultLanguage) throws ExtractionException {
        final ExtractionContext context = createExtractionContext(defaultLanguage);

        try {
            output.openContext(context);
        } catch (TripleHandlerException e) {
            throw new ExtractionException(
                    String.format(Locale.ROOT, "Error starting document with IRI %s", documentIRI), e);
        }

        try {
            if (addDomainTriples) {
                addDomainTriplesPerResourceRoots(resourceRoots, context);
            }
        } finally {
            try {
                output.closeContext(context);
            } catch (TripleHandlerException the) {
                throw new ExtractionException("Error while closing context.", the);
            }
        }

        return context;
    }

    /**
     * Adds metadata triples containing the number of extracted triples and the extraction timestamp.
     *
     * @param context
     *
     * @throws TripleHandlerException
     */
    private void addExtractionTimeSizeMetaTriples(ExtractionContext context) throws TripleHandlerException {
        // adding extraction date
        String xsdDateTimeNow = RDFUtils.toXSDDateTime(new Date());
        output.receiveTriple(SimpleValueFactory.getInstance().createIRI(documentIRI.toString()),
                vSINDICE.getProperty(SINDICE.DATE), SimpleValueFactory.getInstance().createLiteral(xsdDateTimeNow),
                null, context);

        // adding number of extracted triples
        int numberOfTriples = 0;
        CompositeTripleHandler cth = (CompositeTripleHandler) output;
        for (TripleHandler th : cth.getChilds()) {
            if (th instanceof CountingTripleHandler) {
                numberOfTriples = ((CountingTripleHandler) th).getCount();
            }
        }
        output.receiveTriple(SimpleValueFactory.getInstance().createIRI(documentIRI.toString()),
                vSINDICE.getProperty(SINDICE.SIZE), SimpleValueFactory.getInstance().createLiteral(numberOfTriples + 1), // the
                                                                                                                         // number
                                                                                                                         // of
                                                                                                                         // triples
                                                                                                                         // plus
                                                                                                                         // itself
                null, context);
    }

    /**
     * Creates a nesting relationship triple.
     *
     * @param from
     *            the property containing the nested microformat.
     * @param to
     *            the root to the nested microformat.
     * @param th
     *            the triple handler.
     * @param ec
     *            the extraction context used to add such information.
     *
     * @throws TripleHandlerException
     */
    private void createNestingRelationship(PropertyPath from, ResourceRoot to, TripleHandler th, ExtractionContext ec)
            throws TripleHandlerException {
        final BNode fromObject = from.getObject();
        final String bNodeHash = from.getProperty().stringValue() + (fromObject == null ? "" : fromObject.getID());
        BNode bnode = RDFUtils.getBNode(bNodeHash);
        th.receiveTriple(bnode, vSINDICE.getProperty(SINDICE.NESTING_ORIGINAL), from.getProperty(), null, ec);
        th.receiveTriple(bnode, vSINDICE.getProperty(SINDICE.NESTING_STRUCTURED),
                from.getObject() == null ? to.getRoot() : from.getObject(), null, ec);
        th.receiveTriple(from.getSubject(), vSINDICE.getProperty(SINDICE.NESTING), bnode, null, ec);
    }

    /**
     * Entity detection report.
     */
    private static class SingleExtractionReport {
        private final Collection<IssueReport.Issue> issues;
        private final List<ResourceRoot> resourceRoots;
        private final List<PropertyPath> propertyPaths;
        private final boolean touched;

        public SingleExtractionReport(Collection<IssueReport.Issue> issues, List<ResourceRoot> resourceRoots,
                List<PropertyPath> propertyPaths, boolean wasTouched) {
            this.issues = issues;
            this.resourceRoots = resourceRoots;
            this.propertyPaths = propertyPaths;
            this.touched = wasTouched;
        }
    }

}
