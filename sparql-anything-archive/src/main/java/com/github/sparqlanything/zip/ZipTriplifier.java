/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.zip;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sparqlanything.model.Triplifier;

public class ZipTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(ZipTriplifier.class);
	public static final String MATCHES = "archive.matches";

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		// TODO Not implemented yet
		return triplify(properties);
	}

	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return dg;

		String root = Triplifier.getRootArgument(properties);
		Charset charset = Triplifier.getCharsetArgument(properties);
		boolean blank_nodes = Triplifier.getBlankNodeArgument(properties);
		String matches = properties.getProperty(MATCHES, ".*");

		logger.trace("BN nodes {}", blank_nodes);

		Node rootResource;
		if (!blank_nodes) {
			if (root == null) {
				rootResource = NodeFactory.createURI(url.toString());
			} else {
				rootResource = NodeFactory.createURI(root);
			}
		} else {
			rootResource = NodeFactory.createBlankNode();
		}

		Graph g = GraphFactory.createDefaultGraph();
		g.add(new Triple(rootResource, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT)));

		ZipInputStream zis = new ZipInputStream(url.openStream(), charset);
		ZipEntry ze;
		int i = 1;
		while ((ze = zis.getNextEntry()) != null) {
			if (ze.getName().matches(matches)) {
				g.add(new Triple(rootResource, RDF.li(i).asNode(), NodeFactory.createLiteral(ze.getName())));
				i++;
			}
		}

		dg.setDefaultGraph(g);
		dg.addGraph(NodeFactory.createURI(url.toString()), g);

		return dg;
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/zip");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("zip");
	}
}
