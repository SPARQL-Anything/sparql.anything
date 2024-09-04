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


public class JSONPathSlice extends JSONSlice {
	private Object object;
	private int iteration;
	private String dataSourceId;

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


	public static JSONPathSlice makeSlice(Object object, int iteration, String dataSourceId){
		JSONPathSlice r = new JSONPathSlice();
		r.object = object;
		r.iteration = iteration;
		r.dataSourceId = dataSourceId;
		return r;
	}
}
