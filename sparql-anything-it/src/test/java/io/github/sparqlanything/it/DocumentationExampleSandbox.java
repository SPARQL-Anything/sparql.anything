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

package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
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

	public static void options() throws URISyntaxException {
//		System.setProperty("org.slf4j.simpleLogger.log.io.github.sparqlanything", "Trace");
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
//						"                  fx:triplifier         \"io.github.sparqlanything.json.JSONTriplifier\" ;\n" +
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


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT  (fx:after(?p1, ?p2) AS ?p1_after_p2) (fx:after(?p2, ?p1) AS ?p2_after_p1)  (fx:after(?p1, ?p1) AS ?p1_after_p1)  WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p1 1  ; ?p2 2 } }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//				query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?slot ?p (fx:previous(?p) AS ?previous) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p ?slot  . FILTER(?p != rdf:type)} }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?slot ?p (fx:next(?p) AS ?next) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p ?slot  . FILTER(?p != rdf:type)} }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?slot ?p (fx:forward(?p, 3) AS ?forward) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p ?slot  . FILTER(?p != rdf:type)} }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?slot ?p (fx:backward(?p, 2) AS ?backward) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,3]' ; fx:media-type 'application/json' .  ?s ?p ?slot  . FILTER(?p != rdf:type)} }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.startsWith(?string, 'this') AS ?result1) (fx:String.startsWith(?string, 'This') AS ?result2) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'this is a test' .  ?s rdf:_1 ?string  } }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.endsWith(?string, 'test') AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'this is a test' .  ?s rdf:_1 ?string  } }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.indexOf(?string, 'i') AS ?result1) (fx:String.indexOf(?string, 'test') AS ?result2) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'this is a test' .  ?s rdf:_1 ?string  } }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.substring(?string, 10) AS ?result1) (fx:String.substring(?string, 5, 7) AS ?result2) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'this is a test' .  ?s rdf:_1 ?string  } }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.toLowerCase(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'THIS IS A TEST' .  ?s rdf:_1 ?string  } }");
////		m = QueryExecutionFactory.create(query, ds).execConstruct();
////		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
////		m.setNsPrefix("rdfs", RDFS.uri);
////		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
////		m.write(System.out, "TTL");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.toUpperCase(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'this is a test' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.trim(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '  this is a test  ' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.replace(?string, 'f', 'd') AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'fog' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.stripLeading(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '  this is a test  ' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.stripTrailing(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '  this is a test  ' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:String.removeTags(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '<p>This is a test</p>' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:WordUtils.capitalize(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'This is a TEST' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:WordUtils.capitalizeFully(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'This is a TEST' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:WordUtils.initials(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'This is a TEST' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:WordUtils.swapCase(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'This is a TEST' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));


//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:WordUtils.uncapitalize(?string) AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'This is a TEST' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:URLDecoder.decode(?string, 'UTF-8') AS ?result1) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'This+is+a+test' .  ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?s (fx:serial(?s) AS ?serial) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[1,2,1,2,3]' ; fx:media-type 'application/json' .  ?c fx:anySlot ?s  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?wins ?team (fx:serial(?wins, ?team) AS ?serial) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[{\"team\":\"Golden State Warriors\", \"year\":2015, \"wins\": 67}, {\"team\":\"Golden State Warriors\", \"year\":2016, \"wins\": 73}, {\"team\":\"Golden State Warriors\", \"year\":2017, \"wins\": 67}]' ; fx:media-type 'application/json' .  ?c xyz:wins ?wins ; xyz:team ?team  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT ?team ?year (fx:entity('http://example.org/', fx:URLEncoder.encode(?team, 'UTF-8'), ?year) AS ?entity) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content '[{\"team\":\"Golden State Warriors\", \"year\":2015, \"wins\": 67}, {\"team\":\"Golden State Warriors\", \"year\":2016, \"wins\": 73}, {\"team\":\"Golden State Warriors\", \"year\":2017, \"wins\": 67}]' ; fx:media-type 'application/json' .  ?c xyz:year ?year ; xyz:team ?team  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX fx: <http://sparql.xyz/facade-x/ns/> SELECT (fx:literal(?string, 'it') AS ?result) WHERE { SERVICE <x-sparql-anything:> { fx:properties  fx:content 'uno'  . ?s rdf:_1 ?string  } }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

		query = QueryFactory.create("PREFIX  xyz:  <http://sparql.xyz/facade-x/data/> PREFIX  fx:   <http://sparql.xyz/facade-x/ns/> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  CONSTRUCT    {      ?s ?p ?o .   } WHERE   { SERVICE <x-sparql-anything:>       { fx:properties                   fx:use-rdfs-member        true ;                   fx:content                \"[1,2,3]\" ;                   fx:reify-slot-statements  true ;                   fx:media-type       \"application/json\" .         ?s        ?p                  ?o       }   }");
//		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
		System.out.println(query.toString(Syntax.defaultSyntax));

//		query = QueryFactory.create("PREFIX ex: <http://example/> \n" +
//				"PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n" +
//				"PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n" +
//				"\n" +
//				"CONSTRUCT {\n" +
//				" ?bnode ex:p ?A\n" +
//				"} WHERE {\n" +
//				" SERVICE <x-sparql-anything:> {\n" +
//				"\tfx:properties fx:content \"c1,c2\\n" +
//				"b0,A\\n" +
//				"b0,B\\n" +
//				"b0,C\\n" +
//				"b0,D\\n" +
//				"b0,E\\n" +
//				"b1,A\\n" +
//				"b2,B\\n" +
//				"b3,C\\n" +
//				"b4,D\\n" +
//				"b5,E\" ; fx:media-type 'text/csv';  fx:csv.headers true .\n" +
//				" \t[] xyz:c1 ?b0 ; xyz:c2 ?A\n" +
//				" }\n" +
//				" BIND ( fx:bnode ( ?b0 ) as ?bnode ) \n" +
//				"}");
//
		m = QueryExecutionFactory.create(query, ds).execConstruct();
		m.setNsPrefix("xyz", "http://sparql.xyz/facade-x/data/");
		m.setNsPrefix("rdfs", RDFS.uri);
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		m.write(System.out, "TTL");
////		System.out.println(ResultSetFormatter.asText(QueryExecutionFactory.create(query, ds).execSelect()));
//		System.out.println(query.toString(Syntax.defaultSyntax));

	}

	public static void audit() {
		String queryString = "CONSTRUCT\n" + "  {\n" + "    GRAPH ?g { ?s ?p ?o }\n" + "  }\n" + "WHERE\n" + "  { SERVICE <x-sparql-anything:content=abc,audit=true,strategy=0> { \n " + "      GRAPH ?g { ?s  ?p  ?o } }\n" + "  } ";
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		System.out.println(query.toString(Syntax.defaultQuerySyntax));

		Dataset ds = DatasetFactory.createGeneral();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		Dataset dd = QueryExecutionFactory.create(query, ds).execConstructDataset();
		RDFDataMgr.write(System.out, dd, Lang.TRIG);

	}



	public static void main(String[] args) throws URISyntaxException, IOException {
		prefixes.put("xyz", "http://sparql.xyz/facade-x/data/");
		prefixes.put("fx", "http://sparql.xyz/facade-x/ns/");
		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");

		audit();
	}

}
