package com.github.sparqlanything.it;

import com.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.vocabulary.RDFS;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class DocumentationExampleSandbox {

	private static final Map<String, String> prefixes = new HashMap<String, String>();

	public static void bibtex() {
		String queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    ?s ?p ?o .\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.bib>\n" +
				"      { ?s  ?p  ?o }\n" +
				"  }";
		Query query = QueryFactory.create(queryString);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);

		m.write(System.out, "TTL");

		// Query 2
//		queryString = "CONSTRUCT\n" +
//				"  {\n" +
//				"    ?s ?p ?o .\n" +
//				"  }\n" +
//				"WHERE\n" +
//				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.table-headers=true>\n" +
//				"      { ?s  ?p  ?o }\n" +
//				"  }";
//		query = QueryFactory.create(queryString);
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");

		// query 3
//		queryString = "CONSTRUCT\n" +
//				"  {\n" +
//				"    ?s ?p ?o .\n" +
//				"  }\n" +
//				"WHERE\n" +
//				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.merge-paragraphs=true>\n" +
//				"      { ?s  ?p  ?o }\n" +
//				"  }";
//		query = QueryFactory.create(queryString);
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");

	}

	public static void doc() {
		String queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    ?s ?p ?o .\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:https://sparql-anything.cc/examples/Doc1.docx>\n" +
				"      { ?s  ?p  ?o }\n" +
				"  }";
		Query query = QueryFactory.create(queryString);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);

		m.write(System.out, "TTL");

		// Query 2
		queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    ?s ?p ?o .\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.table-headers=true>\n" +
				"      { ?s  ?p  ?o }\n" +
				"  }";
		query = QueryFactory.create(queryString);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
		m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");

		// query 3
		queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    ?s ?p ?o .\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.merge-paragraphs=true>\n" +
				"      { ?s  ?p  ?o }\n" +
				"  }";
		query = QueryFactory.create(queryString);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
		m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");

	}

	public static void yaml() {
		String queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    ?s ?p ?o .\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=/Users/lgu/Desktop/example.yaml>\n" +
				"      { ?s  ?p  ?o }\n" +
				"  }";
		Query query = QueryFactory.create(queryString);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);

		m.write(System.out, "TTL");

		// Query 2
//		queryString = "CONSTRUCT\n" +
//				"  {\n" +
//				"    ?s ?p ?o .\n" +
//				"  }\n" +
//				"WHERE\n" +
//				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.table-headers=true>\n" +
//				"      { ?s  ?p  ?o }\n" +
//				"  }";
//		query = QueryFactory.create(queryString);
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");
//
//		// query 3
//		queryString = "CONSTRUCT\n" +
//				"  {\n" +
//				"    ?s ?p ?o .\n" +
//				"  }\n" +
//				"WHERE\n" +
//				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Doc1.docx,docs.merge-paragraphs=true>\n" +
//				"      { ?s  ?p  ?o }\n" +
//				"  }";
//		query = QueryFactory.create(queryString);
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");

	}

	public static void spreadsheet() {
		String queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    GRAPH ?g { ?s ?p ?o }\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book1.xlsx> { \n " +
				"      GRAPH ?g { ?s  ?p  ?o } }\n" +
				"  } ";
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Dataset dd = QueryExecutionFactory.create(query, ds).execConstructDataset();
		RDFDataMgr.write(System.out, dd, Lang.TRIG);


		// Query 2

		queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    GRAPH ?g { ?s ?p ?o }\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book1.xlsx,spreadsheet.headers=true> { \n " +
				"      GRAPH ?g { ?s  ?p  ?o } }\n" +
				"  } ";

		query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
		dd = QueryExecutionFactory.create(query, ds).execConstructDataset();
		RDFDataMgr.write(System.out, dd, Lang.TRIG);

//		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//
//		m.write(System.out, "TTL");

		// Query 2
//		queryString = "PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>\n" +
//				"PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>\n" +
//				"PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//				"\n" +
//				"CONSTRUCT\n" +
//				"  {\n" +
//				"    ?s1 ?p1 ?o1 .\n" +
//				"  }\n" +
//				"WHERE\n" +
//				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar>\n" +
//				"      { fx:properties fx:archive.matches  \".*txt|.*csv\" .\n" +
//				"        ?s        fx:anySlot            ?file1 .\n" +
//				"        SERVICE <x-sparql-anything:> {\n" +
//				"            fx:properties fx:location ?file1 .\n" +
//				"            fx:properties fx:from-archive \"https://sparql-anything.cc/examples/example.tar\" .\n" +
//				"            ?s1 ?p1 ?o1 .\n" +
//				"        }\n" +
//				"      }\n" +
//				"  }";
//		query = QueryFactory.create(queryString);
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");

	}

	public static void metadata() {
		String queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    GRAPH ?g { ?s ?p ?o }\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=https://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg,metadata=true> { \n " +
				"      GRAPH ?g { ?s  ?p  ?o } }\n" +
				"  } ";
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Dataset dd = QueryExecutionFactory.create(query, ds).execConstructDataset();
		RDFDataMgr.write(System.out, dd, Lang.TRIG);


		// Query 2
//
//		queryString = "CONSTRUCT\n" +
//				"  {\n" +
//				"    GRAPH ?g { ?s ?p ?o }\n" +
//				"  }\n" +
//				"WHERE\n" +
//				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Book1.xlsx,spreadsheet.headers=true> { \n " +
//				"      GRAPH ?g { ?s  ?p  ?o } }\n" +
//				"  } ";
//
//		query = QueryFactory.create(queryString, Syntax.syntaxARQ);
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		dd = QueryExecutionFactory.create(query, ds).execConstructDataset();
//		RDFDataMgr.write(System.out, dd, Lang.TRIG);

//		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//
//		m.write(System.out, "TTL");

		// Query 2
//		queryString = "PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>\n" +
//				"PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>\n" +
//				"PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//				"\n" +
//				"CONSTRUCT\n" +
//				"  {\n" +
//				"    ?s1 ?p1 ?o1 .\n" +
//				"  }\n" +
//				"WHERE\n" +
//				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar>\n" +
//				"      { fx:properties fx:archive.matches  \".*txt|.*csv\" .\n" +
//				"        ?s        fx:anySlot            ?file1 .\n" +
//				"        SERVICE <x-sparql-anything:> {\n" +
//				"            fx:properties fx:location ?file1 .\n" +
//				"            fx:properties fx:from-archive \"https://sparql-anything.cc/examples/example.tar\" .\n" +
//				"            ?s1 ?p1 ?o1 .\n" +
//				"        }\n" +
//				"      }\n" +
//				"  }";
//		query = QueryFactory.create(queryString);
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");

	}

	public static void archive() {
		String queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    ?s ?p ?o .\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar>\n" +
				"      { ?s  ?p  ?o }\n" +
				"  }";
		Query query = QueryFactory.create(queryString);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);

		m.write(System.out, "TTL");

		// Query 2
		queryString = "PREFIX  xyz:  <http://sparql.xyz/facade-x/data/>\n" +
				"PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>\n" +
				"PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"\n" +
				"CONSTRUCT\n" +
				"  {\n" +
				"    ?s1 ?p1 ?o1 .\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar>\n" +
				"      { fx:properties fx:archive.matches  \".*txt|.*csv\" .\n" +
				"        ?s        fx:anySlot            ?file1 .\n" +
				"        SERVICE <x-sparql-anything:> {\n" +
				"            fx:properties fx:location ?file1 .\n" +
				"            fx:properties fx:from-archive \"https://sparql-anything.cc/examples/example.tar\" .\n" +
				"            ?s1 ?p1 ?o1 .\n" +
				"        }\n" +
				"      }\n" +
				"  }";
		query = QueryFactory.create(queryString);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
		m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");


		// Query 2
		queryString = "CONSTRUCT\n" +
				"  {\n" +
				"    ?s ?p ?o .\n" +
				"  }\n" +
				"WHERE\n" +
				"  { SERVICE <x-sparql-anything:location=/Users/lgu/Desktop/example>\n" +
				"      { ?s  ?p  ?o }\n" +
				"  }";
		query = QueryFactory.create(queryString);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
		m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");

	}

	public static void json1() throws URISyntaxException {
//		String location = DocumentationExampleSandbox.class.getClassLoader().getResource("DocExamples/json.json")
//				.toURI().toString();
		String location = "https://sparql-anything.cc/examples/simple.json";
		Query query = QueryFactory.create(
				"CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location + "> { ?s ?p ?o} }");

		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);

		m.write(System.out, "TTL");
	}

	public static void json2() throws URISyntaxException {
		String location = "https://sparql-anything.cc/example1.json";
		Query query = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX fx: <http://sparql.xyz/facade-x/ns/>" + "SELECT * { SERVICE <x-sparql-anything:location="
				+ location + "> { " + " fx:properties fx:json.path '$[?(@.name==\"Friends\")]' . "
				+ " _:s xyz:language ?language . " + " } }");

		Query queryConstruct = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX fx: <http://sparql.xyz/facade-x/ns/>"
				+ "CONSTRUCT { ?s ?p ?o } WHERE { SERVICE <x-sparql-anything:location=" + location + "> { "
				+ " fx:properties fx:json.path '$[?(@.name==\"Friends\")]' . " + " ?s ?p ?o . " + " } }");

		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		System.out.println(queryConstruct.toString(Syntax.defaultSyntax));
		Model m = QueryExecutionFactory.create(queryConstruct, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");

		queryConstruct = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX fx: <http://sparql.xyz/facade-x/ns/>"
				+ "CONSTRUCT { ?s ?p ?o } WHERE { SERVICE <x-sparql-anything:location=" + location + "> { "
				+ " fx:properties fx:json.path.1 '$[?(@.name==\"Friends\")].stars' . "
				+ " fx:properties fx:json.path.2 '$[?(@.name==\"Cougar Town\")].stars' . " + " ?s ?p ?o . " + " } }");

		System.out.println(queryConstruct.toString(Syntax.defaultSyntax));
		m = QueryExecutionFactory.create(queryConstruct, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");

	}

	public static void html1() throws URISyntaxException {
//		String location = DocumentationExampleSandbox.class.getClassLoader().getResource("DocExamples/simple.html")
//				.toURI().toString();
		String location = "https://sparql-anything.cc/examples/simple.html";
		Query query = QueryFactory.create(
				"CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location + "> { ?s ?p ?o} }");

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		System.out.println(query.toString(Syntax.defaultSyntax));

		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);

		m.write(System.out, "TTL");

		query = QueryFactory.create(
				"PREFIX whatwg: <https://html.spec.whatwg.org/#> SELECT ?text WHERE { SERVICE <x-sparql-anything:location="
						+ location + ",html.selector=.paragraph> { ?s whatwg:innerText ?text} }");
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		location = "https://sparql-anything.cc/examples/Microdata1.html";
		query = QueryFactory.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location
				+ ",html.metadata=true> {GRAPH ?g {?s ?p ?o}} }");
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
		m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");

		location = "https://sparql-anything.cc/examples/Microdata1.html";
		query = QueryFactory.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location
				+ ",html.metadata=false> {GRAPH ?g {?s ?p ?o}} }");
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
		m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");

	}

	public static void csv() throws URISyntaxException {
//		String location = DocumentationExampleSandbox.class.getClassLoader().getResource("DocExamples/simple.html")
//				.toURI().toString();
		String location = "https://sparql-anything.cc/examples/simple.csv";
		Query query = QueryFactory.create(
				"CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location + "> { ?s ?p ?o} }");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
//		System.out.println(query.toString(Syntax.defaultSyntax));
//
//		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");
//
//		location = "https://sparql-anything.cc/examples/simple.tsv";
//		query = QueryFactory.create(
//				"PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT (AVG(xsd:float(?petalLength)) AS ?avgPetalLength) WHERE { SERVICE <x-sparql-anything:location="
//						+ location + ",csv.headers=true,csv.format=TDF> { "
//						+ "?s xyz:Sepal_length ?length ; xyz:Petal_length ?petalLength ."
//						+ "FILTER(xsd:float(?length)>4.9) " + "} }");
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//
//		query = QueryFactory.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location
//				+ ",csv.format=TDF> {?s ?p ?o} }");
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");
//
//		location = DocumentationExampleSandbox.class.getClassLoader().getResource("DocExamples/simple.csv").toURI()
//				.toString();
//
//		query = QueryFactory.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location
//				+ ",csv.headers=true> {?s ?p ?o} }");
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");
//
//		location = "https://sparql-anything.cc/examples/simple.tsv";
//		String qs = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT (MAX(xsd:float(?petalLength)) AS ?maxPetalLength) WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.tsv,csv.headers=true> { fx:properties fx:csv.delimiter \"\\t\" . ?s xyz:Sepal_length ?length ; xyz:Petal_length ?petalLength .FILTER(xsd:float(?length)<4.9) } }\n";
//		System.out.println(qs);
//		query = QueryFactory.create(qs);
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		
//		location="https://sparql-anything.cc/examples/csv_with_commas.csv";
//		query = QueryFactory.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location
//				+ ",csv.headers=true,csv.quote-char='> {?s ?p ?o} }");
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");

		location = "https://sparql-anything.cc/examples/simple_with_null.csv";
		query = QueryFactory.create(
				"PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?name ?surname WHERE { SERVICE <x-sparql-anything:location="
						+ location
						+ ",csv.headers=true> {fx:properties fx:csv.null-string \"\" . ?c xyz:name ?name . ?c xyz:surname ?surname . FILTER NOT EXISTS { ?c xyz:email ?email} } }");
		System.out.println(query.toString(Syntax.defaultQuerySyntax));
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

//		
//		location = "https://sparql-anything.cc/examples/Microdata1.html";
//		query = QueryFactory.create(
//				"CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location + ",html.metadata=false> {GRAPH ?g {?s ?p ?o}} }");
//		System.out.println(query.toString(Syntax.defaultQuerySyntax));
////		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefixes(prefixes);
//		m.write(System.out, "TTL");

	}

	public static void binary() throws URISyntaxException {
		String location = "https://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg";
		Query query = QueryFactory.create(
				"CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location + "> { ?s ?p ?o} }");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		System.out.println(query.toString(Syntax.defaultSyntax));
		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");
	}

	public static void txt() throws URISyntaxException {
		String location = "https://sparql-anything.cc/examples/simple.txt";
		Query query = QueryFactory.create(
				"CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=" + location + "> { ?s ?p ?o} }");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		System.out.println(query.toString(Syntax.defaultSyntax));
		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefixes(prefixes);
		m.write(System.out, "TTL");


		query = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  SELECT ?line WHERE { SERVICE <x-sparql-anything:location=" + location + "> {fx:properties fx:txt.regex \".*\\\\n\" . ?s fx:anySlot ?line} }");
		System.out.println(query.toString(Syntax.defaultSyntax));
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		query = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  SELECT ?line WHERE { SERVICE <x-sparql-anything:location=" + location + "> {fx:properties fx:txt.regex \"(.*)\\\\n\" ; fx:txt.group 1 . ?s fx:anySlot ?line} }");
		System.out.println(query.toString(Syntax.defaultSyntax));
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

		query = QueryFactory.create(
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>  SELECT ?line WHERE { SERVICE <x-sparql-anything:location=" + location + "> {fx:properties fx:txt.split \"\\\\n\" . ?s fx:anySlot ?line} }");
		System.out.println(query.toString(Syntax.defaultSyntax));
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
	}

	public static void options() throws URISyntaxException {
//		System.setProperty("org.slf4j.simpleLogger.log.com.github.sparqlanything", "Trace");
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query;
		Model m;

//		query = QueryFactory.create(
//				"PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (COUNT(*) AS ?c) WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:content \"one,two,tree\" ; fx:txt.split \",\" . ?s fx:anySlot ?o} }");
//		System.out.println(query.toString(Syntax.defaultSyntax));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//
//		query = QueryFactory.create(
//				"PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (COUNT(*) AS ?c) WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:content '[\"one\",\"two\",\"three\", \"four\"]' ; fx:media-type \"application/json\" . ?s fx:anySlot ?o} }");
//		System.out.println(query.toString(Syntax.defaultSyntax));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

//		query = QueryFactory.create(
//				"PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (COUNT(?o) AS ?nOfItems)  WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:command 'echo [\"one\",\"two\",\"three\", \"four\"]' ; fx:media-type \"application/json\" .  ?s fx:anySlot ?o} }");
//		System.out.println(query.toString(Syntax.defaultSyntax));
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));

//		query = QueryFactory.create(
//				"PREFIX fx: <http://sparql.xyz/facade-x/ns/> CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:location 'https://sparql-anything.cc/example1.json' ; fx:root 'http://example.org' ; fx:blank-nodes false . ?s ?p ?o} }");
//		System.out.println(query.toString(Syntax.defaultSyntax));
////		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		QueryExecutionFactory.create(query, ds).execConstruct().write(System.out, "TTL");


//		query = QueryFactory.create(
//				"PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>\n" +
//						"\n" +
//						"CONSTRUCT \n" +
//						"  { \n" +
//						"    ?s ?p ?o .\n" +
//						"  }\n" +
//						"WHERE\n" +
//						"  { SERVICE <x-sparql-anything:>\n" +
//						"      { fx:properties\n" +
//						"                  fx:content     '{\"name\":\"Vincent\", \"surname\": \"Vega\"}' ;\n" +
//						"                  fx:media-type         \"application/json\" ;\n" +
//						"                  fx:root         \"http://example.org/myRoot\" ;\n" +
//						"                  fx:blank-nodes  false .\n" +
//						"        ?s        ?p              ?o\n" +
//						"      }\n" +
//						"  }");
////		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		QueryExecutionFactory.create(query, ds).execConstruct().write(System.out, "TTL");
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create(
//				"PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>\n" +
//						"\n" +
//						"CONSTRUCT \n" +
//						"  { \n" +
//						"    ?s ?p ?o .\n" +
//						"  }\n" +
//						"WHERE\n" +
//						"  { SERVICE <x-sparql-anything:>\n" +
//						"      { fx:properties\n" +
//						"                  fx:content     '{\"name\":\"Vincent\", \"surname\": \"Vega\", \"performer\" : {\"name\": \"John \", \"surname\": \" Travolta\"} }' ;\n" +
//						"                  fx:media-type         \"application/json\" ;\n" +
////						"                  fx:namespace         \"http://example.org/myNamespace/\" ;\n" +
//						"                  fx:trim-strings  true .\n" +
//						"        ?s        ?p              ?o\n" +
//						"      }\n" +
//						"  }");
////		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		Model m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.write(System.out, "TTL");
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create(
//				"PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>\n" +
//						"\n" +
//						"CONSTRUCT \n" +
//						"  { \n" +
//						"    ?s ?p ?o .\n" +
//						"  }\n" +
//						"WHERE\n" +
//						"  { SERVICE <x-sparql-anything:>\n" +
//						"      { fx:properties\n" +
//						"                  fx:content     '{\"name\":\"Vincent\", \"surname\": \"Vega\", \"ID\": \"myNull\", \"performer\" : {\"name\": \"John\", \"surname\": \"Travolta\"} }' ;\n" +
//						"                  fx:media-type         \"application/json\" ;\n" +
////						"                  fx:namespace         \"http://example.org/myNamespace/\" ;\n" +
//						"                  fx:null-string  \"myNull\" .\n" +
//						"        ?s        ?p              ?o\n" +
//						"      }\n" +
//						"  }");
////		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.write(System.out, "TTL");
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create(
//				"PREFIX  fx:   <http://sparql.xyz/facade-x/ns/>\n" +
//						"\n" +
//						"CONSTRUCT \n" +
//						"  { \n" +
//						"    ?s ?p ?o .\n" +
//						"  }\n" +
//						"WHERE\n" +
//						"  { SERVICE <x-sparql-anything:>\n" +
//						"      { fx:properties\n" +
//						"                  fx:content     '{\"name\":\"Vincent\", \"surname\": \"Vega\" }' ;\n" +
//						"                  fx:triplifier         \"com.github.sparqlanything.json.JSONTriplifier\" ;\n" +
//						"       . ?s        ?p              ?o\n" +
//						"      }\n" +
//						"  }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.write(System.out, "TTL");
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX  fx:   <http://sparql.xyz/facade-x/ns/> CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:location 'https://sparql-anything.cc/examples/utf16.txt' ; fx:charset 'UTF16' . ?s ?p ?o  } }");
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.write(System.out, "TTL");
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX  fx:   <http://sparql.xyz/facade-x/ns/> CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:content '{\"name\":\"Vincent\", \"surname\": \"Vega\" }' ; fx:ondisk '/tmp/' ; fx:media-type 'application/json' . ?s ?p ?o  } }");
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.write(System.out, "TTL");
//		System.out.println(query.toString(Syntax.defaultSyntax));
//
//		query = QueryFactory.create("PREFIX  fx:   <http://sparql.xyz/facade-x/ns/> CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:content '{\"name\":\"Vincent\", \"surname\": \"Vega\" }' ; fx:ondisk '/tmp/' ; fx:ondisk.reuse true ; fx:media-type 'application/json' . ?s ?p ?o  } }");
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.write(System.out, "TTL");
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?seriesName (GROUP_CONCAT(?star) AS ?cast) WHERE {     SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json> {         ?tvSeries xyz:name ?seriesName .         ?tvSeries xyz:stars ?star .     } } GROUP BY ?seriesName");
//		query = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?seriesName (GROUP_CONCAT(?star) AS ?cast) WHERE {     SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json> {         ?tvSeries xyz:name ?seriesName .         ?tvSeries xyz:stars ?star .     } } GROUP BY ?seriesName");
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?name ?surname ?movie WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simpleArray.json,slice=true> { ?p xyz:name ?name ; xyz:surname ?surname ; xyz:movie ?movie } }");
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT * WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simpleArray.json,slice=true> { ?p1 xyz:name ?name1 ; xyz:surname ?surname1 ; xyz:movie ?movie . ?p2 xyz:name ?name2 ; xyz:surname ?surname2 ; xyz:movie ?movie FILTER(?p1 != ?p2)} }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> CONSTRUCT { ?s ?p ?o } WHERE { SERVICE <x-sparql-anything:> { fx:properties fx:use-rdfs-member true ; fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p ?o } }");
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.setNsPrefix("rdfs", RDFS.uri);
//		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
//		m.write(System.out, "TTL");
////		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?slot WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s fx:anySlot ?slot } }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));



//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?slot (fx:cardinal(?p) AS ?cardinal) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p ?slot  . FILTER(?p != rdf:type)} }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT  (fx:before(?p1, ?p2) AS ?p1_before_p2) (fx:before(?p2, ?p1) AS ?p2_before_p1)  (fx:before(?p1, ?p1) AS ?p1_before_p1)  WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p1 1  ; ?p2 2 } }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT  (fx:after(?p1, ?p2) AS ?p1_after_p2) (fx:after(?p2, ?p1) AS ?p2_after_p1)  (fx:after(?p1, ?p1) AS ?p1_after_p1)  WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p1 1  ; ?p2 2 } }");
//		m = QueryExecutionFactory.create(query, ds).execConstruct();
//		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
//		m.setNsPrefix("rdfs", RDFS.uri);
//		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
//		m.write(System.out, "TTL");
		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
		System.out.println(query.toString(Syntax.defaultSyntax));

		
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		prefixes.put("xyz", "http://sparql.xyz/facade-x/data/");
		prefixes.put("fx", "http://sparql.xyz/facade-x/ns/");
		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");

//		json1();

//		json2();

//		prefixes.put("xhtml", "http://www.w3.org/1999/xhtml#");
//		prefixes.put("whatwg", "https://html.spec.whatwg.org/#");
//
//		html1();

//		csv();

//		binary();

//		txt();

//		Pattern p = Pattern.compile(".*\n");
//		Matcher m = p.matcher("Hello world!\nHello world\n");
//		if(m.find()) {
//			System.out.println(m.group());
//		}

//		archive();
//		spreadsheet();
//		doc();
//		yaml();
//		metadata();
//		bibtex();
		options();

//		FileUtils.write(new File("/Users/lgu/Desktop/utf16.txt"), "UTF-16 test file", Charset.forName("UTF16"));
	}

}
