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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CommandTest {
	private static final Logger log = LoggerFactory.getLogger(CommandTest.class);

	// Operating systems
	public enum OS {
		WINDOWS, LINUX, MAC, SOLARIS
	};

	static {
		String operSys = System.getProperty("os.name").toLowerCase();
		if (operSys.contains("win")) {
			platform = OS.WINDOWS;
		} else if (operSys.contains("nix") || operSys.contains("nux")
				|| operSys.contains("aix")) {
			platform = OS.LINUX;
		} else if (operSys.contains("mac")) {
			platform = OS.MAC;
		} else if (operSys.contains("sunos")) {
			platform = OS.SOLARIS;
		}
	}
	static OS platform;

	@Rule
	public TestName name = new TestName();

	@Test
	public void testEcho() throws IOException {
		if(platform == OS.MAC) {
			QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
			Query query = QueryFactory.create(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("commands/echo.sparql"), StandardCharsets.UTF_8));
			ResultSet rs1 = QueryExecutionFactory.create(query, DatasetGraphFactory.create()).execSelect();
			while (rs1.hasNext()) {
				System.err.println(rs1.next());
			}
		}else{
			log.warn("Skipping test (platform not supported) {}", name.getMethodName());
		}
	}

	@Ignore
	@Test
	public void testGit() throws IOException {
		if(platform == OS.MAC) {
			QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
			Query query = QueryFactory.create(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("commands/logs.sparql"), StandardCharsets.UTF_8));
			ResultSet rs1 = QueryExecutionFactory.create(query, DatasetGraphFactory.create()).execSelect();
			while (rs1.hasNext()) {
				System.err.println(rs1.next());
			}
		}else{
			log.warn("Skipping test (platform not supported) {}", name.getMethodName());
		}
	}
}
