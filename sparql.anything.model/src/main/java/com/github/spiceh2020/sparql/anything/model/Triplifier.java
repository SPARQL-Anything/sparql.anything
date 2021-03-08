package com.github.spiceh2020.sparql.anything.model;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.sparql.core.DatasetGraph;

public interface Triplifier {
	String METADATA_GRAPH_IRI = "http://sparql.xyz/facade-x/data/metadata";
	String XYZ_NS = "http://sparql.xyz/facade-x/data/";
	String FACADE_X_CONST_NAMESPACE_IRI = "http://sparql.xyz/facade-x/ns/";
	String FACADE_X_TYPE_ROOT = FACADE_X_CONST_NAMESPACE_IRI + "root";

	public DatasetGraph triplify(URL url, Properties properties) throws IOException;

	public Set<String> getMimeTypes();

	public Set<String> getExtensions();

}
