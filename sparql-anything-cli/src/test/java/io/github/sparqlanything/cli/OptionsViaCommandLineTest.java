package io.github.sparqlanything.cli;

import org.junit.Test;

public class OptionsViaCommandLineTest {

	@Test
	public void test() throws Exception {
		String q = "SELECT * { SERVICE <x-sparql-anything:> { ?s ?p ?o } }";
		String out = SPARQLAnything.callMain(new String[]{"-q", q, "-c", "content=abc"});
		System.out.println(out);
	}
}
