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

package io.github.sparqlanything.xml;

import io.github.sparqlanything.model.Slice;
import com.ximpleware.VTDNav;
import org.apache.commons.lang3.tuple.Pair;

public class XPathSlice implements Slice<Pair<VTDNav,Integer>> {

	private Pair<VTDNav,Integer> slice;
	private int iteration;
	private String dataSourceId;
	private String rootId;

	@Override
	public Pair<VTDNav,Integer> get() {
		return slice;
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

	public static final XPathSlice make(VTDNav nav, int index, int iteration, String rootId, String dataSourceId){
		XPathSlice slice = new XPathSlice();
		slice.iteration = iteration;
		slice.slice = Pair.of(nav, index);
		slice.rootId = rootId;
		slice.dataSourceId = dataSourceId;
		return slice;
	}
}
