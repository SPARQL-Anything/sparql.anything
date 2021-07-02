package com.github.spiceh2020.sparql.anything.json;

import com.github.spiceh2020.sparql.anything.model.FacadeXGraphBuilder;
import com.github.spiceh2020.sparql.anything.model.StreamingTriplifier;
import com.github.spiceh2020.sparql.anything.model.TripleFilteringFacadeXBuilder;
import com.github.spiceh2020.sparql.anything.model.Triplifier;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class JSONStreamingTriplifier implements StreamingTriplifier {

	private static Logger logger = LoggerFactory.getLogger(JSONStreamingTriplifier.class);

	private boolean transformJSONFromURL(URL url, String rootId, FacadeXGraphBuilder filter) throws IOException {
		JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
		JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
		JsonIterator json = JsonIterator.parse(url.openStream().readAllBytes());
//		byte[] b = new byte[ 1024 * 1024 * 10];
//		JsonIterator json = JsonIterator.parse(b);
//		json.reset(url.openStream());
		return transformJSON(json, url.toString(), rootId, filter);
	}

	private boolean transformJSON(JsonIterator json, String dataSourceId, String rootId, FacadeXGraphBuilder filter) throws IOException {
		filter.addRoot(dataSourceId, rootId);
		Any any = json.readAny();
		if(any != null){
			this.queue.add(new ImmutablePair<>(rootId, any));
		}

		// Stop here, but there is more
		this.dataSourceId = dataSourceId;
		return true;
	}


	private void transform( Map<String,Any> object, String dataSourceId, String containerId, FacadeXGraphBuilder filter) {
		Map<String,Any> waitingList = new HashMap<String, Any>();
		object.keySet().iterator().forEachRemaining(k -> {
			Any o = object.get(k);
			if(o.valueType().equals(ValueType.OBJECT) || o.valueType().equals(ValueType.ARRAY)) {
				String childContainerId = StringUtils.join(containerId, "/", Triplifier.toSafeURIString(k));
				filter.addContainer(dataSourceId, containerId, Triplifier.toSafeURIString(k), childContainerId);
				waitingList.put(childContainerId, o);
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

		waitingList.entrySet().forEach(c ->{
			queue.add(new ImmutablePair<String,Any>(c.getKey(), c.getValue()));
		});
	}

	private void transform( List<Any> arr, String dataSourceId, String containerId, FacadeXGraphBuilder filter) {
		Map<String,Any> waitingList = new HashMap<String, Any>();
		for (int i = 0; i < arr.size(); i++) {
			Any o = arr.get(i);
			if(o.valueType().equals(ValueType.OBJECT) || o.valueType().equals(ValueType.ARRAY)) {
				String childContainerId = StringUtils.join(containerId, "/_", String.valueOf(i+1));
				filter.addContainer(dataSourceId, containerId, i+1, childContainerId);
				waitingList.put(childContainerId, o);
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
		waitingList.entrySet().forEach(c ->{
			queue.add(new ImmutablePair<String,Any>(c.getKey(), c.getValue()));
		});
	}

	@Deprecated
	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		return triplify(url, properties, null);
	}

	// Implements Strategy = 2
	@Override
	public DatasetGraph triplify(URL url, Properties properties, Op op) throws IOException {
		logger.trace("Triplifying ", url.toString());
		logger.trace("Op ", op);
		setup(url, properties, new TripleFilteringFacadeXBuilder(url, op, properties));
		if(stream()) {
			while (!queue.isEmpty()) {
				stream();
			}
		}
		logger.info("Number of triples: {} ", builder.getMainGraph().size());
		if(logger.isDebugEnabled()){
			logger.info("Number of triples: {} ", builder.getMainGraph().size());
		}
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


	// Internal state

	private FacadeXGraphBuilder builder = null;
	private URL url = null;
	private Properties properties = null;
	private Queue<Pair<String,Any>> queue = null;
	private String dataSourceId = null;

	@Override
	public boolean reset() throws IOException{
		this.queue = new ArrayDeque<Pair<String,Any>>();
		return this.transformJSONFromURL(url, Triplifier.getRootArgument(properties, url), this.builder);
	}

	@Override
	public boolean stream() throws IOException{
		if(queue == null){
			reset();
		}
		if(queue.isEmpty()){
			// nothing added
			return false;
		}
		// continue streaming
		Pair<String,Any> next = queue.poll();
		String containerId = next.getKey();
		Any any = next.getValue();
		if(any.valueType().equals(ValueType.OBJECT)) {
			Map<String, Any> object = any.asMap();
			transform(object, dataSourceId, containerId, builder);
		} else {
			List<Any> object = any.asList();
			transform( object, dataSourceId, containerId, builder);
		}
		return !queue.isEmpty();
	}

	@Override
	public void setup(URL url, Properties properties, FacadeXGraphBuilder builder) throws IOException {
		if(this.builder != null){
			throw new IllegalStateException("Streaming in process. Call end() before starting another one.");
		}
		this.url = url;
		this.properties = properties;
		this.builder = builder;
		this.dataSourceId = url.toString();
	}

	@Override
	public void clear() throws IOException{
		builder = null;
		url = null;
		properties = null;
		queue = null;
		dataSourceId = null;
	}

	@Override
	public List<String> getDataSourcesIds() {
		if(dataSourceId == null){
			throw new IllegalStateException("The streaming triplifier is not setup!");
		}
		return Collections.unmodifiableList(Arrays.asList(new String[]{dataSourceId}));
	}

	@Override
	public void flush() throws IOException {
		while(stream()){

		}
	}
}
