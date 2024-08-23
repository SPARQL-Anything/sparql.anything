/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JSONTokenSlice extends JSONSlice {
	private JsonToken token;
	private JsonParser parser;
	private int iteration;
	private String dataSourceId;
	private JSONTokenSlice(){}

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

	public static JSONTokenSlice makeSlice(JsonToken token, JsonParser parser, int iteration, String dataSourceId){
		JSONTokenSlice r = new JSONTokenSlice();
		r.token = token;
		r.parser = parser;
		r.iteration = iteration;
		r.dataSourceId = dataSourceId;
		return r;
	}
}
