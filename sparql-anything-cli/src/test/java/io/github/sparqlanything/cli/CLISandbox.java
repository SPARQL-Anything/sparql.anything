package io.github.sparqlanything.cli;

import org.junit.Test;

import java.util.Objects;

public class CLISandbox {

	@Test
	public void profile() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String q = "SELECT * {  ?s ?p ?o OPTIONAL {?s a ?c} }";
		String[] args = new String[]{"-q", q, "-c", "location=" + f, "-f", "CSV", "-profile", "pp.tsv"};
		SPARQLAnything.callMain(args);
	}
}
