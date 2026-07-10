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



package io.github.sparqlanything.model;

public class IRIArgument {
	public static final IRIArgument LOCATION = new IRIArgument("location");
	public static final IRIArgument MEDIA_TYPE = new IRIArgument("media-type");
	public static final IRIArgument NAMESPACE = new IRIArgument("namespace", Triplifier.XYZ_NS);
	public static final IRIArgument ROOT = new IRIArgument("root");
	public static final IRIArgument BLANK_NODES = new IRIArgument("blank-nodes", "true");
	public static final IRIArgument USE_CACHE = new IRIArgument("use-cache", "false");
	public static final IRIArgument TRIPLIFIER = new IRIArgument("triplifier");
	public static final IRIArgument CHARSET = new IRIArgument("charset", "UTF-8");
	public static final IRIArgument METADATA = new IRIArgument("metadata", "false");
	public static final IRIArgument CONTENT = new IRIArgument("content");
	public static final IRIArgument FROM_ARCHIVE = new IRIArgument("from-archive");
	public static final IRIArgument ARCHIVE_FORMAT = new IRIArgument("archive-format");
	public static final IRIArgument TRIM_STRINGS = new IRIArgument("trim-strings", "false");
	public static final IRIArgument NULL_STRING = new IRIArgument("null-string");
	public static final IRIArgument STRATEGY = new IRIArgument("strategy", "1");
	public static final IRIArgument SLICE = new IRIArgument("slice", "false");
	public static final IRIArgument SLICE_SIZE = new IRIArgument("slice.size", "1");
	public static final IRIArgument COMMAND = new IRIArgument("command");
	public static final IRIArgument USE_RDFS_MEMBER = new IRIArgument("use-rdfs-member", "false");
	public static final IRIArgument ONDISK_REUSE = new IRIArgument("ondisk.reuse", "true");
	public static final IRIArgument ONDISK = new IRIArgument("ondisk");
	public static final IRIArgument OP_SERVICE_SILENT = new IRIArgument("opservice.silent");
	public static final IRIArgument ANNOTATE_TRIPLES_WITH_SLOT_KEYS = new IRIArgument("annotate-triples-with-slot-keys", "false");

	public static final IRIArgument GENERATE_PREDICATE_LABELS = new IRIArgument("generate-predicate-labels", "false");
    public static final IRIArgument  READ_FROM_STD_IN =  new IRIArgument("read-from-std-in", "false");
    public static final IRIArgument  AUDIT =  new IRIArgument("audit", "false");
    public static final IRIArgument  QUERY =  new IRIArgument("query");

	// Options for S3 storage
	public static final IRIArgument  S3_ENDPOINT =  new IRIArgument("s3.endpoint", "false");
	public static final IRIArgument  S3_BUCKET_NAME =  new IRIArgument("s3.bucket-name");
	public static final IRIArgument  S3_KEY =  new IRIArgument("s3.key");
	public static final IRIArgument  S3_ACCESS_KEY =  new IRIArgument("s3.access-key");
	public static final IRIArgument  S3_SECRET_KEY =  new IRIArgument("s3.secret-key");
	public static final IRIArgument  S3_REGION =  new IRIArgument("s3.region");

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
