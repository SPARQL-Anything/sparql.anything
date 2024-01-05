package io.github.sparqlanything.documentationgenerator;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.SPARQLAnythingConstants;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.main.QC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class Utils {

	public static String getFacadeXRdf(Query q) {
		try {
			// Set FacadeX OpExecutor as default executor factory
			QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

			// Execute the query by using standard Jena ARQ's API
			Dataset kb = DatasetFactory.createGeneral();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (q.isConstructType()) {

				if (q.isConstructQuad()) {
					Dataset d = QueryExecutionFactory.create(q, kb).execConstructDataset();
					RDFDataMgr.write(baos, d, Lang.TRIG);
				} else {
					Model m = QueryExecutionFactory.create(q, kb).execConstruct();
					m.setNsPrefixes(SPARQLAnythingConstants.PREFIXES);
					m.write(baos, "TTL");
				}

			} else if (q.isSelectType()) {
				return ResultSetFormatter.asText(QueryExecutionFactory.create(q, kb).execSelect());
			} else if (q.isAskType()) {
				return Boolean.toString(QueryExecutionFactory.create(q, kb).execAsk());
			} else if (q.isDescribeType()) {
				Model m = QueryExecutionFactory.create(q, kb).execDescribe();
				m.setNsPrefixes(SPARQLAnythingConstants.PREFIXES);
				m.write(baos, "TTL");
			}
			return baos.toString();
		} catch (Exception e) {
			System.err.println("Error with query \n" + q.toString(Syntax.syntaxSPARQL_11));
			e.printStackTrace();
		}
		return "";
	}

	public static String addLinkToIssues(String string) {
		StringBuilder sb = new StringBuilder();

		for (int index = 0; index < string.length(); index++) {
			if (string.charAt(index) == '#') {
				// [#num](https://github.com/SPARQL-Anything/sparql.anything/issues/num)
				// read up to next space
				StringBuilder subString = new StringBuilder();
				int index2 = index + 1;
				for (; index2 < string.length(); index2++) {
					if (Character.isDigit(string.charAt(index2))) {
						subString.append(string.charAt(index2));
					} else {
						break;
					}
				}

				try {
					int issueNumber = Integer.parseInt(subString.toString());
					sb.append(String.format("[#%d](https://github.com/SPARQL-Anything/sparql.anything/issues/%d)", issueNumber, issueNumber));
				} catch (NumberFormatException nfe) {
					sb.append("#").append(subString);
				}

				if (index2 < string.length()) sb.append(string.charAt(index2));
				index = index2;

			} else {
				sb.append(string.charAt(index));
			}
		}
		return sb.toString();
	}

	public static String readResourceToString(String resourceExample) throws IOException {
		if (resourceExample != null) {
			URL url = new URL(resourceExample);
			return IOUtils.toString(url.openStream(), Charset.defaultCharset());
		}
		return "";
	}
}
