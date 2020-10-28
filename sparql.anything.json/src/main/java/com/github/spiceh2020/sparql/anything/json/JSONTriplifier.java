package com.github.spiceh2020.sparql.anything.json;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;

import com.github.spiceh2020.json2rdf.transformers.JSONTransformer;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class JSONTriplifier implements Triplifier {

	public final static String propertyPrefix = "propertyPrefix", uriRoot = "uriRoot";

	@Override
	public Graph triplify(URL url, Properties properties) throws IOException {
		JSONTransformer jt;
		if (properties.containsKey(propertyPrefix)) {
			jt = new JSONTransformer(properties.getProperty(propertyPrefix));
		} else {
			jt = new JSONTransformer(url.toString() + "/");
		}
		if (properties.containsKey(uriRoot)) {
			jt.setURIRoot(properties.getProperty(uriRoot));
		}

		Model m = jt.transformJSONFromURL(url);
		Graph g = DatasetFactory.create(m).asDatasetGraph().getDefaultGraph();
		return g;
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/json");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("json");
	}
}
