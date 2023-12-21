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

public class IRIArgument {
	public static final IRIArgument LOCATION = new IRIArgument("location");
	public static final IRIArgument MEDIA_TYPE = new IRIArgument("media-type");
	public static final IRIArgument NAMESPACE = new IRIArgument("namespace", Triplifier.XYZ_NS);
	public static final IRIArgument ROOT = new IRIArgument("root");
	public static final IRIArgument BLANK_NODES = new IRIArgument("blank-nodes", "true");
	public static final IRIArgument NO_CACHE = new IRIArgument("no-cache", "false");
	public static final IRIArgument TRIPLIFIER = new IRIArgument("triplifier");
	public static final IRIArgument CHARSET = new IRIArgument("charset", "UTF-8");
	public static final IRIArgument METADATA = new IRIArgument("metadata", "false");
	public static final IRIArgument CONTENT = new IRIArgument("content");
	public static final IRIArgument FROM_ARCHIVE = new IRIArgument("from-archive");
	public static final IRIArgument TRIM_STRINGS = new IRIArgument("trim-strings", "false");
	public static final IRIArgument NULL_STRING = new IRIArgument("null-string");
	public static final IRIArgument STRATEGY = new IRIArgument("strategy", "1");
	public static final IRIArgument SLICE = new IRIArgument("slice", "false");
	public static final IRIArgument COMMAND = new IRIArgument("command");
	public static final IRIArgument USE_RDFS_MEMBER = new IRIArgument("use-rdfs-member", "false");
	public static final IRIArgument ONDISK_REUSE = new IRIArgument("ondisk.reuse", "true");
	public static final IRIArgument ONDISK = new IRIArgument("ondisk");
	public static final IRIArgument OP_SERVICE_SILENT = new IRIArgument("opservice.silent");
	public static final IRIArgument ANNOTATE_TRIPLES_WITH_SLOT_KEYS = new IRIArgument("annotate-triples-with-slot-keys", "false");

	private final String name;
	private final String defaultValue;

	public IRIArgument(String s) {
		this(s, null);
	}

	public IRIArgument(String s, String defaultValue) {
		this.name = s;
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
