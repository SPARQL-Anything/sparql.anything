/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.spreadsheet.test;

import com.github.sparqlanything.spreadsheet.SpreadsheetTriplifier;
import com.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SpreadsheetTriplifierTest extends AbstractTriplifierTester {
	static private Logger L = LoggerFactory.getLogger(SpreadsheetTriplifierTest.class);

	public SpreadsheetTriplifierTest() {
		super(new SpreadsheetTriplifier(), new Properties(), "xls", "nq");
		super.useDatasetGraph = true;
	}



	@Override
	protected void properties(Properties properties) {

		// SliceArray
		if(name.getMethodName().equals("testNullString$1")){
			properties.setProperty("null-string", "");
		}

		if(name.getMethodName().equals("testFormulaEvaluator")){
			properties.setProperty(SpreadsheetTriplifier.PROPERTY_EVALUATE_FORMULAS, "true");
		}
	}

	@Test
	public void testNullString$1(){
//		RDFDataMgr.write(System.err, resultDatasetGraph, Lang.NQ);
		assertResultIsIsomorphicWithExpected();
	}


	@Test
	public void testNoNullString$1(){
//		RDFDataMgr.write(System.err, resultDatasetGraph, Lang.NQ);
		assertResultIsIsomorphicWithExpected();
	}


	@Test
	public void testFormulaEvaluator(){
//		RDFDataMgr.write(System.err, resultDatasetGraph, Lang.NQ);
		assertResultIsIsomorphicWithExpected();
	}
}
