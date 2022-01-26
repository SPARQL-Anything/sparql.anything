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

package com.github.sparqlanything.bib;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;

public class BibtexTriplifier implements Triplifier {

	public BibtexTriplifier() {

	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);
		String content = properties.getProperty(IRIArgument.CONTENT.toString());
		if (url == null && (content == null || content.isEmpty()))
			return;

		String root = Triplifier.getRootArgument(properties);
		String dataSourceId = root;
		String namespace = Triplifier.getNamespaceArgument(properties);

		builder.addRoot(dataSourceId, root);

		try {
			InputStream is = Triplifier.getInputStream(url, properties);
			BibTeXParser bibtexParser = new BibTeXParser();
			Reader reader = new InputStreamReader(is);
			BibTeXDatabase bibDB = bibtexParser.parse(reader);
			AtomicInteger count = new AtomicInteger();
			bibDB.getEntries().forEach((key, entry) -> {
				String containerIdChild = root + key;
				try {
					builder.addType(dataSourceId, containerIdChild, new URI(namespace + entry.getType().toString()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				builder.addContainer(dataSourceId, root, count.incrementAndGet(), containerIdChild);
				entry.getFields().forEach((keyField, valueField) -> {
					builder.addValue(dataSourceId, containerIdChild, keyField.toString(), valueField.toUserString());
				});
			});

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TriplifierHTTPException e) {
			e.printStackTrace();
		} catch (TokenMgrException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
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
