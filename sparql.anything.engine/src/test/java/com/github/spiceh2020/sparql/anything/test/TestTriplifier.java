/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.spiceh2020.sparql.anything.test;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.core.DatasetGraph;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class TestTriplifier implements Triplifier {

	public TestTriplifier() {
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
