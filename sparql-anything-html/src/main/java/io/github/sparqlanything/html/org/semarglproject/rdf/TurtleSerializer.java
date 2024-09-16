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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Implementation of {@link TripleSink} which serializes triples to {@link CharSink} using
 * <a href="http://www.w3.org/TR/2012/WD-turtle-20120710/">Turtle</a> syntax. *
 */
public final class TurtleSerializer extends Pipe<CharSink> implements TripleSink {

    private static final String DOT_EOL = " .\n";
    private static final String COMMA_EOL = " ,\n";
    private static final String SEMICOLON_EOL = " ;\n";
    private static final String EOL = "\n";

    private static final String MULTILINE_QUOTE = "\"\"\"";
    private static final char SINGLE_LINE_QUOTE = '"';
    private static final char BNODE_START = '[';
    private static final char BNODE_END = ']';
    private static final char URI_START = '<';
    private static final char URI_END = '>';

    private static final char SPACE = ' ';
    private static final char RDF_TYPE_ABBR = 'a';
    private static final String INDENT = "    ";

    private String prevSubj;
    private String prevPred;
    private final Queue<String> bnodeStack = new LinkedList<String>();
    private final Set<String> namedBnodes = new HashSet<String>();
    private String baseUri;

    private TurtleSerializer(CharSink sink) {
        super(sink);
    }

    /**
     * Creates instance of TurtleSerializer connected to specified sink.
     * @param sink sink to be connected to
     * @return instance of TurtleSerializer
     */
    public static TripleSink connect(CharSink sink) {
        return new TurtleSerializer(sink);
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj) {
        try {
            startTriple(subj, pred);
            if (obj.startsWith(RDF.BNODE_PREFIX)) {
                if (!namedBnodes.contains(obj) && obj.endsWith(RDF.SHORTENABLE_BNODE_SUFFIX)) {
                    openBnode(obj);
                } else {
                    sink.process(obj);
                }
            } else {
                serializeUri(obj);
            }
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
        } catch (ParseException e) {
            // ignore
        }
    }

    @Override
    public void startStream() throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        super.startStream();
        prevSubj = null;
        prevPred = null;
        if (baseUri != null) {
            sink.process("@base ").process(URI_START).process(baseUri).process(URI_END).process(DOT_EOL);
        }
        sink.process("@prefix rdf: ").process(URI_START).process(RDF.NS).process(URI_END).process(DOT_EOL);
        bnodeStack.clear();
        namedBnodes.clear();
    }

    @Override
    public void endStream() throws ParseException {
        while (!bnodeStack.isEmpty()) {
            closeBnode();
        }
        if (prevPred != null) {
            sink.process(DOT_EOL);
        } else {
            sink.process(EOL);
        }
        baseUri = null;
        super.endStream();
    }

    @Override
    protected boolean setPropertyInternal(String key, Object value) {
        return false;
    }

    @Override
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri.substring(0, baseUri.length() - 1);
    }

    private void startTriple(String subj, String pred) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (subj.equals(prevSubj)) {
            if (pred.equals(prevPred)) {
                sink.process(COMMA_EOL);
                indent(2);
            } else if (prevPred != null) {
                sink.process(SEMICOLON_EOL);
                indent(1);
                serializePredicate(pred);
            } else {
                indent(0);
                serializePredicate(pred);
            }
        } else {
            if (!bnodeStack.isEmpty()) {
                closeBnode();
                startTriple(subj, pred);
                return;
            } else if (prevSubj != null) {
                sink.process(DOT_EOL);
            }
            if (subj.startsWith(RDF.BNODE_PREFIX)) {
                if (subj.endsWith(RDF.SHORTENABLE_BNODE_SUFFIX)) {
                    openBnode(subj);
                } else {
                    sink.process(subj).process(SPACE);
                    namedBnodes.add(subj);
                }
            } else {
                serializeUri(subj);
            }
            serializePredicate(pred);
        }
        prevSubj = subj;
        prevPred = pred;
    }

    private void serializePredicate(String pred) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        if (RDF.TYPE.equals(pred)) {
            sink.process(RDF_TYPE_ABBR).process(SPACE);
        } else {
            serializeUri(pred);
        }
    }

    private void serializeUri(String uri) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        String escapedUri = uri.replace("\\", "\\\\").replace(">", "\\u003E");
        if (escapedUri.startsWith(RDF.NS)) {
            sink.process("rdf:").process(escapedUri.substring(RDF.NS.length()));
        } else if (baseUri != null && escapedUri.startsWith(baseUri)) {
            sink.process(URI_START).process(escapedUri.substring(baseUri.length())).process(URI_END);
        } else {
            sink.process(URI_START).process(escapedUri).process(URI_END);
        }
        sink.process(SPACE);
    }

    private void indent(int additionalIndent) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        for (int i = 0; i < bnodeStack.size() + additionalIndent; i++) {
            sink.process(INDENT);
        }
    }

    private void addContent(String content) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        String escapedContent = content.replace("\\", "\\\\").replace("\"", "\\\"");
        if (escapedContent.contains(EOL)) {
            sink.process(MULTILINE_QUOTE).process(escapedContent).process(MULTILINE_QUOTE);
        } else {
            sink.process(SINGLE_LINE_QUOTE).process(escapedContent).process(SINGLE_LINE_QUOTE);
        }
    }

    private void openBnode(String obj) throws io.github.sparqlanything.html.org.semarglproject.rdf.ParseException {
        sink.process(BNODE_START);
        bnodeStack.offer(obj);
        prevSubj = obj;
        prevPred = null;
    }

    private void closeBnode() throws ParseException {
        sink.process(BNODE_END);
        bnodeStack.poll();
        prevSubj = bnodeStack.peek();
        prevPred = null;
        if (prevSubj == null) {
            sink.process(DOT_EOL);
        }
    }

}
