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

package io.github.sparqlanything.html.org.apache.any23.mime;

import io.github.sparqlanything.html.org.apache.any23.mime.purifier.Purifier;
import io.github.sparqlanything.html.org.apache.any23.mime.purifier.WhiteSpacesPurifier;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.zip.DefaultZipContainerDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Implementation of {@link MIMETypeDetector} based on <a href="http://tika.apache.org/">Apache Tika</a>.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class TikaMIMETypeDetector implements MIMETypeDetector {

    private Purifier purifier;

    public static final String CSV_MIMETYPE = "text/csv";

    public static final String RESOURCE_NAME = "/org/apache/any23/mime/tika-config.xml";

    /**
     * N3 patterns.
     */
    private static final Pattern[] N3_PATTERNS = { Pattern.compile("^\\S+\\s*<\\S+>\\s*<\\S+>\\s*\\."), // * IRI IRI .
            Pattern.compile("^\\S+\\s*<\\S+>\\s*_:\\S+\\s*\\."), // * IRI BNODE .
            Pattern.compile("^\\S+\\s*<\\S+>\\s*\".*\"(@\\S+)?\\s*\\."), // * IRI LLITERAL .
            Pattern.compile("^\\S+\\s*<\\S+>\\s*\".*\"(\\^\\^\\S+)?\\s*\\.") // * IRI TLITERAL .
    };

    /**
     * N-Quads patterns.
     */
    private static final Pattern[] NQUADS_PATTERNS = { Pattern.compile("^\\S+\\s*<\\S+>\\s*<\\S+>\\s*\\<\\S+>\\s*\\."), // *
                                                                                                                        // IRI
                                                                                                                        // IRI
                                                                                                                        // IRI
                                                                                                                        // .
            Pattern.compile("^\\S+\\s*<\\S+>\\s*_:\\S+\\s*\\<\\S+>\\s*\\."), // * IRI BNODE IRI .
            Pattern.compile("^\\S+\\s*<\\S+>\\s*\".*\"(@\\S+)?\\s*\\<\\S+>\\s*\\."), // * IRI LLITERAL IRI .
            Pattern.compile("^\\S+\\s*<\\S+>\\s*\".*\"(\\^\\^\\S+)?\\s*\\<\\S+>\\s*\\.") // * IRI TLITERAL IRI .
    };

    private static volatile TikaConfig config;

    private static volatile Tika tika;

    private static volatile MimeTypes types;

    /**
     * Checks if the stream contains the <i>N3</i> triple patterns.
     *
     * @param is
     *            input stream to be verified.
     *
     * @return <code>true</code> if <i>N3</i> patterns are detected, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error checking the {@link InputStream}
     */
    public static boolean checkN3Format(InputStream is) throws IOException {
        return findPattern(N3_PATTERNS, '.', is);
    }

    /**
     * Checks if the stream contains the <i>NQuads</i> patterns.
     *
     * @param is
     *            input stream to be verified.
     *
     * @return <code>true</code> if <i>N3</i> patterns are detected, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error checking the {@link InputStream}
     */
    public static boolean checkNQuadsFormat(InputStream is) throws IOException {
        return findPattern(NQUADS_PATTERNS, '.', is);
    }

    /**
     * Checks if the stream contains <i>Turtle</i> triple patterns.
     *
     * @param is
     *            input stream to be verified.
     *
     * @return <code>true</code> if <i>Turtle</i> patterns are detected, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error checking the {@link InputStream}
     */
    public static boolean checkTurtleFormat(InputStream is) throws IOException {
        String sample = extractDataSample(is, '.');
        RDFParser turtleParser = Rio.createParser(RDFFormat.TURTLE);
        turtleParser.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, true);
        ByteArrayInputStream bais = new ByteArrayInputStream(sample.getBytes(StandardCharsets.UTF_8));
        try {
            turtleParser.parse(bais, "");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the stream contains a valid <i>CSV</i> pattern.
     *
     * @param is
     *            input stream to be verified.
     *
     * @return <code>true</code> if <i>CSV</i> patterns are detected, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error checking the {@link InputStream}
     */
//    public static boolean checkCSVFormat(InputStream is) throws IOException {
//        return CSVReaderBuilder.isCSV(is);
//    }

    /**
     * Tries to apply one of the given patterns on a sample of the input stream.
     *
     * @param patterns
     *            the patterns to apply.
     * @param delimiterChar
     *            the delimiter of the sample.
     * @param is
     *            the input stream to sample.
     *
     * @return <code>true</code> if a pattern has been applied, <code>false</code> otherwise.
     *
     * @throws IOException
     *             if there is an error finding the pattern within the {@link InputStream}
     */
    private static boolean findPattern(Pattern[] patterns, char delimiterChar, InputStream is) throws IOException {
        String sample = extractDataSample(is, delimiterChar);
        for (Pattern pattern : patterns) {
            if (pattern.matcher(sample).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts a sample data from the input stream, from the current mark to the first <i>breakChar</i> char.
     *
     * @param is
     *            the input stream to sample.
     * @param breakChar
     *            the char to break to sample.
     *
     * @return the sample string.
     *
     * @throws IOException
     *             if an error occurs during sampling.
     */
    private static String extractDataSample(InputStream is, char breakChar) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        final int MAX_SIZE = 1024 * 2;
        int c;
        boolean insideBlock = false;
        int read = 0;
        br.mark(MAX_SIZE);
        try {
            while ((c = br.read()) != -1) {
                read++;
                if (read > MAX_SIZE) {
                    break;
                }
                if ('<' == c) {
                    insideBlock = true;
                } else if ('>' == c) {
                    insideBlock = false;
                } else if ('"' == c) {
                    insideBlock = !insideBlock;
                }
                sb.append((char) c);
                if (!insideBlock && breakChar == c) {
                    break;
                }
            }
        } finally {
            is.reset();
            br.reset();
        }
        return sb.toString();
    }

    public TikaMIMETypeDetector(Purifier purifier) {
        this.purifier = purifier;
        if (config == null || types == null || tika == null) {
            synchronized (TikaMIMETypeDetector.class) {
                if (config == null) {
                    InputStream is = getResourceAsStream();
                    try {
						config = new TikaConfig(is);
                    } catch (Exception e) {
                        throw new RuntimeException("Error while loading Tika configuration.", e);
                    }
                }
                if (types == null) {
                    types = config.getMimeRepository();
                }
                if (tika == null) {
                    tika = new Tika(config);
                }
            }
        }
    }

    public TikaMIMETypeDetector() {
        this(new WhiteSpacesPurifier());
    }

    /**
     * Estimates the <code>MIME</code> type of the content of input file. The <i>input</i> stream must be resettable.
     *
     * @param fileName
     *            name of the data source.
     * @param input
     *            <code>null</code> or a <i>resettable</i> input stream containing data.
     * @param mimeTypeFromMetadata
     *            mimetype declared in metadata.
     *
     * @return the supposed mime type or <code>null</code> if nothing appropriate found.
     *
     * @throws IllegalArgumentException
     *             if <i>input</i> is not <code>null</code> and is not resettable.
     */
    public MIMEType guessMIMEType(String fileName, InputStream input, MIMEType mimeTypeFromMetadata) {
        if (input != null) {
            try {
                this.purifier.purify(input);
            } catch (IOException e) {
                throw new RuntimeException("Error while purifying the provided input", e);
            }
        }

        final Metadata meta = new Metadata();
        if (mimeTypeFromMetadata != null)
            meta.set(Metadata.CONTENT_TYPE, mimeTypeFromMetadata.getFullType());
        if (fileName != null)
            meta.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);

        String type;
        try {
            final String mt = guessMimeTypeByInputAndMeta(input, meta);
            if (input == null || !MimeTypes.OCTET_STREAM.equals(mt)) {
                type = mt;
            } else {
                if (checkN3Format(input)) {
                    type = RDFFormat.N3.getDefaultMIMEType();
                } else if (checkNQuadsFormat(input)) {
                    type = RDFFormat.NQUADS.getDefaultMIMEType();
                } else if (checkTurtleFormat(input)) {
                    type = RDFFormat.TURTLE.getDefaultMIMEType();
                } else {
                    type = MimeTypes.OCTET_STREAM;
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error while retrieving mime type.", ioe);
        }
        return MIMEType.parse(type);
    }

    /**
     * Loads the <code>Tika</code> configuration file.
     *
     * @return the input stream containing the configuration.
     */
    private InputStream getResourceAsStream() {
        InputStream result;
        result = TikaMIMETypeDetector.class.getResourceAsStream(RESOURCE_NAME);
        if (result == null) {
            try {
                result = TikaMIMETypeDetector.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
            } catch (SecurityException e) {
                // fall through
            }
            if (result == null) {
                result = ClassLoader.getSystemResourceAsStream(RESOURCE_NAME);
            }
        }
        return result;
    }

    /**
     * Automatically detects the MIME type of a document based on magic markers in the stream prefix and any given
     * metadata hints.
     * <p/>
     * The given stream is expected to support marks, so that this method can reset the stream to the position it was in
     * before this method was called.
     *
     * @param stream
     *            document stream
     * @param metadata
     *            metadata hints
     *
     * @return MIME type of the document
     *
     * @throws IOException
     *             if the document stream could not be read
     */
    private String guessMimeTypeByInputAndMeta(InputStream stream, final Metadata metadata) throws IOException {
        if (stream != null) {
            final String type = tika.detect(stream);
            if (type != null && !isGenericMIMEType(type)) {
                return type;
            }
        }

        // Determines the MIMEType based on Content-Type hint if available.
        final String contentType = metadata.get(Metadata.CONTENT_TYPE);
        String candidateMIMEType = null;
        if (contentType != null) {
            try {
                MimeType type = types.forName(contentType);
                if (type != null) {
                    candidateMIMEType = type.getName();
                    if (!isPlainMIMEType(candidateMIMEType)) {
                        return candidateMIMEType;
                    }
                }
            } catch (MimeTypeException mte) {
                // Malformed ocntent-type value, ignore.
            }
        }

        // Determines the MIMEType based on resource name hint if available.
        final String resourceName = metadata.get(TikaCoreProperties.RESOURCE_NAME_KEY);
        if (resourceName != null) {
            String type = tika.detect(resourceName);
            if (type != null && !type.equals(MimeTypes.OCTET_STREAM)) {
                return type;
            }
        }

        // Finally, use the default type if no matches found
        if (candidateMIMEType != null) {
            return candidateMIMEType;
        } else {
            return MimeTypes.OCTET_STREAM;
        }
    }

    private boolean isPlainMIMEType(String type) {
        return type.equals(MimeTypes.OCTET_STREAM) || type.equals(MimeTypes.PLAIN_TEXT);
    }

    private boolean isGenericMIMEType(String type) {
        return isPlainMIMEType(type) || type.equals(MimeTypes.XML);
    }

}
