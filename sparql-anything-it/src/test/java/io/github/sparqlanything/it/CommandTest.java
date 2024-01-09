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

package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CommandTest {
	private static final Logger log = LoggerFactory.getLogger(CommandTest.class);

	@Rule
	public TestName name = new TestName();

	@Test
	public void testShellPipeline() throws IOException {
		if (Utils.platform == Utils.OS.MAC || Utils.platform == Utils.OS.LINUX || Utils.platform == Utils.OS.SOLARIS) {
			QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
			Query query = QueryFactory.create(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("commands/shell_pipeline.sparql"), StandardCharsets.UTF_8));
			Assert.assertTrue(QueryExecutionFactory.create(query, DatasetGraphFactory.create()).execAsk());
		} else {
			log.warn("Skipping test (platform not supported) {}", name.getMethodName());
		}
	}

	@Test
	public void testEcho() throws IOException {
		if (Utils.platform == Utils.OS.MAC) {
			QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
			Query query = QueryFactory.create(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("commands/echo.sparql"), StandardCharsets.UTF_8));
			ResultSet rs1 = QueryExecutionFactory.create(query, DatasetGraphFactory.create()).execSelect();
			while (rs1.hasNext()) {
				System.err.println(rs1.next());
			}
		} else {
			log.warn("Skipping test (platform not supported) {}", name.getMethodName());
		}
	}

	@Ignore
	@Test
	public void testGit() throws IOException {
		if (Utils.platform == Utils.OS.MAC) {
			QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
			Query query = QueryFactory.create(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("commands/logs.sparql"), StandardCharsets.UTF_8));
			ResultSet rs1 = QueryExecutionFactory.create(query, DatasetGraphFactory.create()).execSelect();
			while (rs1.hasNext()) {
				System.err.println(rs1.next());
			}
		} else {
			log.warn("Skipping test (platform not supported) {}", name.getMethodName());
		}
	}
}
