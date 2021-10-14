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

package com.github.sparqlanything.yaml;

import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class YAMLTest {

	private static final Logger logger = LoggerFactory.getLogger(YAMLTest.class);

	@Test
	public void test() throws TriplifierHTTPException, IOException, URISyntaxException {
		URL url = getClass().getClassLoader().getResource("./test.yaml");
		Triplifier t = new YAMLTriplifier();
		Properties properties = new Properties();
		properties.setProperty("location", url.toURI().toString());
		DatasetGraph ds = t.triplify(properties, new BaseFacadeXBuilder("test.yaml", properties));
		ExtendedIterator<Triple> triples = ds.getDefaultGraph().find();
		while(triples.hasNext()){
			logger.trace("{}",triples.next());
		}
		Assert.assertTrue(ds.size() == 2);
		Assert.assertTrue(ds.getDefaultGraph().size() == 11);
	}

}
