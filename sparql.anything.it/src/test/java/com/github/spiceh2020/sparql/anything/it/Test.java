package com.github.spiceh2020.sparql.anything.it;

import java.util.Date;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.engine.main.QC;

import com.github.spiceh2020.sparql.anything.engine.FacadeX;
import com.github.spiceh2020.sparql.anything.engine.TriplifierRegister;


public class Test {

	private static void testText(String location) {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory
				.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE <facade-x:location=" + location + "> { ?s ?p ?o }}");

		QueryExecutionFactory.create(query, kb).execConstruct().write(System.out, "TTL");
	}

	private static void testHTML(String location) {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory
				.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE <facade-x:location=" + location + "> { ?s ?p ?o }}");

		QueryExecutionFactory.create(query, kb).execConstruct().write(System.out, "TTL");
	}

	private static void testBinary(String location) {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE <facade-x:location=" + location
				+ ",metadata=true> { ?s ?p ?o }}");
		QueryExecutionFactory.create(query, kb).execConstruct().write(System.out, "TTL");
	}

	private static void testMetadata(String location) {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory.create(
				"CONSTRUCT {?s ?p ?o} WHERE { SERVICE <facade-x:location=https://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg"
						+ ",metadata=true> { GRAPH <facade-x:metadata>{?s ?p ?o} }}");
		QueryExecutionFactory.create(query, kb).execConstruct().write(System.out, "TTL");
	}

	private static void testCSV(String location) {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory.create(
				"PREFIX fx: <urn:facade-x:ns#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> CONSTRUCT {?s ?p ?o} WHERE { SERVICE <facade-x:location="
						+ location + ",metadata=true> { ?s ?p ?o }}");
		QueryExecutionFactory.create(query, kb).execConstruct().write(System.out, "TTL");
	}

	public static void main(String[] args) {

//		testText("file:///Users/lgu/Desktop/t1.txt");
		testHTML("file:///Users/lgu/Desktop/test.html");
		TriplifierRegister.getInstance().printMediaTypes();
		
//		testMetadata("file:///Users/lgu/Desktop/Canon_40D.jpg");
//		testBinary("file:///Users/lgu/Desktop/Canon_40D.jpg");
//		
//		testCSV("file:///Users/lgu/Desktop/email.csv");
//		
//		testCSV("file:///Users/lgu/Desktop/breakfast_menu.xml");
//		System.getenv("USER");
//		System.out.println(System.getenv("USER"));
//		
//		System.out.println(new Date(1608470554399L).toString());
	}

}
