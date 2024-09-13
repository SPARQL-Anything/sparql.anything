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

package io.github.sparqlanything.html.org.apache.any23.validator;

import io.github.sparqlanything.html.org.apache.any23.validator.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.net.URI;
import java.util.List;

/**
 * This interface models a document to be processed by the {@link Validator}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface DOMDocument {

    /**
     * @return the original document IRI.
     */
    URI getDocumentIRI();

    /**
     * Returns the original document.
     *
     * @return the original document.
     */
    Document getOriginalDocument();

    /**
     * Returns the list of nodes addressed by the given <i>XPath</i>.
     *
     * @param xPath
     *            a valid XPath
     *
     * @return a not null list of nodes.
     */
    List<Node> getNodes(String xPath);

    /**
     * Returns the node addressed by the given <i>XPath</i>, if more then one an exception will be raised.
     *
     * @param xPath
     *            a valid XPath.
     *
     * @return a node or <code>null</code> if nothing found.
     */
    Node getNode(String xPath);

    /**
     * Adds an attribute to a node addressed by the given <i>XPath</i>.
     *
     * @param xPath
     *            the XPath pointing the node.
     * @param attrName
     *            the name of the attribute.
     * @param attrValue
     *            the value of the attribute.
     */
    void addAttribute(String xPath, String attrName, String attrValue);

    /**
     * Returns all the nodes declaring an attribute with the specified name.
     *
     * @param attrName
     *            name of attribute to use for filtering.
     *
     * @return a list of nodes. <i>null</i> if no matches found.
     */
    List<Node> getNodesWithAttribute(String attrName);
}
