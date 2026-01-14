/*
 * Copyright (c) 2025 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

/*
 */

package io.github.sparqlanything.fuseki;

import io.github.sparqlanything.model.annotations.FXFunctionDoc;

/**
 * Metadata for a SPARQL function extracted from annotations.
 */
public class FunctionMetadata {

	private final String uri;
	private final String label;
	private final String description;
	private final String example;
	private final String group;
	private final Class<?> functionClass;

	public FunctionMetadata(String uri, Class<?> functionClass) {
		this.uri = uri;
		this.functionClass = functionClass;
		
		FXFunctionDoc doc = functionClass.getAnnotation(FXFunctionDoc.class);
		if (doc != null) {
			this.description = doc.description();
			this.example = doc.example();
			this.group = doc.group();
			this.label = doc.label().isEmpty() ? extractLabelFromUri(uri) : doc.label();
		} else {
			// Fallback if not annotated
			this.description = "SPARQL Anything function";
			this.example = "";
			this.group = "FUNCTIONS";
			this.label = extractLabelFromUri(uri);
		}
	}

	private String extractLabelFromUri(String uri) {
		// Extract last part after namespace
		int lastSlash = uri.lastIndexOf('/');
		int lastHash = uri.lastIndexOf('#');
		int lastSep = Math.max(lastSlash, lastHash);
		if (lastSep >= 0 && lastSep < uri.length() - 1) {
			return "fx:" + uri.substring(lastSep + 1);
		}
		return uri;
	}

	public String getUri() {
		return uri;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public String getExample() {
		return example;
	}

	public String getGroup() {
		return group;
	}

	public Class<?> getFunctionClass() {
		return functionClass;
	}
}
