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

package io.github.sparqlanything.csv;

import io.github.sparqlanything.model.Slice;
import org.apache.commons.csv.CSVRecord;

import java.util.LinkedHashMap;

public class CSVSlice implements Slice<CSVRecord> {
	private CSVRecord record;
	private int iteration;
	private String dataSourceId;
	private String rootId;
	LinkedHashMap<Integer, String> headers;

	public static CSVSlice makeSlice(CSVRecord record, int iteration, String dataSourceId, String rootId, LinkedHashMap<Integer, String> headers){
		CSVSlice r = new CSVSlice();
		r.dataSourceId = dataSourceId;
		r.rootId = rootId;
		r.iteration = iteration;
		r.record = record;
		r.headers = headers;
		return r;
	}

	@Override
	public CSVRecord get() {
		return record;
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

	public LinkedHashMap<Integer, String> getHeaders(){
		return this.headers;
	}
}
