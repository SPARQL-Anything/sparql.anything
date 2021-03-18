package com.github.spiceh2020.sparql.anything.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import com.github.spiceh2020.sparql.anything.model.TripleFilteringModel;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.RDF;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class JSONTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(JSONTriplifier.class);

	private String propertyPrefix, uriRoot;
	private boolean useBlankNodes = true;

//	private Model transformJSONFile(File input) throws IOException {
//		BufferedReader br = new BufferedReader(new FileReader(input));
//		StringBuilder sb = new StringBuilder();
//		br.lines().forEachOrdered(l -> sb.append(l));
//		br.close();
//		return transformJSON(br.toString());
//	}

	private void transformJSONFromURL(URL url, TripleFilteringModel filter) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

		if (propertyPrefix == null) {
			propertyPrefix = url.toString() + "/";
		}
		StringBuilder sb = new StringBuilder();
		br.lines().forEachOrdered(l -> {
			sb.append(l);
			sb.append('\n');
		});
		br.close();

		transformJSON(sb.toString(), filter);
	}

	private void reset() {
		propertyPrefix = null;
		uriRoot = null;
		useBlankNodes = true;
	}

	private void transformJSON(String json, TripleFilteringModel filter) {
		checkParameters();
		try {
			transform(new JSONObject(json), filter);
		} catch (JSONException e) {
			transform(new JSONArray(json), filter);
		}
	}

	private void checkParameters() {
		if (propertyPrefix == null)
			throw new RuntimeException("The property prefix can't be null");
	}

	public void transform(JSONObject object, TripleFilteringModel filter) {
		//Model m = ModelFactory.createDefaultModel();
		Resource root = createResource(uriRoot);
		filter.add(root, RDF.type, ResourceFactory.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		transform(object, root, filter);
//		return m;
	}

	public void transform(JSONArray arr, TripleFilteringModel filter) {
//		Model m = ModelFactory.createDefaultModel();
		Resource root = createResource(uriRoot);
		filter.add(root, RDF.type, ResourceFactory.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		transform(arr, root, filter);
//		return m;
	}

	private void transform(JSONObject object, Resource r, TripleFilteringModel filter) {
//		m.add(r, RDF.type, RDFS.Resource);
		object.keys().forEachRemaining(k -> {
			Object o = object.get(k);
			Property p = ResourceFactory.createProperty(propertyPrefix + Triplifier.toSafeURIString(k));
//			m.add(p, RDFS.label, m.createTypedLiteral(k));
			if (o instanceof String || o instanceof Boolean || o instanceof Integer) {
				transformPrimites(r, p, o, filter);
			} else if (o instanceof JSONObject) {
				transformJSONObject(r, p, (JSONObject) o, filter);
			} else if (o instanceof JSONArray) {
				transformArray(r, p, (JSONArray) o, filter);
			}
		});
	}

	private void transform(JSONArray arr, Resource r, TripleFilteringModel filter) {
//		m.add(r, RDF.type, RDF.Seq);
		for (int i = 0; i < arr.length(); i++) {
			Object o = arr.get(i);
			Property p = RDF.li(i + 1);
			if (o instanceof String || o instanceof Boolean || o instanceof Integer) {
				transformPrimites( r, p, o, filter);
			} else if (o instanceof JSONObject) {
				transformJSONObject( r, p, (JSONObject) o, filter);
			} else if (o instanceof JSONArray) {
				transformArray( r, p, (JSONArray) o, filter);
			}
		}
		;
	}

	private void transformArray(Resource r, Property p, JSONArray o, TripleFilteringModel filter) {
		Resource seq = createResource(r.getURI() + "/" + p.getLocalName());
		filter.add(r, p, seq);
		transform(o, seq, filter);
	}

	private void transformJSONObject(Resource r, Property p, JSONObject o, TripleFilteringModel filter) {
		Resource rnew = createResource(r.getURI() + "/" + p.getLocalName());
		filter.add(r, p, rnew);
		transform(o, rnew, filter);
	}

	private void transformPrimites(Resource r, Property p, Object o, TripleFilteringModel filter) {
		filter.add(r, p, ResourceFactory.createTypedLiteral(o));
	}

	private Resource createResource(String path) {
		if (useBlankNodes) {
			return ResourceFactory.createResource();
		} else {
			return ResourceFactory.createResource(path);
		}

	}

	public void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
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
		this.uriRoot = Triplifier.getRootArgument(properties, url);
//		Charset charset = getCharsetArgument(properties);
		useBlankNodes = Triplifier.getBlankNodeArgument(properties);
		propertyPrefix = Triplifier.getNamespaceArgument(properties);

		transformJSONFromURL(url, filter);
		logger.info("Number of triples " + filter.getMainGraph().size());
		// FIXME quick and dirty solution for resetting fields
		reset();
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
