package com.github.spiceh2020.sparql.anything.model;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.jena.graph.Graph;

public interface Triplifier {

	public Graph triplify(URL url) throws IOException;

	public List<String> getParameters();

	public void setParameters(Properties properties);
}
