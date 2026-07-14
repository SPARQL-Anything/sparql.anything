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

	public static final String LOCATION_NAME = "location";
	public static final IRIArgument LOCATION = new IRIArgument(LOCATION_NAME);

	public static final String MEDIA_TYPE_NAME = "media-type";
	public static final IRIArgument MEDIA_TYPE = new IRIArgument(MEDIA_TYPE_NAME);

	public static final String NAMESPACE_NAME = "namespace";
	public static final IRIArgument NAMESPACE = new IRIArgument(NAMESPACE_NAME, Triplifier.XYZ_NS);

	public static final String ROOT_NAME = "root";
	public static final IRIArgument ROOT = new IRIArgument(ROOT_NAME);

	public static final String BLANK_NODES_NAME = "blank-nodes";
	public static final IRIArgument BLANK_NODES = new IRIArgument(BLANK_NODES_NAME, "true");

	public static final String USE_CACHE_NAME = "use-cache";
	public static final IRIArgument USE_CACHE = new IRIArgument(USE_CACHE_NAME, "false");

	public static final String TRIPLIFIER_NAME = "triplifier";
	public static final IRIArgument TRIPLIFIER = new IRIArgument(TRIPLIFIER_NAME);

	public static final String CHARSET_NAME = "charset";
	public static final IRIArgument CHARSET = new IRIArgument(CHARSET_NAME, "UTF-8");

	public static final String METADATA_NAME = "metadata";
	public static final IRIArgument METADATA = new IRIArgument(METADATA_NAME, "false");

	public static final String CONTENT_NAME = "content";
	public static final IRIArgument CONTENT = new IRIArgument(CONTENT_NAME);

	public static final String FROM_ARCHIVE_NAME = "from-archive";
	public static final IRIArgument FROM_ARCHIVE = new IRIArgument(FROM_ARCHIVE_NAME);

	public static final String ARCHIVE_FORMAT_NAME = "archive-format";
	public static final IRIArgument ARCHIVE_FORMAT = new IRIArgument(ARCHIVE_FORMAT_NAME);

	public static final String TRIM_STRINGS_NAME = "trim-strings";
	public static final IRIArgument TRIM_STRINGS = new IRIArgument(TRIM_STRINGS_NAME, "false");

	public static final String NULL_STRING_NAME = "null-string";
	public static final IRIArgument NULL_STRING = new IRIArgument(NULL_STRING_NAME);

	public static final String STRATEGY_NAME = "strategy";
	public static final IRIArgument STRATEGY = new IRIArgument(STRATEGY_NAME, "1");

	public static final String SLICE_NAME = "slice";
	public static final IRIArgument SLICE = new IRIArgument(SLICE_NAME, "false");

	public static final String SLICE_SIZE_NAME = "slice.size";
	public static final IRIArgument SLICE_SIZE = new IRIArgument(SLICE_SIZE_NAME, "1");

	public static final String COMMAND_NAME = "command";
	public static final IRIArgument COMMAND = new IRIArgument(COMMAND_NAME);

	public static final String USE_RDFS_MEMBER_NAME = "use-rdfs-member";
	public static final IRIArgument USE_RDFS_MEMBER = new IRIArgument(USE_RDFS_MEMBER_NAME, "false");

	public static final String ONDISK_REUSE_NAME = "ondisk.reuse";
	public static final IRIArgument ONDISK_REUSE = new IRIArgument(ONDISK_REUSE_NAME, "true");

	public static final String ONDISK_NAME = "ondisk";
	public static final IRIArgument ONDISK = new IRIArgument(ONDISK_NAME);

	public static final String OP_SERVICE_SILENT_NAME = "opservice.silent";
	public static final IRIArgument OP_SERVICE_SILENT = new IRIArgument(OP_SERVICE_SILENT_NAME);

	public static final String ANNOTATE_TRIPLES_WITH_SLOT_KEYS_NAME = "annotate-triples-with-slot-keys";
	public static final IRIArgument ANNOTATE_TRIPLES_WITH_SLOT_KEYS = new IRIArgument(ANNOTATE_TRIPLES_WITH_SLOT_KEYS_NAME, "false");

	public static final String GENERATE_PREDICATE_LABELS_NAME = "generate-predicate-labels";
	public static final IRIArgument GENERATE_PREDICATE_LABELS = new IRIArgument(GENERATE_PREDICATE_LABELS_NAME, "false");

	public static final String READ_FROM_STD_IN_NAME = "read-from-std-in";
	public static final IRIArgument READ_FROM_STD_IN = new IRIArgument(READ_FROM_STD_IN_NAME, "false");

	public static final String AUDIT_NAME = "audit";
	public static final IRIArgument AUDIT = new IRIArgument(AUDIT_NAME, "false");

	public static final String QUERY_NAME = "query";
	public static final IRIArgument QUERY = new IRIArgument(QUERY_NAME);

	// Options for S3 storage
	public static final String S3_ENDPOINT_NAME = "s3.endpoint";
	public static final IRIArgument S3_ENDPOINT = new IRIArgument(S3_ENDPOINT_NAME, "false");

	public static final String S3_BUCKET_NAME_NAME = "s3.bucket-name";
	public static final IRIArgument S3_BUCKET_NAME = new IRIArgument(S3_BUCKET_NAME_NAME);

	public static final String S3_KEY_NAME = "s3.key";
	public static final IRIArgument S3_KEY = new IRIArgument(S3_KEY_NAME);

	public static final String S3_ACCESS_KEY_NAME = "s3.access-key";
	public static final IRIArgument S3_ACCESS_KEY = new IRIArgument(S3_ACCESS_KEY_NAME);

	public static final String S3_SECRET_KEY_NAME = "s3.secret-key";
	public static final IRIArgument S3_SECRET_KEY = new IRIArgument(S3_SECRET_KEY_NAME);

	public static final String S3_REGION_NAME = "s3.region";
	public static final IRIArgument S3_REGION = new IRIArgument(S3_REGION_NAME);
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
