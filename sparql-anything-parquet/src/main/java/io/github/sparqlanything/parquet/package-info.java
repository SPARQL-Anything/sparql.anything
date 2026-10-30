/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@Format(name = "Parquet", description = "Apache Parquet is a column-oriented binary file format for tabular data, widely used by Spark, pandas, Polars and DuckDB. Each file carries its own schema, with typed and possibly nested columns (structs, lists and maps).\n" +
		"\n" +
		"A Parquet file is represented like a CSV file with headers: the root container holds one container per row, and each row holds one slot per column, named after the column. Values are typed literals derived from the Parquet schema; structs, lists and maps become nested containers. With the option `metadata=true` the Parquet schema and file metadata are also written to the metadata named graph, so that the data can be written back to Parquet with the same schema.\n" +
		"\n",
resourceExample = "https://sparql-anything.cc/examples/simple.parquet")
package io.github.sparqlanything.parquet;

import io.github.sparqlanything.model.annotations.Format;
