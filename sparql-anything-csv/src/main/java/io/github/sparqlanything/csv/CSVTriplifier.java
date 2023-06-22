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
	private static final Logger log = LoggerFactory.getLogger(CSVTriplifier.class);
	public final static String PROPERTY_FORMAT = "csv.format", PROPERTY_HEADERS = "csv.headers";
	public final static String PROPERTY_DELIMITER = "csv.delimiter";
	public final static String PROPERTY_QUOTE_CHAR = "csv.quote-char";
	public final static String PROPERTY_NULL_STRING = "csv.null-string";

	public final static String IGNORE_COLUMNS_WITH_NO_HEADERS = "csv.ignore-columns-with-no-header";

	public static CSVFormat buildFormat(Properties properties) throws IOException {
		CSVFormat format;
		try {
			format = CSVFormat.valueOf(properties.getProperty(PROPERTY_FORMAT, CSVFormat.Predefined.Default.name()));
		} catch (Exception e) {
			log.warn("Unsupported csv format: '{}', using default.", properties.getProperty(PROPERTY_FORMAT));
			format = CSVFormat.DEFAULT;
		}
		if(properties.containsKey(PROPERTY_NULL_STRING)){
			format = format.withNullString(properties.getProperty(PROPERTY_NULL_STRING)) ;
		}
		if(properties.containsKey(PROPERTY_QUOTE_CHAR)){
			log.debug("Setting quote char to '{}'", properties.getProperty(PROPERTY_QUOTE_CHAR).charAt(0));
			format = format.withQuote(properties.getProperty(PROPERTY_QUOTE_CHAR).charAt(0)) ;
		}
		if(properties.containsKey(PROPERTY_DELIMITER)){
			log.debug("Setting delimiter to {}", properties.getProperty(PROPERTY_DELIMITER));
			if(properties.getProperty(PROPERTY_DELIMITER).length() != 1){
				throw new IOException("Bad value for property " + PROPERTY_DELIMITER + ": string length must be 1, " + Integer.toString(properties.getProperty(PROPERTY_DELIMITER).length()) + " given");
			}
			format = format.withDelimiter(properties.getProperty(PROPERTY_DELIMITER).charAt(0)) ;
		}
		return format;
	}

	public static boolean hasHeaders(Properties properties){
		boolean headers;
		try {
			headers = Boolean.valueOf(properties.getProperty(PROPERTY_HEADERS, "false"));
		} catch (Exception e) {
			log.warn("Unsupported value for csv.headers: '{}', using default (false).",
					properties.getProperty(PROPERTY_HEADERS));
			headers = false;
		}
		log.debug("Use headers: {}", headers);
		return headers;
	}

	public LinkedHashMap<Integer, String> makeHeadersMap(Iterator<CSVRecord> recordIterator , Properties properties){
		LinkedHashMap<Integer, String> headers_map = new LinkedHashMap<Integer, String>();
		if (hasHeaders(properties) && recordIterator.hasNext()) {
			log.trace(" > is headers");
			CSVRecord record = recordIterator.next();

			Iterator<String> columns = record.iterator();
			int colid = 0;
			while (columns.hasNext()) {
				colid++;
				String colstring = columns.next();
				String colname = colstring.strip();

				if(colname.length()==0){
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
	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {

		CSVFormat format = buildFormat(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID; // there is always 1 data source id
		boolean ignoreColumnsWithNoHeaders = PropertyUtils.getBooleanProperty(properties, IGNORE_COLUMNS_WITH_NO_HEADERS, false);

		// Add type Root
		builder.addRoot(dataSourceId);

		try (InputStream is = Triplifier.getInputStream(properties);){
			Reader in = new InputStreamReader(new BOMInputStream(is), charset);
			Iterable<CSVRecord> records = format.parse(in);
			Iterator<CSVRecord> recordIterator = records.iterator();
			LinkedHashMap<Integer, String> headers_map = makeHeadersMap(recordIterator, properties);

			log.debug("Iterating records");
			int rown = 0;
			log.trace(" > record {}", rown);
			// Data
			while (recordIterator.hasNext()) {
				log.trace(" > is data {}", rown);
				// Rows
				rown++;
				if((rown % 10000)==0){
					log.debug("current row num: {}", rown);
				}
				CSVRecord record = recordIterator.next();
				processRow(rown, dataSourceId, SPARQLAnythingConstants.ROOT_ID, record, headers_map, builder, ignoreColumnsWithNoHeaders);
			}
			log.debug("{} records", rown);
		} catch (IllegalArgumentException e) {
			log.error("{} :: {}", e.getMessage(), Triplifier.getResourceId(properties));
			throw new IOException(e);
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("text/csv", "text/tab-separated-values");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("csv", "tsv", "tab");
	}


	private void processRow(int rown, String dataSourceId, String rootId, CSVRecord record, LinkedHashMap<Integer, String> headers_map , FacadeXGraphBuilder builder, boolean ignoreColumnsWithNoHeaders){
		String rowContainerId = StringUtils.join(rootId , "#row" , rown);
		builder.addContainer(dataSourceId, rootId, rown, rowContainerId);
		Iterator<String> cells = record.iterator();
		int cellid = 0;
		while (cells.hasNext()) {
			String value = cells.next();
			log.trace(" > > row {} cell {} is <{}>", rown, cellid, value);
			cellid++;
			if (headers_map.containsKey(cellid)) {
//				String colname = URLEncodedUtils.formatSegments(headers_map.get(cellid)).substring(1);
				String colname = headers_map.get(cellid);
				log.trace("> > > colname >{}< (URL Encoded) >{}<",headers_map.get(cellid),colname);
				if(value != null){
					builder.addValue(dataSourceId, rowContainerId, colname, value);
				}
			} else {
				if(value != null && !ignoreColumnsWithNoHeaders){
					builder.addValue(dataSourceId, rowContainerId, cellid, value);
				}
			}
		}
	}

	@Override
	public Iterable<Slice> slice(Properties properties) throws IOException, TriplifierHTTPException {

		CSVFormat format = buildFormat(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);

		String dataSourceId = ""; // there is always 1 data source id

		// XXX How do we close the inputstream?
		final InputStream is = Triplifier.getInputStream(properties);

		Reader in = new InputStreamReader(new BOMInputStream(is), charset);

		Iterable<CSVRecord> records = format.parse(in);
		final Iterator<CSVRecord> recordIterator = records.iterator();
		final LinkedHashMap<Integer, String> headers_map = makeHeadersMap(recordIterator, properties);

		return new Iterable<Slice>() {
			@Override
			public Iterator<Slice> iterator() {
				log.debug("Iterating slices");
				return new Iterator<Slice>() {
					int rown = 0;

					@Override
					public boolean hasNext() {
						return recordIterator.hasNext();
					}

					@Override
					public Slice next() {
						rown++;
						log.trace("next slice: {}", rown);
						return CSVSlice.makeSlice(recordIterator.next(), rown, dataSourceId,  headers_map);
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
