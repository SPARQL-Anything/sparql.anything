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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Hans Brende
 */
public class JsoupUtils {

    public static Document parse(InputStream input, String documentIRI, String encoding) throws IOException {
        // Jsoup doesn't allow null document URIs
        if (documentIRI == null) {
            documentIRI = "";
        }

        // workaround for Jsoup issue #1009
        if (encoding == null) {

            int c;
            do {
                c = input.read();
            } while (c != -1 && Character.isWhitespace(c));

            if (c != -1) {
                int capacity = 256;
                byte[] bytes = new byte[capacity];
                int length = 0;
                bytes[length++] = (byte) c;

                if (c == '<') {
                    c = input.read();
                    if (c != -1) {
                        bytes[length++] = (byte) c;
                        if (c == '?') {
                            c = input.read();

                            while (c != -1) {
                                if (length == capacity) {
                                    capacity *= 2;
                                    bytes = Arrays.copyOf(bytes, capacity);
                                }
                                bytes[length++] = (byte) c;

                                if (c == '>') {
                                    if (length >= 20 && bytes[length - 2] == '?') {
                                        String decl = "<" + new String(bytes, 2, length - 4, StandardCharsets.UTF_8)
                                                + ">";
                                        Document doc = Jsoup.parse(decl, documentIRI,
                                                Parser.xmlParser());
                                        for (org.jsoup.nodes.Element el : doc.children()) {
                                            if ("xml".equalsIgnoreCase(el.tagName())) {
                                                String enc = el.attr("encoding");
                                                if (enc != null && !enc.isEmpty()) {
                                                    encoding = enc;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }

                                c = input.read();
                            }
                        }
                    }

                }

                input = new SequenceInputStream(new ByteArrayInputStream(bytes, 0, length), input);
            }

        }

        // Use Parser.htmlParser() to parse javascript correctly
        return Jsoup.parse(input, encoding, documentIRI, Parser.htmlParser());
    }

}
