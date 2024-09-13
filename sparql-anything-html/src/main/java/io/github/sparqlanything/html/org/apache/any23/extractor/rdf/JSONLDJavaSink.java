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

package io.github.sparqlanything.html.org.apache.any23.extractor.rdf;

import java.util.Map.Entry;
import java.util.UUID;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import com.github.jsonldjava.core.JsonLdConsts;
import com.github.jsonldjava.core.JsonLdTripleCallback;
import com.github.jsonldjava.core.RDFDataset;

/**
 * @author Hans Brende (hansbrende@apache.org)
 */
class JSONLDJavaSink implements JsonLdTripleCallback {

    private static final String BNODE_PREFIX = JsonLdConsts.BLANK_NODE_PREFIX;

    private final ExtractionResult handler;
    private final ValueFactory valueFactory;
    private final String bNodeUniquifier;

    JSONLDJavaSink(ExtractionResult handler, ValueFactory valueFactory) {
        this.handler = handler;
        this.valueFactory = valueFactory;
        this.bNodeUniquifier = "n" + UUID.randomUUID().toString().replace("-", "") + "x";
    }

    private Resource createResource(RDFDataset.Node resource) {
        String value = resource == null ? null : resource.getValue();
        if (value != null && value.startsWith(BNODE_PREFIX)) {
            String bNodeId = value.substring(BNODE_PREFIX.length());

            if (bNodeId.length() < 32) { // not globally unique; will collide with other blank node ids
                if (bNodeId.isEmpty()) {
                    bNodeId = Integer.toHexString(System.identityHashCode(resource));
                }
                bNodeId = bNodeUniquifier + bNodeId;
            }

            return valueFactory.createBNode(bNodeId);
        }
        return valueFactory.createIRI(value);
    }

    private void writeQuad(RDFDataset.Node sNode, RDFDataset.Node pNode, Value o, String graphName) {
        if (graphName != null && graphName.startsWith(BNODE_PREFIX)) {
            // TODO support blank node graph names in Any23
            return;
        }
        Resource s = createResource(sNode);
        IRI p = valueFactory.createIRI(pNode == null ? null : pNode.getValue());
        if (s == null || p == null || o == null) {
            return;
        }
        if (graphName == null || graphName.isEmpty() || JsonLdConsts.DEFAULT.equalsIgnoreCase(graphName)) {
            handler.writeTriple(s, p, o);
        } else {
            handler.writeTriple(s, p, o, valueFactory.createIRI(graphName));
        }
    }

    @Override
    public Object call(final RDFDataset dataset) {
        for (Entry<String, String> nextNamespace : dataset.getNamespaces().entrySet()) {
            handler.writeNamespace(nextNamespace.getKey(), nextNamespace.getValue());
        }
        for (String graphName : dataset.keySet()) {
            for (RDFDataset.Quad quad : dataset.getQuads(graphName)) {
                RDFDataset.Node s = quad.getSubject();
                RDFDataset.Node p = quad.getPredicate();
                RDFDataset.Node o = quad.getObject();
                if (o == null || !o.isLiteral()) {
                    writeQuad(s, p, createResource(o), graphName);
                } else {
                    String lang = o.getLanguage();
                    String datatype = o.getDatatype();
                    String literal = o.getValue();
                    if (lang != null && !lang.isEmpty()
                            && (datatype == null || datatype.indexOf(':') < 0
                                    || JsonLdConsts.RDF_LANGSTRING.equalsIgnoreCase(datatype)
                                    || JsonLdConsts.XSD_STRING.equalsIgnoreCase(datatype))) {
                        writeQuad(s, p, valueFactory.createLiteral(literal, lang), graphName);
                    } else if (datatype != null && !datatype.isEmpty()) {
                        writeQuad(s, p, valueFactory.createLiteral(literal, valueFactory.createIRI(datatype)),
                                graphName);
                    } else {
                        writeQuad(s, p, valueFactory.createLiteral(literal), graphName);
                    }
                }
            }
        }
        return null;
    }
}
