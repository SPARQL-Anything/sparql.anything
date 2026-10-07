package io.github.sparqlanything.cli;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Issue677Test {

	private static final String Q = "SELECT ?v WHERE { BIND($_x AS ?v) }";

	private List<String> values(String output) {
		return Arrays.stream(output.split("\\R"))
			.map(String::trim)
			.filter(l -> !l.isEmpty() && !l.equals("v"))
			.sorted()
			.collect(Collectors.toList());
	}

	@Test
	public void allBindingSetsPrintedToStdout() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{
			"-q", Q, "-f", "csv",
			"-v", "x=a", "-v", "x=b", "-v", "x=c"
		});
		Assert.assertEquals(List.of("a", "b", "c"), values(out));
	}

	@Test
	public void allRangeValuesPrintedToStdout() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{
			"-q", Q, "-f", "csv",
			"-v", "x=1...3"
		});
		Assert.assertEquals(List.of("1", "2", "3"), values(out));
	}

	@Test
	public void stdoutStillUsableAfterRun() throws Exception {
		SPARQLAnything.callMain(new String[]{"-q", Q, "-f", "csv", "-v", "x=a"});
		String out = SPARQLAnything.callMain(new String[]{"-q", Q, "-f", "csv", "-v", "x=b"});
		Assert.assertEquals(List.of("b"), values(out));
	}
}
