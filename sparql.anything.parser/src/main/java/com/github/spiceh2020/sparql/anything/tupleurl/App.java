package com.github.spiceh2020.sparql.anything.tupleurl;

public class App {

	private static void testURL() {
		String tupleURL = "tuple:https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/sparql.anything.engine/src/main/resources/test.json";
		TupleURLParser p = new TupleURLParser(tupleURL);
		System.out.println(p.getProperties().toString());

	}
	
	private static void testParam() {
		String tupleURL = "tuple:useBlankNodes=true,propertyPrefix=https://w3id.org/spice/properties/,location=https://raw.githubusercontent.com/spice-h2020/sparql.everything/main/sparql.anything.engine/src/main/resources/test.json";
		TupleURLParser p = new TupleURLParser(tupleURL);
		System.out.println(p.getProperties().toString());
	}

	public static void main(String[] args) {
		testURL();
		testParam();
	}
}
