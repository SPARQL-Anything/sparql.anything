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

package io.github.sparqlanything.spreadsheet;

import io.github.sparqlanything.model.*;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@io.github.sparqlanything.model.annotations.Triplifier
public class SpreadsheetTriplifier implements Triplifier {

	@Example(resource = "https://sparql-anything.cc/examples/Book1.xlsx", query = "CONSTRUCT { GRAPH ?g { ?s ?p ?o .} } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book1.xlsx,spreadsheet.headers=true> { GRAPH ?g { ?s ?p ?o } } }", description = "Construct the dataset by using the headers of the columns to mint the property URIs.")
	@Option(description = "It tells the spreadsheet triplifier to use the headers of the spreadsheet file for minting the properties of the generated triples.", validValues = "true/false")
	public final static IRIArgument PROPERTY_HEADERS = new IRIArgument("spreadsheet.headers", "false");

	@Example(resource = "https://sparql-anything.cc/examples/Book2.xlsx", query = "CONSTRUCT { GRAPH ?g { ?s ?p ?o .} } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book2.xlsx,spreadsheet.evaluate-formulas=true> { GRAPH ?g { ?s ?p ?o } } }", description = "Construct the dataset by evaluating the formulas.")
	@Option(description = "It tells the spreadsheet triplifier to evaluate formulas of the spreadsheet.", validValues = "true/false")
	public final static IRIArgument PROPERTY_EVALUATE_FORMULAS = new IRIArgument("spreadsheet.evaluate-formulas", "false");

	@Example(resource = "https://sparql-anything.cc/examples/Book3.xlsx", query = "CONSTRUCT { GRAPH ?g { ?s ?p ?o .} } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book3.xlsx,spreadsheet.composite-values=true> { GRAPH ?g { ?s ?p ?o } } } ", description = "Construct the dataset by extracting hyperlinks and comments from the cells.")
	@Option(description = "It tells the spreadsheet triplifier to extract from the cells hyperlinks and comments. If enabled, the cells will be triplified as containers instead of literals (see #308)", validValues = "true/false")
	public final static IRIArgument PROPERTY_COMPOSITE_VALUES = new IRIArgument("spreadsheet.composite-values", "false");

	@Example(resource = "https://sparql-anything.cc/examples/Book1.xlsx", description = "Construct the dataset by using the headers of the columns to mint the property URIs.", query = "CONSTRUCT { GRAPH ?g { ?s ?p ?o .} } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book1.xlsx,spreadsheet.headers=true,spreadsheet.headers-row=2> { GRAPH ?g { ?s ?p ?o } } }")
	@Option(description = "It specifies the number of the row to use for extracting column headers. -- see #179", validValues = "Any integer")
	public final static IRIArgument PROPERTY_HEADER_ROW = new IRIArgument("spreadsheet.headers-row", "1");

	@Example(resource = "https://sparql-anything.cc/examples/spreadsheet.xls", query = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?fred ?sally WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/spreadsheet.xls> { fx:properties fx:spreadsheet.headers true . fx:properties fx:spreadsheet.ignore-columns-with-no-header true . GRAPH <https://sparql-anything.cc/examples/spreadsheet.xls#Sheet1> { ?root a fx:root ; rdf:_1 [rdf:_1 ?fred] ; rdf:_2 [rdf:_1 ?sally] . } } } ", description = "Construct the dataset by using the headers of the columns to mint the property URIs.")
	@Option(description = "It tells the spreadsheet triplifier to ignore from the cells of columns having no headers. Note that if the property is set as true when spreadsheet.headers is false, the triplifier does not generate any slot (as no headers are collected). -- see #180", validValues = "Any integer")
	public final static IRIArgument IGNORE_COLUMNS_WITH_NO_HEADERS = new IRIArgument("spreadsheet.ignore-columns-with-no-header", "false");
	private static final Logger logger = LoggerFactory.getLogger(SpreadsheetTriplifier.class);
	private FormulaEvaluator evaluator;


	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);
		if (url == null) {
			logger.warn("No location provided");
			return;
		}
		boolean evaluateFormulas = PropertyUtils.getBooleanProperty(properties, PROPERTY_EVALUATE_FORMULAS);
		boolean compositeValues = PropertyUtils.getBooleanProperty(properties, PROPERTY_COMPOSITE_VALUES);
		final boolean headers = PropertyUtils.getBooleanProperty(properties, PROPERTY_HEADERS);
		final boolean ignoreColumnsWithNoHeaders = PropertyUtils.getBooleanProperty(properties, IGNORE_COLUMNS_WITH_NO_HEADERS);
		final int headersRow = PropertyUtils.getIntegerProperty(properties, PROPERTY_HEADER_ROW);

		Workbook wb = WorkbookFactory.create(url.openStream());
		this.evaluator = wb.getCreationHelper().createFormulaEvaluator();

		wb.sheetIterator().forEachRemaining(s -> {
			String dataSourceId = Triplifier.toSafeURIString(s.getSheetName());
			populate(s, dataSourceId, builder, headers, evaluateFormulas, compositeValues, ignoreColumnsWithNoHeaders, headersRow);
		});

		wb.close();

	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("xls", "xlsx");
	}

	private Map<Integer, String> makeHeaders(Sheet s, boolean headers, int headersRow, boolean evaluateFormulas) {
		Map<Integer, String> headers_map = new HashMap<>();
		if (headers) {
			Row row = s.getRow(headersRow - 1);
			int columnId = 0;
			for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
				columnId++;
				Cell cell = row.getCell(cellNum);
				Object value = extractCellValue(cell, evaluateFormulas);
				String columnString = value.toString();

				String columnName = columnString.strip();
				if ("".equals(columnName)) {
					continue;
				}
				int c = 0;
				while (headers_map.containsValue(columnName)) {
					c++;
					columnName += "_" + c;
				}

				log.trace("adding column name >{}< (column id {})", columnName, columnId);
				headers_map.put(columnId, columnName);
			}

		}
		return headers_map;
	}

	private void populate(Sheet s, String dataSourceId, FacadeXGraphBuilder builder, boolean headers, boolean evaluateFormulas, boolean compositeValues, boolean ignoreColumnsWithNoHeaders, int headersRow) {

		// Add type Root
		builder.addRoot(dataSourceId);

		int rowNumber = 0; // this counts the LI index not the spreadsheet rows
		Map<Integer, String> headers_map = makeHeaders(s, headers, headersRow, evaluateFormulas);

		for (int rowNum = s.getFirstRowNum(); rowNum <= s.getLastRowNum(); rowNum++) {

			// skip headers row
			if (headers && rowNum == headersRow - 1) continue;

			// Rows
			rowNumber++;
			String row = "_Row_".concat(String.valueOf(rowNumber));
			builder.addContainer(dataSourceId, SPARQLAnythingConstants.ROOT_ID, rowNumber, row);
			Row record = s.getRow(rowNum);
			logger.trace("Reading Row {} from sheet {}", rowNum, s.getSheetName());

			if (record != null) {
				int columnId = 0;
				for (int cellNum = record.getFirstCellNum(); cellNum < record.getLastCellNum(); cellNum++) {
					Cell cell = record.getCell(cellNum);
					columnId++;
					if (compositeValues) {
						String value = row.concat("_").concat(String.valueOf(cellNum));
						extractCompositeCellValue(dataSourceId, value, cell, evaluateFormulas, builder);
						if (headers && headers_map.containsKey(columnId)) {
							builder.addContainer(dataSourceId, row, Triplifier.toSafeURIString(headers_map.get(columnId)), value);
						} else if (!ignoreColumnsWithNoHeaders) {
							builder.addValue(dataSourceId, row, columnId, value);
						}
					} else {
						Object value = extractCellValue(cell, evaluateFormulas);
						if (headers && headers_map.containsKey(columnId)) {
							builder.addValue(dataSourceId, row, Triplifier.toSafeURIString(headers_map.get(columnId)), value);
						} else if (!ignoreColumnsWithNoHeaders) {
							builder.addValue(dataSourceId, row, columnId, value);
						}
					}
				}

			}

		}
	}

	private Object extractCellValue(Cell cell, boolean evaluateFormulas) {
		if (cell == null) return "";
		switch (cell.getCellType()) {
			case BOOLEAN:
				return cell.getBooleanCellValue();
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				return cell.getNumericCellValue();
			case FORMULA:
				if (evaluateFormulas) {
					Cell evaluatedCell = evaluator.evaluateInCell(cell);
					return extractCellValue(evaluatedCell, true);
				} else {
					return cell.getCellFormula();
				}
			case BLANK:
			case ERROR:
			case _NONE:
		}
		return "";
	}

	private void extractCompositeCellValue(String dataSourceId, String containerId, Cell cell, boolean evaluateFormulas, FacadeXGraphBuilder builder) {
		if (cell == null) return;
		builder.addType(dataSourceId, containerId, cell.getCellType().toString());
		switch (cell.getCellType()) {
			case BOOLEAN:
				builder.addValue(dataSourceId, containerId, 1, cell.getBooleanCellValue());
				break;
			case STRING:
				builder.addValue(dataSourceId, containerId, 1, cell.getStringCellValue());
				break;
			case NUMERIC:
				builder.addValue(dataSourceId, containerId, 1, cell.getNumericCellValue());
				break;
			case FORMULA:
				if (evaluateFormulas) {
					Cell evaluatedCell = evaluator.evaluateInCell(cell);
					builder.addValue(dataSourceId, containerId, 1, extractCellValue(evaluatedCell, true));
				} else {
					builder.addValue(dataSourceId, containerId, 1, cell.getCellFormula());
				}
				break;
			case BLANK:
			case ERROR:
			case _NONE:
			default:
				break;
		}
		if (cell.getHyperlink() != null) {
			if (cell.getHyperlink().getAddress() != null) {
				builder.addValue(dataSourceId, containerId, "address", cell.getHyperlink().getAddress());
			}
			if (cell.getHyperlink().getLabel() != null) {
				builder.addValue(dataSourceId, containerId, "label", cell.getHyperlink().getLabel());
			} else {
				builder.addValue(dataSourceId, containerId, "label", cell.getStringCellValue());
			}
		}

		if (cell.getCellComment() != null) {
			Comment comment = cell.getCellComment();
			if (comment.getAuthor() != null) {
				builder.addValue(dataSourceId, containerId, "author", comment.getAuthor());
			}
			if (comment.getString() != null) {
				RichTextString commentRichTextString = comment.getString();
				commentRichTextString.clearFormatting();
				builder.addValue(dataSourceId, containerId, "threadedComment", commentRichTextString.getString());
			}
		}


	}

}
