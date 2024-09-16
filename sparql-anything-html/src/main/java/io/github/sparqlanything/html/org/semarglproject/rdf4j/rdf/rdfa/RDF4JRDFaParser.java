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
package io.github.sparqlanything.html.org.semarglproject.rdf4j.rdf.rdfa;

import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.rio.ParseErrorListener;
import org.eclipse.rdf4j.rio.ParseLocationListener;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RioSetting;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.RDFaParserSettings;
import org.eclipse.rdf4j.rio.helpers.RDFaVersion;
import io.github.sparqlanything.html.org.semarglproject.source.StreamProcessor;
import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.RdfaParser;
import io.github.sparqlanything.html.org.semarglproject.rdf4j.core.sink.RDF4JSink;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDFa;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of Sesame's RDFParser on top of Semargl RDFaParser.
 *
 * @author Peter Ansell p_ansell@yahoo.com
 * @author Lev Khomich levkhomich@gmail.com
 *
 */
public final class RDF4JRDFaParser implements RDFParser, ProcessorGraphHandler {

    private ParserConfig parserConfig;

    private final StreamProcessor streamProcessor;

    private ParseErrorListener parseErrorListener;

    /**
     * Default constructor. Creates RDFa parser in 1.1 mode with disabled vocabulary expansion feature.
     * <p>
     * Supported settings can be found using {@link #getSupportedSettings()} and can be modified using
     * the {@link ParserConfig} object returned from the {@link #getParserConfig()} method.
     */
    public RDF4JRDFaParser() {
        streamProcessor = new StreamProcessor(RdfaParser.connect(RDF4JSink.connect(null)));
        streamProcessor.setProperty(StreamProcessor.PROCESSOR_GRAPH_HANDLER_PROPERTY, this);
        setParserConfig(new ParserConfig());
        // by default this would be set to false if not set here
        setPreserveBNodeIDs(true);
        parseErrorListener = null;
    }

    /**
     * Constructor which allows to specify custom XMLReader.
     * @param xmlReader instance of XMLReader to be used in processing
     */
    public RDF4JRDFaParser(XMLReader xmlReader) {
        this();
        setXmlReader(xmlReader);
    }

    @Override
    public RDFFormat getRDFFormat() {
        return RDFFormat.RDFA;
    }

    @Override
    public void parse(InputStream in, String baseURI) throws RDFParseException, RDFHandlerException {
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        try {
            parse(reader, baseURI);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    @Override
    public void parse(Reader reader, String baseURI) throws RDFParseException, RDFHandlerException {
        refreshSettings();
        try {
            streamProcessor.process(reader, baseURI);
        } catch (ParseException e) {
            throw new RDFParseException(e);
        }
    }

    @Override
    public RDFParser setValueFactory(ValueFactory valueFactory) {
        streamProcessor.setProperty(RDF4JSink.VALUE_FACTORY_PROPERTY, valueFactory);
        return this;
    }

    @Override
    public RDFParser setRDFHandler(RDFHandler handler) {
        streamProcessor.setProperty(RDF4JSink.RDF_HANDLER_PROPERTY, handler);
        return this;
    }

    @Override
    public RDFParser setParseErrorListener(ParseErrorListener el) {
        this.parseErrorListener = el;
        return this;
    }

    @Override
    public RDFParser setParseLocationListener(ParseLocationListener ll) {
        // not supported yet
        return this;
    }

    @Override
    public RDFParser setParserConfig(ParserConfig config) {
        this.parserConfig = config;
        refreshSettings();
        return this;
    }

    @Override
    public ParserConfig getParserConfig() {
        return this.parserConfig;
    }

    @Override
    public Collection<RioSetting<?>> getSupportedSettings() {
        Collection<RioSetting<?>> result = new ArrayList<RioSetting<?>>(5);

        result.add(BasicParserSettings.PRESERVE_BNODE_IDS);
        result.add(SemarglParserSettings.PROCESSOR_GRAPH_ENABLED);
        result.add(RDFaParserSettings.VOCAB_EXPANSION_ENABLED);
        result.add(RDFaParserSettings.RDFA_COMPATIBILITY);
        result.add(SemarglParserSettings.CUSTOM_XML_READER);

        return result;
    }

    @Override
    public <T> RDFParser set(RioSetting<T> setting, T value) {
        parserConfig.set(setting, value);
        return this;
    }

    @Override
    public void setVerifyData(boolean verifyData) {
        // Does not support verification of data values, see getSupportedSettings for list of supported settings
    }

    @Override
    public void setPreserveBNodeIDs(boolean preserveBNodeIDs) {
        parserConfig.set(BasicParserSettings.PRESERVE_BNODE_IDS, preserveBNodeIDs);
        refreshSettings();
    }

    @Override
    public void setStopAtFirstError(boolean stopAtFirstError) {
        // Does not support changing this setting, see getSupportedSettings for list of supported settings
        // RDFa parser ignores all errors when it is possible to continue
    }

    @Override
    public void setDatatypeHandling(DatatypeHandling datatypeHandling) {
        // Does not support datatype handling, see getSupportedSettings for list of supported settings
    }

    /**
     * Changes {@link RdfaParser#ENABLE_PROCESSOR_GRAPH} setting
     * @param processorGraphEnabled new value to be set
     */
    public void setProcessorGraphEnabled(boolean processorGraphEnabled) {
        parserConfig.set(SemarglParserSettings.PROCESSOR_GRAPH_ENABLED, processorGraphEnabled);
        refreshSettings();
    }

    /**
     * Changes {@link RdfaParser#ENABLE_VOCAB_EXPANSION} setting
     * @param vocabExpansionEnabled new value to be set
     */
    public void setVocabExpansionEnabled(boolean vocabExpansionEnabled) {
        parserConfig.set(RDFaParserSettings.VOCAB_EXPANSION_ENABLED, vocabExpansionEnabled);
        refreshSettings();
    }

    /**
     * Changes {@link RdfaParser#RDFA_VERSION_PROPERTY} setting
     * @param rdfaCompatibility new value to be set
     */
    public void setRdfaCompatibility(short rdfaCompatibility) {
        // Map from RDFa short constants to Sesame RDFaVersion
        RDFaVersion version = RDFaVersion.RDFA_1_1;
        if(rdfaCompatibility == RDFa.VERSION_10) {
            version = RDFaVersion.RDFA_1_0;
        }
        else if(rdfaCompatibility == RDFa.VERSION_11) {
            version = RDFaVersion.RDFA_1_1;
        }
        setRdfaCompatibility(version);
    }

    /**
     * Changes {@link RdfaParser#RDFA_VERSION_PROPERTY} setting
     * @param version new value to be set
     */
    public void setRdfaCompatibility(RDFaVersion version) {
        parserConfig.set(RDFaParserSettings.RDFA_COMPATIBILITY, version);
        refreshSettings();
    }

    /**
     * Sets a custom {@link XMLReader}.
     * @param reader new value to be set
     */
    public void setXmlReader(XMLReader reader) {
        parserConfig.set(SemarglParserSettings.CUSTOM_XML_READER, reader);
        refreshSettings();
    }

    /**
     * Refreshes the settings on the stream processor using the current values from the parserConfig.
     */
    private void refreshSettings() {
        // Map from Sesame RDFaVersion to the RDFa short constants
        short rdfaCompatibility = RDFa.VERSION_11;
        RDFaVersion version = parserConfig.get(RDFaParserSettings.RDFA_COMPATIBILITY);
        if(version == RDFaVersion.RDFA_1_0) {
            rdfaCompatibility = RDFa.VERSION_10;
        }
        else if(version == RDFaVersion.RDFA_1_1) {
            rdfaCompatibility = RDFa.VERSION_11;
        }
        streamProcessor.setProperty(RdfaParser.RDFA_VERSION_PROPERTY, rdfaCompatibility);
        streamProcessor.setProperty(RdfaParser.ENABLE_VOCAB_EXPANSION,
                parserConfig.get(RDFaParserSettings.VOCAB_EXPANSION_ENABLED));
        streamProcessor.setProperty(RdfaParser.ENABLE_PROCESSOR_GRAPH,
                parserConfig.get(SemarglParserSettings.PROCESSOR_GRAPH_ENABLED));
        streamProcessor.setProperty(StreamProcessor.XML_READER_PROPERTY,
                parserConfig.get(SemarglParserSettings.CUSTOM_XML_READER));
    }

    @Override
    public void info(String infoClass, String message) {
    }

    @Override
    public void warning(String warningClass, String message) {
        if (parseErrorListener != null) {
            parseErrorListener.warning(message, -1, -1);
        }
    }

    @Override
    public void error(String errorClass, String message) {
        if (parseErrorListener != null) {
            parseErrorListener.error(message, -1, -1);
        }
    }

}
