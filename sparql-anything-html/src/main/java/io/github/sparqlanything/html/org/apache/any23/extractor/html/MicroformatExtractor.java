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

package io.github.sparqlanything.html.org.apache.any23.extractor.html;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionParameters;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.annotations.Includes;
import io.github.sparqlanything.html.org.apache.any23.rdf.Any23ValueFactoryWrapper;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Locale;

/**
 * The abstract base class for any <a href="microformats.org/">Microformat specification</a> extractor.
 */
public abstract class MicroformatExtractor implements TagSoupDOMExtractor {

    public static final String BEGIN_SCRIPT = "<script>";
    public static final String END_SCRIPT = "</script>";

    private HTMLDocument htmlDocument;

    private ExtractionContext context;

    private IRI documentIRI;

    private ExtractionResult out;

    protected final Any23ValueFactoryWrapper valueFactory = new Any23ValueFactoryWrapper(
            SimpleValueFactory.getInstance());

    /**
     * Returns the description of this extractor.
     *
     * @return a human readable description.
     */
    public abstract ExtractorDescription getDescription();

    /**
     * Performs the extraction of the data and writes them to the model. The nodes generated in the model can have any
     * name or implicit label but if possible they <i>SHOULD</i> have names (either URIs or AnonId) that are uniquely
     * derivable from their position in the DOM tree, so that multiple extractors can merge information.
     *
     * @return true if extraction is successful
     *
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    protected abstract boolean extract() throws ExtractionException;

    public HTMLDocument getHTMLDocument() {
        return htmlDocument;
    }

    public ExtractionContext getExtractionContext() {
        return context;
    }

    public IRI getDocumentIRI() {
        return documentIRI;
    }

    public final void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, Document in,
            ExtractionResult out) throws IOException, ExtractionException {
        this.htmlDocument = new HTMLDocument(in);
        this.context = extractionContext;
        this.documentIRI = extractionContext.getDocumentIRI();
        this.out = out;
        valueFactory.setIssueReport(out);
        try {
            extract();
        } finally {
            valueFactory.setIssueReport(null);
        }
    }

    /**
     * Returns the {@link io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult} associated to the extraction session.
     *
     * @return a valid extraction result.
     */
    protected ExtractionResult getCurrentExtractionResult() {
        return out;
    }

    protected void setCurrentExtractionResult(ExtractionResult out) {
        this.out = out;
    }

    protected ExtractionResult openSubResult(ExtractionContext context) {
        return out.openSubResult(context);
    }

    /**
     * Helper method that adds a literal property to a subject only if the value of the property is a valid string.
     *
     * @param n
     *            the <i>HTML</i> node from which the property value has been extracted.
     * @param subject
     *            the property subject.
     * @param p
     *            the property IRI.
     * @param value
     *            the property value.
     *
     * @return returns <code>true</code> if the value has been accepted and added, <code>false</code> otherwise.
     */
    protected boolean conditionallyAddStringProperty(Node n, Resource subject, IRI p, String value) {
        if (value == null)
            return false;
        value = value.trim();
        return value.length() > 0 && conditionallyAddLiteralProperty(n, subject, p, valueFactory.createLiteral(value));
    }

    /**
     * Helper method that adds a literal property to a node.
     *
     * @param n
     *            the <i>HTML</i> node from which the property value has been extracted.
     * @param subject
     *            subject the property subject.
     * @param property
     *            the property IRI.
     * @param literal
     *            value the property value.
     *
     * @return returns <code>true</code> if the literal has been accepted and added, <code>false</code> otherwise.
     */
    protected boolean conditionallyAddLiteralProperty(Node n, Resource subject, IRI property, Literal literal) {
        final String literalStr = literal.stringValue();
        if (containsScriptBlock(literalStr)) {
            out.notifyIssue(IssueReport.IssueLevel.WARNING,
                    String.format(Locale.ROOT, "Detected script in literal: [%s]", literalStr), -1, -1);
            return false;
        }
        out.writeTriple(subject, property, literal);
        TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addPropertyPath(this.getClass(), subject, property, null, DomUtils.getXPathListForNode(n));
        return true;
    }

    /**
     * Helper method that adds a IRI property to a node.
     *
     * @param subject
     *            the property subject.
     * @param property
     *            the property IRI.
     * @param uri
     *            the property object.
     *
     * @return <code>true</code> if the the resource has been added, <code>false</code> otherwise.
     */
    protected boolean conditionallyAddResourceProperty(Resource subject, IRI property, IRI uri) {
        if (uri == null)
            return false;
        out.writeTriple(subject, property, uri);
        return true;
    }

    /**
     * Helper method that adds a BNode property to a node.
     *
     * @param n
     *            the <i>HTML</i> node used for extracting such property.
     * @param subject
     *            the property subject.
     * @param property
     *            the property IRI.
     * @param bnode
     *            the property value.
     */
    protected void addBNodeProperty(Node n, Resource subject, IRI property, BNode bnode) {
        out.writeTriple(subject, property, bnode);
        TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addPropertyPath(this.getClass(), subject, property, bnode, DomUtils.getXPathListForNode(n));
    }

    /**
     * Helper method that adds a BNode property to a node.
     *
     * @param subject
     *            the property subject.
     * @param property
     *            the property IRI.
     * @param bnode
     *            the property value.
     */
    protected void addBNodeProperty(Resource subject, IRI property, BNode bnode) {
        out.writeTriple(subject, property, bnode);
    }

    /**
     * Helper method that adds a IRI property to a node.
     *
     * @param subject
     *            subject to add
     * @param property
     *            predicate to add
     * @param object
     *            object to add
     */
    protected void addIRIProperty(Resource subject, IRI property, IRI object) {
        out.writeTriple(subject, property, object);
    }

    protected IRI fixLink(String link) {
        return valueFactory.fixLink(link, null);
    }

    protected IRI fixLink(String link, String defaultSchema) {
        return valueFactory.fixLink(link, defaultSchema);
    }

    private boolean containsScriptBlock(String in) {
        final String inLowerCase = in.toLowerCase(Locale.ROOT);
        final int beginBlock = inLowerCase.indexOf(BEGIN_SCRIPT);
        if (beginBlock == -1) {
            return false;
        }
        return inLowerCase.indexOf(END_SCRIPT, beginBlock + BEGIN_SCRIPT.length()) != -1;
    }

    /**
     * This method checks if there is a native nesting relationship between two {@link MicroformatExtractor}.
     *
     * @see  io.github.sparqlanything.html.org.apache.any23.extractor.html.annotations.Includes
     *
     * @param including
     *            the including {@link MicroformatExtractor}
     * @param included
     *            the included {@link MicroformatExtractor}
     *
     * @return <code>true</code> if there is a declared nesting relationship
     */
    public static boolean includes(Class<? extends MicroformatExtractor> including,
            Class<? extends MicroformatExtractor> included) {
        Includes includes = including.getAnnotation(Includes.class);
        if (includes != null) {
            Class<? extends MicroformatExtractor>[] extractors = includes.extractors();
            if (extractors != null && extractors.length > 0) {
                for (Class<? extends MicroformatExtractor> extractor : extractors) {
                    if (extractor.equals(included)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
