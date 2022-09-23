/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.spreadsheet;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.PropertyUtils;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpreadsheetTriplifier implements Triplifier {

	public final static String PROPERTY_HEADERS = "spreadsheet.headers";
	public final static String PROPERTY_EVALUATE_FORMULAS = "spreadsheet.evaluate-formulas";
	public final static String PROPERTY_COMPOSITE_VALUES = "spreadsheet.composite-values";
	private static final Logger logger = LoggerFactory.getLogger(SpreadsheetTriplifier.class);
	private FormulaEvaluator evaluator;


	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);
		if (url == null) {
			logger.warn("No location provided");
			return;
		}
		String root = Triplifier.getRootArgument(properties);
		boolean evaluateFormulas = PropertyUtils.getBooleanProperty(properties, PROPERTY_EVALUATE_FORMULAS, false);
		boolean compositeValues = PropertyUtils.getBooleanProperty(properties, PROPERTY_COMPOSITE_VALUES, false);
		String namespace = Triplifier.getNamespaceArgument(properties);
		AtomicBoolean headers = new AtomicBoolean();
		try {
			headers.set(Boolean.valueOf(properties.getProperty(PROPERTY_HEADERS, "false")));
		} catch (Exception e) {
			log.warn("Unsupported value for csv.headers: '{}', using default (false).", properties.getProperty(PROPERTY_HEADERS));
			headers.set(false);
		}

		Workbook wb = WorkbookFactory.create(url.openStream());
		this.evaluator = wb.getCreationHelper().createFormulaEvaluator();

		wb.sheetIterator().forEachRemaining(s -> {
			String dataSourceId = root + Triplifier.toSafeURIString(s.getSheetName());
			String sheetRoot = dataSourceId;
			populate(s, dataSourceId, sheetRoot, builder, headers.get(), evaluateFormulas, compositeValues, namespace);
		});

	}

	private void populate(Sheet s, String dataSourceId, String root, FacadeXGraphBuilder builder, boolean headers, boolean evaluateFormulas, boolean compositeValues, String namespace) {

		// Add type Root
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
					Object value = extractCellValue(cell, evaluateFormulas);
					String colstring = value.toString();

					String colname = colstring.strip();
					if ("".equals(colname)) {
						colname = Integer.toString(colid);
					}
					int c = 0;
					while (headers_map.containsValue(colname)) {
						c++;
						colname += "_" + c;
					}

					log.trace("adding colname >{}<", colname);
					headers_map.put(colid, colname);
				}

			} else {
				// Rows
				rown++;
				String row = root + "_Row_" + rown;
				builder.addContainer(dataSourceId, root, rown, row);
				Row record = s.getRow(rowNum);
				logger.trace("Reading Row {} from sheet {}", rowNum, s.getSheetName());

				if (record != null) {
					int colid = 0;
					for (int cellNum = record.getFirstCellNum(); cellNum < record.getLastCellNum(); cellNum++) {
						Cell cell = record.getCell(cellNum);
						if (compositeValues) {
							String value = row + "_" + cellNum;
							extractCompositeCellValue(dataSourceId, value, cell, evaluateFormulas, builder, namespace);
							colid++;
							if (headers && headers_map.containsKey(colid)) {
								builder.addContainer(dataSourceId, row, Triplifier.toSafeURIString(headers_map.get(colid)), value);
							} else {
								builder.addValue(dataSourceId, row, colid, value);
							}
						} else {
							Object value = extractCellValue(cell, evaluateFormulas);
							colid++;
							if (headers && headers_map.containsKey(colid)) {
								builder.addValue(dataSourceId, row, Triplifier.toSafeURIString(headers_map.get(colid)), value);
							} else {
								builder.addValue(dataSourceId, row, colid, value);
							}
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
					return extractCellValue(evaluatedCell, evaluateFormulas);
				} else {
					return cell.getCellFormula();
				}
			case BLANK:
			case ERROR:
			case _NONE:
		}
		return "";
	}


	private void extractCompositeCellValue(String dataSourceId, String containerId, Cell cell, boolean evaluateFormulas, FacadeXGraphBuilder builder, String namespace) {
		if (cell == null) return;
		builder.addType(dataSourceId, containerId, namespace + cell.getCellType().toString());
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
					builder.addValue(dataSourceId, containerId, 1, extractCellValue(evaluatedCell, evaluateFormulas));
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

		if (cell.getCellComment() != null){
			Comment comment = cell.getCellComment();
			if(comment.getAuthor()!=null){
				builder.addValue(dataSourceId, containerId, "author", comment.getAuthor());
			}
			if(comment.getString()!=null){
				RichTextString commentRichTextString = comment.getString();
				commentRichTextString.clearFormatting();
				builder.addValue(dataSourceId, containerId, "threadedComment", commentRichTextString.getString());
			}
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
