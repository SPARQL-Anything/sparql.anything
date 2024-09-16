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
package io.github.sparqlanything.html.org.semarglproject.rdf4j.core.sink;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.sink.QuadSink;
import io.github.sparqlanything.html.org.semarglproject.sink.TripleSink;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDF;

/**
 * Implementation if {@link TripleSink} which feeds triples from Semargl's pipeline to Sesame's {@link RDFHandler}.
 * <br>
 *     List of supported options:
 *     <ul>
 *         <li>{@link #RDF_HANDLER_PROPERTY}</li>
 *         <li>{@link #VALUE_FACTORY_PROPERTY}</li>
 *     </ul>
 *
 * @author Peter Ansell p_ansell@yahoo.com
 * @author Lev Khomich levkhomich@gmail.com
 *
 */
public class RDF4JSink implements QuadSink {

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * Allows to specify Sesame's RDF handler.
     * Subclass of {@link RDFHandler} must be passed as a value.
     */
    public static final String RDF_HANDLER_PROPERTY = "http://semarglproject.org/sesame/properties/rdf-handler";

    /**
     * Used as a key with {@link #setProperty(String, Object)} method.
     * Allows to specify Sesame's value factory used to generate statemets.
     * Subclass of {@link ValueFactory} must be passed as a value.
     */
    public static final String VALUE_FACTORY_PROPERTY = "http://semarglproject.org/sesame/properties/value-factory";

    protected RDFHandler handler;
    protected ValueFactory valueFactory;

    protected RDF4JSink(RDFHandler handler) {
        this.valueFactory = SimpleValueFactory.getInstance();
        this.handler = handler;
    }

    /**
     * Instantiates sink for specified Sesame {@link RDFHandler}
     * @param handler RDFHandler to sink triples to
     * @return new instance of Sesame sink
     */
    public static QuadSink connect(RDFHandler handler) {
        return new RDF4JSink(handler);
    }

    private Resource convertNonLiteral(String arg) {
        if (arg.startsWith(RDF.BNODE_PREFIX)) {
            return valueFactory.createBNode(arg.substring(2));
        }
        return valueFactory.createIRI(arg);
    }

    @Override
    public final void addNonLiteral(String subj, String pred, String obj) {
        addTriple(convertNonLiteral(subj), valueFactory.createIRI(pred), convertNonLiteral(obj));
    }

    @Override
    public final void addPlainLiteral(String subj, String pred, String content, String lang) {
        if (lang == null) {
            addTriple(convertNonLiteral(subj), valueFactory.createIRI(pred), valueFactory.createLiteral(content));
        } else {
            addTriple(convertNonLiteral(subj), valueFactory.createIRI(pred),
                    valueFactory.createLiteral(content, lang));
        }
    }

    @Override
    public final void addTypedLiteral(String subj, String pred, String content, String type) {
        Literal literal = valueFactory.createLiteral(content, valueFactory.createIRI(type));
        addTriple(convertNonLiteral(subj), valueFactory.createIRI(pred), literal);
    }

    protected void addTriple(Resource subject, IRI predicate, Value object) {
        try {
            handler.handleStatement(valueFactory.createStatement(subject, predicate, object));
        } catch(RDFHandlerException e) {
            // TODO: provide standard way to handle exceptions inside of triple sinks
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void addNonLiteral(String subj, String pred, String obj, String graph) {
        if (graph == null) {
            addNonLiteral(subj, pred, obj);
        } else {
            addQuad(convertNonLiteral(subj), valueFactory.createIRI(pred), convertNonLiteral(obj),
                    convertNonLiteral(graph));
        }
    }

    @Override
    public final void addPlainLiteral(String subj, String pred, String content, String lang, String graph) {
        if (graph == null) {
            addPlainLiteral(subj, pred, content, lang);
        } else {
            if (lang == null) {
                addQuad(convertNonLiteral(subj), valueFactory.createIRI(pred), valueFactory.createLiteral(content),
                        convertNonLiteral(graph));
            } else {
                addQuad(convertNonLiteral(subj), valueFactory.createIRI(pred),
                        valueFactory.createLiteral(content, lang), convertNonLiteral(graph));
            }
        }
    }

    @Override
    public final void addTypedLiteral(String subj, String pred, String content, String type, String graph) {
        if (graph == null) {
            addTypedLiteral(subj, pred, content, type);
        } else {
            Literal literal = valueFactory.createLiteral(content, valueFactory.createIRI(type));
            addQuad(convertNonLiteral(subj), valueFactory.createIRI(pred), literal, convertNonLiteral(graph));
        }
    }

    protected void addQuad(Resource subject, IRI predicate, Value object, Resource graph) {
        try {
            handler.handleStatement(valueFactory.createStatement(subject, predicate, object, graph));
        } catch(RDFHandlerException e) {
            // TODO: provide standard way to handle exceptions inside of triple sinks
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startStream() throws ParseException {
        try {
            handler.startRDF();
        } catch(RDFHandlerException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public void endStream() throws ParseException {
        try {
            handler.endRDF();
        } catch(RDFHandlerException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public boolean setProperty(String key, Object value) {
        if (RDF_HANDLER_PROPERTY.equals(key) && value instanceof RDFHandler) {
            handler = (RDFHandler) value;
        } else if (VALUE_FACTORY_PROPERTY.equals(key) && value instanceof ValueFactory) {
            valueFactory = (ValueFactory) value;
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void setBaseUri(String baseUri) {
    }

}
