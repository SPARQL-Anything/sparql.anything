package com.github.spiceh2020.sparql.anything.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class CSVTriplifier implements Triplifier {
	private static final Logger log = LoggerFactory.getLogger(CSVTriplifier.class);
	public final static String PROPERTY_FORMAT = "csv.format", PROPERTY_HEADERS = "csv.headers";

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {

		// TODO Support all flavour of csv types
		CSVFormat format;
		try{
			format = CSVFormat.valueOf(properties.getProperty(PROPERTY_FORMAT, "DEFAULT"));
		}catch(Exception e){
			log.warn("Unsupported csv format: '{}', using default.", properties.getProperty(PROPERTY_FORMAT));
			format = CSVFormat.DEFAULT;
		}
		String root = null;
		try{
			root = properties.getProperty(IRIArgument.ROOT.toString());
			if (root == null || root.trim().equals("")) {
				throw new Exception();
			}
		}catch(Exception e){
			log.warn("Unsupported parameter value for 'root', using default (no value).");
		}

		String namespace = null;
		try{
			root = properties.getProperty(IRIArgument.ROOT.toString());
			if (root == null || root.trim().equals("")) {
				throw new Exception();
			}
		}catch(Exception e){
			log.warn("Unsupported parameter value for 'root': '{}', using default (location}).", url);
			namespace = url.toString() + "#";
		}
		boolean headers;
		try{
			headers = Boolean.valueOf(properties.getProperty(PROPERTY_HEADERS, "false"));
		}catch(Exception e){
			log.warn("Unsupported value for csv.headers: '{}', using default (false).", properties.getProperty(PROPERTY_HEADERS));
			headers = false;
		}
		Reader in = null;
		try {
			in = new FileReader(new File(url.toURI()));
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		Model model = ModelFactory.createDefaultModel();

		Resource document;
		if(root !=null){
			document = model.createResource(root);
		}else{
			document = model.createResource();
		}
		Iterable<CSVRecord> records = format.parse(in);
		int rown = 0;
		LinkedHashMap<Integer,String> headers_map = new LinkedHashMap<Integer,String>();
		Iterator<CSVRecord> recordIterator = records.iterator();
		while (recordIterator.hasNext()) {
			// Header
			if(headers && rown == 0){
				CSVRecord record = recordIterator.next();
				Iterator<String> columns = record.iterator();
				int colid = 0;
				while(columns.hasNext()){
					colid++;
					String colstring = columns.next();
					String colname = colstring;
					int c = 0;
					while (headers_map.containsValue(colname)){
						c++;
						colname += "_" + String.valueOf(c);
					}
					headers_map.put(colid, colname);
				}

			}
			// Data
			if(recordIterator.hasNext()) {
				// Rows
				rown++;
				Resource row = model.createResource();
				document.addProperty(RDF.li(rown), row);
				CSVRecord record = recordIterator.next();
				Iterator<String> cells = record.iterator();
				int cellid = 0;
				while(cells.hasNext()){
					String value = cells.next();
					cellid ++;
					Property p;
					if(headers && headers_map.containsKey(cellid)) {
						p = model.createProperty(namespace,headers_map.get(cellid));
					}else{
						p = RDF.li(cellid);
					}
					row.addProperty(p, model.createLiteral(value));
				}
			}
		}
		DatasetGraph dg = DatasetFactory.create(model).asDatasetGraph();
		dg.addGraph(NodeFactory.createURI(url.toString()), model.getGraph());
		return dg;
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
