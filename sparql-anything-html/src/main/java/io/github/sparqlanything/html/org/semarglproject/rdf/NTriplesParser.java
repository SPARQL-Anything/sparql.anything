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
package io.github.sparqlanything.html.org.semarglproject.rdf;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler;
import io.github.sparqlanything.html.org.semarglproject.sink.CharSink;
import io.github.sparqlanything.html.org.semarglproject.sink.Pipe;
import io.github.sparqlanything.html.org.semarglproject.sink.TripleSink;
import io.github.sparqlanything.html.org.semarglproject.source.StreamProcessor;

import java.util.BitSet;

/**
 * Implementation of streaming <a href="http://www.w3.org/2001/sw/RDFCore/ntriples/">NTriples</a> parser.
 * <br>
 *     List of supported options:
 *     <ul>
 *         <li>{@link StreamProcessor#PROCESSOR_GRAPH_HANDLER_PROPERTY}</li>
 *         <li>{@link StreamProcessor#ENABLE_ERROR_RECOVERY}</li>
 *     </ul>
 */
public final class NTriplesParser extends Pipe<TripleSink> implements CharSink {

    /**
     * Class URI for errors produced by a parser
     */
    public static final String ERROR = "http://semarglproject.org/ntriples/Error";

    private static final short PARSING_OUTSIDE = 0;
    private static final short PARSING_URI = 1;
    private static final short PARSING_BNODE = 2;
    private static final short PARSING_LITERAL = 3;
    private static final short PARSING_AFTER_LITERAL = 4;
    private static final short PARSING_LITERAL_TYPE = 5;
    private static final short PARSING_COMMENT = 6;

    private static final char SENTENCE_END = '.';

    /**
     * NTriples whitespace char checker
     */
    private static final BitSet WHITESPACE = new BitSet();

    static {
        WHITESPACE.set('\t');
        WHITESPACE.set(' ');
        WHITESPACE.set('\r');
        WHITESPACE.set('\n');
    }


    private String subj = null;
    private String pred = null;
    private String literalObj = null;

    private io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler processorGraphHandler = null;
    private boolean ignoreErrors = false;
    private boolean skipSentence = false;

    private short parsingState;

    private int tokenStartPos;
    private short charsToEscape = 0;
    private boolean waitingForSentenceEnd = false;
    private StringBuilder addBuffer = null;

    private NTriplesParser(TripleSink sink) {
        super(sink);
    }

    /**
     * Creates instance of NTriplesParser connected to specified sink.
     * @param sink sink to be connected to
     * @return instance of NTriplesParser
     */
    public static CharSink connect(TripleSink sink) {
        return new NTriplesParser(sink);
    }

    private void error(String msg) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (processorGraphHandler != null) {
            processorGraphHandler.error(ERROR, msg);
        }
        if (!ignoreErrors) {
            throw new io.github.sparqlanything.html.org.semarglproject.rdf.ParseException(msg);
        } else {
            resetTriple();
            skipSentence = true;
            parsingState = PARSING_OUTSIDE;
        }
    }

    @Override
    public NTriplesParser process(String str) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        return process(str.toCharArray(), 0, str.length());
    }

    @Override
    public NTriplesParser process(char ch) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        char[] buffer = new char[1];
        buffer[0] = ch;
        return process(buffer, 0, 1);
    }

    @Override
    public NTriplesParser process(char[] buffer, int start, int count) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (tokenStartPos != -1) {
            tokenStartPos = start;
        }
        int end = start + count;

        for (int pos = start; pos < end; pos++) {
            if (skipSentence && buffer[pos] != SENTENCE_END) {
                continue;
            } else {
                skipSentence = false;
            }

            if (parsingState == PARSING_OUTSIDE) {
                processOutsideChar(buffer, pos);
            } else if (parsingState == PARSING_COMMENT) {
                if (buffer[pos] == '\n' || buffer[pos] == '\r') {
                    parsingState = PARSING_OUTSIDE;
                }
            } else if (parsingState == PARSING_URI) {
                if (buffer[pos] == '>') {
                    onNonLiteral(unescape(extractToken(buffer, pos, 1)));
                    parsingState = PARSING_OUTSIDE;
                }
            } else if (parsingState == PARSING_BNODE) {
                if (WHITESPACE.get(buffer[pos]) || buffer[pos] == SENTENCE_END) {
                    onNonLiteral(extractToken(buffer, pos - 1, 0));
                    parsingState = PARSING_OUTSIDE;
                }
            } else if (parsingState == PARSING_LITERAL) {
                processLiteralChar(buffer, pos);
            } else if (parsingState == PARSING_AFTER_LITERAL) {
                if (buffer[pos] == '@' || buffer[pos] == '^') {
                    tokenStartPos = pos;
                    parsingState = PARSING_LITERAL_TYPE;
                } else if (WHITESPACE.get(buffer[pos]) || buffer[pos] == SENTENCE_END) {
                    onPlainLiteral(literalObj, null);
                    parsingState = PARSING_OUTSIDE;
                    processOutsideChar(buffer, pos);
                } else {
                    error("Unexpected character '" + buffer[pos] + "' after literal");
                }
            } else if (parsingState == PARSING_LITERAL_TYPE) {
                processLiteralTypeChar(buffer, pos);
            }
        }
        if (tokenStartPos != -1) {
            if (addBuffer == null) {
                addBuffer = new StringBuilder();
            }
            addBuffer.append(buffer, tokenStartPos, end - tokenStartPos);
        }
        return this;
    }

    private void processLiteralChar(char[] buffer, int pos) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (charsToEscape == 9 && buffer[pos] == 'u') {
            charsToEscape -= 5;
        } else if (charsToEscape == 9 && buffer[pos] != 'U') {
            charsToEscape = 0;
        } else if (charsToEscape > 0) {
            charsToEscape--;
        } else {
            if (buffer[pos] == '\"') {
                literalObj = unescape(extractToken(buffer, pos, 1));
                parsingState = PARSING_AFTER_LITERAL;
            } else if (buffer[pos] == '\\') {
                charsToEscape = 9;
            }
        }
    }

    private void processLiteralTypeChar(char[] buffer, int pos) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (WHITESPACE.get(buffer[pos])) {
            String type = extractToken(buffer, pos, 0);
            int trimSize = type.charAt(type.length() - 1) == SENTENCE_END ? 1 : 0;
            if (type.charAt(0) == '@') {
                onPlainLiteral(literalObj, type.substring(1, type.length() - 1 - trimSize));
            } else if (type.startsWith("^^<") && type.charAt(type.length() - 2) == '>') {
                onTypedLiteral(literalObj, type.substring(3, type.length() - 2 - trimSize));
            } else {
                error("Literal type '" + type + "' can not be parsed");
            }
            parsingState = PARSING_OUTSIDE;
            if (trimSize > 0) {
                finishSentence();
            }
        }
    }

    private void processOutsideChar(char[] buffer, int pos) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        switch (buffer[pos]) {
            case '\"':
                parsingState = PARSING_LITERAL;
                tokenStartPos = pos;
                break;
            case '<':
                parsingState = PARSING_URI;
                tokenStartPos = pos;
                break;
            case '_':
                parsingState = PARSING_BNODE;
                tokenStartPos = pos;
                break;
            case '#':
                parsingState = PARSING_COMMENT;
                break;
            case SENTENCE_END:
                finishSentence();
                break;
            default:
                if (!WHITESPACE.get(buffer[pos])) {
                    error("Unexpected character '" + buffer[pos] + "'");
                }
        }
    }

    private void finishSentence() throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (waitingForSentenceEnd) {
            waitingForSentenceEnd = false;
        } else {
            error("Unexpected end of sentence");
        }
    }

    private void onNonLiteral(String uri) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (waitingForSentenceEnd) {
            error("End of sentence expected");
        }
        if (subj == null) {
            subj = uri;
        } else if (pred == null) {
            pred = uri;
        } else {
            sink.addNonLiteral(subj, pred, uri);
            resetTriple();
        }
    }

    private void onPlainLiteral(String value, String lang) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (subj == null || pred == null) {
            if (waitingForSentenceEnd) {
                error("End of sentence expected");
            } else {
                error("Literal is not an object");
            }
        }
        sink.addPlainLiteral(subj, pred, value, lang);
        resetTriple();
    }

    private void onTypedLiteral(String value, String type) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (subj == null || pred == null) {
            if (waitingForSentenceEnd) {
                error("End of sentence expected");
            } else {
                error("Literal is not an object");
            }
        }
        sink.addTypedLiteral(subj, pred, value, type);
        resetTriple();
    }

    @Override
    public void setBaseUri(String baseUri) {
    }

    @Override
    protected boolean setPropertyInternal(String key, Object value) {
        if (StreamProcessor.PROCESSOR_GRAPH_HANDLER_PROPERTY.equals(key) && value instanceof io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler) {
            processorGraphHandler = (ProcessorGraphHandler) value;
        } else if (StreamProcessor.ENABLE_ERROR_RECOVERY.equals(key) && value instanceof Boolean) {
            ignoreErrors = (Boolean) value;
        }
        return false;
    }

    private String extractToken(char[] buffer, int tokenEndPos, int trimSize) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        String saved;
        if (addBuffer != null) {
            if (tokenEndPos - trimSize >= tokenStartPos) {
                addBuffer.append(buffer, tokenStartPos, tokenEndPos - tokenStartPos - trimSize + 1);
            }
            addBuffer.delete(0, trimSize);
            saved = addBuffer.toString();
            addBuffer = null;
        } else {
            saved = String.valueOf(buffer, tokenStartPos + trimSize, tokenEndPos - tokenStartPos + 1 - 2 * trimSize);
        }
        tokenStartPos = -1;
        return saved;
    }

    @Override
    public void startStream() throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        super.startStream();
        resetTriple();
        waitingForSentenceEnd = false;
        parsingState = PARSING_OUTSIDE;
    }

    private void resetTriple() {
        addBuffer = null;
        tokenStartPos = -1;
        subj = null;
        pred = null;
        waitingForSentenceEnd = true;
    }

    @Override
    public void endStream() throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (tokenStartPos != -1 || waitingForSentenceEnd) {
            error("Unexpected end of stream");
        }
        super.endStream();
    }

    private String unescape(String str) throws ParseException {
        int limit = str.length();
        StringBuilder result = new StringBuilder(limit);

        for (int i = 0; i < limit; i++) {
            char ch = str.charAt(i);
            if (ch != '\\') {
                result.append(ch);
                continue;
            }
            i++;
            if (i == limit) {
                break;
            }
            ch = str.charAt(i);
            switch (ch) {
                case '\\':
                case '\'':
                case '\"':
                    result.append(ch);
                    break;
                case 'b':
                    result.append('\b');
                    break;
                case 'f':
                    result.append('\f');
                    break;
                case 'n':
                    result.append('\n');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                case 't':
                    result.append('\t');
                    break;
                case 'u':
                case 'U':
                    int sequenceLength = ch == 'u' ? 4 : 8;
                    if (i + sequenceLength >= limit) {
                        error("Error parsing escape sequence '\\" + ch + "'");
                    }
                    String code = str.substring(i + 1, i + 1 + sequenceLength);
                    i += sequenceLength;

                    try {
                        int value = Integer.parseInt(code, 16);
                        result.append((char) value);
                    } catch (NumberFormatException nfe) {
                        error("Error parsing escape sequence '\\" + ch + "'");
                    }
                    break;
                default:
                    result.append(ch);
                    break;
            }
        }
        return result.toString();
    }

}
