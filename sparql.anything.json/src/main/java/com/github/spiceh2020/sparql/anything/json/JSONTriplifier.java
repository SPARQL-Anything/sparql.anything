package com.github.spiceh2020.sparql.anything.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
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

	private Model transformJSONFromURL(URL url) throws IOException {
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

		return transformJSON(sb.toString());
	}

	private void reset() {
		propertyPrefix = null;
		uriRoot = null;
		useBlankNodes = true;
	}

	private Model transformJSON(String json) {
		checkParameters();
		try {
			return getModel(new JSONObject(json));
		} catch (JSONException e) {
			return getModel(new JSONArray(json));
		}
	}

	private void checkParameters() {
		if (propertyPrefix == null)
			throw new RuntimeException("The property prefix can't be null");
	}

	public Model getModel(JSONObject object) {
		Model m = ModelFactory.createDefaultModel();
		Resource root = createResource(uriRoot);
		m.add(root, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		getModel(object, root, m);
		return m;
	}

	public Model getModel(JSONArray arr) {
		Model m = ModelFactory.createDefaultModel();
		Resource root = createResource(uriRoot);
		m.add(root, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		getModel(arr, root, m);
		return m;
	}

	private void getModel(JSONObject object, Resource r, Model m) {
//		m.add(r, RDF.type, RDFS.Resource);
		object.keys().forEachRemaining(k -> {
			Object o = object.get(k);
			Property p = m.createProperty(propertyPrefix + Triplifier.toSafeURIString(k));
//			m.add(p, RDFS.label, m.createTypedLiteral(k));
			if (o instanceof String || o instanceof Boolean || o instanceof Integer) {
				transformPrimites(m, r, p, o);
			} else if (o instanceof JSONObject) {
				transformJSONObject(m, r, p, (JSONObject) o);
			} else if (o instanceof JSONArray) {
				transformArray(m, r, p, (JSONArray) o);
			}
		});
	}

	private void getModel(JSONArray arr, Resource r, Model m) {
//		m.add(r, RDF.type, RDF.Seq);
		for (int i = 0; i < arr.length(); i++) {
			Object o = arr.get(i);
			Property p = RDF.li(i + 1);
			if (o instanceof String || o instanceof Boolean || o instanceof Integer) {
				transformPrimites(m, r, p, o);
			} else if (o instanceof JSONObject) {
				transformJSONObject(m, r, p, (JSONObject) o);
			} else if (o instanceof JSONArray) {
				transformArray(m, r, p, (JSONArray) o);
			}
		}
		;
	}

	private void transformArray(Model m, Resource r, Property p, JSONArray o) {
		Resource seq = createResource(r.getURI() + "/" + p.getLocalName());
		m.add(r, p, seq);
		getModel(o, seq, m);
	}

	private void transformJSONObject(Model m, Resource r, Property p, JSONObject o) {
		Resource rnew = createResource(r.getURI() + "/" + p.getLocalName());
		m.add(r, p, rnew);
		getModel(o, rnew, m);
	}

	private void transformPrimites(Model m, Resource r, Property p, Object o) {
		m.add(r, p, m.createTypedLiteral(o));
	}

	private Resource createResource(String path) {
		if (useBlankNodes) {
			return ModelFactory.createDefaultModel().createResource();
		} else {
			return ModelFactory.createDefaultModel().createResource(path);
		}

	}

	public void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
	}

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		logger.trace("Triplifying " + url.toString());
		
		this.uriRoot = getRootArgument(properties, url);
//		Charset charset = getCharsetArgument(properties);
		useBlankNodes = getBlankNodeArgument(properties);
		propertyPrefix = getNamespaceArgument(properties, url);

		Model m = transformJSONFromURL(url);
		logger.trace("Number of triples " + m.size());
		DatasetGraph dg = DatasetFactory.create(m).asDatasetGraph();
		dg.addGraph(NodeFactory.createURI(url.toString()), dg.getDefaultGraph());
		// FIXME quick and dirty solution for resetting fields
		reset();
		return dg;
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
