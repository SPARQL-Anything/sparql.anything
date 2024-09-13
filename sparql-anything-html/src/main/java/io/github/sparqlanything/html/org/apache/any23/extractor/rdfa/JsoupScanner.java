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

import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;
import io.github.sparqlanything.html.org.semarglproject.sink.XmlSink;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

import java.util.ArrayList;

/**
 * @author Hans Brende (hansbrende@apache.org)
 */
class JsoupScanner implements NodeVisitor {

    private final NamespaceSupport ns = new NamespaceSupport();
    private final AttributesImpl attrs = new AttributesImpl();
    private final String[] nameParts = new String[3];

    private final XmlSink handler;

    JsoupScanner(XmlSink handler) {
        this.handler = handler;
    }

    private static String orEmpty(String str) {
        return str == null ? "" : str;
    }

    private static final String[] commonHashDelimitedVocabs = { "http://creativecommons.org/ns",
            "http://www.w3.org/2002/07/owl", "http://www.w3.org/1999/02/22-rdf-syntax-ns", "http://www.w3.org/ns/rdfa",
            "http://www.w3.org/2000/01/rdf-schema", "http://www.w3.org/1999/xhtml/vocab",
            "http://www.w3.org/2001/XMLSchema", "http://microformats.org/profile/hcard",
            "http://www.w3.org/2006/vcard/ns", "http://ogp.me/ns", "http://ogp.me/ns/music", "http://ogp.me/ns/video",
            "http://ogp.me/ns/article", "http://ogp.me/ns/book", "http://ogp.me/ns/profile",
            "http://ogp.me/ns/website" };

    private void startElement(Element e) throws SAXException {
        ns.pushContext();

        attrs.clear();
        final ArrayList<String> remainingAttrs = new ArrayList<>();
        for (org.jsoup.nodes.Attribute attr : e.attributes()) {
            String name = attr.getKey();
            String value = attr.getValue();
            if (name.startsWith("xmlns")) {
                if (name.length() == 5) {
                    ns.declarePrefix("", value);
                    handler.startPrefixMapping("", value);
                    continue;
                } else if (name.charAt(5) == ':') {
                    String localName = name.substring(6);
                    ns.declarePrefix(localName, value);
                    handler.startPrefixMapping(localName, value);
                    continue;
                }
            } else if (name.equalsIgnoreCase("vocab")) {
                // Fix for ANY23-428
                name = "vocab";
                value = value.trim();
                int len = value.length();
                char lastChar;
                if (len != 0 && (lastChar = value.charAt(len - 1)) != '/' && lastChar != '#' && lastChar != ':') {
                    if (ArrayUtils.contains(commonHashDelimitedVocabs, value)) {
                        value += "#";
                    } else {
                        value += "/";
                    }
                }
            }

            remainingAttrs.add(name);
            remainingAttrs.add(value);
        }

        for (int i = 0, len = remainingAttrs.size(); i < len; i += 2) {
            String name = remainingAttrs.get(i);
            String value = remainingAttrs.get(i + 1);
            String[] parts = ns.processName(name, nameParts, true);
            if (parts != null) {
                attrs.addAttribute(orEmpty(parts[0]), orEmpty(parts[1]), parts[2], "CDATA", value);
            }
        }

        String qName = e.tagName();

        String[] parts = ns.processName(qName, nameParts, false);
        if (parts == null) {
            handler.startElement("", "", qName, attrs);
        } else {
            handler.startElement(orEmpty(parts[0]), orEmpty(parts[1]), parts[2], attrs);
        }

    }

    private void endElement(Element e) throws SAXException {

        String qName = e.tagName();
        String[] parts = ns.processName(qName, nameParts, false);
        if (parts == null) {
            handler.endElement("", "", qName);
        } else {
            handler.endElement(orEmpty(parts[0]), orEmpty(parts[1]), parts[2]);
        }

        for (org.jsoup.nodes.Attribute attr : e.attributes()) {
            String name = attr.getKey();
            if (name.startsWith("xmlns")) {
                if (name.length() == 5) {
                    handler.endPrefixMapping("");
                } else if (name.charAt(5) == ':') {
                    String localName = name.substring(6);
                    handler.endPrefixMapping(localName);
                }
            }
        }

        ns.popContext();
    }

    private void handleText(String str) throws SAXException {
        handler.characters(str.toCharArray(), 0, str.length());
    }

    private void handleComment(String str) throws SAXException {
        handler.comment(str.toCharArray(), 0, str.length());
    }

    @Override
    public void head(Node node, int depth) {
        try {
            if (node instanceof Element) {
                startElement((Element) node);
            } else if (node instanceof CDataNode) {
                handler.startCDATA();
                handleText(((CDataNode) node).text());
            } else if (node instanceof TextNode) {
                handleText(((TextNode) node).text());
                // TODO support document types
                // } else if (node instanceof DocumentType) {
                // DocumentType dt = (DocumentType)node;
                // handler.startDTD(dt.attr("name"), orNull(dt.attr("publicId")), orNull(dt.attr("systemId")));
            } else if (node instanceof Comment) {
                handleComment(((Comment) node).getData());
            }
        } catch (SAXException e) {
            sneakyThrow(e);
        }
    }

    @Override
    public void tail(Node node, int depth) {
        try {
            if (node instanceof Element) {
                endElement((Element) node);
            } else if (node instanceof CDataNode) {
                handler.endCDATA();
                // TODO support document types
                // } else if (node instanceof DocumentType) {
                // handler.endDTD();
            }
        } catch (SAXException e) {
            sneakyThrow(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }
}
