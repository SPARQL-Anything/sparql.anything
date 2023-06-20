/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.zip;

import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FolderTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(FolderTriplifier.class);


	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		URL url = Triplifier.getLocation(properties);
		if(url == null){
			logger.warn("No location provided");
			return;
		}
		String root = Triplifier.getRootArgument(properties);
		String dataSourceId = "";
		String matches = properties.getProperty(ZipTriplifier.MATCHES, ".*");

		logger.trace("Matches {}", matches);

		builder.addRoot(dataSourceId, root);

		try {
			Path path = Paths.get(url.toURI());
			AtomicInteger i = new AtomicInteger(1);
			Files.walk(path).forEach(p -> {
				logger.trace("{} matches? {}", p.toString(), path.toString().matches(matches));
				if (p.toString().matches(matches)) {
					builder.addValue(dataSourceId, root, i.getAndIncrement(), p.toUri().toString());
				}
			});

		} catch (URISyntaxException e) {
			logger.error("",e);
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet();
	}
}
