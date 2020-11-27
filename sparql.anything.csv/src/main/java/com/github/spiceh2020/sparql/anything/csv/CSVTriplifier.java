package com.github.spiceh2020.sparql.anything.csv;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.core.DatasetGraph;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class CSVTriplifier implements Triplifier {

	public final static String propertyPrefix = "propertyPrefix", uriRoot = "uriRoot";

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		throw new IOException("Not implemented yet");
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("text/csv");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("csv");
	}
}
