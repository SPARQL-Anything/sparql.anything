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

package io.github.sparqlanything.it;

import io.github.sparqlanything.engine.FacadeX;
import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class AbstractExecutionTester {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractExecutionTester.class);

	protected static final String FILENAME_PLACEHOLDER = "%%fileName%%";
	@Rule
	public TestName name = new TestName();
	protected Dataset dataset = null;
	protected Query query = null;
	protected URI expectedFile = null;
	protected ResultSet expected = null;
	protected ResultSet result = null;
	protected Graph expectedGraph = null;
	protected Graph resultGraph = null;
	protected Dataset expectedDataset = null;
	protected Dataset resultDataset = null;
	protected Boolean expectedBoolean = null;
	protected Boolean resultBoolean = null;
	protected String outputFormat = null;
	protected String[] formats = new String[]{"TTL", "NQ", "CSV"};

	@Before
	public void run() throws URISyntaxException, IOException {
		logger.debug("{} (run)", name.getMethodName());

		prepareDataset();
		prepareQuery();
		prepareExpected();
		beforeExecution();
		perform();
	}

	protected void prepareDataset() {
		dataset = DatasetFactory.createGeneral();
	}

	protected void prepareQuery() throws IOException, URISyntaxException {
		String queryFileName = name.getMethodName().substring(4) + ".sparql";
		URI queryFile = getClass().getClassLoader().getResource(queryFileName).toURI();
		String queryStr = IOUtils.toString(queryFile, StandardCharsets.UTF_8);
		queryStr = prepareQueryString(queryStr);
		query = QueryFactory.create(queryStr);
	}

	protected void prepareExpected() throws URISyntaxException {
		String expectedFilePrefix = name.getMethodName().substring(4) + ".";
		expectedFile = null;
		for (String f : formats) {
			if (getClass().getClassLoader().getResource(expectedFilePrefix + f.toLowerCase()) != null) {
				expectedFile = getClass().getClassLoader().getResource(expectedFilePrefix + f.toLowerCase()).toURI();
				outputFormat = f;
				break;
			}
		}
		logger.debug("Output file: {}", expectedFile);
		// Load expected result
		if (query.isSelectType()) {
			expected = ResultSetFactory.load(expectedFile.toString(), ResultsFormat.guessSyntax(expectedFile.toString()));
		} else if (query.isConstructType()) {
			if (outputFormat.equals("NQ")) {
				expectedDataset = QueryExecutionFactory.create(query, dataset).execConstructDataset();
			} else {
				expectedGraph = QueryExecutionFactory.create(query, dataset).execConstruct().getGraph();
			}
		} else if (query.isAskType()) {
			expected = ResultSetFactory.load(expectedFile.toString(), ResultsFormat.guessSyntax(expectedFile.toString()));
			String bv = expected.getResultVars().get(0);
			expectedBoolean = expected.rewindable().next().getLiteral(bv).getBoolean();
		}
	}

	protected void beforeExecution() {
		// Extension point
	}

	protected void perform() throws URISyntaxException, IOException {
		logger.debug("{} (perform)", name.getMethodName());
		// Execute
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		if (query.isSelectType()) {
			result = QueryExecutionFactory.create(query, dataset).execSelect();
		} else if (query.isConstructType()) {
			if (outputFormat.equals("NQ")) {
				resultDataset = QueryExecutionFactory.create(query, dataset).execConstructDataset();
			} else {
				resultGraph = QueryExecutionFactory.create(query, dataset).execConstruct().getGraph();
			}
		} else if (query.isAskType()) {
			resultBoolean = QueryExecutionFactory.create(query, dataset).execAsk();
		}
	}

	protected String prepareQueryString(String queryStr) {
		// Extension point
		if (queryStr.contains(FILENAME_PLACEHOLDER)) {
			String fileName = name.getMethodName().substring(4) + "Input";
			String filePath = getClass().getClassLoader().getResource(".").getPath();
			String file = filePath + "/" + fileName;
			queryStr = queryStr.replace(FILENAME_PLACEHOLDER, file);
			logger.debug("Input file name: {}", file);
		}
		logger.debug("queryStr: {}", queryStr);
		return queryStr;
	}
}
