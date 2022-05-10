
package com.github.sparqlanything.it;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.main.QC;
import org.jsfr.json.Collector;
import org.jsfr.json.JacksonParser;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.ValueBox;
import org.jsfr.json.provider.JacksonProvider;

import com.github.sparqlanything.engine.FacadeX;

public class DocumentationExampleSandbox {

	private static Map<String, String> prefixes = new HashMap<String, String>();

	public static void json1() throws URISyntaxException {
		String location = DocumentationExampleSandbox.class.getClassLoader().getResource("DocExamples/json.json")
				.toURI().toString();
		Query query = QueryFactory.create(
				"CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location + "> { ?s ?p ?o} }");

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);

		m.write(System.out, "TTL");
	}

	public static void json2() throws URISyntaxException {
		String location = DocumentationExampleSandbox.class.getClassLoader().getResource("DocExamples/json2.json")
				.toURI().toString();
		Query query = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX fx: <http://sparql.xyz/facade-x/ns/>" + "SELECT * { SERVICE <x-sparql-anything:location="
				+ location + "> { " + " fx:properties fx:json.path '$[?(@.name==\"Friends\")]' . " + " _:s xyz:language ?language . "
				+ " } }");
		
		Query queryConstruct = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX fx: <http://sparql.xyz/facade-x/ns/>" + "CONSTRUCT { ?s ?p ?o } WHERE { SERVICE <x-sparql-anything:location="
				+ location + "> { " + " fx:properties fx:json.path '$[?(@.name==\"Friends\")]' . " + " ?s ?p ?o . "
				+ " } }");

		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		System.out.println(queryConstruct.toString(Syntax.defaultSyntax));
		Model m = QueryExecutionFactory.create(queryConstruct, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");

	}

	public static void main(String[] args) throws URISyntaxException {
		prefixes.put("xyz", "http://sparql.xyz/facade-x/data/");
		prefixes.put("fx", "http://sparql.xyz/facade-x/ns/");
		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");

//		json1();

		json2();
	}

}
