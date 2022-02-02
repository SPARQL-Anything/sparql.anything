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

package com.github.sparqlanything.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import com.github.sparqlanything.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVTriplifier implements Triplifier, Slicer {
	private static final Logger log = LoggerFactory.getLogger(CSVTriplifier.class);
	public final static String PROPERTY_FORMAT = "csv.format", PROPERTY_HEADERS = "csv.headers";
	public final static String PROPERTY_DELIMITER = "csv.delimiter";
	public final static String PROPERTY_NULLSTRING = "csv.null-string";

	public static CSVFormat buildFormat(Properties properties) throws IOException {
		CSVFormat format;
		try {
			format = CSVFormat.valueOf(properties.getProperty(PROPERTY_FORMAT, CSVFormat.Predefined.Default.name()));
		} catch (Exception e) {
			log.warn("Unsupported csv format: '{}', using default.", properties.getProperty(PROPERTY_FORMAT));
			format = CSVFormat.DEFAULT;
		}
		if(properties.containsKey(PROPERTY_NULLSTRING)){
			format = format.withNullString(properties.getProperty(PROPERTY_NULLSTRING)) ;
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

				int c = 0;
				while (headers_map.containsValue(colname)) {
					c++;
					colname += "_" + String.valueOf(c);
				}
				log.trace("adding colname >{}<", colname);
				headers_map.put(colid, colname);
			}
		}
		return headers_map;
	}
	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {

		URL url = Triplifier.getLocation(properties);
		log.debug("Location: {}", url);
		if (url == null)
			return;

		CSVFormat format = buildFormat(properties);
		String root = Triplifier.getRootArgument(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);

		String dataSourceId = Triplifier.getRootArgument(properties); // there is always 1 data source id

		// Add type Root
		builder.addRoot(dataSourceId, root);
		try {
			final InputStream is = Triplifier.getInputStream(url, properties);
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
				processRow(rown, dataSourceId, root, record, headers_map, builder);
			}
			log.debug("{} records", rown);
		} catch (IllegalArgumentException e) {
			log.error("{} :: {}", e.getMessage(), url);
			throw new IOException(e);
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("text/csv");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("csv");
	}

	public List<String> getDataSourceIds(Properties properties){
		String s = Triplifier.getRootArgument(properties);
		return Arrays.asList(s);
	}

	private void processRow(int rown, String dataSourceId, String rootId, CSVRecord record, LinkedHashMap<Integer, String> headers_map , FacadeXGraphBuilder builder){
		String rowContainerId = StringUtils.join(rootId , "#row" , rown);
		builder.addContainer(dataSourceId, rootId, rown, rowContainerId);
		Iterator<String> cells = record.iterator();
		int cellid = 0;
		while (cells.hasNext()) {
			String value = cells.next();
			log.trace(" > > row {} cell {} is <{}>", rown, cellid, value);
			cellid++;
			if (headers_map.containsKey(cellid)) {
				String colname = URLEncodedUtils.formatSegments(headers_map.get(cellid)).substring(1);
				if(value != null){
					builder.addValue(dataSourceId, rowContainerId, colname, value);
				}
			} else {
				if(value != null){
					builder.addValue(dataSourceId, rowContainerId, cellid, value);
				}
			}
		}
	}

	@Override
	public Iterable<Slice> slice(Properties properties) throws IOException, TriplifierHTTPException {

		URL url = Triplifier.getLocation(properties);
		log.debug("Location: {}", url);
		if (url == null)
			return Collections.emptySet();

		CSVFormat format = buildFormat(properties);
		String root = Triplifier.getRootArgument(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);

		boolean headers = hasHeaders(properties);
		String dataSourceId = Triplifier.getRootArgument(properties); // there is always 1 data source id
		String containerRowPrefix = url.toString() + "#row";

		final InputStream is = Triplifier.getInputStream(url, properties);
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
						return CSVSlice.makeSlice(recordIterator.next(), rown, dataSourceId, root, headers_map);
					}
				};
			}
		};
	}

	@Override
	public void triplify(Slice slice, Properties p, FacadeXGraphBuilder builder) {
		CSVSlice csvo = (CSVSlice) slice;
		builder.addRoot(csvo.getDatasourceId(), csvo.getRootId());
		processRow(csvo.iteration(), csvo.getDatasourceId(), csvo.getRootId(), csvo.get(), csvo.getHeaders(), builder);
	}
}
