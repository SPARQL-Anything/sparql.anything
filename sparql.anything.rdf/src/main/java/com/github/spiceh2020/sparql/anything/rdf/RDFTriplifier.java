package com.github.spiceh2020.sparql.anything.rdf;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class RDFTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(RDFTriplifier.class);

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		// TODO Not implemented yet
		return triplify(properties);
	}

	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return DatasetGraphFactory.create();

		DatasetGraph dg = DatasetGraphFactory.create();
		logger.info("URL {}", url.toString());
		RDFDataMgr.read(dg, url.toString());
		return dg;
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/rdf+thrift", "application/trix+xml", "application/n-quads", "text/trig",
				"application/owl+xml", "text/turtle", "application/rdf+xml", "application/n-triples",
				"application/ld+json");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("rdf", "ttl", "nt", "jsonld", "owl", "trig", "nq", "trix", "trdf");
	}

}
