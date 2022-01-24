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

package com.github.sparqlanything.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.core.DatasetGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;

public class JSONTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(JSONTriplifier.class);

	private String[] getDataSources(URL url) {
		return new String[] { url.toString() };
	}

	private String getRootId(URL url, String dataSourceId, Properties properties) {
		return Triplifier.getRootArgument(properties);
	}

	private void transform(URL url, Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {

		final InputStream us = Triplifier.getInputStream(url, properties);

		JsonFactory factory = JsonFactory.builder().build();
		JsonParser parser = factory.createParser(us);

		try {
			// Only 1 data source expected
			String dataSourceId;
			if (properties.containsKey(IRIArgument.ROOT.toString())) {
				logger.trace("Setting Data source Id using Root argument");
				dataSourceId = properties.getProperty(IRIArgument.ROOT.toString());
			} else if (properties.containsKey(IRIArgument.CONTENT.toString())) {
				logger.trace("Setting Data source Id using Content argument");
				dataSourceId = Triplifier.XYZ_NS
						+ DigestUtils.md5Hex(properties.getProperty(IRIArgument.CONTENT.toString()));
			} else {
				dataSourceId = getDataSources(url)[0];
			}
			transformJSON(parser, dataSourceId, getRootId(url, dataSourceId, properties), builder);
		} finally {
			us.close();
		}
	}

	private void transformJSON(JsonParser parser, String dataSourceId, String rootId, FacadeXGraphBuilder builder)
			throws IOException {

		builder.addRoot(dataSourceId, rootId);
		logger.trace("Transforming json (dataSourceId {} rootId {})", dataSourceId, rootId);
		JsonToken token = parser.nextToken();
		if (token == JsonToken.START_OBJECT) {
			logger.trace("Transforming object");
			transformObject(parser, dataSourceId, rootId, builder);
		} else if (token == JsonToken.START_ARRAY) {
			logger.trace("Transforming array");
			transformArray(parser, dataSourceId, rootId, builder);
		}

	}

	private void transformArray(JsonParser parser, String dataSourceId, String containerId, FacadeXGraphBuilder builder)
			throws IOException {
		int i = 0;
		JsonToken token;

		while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
			switch (token) {
			case START_ARRAY:
				String childContainerIdarr = StringUtils.join(containerId, "/_", String.valueOf(i + 1));
				builder.addContainer(dataSourceId, containerId, i + 1, childContainerIdarr);
				transformArray(parser, dataSourceId, childContainerIdarr, builder);
				break;
			case START_OBJECT:
				String childContainerId = StringUtils.join(containerId, "/_", String.valueOf(i + 1));
				builder.addContainer(dataSourceId, containerId, i + 1, childContainerId);
				transformObject(parser, dataSourceId, childContainerId, builder);
				break;
			case VALUE_FALSE:
			case VALUE_TRUE:
				builder.addValue(dataSourceId, containerId, i + 1, parser.getValueAsBoolean());
				break;
			case VALUE_NUMBER_FLOAT:
				builder.addValue(dataSourceId, containerId, i + 1, parser.getValueAsDouble());
				break;
			case VALUE_NUMBER_INT:
				builder.addValue(dataSourceId, containerId, i + 1, parser.getValueAsInt());
				break;
			case VALUE_STRING:
				builder.addValue(dataSourceId, containerId, i + 1, parser.getValueAsString());
				break;
			case VALUE_NULL:
			case END_ARRAY:
			case END_OBJECT:
			case FIELD_NAME:
			case VALUE_EMBEDDED_OBJECT:
			case NOT_AVAILABLE:
			default:
				// NOP
				break;

			}
			i++;
		}

	}

	private void transformObject(JsonParser parser, String dataSourceId, String containerId,
			FacadeXGraphBuilder builder) throws IOException {

		JsonToken token;

		while ((token = parser.nextToken()) != JsonToken.END_OBJECT) {

			if (token == JsonToken.FIELD_NAME) {
				String k = parser.getText();
				token = parser.nextToken();

				switch (token) {
				case START_ARRAY:
					String childContainerIdArr = StringUtils.join(containerId, "/", Triplifier.toSafeURIString(k));
					builder.addContainer(dataSourceId, containerId, Triplifier.toSafeURIString(k), childContainerIdArr);
					transformArray(parser, dataSourceId, childContainerIdArr, builder);
					break;
				case START_OBJECT:
					String childContainerId = StringUtils.join(containerId, "/", Triplifier.toSafeURIString(k));
					builder.addContainer(dataSourceId, containerId, Triplifier.toSafeURIString(k), childContainerId);
					transformObject(parser, dataSourceId, childContainerId, builder);
					break;
				case VALUE_NUMBER_FLOAT:
					logger.trace("{} float", k);
					builder.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k),
							parser.getValueAsDouble());
					break;
				case VALUE_NUMBER_INT:
					logger.trace("{} int", k);
					builder.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), parser.getValueAsInt());
					break;
				case VALUE_STRING:
					builder.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k),
							parser.getValueAsString());
					break;
				case VALUE_FALSE:
				case VALUE_TRUE:
					builder.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k),
							parser.getValueAsBoolean());
					break;
				case END_ARRAY:
				case END_OBJECT:
				case FIELD_NAME:
				case VALUE_EMBEDDED_OBJECT:
				case NOT_AVAILABLE:
				case VALUE_NULL:
				default:
					break;
				}

			}
		}

	}

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);
//		logger.trace("Triplifying ", url.toString());

		transform(url, properties, builder);

//		if (logger.isDebugEnabled()) {
//			logger.debug("Number of triples: {} ", builder.getMainGraph().size());
//		}
		return builder.getDatasetGraph();
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/json");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("json");
	}
}
