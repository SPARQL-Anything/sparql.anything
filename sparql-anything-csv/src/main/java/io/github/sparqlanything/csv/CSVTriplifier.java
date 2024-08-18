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

package io.github.sparqlanything.csv;

import com.google.common.collect.Sets;
import io.github.sparqlanything.model.*;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

@io.github.sparqlanything.model.annotations.Triplifier
public class CSVTriplifier implements Triplifier, Slicer<CSVRecord> {

	@Example(resource = "https://sparql-anything.cc/examples/simple.tsv", query = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT (AVG(xsd:float(?petalLength)) AS ?avgPetalLength) WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.headers=true,csv.format=TDF> { ?s xyz:Sepal_length ?length ; xyz:Petal_length ?petalLength FILTER ( xsd:float(?length) > 4.9 ) } }", description = "Compute the average petal length of the species having sepal length greater than 4.9")
	@Option(description = "It tells the CSV triplifier to use the headers of the CSV file for minting the properties of the generated triples.", validValues = "true/false")
	public final static IRIArgument PROPERTY_HEADERS = new IRIArgument("csv.headers", "false");

	@Example(resource = "https://sparql-anything.cc/examples/simple.tsv", query = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT (AVG(xsd:float(?petalLength)) AS ?avgPetalLength) WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.headers=true,csv.format=TDF,csv.headers-row=3> { ?s xyz:Sepal_length ?length ; xyz:Petal_length ?petalLength FILTER ( xsd:float(?length) > 4.9 ) } }", description = "Compute the average petal length of the species having sepal length greater than 4.9")
	@Option(description = "It specifies the number of the row to use for extracting column headers. Note this option affects the performance as it requires to pass through input twice. -- see #179", validValues = "Any integer")
	public final static IRIArgument PROPERTY_HEADER_ROW = new IRIArgument("csv.headers-row", "1");

	@Example(resource = "https://sparql-anything.cc/examples/simple.tsv", query = "CONSTRUCT { ?s ?p ?o . } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.format=TDF> { ?s ?p ?o } } ", description = "Constructing a Facade-X RDF graph out of the TSV file available at https://sparql-anything.cc/examples/simple.tsv")
	@Option(description = "The format of the input CSV file.", validValues = "Any predefined [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) of the Apache's commons CSV library.")
	public final static IRIArgument PROPERTY_FORMAT = new IRIArgument("csv.format", CSVFormat.Predefined.Default.name());

	@Example(resource = "https://sparql-anything.cc/examples/simple.tsv", description = "Compute the maximum petal length of the species having sepal length less than 4.9", query = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (MAX(xsd:float(?petalLength)) AS ?maxPetalLength) WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.headers=true> { fx:properties fx:csv.delimiter \"\\t\" . ?s xyz:Sepal_length ?length ; xyz:Petal_length ?petalLength FILTER ( xsd:float(?length) < 4.9 ) } }")
	@Option(description = "It sets the column delimiter, usually ,;\\t etc.", validValues = "Any single character")
	public final static IRIArgument PROPERTY_DELIMITER = new IRIArgument("csv.delimiter", ",");

	@Example(resource = "https://sparql-anything.cc/examples/csv_with_commas.csv", description = "Constructing a Facade-X RDF graph out of the CSV available at https://sparql-anything.cc/examples/csv_with_commas.csv", query = "CONSTRUCT { ?s ?p ?o . } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/csv_with_commas.csv,csv.headers=true,csv.quote-char='> { ?s ?p ?o } }")
	@Option(description = "It sets the quoting character", validValues = "Any single character")
	public final static IRIArgument PROPERTY_QUOTE_CHAR = new IRIArgument("csv.quote-char", "\"");

	@Example(resource = "https://sparql-anything.cc/examples/simple_with_null.csv", description = "Retrieving name surname of who doesn't have an email address.", query = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?name ?surname WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple_with_null.csv,csv.headers=true> { fx:properties fx:csv.null-string \"\" . ?c xyz:name ?name ; xyz:surname ?surname FILTER NOT EXISTS { ?c xyz:email ?email } } }")
	@Option(description = "It tells the CSV triplifier to not produce triples where the specified string would be in the object position of the triple", validValues = "Any String")
	public final static IRIArgument PROPERTY_NULL_STRING = new IRIArgument("csv.null-string", null);

	@Example( query = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> SELECT DISTINCT ?fred ?sally WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:csv.headers true . fx:properties fx:content \",state\\nfred,CO\\nsally,FL\" . fx:properties fx:media-type \"text/csv\" . fx:properties fx:csv.ignore-columns-with-no-header true . ?root a fx:root ; rdf:_1 [rdf:_1 ?fred] ; rdf:_2 [rdf:_1 ?sally] . } }")
	@Option(description = "It tells the csv triplifier to ignore from the cells of columns having no headers. Note that if the property is set as true when csv.headers is false, the triplifier does not generate any slot (as no headers are collected). -- see #180", validValues = "true/false")
	public final static IRIArgument IGNORE_COLUMNS_WITH_NO_HEADERS = new IRIArgument("csv.ignore-columns-with-no-header", "false");
	private static final Logger log = LoggerFactory.getLogger(CSVTriplifier.class);

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {

		CSVFormat format = buildFormat(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);
		int headersRowNumber = PropertyUtils.getIntegerProperty(properties, PROPERTY_HEADER_ROW);
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID; // there is always 1 data source id
		boolean ignoreColumnsWithNoHeaders = PropertyUtils.getBooleanProperty(properties, IGNORE_COLUMNS_WITH_NO_HEADERS);

		// Add type Root
		builder.addRoot(dataSourceId);

		try (InputStream is = Triplifier.getInputStream(properties); Reader in = new InputStreamReader(BOMInputStream.builder().setInputStream(is).get(), charset)) {

			Iterable<CSVRecord> records = format.parse(in);
			Iterator<CSVRecord> recordIterator = records.iterator();
			LinkedHashMap<Integer, String> headers_map = makeHeadersMapFromOpenIterator(recordIterator, properties, format, charset);

			log.debug("Iterating records");
			int rown = 0;
			log.trace(" > record {}", rown);
			// Data
			while (recordIterator.hasNext()) {
				log.trace(" > is data {}", rown);
				// Rows
				rown++;
				if ((rown % 10000) == 0) {
					log.debug("current row num: {}", rown);
				}
				CSVRecord record = recordIterator.next();
				if (rown == headersRowNumber && !headers_map.isEmpty()) {
					// skip headers row
					rown--;
					headersRowNumber = -1; // this avoids that the condition is verified in the next iterations
					continue;
				}
				processRow(rown, dataSourceId, SPARQLAnythingConstants.ROOT_ID, record, headers_map, builder, ignoreColumnsWithNoHeaders);
			}
			log.debug("{} records", rown);
		} catch (IllegalArgumentException e) {
			log.error("{} :: {}", e.getMessage(), Triplifier.getResourceId(properties));
			throw new IOException(e);
		}
	}

	public static CSVFormat buildFormat(Properties properties) throws IOException {
		CSVFormat format = CSVFormat.valueOf(PropertyUtils.getStringProperty(properties, PROPERTY_FORMAT));

		String nullString = PropertyUtils.getStringProperty(properties, PROPERTY_NULL_STRING);
		if (nullString != null) format = format.withNullString(nullString);

		String quoteChar = PropertyUtils.getStringProperty(properties, PROPERTY_QUOTE_CHAR);
		ensureLength1(quoteChar, PROPERTY_QUOTE_CHAR);
		format = format.withQuote(quoteChar.charAt(0));

		String delimiter = PropertyUtils.getStringProperty(properties, PROPERTY_DELIMITER);
		ensureLength1(delimiter, PROPERTY_DELIMITER);
		format = format.withDelimiter(delimiter.charAt(0));

		return format;
	}

	public LinkedHashMap<Integer, String> makeHeadersMapFromOpenIterator(Iterator<CSVRecord> recordIterator, Properties properties, CSVFormat format, Charset charset) throws TriplifierHTTPException, IOException {
		int headersRow = PropertyUtils.getIntegerProperty(properties, PROPERTY_HEADER_ROW);
		Iterator<CSVRecord> iterator = recordIterator;
		if (headersRow > 0) {
			Reader in = new InputStreamReader(BOMInputStream.builder().setInputStream(Triplifier.getInputStream(properties)).get(), charset);
			Iterable<CSVRecord> records = format.parse(in);
			iterator = records.iterator();
			LinkedHashMap<Integer, String> headers_map = makeHeadersMapFromOpenIterator(properties, headersRow, iterator);
			in.close();
			return headers_map;
		}
		return makeHeadersMapFromOpenIterator(properties, headersRow, iterator);
	}

	private void processRow(int rown, String dataSourceId, String rootId, CSVRecord record, LinkedHashMap<Integer, String> headers_map, FacadeXGraphBuilder builder, boolean ignoreColumnsWithNoHeaders) {
		String rowContainerId = StringUtils.join(rootId, "#row", rown);
		builder.addContainer(dataSourceId, rootId, rown, rowContainerId);
		Iterator<String> cells = record.iterator();
		int cellid = 0;
		while (cells.hasNext()) {
			String value = cells.next();
			log.trace(" > > row {} cell {} is <{}>", rown, cellid, value);
			cellid++;
			if (headers_map.containsKey(cellid)) {
				String colname = headers_map.get(cellid);
				log.trace("> > > colname >{}< (URL Encoded) >{}<", headers_map.get(cellid), colname);
				if (value != null) {
					builder.addValue(dataSourceId, rowContainerId, colname, value);
				}
			} else {
				if (value != null && !ignoreColumnsWithNoHeaders) {
					builder.addValue(dataSourceId, rowContainerId, cellid, value);
				}
			}
		}
	}

	private static void ensureLength1(String value, IRIArgument property) throws IOException {
		if (value.length() != 1) {
			throw new IOException("Bad value for property " + property.toString() + ": string length must be 1, " + value.length() + " given");
		}
	}

	private static LinkedHashMap<Integer, String> makeHeadersMapFromOpenIterator(Properties properties, int headersRow, Iterator<CSVRecord> iterator) {
		int rowNumber = 1;
		LinkedHashMap<Integer, String> headers_map = new LinkedHashMap<>();
		if (hasHeaders(properties) && iterator.hasNext()) {
			while (rowNumber != headersRow && iterator.hasNext()) {
				rowNumber++;
				iterator.next();
			}
			CSVRecord record = iterator.next();

			log.trace(" > is headers");

			Iterator<String> columns = record.iterator();
			int colid = 0;
			while (columns.hasNext()) {
				colid++;
				String colstring = columns.next();
				String colname = colstring.strip();

				if (colname.length() == 0) {
					continue;
				}

				int c = 0;
				while (headers_map.containsValue(colname)) {
					c++;
					colname += "_".concat(String.valueOf(c));
				}
				log.trace("adding colname >{}<", colname);
				headers_map.put(colid, colname);
			}
		}
		return headers_map;
	}

	public static boolean hasHeaders(Properties properties) {
		return PropertyUtils.getBooleanProperty(properties, PROPERTY_HEADERS);
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("text/csv", "text/tab-separated-values");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("csv", "tsv", "tab");
	}

	@Override
	public  CloseableIterable<Slice<CSVRecord>> slice(Properties properties) throws IOException, TriplifierHTTPException {

		CSVFormat format = buildFormat(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID; // there is always 1 data source id

		// XXX How do we close the inputstream?
		final InputStream is = Triplifier.getInputStream(properties);

		Reader in = new InputStreamReader(BOMInputStream.builder().setInputStream(is).get(), charset);

		Iterable<CSVRecord> records = format.parse(in);
		final Iterator<CSVRecord> recordIterator = records.iterator();
		final LinkedHashMap<Integer, String> headers_map = makeHeadersMapFromOpenIterator(recordIterator, properties, format, charset);

		return new CloseableIterable<>() {

			@Override
			public void close() {
				// The InputStream is closed by hasNext method of the iterator
			}

			@Override
			public Iterator<Slice<CSVRecord>> iterator() {
				log.debug("Iterating slices");
				return new Iterator<>() {
					int rown = 0;
					int headersRowNumber = PropertyUtils.getIntegerProperty(properties, PROPERTY_HEADER_ROW);

					@Override
					public boolean hasNext() {
						boolean hasNext = recordIterator.hasNext();
						if (!hasNext) {
							try {
								in.close();
								is.close();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						return hasNext;
					}

					@Override
					public Slice<CSVRecord> next() {
						rown++;
						if (rown == headersRowNumber && !headers_map.isEmpty()) {
							// skip headers row
							rown--;
							headersRowNumber = -1; // this avoids that the condition is verified in the next iterations
							recordIterator.next();
						}
						log.trace("next slice: {}", rown);
						return CSVSlice.makeSlice(recordIterator.next(), rown, dataSourceId, headers_map);
					}
				};
			}
		};
	}

	@Override
	public void triplify(Slice<CSVRecord> slice, Properties p, FacadeXGraphBuilder builder) {
		CSVSlice csvo = (CSVSlice) slice;
		boolean ignoreColumnsWithNoHeaders = PropertyUtils.getBooleanProperty(p, IGNORE_COLUMNS_WITH_NO_HEADERS, false);
		builder.addRoot(csvo.getDatasourceId());
		processRow(csvo.iteration(), csvo.getDatasourceId(), builder.getRootURI(csvo.getDatasourceId()), csvo.get(), csvo.getHeaders(), builder, ignoreColumnsWithNoHeaders);
	}
}
