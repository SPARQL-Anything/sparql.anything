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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single YASGUI snippet. 
 */
public class SnippetSection {

	private final String label;
	private final String code;
	private final String group;

	public SnippetSection(String label, String code, String group) {
		this.label = label;
		this.code = code;
		this.group = group;
	}

	@JsonProperty("label")
	public String getLabel() {
		return label;
	}

	@JsonProperty("code")
	public String getCode() {
		return code;
	}

	@JsonProperty("group")
	public String getGroup() {
		return group;
	}

	/**
	 * Escapes the code for inclusion in a JavaScript string.
	 * Handles newlines, quotes, backslashes, etc.
	 */
	public String getEscapedCode() {
		return code
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}

	/**
	 * Escapes the label for inclusion in a JavaScript string.
	 */
	public String getEscapedLabel() {
		return label
			.replace("\\", "\\\\")
			.replace("\"", "\\\"");
	}
}
