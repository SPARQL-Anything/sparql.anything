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

package com.github.sparqlanything.yaml;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class YAMLTriplifier implements Triplifier {

	private static final Logger logger = LoggerFactory.getLogger(YAMLTriplifier.class);

	private String[] getDataSources(URL url) {
		return new String[] { url.toString() };
	}

	private String getRootId(URL url, String dataSourceId, Properties properties) {
		return Triplifier.getRootArgument(properties);
	}

	protected void transform(URL url, Properties properties, FacadeXGraphBuilder builder)
		throws IOException, TriplifierHTTPException {

		final InputStream is = Triplifier.getInputStream(url, properties);

		LoadSettings settings = LoadSettings.builder().setLabel("Custom user configuration").build();
		Load load = new Load(settings);

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
			Iterable<Object> iter = load.loadAllFromInputStream(is);
			transformYAML(iter, dataSourceId, getRootId(url, dataSourceId, properties), builder);
		} finally {
			is.close();
		}
	}

	private void transformYAML(Iterable<Object> iter, String dataSourceId, String rootId, FacadeXGraphBuilder builder) {
		builder.addRoot(dataSourceId,rootId);
		Iterator<Object> iterator = iter.iterator();
		while(iterator.hasNext()){
			Object value = iterator.next();
			if(value instanceof String){
				builder.addValue(dataSourceId, rootId, (Integer) 1, value);
			} else if(value instanceof Integer){
				builder.addValue(dataSourceId, rootId, (Integer) 1, value);
			} else if(value instanceof Boolean){
				builder.addValue(dataSourceId, rootId, (Integer) 1, value);
			} else if(value instanceof Double){
				builder.addValue(dataSourceId, rootId, (Integer) 1, value);
			} else if(value instanceof Map){
				// Directly link to the map
				transformMap((Map)value, dataSourceId, rootId, builder);
			} else if(value instanceof List){
				List list = (List) value;
				for(int x=0; x<list.size(); x++){
					transformIntKeyValue(x+1, list.get(x), dataSourceId, rootId, builder);
				}
			} else {
				throw new UnsupportedOperationException("Unknown structure: " + value.getClass());
			}
		}
	}

	private void transformMap(Map<?,?> o, String dataSourceId, String containerId, FacadeXGraphBuilder builder) {
		for(Map.Entry<?,?> entry: o.entrySet()){
			Object key = entry.getKey();
			Object value = entry.getValue();
			transformStringKeyValue((String) key, value, dataSourceId, containerId, builder);
		}
	}


	private void transformStringKeyValue(String key, Object value, String dataSourceId, String containerId, FacadeXGraphBuilder builder) {
			key = Triplifier.toSafeURIString(key);
			if(value instanceof String){
				builder.addValue(dataSourceId, containerId, (String) key, value);
			} else if(value instanceof Integer){
				builder.addValue(dataSourceId, containerId, (String) key, value);
			} else if(value instanceof Boolean){
				builder.addValue(dataSourceId, containerId, (String) key, value);
			} else if(value instanceof Double){
				builder.addValue(dataSourceId, containerId, (String) key, value);
			} else if(value instanceof Map){
				String childId = containerId + "/" + key;
				builder.addContainer(dataSourceId, containerId,(String) key, childId);
				transformMap((Map) value, dataSourceId, childId, builder);
			} else if(value instanceof List){
				String childId = containerId + "/" + key;
				List list = (List) value;
				for(int x=0; x<list.size(); x++){
					transformIntKeyValue(x+1, list.get(x), dataSourceId, childId, builder);
				}
			} else {
				throw new UnsupportedOperationException("Unknown structure: " + value.getClass());
			}
	}

	private void transformIntKeyValue(int key, Object value, String dataSourceId, String containerId, FacadeXGraphBuilder builder) {
		if(value instanceof String){
			builder.addValue(dataSourceId, containerId, (Integer) key, value);
		} else if(value instanceof Integer){
			builder.addValue(dataSourceId, containerId, (Integer) key, value);
		} else if(value instanceof Boolean){
			builder.addValue(dataSourceId, containerId, (Integer) key, value);
		} else if(value instanceof Double){
			builder.addValue(dataSourceId, containerId, (Integer) key, value);
		} else if(value instanceof Map){
			String childId = containerId + "/" + key;
			builder.addContainer(dataSourceId, containerId,(Integer) key, childId);
			transformMap((Map) value, dataSourceId, childId, builder);
		} else if(value instanceof List){
			String childId = containerId + "/" + key;
			builder.addContainer(dataSourceId, containerId,(Integer) key, childId);
			List list = (List) value;
			for(int x=0; x<list.size(); x++){
				transformIntKeyValue(x+1, list.get(x), dataSourceId, childId, builder);
			}
		} else {
			throw new UnsupportedOperationException("Unknown structure: " + value.getClass());
		}
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder)
			throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);

		transform(url, properties, builder);

//		if (logger.isDebugEnabled()) {
//			logger.debug("Number of triples: {} ", builder.getMainGraph().size());
//		}
//		return builder.getDatasetGraph();
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/yaml", "text/yaml", "x-text/yaml");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("yaml");
	}
}
