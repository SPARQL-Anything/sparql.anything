package com.github.spiceh2020.sparql.anything.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class TestDataCreatorForISWC2021 {

	private static String queriesFolderName = "queries";

	public static void main(String[] args) throws MalformedURLException, IOException {

		String outfolder = args[0] + "/";
		new File(outfolder).mkdir();

		File queriesFolder = new File(outfolder + queriesFolderName);
		if (queriesFolder.exists()) {
			FileUtils.deleteDirectory(queriesFolder);
		}
		queriesFolder.mkdir();

		JSONArray arr = new JSONArray();
		String[] files = {
				"https://raw.githubusercontent.com/SPARQL-Anything/sparql.anything/main/experiment/data/COLLEZIONI_FONDO_GABINIO_MARZO_2017.json",
				"https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_GAM.json",
				"https://raw.githubusercontent.com/spice-h2020/sparql.anything/main/experiment/data/COLLEZIONI_MAO.json" };

		for (String f : files) {
			JSONArray l = readFileFromURL(new URL(f));
			System.out.println(f + " - " + l.length());
			for (int i = 0; i < l.length(); i++) {
				arr.put(l.get(i));
			}
		}
		System.out.println("Generated array of length " + arr.length());
		int c = arr.length();
		while (c < 1000000) {
			for (int i = 0; i < arr.length(); i++) {
				arr.put(arr.get(i));
				c++;
				if (c == 1000000)
					break;
			}
		}

		System.out.println(arr.length());

		int[] sizes = { 10, 100, 1000, 10000, 100000 };
		FileWriter fwa = new FileWriter(new File(outfolder + "1000000.json"));
		arr.write(fwa);
		fwa.flush();
		fwa.close();
		
		createQueriesForArray(arr,   "1000000.json", queriesFolder.getAbsolutePath());

		for (int s : sizes) {
			System.out.println("Computing " + s);
			JSONArray toWrite = new JSONArray();
			for (int i = 0; i < s; i++) {
				toWrite.put(arr.get(i));
			}
			System.out.println(toWrite.length());
			FileWriter fw = new FileWriter(new File(outfolder + s + ".json"));
			toWrite.write(fw);
			fw.flush();
			fw.close();
			System.out.println("Written");
			
			createQueriesForArray(toWrite,  s + ".json", queriesFolder.getAbsolutePath());
		}

	}

	private static void createQueriesForArray(JSONArray ja, String filename, String queriesFolder) throws IOException {
		JSONObject jo = ja.getJSONObject(ja.length() / 2);
		// SPARQL ANything
		String q1 = String.format(q1Template, filename, jo.get("Autore"));
		writeFile(q1, new File(queriesFolder + "/q1_" + ja.length() + ".sparql"));
		String q2 = String.format(q2Template, filename, jo.get("Tecnica"));
		writeFile(q2, new File(queriesFolder + "/q2_" + ja.length() + ".sparql"));
		String q3 = String.format(q3Template, filename, jo.get("Datazione"));
		writeFile(q3, new File(queriesFolder + "/q3_" + ja.length() + ".sparql"));
		// SPARQL Generate
		{
			 q1 = String.format(q1TemplateG, filename, jo.get("Autore"));
			writeFile(q1, new File(queriesFolder + "/q1_" + ja.length() + ".rqg"));
			 q2 = String.format(q2TemplateG, filename, jo.get("Tecnica"));
			writeFile(q2, new File(queriesFolder + "/q2_" + ja.length() + ".rqg"));
			 q3 = String.format(q3TemplateG, filename, jo.get("Datazione"));
			writeFile(q3, new File(queriesFolder + "/q3_" + ja.length() + ".rqg"));

		}
	}

	private static void writeFile(String content, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes());
		fos.flush();
		fos.close();
	}

	private static String q1Template = "SELECT ?titolo\n" + "WHERE{\n" + "\n"
			+ "	SERVICE <x-sparql-anything:location=%s> {\n"
			+ "		?s <http://sparql.xyz/facade-x/data/Autore> \"%s\" .\n"
			+ "		?s <http://sparql.xyz/facade-x/data/Titolo> ?titolo .\n" + "	}\n" + "}\n";
	private static String q2Template = "SELECT DISTINCT ?technique\n" + "WHERE{\n" + "\n"
			+ "	SERVICE <x-sparql-anything:location=%s> {\n"
			+ "		?s <http://sparql.xyz/facade-x/data/Tecnica> ?technique .\n"
			+ "		FILTER(REGEX(?technique,\".*%s.*\",\"i\"))\n" + "	}\n" + "}\n";

	private static String q3Template = "SELECT ?titolo\n" + "WHERE {\n"
			+ "	SERVICE <x-sparql-anything:location=%s> {\n"
			+ "		?s <http://sparql.xyz/facade-x/data/Datazione> ?date .\n"
			+ "		?s <http://sparql.xyz/facade-x/data/Titolo> ?titolo .\n"
			+ "		FILTER(REGEX(?date,\".*%s.*\",\"i\"))\n" + "	}\n" + "}\n" + "";

	private static String q1TemplateG = "" +
			"PREFIX ite: <http://w3id.org/sparql-generate/iter/>\n" +
			"\n" +
			"SELECT ?titolo\n" +
			"ITERATOR ite:JSONPath(<%s>,\"$[?(@.Autore=='%s')].Titolo\") AS ?titolo\n" +
			"WHERE{}\n";
//			"" +
//			"PREFIX ite: <http://w3id.org/sparql-generate/iter/>\n" +
//			"\n" +
//			"SELECT ?titolo\n" +
//			"ITERATOR ite:JSONPath(<%s>,\"$[*]\",\"$.Autore\",\"$.Titolo\") AS ?obj ?autore ?titolo\n" +
//			"WHERE{\n" +
//			"\tFILTER(REGEX(?autore,\".*%s.*\",\"i\"))\n" +
//			"}";
	private static String q2TemplateG = "PREFIX ite: <http://w3id.org/sparql-generate/iter/>\n" +
			"\n" +
			"SELECT DISTINCT ?technique\n" +
			"ITERATOR ite:JSONPath(<%s>,\"$[*].Tecnica\") AS ?technique \n" +
			"WHERE{\n" +
			"  FILTER(REGEX(?technique,\".*%s.*\",\"i\"))\n" +
			"}";
	private static String q3TemplateG = "PREFIX ite: <http://w3id.org/sparql-generate/iter/>\n" +
			"\n" +
			"SELECT DISTINCT ?titolo\n" +
			"ITERATOR ite:JSONPath(<%s>,\"$[*]\",\"@.Datazione\",\"@.Titolo\") AS ?obj ?date ?titolo\n" +
			"WHERE{\n" +
			"  FILTER(REGEX(?date,\".*%s.*\",\"i\"))\n" +
			"}\n";

	public static JSONArray readFileFromURL(URL url) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

		StringBuilder sb = new StringBuilder();
		br.lines().forEachOrdered(l -> {
			sb.append(l);
			sb.append('\n');
		});
		br.close();

		JSONArray arr = new JSONArray(sb.toString());
		return arr;

	}

}