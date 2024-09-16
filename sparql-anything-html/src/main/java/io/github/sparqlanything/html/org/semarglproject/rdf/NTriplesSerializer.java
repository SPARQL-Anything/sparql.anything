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
import io.github.sparqlanything.html.org.semarglproject.sink.CharSink;
import io.github.sparqlanything.html.org.semarglproject.sink.Pipe;
import io.github.sparqlanything.html.org.semarglproject.sink.TripleSink;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDF;

import java.util.BitSet;

/**
 * Implementation of {@link TripleSink} which serializes triples to
 * {@link CharSink} using NTriples syntax.
 */
public class NTriplesSerializer extends Pipe<CharSink> implements TripleSink {

    protected static final String DOT_EOL = ".\n";
    protected static final char SPACE = ' ';

    private static final char QUOTE = '"';
    private static final char URI_START = '<';
    private static final char URI_END = '>';

    private static final BitSet ESCAPABLE_CONTENT_CHARS = new BitSet();
    private static final BitSet ESCAPABLE_URI_CHARS = new BitSet();

    static {
        ESCAPABLE_CONTENT_CHARS.set('\\');
        ESCAPABLE_CONTENT_CHARS.set('\"');
        ESCAPABLE_CONTENT_CHARS.set('\b');
        ESCAPABLE_CONTENT_CHARS.set('\f');
        ESCAPABLE_CONTENT_CHARS.set('\n');
        ESCAPABLE_CONTENT_CHARS.set('\r');
        ESCAPABLE_CONTENT_CHARS.set('\t');

        for (char ch = 0; ch <= 0x20; ch++) {
            ESCAPABLE_URI_CHARS.set(ch);
        }
        ESCAPABLE_URI_CHARS.set('\\');
        ESCAPABLE_URI_CHARS.set('<');
        ESCAPABLE_URI_CHARS.set('>');
        ESCAPABLE_URI_CHARS.set('{');
        ESCAPABLE_URI_CHARS.set('}');
        ESCAPABLE_URI_CHARS.set('"');
        ESCAPABLE_URI_CHARS.set('`');
        ESCAPABLE_URI_CHARS.set('|');
        ESCAPABLE_URI_CHARS.set('^');
    }

    protected NTriplesSerializer(CharSink sink) {
        super(sink);
    }

    /**
     * Creates instance of TurtleSerializer connected to specified sink.
     * @param sink sink to be connected to
     * @return instance of TurtleSerializer
     */
    public static TripleSink connect(CharSink sink) {
        return new NTriplesSerializer(sink);
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj) {
        try {
            startTriple(subj, pred);
            serializeBnodeOrUri(obj);
            sink.process(DOT_EOL);
        } catch (ParseException e) {
            // ignore
        }
    }

    @Override
    public void addPlainLiteral(String subj, String pred, String content, String lang) {
        try {
            startTriple(subj, pred);
            addContent(content);
            if (lang != null) {
                sink.process('@').process(lang);
            }
            sink.process(SPACE).process(DOT_EOL);
        } catch (ParseException e) {
            // ignore
        }
    }

    @Override
    public void addTypedLiteral(String subj, String pred, String content, String type) {
        try {
            startTriple(subj, pred);
            addContent(content);
            sink.process("^^");
            serializeUri(type);
            sink.process(DOT_EOL);
        } catch (ParseException e) {
            // ignore
        }
    }

    @Override
    protected boolean setPropertyInternal(String key, Object value) {
        return false;
    }

    @Override
    public void setBaseUri(String baseUri) {
        // ignore
    }

    protected void startTriple(String subj, String pred) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        serializeBnodeOrUri(subj);
        serializeBnodeOrUri(pred);
    }

    protected void serializeBnodeOrUri(String value) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (value.startsWith(RDF.BNODE_PREFIX)) {
            sink.process(value).process(SPACE);
        } else {
            serializeUri(value);
        }
    }

    protected void serializeUri(String uri) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        String escapedUri = escapeUri(uri);
        sink.process(URI_START).process(escapedUri).process(URI_END).process(SPACE);
    }

    protected void addContent(String content) throws ParseException {
        String escapedContent = escapeContent(content);
        sink.process(QUOTE).process(escapedContent).process(QUOTE);
    }

    private static String escapeContent(String str) {
        int limit = str.length();
        int pos = 0;
        for (; pos < limit; pos++) {
            char ch = str.charAt(pos);
            if (ch > 0x80 || ESCAPABLE_CONTENT_CHARS.get(ch)) {
                break;
            }
        }
        if (pos == limit) {
            return str;
        }
        StringBuilder result = new StringBuilder(limit);
        result.append(str.substring(0, pos));
        for (; pos < limit; pos++) {
            char ch = str.charAt(pos);
            if (ch < 0x80) {
                switch (ch) {
                    case '\\':
                    case '\"':
                        result.append('\\').append(ch);
                        break;
                    case '\b':
                        result.append("\\b");
                        break;
                    case '\f':
                        result.append("\\f");
                        break;
                    case '\n':
                        result.append("\\n");
                        break;
                    case '\r':
                        result.append("\\r");
                        break;
                    case '\t':
                        result.append("\\t");
                        break;
                    default:
                        result.append(ch);
                }
            } else if (ch <= 0xFFFF) {
                result.append("\\u").append(String.format("%04X", (int) ch));
            } else {
                result.append("\\U").append(String.format("%08X", (int) ch));
            }
        }
        return result.toString();
    }

    private static String escapeUri(String str) {
        int limit = str.length();
        int pos = 0;
        for (; pos < limit; pos++) {
            char ch = str.charAt(pos);
            if (ch > 0x80 || ESCAPABLE_URI_CHARS.get(ch)) {
                break;
            }
        }
        if (pos == limit) {
            return str;
        }
        StringBuilder result = new StringBuilder(limit);
        result.append(str.substring(0, pos));
        for (; pos < limit; pos++) {
            char ch = str.charAt(pos);
            if (ESCAPABLE_URI_CHARS.get(ch)) {
                result.append("\\u").append(String.format("%04X", (int) ch));
            } else if (ch < 0x80) {
                result.append(ch);
            } else if (ch <= 0xFFFF) {
                result.append("\\u").append(String.format("%04X", (int) ch));
            } else {
                result.append("\\U").append(String.format("%08X", (int) ch));
            }
        }
        return result.toString();
    }


}
