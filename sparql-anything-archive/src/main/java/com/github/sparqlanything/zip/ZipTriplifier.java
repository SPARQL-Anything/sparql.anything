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

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(ZipTriplifier.class);
	public static final String MATCHES = "archive.matches";

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);
		if(url == null){
			logger.warn("No location provided");
			return;
		}
		String root = Triplifier.getRootArgument(properties);
		String dataSourceId = root;
		Charset charset = Triplifier.getCharsetArgument(properties);
		String matches = properties.getProperty(MATCHES, ".*");

		builder.addRoot(dataSourceId, root);

		ZipInputStream zis = new ZipInputStream(url.openStream(), charset);
		ZipEntry ze;
		int i = 1;
		while ((ze = zis.getNextEntry()) != null) {
			if (ze.getName().matches(matches)) {
				builder.addValue(dataSourceId, root, i, NodeFactory.createLiteral(ze.getName()));
				i++;
			}
		}
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
