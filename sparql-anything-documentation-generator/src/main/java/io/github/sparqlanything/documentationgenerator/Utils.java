package io.github.sparqlanything.documentationgenerator;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.SPARQLAnythingConstants;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.main.QC;

import java.io.ByteArrayOutputStream;

public class Utils {

	public static String getFacadeXRdf(Query q) {
		try {
			// Set FacadeX OpExecutor as default executor factory
			QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

			// Execute the query by using standard Jena ARQ's API
			Dataset kb = DatasetFactory.createGeneral();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (q.isConstructType()) {
				Model m = QueryExecutionFactory.create(q, kb).execConstruct();
				m.setNsPrefixes(SPARQLAnythingConstants.PREFIXES);
				m.write(baos, "TTL");
			} else if (q.isConstructQuad()) {
				Dataset d = QueryExecutionFactory.create(q, kb).execConstructDataset();
				RDFDataMgr.write(baos, d, Lang.TRIG);
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
}
