package com.github.spiceh2020.sparql.anything.engine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;

import com.github.spiceh2020.json2rdf.transformers.JSONTransformer;
import com.github.spiceh2020.sparql.anything.model.Format;

public class Main {

	private static void uriScheme() {
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

		TriplifierRegister.getInstance().registerTriplifierForFormat(Format.JSON, JSONTransformer.class);

		// @f:off
		String query = ""
				+ "PREFIX source: <https://w3id.org/spice/properties/>"
				+ "SELECT DISTINCT * {"
				+ "SERVICE <tuple:propertyPrefix=https://w3id.org/spice/properties/,location=https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/src/main/resources/test.json> "
				+ " {"
				+ "?s ?p ?o"
				+ "}" 
				+ "}";
		// @f:on

		QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(query), kb);

		System.out.println(ResultSetFormatter.asText(qexec.execSelect()));
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		uriScheme();
	}
}
