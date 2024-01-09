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

@Format(description = "RDF files can be targeted by the option `location`, the content is loaded as-is (no facade-x interpretation, obviously). In addition, the SPARQL Anything Command Line Interface can load static RDF files.\n" +
		"\n" +
		"The query does not need to include a SERVICE clause, so you can use the tool to just query some RDF file of your choice.\n" +
		"This is useful when you want to break down the process so that RDF files produced by previous SPARQL Anything processes are joined with data coming from additional transformatioons.\n" +
		"Examples of this can be found in the [tutorials](../TUTORIALS.md).\n\n" +
		"This feature is enabled with the command line argument `-l|--load` that accepts a file or a directory.\n" +
		"The files are loaded in a Dataset which becomes the target for the query execution.\n" +
		"A single file will be loaded in the default Graph. \n" +
		"In the second case, all RDF files in the folder are loaded, each one on a Named Graph.\n" +
		"\n" +
		"See also the documentation of the [Command Line Interface (CLI)](../CLI.md).\n",
name="RDF",
resourceExample = "https://sparql-anything.cc/examples/simple.ttl")
package io.github.sparqlanything.rdf;

import io.github.sparqlanything.model.annotations.Format;
