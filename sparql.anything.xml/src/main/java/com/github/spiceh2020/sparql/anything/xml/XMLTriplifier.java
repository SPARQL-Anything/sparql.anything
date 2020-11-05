package com.github.spiceh2020.sparql.anything.xml;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class XMLTriplifier implements Triplifier {

	public final static String propertyPrefix = "propertyPrefix", uriRoot = "uriRoot";

	@Override
	public Graph triplify(URL url, Properties properties) throws IOException {
		throw new IOException("Not implemented yet");
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/xml");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("xml");
	}
}
