package com.github.spiceh2020.sparql.anything.json;

import com.github.spiceh2020.sparql.anything.model.TripleFilteringModel;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

public class JSONTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(JSONTriplifier.class);

	private void transformJSONFromURL(URL url, String rootId, TripleFilteringModel filter) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

		StringBuilder sb = new StringBuilder();
		br.lines().forEachOrdered(l -> {
			sb.append(l);
			sb.append('\n');
		});
		br.close();

		transformJSON(sb.toString(), url.toString(), rootId, filter);
	}

	private void transformJSON(String json, String dataSourceId, String rootId, TripleFilteringModel filter) {
		filter.addRoot(dataSourceId, rootId);
		try {
			transform( new JSONObject(json), dataSourceId, rootId, filter);
		} catch (JSONException e) {
			transform( new JSONArray(json), dataSourceId, rootId, filter);
		}
	}

	private void transform( JSONObject object, String dataSourceId, String containerId, TripleFilteringModel filter) {
		object.keys().forEachRemaining(k -> {
			Object o = object.get(k);
			if (o instanceof String || o instanceof Boolean || o instanceof Integer) {
				filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), o);
			} else if(o instanceof JSONObject || o instanceof JSONArray) {
				String childContainerId = containerId + "/" + Triplifier.toSafeURIString(k);
				filter.addContainer(dataSourceId, containerId, Triplifier.toSafeURIString(k), childContainerId);
				if (o instanceof JSONObject) {
					transform((JSONObject) o, dataSourceId, childContainerId, filter);
				} else if (o instanceof JSONArray) {
					transform((JSONArray) o, dataSourceId, childContainerId, filter);
				}
			}
		});
	}

	private void transform( JSONArray arr, String dataSourceId, String containerId, TripleFilteringModel filter) {
		for (int i = 0; i < arr.length(); i++) {
			Object o = arr.get(i);
			if (o instanceof String || o instanceof Boolean || o instanceof Integer) {
				filter.addValue(dataSourceId, containerId, i+1, o);
			} else if(o instanceof JSONObject || o instanceof JSONArray) {
				String childContainerId = containerId + "/_" + String.valueOf(i+1);
				filter.addContainer(dataSourceId, containerId, i+1, childContainerId);
				if (o instanceof JSONObject) {
					transform((JSONObject) o, dataSourceId, childContainerId, filter);
				} else if (o instanceof JSONArray) {
					transform((JSONArray) o, dataSourceId, childContainerId, filter);
				}
			}
		}
	}

	@Deprecated
	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		return triplify(url, properties, null);
	}

	@Override
	public DatasetGraph triplify(URL url, Properties properties, Op op) throws IOException {
		logger.trace("Triplifying ", url.toString());
		logger.trace("Op ", op);
		TripleFilteringModel filter = new TripleFilteringModel(url, op, properties);
		transformJSONFromURL(url, Triplifier.getRootArgument(properties, url), filter);
		logger.info("Number of triples " + filter.getMainGraph().size());
		return filter.getDatasetGraph();
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
