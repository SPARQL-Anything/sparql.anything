package com.github.spiceh2020.sparql.anything.engine;

import java.io.ByteArrayInputStream;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;

import com.github.spiceh2020.sparql.anything.json.JSONTriplifier;

public class Main {

	private static void uriScheme() throws Exception {
		Dataset kb = DatasetFactory.createGeneral(); // createMem = deprecated
		RDFDataMgr.read(kb, new ByteArrayInputStream(
				"<https://example.org/testSubject> <https://example.org/testPredicate> <https://example.org/testObject> <https://example.org/testGraph> ."
						.getBytes()),
				Lang.NQ);

		OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
			@Override
			public OpExecutor create(ExecutionContext execCxt) {
				return new TupleOpExecutor(execCxt);
			}
		};

		QC.setFactory(ARQ.getContext(), customExecutorFactory);

//		TriplifierRegister.getInstance().registerTriplifier(new JSONTransformer());
		
		// @f:off
				String query0 = ""
						+ "PREFIX source: <https://w3id.org/spice/properties/>"
						+ "SELECT DISTINCT * {"
						+ "SERVICE <tuple:triplifier=com.github.spiceh2020.sparql.anything.json.JSONTriplifier,useBlankNodes=false,uriRoot=https://w3id.org/spice/resource/root,propertyPrefix=https://w3id.org/spice/properties/,location=https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/sparql.anything.engine/src/main/resources/test.json> "
						+ " {"
						+ "?s ?p ?o"
						+ "}" 
						+ "}";
				// @f:on
				
				System.out.println(
						ResultSetFormatter.asText(QueryExecutionFactory.create(QueryFactory.create(query0), kb).execSelect()));

		TriplifierRegister.getInstance().registerTriplifier(new JSONTriplifier());
		// @f:off
		String query1 = ""
				+ "PREFIX source: <https://w3id.org/spice/properties/>"
				+ "SELECT DISTINCT * {"
				+ "SERVICE <tuple:useBlankNodes=false,uriRoot=https://w3id.org/spice/resource/root,propertyPrefix=https://w3id.org/spice/properties/,location=https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/sparql.anything.engine/src/main/resources/test.json> "
				+ " {"
				+ "?s ?p ?o"
				+ "}" 
				+ "}";
		// @f:on

		// @f:off
				String query2 = ""
						+ "PREFIX source: <https://w3id.org/spice/properties/>"
						+ "SELECT DISTINCT * {"
						+ "SERVICE <tuple:https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/sparql.anything.engine/src/main/resources/test.json> "
						+ " {"
						+ "?s ?p ?o"
						+ "}" 
						+ "}";
				// @f:on

		System.out.println(
				ResultSetFormatter.asText(QueryExecutionFactory.create(QueryFactory.create(query1), kb).execSelect()));
		System.out.println(
				ResultSetFormatter.asText(QueryExecutionFactory.create(QueryFactory.create(query2), kb).execSelect()));
	}

	public static void main(String[] args) throws Exception {
		uriScheme();
	}
}
