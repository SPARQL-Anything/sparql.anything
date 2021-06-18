package com.github.spiceh2020.sparql.anything.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import com.github.spiceh2020.sparql.anything.model.TripleFilteringFacadeXBuilder;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;

public class JSONTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(JSONTriplifier.class);
	private static final byte[] BUFF = new byte[1024];

	private void transformJSONFromURL(URL url, String rootId, Properties properties, Charset charset,
			FacadeXGraphBuilder filter) throws IOException {
		JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
		JsonStream.setMode(EncodingMode.DYNAMIC_MODE);

//		JsonIterator json = JsonIterator.parse(url.openStream().readAllBytes());
		JsonIterator json = JsonIterator.parse(BUFF);

//		final InputStream us = url.openStream();
		try {
			final InputStream us = Triplifier.getInputStream(url, properties, charset);

			// XXX We need to do this roundtrip since JsonIterator does not seem to properly
			// unescape \\uXXXX - to be investigated.
			final InputStream stream = IOUtils.toInputStream(
					new UnicodeUnescaper().translate(IOUtils.toString(us, StandardCharsets.UTF_8)),
					StandardCharsets.UTF_8);
			try {
				json.reset(stream);
				transformJSON(json, url.toString(), rootId, filter);
			} finally {
				stream.close();
				us.close();
			}
		} catch (ArchiveException e) {
			logger.warn("Error {}", e.getMessage());
		}
	}

//	private void any(String jsoon)

	private void transformJSON(JsonIterator json, String dataSourceId, String rootId, FacadeXGraphBuilder filter)
			throws IOException {

		filter.addRoot(dataSourceId, rootId);
//		Any any = json.readAny();
//
//		if(any.valueType().equals(ValueType.OBJECT)) {
//			Map<String, Any> object = any.asMap();
//			transform(object, dataSourceId, rootId, filter);
//		} else {
//			List<Any> object = any.asList();
//			transform( object, dataSourceId, rootId, filter);
//		}
		if (json.whatIsNext().equals(ValueType.OBJECT)) {
			Map<String, Any> object = json.readAny().asMap();
			transform(object, dataSourceId, rootId, filter);
		} else {
			int i = 0;
			while (json.readArray()) {
				transformArrayElement(json.readAny(), i, dataSourceId, rootId, filter);
				i++;
			}
		}
	}

	private void transform(Map<String, Any> object, String dataSourceId, String containerId,
			FacadeXGraphBuilder filter) {
		object.keySet().iterator().forEachRemaining(k -> {
			Any o = object.get(k);
			if (o.valueType().equals(ValueType.OBJECT) || o.valueType().equals(ValueType.ARRAY)) {
				String childContainerId = StringUtils.join(containerId, "/", Triplifier.toSafeURIString(k));
				filter.addContainer(dataSourceId, containerId, Triplifier.toSafeURIString(k), childContainerId);
				if (o.valueType().equals(ValueType.OBJECT)) {
					transform(o.asMap(), dataSourceId, childContainerId, filter);
				} else {
					transform(o.asList(), dataSourceId, childContainerId, filter);
				}
			} else
			// Value
			if (((Any) o).valueType().equals(ValueType.STRING)) {
				filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toString());
			} else if (((Any) o).valueType().equals(ValueType.BOOLEAN)) {
				filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toBoolean());
			} else if (((Any) o).valueType().equals(ValueType.NUMBER)) {
//				filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toString());
				if (o.toString().contains(".")) {
					filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toFloat());
				} else if (o.toString().contains("0")) {
					// There is a bug with Jsoniter when the int number contains a zero, we do the cast ourselves in that case
					filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), Integer.valueOf(o.toString().trim()));
				} else {
					filter.addValue(dataSourceId, containerId, Triplifier.toSafeURIString(k), ((Any) o).toInt());
				}
			}
		});
	}

//	private void transform( List<Any> arr, String dataSourceId, String containerId, FacadeXGraphBuilder filter) {
//		for (int i = 0; i < arr.size(); i++) {
//			Any o = arr.get(i);
//			if(o.valueType().equals(ValueType.OBJECT) || o.valueType().equals(ValueType.ARRAY)) {
//				String childContainerId = StringUtils.join(containerId, "/_", String.valueOf(i+1));
//				filter.addContainer(dataSourceId, containerId, i+1, childContainerId);
//				if (o.valueType().equals(ValueType.OBJECT)) {
//					transform(o.asMap(), dataSourceId, childContainerId, filter);
//				} else {
//					transform(o.asList(), dataSourceId, childContainerId, filter);
//				}
//			}else
//			if(((Any) o).valueType().equals(ValueType.STRING)){
//				filter.addValue(dataSourceId, containerId, i+1, ((Any) o).toString());
//			}else
//			if(((Any) o).valueType().equals(ValueType.BOOLEAN)){
//				filter.addValue(dataSourceId, containerId, i+1, ((Any) o).toBoolean());
//			}else
//			if(((Any) o).valueType().equals(ValueType.NUMBER)){
//				log.info("Type: {} {}", o.valueType(), o.getClass());
//				if(o.toString().contains(".")){
//					filter.addValue(dataSourceId, containerId, i+1, ((Any) o).toFloat());
//				} else {
//					filter.addValue(dataSourceId, containerId, i+1, ((Any) o).toInt());
//				}
//			}
//		}
//	}

	private void transform(List<Any> arr, String dataSourceId, String containerId, FacadeXGraphBuilder filter) {
		for (int i = 0; i < arr.size(); i++) {
			Any o = arr.get(i);
			transformArrayElement(o, i, dataSourceId, containerId, filter);
		}
	}

	private void transformArrayElement(Any o, int i, String dataSourceId, String containerId,
			FacadeXGraphBuilder filter) {
		if (o.valueType().equals(ValueType.OBJECT) || o.valueType().equals(ValueType.ARRAY)) {
			String childContainerId = StringUtils.join(containerId, "/_", String.valueOf(i + 1));
			filter.addContainer(dataSourceId, containerId, i + 1, childContainerId);
			if (o.valueType().equals(ValueType.OBJECT)) {
				transform(o.asMap(), dataSourceId, childContainerId, filter);
			} else {
				transform(o.asList(), dataSourceId, childContainerId, filter);
			}
		} else if (((Any) o).valueType().equals(ValueType.STRING)) {
			filter.addValue(dataSourceId, containerId, i + 1, ((Any) o).toString());
		} else if (((Any) o).valueType().equals(ValueType.BOOLEAN)) {
			filter.addValue(dataSourceId, containerId, i + 1, ((Any) o).toBoolean());
		} else if (((Any) o).valueType().equals(ValueType.NUMBER)) {
			log.info("Type: {} {} {}", o.valueType(), o.getClass(), o.toString());
			if (o.toString().contains(".")) {
				filter.addValue(dataSourceId, containerId, i + 1, ((Any) o).toFloat());
			} else if (o.toString().equals("0")) {
				filter.addValue(dataSourceId, containerId, i + 1, 0);
			} else {
				filter.addValue(dataSourceId, containerId, i + 1, ((Any) o).toInt());
			}
		}
	}

	@Deprecated
	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {
		return triplify(properties, null);
	}

	@Override
	public DatasetGraph triplify(Properties properties, Op op) throws IOException {

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return DatasetGraphFactory.create();

		logger.trace("Triplifying ", url.toString());
		logger.trace("Op ", op);
		Charset charset = Triplifier.getCharsetArgument(properties);
		FacadeXGraphBuilder filter = new TripleFilteringFacadeXBuilder(url, op, properties);
		transformJSONFromURL(url, Triplifier.getRootArgument(properties, url), properties, charset, filter);
		if (logger.isDebugEnabled()) {
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
