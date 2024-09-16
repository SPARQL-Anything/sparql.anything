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

package io.github.sparqlanything.html.org.apache.any23.extractor.rdfa;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;

/**
 * @author Hans Brende (hansbrende@apache.org)
 */
final class SemarglSink implements io.github.sparqlanything.html.org.semarglproject.sink.TripleSink, io.github.sparqlanything.html.org.semarglproject.rdf.ProcessorGraphHandler {

    private static final String BNODE_PREFIX = io.github.sparqlanything.html.org.semarglproject.vocab.RDF.BNODE_PREFIX;

    private final ExtractionResult handler;
    private final ValueFactory valueFactory;

    SemarglSink(ExtractionResult handler, ValueFactory valueFactory) {
        this.handler = handler;
        this.valueFactory = valueFactory;
    }

    private Resource createResource(String arg) {
        if (arg.startsWith(BNODE_PREFIX)) {
            return valueFactory.createBNode(arg.substring(BNODE_PREFIX.length()));
        }
        return valueFactory.createIRI(arg);
    }

    private void writeTriple(String s, String p, Value o) {
        handler.writeTriple(createResource(s), valueFactory.createIRI(p), o);
    }

    @Override
    public final void addNonLiteral(String s, String p, String o) {
        writeTriple(s, p, createResource(o));
    }

    @Override
    public final void addPlainLiteral(String s, String p, String o, String lang) {
        writeTriple(s, p, lang == null ? valueFactory.createLiteral(o) : valueFactory.createLiteral(o, lang));
    }

    @Override
    public final void addTypedLiteral(String s, String p, String o, String type) {
        writeTriple(s, p, valueFactory.createLiteral(o, valueFactory.createIRI(type)));
    }

    @Override
    public void startStream() {

    }

    @Override
    public void endStream() {
    }

    @Override
    public boolean setProperty(String key, Object value) {
        return false;
    }

    @Override
    public void setBaseUri(String baseUri) {
    }

    @Override
    public void info(String infoClass, String message) {

    }

    @Override
    public void warning(String warningClass, String message) {
        handler.notifyIssue(IssueReport.IssueLevel.WARNING, message, -1, -1);
    }

    @Override
    public void error(String errorClass, String message) {
        handler.notifyIssue(IssueReport.IssueLevel.ERROR, message, -1, -1);
    }
}
