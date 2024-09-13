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

import org.jsoup.nodes.Attribute;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;

/**
 * The parsing configuration for a {@link TagSoupParser}
 *
 * @author Hans Brende
 */
abstract class TagSoupParsingConfiguration {

    String name() {
        return getClass().getSimpleName();
    }

    abstract Document parse(InputStream input, String documentIRI, String encoding) throws IOException;

    static TagSoupParsingConfiguration getDefault() {
        return JsoupConfig.instance;
    }

    private static class JsoupConfig extends TagSoupParsingConfiguration {

        private static final JsoupConfig instance = new JsoupConfig();

        @Override
        Document parse(InputStream input, String documentIRI, String encoding) throws IOException {

            org.jsoup.nodes.Document document = JsoupUtils.parse(input, documentIRI, encoding);

            return convert(document);
        }

        private static Document convert(org.jsoup.nodes.Document document) {
            Document w3cDoc = new org.apache.html.dom.HTMLDocumentImpl();

            org.jsoup.nodes.Element rootEl = document.children().first();
            if (rootEl != null) {
                NodeTraversor.traverse(new DocumentConverter(w3cDoc), rootEl);
            }

            return w3cDoc;
        }

        private static class DocumentConverter implements NodeVisitor {

            private final Document doc;
            private org.w3c.dom.Element dest;

            DocumentConverter(Document doc) {
                this.doc = doc;
            }

            @Override
            public void head(org.jsoup.nodes.Node source, int depth) {
                if (source instanceof org.jsoup.nodes.Element) {
                    org.jsoup.nodes.Element sourceEl = (org.jsoup.nodes.Element) source;

                    org.w3c.dom.Element el = doc.createElement(sourceEl.tagName());
                    copyAttributes(sourceEl, el);
                    if (dest == null) {
                        doc.appendChild(el);
                    } else {
                        dest.appendChild(el);
                    }
                    dest = el;
                } else if (source instanceof org.jsoup.nodes.TextNode) {
                    org.jsoup.nodes.TextNode sourceText = (org.jsoup.nodes.TextNode) source;
                    Text text = doc.createTextNode(sourceText.getWholeText());
                    dest.appendChild(text);
                } else if (source instanceof org.jsoup.nodes.Comment) {
                    org.jsoup.nodes.Comment sourceComment = (org.jsoup.nodes.Comment) source;
                    Comment comment = doc.createComment(sourceComment.getData());
                    dest.appendChild(comment);
                } else if (source instanceof org.jsoup.nodes.DataNode) {
                    org.jsoup.nodes.DataNode sourceData = (org.jsoup.nodes.DataNode) source;
                    Text node = doc.createTextNode(stripCDATA(sourceData.getWholeData()));
                    dest.appendChild(node);
                }
            }

            @Override
            public void tail(org.jsoup.nodes.Node source, int depth) {
                if (source instanceof org.jsoup.nodes.Element && dest.getParentNode() instanceof org.w3c.dom.Element) {
                    dest = (org.w3c.dom.Element) dest.getParentNode();
                }
            }

            private void copyAttributes(org.jsoup.nodes.Node source, org.w3c.dom.Element el) {
                for (Attribute attribute : source.attributes()) {
                    // valid xml attribute names are: ^[a-zA-Z_:][-a-zA-Z0-9_:.]
                    String key = attribute.getKey().replaceAll("[^-a-zA-Z0-9_:.]", "");
                    if (key.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*"))
                        el.setAttribute(key, attribute.getValue());
                }
            }
        }

        private static String stripCDATA(String string) {
            return reduceToContent(string, "<![CDATA[", "]]>");
        }

        private static String reduceToContent(String string, String startMarker, String endMarker) {
            int i = 0;
            int startContent = -1;
            int l1 = startMarker.length();

            int l2;
            char c;
            for (l2 = endMarker.length(); i < string.length() - l1 - l2; ++i) {
                c = string.charAt(i);
                if (!Character.isWhitespace(c)) {
                    if (c == startMarker.charAt(0) && startMarker.equals(string.substring(i, l1 + i))) {
                        startContent = i + l1;
                        break;
                    }

                    return string;
                }
            }

            if (startContent != -1) {
                for (i = string.length() - 1; i > startContent + l2; --i) {
                    c = string.charAt(i);
                    if (!Character.isWhitespace(c)) {
                        if (c == endMarker.charAt(l2 - 1) && endMarker.equals(string.substring(i - l2 + 1, i + 1))) {

                            return string.substring(startContent, i - 2);
                        }

                        return string;
                    }
                }

            }
            return string;
        }

    }

}
