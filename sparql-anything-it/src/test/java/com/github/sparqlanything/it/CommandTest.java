/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.it;

import com.github.sparqlanything.engine.FacadeX;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CommandTest {

	@Ignore // Platform dependent!
	@Test
	public void testEcho() throws IOException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		Query query = QueryFactory.create(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("echo.sparql"), StandardCharsets.UTF_8));
		ResultSet rs1 = QueryExecutionFactory.create(query, DatasetGraphFactory.create()).execSelect();
		while(rs1.hasNext()){
			System.err.println(rs1.next());
		}
	}
}
