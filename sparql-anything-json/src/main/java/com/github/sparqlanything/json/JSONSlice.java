/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.github.sparqlanything.model.Slice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONSlice implements Slice<JsonToken> {
	private static Logger logger = LoggerFactory.getLogger(JSONTriplifier.class);
	private JsonToken token;
	private JsonParser parser;
	private int iteration;
	private String dataSourceId;
	private String rootId;

	private JSONSlice(){}

	@Override
	public JsonToken get() {
		return token;
	}

	public JsonParser getParser() {
		return parser;
	}

	@Override
	public int iteration() {
		return iteration;
	}

	@Override
	public String getDatasourceId() {
		return dataSourceId;
	}

	@Override
	public String getRootId() {
		return rootId;
	}

	public static JSONSlice makeSlice(JsonToken token, JsonParser parser, int iteration, String rootId, String dataSourceId){
		JSONSlice r = new JSONSlice();
		r.token = token;
		r.parser = parser;
		r.iteration = iteration;
		r.dataSourceId = dataSourceId;
		r.rootId = rootId;
		return r;
	}
}
