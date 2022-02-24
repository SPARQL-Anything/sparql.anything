/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.sparqlanything.spreadsheet;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Node;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpreadsheetTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(SpreadsheetTriplifier.class);

	public final static String PROPERTY_HEADERS = "spreadsheet.headers";

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);
		if(url == null){
			logger.warn("No location provided");
			return;
		}
		String root = Triplifier.getRootArgument(properties);
//		Charset charset = getCharsetArgument(properties);
		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);
		String namespace = Triplifier.getNamespaceArgument(properties);

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
			String dataSourceId = root + Triplifier.toSafeURIString(s.getSheetName());
			String sheetRoot = dataSourceId;
//			Graph g = toGraph(s, uriGraph, namespace, blank_nodes, headers.get());
			populate(s, dataSourceId, sheetRoot, builder, headers.get());
//			dg.addGraph(NodeFactory.createURI(uriGraph), g);
		});

//		dg.setDefaultGraph(dg.getUnionGraph());
	}

//	private Graph toGraph(Sheet s, String root, String namespace, boolean blank_nodes, boolean headers) {
	private void populate(Sheet s, String dataSourceId, String root, FacadeXGraphBuilder builder, boolean headers) {

		// Add type Root
//		g.add(new Triple(document, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));
		builder.addRoot(dataSourceId, root);

		int rown = 0; // this counts the LI index not the spreadsheet rows
		LinkedHashMap<Integer, String> headers_map = new LinkedHashMap<Integer, String>();

		for (int rowNum = s.getFirstRowNum(); rowNum <= s.getLastRowNum(); rowNum++) {
			// Header
			if (headers && rowNum == 0) {
				Row row = s.getRow(rowNum);
				int colid = 0;
				for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
					colid++;
					Cell cell = row.getCell(cellNum);
					String colstring = cellToString(cell);
					String colname = colstring.strip();
					if("".equals(colname)){
						colname = Integer.toString(colid);
					}
					int c = 0;
					while (headers_map.containsValue(colname)) {
						c++;
						colname += "_" + String.valueOf(c);
					}

					log.trace("adding colname >{}<", colname);
					headers_map.put(colid, colname);
				}

			}else{
				// Data
				// Rows
				rown++;
				String row = root + "_Row_" + rown;
//				if (blank_nodes) {
//					row = NodeFactory.createBlankNode();
//				} else {
//					row = NodeFactory.createURI(root + "_Row_" + rown);
//				}
				builder.addContainer(dataSourceId, root, rown, row);
//				g.add(new Triple(document, RDF.li(rown).asNode(), row));
				Row record = s.getRow(rowNum);
				Iterator<Cell> cellIterator = record.cellIterator();
				int colid = 0;
				for (int cellNum = record.getFirstCellNum(); cellNum < record.getLastCellNum(); cellNum++) {
					Cell cell = record.getCell(cellNum);
					String value = cellToString(cell);
					colid++;
					Node property;
					if (headers && headers_map.containsKey(colid)) {
						builder.addValue(dataSourceId, row, Triplifier.toSafeURIString(headers_map.get(colid)), value );
//						property = NodeFactory
//								.createURI(namespace + Triplifier.toSafeURIString(headers_map.get(colid)));
					} else {
						builder.addValue(dataSourceId, row, colid, value );
					}

//					g.add(new Triple(row, property, NodeFactory.createLiteral(value)));
				}
			}
		}

//		return g;
	}

	private String cellToString(Cell cell) {
		if(cell == null){
			return "";
		}
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
		return Sets.newHashSet("application/vnd.ms-excel",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("xls", "xlsx");
	}

}
