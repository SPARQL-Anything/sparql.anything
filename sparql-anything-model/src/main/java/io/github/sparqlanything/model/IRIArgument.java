/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.model;

public enum IRIArgument {

	LOCATION("location"), MEDIA_TYPE("media-type"), NAMESPACE("namespace", Triplifier.XYZ_NS), ROOT("root"), BLANK_NODES("blank-nodes", "true"),NO_CACHE("no-cache", "false"), TRIPLIFIER("triplifier"), CHARSET("charset", "UTF-8"), METADATA("metadata", "false"), CONTENT("content"), FROM_ARCHIVE("from-archive"), TRIM_STRINGS("trim-strings", "false"), NULL_STRING("null-string"), STRATEGY("strategy", "1"), SLICE("slice", "false"), COMMAND("command"), USE_RDFS_MEMBER("use-rdfs-member", "false"), ONDISK_REUSE("ondisk.reuse", "true"), ONDISK("ondisk"), OP_SERVICE_SILENT("opservice.silent"), ANNOTATE_TRIPLES_WITH_SLOT_KEYS("annotate-triples-with-slot-keys", "false"),;

	private final String s;
	private String defaultValue;

	IRIArgument(String s) {
		this.s = s;
	}

	IRIArgument(String s, String defaultValue) {
		this.s = s;
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return s;
	}

	public String getDefaultValue(){
		return defaultValue;
	}

}
