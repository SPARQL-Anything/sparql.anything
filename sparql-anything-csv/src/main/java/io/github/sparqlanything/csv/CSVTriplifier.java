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

package io.github.sparqlanything.csv;

import io.github.sparqlanything.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
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

public class CSVTriplifier implements Triplifier, Slicer {
	public final static IRIArgument PROPERTY_HEADERS = new IRIArgument("csv.headers", "false");
	public final static IRIArgument PROPERTY_HEADER_ROW = new IRIArgument("csv.headers-row", "1");
//	public final static String PROPERTY_FORMAT = "csv.format";

	public final static IRIArgument PROPERTY_FORMAT = new IRIArgument("csv.format", CSVFormat.Predefined.Default.name());
	public final static String PROPERTY_DELIMITER = "csv.delimiter";
	public final static String PROPERTY_QUOTE_CHAR = "csv.quote-char";
	public final static String PROPERTY_NULL_STRING = "csv.null-string";
	public final static String IGNORE_COLUMNS_WITH_NO_HEADERS = "csv.ignore-columns-with-no-header";
	private static final Logger log = LoggerFactory.getLogger(CSVTriplifier.class);

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {

		CSVFormat format = buildFormat(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);
		int headersRowNumber= PropertyUtils.getIntegerProperty(properties, PROPERTY_HEADER_ROW);
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID; // there is always 1 data source id
		boolean ignoreColumnsWithNoHeaders = PropertyUtils.getBooleanProperty(properties, IGNORE_COLUMNS_WITH_NO_HEADERS, false);

		// Add type Root
		builder.addRoot(dataSourceId);

		try (InputStream is = Triplifier.getInputStream(properties); Reader in = new InputStreamReader(new BOMInputStream(is), charset)) {

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
				if(rown == headersRowNumber && !headers_map.isEmpty()){
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

		if (properties.containsKey(PROPERTY_NULL_STRING)) {
			format = format.withNullString(properties.getProperty(PROPERTY_NULL_STRING));
		}
		if (properties.containsKey(PROPERTY_QUOTE_CHAR)) {
			log.debug("Setting quote char to '{}'", properties.getProperty(PROPERTY_QUOTE_CHAR).charAt(0));
			format = format.withQuote(properties.getProperty(PROPERTY_QUOTE_CHAR).charAt(0));
		}
		if (properties.containsKey(PROPERTY_DELIMITER)) {
			log.debug("Setting delimiter to {}", properties.getProperty(PROPERTY_DELIMITER));
			if (properties.getProperty(PROPERTY_DELIMITER).length() != 1) {
				throw new IOException("Bad value for property " + PROPERTY_DELIMITER + ": string length must be 1, " + properties.getProperty(PROPERTY_DELIMITER).length() + " given");
			}
			format = format.withDelimiter(properties.getProperty(PROPERTY_DELIMITER).charAt(0));
		}
		return format;
	}

	public LinkedHashMap<Integer, String> makeHeadersMapFromOpenIterator(Iterator<CSVRecord> recordIterator, Properties properties, CSVFormat format, Charset charset) throws TriplifierHTTPException, IOException {
		int headersRow = PropertyUtils.getIntegerProperty(properties, PROPERTY_HEADER_ROW);
		Iterator<CSVRecord> iterator = recordIterator;
		if (headersRow > 0) {
			Reader in = new InputStreamReader(new BOMInputStream(Triplifier.getInputStream(properties)), charset);
			Iterable<CSVRecord> records = format.parse(in);
			iterator = records.iterator();
			LinkedHashMap<Integer, String> headers_map = makeHeadersMapFromOpenIterator(properties, headersRow, iterator);
			in.close();
			return headers_map;
		}
		return makeHeadersMapFromOpenIterator(properties, headersRow, iterator);
	}

	private static LinkedHashMap<Integer, String> makeHeadersMapFromOpenIterator(Properties properties, int headersRow, Iterator<CSVRecord> iterator) {
		int rowNumber = 1;
		LinkedHashMap<Integer, String> headers_map = new LinkedHashMap<Integer, String>();
		if (hasHeaders(properties) && iterator.hasNext()) {
			while(rowNumber!= headersRow && iterator.hasNext()){
				rowNumber ++;
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
	public Iterable<Slice> slice(Properties properties) throws IOException, TriplifierHTTPException {

		CSVFormat format = buildFormat(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID; // there is always 1 data source id

		// XXX How do we close the inputstream?
		final InputStream is = Triplifier.getInputStream(properties);

		Reader in = new InputStreamReader(new BOMInputStream(is), charset);

		Iterable<CSVRecord> records = format.parse(in);
		final Iterator<CSVRecord> recordIterator = records.iterator();
		final LinkedHashMap<Integer, String> headers_map = makeHeadersMapFromOpenIterator(recordIterator, properties, format, charset);

		return new Iterable<Slice>() {
			@Override
			public Iterator<Slice> iterator() {
				log.debug("Iterating slices");
				return new Iterator<Slice>() {
					int rown = 0;
					int headersRowNumber = PropertyUtils.getIntegerProperty(properties, PROPERTY_HEADER_ROW);

					@Override
					public boolean hasNext() {
						boolean hasNext = recordIterator.hasNext();
						if(!hasNext){
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
					public Slice next() {
						rown++;
						if(rown == headersRowNumber && !headers_map.isEmpty()){
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
	public void triplify(Slice slice, Properties p, FacadeXGraphBuilder builder) {
		CSVSlice csvo = (CSVSlice) slice;
		boolean ignoreColumnsWithNoHeaders = PropertyUtils.getBooleanProperty(p, IGNORE_COLUMNS_WITH_NO_HEADERS, false);
		builder.addRoot(csvo.getDatasourceId());
		processRow(csvo.iteration(), csvo.getDatasourceId(), builder.getRootURI(csvo.getDatasourceId()), csvo.get(), csvo.getHeaders(), builder, ignoreColumnsWithNoHeaders);
	}
}
