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

@Format(name = "YAML",
description = "YAML is a lightweight, human-readable data-serialization language.\n" +
		"YAML is a ``superset'' of JSON (any JSON file can be specified in YAML) and, similarly to JSON, data can be organised in lists or associative arrays.\n" +
		"However, differently from JSON, comments and custom data types are allowed.\n" +
		"Therefore, in addition to the basic data structures required for capturing JSON files, *instance-of* is needed for representing custom data types.\n",
resourceExample = "https://sparql-anything.cc/examples/example.yaml")
package io.github.sparqlanything.yaml;

import io.github.sparqlanything.model.annotations.Format;
