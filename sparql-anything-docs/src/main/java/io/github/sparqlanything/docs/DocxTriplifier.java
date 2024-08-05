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

package io.github.sparqlanything.docs;

import com.google.common.collect.Sets;
import io.github.sparqlanything.model.*;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;
import org.apache.jena.graph.NodeFactory;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@io.github.sparqlanything.model.annotations.Triplifier
public class DocxTriplifier implements Triplifier {

	@Example(resource = "https://sparql-anything.cc/examples/Doc1.docx", description = "Construct the graph by merging multiple consecutive paragraphs into single a single slot.", query = "CONSTRUCT { ?s ?p ?o . } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.merge-paragraphs=true> { ?s ?p ?o } }")
	@Option(description = "It tells the document triplifier to merge all the paragraphs of the document into a single slot (new line characters are preserved)", validValues = "true/false")
	public final static IRIArgument MERGE_PARAGRAPHS = new IRIArgument("docs.merge-paragraphs", "false");

	@Example(description = "Construct the dataset by using the headers of the columns of the tables to mint the property URIs.", query = "CONSTRUCT { ?s ?p ?o . } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.table-headers=true> { ?s ?p ?o } }", resource = "https://sparql-anything.cc/examples/Doc1.docx")
	@Option(description = "It tells the document triplifier to use the headers of the tables within the document file for minting the properties of the generated triples.", validValues = "true/false")
	public final static IRIArgument TABLE_HEADERS = new IRIArgument("docs.table-headers", "false");

	private static final Logger logger = LoggerFactory.getLogger(DocxTriplifier.class);

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);
		if (url == null) return;

		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		String namespace = PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE);
		boolean mergeParagraphs = PropertyUtils.getBooleanProperty(properties, MERGE_PARAGRAPHS);
		boolean headers = PropertyUtils.getBooleanProperty(properties, TABLE_HEADERS);

		builder.addRoot(dataSourceId);

		try (InputStream is = url.openStream(); XWPFDocument document = new XWPFDocument(is)) {

			List<XWPFParagraph> paragraphs = document.getParagraphs();

			builder.addType(dataSourceId, SPARQLAnythingConstants.ROOT_ID, "Document");

			int count = 1;
			if (!mergeParagraphs)
				count = extractParagraphs(builder, dataSourceId, paragraphs, count);
			 else
				count = extractParagraphsAsASingleSlot(builder, dataSourceId, paragraphs, count);

			count = extractTables(builder, dataSourceId, namespace, headers, document, count);

			extractComments(builder, document, dataSourceId, dataSourceId);

		}

	}

	private static int extractTables(FacadeXGraphBuilder builder, String dataSourceId, String namespace, boolean headers, XWPFDocument document, int count) {
		for (XWPFTable xwpfTable : document.getTables()) {
			String tableId = "Table_".concat(String.valueOf(count));
			builder.addContainer(dataSourceId, SPARQLAnythingConstants.ROOT_ID, count, tableId);

			LinkedHashMap<Integer, String> headers_map = new LinkedHashMap<>();
			int rowNumber = 0;
			Iterator<XWPFTableRow> rowIterator = xwpfTable.getRows().iterator();
			while (rowIterator.hasNext()) {
				// Header
				if (headers && rowNumber == 0) {
					XWPFTableRow xwpfTableRow = rowIterator.next();
					Iterator<XWPFTableCell> cellIterator = xwpfTableRow.getTableCells().iterator();
					int columnId = 0;
					while (cellIterator.hasNext()) {
						columnId++;
						XWPFTableCell xwpfTableCell = cellIterator.next();
						String columnString = xwpfTableCell.getText();
						String columnName = columnString.strip();
						int c = 0;
						while (headers_map.containsValue(columnName)) {
							c++;
							columnName += "_".concat(String.valueOf(c));
						}
						headers_map.put(columnId, columnName);
					}
				}

				// Data
				if (rowIterator.hasNext()) {
					XWPFTableRow xwpfTableRow = rowIterator.next();
					rowNumber++;
					String rowId = namespace + "Table_" + count + "_Row_" + rowNumber;
					builder.addContainer(dataSourceId, tableId, rowNumber, rowId);
					Iterator<XWPFTableCell> cellIterator = xwpfTableRow.getTableCells().iterator();
					int columnId = 0;
					while (cellIterator.hasNext()) {
						columnId++;
						XWPFTableCell xwpfTableCell = cellIterator.next();
						String value = xwpfTableCell.getText();
						if (headers && headers_map.containsKey(columnId)) {
							builder.addValue(dataSourceId, rowId, headers_map.get(columnId), value);
						} else {
							builder.addValue(dataSourceId, rowId, columnId, value);
						}
					}
				}

			}

			count++;
		}
		return count;
	}

	private static int extractParagraphs(FacadeXGraphBuilder builder, String dataSourceId, List<XWPFParagraph> paragraphs, int count) {
		for (XWPFParagraph para : paragraphs) {
			logger.trace("Paragraph {} {}", count, para.getText());
			String paragraphId;
			if (para.getStyle() != null) {
				paragraphId = "/".concat(Triplifier.toSafeURIString(para.getStyle())).concat("/").concat(String.valueOf(count));
				builder.addType(dataSourceId, paragraphId, Triplifier.toSafeURIString(para.getStyle()));
			} else {
				paragraphId = "/paragraph/".concat(String.valueOf(count));
				builder.addType(dataSourceId, paragraphId, "Paragraph");
			}

			int paragraphSlots = 1;
			builder.addContainer(dataSourceId, SPARQLAnythingConstants.ROOT_ID, count, paragraphId);
			builder.addValue(dataSourceId, paragraphId, paragraphSlots, para.getText());
			paragraphSlots ++;

			for (CTMarkupRange markup : para.getCTP().getCommentRangeStartList()) {
				builder.addContainer(dataSourceId, paragraphId, paragraphSlots, getCommentId(dataSourceId, markup.getId().toString()));
				paragraphSlots ++;
			}

			count++;
		}
		return count;
	}

	private static int extractParagraphsAsASingleSlot(FacadeXGraphBuilder builder, String dataSourceId, List<XWPFParagraph> paragraphs, int count) {
		StringBuilder sb = new StringBuilder();
		for (XWPFParagraph para : paragraphs) {
			sb.append(para.getText());
			sb.append("\n");
		}
		builder.addValue(dataSourceId, SPARQLAnythingConstants.ROOT_ID, count, NodeFactory.createLiteralString(sb.toString()));
		count++;
		return count;
	}

	private static String getCommentId(String dataSourceId, String id){
		return dataSourceId.concat("/Comment_").concat(id);
	}

	private static void extractComments(FacadeXGraphBuilder builder, XWPFDocument document, String dataSourceId, String documentId) {
		for (XWPFComment comment : document.getComments()) {
			String commentId = getCommentId(dataSourceId, comment.getId());
			builder.addType(dataSourceId, commentId, "Comment");
			//builder.addContainer(dataSourceId, documentId, slotCounter.getAndIncrement(), commentId);


			// add author
			String authorCommentId = commentId.concat("/Author");
			builder.addType(dataSourceId, authorCommentId, "CommentAuthor");
			builder.addContainer(dataSourceId, commentId, 1, authorCommentId);
			builder.addValue(dataSourceId, authorCommentId, 1, comment.getAuthor());

			// add author
			String textCommentId = commentId.concat("/CommentText");
			builder.addType(dataSourceId, textCommentId, "CommentText");
			builder.addContainer(dataSourceId, commentId, 2, textCommentId);
			builder.addValue(dataSourceId, textCommentId, 1, comment.getText());

			// add author
			String commentIdId = commentId.concat("/CommentId");
			builder.addType(dataSourceId, commentIdId, "CommentId");
			builder.addContainer(dataSourceId, commentId, 3, commentIdId);
			builder.addValue(dataSourceId, commentIdId, 1, comment.getId());
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("docx");
	}

}
