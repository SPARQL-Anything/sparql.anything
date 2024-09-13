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

package io.github.sparqlanything.html.org.apache.any23.writer;

import io.github.sparqlanything.html.org.apache.any23.writer.*;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.TreeSet;

/**
 * This writer simply produces a list of unique <i>IRI</i> present in the subject or in the object of every single
 * extracted <i>RDF Statement</i>.
 *
 * @author Davide Palmisano (palmisano@fbk.eu)
 * @author Hans Brende (hansbrende@apache.org)
 */
public class URIListWriter extends TripleWriterHandler implements FormatWriter {

    private static final Charset charset = StandardCharsets.UTF_8;

    static final TripleFormat FORMAT = TripleFormat.of("URIList", Collections.singleton(URIListWriterFactory.MIME_TYPE),
            charset, Collections.singleton("txt"), null, TripleFormat.NONSTANDARD);

    private final TreeSet<String> resources = new TreeSet<>();

    private PrintWriter writer;

    public URIListWriter(OutputStream outputStream) {
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, charset)));
    }

    @Override
    public void writeTriple(Resource s, IRI p, Value o, Resource g) throws TripleHandlerException {
        String string;
        if (s instanceof IRI && resources.add(string = s.stringValue())) {
            writer.println(string);
        }
        if (o instanceof IRI && resources.add(string = o.stringValue())) {
            writer.println(string);
        }
    }

    @Override
    public void writeNamespace(String prefix, String uri) throws TripleHandlerException {
    }

    @Override
    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        writer.flush();
    }

    @Override
    public void close() throws TripleHandlerException {
        writer.flush();
        writer = null;
        resources.clear();
    }

    @Override
    public boolean isAnnotated() {
        return false;
    }

    @Override
    public void setAnnotated(boolean f) {
        // Empty.
    }

}
