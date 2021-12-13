/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.engine.test;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.core.DatasetGraph;

import com.github.sparqlanything.model.Triplifier;

public class TestTriplifier implements Triplifier {

	public TestTriplifier() {
	}

	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
		// TODO Not tested yet
		return triplify(properties);
	}

	@Override
	public DatasetGraph triplify(Properties properties) throws IOException {
		return TriplifierRegistryTest.createExampleGraph();
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("test-mime");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("test");
	}
}
