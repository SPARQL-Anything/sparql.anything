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

import io.github.sparqlanything.html.org.semarglproject.rdf.NTriplesSerializer;
import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.sink.CharSink;
import io.github.sparqlanything.html.org.semarglproject.sink.QuadSink;

/**
 * Implementation of {@link io.github.sparqlanything.html.org.semarglproject.sink.TripleSink} which serializes triples to
 * {@link CharSink} using NTriples syntax.
 */
public class NQuadsSerializer extends NTriplesSerializer implements QuadSink {

    private NQuadsSerializer(CharSink sink) {
        super(sink);
    }

    /**
     * Creates instance of TurtleSerializer connected to specified sink.
     * @param sink sink to be connected to
     * @return instance of TurtleSerializer
     */
    public static QuadSink connect(CharSink sink) {
        return new NQuadsSerializer(sink);
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj, String graph) {
        try {
            startTriple(subj, pred);
            serializeBnodeOrUri(obj);
            if (graph != null) {
                serializeBnodeOrUri(graph);
            }
            sink.process(DOT_EOL);
        } catch (ParseException e) {
            // ignore
        }
    }

    @Override
    public void addPlainLiteral(String subj, String pred, String content, String lang, String graph) {
        try {
            startTriple(subj, pred);
            addContent(content);
            if (lang != null) {
                sink.process('@').process(lang);
            }
            sink.process(SPACE);
            if (graph != null) {
                serializeBnodeOrUri(graph);
            }
            sink.process(DOT_EOL);
        } catch (ParseException e) {
            // ignore
        }
    }

    @Override
    public void addTypedLiteral(String subj, String pred, String content, String type, String graph) {
        try {
            startTriple(subj, pred);
            addContent(content);
            sink.process("^^");
            serializeUri(type);
            if (graph != null) {
                serializeBnodeOrUri(graph);
            }
            sink.process(DOT_EOL);
        } catch (ParseException e) {
            // ignore
        }
    }

}
