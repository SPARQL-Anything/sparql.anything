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

package io.github.sparqlanything.bib;

import io.github.sparqlanything.model.*;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BibtexTriplifier implements Triplifier {
	private static final Logger logger = LoggerFactory.getLogger(BibtexTriplifier.class);
	public BibtexTriplifier() {

	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		String root = Triplifier.getRootArgument(properties);
		String dataSourceId = root;
		String namespace = PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE);

		builder.addRoot(dataSourceId, root);

		try (InputStream is = Triplifier.getInputStream(properties)){

			BibTeXParser bibtexParser = new BibTeXParser();
			Reader reader = new InputStreamReader(is);
			BibTeXDatabase bibDB = bibtexParser.parse(reader);
			AtomicInteger count = new AtomicInteger();
			bibDB.getEntries().forEach((key, entry) -> {
				String containerIdChild = root + key;
				try {
					builder.addType(dataSourceId, containerIdChild, new URI(namespace + entry.getType().toString()));
				} catch (URISyntaxException e) {
					logger.error("",e);
				}
				builder.addContainer(dataSourceId, root, count.incrementAndGet(), containerIdChild);
				entry.getFields().forEach((keyField, valueField) -> {
					builder.addValue(dataSourceId, containerIdChild, keyField.toString(), valueField.toUserString());
				});
			});

		} catch (IOException|TriplifierHTTPException|TokenMgrException|ParseException e) {
			logger.error("", e);
			throw new IOException(e);
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/x-bibtex");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("bib", "bibtex");
	}

}
