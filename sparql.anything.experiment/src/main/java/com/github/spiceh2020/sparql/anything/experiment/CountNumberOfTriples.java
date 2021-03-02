package com.github.spiceh2020.sparql.anything.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

public class CountNumberOfTriples {

	public static void main(String[] args) {
		String folder = args[0];

		File f = new File(folder);
		List<String> fileNames = new ArrayList<>();
		for (File f1 : f.listFiles()) {
			fileNames.add(f1.getName());
		}

		fileNames.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		for (String filename : fileNames) {
			Model m = ModelFactory.createDefaultModel();
			RDFDataMgr.read(m, folder +"/"+ filename);
			System.out.println(filename + "\t\t\t" + m.size());
		}

	}

}
