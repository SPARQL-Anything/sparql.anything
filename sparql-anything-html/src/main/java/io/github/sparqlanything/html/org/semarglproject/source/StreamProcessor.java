/**
 * Copyright 2012-2013 the Semargl contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.sparqlanything.html.org.semarglproject.source;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.sink.DataSink;
import io.github.sparqlanything.html.org.semarglproject.source.AbstractSource;
import io.github.sparqlanything.html.org.semarglproject.source.BaseStreamProcessor;
import io.github.sparqlanything.html.org.semarglproject.source.XmlSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.io.Reader;

/**
 * Simple pipeline managing wrapper. Automatically instantiates source appropriate for specified sink.
 * Provides processing and setup methods.
 * <br>
 * List of supported properties:
 *     <ul>
 *         <li>{@link #XML_READER_PROPERTY}</li>
 *     </ul>
 */
public final class StreamProcessor extends BaseStreamProcessor {

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * Allows to specify custom {@link XMLReader} used with SAX parsers.
     */
    public static final String XML_READER_PROPERTY = "http://semarglproject.org/core/properties/xml-parser";

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * Enables or disables error recovery mechanism.
     */
    public static final String ENABLE_ERROR_RECOVERY =
            "http://semarglproject.org/core/properties/enable-error-recovery";

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * Allows to specify handler for processor events.
     * Subclass of {@link io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler} must be passed as a value.
     */
    public static final String PROCESSOR_GRAPH_HANDLER_PROPERTY =
            "http://semarglproject.org/core/properties/processor-graph-handler";

    private final DataSink sink;
    private final io.github.sparqlanything.html.org.semarglproject.source.AbstractSource source;

    /**
     * Instantiates stream processor for pipe starting with specified sink.
     * @param sink pipe's input
     */
    public StreamProcessor(DataSink sink) {
        this.sink = sink;
        this.source = createSourceForSink(sink);
    }

    @Override
    public void processInternal(InputStream inputStream, String mimeType, String baseUri) throws ParseException {
        source.process(inputStream, mimeType, baseUri);
    }

    @Override
    protected void startStream() throws ParseException {
        sink.startStream();
    }

    @Override
    protected void endStream() throws ParseException {
        sink.endStream();
    }

    @Override
    public void processInternal(Reader reader, String mimeType, String baseUri) throws ParseException {
        source.process(reader, mimeType, baseUri);
    }

    @Override
    public boolean setProperty(String key, Object value) {
        boolean result = false;
        if (XML_READER_PROPERTY.equals(key) && value instanceof XMLReader && source instanceof io.github.sparqlanything.html.org.semarglproject.source.XmlSource) {
            try {
                if (value != null) {
                    ((XmlSource) source).setXmlReader((XMLReader) value);
                    result = true;
                }
            } catch(SAXException e) {
                throw new IllegalArgumentException("XMLReader was not able to be initialized", e);
            }
        }
        return sink.setProperty(key, value) || result;
    }

}
