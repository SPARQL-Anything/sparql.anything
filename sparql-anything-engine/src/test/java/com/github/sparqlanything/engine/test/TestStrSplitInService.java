/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

import com.github.sparqlanything.engine.FacadeX;
import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Reported in https://github.com/SPARQL-Anything/sparql.anything/issues/330
 */
public class TestStrSplitInService {
	Logger log = LoggerFactory.getLogger(TestStrSplitInService.class);

	private QueryExecution qe;

	@Ignore
	@Test
	public void testIssue330() throws IOException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		PropertyFunctionRegistry reg = PropertyFunctionRegistry.get(ARQ.getContext());
		Iterator<String> i = reg.keys();
		while(i.hasNext()){
			log.info("Registered magic property: {}", i.next());
		}

		String query = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("./strSplit.sparql"), StandardCharsets.UTF_8);
		String location = getClass().getClassLoader().getResource("./strSplit.csv").toString();
		query = query.replace("%%location%%", location);
		log.info("{}", query);
		QueryExecution qe = QueryExecutionFactory.create(
				query,
				ModelFactory.createDefaultModel());
		ResultSet rs = qe.execSelect();
		Assert.assertTrue(rs.hasNext());
		while (rs.hasNext()){
			log.info("{}", rs.next());
		}
	}
}
