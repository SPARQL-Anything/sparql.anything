package com.github.spiceh2020.sparql.anything.html;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.RDF;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class HTMLTriplifier implements Triplifier {

	private static final Logger log = LoggerFactory.getLogger(HTMLTriplifier.class);
	private static final String PROPERTY_SELECTOR = "html.selector";
	private static final String HTML_NS = "http://www.w3.org/1999/xhtml#";

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		String namespace = properties.getProperty(IRIArgument.NAMESPACE.toString(), url.toString() + "#");
		String root = properties.getProperty(IRIArgument.ROOT.toString(), url.toString() + "#");
		String charset = properties.getProperty(IRIArgument.CHARSET.toString(), "UTF-8");
		String selector = properties.getProperty(PROPERTY_SELECTOR, ":root");

		log.trace("namespace {}\n root {}\ncharset {}\nselector {}", namespace, root, charset, selector);

		Document doc;
		// If location is a http or https, raise exception if status is not 200
		log.debug("Loading URL: {}", url);
		if(url.getProtocol().equals("http") ||url.getProtocol().equals("https")) {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			log.debug("Response code: {}", conn.getResponseCode());
			if(conn.getResponseCode() != 200){
				throw new IOException(HttpStatus.getStatusText(conn.getResponseCode()));
			}
			doc = Jsoup.parse(conn.getInputStream(), charset, url.toString());
		} else {
			doc = Jsoup.parse(url.openStream(), charset, url.toString());
		}
		Model model = ModelFactory.createDefaultModel();
		// log.info(doc.title());
		Elements elements = doc.select(selector);
		Resource rootResource = null;
		if (elements.size() > 1) {
			// Create a root container
			rootResource = model.createResource(root);
			rootResource.addProperty(RDF.type, model.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		}
		int counter = 0;
		for (Element element : elements) {
			counter++;
			if (elements.size() > 1) {
				// link to root container
				Resource elResource = toResource(model, element);
				rootResource.addProperty(RDF.li(counter), elResource);
			}else{
				// Is root container
				model.add(toResource(model, element), RDF.type, model.createResource(Triplifier.FACADE_X_TYPE_ROOT));
			}
			populate(model, element, namespace);
		}
		DatasetGraph dg = DatasetFactory.create(model).asDatasetGraph();
		dg.addGraph(NodeFactory.createURI(url.toString()), model.getGraph());
		return dg;
	}

	private void populate(Model model, Element element, String namespace) {

		String tagName = element.tagName(); // tagname is the type
		Resource resource = toResource(model, element);
		resource.addProperty(RDF.type, ResourceFactory.createResource(HTML_NS + tagName));
		// attributes
		for (Attribute attribute : element.attributes()) {
			String key = attribute.getKey();
			String value = attribute.getValue();
			resource.addProperty(ResourceFactory.createProperty(HTML_NS + key), model.createLiteral(value));
		}
		// Children
		int counter = 0;
		for (Element child : element.children()) {
			counter++;
			resource.addProperty(RDF.li(counter), toResource(model, child));
			populate(model, child, namespace);
		}

		if (element.hasText() && element.ownText().length() > 0) {
			counter++;
			resource.addProperty(RDF.li(counter), element.ownText());
		}
	}

	private Resource toResource(Model model, Element element) {
		return model.createResource(new AnonId(Integer.toHexString(element.hashCode())));
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("text/html");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("html");
	}
}
