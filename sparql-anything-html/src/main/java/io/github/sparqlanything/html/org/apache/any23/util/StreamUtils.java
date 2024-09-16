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

package io.github.sparqlanything.html.org.apache.any23.util;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Contains general utility functions for handling streams.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StreamUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    private StreamUtils() {
    }

    /**
     * Returns all the lines read from an input stream.
     *
     * @param is
     *            input stream.
     *
     * @return list of not <code>null</code> lines.
     *
     * @throws IOException
     *             if an error occurs while consuming the <code>is</code> stream.
     */
    public static String[] asLines(InputStream is) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        final List<String> lines = new ArrayList<String>();
        try {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray(new String[lines.size()]);
        } finally {
            closeGracefully(br);
        }
    }

    /**
     * Returns the string content of a stream.
     *
     * @param is
     *            input stream.
     * @param preserveNL
     *            preserves new line chars.
     *
     * @return the string content.
     *
     * @throws IOException
     *             if an error occurs while consuming the <code>is</code> stream.
     */
    public static String asString(InputStream is, boolean preserveNL) throws IOException {
        if (is == null) {
            throw new NullPointerException("input stream is null.");
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        try {
            final StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                if (preserveNL)
                    content.append('\n');
            }
            return content.toString();
        } finally {
            closeGracefully(br);
        }
    }

    /**
     * Returns the string content of a stream, new line chars will be removed.
     *
     * @param is
     *            input stream.
     *
     * @return the string content.
     *
     * @throws IOException
     *             if an error occurs while consuming the <code>is</code> stream.
     */
    public static String asString(InputStream is) throws IOException {
        return asString(is, false);
    }

    /**
     * Closes the closable interface and reports error if any.
     *
     * @param closable
     *            the closable object to be closed.
     */
    public static void closeGracefully(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (Exception e) {
                logger.error("Error while closing object " + closable, e);
            }
        }
    }

    /**
     * Converts a {@link Document} to an {@link InputStream}
     *
     * @param doc
     *            the {@link Document} to convert
     *
     * @return an {@link InputStream} representing the contents of the input {@link Document}
     *
     * @throws TransformerFactoryConfigurationError
     *             thrown when there is a problem with configuration with the Transformer Factories
     * @throws TransformerConfigurationException
     *             thrown when a serious configuration error exists
     */
    public static InputStream documentToInputStream(Document doc)
            throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(doc);
        Result outputTarget = new StreamResult(outputStream);
        try {
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        } catch (TransformerException e) {
            logger.error("Error during transformation: {}", e);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static Document inputStreamToDocument(InputStream is) throws MalformedByteSequenceException {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document doc = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Error converting InputStream to Document: {}", e);
        }

        try {
            BOMInputStream bomIn = new BOMInputStream(is, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE,
                    ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);
            if (bomIn.hasBOM()) {
                @SuppressWarnings("unused")
                int firstNonBOMByte = bomIn.read(); // Skips BOM
            }
            doc = builder.parse(bomIn);
        } catch (SAXException | IOException e) {
            logger.error("Error converting InputStream to Document: {}", e);
        }
        return doc;
    }
}
