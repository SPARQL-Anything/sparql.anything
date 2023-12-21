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

package io.github.sparqlanything.binary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import io.github.sparqlanything.model.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.NodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryTriplifier implements Triplifier {

	private static final Logger logger = LoggerFactory.getLogger(BinaryTriplifier.class);

//	public static final String ENCODING = "bin.encoding";
	public static final IRIArgument ENCODING = new IRIArgument("bin.encoding", Encoding.BASE64.name());

	public static enum Encoding {
		BASE64
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);
		if (url == null) {
			logger.warn("No location provided");
			return;
		}
		String encodingString = PropertyUtils.getStringProperty(properties, ENCODING);
		Encoding encoding = Encoding.valueOf(encodingString);
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;

		String value;
		byte[] file = downloadUrl(url);

		switch (encoding) {
		case BASE64:
			value = Base64.encodeBase64String(file);
			break;
		default:
			value = Base64.encodeBase64String(file);
			break;
		}
		// Add root
		builder.addRoot(dataSourceId);
		// Add content
		builder.addValue(dataSourceId, SPARQLAnythingConstants.ROOT_ID, 1, NodeFactory.createLiteralByValue(value, XSDDatatype.XSDbase64Binary));
	}

	private byte[] downloadUrl(URL toDownload) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			byte[] chunk = new byte[4096];
			int bytesRead;
			InputStream stream = toDownload.openStream();

			while ((bytesRead = stream.read(chunk)) > 0) {
				outputStream.write(chunk, 0, bytesRead);
			}

			stream.close();

			byte[] result = outputStream.toByteArray();
			outputStream.close();
			return result;
		} catch (IOException e) {
			logger.error("",e);
			return null;
		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/octet-stream");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("bin", "dat");
	}
}
