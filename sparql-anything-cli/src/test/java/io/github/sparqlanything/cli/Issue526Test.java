package io.github.sparqlanything.cli;

import org.junit.Test;

public class Issue526Test {
	String rq = getClass().getClassLoader().getResource("./issue526.rq").getFile();
	String csv = getClass().getClassLoader().getResource("./issue526.csv").getFile();
	@Test
	public void test() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			rq,
			"-c",
			"location=" + csv
		});
	}
}
