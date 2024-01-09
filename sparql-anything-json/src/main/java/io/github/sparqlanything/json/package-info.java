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

@Format(name = "JSON",
description = "The JavaScript Object Notation is specified by [ECMA](https://www.ecma-international.org/publications-and-standards/standards/ecma-404/).\n" +
		"The syntax defines three types of elements:\n" +
		"- *objects*, a set of key-value pairs, where keys are supposed to be unique;\n" +
		"- *values*, which are either strings, numbers, boolean, or the primitive 'null';\n" +
		"- and, *arrays*, which specify sequences (containing other arrays, objects, or values).\n" +
		"\n" +
		"According to Facade-X model, SPARQL Anything interprets objects and arrays as containers:\n" +
		"- RDF properties are used to link objects to values.\n" +
		"- Arrays are represented by the ordered sequence component.\n" +
		"- Values are expressed as *rdf:Literal*, selecting relevant XSD datatypes from the RDFS specification: *xsd:string*, *xsd:boolean*, *xsd:int*, *xsd:float*\n" +
		"\n" +
		"Currently, fields with the 'null' value are ignored.\n" +
		"<!-- However, we may decide to represent it as blank node or to create a primitive entity to express it, for example, similar to \\tt{rdf:nil}.}.  -->\n",
resourceExample = "https://sparql-anything.cc/examples/simple.json")
package io.github.sparqlanything.json;

import io.github.sparqlanything.model.annotations.Format;
