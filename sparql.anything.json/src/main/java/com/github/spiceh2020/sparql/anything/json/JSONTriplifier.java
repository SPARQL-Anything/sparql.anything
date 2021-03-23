package com.github.spiceh2020.sparql.anything.json;

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import com.github.spiceh2020.sparql.anything.model.TripleFilteringFacadeXBuilder;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class JSONTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(JSONTriplifier.class);

	private void transformJSONFromURL(URL url, String rootId, FacadeXGraphBuilder filter) throws IOException {
		JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
		JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
		JsonIterator json = JsonIterator.parse(url.openStream().readAllBytes());
		transformJSON(json, url.toString(), rootId, filter);
	}

//	private void any(String jsoon)

	private void transformJSON(JsonIterator json, String dataSourceId, String rootId, FacadeXGraphBuilder filter) throws IOException {

		filter.addRoot(dataSourceId, rootId);
		Any any = json.readAny();

		if(any.valueType().equals(ValueType.OBJECT)) {
			Map<String, Any> object = any.asMap();
			transform(object, dataSourceId, rootId, filter);
		} else {
			List<Any> object = any.asList();
			transform( object, dataSourceId, rootId, filter);
		}
	}

	private void transform( Map<String,Any> object, String dataSourceId, String containerId, FacadeXGraphBuilder filter) {
		object.keySet().iterator().forEachRemaining(k -> {
			Any o = object.get(k);
			if(o.valueType().equals(ValueType.OBJECT) || o.valueType().equals(ValueType.ARRAY)) {
				String childContainerId = StringUtils.join(containerId, "/", Triplifier.toSafeURIString(k));
				filter.addContainer(dataSourceId, containerId, Triplifier.toSafeURIString(k), childContainerId);
				if (o.valueType().equals(ValueType.OBJECT)) {
					transform(o.asMap(), dataSourceId, childContainerId, filter);
				} else {
					transform( o.asList(), dataSourceId, childContainerId, filter);
				}
			}else
				// Value
			if(((Any) o).valueType().equals(ValueType.STRING)){
				filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toString());
			}else
			if(((Any) o).valueType().equals(ValueType.BOOLEAN)){
				filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toBoolean());
			}else
			if(((Any) o).valueType().equals(ValueType.NUMBER)){
//				filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toString());
				if(o.toString().contains(".")){
					filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toFloat());
				} else {
					filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toInt());
				}
			}
		});
	}

	private void transform( List<Any> arr, String dataSourceId, String containerId, FacadeXGraphBuilder filter) {
		for (int i = 0; i < arr.size(); i++) {
			Any o = arr.get(i);
			if(o.valueType().equals(ValueType.OBJECT) || o.valueType().equals(ValueType.ARRAY)) {
				String childContainerId = StringUtils.join(containerId, "/_", String.valueOf(i+1));
				filter.addContainer(dataSourceId, containerId, i+1, childContainerId);
				if (o.valueType().equals(ValueType.OBJECT)) {
					transform(o.asMap(), dataSourceId, childContainerId, filter);
				} else {
					transform(o.asList(), dataSourceId, childContainerId, filter);
				}
			}else
			if(((Any) o).valueType().equals(ValueType.STRING)){
				filter.addValue(dataSourceId, containerId, i+1, ((Any) o).toString());
			}else
			if(((Any) o).valueType().equals(ValueType.BOOLEAN)){
				filter.addValue(dataSourceId, containerId, i+1, ((Any) o).toBoolean());
			}else
			if(((Any) o).valueType().equals(ValueType.NUMBER)){
				log.info("Type: {} {}", o.valueType(), o.getClass());
				if(o.toString().contains(".")){
					filter.addValue(dataSourceId, containerId, i+1, ((Any) o).toFloat());
				} else {
					filter.addValue(dataSourceId, containerId, i+1, ((Any) o).toInt());
				}
			}
		}
	}

	@Deprecated
	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		return triplify(url, properties, null);
	}

	@Override
	public DatasetGraph triplify(URL url, Properties properties, Op op) throws IOException {
		logger.trace("Triplifying ", url.toString());
		logger.trace("Op ", op);
		FacadeXGraphBuilder filter = new TripleFilteringFacadeXBuilder(url, op, properties);
		transformJSONFromURL(url, Triplifier.getRootArgument(properties, url), filter);
		if(logger.isDebugEnabled()){
			logger.debug("Number of triples: {} ", filter.getMainGraph().size());
		}
		return filter.getDatasetGraph();
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
