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

package io.github.sparqlanything.parquet;

import com.google.common.collect.Sets;
import dev.hardwood.InputFile;
import dev.hardwood.metadata.LogicalType;
import dev.hardwood.metadata.SchemaElement;
import dev.hardwood.reader.ParquetFileReader;
import dev.hardwood.reader.RowReader;
import dev.hardwood.row.PqInterval;
import dev.hardwood.row.PqList;
import dev.hardwood.row.PqMap;
import dev.hardwood.row.PqStruct;
import dev.hardwood.row.PqVariant;
import dev.hardwood.row.PqVariantArray;
import dev.hardwood.row.PqVariantObject;
import dev.hardwood.row.StructAccessor;
import dev.hardwood.schema.ColumnProjection;
import dev.hardwood.schema.SchemaNode;
import io.github.sparqlanything.model.*;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@io.github.sparqlanything.model.annotations.Triplifier
public class ParquetTriplifier implements Triplifier, Slicer<StructAccessor> {

	@Example(resource = "https://sparql-anything.cc/examples/simple.parquet", query = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> SELECT ?name ?value WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.parquet,parquet.typed-literals=false> { ?row xyz:name ?name ; xyz:value ?value } }", description = "Read every value of a Parquet file as a plain string, as the CSV triplifier does")
	@Option(description = "It tells the Parquet triplifier to produce typed literals (xsd:int, xsd:decimal, xsd:dateTime, ...) from the Parquet schema. When false, every value is a plain string.", validValues = "true/false")
	public final static IRIArgument PROPERTY_TYPED_LITERALS = new IRIArgument("parquet.typed-literals", "true");

	@Example(resource = "https://sparql-anything.cc/examples/simple.parquet", query = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> SELECT ?name WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.parquet,parquet.columns=name> { ?row xyz:name ?name } }", description = "Read only the column `name`")
	@Option(description = "A comma-separated list of the top-level columns to read. Other columns are not decoded at all. If not set, all columns are read.", validValues = "Comma-separated column names")
	public final static IRIArgument PROPERTY_COLUMNS = new IRIArgument("parquet.columns", null);

	@Example(resource = "https://sparql-anything.cc/examples/simple.parquet", query = "CONSTRUCT { ?s ?p ?o } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.parquet,parquet.headers.sanitize=true> { ?s ?p ?o } }", description = "Constructing a Facade-X RDF graph with sanitized column names")
	@Option(description = "It tells the Parquet triplifier to sanitize the column names used for minting the properties. Anything other than letters and numbers is replaced with underscores or the character provided.", validValues = "true/false or any String")
	public final static IRIArgument PROPERTY_HEADERS_SANITIZE = new IRIArgument("parquet.headers.sanitize", "false");

	private static final Logger log = LoggerFactory.getLogger(ParquetTriplifier.class);

	private static final String ROW_PREFIX = "#row";
	private static final String SCHEMA_CONTAINER = "#parquet-schema";
	private static final DateTimeFormatter TIME = DateTimeFormatter.ISO_LOCAL_TIME;
	private static final DateTimeFormatter LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		Settings settings = new Settings(properties);
		builder.addRoot(dataSourceId);

		try (ParquetInput input = ParquetInput.open(properties); ParquetFileReader reader = ParquetFileReader.open(input.file())) {
			List<Column> columns = columns(reader, properties);
			if (PropertyUtils.getBooleanProperty(properties, IRIArgument.METADATA)) {
				addSchemaToMetadataGraph(reader, properties, builder);
			}
			try (RowReader rows = rowReader(reader, properties)) {
				int rown = 0;
				while (rows.hasNext()) {
					rows.next();
					rown++;
					if ((rown % 10000) == 0) {
						log.debug("current row num: {}", rown);
					}
					addRow(rown, dataSourceId, rows, columns, settings, builder);
				}
				log.debug("{} rows", rown);
			}
		} catch (UncheckedIOException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public CloseableIterable<Slice<StructAccessor>> slice(Properties properties) throws IOException, TriplifierHTTPException {
		final String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		final ParquetInput input = ParquetInput.open(properties);
		final ParquetFileReader reader;
		final RowReader rows;
		final List<Column> columns;
		try {
			reader = ParquetFileReader.open(input.file());
			columns = columns(reader, properties);
			rows = rowReader(reader, properties);
		} catch (IOException | RuntimeException e) {
			input.close();
			throw e;
		}

		return new CloseableIterable<>() {

			private boolean closed = false;

			@Override
			public void close() throws IOException {
				if (closed) {
					return;
				}
				closed = true;
				try {
					rows.close();
					reader.close();
				} finally {
					input.close();
				}
			}

			@Override
			public Iterator<Slice<StructAccessor>> iterator() {
				return new Iterator<>() {
					int rown = 0;

					@Override
					public boolean hasNext() {
						boolean hasNext = rows.hasNext();
						if (!hasNext) {
							try {
								close();
							} catch (IOException e) {
								throw new UncheckedIOException(e);
							}
						}
						return hasNext;
					}

					@Override
					public Slice<StructAccessor> next() {
						rows.next();
						rown++;
						// The slice is a view over the row reader: it is triplified before the next row is read
						return ParquetSlice.makeSlice(rows, rown, dataSourceId, columns);
					}
				};
			}
		};
	}

	@Override
	public void triplify(Slice<StructAccessor> slice, Properties p, FacadeXGraphBuilder builder) {
		ParquetSlice parquetSlice = (ParquetSlice) slice;
		builder.addRoot(parquetSlice.getDatasourceId());
		addRow(parquetSlice.iteration(), parquetSlice.getDatasourceId(), parquetSlice.get(), parquetSlice.getColumns(), new Settings(p), builder);
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/vnd.apache.parquet", "application/x-parquet");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("parquet", "pq");
	}

	/**
	 * A top-level column: its name in the file, the slot key used in the graph and its schema.
	 */
	record Column(String name, String slotKey, SchemaNode schema) {
	}

	private record Settings(boolean typed) {
		Settings(Properties properties) {
			this(PropertyUtils.getBooleanProperty(properties, PROPERTY_TYPED_LITERALS));
		}
	}

	private static RowReader rowReader(ParquetFileReader reader, Properties properties) {
		List<String> projected = projectedColumns(properties);
		if (projected == null) {
			return reader.rowReader();
		}
		return reader.buildRowReader().projection(ColumnProjection.columns(projected.toArray(new String[0]))).build();
	}

	private static List<String> projectedColumns(Properties properties) {
		String columns = PropertyUtils.getStringProperty(properties, PROPERTY_COLUMNS);
		if (columns == null || columns.isBlank()) {
			return null;
		}
		List<String> result = new ArrayList<>();
		for (String c : columns.split(",")) {
			if (!c.isBlank()) {
				result.add(c.strip());
			}
		}
		return result;
	}

	static List<Column> columns(ParquetFileReader reader, Properties properties) throws IOException {
		List<String> projected = projectedColumns(properties);
		List<SchemaNode> fields = reader.getFileSchema().getRootNode().children();
		if (projected != null) {
			Set<String> known = new HashSet<>();
			fields.forEach(f -> known.add(f.name()));
			for (String c : projected) {
				if (!known.contains(c)) {
					throw new IOException("Unknown column in " + PROPERTY_COLUMNS + ": " + c);
				}
			}
		}
		String sanitize = PropertyUtils.getStringProperty(properties, PROPERTY_HEADERS_SANITIZE);
		Set<String> slotKeys = new HashSet<>();
		List<Column> columns = new ArrayList<>();
		for (SchemaNode field : fields) {
			if (projected != null && !projected.contains(field.name())) {
				continue;
			}
			String slotKey = sanitize(field.name(), sanitize);
			// Sanitizing can make two names equal: keep slot keys unique, as the CSV triplifier does
			String unique = slotKey;
			int c = 0;
			while (!slotKeys.add(unique)) {
				c++;
				unique = slotKey + "_" + c;
			}
			columns.add(new Column(field.name(), unique, field));
		}
		return columns;
	}

	static String sanitize(String name, String sanitizeOption) {
		if (sanitizeOption == null || sanitizeOption.equalsIgnoreCase("false")) {
			return name;
		}
		String replacement = sanitizeOption.equalsIgnoreCase("true") ? "_" : sanitizeOption;
		String quoted = java.util.regex.Pattern.quote(replacement);
		return name.strip().replaceAll("[^\\p{L}\\p{N}]+", java.util.regex.Matcher.quoteReplacement(replacement))
				.replaceAll("^(" + quoted + ")+|(" + quoted + ")+$", "");
	}

	private static void addRow(int rown, String dataSourceId, StructAccessor row, List<Column> columns, Settings settings, FacadeXGraphBuilder builder) {
		String rowContainerId = StringUtils.join(SPARQLAnythingConstants.ROOT_ID, ROW_PREFIX, rown);
		builder.addContainer(dataSourceId, SPARQLAnythingConstants.ROOT_ID, rown, rowContainerId);
		for (Column column : columns) {
			if (row.isNull(column.name())) {
				continue;
			}
			Object value = row.getValue(column.name());
			addValue(dataSourceId, rowContainerId, new Slot(column.slotKey()), value, column.schema(), settings, builder);
		}
	}

	/**
	 * Either a named slot (a column or struct field) or a numbered one (a list item).
	 */
	private record Slot(String key, int index) {
		Slot(String key) {
			this(key, 0);
		}

		Slot(int index) {
			this(null, index);
		}

		String childId(String containerId) {
			return key != null ? StringUtils.join(containerId, "/", Triplifier.toSafeURIString(key)) : StringUtils.join(containerId, "/_", String.valueOf(index));
		}
	}

	private static void addValue(String dataSourceId, String containerId, Slot slot, Object value, SchemaNode schema, Settings settings, FacadeXGraphBuilder builder) {
		if (value == null) {
			return;
		}
		if (value instanceof PqStruct struct) {
			String childId = addContainer(dataSourceId, containerId, slot, builder);
			List<SchemaNode> fields = schema instanceof SchemaNode.GroupNode g ? g.children() : List.of();
			for (int i = 0; i < struct.getFieldCount(); i++) {
				if (!struct.isNull(i)) {
					addValue(dataSourceId, childId, new Slot(struct.getFieldName(i)), struct.getValue(i), i < fields.size() ? fields.get(i) : null, settings, builder);
				}
			}
		} else if (value instanceof PqList list) {
			String childId = addContainer(dataSourceId, containerId, slot, builder);
			SchemaNode element = schema instanceof SchemaNode.GroupNode g && g.isList() ? g.getListElement() : null;
			for (int i = 0; i < list.size(); i++) {
				// A null item leaves a gap in the numbering, so positions are kept
				if (!list.isNull(i)) {
					addValue(dataSourceId, childId, new Slot(i + 1), list.get(i), element, settings, builder);
				}
			}
		} else if (value instanceof PqMap map) {
			addMap(dataSourceId, containerId, slot, map, schema, settings, builder);
		} else if (value instanceof PqVariant variant) {
			addVariant(dataSourceId, containerId, slot, variant, settings, builder);
		} else {
			Literal literal = literal(value, schema);
			addLiteral(dataSourceId, containerId, slot, literal, settings, builder);
		}
	}

	private static void addMap(String dataSourceId, String containerId, Slot slot, PqMap map, SchemaNode schema, Settings settings, FacadeXGraphBuilder builder) {
		String childId = addContainer(dataSourceId, containerId, slot, builder);
		SchemaNode keySchema = null;
		SchemaNode valueSchema = null;
		if (schema instanceof SchemaNode.GroupNode g && g.isMap()) {
			keySchema = g.getMapKey();
			valueSchema = g.getMapValue();
		}
		boolean stringKeys = keySchema instanceof SchemaNode.PrimitiveNode p && p.logicalType() instanceof LogicalType.StringType;
		int i = 0;
		for (PqMap.Entry entry : map.getEntries()) {
			i++;
			Object entryValue = entry.isValueNull() ? null : entry.getValue();
			if (stringKeys) {
				// String keys become slot names, as for JSON objects
				addValue(dataSourceId, childId, new Slot(entry.getStringKey()), entryValue, valueSchema, settings, builder);
			} else {
				// Other keys keep their type: one entry container per pair
				String entryId = addContainer(dataSourceId, childId, new Slot(i), builder);
				addValue(dataSourceId, entryId, new Slot("key"), entry.getKey(), keySchema, settings, builder);
				addValue(dataSourceId, entryId, new Slot("value"), entryValue, valueSchema, settings, builder);
			}
		}
	}

	private static void addVariant(String dataSourceId, String containerId, Slot slot, PqVariant variant, Settings settings, FacadeXGraphBuilder builder) {
		switch (variant.type()) {
			case NULL -> {
			}
			case OBJECT -> {
				String childId = addContainer(dataSourceId, containerId, slot, builder);
				PqVariantObject object = variant.asObject();
				for (int i = 0; i < object.getFieldCount(); i++) {
					String name = object.getFieldName(i);
					if (!object.isNull(name)) {
						addVariant(dataSourceId, childId, new Slot(name), object.getVariant(name), settings, builder);
					}
				}
			}
			case ARRAY -> {
				String childId = addContainer(dataSourceId, containerId, slot, builder);
				PqVariantArray array = variant.asArray();
				for (int i = 0; i < array.size(); i++) {
					addVariant(dataSourceId, childId, new Slot(i + 1), array.get(i), settings, builder);
				}
			}
			case BOOLEAN_TRUE, BOOLEAN_FALSE -> addLiteral(dataSourceId, containerId, slot, literal(variant.asBoolean(), null), settings, builder);
			case INT8, INT16, INT32 -> addLiteral(dataSourceId, containerId, slot, literal(variant.asInt(), null), settings, builder);
			case INT64 -> addLiteral(dataSourceId, containerId, slot, literal(variant.asLong(), null), settings, builder);
			case FLOAT -> addLiteral(dataSourceId, containerId, slot, literal(variant.asFloat(), null), settings, builder);
			case DOUBLE -> addLiteral(dataSourceId, containerId, slot, literal(variant.asDouble(), null), settings, builder);
			case DECIMAL4, DECIMAL8, DECIMAL16 -> addLiteral(dataSourceId, containerId, slot, literal(variant.asDecimal(), null), settings, builder);
			case DATE -> addLiteral(dataSourceId, containerId, slot, literal(variant.asDate(), null), settings, builder);
			case TIME_NTZ -> addLiteral(dataSourceId, containerId, slot, literal(variant.asTime(), null), settings, builder);
			case TIMESTAMP, TIMESTAMP_NANOS -> addLiteral(dataSourceId, containerId, slot, literal(variant.asTimestamp(), null), settings, builder);
			case TIMESTAMP_NTZ, TIMESTAMP_NTZ_NANOS -> addLiteral(dataSourceId, containerId, slot, literal(variant.asLocalTimestamp(), null), settings, builder);
			case BINARY -> addLiteral(dataSourceId, containerId, slot, literal(variant.asBinary(), null), settings, builder);
			case UUID -> addLiteral(dataSourceId, containerId, slot, literal(variant.asUuid(), null), settings, builder);
			case STRING -> addLiteral(dataSourceId, containerId, slot, literal(variant.asString(), null), settings, builder);
		}
	}

	private static String addContainer(String dataSourceId, String containerId, Slot slot, FacadeXGraphBuilder builder) {
		String childId = slot.childId(containerId);
		if (slot.key() != null) {
			builder.addContainer(dataSourceId, containerId, slot.key(), childId);
		} else {
			builder.addContainer(dataSourceId, containerId, slot.index(), childId);
		}
		return childId;
	}

	private static void addLiteral(String dataSourceId, String containerId, Slot slot, Literal literal, Settings settings, FacadeXGraphBuilder builder) {
		if (!settings.typed() || literal.datatype() == null) {
			if (slot.key() != null) {
				builder.addValue(dataSourceId, containerId, slot.key(), literal.lexicalForm());
			} else {
				builder.addValue(dataSourceId, containerId, slot.index(), literal.lexicalForm());
			}
		} else if (slot.key() != null) {
			builder.addValue(dataSourceId, containerId, slot.key(), literal.lexicalForm(), literal.datatype());
		} else {
			builder.addValue(dataSourceId, containerId, slot.index(), literal.lexicalForm(), literal.datatype());
		}
	}

	/**
	 * A lexical form and its XSD datatype; a null datatype means a plain string.
	 */
	record Literal(String lexicalForm, XSDDatatype datatype) {
	}

	static Literal literal(Object value, SchemaNode schema) {
		LogicalType logicalType = schema instanceof SchemaNode.PrimitiveNode p ? p.logicalType() : null;
		if (value instanceof String s) {
			return new Literal(s, null);
		} else if (value instanceof Boolean b) {
			return new Literal(b.toString(), XSDDatatype.XSDboolean);
		} else if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
			return integer(((Number) value).longValue(), value, logicalType);
		} else if (value instanceof Float f) {
			return new Literal(floatingPoint(f.doubleValue(), f.toString()), XSDDatatype.XSDfloat);
		} else if (value instanceof Double d) {
			return new Literal(floatingPoint(d, d.toString()), XSDDatatype.XSDdouble);
		} else if (value instanceof BigDecimal d) {
			// toPlainString keeps the scale: DECIMAL(10,2) 12.50 stays "12.50"
			return new Literal(d.toPlainString(), XSDDatatype.XSDdecimal);
		} else if (value instanceof LocalDate d) {
			return new Literal(d.toString(), XSDDatatype.XSDdate);
		} else if (value instanceof LocalTime t) {
			boolean utc = logicalType instanceof LogicalType.TimeType tt && tt.isAdjustedToUTC();
			return new Literal(TIME.format(t) + (utc ? "Z" : ""), XSDDatatype.XSDtime);
		} else if (value instanceof Instant i) {
			return new Literal(DateTimeFormatter.ISO_INSTANT.format(i), XSDDatatype.XSDdateTime);
		} else if (value instanceof LocalDateTime t) {
			return new Literal(LOCAL_DATE_TIME.format(t), XSDDatatype.XSDdateTime);
		} else if (value instanceof byte[] bytes) {
			return new Literal(Base64.getEncoder().encodeToString(bytes), XSDDatatype.XSDbase64Binary);
		} else if (value instanceof UUID u) {
			return new Literal(u.toString(), null);
		} else if (value instanceof PqInterval interval) {
			return new Literal(duration(interval), XSDDatatype.XSDduration);
		}
		return new Literal(value.toString(), null);
	}

	private static Literal integer(long v, Object value, LogicalType logicalType) {
		if (logicalType instanceof LogicalType.IntType it) {
			if (!it.isSigned()) {
				// Unsigned values arrive in a signed Java type of the same width
				String lexical = switch (it.bitWidth()) {
					case 8 -> String.valueOf(v & 0xFFL);
					case 16 -> String.valueOf(v & 0xFFFFL);
					case 32 -> String.valueOf(v & 0xFFFFFFFFL);
					default -> Long.toUnsignedString(v);
				};
				return new Literal(lexical, switch (it.bitWidth()) {
					case 8 -> XSDDatatype.XSDunsignedByte;
					case 16 -> XSDDatatype.XSDunsignedShort;
					case 32 -> XSDDatatype.XSDunsignedInt;
					default -> XSDDatatype.XSDunsignedLong;
				});
			}
			return new Literal(String.valueOf(v), switch (it.bitWidth()) {
				case 8 -> XSDDatatype.XSDbyte;
				case 16 -> XSDDatatype.XSDshort;
				case 32 -> XSDDatatype.XSDint;
				default -> XSDDatatype.XSDlong;
			});
		}
		if (value instanceof Byte) {
			return new Literal(String.valueOf(v), XSDDatatype.XSDbyte);
		} else if (value instanceof Short) {
			return new Literal(String.valueOf(v), XSDDatatype.XSDshort);
		} else if (value instanceof Integer) {
			return new Literal(String.valueOf(v), XSDDatatype.XSDint);
		}
		return new Literal(String.valueOf(v), XSDDatatype.XSDlong);
	}

	private static String floatingPoint(double d, String javaForm) {
		if (Double.isNaN(d)) {
			return "NaN";
		} else if (Double.isInfinite(d)) {
			return d > 0 ? "INF" : "-INF";
		}
		return javaForm;
	}

	static String duration(PqInterval interval) {
		long millis = interval.milliseconds();
		StringBuilder sb = new StringBuilder("P");
		sb.append(interval.months()).append('M').append(interval.days()).append("DT");
		sb.append(BigDecimal.valueOf(millis, 3).stripTrailingZeros().toPlainString()).append('S');
		return sb.toString();
	}

	/**
	 * Writes the Parquet schema and file metadata to the metadata named graph, so that the data graph plus this
	 * graph carry what is needed to rebuild a file with the same schema and values.
	 */
	private static void addSchemaToMetadataGraph(ParquetFileReader reader, Properties properties, FacadeXGraphBuilder builder) {
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		String root = SPARQLAnythingConstants.ROOT_ID;
		// Build with the same options (blank nodes, namespace, ...) as the data graph, then copy into the metadata graph
		BaseFacadeXGraphBuilder metadata = new BaseFacadeXGraphBuilder(properties);
		dev.hardwood.metadata.FileMetaData fileMetaData = reader.getFileMetaData();

		metadata.addValue(dataSourceId, root, "parquet-version", String.valueOf(fileMetaData.version()), XSDDatatype.XSDint);
		metadata.addValue(dataSourceId, root, "num-rows", String.valueOf(fileMetaData.numRows()), XSDDatatype.XSDlong);
		if (fileMetaData.createdBy() != null) {
			metadata.addValue(dataSourceId, root, "created-by", fileMetaData.createdBy());
		}
		Map<String, String> keyValues = fileMetaData.keyValueMetadata();
		if (keyValues != null && !keyValues.isEmpty()) {
			String kvId = root + "#parquet-key-value-metadata";
			metadata.addContainer(dataSourceId, root, "key-value-metadata", kvId);
			int i = 0;
			for (Map.Entry<String, String> kv : keyValues.entrySet()) {
				i++;
				String entryId = kvId + "/_" + i;
				metadata.addContainer(dataSourceId, kvId, i, entryId);
				metadata.addValue(dataSourceId, entryId, "key", kv.getKey());
				if (kv.getValue() != null) {
					metadata.addValue(dataSourceId, entryId, "value", kv.getValue());
				}
			}
		}

		// The schema is stored in the file as a flattened depth-first list: rebuild the tree as containers
		List<SchemaElement> elements = fileMetaData.schema();
		metadata.addContainer(dataSourceId, root, "schema", SCHEMA_CONTAINER);
		addSchemaElement(elements, new int[]{0}, SCHEMA_CONTAINER, dataSourceId, metadata);

		Node metadataGraph = NodeFactory.createURI(Triplifier.METADATA_GRAPH_IRI);
		Graph g = metadata.getDatasetGraph().getGraph(metadata.dataSourceId2node(dataSourceId));
		g.find().forEachRemaining(t -> builder.add(metadataGraph, t.getSubject(), t.getPredicate(), t.getObject()));
	}

	private static void addSchemaElement(List<SchemaElement> elements, int[] position, String containerId, String dataSourceId, BaseFacadeXGraphBuilder builder) {
		SchemaElement element = elements.get(position[0]++);
		builder.addValue(dataSourceId, containerId, "name", element.name());
		if (element.repetitionType() != null) {
			builder.addValue(dataSourceId, containerId, "repetition", element.repetitionType().name());
		}
		if (element.type() != null) {
			builder.addValue(dataSourceId, containerId, "physical-type", element.type().name());
		}
		if (element.typeLength() != null) {
			builder.addValue(dataSourceId, containerId, "type-length", String.valueOf(element.typeLength()), XSDDatatype.XSDint);
		}
		if (element.convertedType() != null) {
			builder.addValue(dataSourceId, containerId, "converted-type", element.convertedType().name());
		}
		if (element.fieldId() != null) {
			builder.addValue(dataSourceId, containerId, "field-id", String.valueOf(element.fieldId()), XSDDatatype.XSDint);
		}
		if (element.logicalType() != null) {
			addLogicalType(element.logicalType(), containerId, dataSourceId, builder);
		} else {
			// Legacy files only carry the converted type's decimal parameters
			if (element.precision() != null) {
				builder.addValue(dataSourceId, containerId, "precision", String.valueOf(element.precision()), XSDDatatype.XSDint);
			}
			if (element.scale() != null) {
				builder.addValue(dataSourceId, containerId, "scale", String.valueOf(element.scale()), XSDDatatype.XSDint);
			}
		}
		int children = element.numChildren() == null ? 0 : element.numChildren();
		for (int i = 1; i <= children; i++) {
			String childId = containerId + "/_" + i;
			builder.addContainer(dataSourceId, containerId, i, childId);
			addSchemaElement(elements, position, childId, dataSourceId, builder);
		}
	}

	private static void addLogicalType(LogicalType logicalType, String containerId, String dataSourceId, BaseFacadeXGraphBuilder builder) {
		// Names as in the Parquet format specification; the NULL logical type is called UNKNOWN there
		String name = logicalType instanceof LogicalType.NullType ? "UNKNOWN" : logicalType.getClass().getSimpleName().replaceFirst("Type$", "").toUpperCase(Locale.ROOT);
		builder.addValue(dataSourceId, containerId, "logical-type", name);
		if (logicalType instanceof LogicalType.DecimalType d) {
			builder.addValue(dataSourceId, containerId, "precision", String.valueOf(d.precision()), XSDDatatype.XSDint);
			builder.addValue(dataSourceId, containerId, "scale", String.valueOf(d.scale()), XSDDatatype.XSDint);
		} else if (logicalType instanceof LogicalType.IntType i) {
			builder.addValue(dataSourceId, containerId, "bit-width", String.valueOf(i.bitWidth()), XSDDatatype.XSDint);
			builder.addValue(dataSourceId, containerId, "signed", String.valueOf(i.isSigned()), XSDDatatype.XSDboolean);
		} else if (logicalType instanceof LogicalType.TimeType t) {
			builder.addValue(dataSourceId, containerId, "unit", t.unit().name());
			builder.addValue(dataSourceId, containerId, "adjusted-to-utc", String.valueOf(t.isAdjustedToUTC()), XSDDatatype.XSDboolean);
		} else if (logicalType instanceof LogicalType.TimestampType t) {
			builder.addValue(dataSourceId, containerId, "unit", t.unit().name());
			builder.addValue(dataSourceId, containerId, "adjusted-to-utc", String.valueOf(t.isAdjustedToUTC()), XSDDatatype.XSDboolean);
		} else if (logicalType instanceof LogicalType.VariantType v) {
			builder.addValue(dataSourceId, containerId, "spec-version", String.valueOf(v.specVersion()), XSDDatatype.XSDint);
		} else if (logicalType instanceof LogicalType.GeometryType g) {
			if (g.crs() != null) builder.addValue(dataSourceId, containerId, "crs", g.crs());
		} else if (logicalType instanceof LogicalType.GeographyType g) {
			if (g.crs() != null) builder.addValue(dataSourceId, containerId, "crs", g.crs());
			if (g.edgeInterpolation() != null) builder.addValue(dataSourceId, containerId, "edge-interpolation", g.edgeInterpolation().name());
		}
	}

	/**
	 * Parquet needs random access (the schema is in a footer at the end of the file). Local files are read in
	 * place; any other source is copied to a temporary file that is deleted on close.
	 */
	static final class ParquetInput implements AutoCloseable {
		private final InputFile file;
		private final Path temporary;

		private ParquetInput(InputFile file, Path temporary) {
			this.file = file;
			this.temporary = temporary;
		}

		InputFile file() {
			return file;
		}

		static ParquetInput open(Properties properties) throws IOException, TriplifierHTTPException {
			Path local = localPath(properties);
			if (local != null) {
				return new ParquetInput(InputFile.of(local), null);
			}
			Path temporary = Files.createTempFile("sparql-anything-", ".parquet");
			try (InputStream is = Triplifier.getInputStream(properties)) {
				Files.copy(is, temporary, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException | TriplifierHTTPException | RuntimeException e) {
				Files.deleteIfExists(temporary);
				throw e;
			}
			return new ParquetInput(InputFile.of(temporary), temporary);
		}

		private static Path localPath(Properties properties) {
			// Only a plain location: content, command and S3 go through the resource services
			if (properties.containsKey(IRIArgument.CONTENT.toString()) || properties.containsKey(IRIArgument.COMMAND.toString()) || properties.containsKey(IRIArgument.S3_ENDPOINT.toString())) {
				return null;
			}
			String location = properties.getProperty(IRIArgument.LOCATION.toString());
			if (location == null) {
				return null;
			}
			try {
				URL url = Triplifier.getLocation(properties);
				if (url != null && url.getProtocol().equals("file")) {
					return Paths.get(url.toURI());
				}
			} catch (IOException | URISyntaxException | IllegalArgumentException e) {
				log.debug("Location {} is not a local file: {}", location, e.getMessage());
			}
			return null;
		}

		@Override
		public void close() throws IOException {
			if (temporary != null) {
				Files.deleteIfExists(temporary);
			}
		}
	}
}
