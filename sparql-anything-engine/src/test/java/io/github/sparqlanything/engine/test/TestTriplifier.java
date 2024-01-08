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

package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class TestTriplifier implements Triplifier {

	public TestTriplifier() {
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {
//		return
		DatasetGraph dg = TriplifierRegistryTest.createExampleGraph();
		Iterator<Quad> quad = dg.find();
		while(quad.hasNext()){
			Quad q = quad.next();
			builder.add(q.getGraph(), q.getSubject(), q.getPredicate(), q.getObject());
		}
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
