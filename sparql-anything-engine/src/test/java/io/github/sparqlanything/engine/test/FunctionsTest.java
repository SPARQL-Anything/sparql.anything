/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.WordUtils;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FunctionsTest {

	private final static Logger log = LoggerFactory.getLogger(FunctionsTest.class);

	public ResultSet execute(String queryString) {

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Dataset kb = DatasetFactory.createGeneral();
		log.trace("Query string\n{}\n", queryString);
		Query q = QueryFactory.create(queryString);
		log.trace("\nQuery parsed \n{}\n", q.toString(Syntax.syntaxSPARQL_11));
		return QueryExecutionFactory.create(q, kb).execSelect();
	}

	@Test
	public void next() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?three WHERE {"
				+ "BIND(fx:next(rdf:_2) as ?three)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String threeUri = result.next().get("three").asResource().getURI();
		Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#_3", threeUri);
	}

	@Test
	public void levenshteinDistance() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> SELECT ?result WHERE { BIND (fx:LevenshteinDistance(\"abc\", \"cbe\") AS ?result) } ";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		int dist = result.next().get("result").asLiteral().getInt();
		Assert.assertEquals(2, dist);
	}

	@Test
	public void levenshteinDistanceURI() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> SELECT ?result WHERE { BIND (fx:LevenshteinDistance(<abc>, <cbe>) AS ?result) } ";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		int dist = result.next().get("result").asLiteral().getInt();
		Assert.assertEquals(2, dist);
	}

	@Test
	public void cosineDistance() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> SELECT ?result WHERE { BIND (fx:CosineDistance(\"abc\", \"cbe\") AS ?result) } ";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		double dist = result.next().get("result").asLiteral().getDouble();
		Assert.assertEquals(1.0, dist, 0.01);
	}

	@Test
	public void jaccardDistance() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> SELECT ?result WHERE { BIND (fx:JaccardDistance(\"abc\", \"cbe\") AS ?result) } ";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		double dist = result.next().get("result").asLiteral().getDouble();
		Assert.assertEquals(0.4, dist, 0.01);
	}

	@Test
	public void jaroWinklerDistance() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> SELECT ?result WHERE { BIND (fx:JaroWinklerDistance(\"abc\", \"cbe\") AS ?result) } ";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		double dist = result.next().get("result").asLiteral().getDouble();
		Assert.assertEquals(0.24, dist, 0.01);
	}
	@Test
	public void longestCommonSubsequenceDistance() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> SELECT ?result WHERE { BIND (fx:LongestCommonSubsequenceDistance(\"abc\", \"abe\") AS ?result) } ";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		int dist = result.next().get("result").asLiteral().getInt();
		Assert.assertEquals(2, dist);
	}
	@Test
	public void hammingDistance() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> SELECT ?result WHERE { BIND (fx:HammingDistance(\"abc\", \"abe\") AS ?result) } ";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		int dist = result.next().get("result").asLiteral().getInt();
		Assert.assertEquals(1, dist);
	}

	@Test
	public void previous() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?three WHERE {"
				+ "BIND(fx:previous(rdf:_4) as ?three)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String threeUri = result.next().get("three").asResource().getURI();
		Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#_3", threeUri);
	}

	@Test
	public void after() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?true WHERE {"
				+ "BIND(fx:after(rdf:_4, rdf:_3) as ?true)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String trueStr = result.next().get("true").asNode().toString();
		Assert.assertEquals("\"true\"^^http://www.w3.org/2001/XMLSchema#boolean", trueStr);
	}

	@Test
	public void before() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?true WHERE {"
				+ "BIND(fx:before(rdf:_2, rdf:_3) as ?true)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String trueStr = result.next().get("true").asNode().toString();
		Assert.assertEquals("\"true\"^^http://www.w3.org/2001/XMLSchema#boolean", trueStr);
	}

	@Test
	public void forward() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?seven WHERE {"
				+ "BIND(fx:forward(rdf:_2, 5) as ?seven)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String sevenUri = result.next().get("seven").asResource().getURI();
		Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#_7", sevenUri);
	}

	@Test
	public void backward() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?twenty WHERE {"
				+ "BIND(fx:backward(rdf:_24, 4) as ?twenty)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String twentyUri = result.next().get("twenty").asResource().getURI();
		Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#_20", twentyUri);
	}

	@Test
	public void cardinal() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?four WHERE {"
				+ "BIND ( fx:cardinal ( rdf:_4 ) as ?four )" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		int four = result.next().get("four").asLiteral().getInt();
		Assert.assertEquals(4, four);
	}

	@Test
	public void isContainerMembershipProperty() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?resultTrue ?resultFalse WHERE { BIND ( fx:isContainerMembershipProperty ( rdf:_42 ) as ?resultTrue )  BIND ( fx:isContainerMembershipProperty ( 42 ) as ?resultFalse )}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		QuerySolution qs = result.next();
		boolean rt = qs.get("resultTrue").asLiteral().getBoolean();
		boolean rf = qs.get("resultFalse").asLiteral().getBoolean();
		Assert.assertTrue(rt);
		Assert.assertFalse(rf);
	}

	@Test
	public void substring1() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?ob WHERE {"
				+ "BIND(fx:String.substring(\"bob\", 1) as ?ob)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String ob = result.next().get("ob").asLiteral().getString();
		Assert.assertEquals("ob", ob);
	}

	@Test
	public void substring2() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?bo WHERE {"
				+ "BIND(fx:String.substring(\"bob\", 0, 2) as ?bo)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String bo = result.next().get("bo").asLiteral().getString();
		Assert.assertEquals("bo", bo);
	}

	@Test
	public void trim() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?bob WHERE {"
				+ "BIND(fx:String.trim(\" bob \") as ?bob)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		String bob = result.next().get("bob").asLiteral().getString();
		Assert.assertEquals("bob", bob);
	}

	@Test
	public void indexOf() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?one WHERE {"
				+ "BIND(fx:String.indexOf(\"bob\", \"o\") as ?one)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		int one = result.next().get("one").asLiteral().getInt();
		Assert.assertEquals(1, one);
	}

	@Test
	public void lastIndexOf() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?two WHERE {"
				+ "BIND(fx:String.lastIndexOf(\"bob\", \"b\") as ?two)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());
		int two = result.next().get("two").asLiteral().getInt();
		Assert.assertEquals(2, two);
	}


	@Test
	public void serial_1() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?one ?two ?three WHERE {"
				+ "BIND(fx:serial(\"c\") as ?one)" + "BIND(fx:serial(\"c\") as ?two)"
				+ "BIND(fx:serial(\"c\") as ?three)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());

		QuerySolution s = result.next();
		int one = s.get("one").asLiteral().getInt();
		int two = s.get("two").asLiteral().getInt();
		int three = s.get("three").asLiteral().getInt();
		assertEquals(1, one);
		assertEquals(2, two);
		Assert.assertEquals(three, 3);
	}

	@Test
	public void serial_2() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?one ?two ?three WHERE {"
				+ "BIND(fx:serial(\"a\") as ?one)" + "BIND(fx:serial(\"b\") as ?two)"
				+ "BIND(fx:serial(\"c\") as ?three)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());

		QuerySolution s = result.next();
		int one = s.get("one").asLiteral().getInt();
		int two = s.get("two").asLiteral().getInt();
		int three = s.get("three").asLiteral().getInt();
		assertEquals(1, one);
		assertEquals(1, two);
		assertEquals(1, three);
	}

	@Test
	public void serial_3() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "SELECT ?one ?two ?three WHERE {"
				+ "BIND(fx:serial(\"a\", \"b\", \"c\") as ?one)" + "BIND(fx:serial(\"a\", \"b\", \"c\") as ?two)"
				+ "BIND(fx:serial(\"a\", \"b\", \"c\") as ?three)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());

		QuerySolution s = result.next();
		int one = s.get("one").asLiteral().getInt();
		int two = s.get("two").asLiteral().getInt();
		int three = s.get("three").asLiteral().getInt();
		assertEquals(1, one);
		assertEquals(2, two);
		assertEquals(3, three);
	}

	@Test
	public void serial_4() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "PREFIX ex: <http://example.org/>\n"
				+ "" + "SELECT ?one ?two ?three WHERE {" + "VALUES(?v1 ?v2){ ( ex:1_1 ex:1_2 ) ( ex:2_1 ex:2_2 ) }"
				+ "BIND(fx:serial(?v1, ?v2) as ?one)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());

		// Two rows, both ?one == 1
		QuerySolution s = result.next();
		int one = s.get("one").asLiteral().getInt();
		assertEquals(1, one);
		s = result.next();
		one = s.get("one").asLiteral().getInt();
		assertEquals(1, one);
	}

	@Test
	public void serial_5() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "PREFIX ex: <http://example.org/>\n"
				+ "" + "SELECT ?c WHERE {"
				+ "VALUES (?v1 ?v2) { ( ex:1_1 ex:1_2 ) ( ex:1_1 ex:1_2 ) ( ex:1_1 ex:1_2 )  ( ex:1_1 ex:1_2 ) }"
				+ "BIND(fx:serial(?v1, ?v2) as ?c)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());

		// 4 rows, ?c = 1, 2, 3, 4
		QuerySolution s = result.next();
		int c = s.get("c").asLiteral().getInt();
		assertEquals(1, c);
		s = result.next();
		c = s.get("c").asLiteral().getInt();
		assertEquals(2, c);
		s = result.next();
		c = s.get("c").asLiteral().getInt();
		assertEquals(3, c);
		s = result.next();
		c = s.get("c").asLiteral().getInt();
		assertEquals(4, c);

	}

	@Test
	public void serial_6() {
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "PREFIX ex: <http://example.org/>\n"
				+ "" + "SELECT ?c WHERE {"
				+ "VALUES (?v1 ?v2) { ( ex:1_1 ex:1_2 ) ( ex:1_1 ex:1_2 ) ( ex:XXXX ex:YYYY )  ( ex:1_1 ex:1_2 ) }"
				+ "BIND(fx:serial(?v1, ?v2) as ?c)" + "}";
		ResultSet result = execute(q);
		Assert.assertTrue(result.hasNext());

		// 4 rows, ?c = 1, 2, 1, 3
		QuerySolution s = result.next();
		int c = s.get("c").asLiteral().getInt();
		assertEquals(1, c);
		s = result.next();
		c = s.get("c").asLiteral().getInt();
		assertEquals(2, c);
		s = result.next();
		c = s.get("c").asLiteral().getInt();
		assertEquals(1, c);
		s = result.next();
		c = s.get("c").asLiteral().getInt();
		assertEquals(3, c);

	}

	public void testStringFunction(String functionURI, String expectedResult, String testString) {
		testStringFunction(functionURI, expectedResult, testString, null);
	}

	public void testStringFunction(String functionURI, String expectedResult, String testString, List<String> arguments) {
		String argString = "";
		if(arguments!=null){
			arguments.replaceAll(s->{
				if(s.matches("-?\\d+")){
					return s;
				}
				return "\"".concat(s).concat("\"");
			});
			argString = ",".concat(String.join(",", arguments));
		}
		String q = "PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n" + "SELECT ?result WHERE {\n\tBIND(" + functionURI
				+ "(\"" + testString + "\""+argString+") as ?result)" + "\n}";
		ResultSet result = execute(q);
		assertEquals(expectedResult, result.next().get("result").asLiteral().getValue().toString());
	}

	@Test
	public void testHashFunctions() {
		testStringFunction("fx:DigestUtils.md2Hex", DigestUtils.md2Hex("test"), "test");
		testStringFunction("fx:DigestUtils.md5Hex", DigestUtils.md5Hex("test"), "test");
		testStringFunction("fx:DigestUtils.sha1Hex", DigestUtils.sha1Hex("test"), "test");
		testStringFunction("fx:DigestUtils.sha256Hex", DigestUtils.sha256Hex("test"), "test");
		testStringFunction("fx:DigestUtils.sha384Hex", DigestUtils.sha384Hex("test"), "test");
		testStringFunction("fx:DigestUtils.sha512Hex", DigestUtils.sha512Hex("test"), "test");
	}
	
	@Test
	public void testWordUtilsFunctions() {
		testStringFunction("fx:WordUtils.capitalizeFully", WordUtils.capitalizeFully("test"), "test");
		testStringFunction("fx:WordUtils.capitalize", WordUtils.capitalize("test"), "test");
		testStringFunction("fx:WordUtils.initials", WordUtils.initials("test"), "test");
		testStringFunction("fx:WordUtils.swapCase", WordUtils.swapCase("swapCase"), "swapCase");
		testStringFunction("fx:WordUtils.uncapitalize", WordUtils.uncapitalize("TEST"), "TEST");
	}

	@Test
	public void testStringReplace(){
		testStringFunction("fx:String.replace", "test".replace("te", "fe"), "test", Lists.newArrayList("te", "fe"));
	}
	@Test
	public void testStringFunctions(){
		testStringFunction("fx:String.trim", " test ".trim(), "test");
		testStringFunction("fx:String.substring", "test".substring(1), "test", Lists.newArrayList("1"));
		testStringFunction("fx:String.substring", "test".substring(1,2), "test", Lists.newArrayList("1", "2"));
		testStringFunction("fx:String.indexOf", String.valueOf("test".indexOf("e")), "test", Lists.newArrayList("e"));
		testStringFunction("fx:String.startsWith", String.valueOf("test".startsWith("te")), "test", Lists.newArrayList("te"));
		testStringFunction("fx:String.endsWith", String.valueOf("test".endsWith("st")), "test", Lists.newArrayList("st"));
		testStringFunction("fx:String.replace", "test".replace("t", "f"), "test", Lists.newArrayList("t", "f"));
		testStringFunction("fx:String.replace", "test".replace("te", "fe"), "test", Lists.newArrayList("te", "fe"));
		testStringFunction("fx:String.strip", "  test  \t".strip(), "  test  \t");
		testStringFunction("fx:String.stripLeading", "  test  \t".stripLeading(), "  test  \t");
		testStringFunction("fx:String.stripTrailing", "  test  \t".stripTrailing(), "  test  \t");
		testStringFunction("fx:String.lastIndexOf", String.valueOf("test".lastIndexOf("t")), "test", Lists.newArrayList("t"));
	}

	public void execTestEntityFunction(String expectedResult, String... str) {
		StringBuilder sb = new StringBuilder();
		sb.append("" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
				"PREFIX fx: <http://sparql.xyz/facade-x/ns/>\n" +
				"SELECT ?result WHERE {" + "BIND( fx:entity ( ");
		boolean first = true;
		for(String s: str){
			if(first){
				first = false;
			}else{
				sb.append(",");
			}
			sb.append(s);
		}
		sb.append(") as ?result)" + "}");
		String q = sb.toString();
		ResultSet result = execute(q);
		assertEquals(expectedResult, result.next().get("result").asResource().getURI());
	}

	@Test
	public void testEntityFunction(){
		execTestEntityFunction("http://sparql.xyz/facade-x/ns/type/position/1",
			"fx:" , "\"type/\"", "\"position\"", "\"/\"", "1"
		);

		execTestEntityFunction("http://www.example.org/type/person/enrico",
				"<http://www.example.org/>" , "\"type/\"", "\"person\"", "\"/\"", "\"enrico\"^^xsd:string"
		);

		execTestEntityFunction("http://www.example.org/1/10#100",
				"<http://www.example.org/>" , "rdf:_1", "\"/\"", "rdf:_10", "\"#\"", "\"100\"^^xsd:int"
		);
	}
}
