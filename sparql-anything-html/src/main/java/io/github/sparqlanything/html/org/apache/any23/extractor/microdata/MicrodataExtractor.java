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

package io.github.sparqlanything.html.org.apache.any23.extractor.microdata;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionParameters;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.*;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.MicrodataParser;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import org.eclipse.rdf4j.common.net.ParsedIRI;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of <a href="https://www.w3.org/TR/microdata/">Microdata</a> extractor, based on
 * {@link TagSoupDOMExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @author Hans Brende (hansbrende@apache.org)
 */
public class MicrodataExtractor implements Extractor.TagSoupDOMExtractor {

    static final IRI MICRODATA_ITEM = RDFUtils.iri("http://www.w3.org/1999/xhtml/microdata#item");

    private static final ParsedIRI EMPTY_FRAG = ParsedIRI.create("#");

    @Override
    public ExtractorDescription getDescription() {
        return MicrodataExtractorFactory.getDescriptionInstance();
    }

    /**
     * This extraction performs the <a href="https://www.w3.org/TR/microdata-rdf/">Microdata to RDF conversion
     * algorithm</a>.
     */
    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, Document in,
            ExtractionResult out) throws IOException, ExtractionException {

        final MicrodataParserReport parserReport = MicrodataParser.getMicrodata(in);
        if (parserReport.getErrors().length > 0) {
            notifyError(parserReport.getErrors(), out);
        }
        final io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope[] itemScopes = parserReport.getDetectedItemScopes();
        if (itemScopes.length == 0) {
            return;
        }

        final IRI documentIRI = extractionContext.getDocumentIRI();
        final ParsedIRI parsedDocumentIRI = ParsedIRI.create(documentIRI.stringValue());

        boolean isStrict = extractionParameters.getFlag("any23.microdata.strict");
        final IRI defaultNamespace;
        if (!isStrict) {
            defaultNamespace = RDFUtils.iri(extractionParameters.getProperty("any23.microdata.ns.default"));
            if (!defaultNamespace.getLocalName().isEmpty()) {
                throw new IllegalArgumentException("invalid namespace IRI: " + defaultNamespace);
            }
        } else {
            // TODO: incorporate document's "base" element
            defaultNamespace = RDFUtils.iri(parsedDocumentIRI.resolve(EMPTY_FRAG).toString());
        }

        // https://www.w3.org/TR/microdata-rdf/#generate-the-triples
        final Map<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope, Resource> mappings = new HashMap<>();
        for (io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope itemScope : itemScopes) {
            Resource subject = processType(itemScope, parsedDocumentIRI, out, mappings, defaultNamespace);

            // Writing out md:item triple has been removed from spec
            // but for now, keep for backwards compatibility.
            out.writeTriple(documentIRI, MICRODATA_ITEM, subject);
        }
    }

    /**
     * Recursive method implementing 6.3 "generate the triples" of the
     * <a href="https://www.w3.org/TR/microdata-rdf/#generate-the-triples">Microdata to RDF</a> extraction algorithm.
     */
    private Resource processType(io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope itemScope, ParsedIRI documentIRI, ExtractionResult out,
                                 Map<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope, Resource> mappings, IRI defaultNamespace) throws ExtractionException {
        Resource subject = mappings.computeIfAbsent(itemScope,
                scope -> createSubjectForItemId(documentIRI, scope.getItemId()));

        List<IRI> itemScopeTypes = itemScope.getTypes();
        if (!itemScopeTypes.isEmpty()) {
            defaultNamespace = getNamespaceIRI(itemScopeTypes.get(0));
            for (IRI type : itemScopeTypes) {
                out.writeTriple(subject, RDF.TYPE, type);
            }
        }
        for (Map.Entry<String, List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp>> itemProps : itemScope.getProperties().entrySet()) {
            String propName = itemProps.getKey();
            IRI predicate = getPredicate(defaultNamespace, propName);
            if (predicate == null) {
                continue;
            }
            for (io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp itemProp : itemProps.getValue()) {
                try {
                    processProperty(subject, predicate, itemProp, documentIRI, mappings, out, defaultNamespace);
                } catch (URISyntaxException e) {
                    throw new ExtractionException(
                            "Error while processing on subject '" + subject + "' the itemProp: '" + itemProp + "' ");
                }
            }
        }
        return subject;
    }

    private static Resource createSubjectForItemId(ParsedIRI documentIRI, String itemId) {
        if (itemId == null) {
            return RDFUtils.bnode();
        }
        try {
            return toAbsoluteIRI(documentIRI, itemId);
        } catch (URISyntaxException e) {
            return RDFUtils.bnode();
        }
    }

    private void processProperty(Resource subject, IRI predicate, ItemProp itemProp, ParsedIRI documentIRI,
                                 Map<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemScope, Resource> mappings, ExtractionResult out, IRI defaultNamespace)
            throws URISyntaxException, ExtractionException {

        Value value;
        Object propValue = itemProp.getValue().getContent();
        io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue.Type propType = itemProp.getValue().getType();
        if (itemProp.getValue().literal != null) {
            value = itemProp.getValue().literal;
        } else if (propType.equals(io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue.Type.Nested)) {
            value = processType((ItemScope) propValue, documentIRI, out, mappings, defaultNamespace);
        } else if (propType.equals(ItemPropValue.Type.Link)) {
            value = toAbsoluteIRI(documentIRI, (String) propValue);
            // TODO: support registries so hardcoding not needed
            if (predicate.stringValue().equals("http://schema.org/additionalType")) {
                if (itemProp.reverse) {
                    out.writeTriple((Resource) value, RDF.TYPE, subject);
                } else {
                    out.writeTriple(subject, RDF.TYPE, value);
                }
            }
        } else {
            throw new RuntimeException(
                    "Invalid Type '" + propType + "' for ItemPropValue with name: '" + predicate + "'");
        }
        if (itemProp.reverse) {
            out.writeTriple((Resource) value, predicate, subject);
        } else {
            out.writeTriple(subject, predicate, value);
        }
    }

    private static final String hcardPrefix = "http://microformats.org/profile/hcard";
    private static final IRI hcardNamespaceIRI = RDFUtils.iri("http://microformats.org/profile/hcard#");

    private static IRI getNamespaceIRI(IRI itemType) {
        // TODO: support registries so hardcoding not needed
        return itemType.stringValue().startsWith(hcardPrefix) ? hcardNamespaceIRI : itemType;
    }

    private static IRI getPredicate(IRI namespaceIRI, String localName) {
        return toAbsoluteIRI(localName).orElseGet(
                () -> namespaceIRI == null ? null : RDFUtils.iri(namespaceIRI.getNamespace(), localName.trim()));
    }

    private static Optional<IRI> toAbsoluteIRI(String urlString) {
        if (urlString != null) {
            try {
                ParsedIRI iri = ParsedIRI.create(urlString.trim());
                if (iri.isAbsolute()) {
                    return Optional.of(RDFUtils.iri(iri.toString()));
                }
            } catch (RuntimeException e) {
                // not an absolute iri
            }
        }
        return Optional.empty();
    }

    private static IRI toAbsoluteIRI(ParsedIRI documentIRI, String part) throws URISyntaxException {
        try {
            return RDFUtils.iri(documentIRI.resolve(part.trim()));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof URISyntaxException) {
                throw (URISyntaxException) e.getCause();
            } else {
                throw new URISyntaxException(String.valueOf(part),
                        e.getClass().getName() + (e.getMessage() != null ? ": " + e.getMessage() : ""));
            }
        }
    }

    private void notifyError(MicrodataParserException[] errors, ExtractionResult out) {
        for (MicrodataParserException mpe : errors) {
            out.notifyIssue(IssueReport.IssueLevel.ERROR, mpe.toJSON(), mpe.getErrorLocationBeginRow(),
                    mpe.getErrorLocationBeginCol());
        }
    }

}
