package it.cnr.istc.stlab.executor;

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
import org.apache.jena.sparql.function.FunctionRegistry;

public class MainTest {

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

		// @f:off
		String query = ""
				+ "PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/src/main/resources/test.json/>"
				+ "SELECT DISTINCT * {"
				+ "GRAPH <tuple://https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/src/main/resources/test.json> "
				+ " {"
				+ "?s source:a ?o"
				+ "}" 
				+ "}";
		// @f:on


		QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(query), kb);

		System.out.println(ResultSetFormatter.asText(qexec.execSelect()));
	}
	
	private static void functionBinding() {
		Dataset kb = DatasetFactory.createGeneral(); // createMem = deprecated
		RDFDataMgr.read(kb, new ByteArrayInputStream(
				"<https://example.org/testSubject> <https://example.org/testPredicate> <https://example.org/testObject> <https://example.org/testGraph> ."
						.getBytes()),
				Lang.NQ);

		OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
			@Override
			public OpExecutor create(ExecutionContext execCxt) {
				return new TupleOpExecutorFunct(execCxt);
			}
		};

		QC.setFactory(ARQ.getContext(), customExecutorFactory);

		// @f:off
		String query =
				  "PREFIX source: <https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/src/main/resources/test.json/>"
				+ "PREFIX f: <http://example.org/function#> "
				+ "SELECT DISTINCT * {"
				+ "BIND(IRI(f:tuplify('https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/src/main/resources/test.json')) AS ?g)"
//				+ "GRAPH <https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/src/main/resources/test.json?tuple> "
//				+ "VALUES (?g) {(IRI(f:tuplify('https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/src/main/resources/test.json')))}"
//				+ "{SELECT ?g {BIND(IRI(f:tuplify('https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/src/main/resources/test.json')) AS ?g)}}" 
				+ "GRAPH ?g "
				+ " {"
				+ "?s ?p ?o"
				+ "} "
				+ "}";
		// @f:on

		FunctionRegistry.get().put("http://example.org/function#tuplify", Tuplify.class);

		QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(query), kb);

		System.out.println(ResultSetFormatter.asText(qexec.execSelect()));
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		uriScheme();
		functionBinding();
	}
}
