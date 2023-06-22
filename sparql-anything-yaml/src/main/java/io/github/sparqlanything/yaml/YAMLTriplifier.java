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

package io.github.sparqlanything.yaml;

import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.SPARQLAnythingConstants;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class YAMLTriplifier implements Triplifier {

	protected void transform(Properties properties, FacadeXGraphBuilder builder)
		throws IOException, TriplifierHTTPException {

		final InputStream is = Triplifier.getInputStream(properties);

		LoadSettings settings = LoadSettings.builder().setLabel("Custom user configuration").build();
		Load load = new Load(settings);

		try {
			// Only 1 data source expected
			String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
			Iterable<Object> iter = load.loadAllFromInputStream(is);
			transformYAML(iter, dataSourceId, builder);
		} finally {
			is.close();
		}
	}

	private void transformYAML(Iterable<Object> iter, String dataSourceId, FacadeXGraphBuilder builder) {
		builder.addRoot(dataSourceId);
		for (Object value : iter) {
			if (value instanceof String) {
				builder.addValue(dataSourceId, SPARQLAnythingConstants.ROOT_ID, (Integer) 1, value);
			} else if (value instanceof Integer) {
				builder.addValue(dataSourceId, SPARQLAnythingConstants.ROOT_ID, (Integer) 1, value);
			} else if (value instanceof Boolean) {
				builder.addValue(dataSourceId, SPARQLAnythingConstants.ROOT_ID, (Integer) 1, value);
			} else if (value instanceof Double) {
				builder.addValue(dataSourceId, SPARQLAnythingConstants.ROOT_ID, (Integer) 1, value);
			} else if (value instanceof Map) {
				// Directly link to the map
				transformMap((Map) value, dataSourceId, SPARQLAnythingConstants.ROOT_ID, builder);
			} else if (value instanceof List) {
				List list = (List) value;
				for (int x = 0; x < list.size(); x++) {
					transformIntKeyValue(x + 1, list.get(x), dataSourceId, SPARQLAnythingConstants.ROOT_ID, builder);
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
			if( value == null){
				log.warn("skipping {} because value is null", key);
				continue;
			}
			transformStringKeyValue((String) key, value, dataSourceId, containerId, builder);
		}
	}


	private void transformStringKeyValue(String key, Object value, String dataSourceId, String containerId, FacadeXGraphBuilder builder) {
		if(value == null){
			log.warn("skipping {} because value is null", key);
			return;
		}
//		key = Triplifier.toSafeURIString(key);
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
				builder.addContainer(dataSourceId, containerId, (String) key, childId);
				for(int x=0; x<list.size(); x++){
					transformIntKeyValue(x+1, list.get(x), dataSourceId, childId, builder);
				}
			} else {
				throw new UnsupportedOperationException("Unknown structure: " + value.getClass());
			}
	}

	private void transformIntKeyValue(int key, Object value, String dataSourceId, String containerId, FacadeXGraphBuilder builder) {
		if(value == null){
			log.warn("skipping {} because value is null", key);
			return;
		}
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
		transform(properties, builder);
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
