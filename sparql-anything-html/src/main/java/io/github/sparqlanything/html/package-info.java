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

@Format(name = "HTML", description = "HTML can be captured by the Document Object Model (DOM) specification. HTML elements (also known as tags) can be considered containers.\n" +
		"\n" +
		"According to the Facade-X model, SPARQL Anything uses:\n" +
		"\n" +
		"RDF Properties for specifying tag attributes;\n" +
		"Container membership properties for specifying relations to child elements in the DOM tree. These may include text, which can be expressed as RDF literals of type xsd:string.\n" +
		"Tag names are used to type the container. Specifically, the tag name is used to mint a URI that identifies the class of the corresponding containers.",
resourceExample = "https://sparql-anything.cc/examples/simple.html")
package io.github.sparqlanything.html;

import io.github.sparqlanything.model.annotations.Format;
