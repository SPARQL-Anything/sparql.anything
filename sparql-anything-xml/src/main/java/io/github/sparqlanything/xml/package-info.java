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

@Format(name = "XML",
description = "The following analysis refers to the [Document Object Model (DOM) specification](https://dom.spec.whatwg.org).\n" +
		"XML elements (also known as tags) can be definitely considered containers, so we can reuse both the `rdf:Property` component for specifying tag attributes, and container membership properties for specifying relations to child elements in the DOM tree.\n" +
		"These may include text, which are expressed as RDF literals of type `xsd:string`. \n" +
		"Tag names are represented as RDF types: `rdf:type`.\n" +
		"SPARQL Anything reuses namespaces declared within the original document to name properties and types, when available, otherwise fallbacks to the default `xyz:`.\n" +
		"\n" +
		"!!! note\n" +
		"\tXML attribute values are always interpreted as literals, even if they are supposed to be QName in a referred XML schema. See also [this comment](https://github.com/SPARQL-Anything/sparql.anything/issues/322#issuecomment-1351299515).\n" +
		"\n",
resourceExample = "https://sparql-anything.cc/examples/simple.xml")
package io.github.sparqlanything.xml;

import io.github.sparqlanything.model.annotations.Format;
