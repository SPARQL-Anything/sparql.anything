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

package com.github.sparqlanything.json;

import com.github.sparqlanything.model.Slice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONPathSlice implements Slice<Object> {
	private static Logger logger = LoggerFactory.getLogger(JSONPathSlice.class);
	private Object object;
	private int iteration;
	private String dataSourceId;
	private String rootId;

	private JSONPathSlice(){}

	@Override
	public Object get() {
		return object;
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

	public static JSONPathSlice makeSlice(Object object, int iteration, String rootId, String dataSourceId){
		JSONPathSlice r = new JSONPathSlice();
		r.object = object;
		r.iteration = iteration;
		r.dataSourceId = dataSourceId;
		r.rootId = rootId;
		return r;
	}
}
