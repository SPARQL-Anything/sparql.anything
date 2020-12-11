package com.github.spiceh2020.sparql.anything.experiment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ComputeStats {

	final static String DELIMITER = "\" \t\n\r(){},;";

	public static Set<String> getTokens(File f) throws IOException {
		String q = FileUtils.readFileToString(f, Charset.defaultCharset());
		StringTokenizer st = new StringTokenizer(q, DELIMITER, false);
		Set<String> result = new HashSet<>();
		while (st.hasMoreElements()) {
			result.add(st.nextElement().toString());

		}

		return result;
	}

	public static List<String> getTokensAsList(File f) throws IOException {
		String q = FileUtils.readFileToString(f, Charset.defaultCharset());
		StringTokenizer st = new StringTokenizer(q, DELIMITER, false);
		List<String> result = new ArrayList<>();
		while (st.hasMoreElements()) {
			result.add(st.nextElement().toString());
		}

		return result;
	}

	public static void processFolder(String folder, String filename, int[] n, String extension) throws IOException {
		System.out.println(folder);
		System.out.println(
				"\tCompetency Question\tNumber of Distinct Tokens\tNumber of Tokens");
		for (int i = 0; i < n.length; i++) {
			File f = new File(folder + "/" + filename + n[i] + "." + extension);
			if (!f.getName().contains("_") && FilenameUtils.getExtension(f.getName()).equals(extension)) {
				System.out.println(
						"\t" + filename + n[i] + "\t" + getTokens(f).size() + "\t" + getTokensAsList(f).size());
			}
		}
	}

	public static void main(String[] args) throws IOException {
		String experimentFolder = args[0];
		int[] n = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		processFolder(experimentFolder + "sparql-anything-queries", "q", n, "rqg");
		processFolder(experimentFolder + "sparql-generate-queries", "q", n, "rqg");
		processFolder(experimentFolder + "rml-mappings", "m", new int[] { 1, 2, 3, 4 }, "ttl");
	}
}
