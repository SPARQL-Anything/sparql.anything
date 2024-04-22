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

@Format(binary = true, name = "Spreadsheet",
description = "Spreadsheets are files that organise data as a collection of named tables.\n" +
		"Similarly to CSV, each table of a spreadsheet can be seen as a container of data records.\n" +
		"Each container is then stored in a different RDF graph.",
resourceExample = "https://sparql-anything.cc/examples/Book1.xlsx")
package io.github.sparqlanything.spreadsheet;

import io.github.sparqlanything.model.annotations.Format;
