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
import io.github.sparqlanything.html.org.semarglproject.sink.CharSink;
import io.github.sparqlanything.html.org.semarglproject.sink.DataSink;
import io.github.sparqlanything.html.org.semarglproject.sink.XmlSink;
import io.github.sparqlanything.html.org.semarglproject.source.AbstractSource;
import io.github.sparqlanything.html.org.semarglproject.source.CharSource;
import io.github.sparqlanything.html.org.semarglproject.source.XmlSource;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Pipeline managing class to subclass from.
 */
public abstract class BaseStreamProcessor {

    protected abstract void startStream() throws ParseException;

    protected abstract void endStream() throws ParseException;

    protected abstract void processInternal(Reader reader, String mimeType, String baseUri) throws ParseException;

    protected abstract void processInternal(InputStream inputStream, String mimeType,
                                            String baseUri) throws ParseException;

    /**
     * Key-value based settings. Property settings are passed to child sinks.
     * @param key property key
     * @param value property value
     * @return true if at least one sink understands specified property, false otherwise
     */
    public abstract boolean setProperty(String key, Object value);

    /**
     * Processes specified document's file using file path as base URI
     * @param file document's file
     * @throws ParseException
     */
    public final void process(File file) throws ParseException {
        String baseUri = "file://" + file.getAbsolutePath();
        process(file, baseUri);
    }

    /**
     * Processes specified document's file
     * @param file document's file
     * @param baseUri document's URI
     * @throws ParseException
     */
    public final void process(File file, String baseUri) throws ParseException {
        FileReader reader;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new ParseException(e);
        }
        try {
            process(reader, null, baseUri);
        } finally {
            closeQuietly(reader);
        }
    }

    /**
     * Processes document pointed by specified URI
     * @param uri document's URI
     * @throws ParseException
     */
    public final void process(String uri) throws ParseException {
        process(uri, uri);
    }

    /**
     * Processes document pointed by specified URI. Uses specified URI as document's base.
     * @param uri document's URI
     * @param baseUri document's URI
     * @throws ParseException
     */
    public final void process(String uri, String baseUri) throws ParseException {
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            throw new ParseException(e);
        }
        try {
            URLConnection urlConnection = url.openConnection();
            String mimeType = urlConnection.getContentType();
            InputStream inputStream = urlConnection.getInputStream();
            try {
                process(inputStream, mimeType, baseUri);
            } finally {
                closeQuietly(inputStream);
            }
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    /**
     * Processes stream input for document
     * @param inputStream document's input stream
     * @param baseUri document's base URI
     * @throws ParseException
     */
    public void process(InputStream inputStream, String baseUri) throws ParseException {
        process(inputStream, null, baseUri);
    }

    /**
     * Processes stream input for document
     * @param inputStream document's input stream
     * @param mimeType document's MIME type
     * @param baseUri document's base URI
     * @throws ParseException
     */
    public final void process(InputStream inputStream, String mimeType, String baseUri) throws ParseException {
        startStream();
        try {
            processInternal(inputStream, mimeType, baseUri);
        } finally {
            endStream();
        }
    }

    /**
     * Processes reader input for document's
     * @param reader document's reader
     * @throws ParseException
     */
    public void process(Reader reader, String baseUri) throws ParseException {
        process(reader, null, baseUri);
    }

    /**
     * Processes reader input for document's
     * @param reader document's reader
     * @param mimeType document's MIME type
     * @param baseUri document's base URI
     * @throws ParseException
     */
    public final void process(Reader reader, String mimeType, String baseUri) throws ParseException {
        startStream();
        try {
            processInternal(reader, mimeType, baseUri);
        } finally {
            endStream();
        }
    }

    /**
     * Creates source appropriate for specified sink.
     * @param sink sink to create source for
     * @return new instance of source which can stream to sink
     */
    protected static io.github.sparqlanything.html.org.semarglproject.source.AbstractSource createSourceForSink(DataSink sink) {
        if (sink instanceof CharSink) {
            return new io.github.sparqlanything.html.org.semarglproject.source.CharSource((CharSink) sink);
        } else if (sink instanceof XmlSink) {
            return new io.github.sparqlanything.html.org.semarglproject.source.XmlSource((XmlSink) sink);
        }
        return null;
    }

    static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

}
