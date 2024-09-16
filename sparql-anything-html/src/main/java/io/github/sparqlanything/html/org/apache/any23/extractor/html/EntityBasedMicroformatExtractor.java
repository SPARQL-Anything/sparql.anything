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

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import org.eclipse.rdf4j.model.BNode;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Base class for microformat extractors based on entities.
 *
 * @author Gabriele Renzi
 */
public abstract class EntityBasedMicroformatExtractor extends MicroformatExtractor {

    /**
     * Returns the base class name for the extractor.
     *
     * @return a string containing the base of the extractor.
     */
    protected abstract String getBaseClassName();

    /**
     * Resets the internal status of the extractor to prepare it to a new extraction section.
     */
    protected abstract void resetExtractor();

    /**
     * Extracts an entity from a <i>DOM</i> node.
     *
     * @param node
     *            the DOM node.
     * @param out
     *            the extraction result collector.
     *
     * @return <code>true</code> if the extraction has produces something, <code>false</code> otherwise.
     *
     * @throws ExtractionException
     *             if there is an error during extraction
     */
    protected abstract boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException;

    @Override
    public boolean extract() throws ExtractionException {
        List<Node> nodes = DomUtils.findAllByClassName(getHTMLDocument().getDocument(), getBaseClassName());
        boolean foundAny = false;
        int count = 1;
        for (Node node : nodes) {
            resetExtractor();
            String contextID = Integer.toString(count);
            ExtractionResult subResult = openSubResult(getExtractionContext().copy(contextID));
            foundAny |= extractEntity(node, subResult);
            subResult.close();
            count++;
        }
        return foundAny;
    }

    /**
     * @param node
     *            a DOM node representing a blank node
     *
     * @return an RDF blank node corresponding to that DOM node, by using a blank node ID like "MD5 of
     *         http://doc-uri/#xpath/to/node"
     */
    protected BNode getBlankNodeFor(Node node) {
        return RDFUtils.getBNode(getDocumentIRI() + "#" + DomUtils.getXPathForNode(node));
    }

}
