package com.github.spiceh2020.sparql.anything;

import java.net.URL;
import java.util.List;

import org.apache.jena.graph.Graph;

public interface Triplifier {

	public Graph triplify(URL url);

	public List<String> getParameters();

	public void setParameter(String key, String value);
}
