package com.github.spiceh2020.sparql.anything.spreadsheet;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class SpreadsheetTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(SpreadsheetTriplifier.class);

	public final static String PROPERTY_HEADERS = "spreadsheet.headers";

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();
		String root = getRootArgument(properties, url);
//		Charset charset = getCharsetArgument(properties);
		boolean blank_nodes = getBlankNodeArgument(properties);
		String namespace = getNamespaceArgument(properties, url);

		AtomicBoolean headers = new AtomicBoolean();
		try {
			headers.set(Boolean.valueOf(properties.getProperty(PROPERTY_HEADERS, "false")));
		} catch (Exception e) {
			log.warn("Unsupported value for csv.headers: '{}', using default (false).",
					properties.getProperty(PROPERTY_HEADERS));
			headers.set(false);
		}

		Workbook wb = WorkbookFactory.create(url.openStream());

		wb.sheetIterator().forEachRemaining(s -> {
			String uriGraph = root + uriEscaper(s.getSheetName());
			Graph g = toGraph(s, uriGraph, namespace, blank_nodes, headers.get());
			dg.addGraph(NodeFactory.createURI(uriGraph), g);
		});

		dg.setDefaultGraph(dg.getUnionGraph());

		return dg;
	}

	private Graph toGraph(Sheet s, String root, String namespace, boolean blank_nodes, boolean headers) {
		Graph g = GraphFactory.createGraphMem();

		Node document;

		if (blank_nodes) {
			document = NodeFactory.createBlankNode();
		} else {
			document = NodeFactory.createURI(root + "_root");
		}

		// Add type Root
		g.add(new Triple(document, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

		int rown = 0;
		LinkedHashMap<Integer, String> headers_map = new LinkedHashMap<Integer, String>();

		Iterator<Row> rowIterator = s.rowIterator();
		while (rowIterator.hasNext()) {
			// Header
			if (headers && rown == 0) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int colid = 0;
				while (cellIterator.hasNext()) {
					colid++;
					Cell cell = (Cell) cellIterator.next();

					String colstring = cellToString(cell);
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
			if (rowIterator.hasNext()) {
				// Rows
				rown++;
				Node row;
				if (blank_nodes) {
					row = NodeFactory.createBlankNode();
				} else {
					row = NodeFactory.createURI(root + "_Row_" + rown);
				}
				g.add(new Triple(document, RDF.li(rown).asNode(), row));
				Row record = rowIterator.next();
				Iterator<Cell> cellIterator = record.cellIterator();
				int colid = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String value = cellToString(cell);
					colid++;

					Node property;
					if (headers && headers_map.containsKey(colid)) {
						property = NodeFactory.createURI(namespace + uriEscaper(headers_map.get(colid)));
					} else {
						property = RDF.li(colid).asNode();
					}

					g.add(new Triple(row, property, NodeFactory.createLiteral(value)));

				}
			}
		}

		return g;
	}

	private String cellToString(Cell cell) {

		if (cell.getCellType() == CellType.BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == CellType.FORMULA) {
			return cell.getCellFormula();
		} else if (cell.getCellType() == CellType.NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		} else {
			return cell.getStringCellValue();
		}

	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("xls", "xlsx");
	}

}
