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

import io.github.sparqlanything.html.org.apache.any23.ExtractionReport;
import io.github.sparqlanything.html.org.apache.any23.configuration.Configuration;
import io.github.sparqlanything.html.org.apache.any23.configuration.DefaultConfiguration;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionParameters;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorGroup;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorRegistryImpl;
import io.github.sparqlanything.html.org.apache.any23.extractor.SingleDocumentExtraction;
import io.github.sparqlanything.html.org.apache.any23.extractor.SingleDocumentExtractionReport;
import io.github.sparqlanything.html.org.apache.any23.http.AcceptHeaderBuilder;
import io.github.sparqlanything.html.org.apache.any23.http.DefaultHTTPClient;
import io.github.sparqlanything.html.org.apache.any23.http.DefaultHTTPClientConfiguration;
import io.github.sparqlanything.html.org.apache.any23.http.HTTPClient;
import io.github.sparqlanything.html.org.apache.any23.mime.MIMEType;
import io.github.sparqlanything.html.org.apache.any23.mime.MIMETypeDetector;
import io.github.sparqlanything.html.org.apache.any23.mime.TikaMIMETypeDetector;
import io.github.sparqlanything.html.org.apache.any23.mime.purifier.WhiteSpacesPurifier;
import io.github.sparqlanything.html.org.apache.any23.source.DocumentSource;
import io.github.sparqlanything.html.org.apache.any23.source.FileDocumentSource;
import io.github.sparqlanything.html.org.apache.any23.source.HTTPDocumentSource;
import io.github.sparqlanything.html.org.apache.any23.source.LocalCopyFactory;
import io.github.sparqlanything.html.org.apache.any23.source.MemCopyFactory;
import io.github.sparqlanything.html.org.apache.any23.source.StringDocumentSource;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * A facade with convenience methods for typical <i>Any23</i> extraction operations.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class Any23 {

    /**
     * Any23 core library version. NOTE: there's also a version string in pom.xml, they should match.
     */
    public static final String VERSION = DefaultConfiguration.singleton().getPropertyOrFail("any23.core.version");

    /**
     * Default HTTP User Agent defined in default configuration.
     */
    public static final String DEFAULT_HTTP_CLIENT_USER_AGENT = DefaultConfiguration.singleton()
            .getPropertyOrFail("any23.http.user.agent.default");

    protected static final Logger logger = LoggerFactory.getLogger(Any23.class);

    private final Configuration configuration;
    private final String defaultUserAgent;

    private MIMETypeDetector mimeTypeDetector = new TikaMIMETypeDetector(new WhiteSpacesPurifier());

    private HTTPClient httpClient = new DefaultHTTPClient();

    private boolean httpClientInitialized = false;

    private final ExtractorGroup factories;
    private LocalCopyFactory streamCache;
    private String userAgent;

    /**
     * Constructor that allows the specification of a custom configuration and of a list of extractors.
     *
     * @param configuration
     *            configuration used to build the <i>Any23</i> instance.
     * @param extractorGroup
     *            the group of extractors to be applied.
     */
    public Any23(Configuration configuration, ExtractorGroup extractorGroup) {
        if (configuration == null)
            throw new NullPointerException("configuration must be not null.");
        this.configuration = configuration;
        if (logger.isDebugEnabled()) {
            logger.debug(configuration.getConfigurationDump());
        }

        this.defaultUserAgent = configuration.getPropertyOrFail("any23.http.user.agent.default");

        this.factories = (extractorGroup == null) ? ExtractorRegistryImpl.getInstance().getExtractorGroup()
                : extractorGroup;
        setCacheFactory(new MemCopyFactory());
    }

    /**
     * Constructor that allows the specification of a list of extractors.
     *
     * @param extractorGroup
     *            the group of extractors to be applied.
     */
    public Any23(ExtractorGroup extractorGroup) {
        this(DefaultConfiguration.singleton(), extractorGroup);
    }

    /**
     * Constructor that allows the specification of a custom configuration and of list of extractor names.
     *
     * @param configuration
     *            a {@link Configuration} object
     * @param extractorNames
     *            list of extractor's names.
     */
    public Any23(Configuration configuration, String... extractorNames) {
        this(configuration, extractorNames == null ? null
                : ExtractorRegistryImpl.getInstance().getExtractorGroup(Arrays.asList(extractorNames)));
    }

    /**
     * Constructor that allows the specification of a list of extractor names.
     *
     * @param extractorNames
     *            list of extractor's names.
     */
    public Any23(String... extractorNames) {
        this(DefaultConfiguration.singleton(), extractorNames);
    }

    /**
     * Constructor accepting {@link Configuration}.
     *
     * @param configuration
     *            a {@link Configuration} object
     */
    public Any23(Configuration configuration) {
        this(configuration, (String[]) null);
    }

    /**
     * Constructor with default configuration.
     */
    public Any23() {
        this(DefaultConfiguration.singleton());
    }

    /**
     * Sets the <i>HTTP Header User Agent</i>, see <i>RFC 2616-14.43</i>.
     *
     * @param userAgent
     *            text describing the user agent.
     */
    public void setHTTPUserAgent(String userAgent) {
        if (httpClientInitialized) {
            throw new IllegalStateException("Cannot change HTTP configuration after client has been initialized");
        }
        if (userAgent == null) {
            userAgent = defaultUserAgent;
        }
        if (userAgent.trim().length() == 0) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Invalid user agent: '%s'", userAgent));
        }
        this.userAgent = userAgent;
    }

    /**
     * Returns the <i>HTTP Header User Agent</i>, see <i>RFC 2616-14.43</i>.
     *
     * @return text describing the user agent.
     */
    public String getHTTPUserAgent() {
        return this.userAgent;
    }

    /**
     * Allows to set the {@link HTTPClient} implementation used to retrieve contents. The default
     * instance is {@link DefaultHTTPClient}.
     *
     * @param httpClient
     *            a valid client instance.
     *
     * @throws IllegalStateException
     *             if invoked after client has been initialized.
     */
    public void setHTTPClient(HTTPClient httpClient) {
        if (httpClient == null) {
            throw new NullPointerException("httpClient cannot be null.");
        }
        if (httpClientInitialized) {
            throw new IllegalStateException("Cannot change HTTP configuration after client has been initialized");
        }
        this.httpClient = httpClient;
    }

    /**
     * Returns the current {@link HTTPClient} implementation.
     *
     * @return instance of HTTPClient.
     *
     * @throws IOException
     *             if the HTTP client has not initialized.
     */
    public HTTPClient getHTTPClient() throws IOException {
        if (!httpClientInitialized) {
            if (userAgent == null) {
                throw new IOException("Must call " + Any23.class.getSimpleName()
                        + ".setHTTPUserAgent(String) before extracting from HTTP IRI");
            }
            httpClient.init(new DefaultHTTPClientConfiguration(this.getAcceptHeader()));
            httpClientInitialized = true;
        }
        return httpClient;
    }

    /**
     * Allows to set a {@link LocalCopyFactory} instance.
     *
     * @param cache
     *            valid cache instance.
     */
    public void setCacheFactory(LocalCopyFactory cache) {
        if (cache == null) {
            throw new NullPointerException("cache cannot be null.");
        }
        this.streamCache = cache;
    }

    /**
     * Allows to set an instance of {@link MIMETypeDetector}.
     *
     * @param detector
     *            a valid detector instance, if <code>null</code> all the detectors will be used.
     */
    public void setMIMETypeDetector(MIMETypeDetector detector) {
        this.mimeTypeDetector = detector;
    }

    /**
     * <p>
     * Returns the most appropriate {@link DocumentSource} for the given<code>documentIRI</code>.
     * </p>
     * <p>
     * <b>N.B.</b> <code>documentIRI's</code> <i>should</i> contain a protocol. E.g. <b>http:</b>, <b>https:</b>,
     * <b>file:</b>
     * </p>
     *
     * @param documentIRI
     *            the document <i>IRI</i>.
     *
     * @return a new instance of DocumentSource.
     *
     * @throws URISyntaxException
     *             if an error occurs while parsing the <code>documentIRI</code> as a <i>IRI</i>.
     * @throws IOException
     *             if an error occurs while initializing the internal {@link HTTPClient}.
     */
    public DocumentSource createDocumentSource(String documentIRI) throws URISyntaxException, IOException {
        if (documentIRI == null)
            throw new NullPointerException("documentIRI cannot be null.");
        if (documentIRI.toLowerCase(Locale.ROOT).startsWith("file:")) {
            return new FileDocumentSource(new File(new URI(documentIRI)));
        }
        if (documentIRI.toLowerCase(Locale.ROOT).startsWith("http:")
                || documentIRI.toLowerCase(Locale.ROOT).startsWith("https:")) {
            return new HTTPDocumentSource(getHTTPClient(), documentIRI);
        }
        throw new IllegalArgumentException(String.format(Locale.ROOT,
                "Unsupported protocol for document IRI: '%s' . " + "Check that document IRI contains a protocol.",
                documentIRI));
    }

    /**
     * Performs metadata extraction from the content of the given <code>in</code> document source, sending the generated
     * events to the specified <code>outputHandler</code>.
     *
     * @param eps
     *            the extraction parameters to be applied.
     * @param in
     *            the input document source.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     * @param encoding
     *            explicit encoding see <a href="http://www.iana.org/assignments/character-sets">available
     *            encodings</a>.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(ExtractionParameters eps, DocumentSource in, TripleHandler outputHandler,
                                    String encoding) throws IOException, ExtractionException {
        final SingleDocumentExtraction ex = new SingleDocumentExtraction(configuration, in, factories, outputHandler);
        ex.setMIMETypeDetector(mimeTypeDetector);
        ex.setLocalCopyFactory(streamCache);
        ex.setParserEncoding(encoding);
        final SingleDocumentExtractionReport sder = ex.run(eps);
        return new ExtractionReport(ex.getMatchingExtractors(), ex.getParserEncoding(), ex.getDetectedMIMEType(),
                sder.getValidationReport(), sder.getExtractorToIssues());
    }

    /**
     * Performs metadata extraction on the <code>in</code> string associated to the <code>documentIRI</code> IRI,
     * declaring <code>contentType</code> and <code>encoding</code>. The generated events are sent to the specified
     * <code>outputHandler</code>.
     *
     * @param in
     *            raw data to be analyzed.
     * @param documentIRI
     *            IRI from which the raw data has been extracted.
     * @param contentType
     *            declared data content type.
     * @param encoding
     *            declared data encoding.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(String in, String documentIRI, String contentType, String encoding,
            TripleHandler outputHandler) throws IOException, ExtractionException {
        return extract(new StringDocumentSource(in, documentIRI, contentType, encoding), outputHandler);
    }

    /**
     * Performs metadata extraction on the <code>in</code> string associated to the <code>documentIRI</code> IRI,
     * sending the generated events to the specified <code>outputHandler</code>.
     *
     * @param in
     *            raw data to be analyzed.
     * @param documentIRI
     *            IRI from which the raw data has been extracted.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(String in, String documentIRI, TripleHandler outputHandler)
            throws IOException, ExtractionException {
        return extract(new StringDocumentSource(in, documentIRI), outputHandler);
    }

    /**
     * Performs metadata extraction from the content of the given <code>file</code> sending the generated events to the
     * specified <code>outputHandler</code>.
     *
     * @param file
     *            file containing raw data.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(File file, TripleHandler outputHandler) throws IOException, ExtractionException {
        return extract(new FileDocumentSource(file), outputHandler);
    }

    /**
     * Performs metadata extraction from the content of the given <code>documentIRI</code> sending the generated events
     * to the specified <code>outputHandler</code>. If the <i>IRI</i> is replied with a redirect, the last will be
     * followed.
     *
     * @param eps
     *            the parameters to be applied to the extraction.
     * @param documentIRI
     *            the IRI from which retrieve document.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(ExtractionParameters eps, String documentIRI, TripleHandler outputHandler)
            throws IOException, ExtractionException {
        try {
            return extract(eps, createDocumentSource(documentIRI), outputHandler);
        } catch (URISyntaxException ex) {
            throw new ExtractionException("Error while extracting data from document IRI.", ex);
        }
    }

    /**
     * Performs metadata extraction from the content of the given <code>documentIRI</code> sending the generated events
     * to the specified <code>outputHandler</code>. If the <i>IRI</i> is replied with a redirect, the last will be
     * followed.
     *
     * @param documentIRI
     *            the IRI from which retrieve document.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(String documentIRI, TripleHandler outputHandler)
            throws IOException, ExtractionException {
        return extract((ExtractionParameters) null, documentIRI, outputHandler);
    }

    /**
     * Performs metadata extraction from the content of the given <code>in</code> document source, sending the generated
     * events to the specified <code>outputHandler</code>.
     *
     * @param in
     *            the input document source.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     * @param encoding
     *            explicit encoding see <a href="http://www.iana.org/assignments/character-sets">available
     *            encodings</a>.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(DocumentSource in, TripleHandler outputHandler, String encoding)
            throws IOException, ExtractionException {
        return extract(null, in, outputHandler, encoding);
    }

    /**
     * Performs metadata extraction from the content of the given <code>in</code> document source, sending the generated
     * events to the specified <code>outputHandler</code>.
     *
     * @param in
     *            the input document source.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(DocumentSource in, TripleHandler outputHandler)
            throws IOException, ExtractionException {
        return extract(null, in, outputHandler, null);
    }

    /**
     * Performs metadata extraction from the content of the given <code>in</code> document source, sending the generated
     * events to the specified <code>outputHandler</code>.
     *
     * @param eps
     *            the parameters to be applied for the extraction phase.
     * @param in
     *            the input document source.
     * @param outputHandler
     *            handler responsible for collecting of the extracted metadata.
     *
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error reading the {@link DocumentSource}
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    public ExtractionReport extract(ExtractionParameters eps, DocumentSource in, TripleHandler outputHandler)
            throws IOException, ExtractionException {
        return extract(eps, in, outputHandler, null);
    }

    private String getAcceptHeader() {
        Collection<MIMEType> mimeTypes = new ArrayList<>();
        for (ExtractorFactory<?> factory : factories) {
            mimeTypes.addAll(factory.getSupportedMIMETypes());
        }
        return new AcceptHeaderBuilder(mimeTypes).getAcceptHeader();
    }

}
