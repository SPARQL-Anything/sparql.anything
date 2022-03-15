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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.Slice;
import com.github.sparqlanything.model.Slicer;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;

public class JSONTriplifier implements Triplifier, Slicer {

	private static Logger logger = LoggerFactory.getLogger(JSONTriplifier.class);

//	private String[] getDataSources(URL url) {
//		return new String[] { url.toString() };
//	}
//
//	private String getRootId(URL url, String dataSourceId, Properties properties) {
//		return Triplifier.getRootArgument(properties);
//	}

	private void transform(URL url, Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {

		final InputStream us = Triplifier.getInputStream(properties);

		JsonFactory factory = JsonFactory.builder().build();
		JsonParser parser = factory.createParser(us);

		try {
			// Only 1 data source expected
			String dataSourceId = Triplifier.getRootArgument(properties);
//			if (properties.containsKey(IRIArgument.ROOT.toString())) {
//				logger.trace("Setting Data source Id using Root argument");
//				dataSourceId = properties.getProperty(IRIArgument.ROOT.toString());
//			} else if (properties.containsKey(IRIArgument.CONTENT.toString())) {
//				logger.trace("Setting Data source Id using Content argument");
//				dataSourceId = Triplifier.XYZ_NS
//						+ DigestUtils.md5Hex(properties.getProperty(IRIArgument.CONTENT.toString()));
//			} else {
//				dataSourceId = Triplifier.getRootArgument(properties); //getDataSources(url)[0];
//			}
			transformJSON(parser, dataSourceId, Triplifier.getRootArgument(properties), builder);
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
	private void transformArrayItem(int i, JsonToken token, JsonParser parser, String dataSourceId, String containerId, FacadeXGraphBuilder builder)
			throws IOException {
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
	}
	private void transformArray(JsonParser parser, String dataSourceId, String containerId, FacadeXGraphBuilder builder)
			throws IOException {
		int i = 0;
		JsonToken token;

		while ((token = parser.nextToken()) != END_ARRAY) {
			transformArrayItem(i, token, parser, dataSourceId, containerId, builder);
			i++;
		}

	}

	private void transformObject(JsonParser parser, String dataSourceId, String containerId,
			FacadeXGraphBuilder builder) throws IOException {

		JsonToken token;
		Integer coercedInt;
		String coercedStr;

		while ((token = parser.nextToken()) != END_OBJECT) {
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
						coercedInt = null;
						coercedStr = null;
						Boolean kIsInteger = true; // assume it is
						try {
							coercedInt = parser.getValueAsInt();
						} catch (Exception e) { // could tighten this to com.fasterxml.jackson.core.exc.InputCoercionException
							logger.warn("{} can not be parsed as an integer -- treating it as a string", k);
							kIsInteger = false;
							coercedStr = parser.getValueAsString();
						}
						builder.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k),
								kIsInteger ? coercedInt : coercedStr);
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
			}else{
				throw new IOException("Unexpected token in object");
			}
		}

	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);
//		logger.trace("Triplifying ", url.toString());

		transform(url, properties, builder);

//		if (logger.isDebugEnabled()) {
//			logger.debug("Number of triples: {} ", builder.getMainGraph().size());
//		}
//		return builder.getDatasetGraph();
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/json");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("json");
	}

	@Override
	public Iterable<Slice> slice(Properties properties) throws IOException, TriplifierHTTPException {
		final InputStream us = Triplifier.getInputStream(properties);

		JsonFactory factory = JsonFactory.builder().build();
		JsonParser parser = factory.createParser(us);

		JsonToken token = parser.nextToken();
		// If the root is an array.
		if(token == JsonToken.START_ARRAY){

		} else {
			throw new IOException("Not a JSON array");
		}

		try {
			// Only 1 data source expected
			String rootId = Triplifier.getRootArgument(properties);
			String dataSourceId = rootId;
			return new Iterable<Slice>() {
				JsonToken next = null;
				@Override
				public Iterator<Slice> iterator() {
					log.debug("Iterating slices");
					return new Iterator<Slice>() {
						int sln = 0;

						@Override
						public boolean hasNext() {
							if(next != null){
								return true;
							}
							try {
								next = parser.nextToken();
								while(next == JsonToken.END_ARRAY || next == END_OBJECT){
									next = parser.nextToken();
								}
							} catch (IOException e) {
								next = null;
								return false;
							}
							if(next != null){
								return true;
							}else{
								return false;
							}

						}

						@Override
						public Slice next() {
							if(next == null){
								return null;
							}
							sln++;
							log.trace("next slice: {}", sln);
							JsonToken tk = next;
							next = null;
							return JSONSlice.makeSlice(tk, parser, sln, rootId, dataSourceId);
						}
					};
				}
			};
		}finally{
			us.close();
		}
	}

	private void processSlice(int iteration, String rootId, String dataSourceId, JsonToken token, JsonParser parser, FacadeXGraphBuilder builder){
		String sliceContainerId = StringUtils.join(rootId , "#slice" , iteration);
		builder.addContainer(dataSourceId, rootId, iteration, sliceContainerId);

	}
							@Override
	public void triplify(Slice slice, Properties p, FacadeXGraphBuilder builder) {
		JSONSlice jslice = (JSONSlice) slice;
		try {
			builder.addRoot(jslice.getDatasourceId(), jslice.getRootId());
			// Method is 0-indexed
			transformArrayItem(jslice.iteration() - 1, jslice.get(), jslice.getParser(), jslice.getDatasourceId(), jslice.getRootId(), builder);
		} catch (IOException e) {
			log.error("An error occurred while transforming slice {}: {}", slice.iteration(), e);
		}
	}
}
