package io.github.sparqlanything.cli;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class ProfileTest {

	@Test
	public void profileWithFilePath() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String q = "SELECT * {  ?s ?p ?o OPTIONAL {?s a ?c} }";
		String filepath = "pp.tsv";
		String[] args = new String[]{"-q", q, "-c", "location=" + f, "-f", "CSV", "-profile", filepath};
		SPARQLAnything.callMain(args);
		Assert.assertTrue(new File(filepath).exists());
		new File(filepath).delete();
	}

	@Test
	public void profileWithDefaultFile() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String q = "SELECT * {  ?s ?p ?o OPTIONAL {?s a ?c} }";
		String filepath = "profile.tsv";
		String[] args = new String[]{"-q", q, "-c", "location=" + f, "-f", "CSV", "-profile"};
		SPARQLAnything.callMain(args);
		Assert.assertTrue(new File(filepath).exists());
		new File(filepath).delete();
	}

	@Test
	public void noProfile() throws Exception {
		String f = Objects.requireNonNull(getClass().getClassLoader().getResource("books.xml")).toURI().toString();
		String q = "SELECT * {  ?s ?p ?o OPTIONAL {?s a ?c} }";
		String filepath = "profile.tsv";
		String[] args = new String[]{"-q", q, "-c", "location=" + f, "-f", "CSV"};
		SPARQLAnything.callMain(args);
		Assert.assertFalse(new File(filepath).exists());
	}
}
