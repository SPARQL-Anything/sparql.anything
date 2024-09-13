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

package io.github.sparqlanything.html.org.apache.any23.vocab;

import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import io.github.sparqlanything.html.org.apache.any23.util.DiscoveryUtils;
import io.github.sparqlanything.html.org.apache.any23.util.StringUtils;
import io.github.sparqlanything.html.org.apache.any23.vocab.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * This class provides a set of methods for generating <a href="http://www.w3.org/TR/rdf-schema/">RDF Schema</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFSchemaUtils {

    private static final String RDF_XML_SEPARATOR = StringUtils.multiply('=', 100);

    private RDFSchemaUtils() {
    }

    /**
     * Serializes a vocabulary composed of the given <code>namespace</code>, <code>resources</code> and
     * <code>properties</code>.
     *
     * @param namespace
     *            vocabulary namespace.
     * @param classes
     *            list of classes.
     * @param properties
     *            list of properties.
     * @param comments
     *            map of resource comments.
     * @param writer
     *            writer to print out the RDF Schema triples.
     *
     * @throws RDFHandlerException
     *             if there is an error handling the RDF
     */
    public static void serializeVocabulary(IRI namespace, IRI[] classes, IRI[] properties, Map<IRI, String> comments,
            RDFWriter writer) {
        writer.startRDF();
        for (IRI clazz : classes) {
            writer.handleStatement(RDFUtils.quad(clazz, RDF.TYPE, RDFS.CLASS, namespace));
            writer.handleStatement(RDFUtils.quad(clazz, RDFS.MEMBER, namespace, namespace));
            final String comment = comments.get(clazz);
            if (comment != null)
                writer.handleStatement(RDFUtils.quad(clazz, RDFS.COMMENT, RDFUtils.literal(comment), namespace));
        }
        for (IRI property : properties) {
            writer.handleStatement(RDFUtils.quad(property, RDF.TYPE, RDF.PROPERTY, namespace));
            writer.handleStatement(RDFUtils.quad(property, RDFS.MEMBER, namespace, namespace));
            final String comment = comments.get(property);
            if (comment != null)
                writer.handleStatement(RDFUtils.quad(property, RDFS.COMMENT, RDFUtils.literal(comment), namespace));
        }
        writer.endRDF();
    }

    /**
     * Serializes the given <code>vocabulary</code> to triples over the given <code>writer</code>.
     *
     * @param vocabulary
     *            vocabulary to be serialized.
     * @param writer
     *            output writer.
     *
     * @throws RDFHandlerException
     *             if there is an error handling the RDF
     */
    public static void serializeVocabulary(Vocabulary vocabulary, RDFWriter writer) {
        serializeVocabulary(vocabulary.getNamespace(), vocabulary.getClasses(), vocabulary.getProperties(),
                vocabulary.getComments(), writer);
    }

    /**
     * Serializes the given <code>vocabulary</code> to <i>NQuads</i> over the given output stream.
     *
     * @param vocabulary
     *            vocabulary to be serialized.
     * @param format
     *            output format for vocabulary.
     * @param willFollowAnother
     *            if <code>true</code> another vocab will be printed in the same stream.
     * @param ps
     *            output stream.
     *
     * @throws RDFHandlerException
     *             if there is an error handling the RDF
     */
    public static void serializeVocabulary(Vocabulary vocabulary, RDFFormat format, boolean willFollowAnother,
            PrintStream ps) {
        final RDFWriter rdfWriter;
        if (format == RDFFormat.RDFXML) {
            rdfWriter = Rio.createWriter(RDFFormat.RDFXML, ps);
            if (willFollowAnother)
                ps.print("\n");
            ps.print(RDF_XML_SEPARATOR);
            ps.print("\n");
        } else {
            rdfWriter = Rio.createWriter(format, ps);
        }
        serializeVocabulary(vocabulary, rdfWriter);
    }

    /**
     * Serialized the given <code>vocabulary</code> to <i>NQuads</i> and return them as string.
     *
     * @param vocabulary
     *            vocabulary to be serialized.
     * @param format
     *            output format for vocabulary.
     *
     * @return string contained serialization.
     *
     * @throws RDFHandlerException
     *             if there is an error handling the RDF
     */
    public static String serializeVocabulary(Vocabulary vocabulary, RDFFormat format) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps;
        try {
            ps = new PrintStream(baos, true, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException("UTF-8 encoding error when serializing the vocabulary to NQuads.", e1);
        }
        serializeVocabulary(vocabulary, format, false, ps);
        ps.close();
        try {
            return baos.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error writing ByteArrayOutputStream to String with \"UTF-8\" encoding!");
        }
    }

    /**
     * Serializes all the vocabularies to <i>NQuads</i> over the given output stream.
     *
     * @param format
     *            output format for vocabularies.
     * @param ps
     *            output print stream.
     */
    public static void serializeVocabularies(RDFFormat format, PrintStream ps) {
        final Class<Vocabulary> vocabularyClass = Vocabulary.class;
        @SuppressWarnings("rawtypes")
        final List<Class> vocabularies = DiscoveryUtils.getClassesInPackage(vocabularyClass.getPackage().getName(),
                vocabularyClass);
        int currentIndex = 0;
        for (Class<?> vocabClazz : vocabularies) {
            final Vocabulary instance;
            try {
                final Constructor<?> constructor = vocabClazz.getDeclaredConstructor();
                constructor.trySetAccessible();
                instance = (Vocabulary) constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error while instantiating vocabulary class " + vocabClazz, e);
            }
            try {
                serializeVocabulary(instance, format, currentIndex < vocabularies.size() - 2, ps);
            } catch (RDFHandlerException rdfhe) {
                throw new RuntimeException("Error while serializing vocabulary.", rdfhe);
            }
        }
    }

}
