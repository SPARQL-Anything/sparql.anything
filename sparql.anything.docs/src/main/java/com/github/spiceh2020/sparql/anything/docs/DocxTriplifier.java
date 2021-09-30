package com.github.spiceh2020.sparql.anything.docs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class DocxTriplifier implements Triplifier {

	public final static String KEEP_PARAGRAPH = "docs.preserver-paragraphs";

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();

		URL url = Triplifier.getLocation(properties);
		if (url == null)
			return dg;
		String root = Triplifier.getRootArgument(properties, url);
//		Charset charset = getCharsetArgument(properties);
//		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);
//		String namespace = Triplifier.getNamespaceArgument(properties);
		String dataSourceId = builder.getMainGraphName().getURI();

		builder.addRoot(dataSourceId, root);

		InputStream is = url.openStream();
		try (XWPFDocument document = new XWPFDocument(is)) {
			List<XWPFParagraph> paragraphs = document.getParagraphs();

			boolean keepParagraph = Boolean.parseBoolean(properties.getProperty(KEEP_PARAGRAPH, "false"));

			if (keepParagraph) {
				int count = 1;
				for (XWPFParagraph para : paragraphs) {
//					builder.addValue(dataSourceId, root, count, para.getText());
					builder.addValue(dataSourceId, root, count,
							NodeFactory.createLiteral(para.getText(), XSDDatatype.XSDstring));
					count++;
				}
			} else {
				StringBuilder sb = new StringBuilder();
				for (XWPFParagraph para : paragraphs) {
					sb.append(para.getText());
				}
//				builder.addValue(dataSourceId, root, 1, sb.toString());
				builder.addValue(dataSourceId, root, 1,
						NodeFactory.createLiteral(sb.toString(), XSDDatatype.XSDstring));
			}
		}

		is.close();

		return builder.getDatasetGraph();
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
