/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.parquet;

import dev.hardwood.row.StructAccessor;
import io.github.sparqlanything.model.Slice;

import java.util.List;

public class ParquetSlice implements Slice<StructAccessor> {
	private StructAccessor row;
	private int iteration;
	private String dataSourceId;
	private List<ParquetTriplifier.Column> columns;

	static ParquetSlice makeSlice(StructAccessor row, int iteration, String dataSourceId, List<ParquetTriplifier.Column> columns) {
		ParquetSlice r = new ParquetSlice();
		r.dataSourceId = dataSourceId;
		r.iteration = iteration;
		r.row = row;
		r.columns = columns;
		return r;
	}

	@Override
	public StructAccessor get() {
		return row;
	}

	@Override
	public int iteration() {
		return iteration;
	}

	@Override
	public String getDatasourceId() {
		return dataSourceId;
	}

	public List<ParquetTriplifier.Column> getColumns() {
		return columns;
	}
}
