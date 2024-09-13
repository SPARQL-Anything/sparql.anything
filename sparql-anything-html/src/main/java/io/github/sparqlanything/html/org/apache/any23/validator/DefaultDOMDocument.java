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

import io.github.sparqlanything.html.org.apache.any23.extractor.html.DomUtils;
import io.github.sparqlanything.html.org.apache.any23.validator.DOMDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * This class wraps the <i>DOM</i> document.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class DefaultDOMDocument implements DOMDocument {

    private URI documentIRI;

    private Document document;

    public DefaultDOMDocument(URI documentIRI, Document document) {
        if (documentIRI == null) {
            throw new NullPointerException("documentIRI cannot be null.");
        }
        if (document == null) {
            throw new NullPointerException("document cannot be null.");
        }
        this.documentIRI = documentIRI;
        this.document = document;
    }

    public URI getDocumentIRI() {
        return documentIRI;
    }

    public Document getOriginalDocument() {
        return document;
    }

    public List<Node> getNodes(String xPath) {
        return DomUtils.findAll(document, xPath);
    }

    public Node getNode(String xPath) {
        List<Node> nodes = DomUtils.findAll(document, xPath);
        if (nodes.size() == 0) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Cannot find node at XPath '%s'", xPath));
        }
        if (nodes.size() > 1) {
            throw new IllegalArgumentException(
                    String.format(Locale.ROOT, "The given XPath '%s' corresponds to more than one node.", xPath));
        }
        return nodes.get(0);
    }

    public void addAttribute(String xPath, String attrName, String attrValue) {
        Node node = getNode(xPath);
        NamedNodeMap namedNodeMap = node.getAttributes();
        Attr attr = document.createAttribute(attrName);
        attr.setNodeValue(attrValue);
        namedNodeMap.setNamedItem(attr);
    }

    public List<Node> getNodesWithAttribute(String attrName) {
        return DomUtils.findAllByAttributeName(document, attrName);
    }

}
