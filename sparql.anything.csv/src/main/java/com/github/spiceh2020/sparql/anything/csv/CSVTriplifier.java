package com.github.spiceh2020.sparql.anything.csv;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import com.github.spiceh2020.sparql.anything.model.TripleFilteringFacadeXBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class CSVTriplifier implements Triplifier {
	private static final Logger log = LoggerFactory.getLogger(CSVTriplifier.class);
	public final static String PROPERTY_FORMAT = "csv.format", PROPERTY_HEADERS = "csv.headers";

	@Deprecated
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		return triplify(url,  properties, null);
	}

	@Override
	public DatasetGraph triplify(URL url, Properties properties, Op op) throws IOException {
		log.info("CSV Triplifier: {}", op);
		// TODO Support all flavour of csv types
		CSVFormat format;
		try{
			format = CSVFormat.valueOf(properties.getProperty(PROPERTY_FORMAT, "DEFAULT"));
		}catch(Exception e){
			log.warn("Unsupported csv format: '{}', using default.", properties.getProperty(PROPERTY_FORMAT));
			format = CSVFormat.DEFAULT;
		}
		String root = Triplifier.getRootArgument(properties, url);
		Charset charset = Triplifier.getCharsetArgument(properties);
		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);
		String namespace = Triplifier.getNamespaceArgument(properties);

		boolean headers;
		try{
			headers = Boolean.valueOf(properties.getProperty(PROPERTY_HEADERS, "false"));
		}catch(Exception e){
			log.warn("Unsupported value for csv.headers: '{}', using default (false).", properties.getProperty(PROPERTY_HEADERS));
			headers = false;
		}
		Reader in = null;
		final InputStream is = url.openStream();
		try {
			in = new InputStreamReader(new BOMInputStream(is), charset);
		} catch (IllegalArgumentException e) {
			log.error("{} :: {}", e.getMessage(), url);
			throw new IOException(e);
		}
		FacadeXGraphBuilder builder = new TripleFilteringFacadeXBuilder(url, op, properties );
		String dataSourceId = url.toString();
		String rootId = root;
		if(rootId == null){
			rootId = url.toString() + "#root";
		}
		String containerRowPrefix = url.toString() + "#row";
		// Add type Root
		builder.addRoot(dataSourceId, rootId);
		try {
			Iterable<CSVRecord> records = format.parse(in);
			int rown = 0;
			LinkedHashMap<Integer, String> headers_map = new LinkedHashMap<Integer, String>();
			Iterator<CSVRecord> recordIterator = records.iterator();
			while (recordIterator.hasNext()) {
				// Header
				if (headers && rown == 0) {
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
				// Data
				if (recordIterator.hasNext()) {
					// Rows
					rown++;
					String rowContainerId = containerRowPrefix + rown;
					builder.addContainer(dataSourceId, rootId, rown, rowContainerId);
					CSVRecord record = recordIterator.next();
					Iterator<String> cells = record.iterator();
					int cellid = 0;
					while (cells.hasNext()) {
						String value = cells.next();
						cellid++;
						if (headers && headers_map.containsKey(cellid)) {
							builder.addValue(dataSourceId, rowContainerId, headers_map.get(cellid), value);
						} else {
							builder.addValue(dataSourceId, rowContainerId, cellid, value);
						}
					}
				}
			}
		} finally{
			is.close();
		}
		return builder.getDatasetGraph();
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("text/csv");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("csv");
	}
}
