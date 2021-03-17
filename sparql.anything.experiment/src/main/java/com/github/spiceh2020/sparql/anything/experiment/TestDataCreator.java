package com.github.spiceh2020.sparql.anything.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;

public class TestDataCreator {

	public static void main(String[] args) throws MalformedURLException, IOException {

		String outfolder = args[0]+"/";
		new File(outfolder).mkdir();

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
		System.out.println("Generated array of length "+arr.length());
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
		}
	}

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