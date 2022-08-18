/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sparqlanything.zip;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.Triplifier;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

public class TarTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(TarTriplifier.class);

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		URL location = Triplifier.getLocation(properties);
		if(location == null){
			logger.warn("No location provided");
			return;
		}
		Charset charset = Triplifier.getCharsetArgument(properties);
		String root = Triplifier.getRootArgument(properties);
		String dataSourceId = root;
		String matches = properties.getProperty(ZipTriplifier.MATCHES, ".*");

		logger.trace("Matches {}", matches);

//		Graph g = GraphFactory.createDefaultGraph();
		builder.addRoot(dataSourceId, root);

		try {
			TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
					.createArchiveInputStream("tar", location.openStream(), charset.toString());
			int i = 1;
			TarArchiveEntry entry = null;
			while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {

				if (entry.getName().matches(matches)) {
					builder.addValue(dataSourceId, root, i, entry.getName());
					i++;
				}

			}

		} catch (ArchiveException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/x-tar");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("tar");
	}
}
