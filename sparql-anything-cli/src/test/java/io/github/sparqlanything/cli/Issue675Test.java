package io.github.sparqlanything.cli;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Issue675Test {
	@Test
	public void numberedOutputForEachBindingSet() throws Exception {

		Path dir = Files.createTempDirectory("issueNNN");
		File out = dir.resolve("out.csv").toFile();

		SPARQLAnything.callMain(new String[]{
			"-q", "SELECT ?v WHERE { BIND($_x AS ?v) }",
			"-v", "x=a", "-v", "x=b", "-v", "x=c",
			"-f", "csv",
			"-o", out.getAbsolutePath()
		});

		Assert.assertFalse("base file should not be written", out.exists());

		Set<String> values = new HashSet<>();
		for (int i = 1; i <= 3; i++) {
			File f = dir.resolve("out-" + i + ".csv").toFile();
			Assert.assertTrue(f.getName() + " missing", f.exists());
			List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
			Assert.assertEquals("v", lines.get(0).trim());
			values.add(lines.get(1).trim());
		}
		Assert.assertEquals(Set.of("a", "b", "c"), values);
		Assert.assertFalse(dir.resolve("out-4.csv").toFile().exists());
	}
}
