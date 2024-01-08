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

@Format(name = "Markdown", description = "Markdown is a lightweight markup language for writing formatted documents inspired to conventions of web posting. We can interpret a Markdown document as a sequence of blocks (e.g. paragraphs, lists, headings, code blocks). Some blocks (e.g. list items) contain other blocks, whereas others contain inline contents (e.g. links, images etc.). In SPARQL Anything, a document is represented as a list of typed containers. Where the type denotes the kind of block (e.g. heading, paragraph, emphasised text, link, image etc.); lists are needed for specifying the sequence of the blocks. Additional attributes such as the depth of the header or the type of list (bullets, numbers, etc...) can be also supported, relying on the key-value structure.\n\nSPARQL Anything relies on the [CommonMark](https://github.com/commonmark/commonmark-java) Java implementation of Commons Markdown.", resourceExample = "https://sparql-anything.cc/examples/simple.md")
package io.github.sparqlanything.markdown;

import io.github.sparqlanything.model.annotations.Format;
