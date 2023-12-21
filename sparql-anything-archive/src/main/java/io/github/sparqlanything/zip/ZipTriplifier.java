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

import io.github.sparqlanything.model.*;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;
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

@io.github.sparqlanything.model.annotations.Triplifier
public class ZipTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(ZipTriplifier.class);

	@Example(resource = "https://sparql-anything.cc/examples/example.tar", description = "Select and triplify only .csv and .txt files within the archive.", query = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX fx: <http://sparql.xyz/facade-x/ns/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> CONSTRUCT { ?s1 ?p1 ?o1 . } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/example.tar> { fx:properties fx:archive.matches \".*txt|.*csv\" . ?s fx:anySlot ?file1 SERVICE <x-sparql-anything:> { fx:properties fx:location ?file1 ; fx:from-archive \"https://sparql-anything.cc/examples/example.tar\" . ?s1 ?p1 ?o1 } } }")
	@Option( description = "It tells sparql.anything to evaluate a regular expression on the filenames within the archives. In this case the slots will be filled with the files that match the regex only.", validValues = "Any valid regular expression")
	public static final IRIArgument MATCHES = new IRIArgument("archive.matches",".*");

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);
		if(url == null){
			logger.warn("No location provided");
			return;
		}
		String dataSourceId = "";
		Charset charset = Triplifier.getCharsetArgument(properties);
		String matches = PropertyUtils.getStringProperty(properties, MATCHES);

		builder.addRoot(dataSourceId);

		ZipInputStream zis = new ZipInputStream(url.openStream(), charset);
		ZipEntry ze;
		int i = 1;
		while ((ze = zis.getNextEntry()) != null) {
			if (ze.getName().matches(matches)) {
				builder.addValue(dataSourceId, SPARQLAnythingConstants.ROOT_ID, i, NodeFactory.createLiteral(ze.getName()));
				i++;
			}
		}

		zis.close();
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
