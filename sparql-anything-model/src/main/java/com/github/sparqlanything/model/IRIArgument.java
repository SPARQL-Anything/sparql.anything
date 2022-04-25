/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.model;

public enum IRIArgument {

	LOCATION("location"), MEDIA_TYPE("media-type"), NAMESPACE("namespace"), ROOT("root"), BLANK_NODES("blank-nodes"),
	TRIPLIFIER("triplifier"), CHARSET("charset"), METADATA("metadata"), CONTENT("content"),
	FROM_ARCHIVE("from-archive"), TRIM_STRINGS("trim-strings"), NULL_STRING("null-string"), STRATEGY("strategy"),
	SLICE("slice"), COMMAND("command"), USE_RDFS_MEMBER("use-rdfs-member");

	private String s;

	IRIArgument(String s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return s;
	}

}
