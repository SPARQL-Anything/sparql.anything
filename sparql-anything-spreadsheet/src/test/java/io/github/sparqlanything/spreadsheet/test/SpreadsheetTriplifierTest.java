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

package io.github.sparqlanything.spreadsheet.test;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.spreadsheet.SpreadsheetTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class SpreadsheetTriplifierTest extends AbstractTriplifierTester {

	public SpreadsheetTriplifierTest() {
		super(new SpreadsheetTriplifier(), new Properties(), "xls", "nq");
		this.printWholeGraph = true;
	}



	@Override
	protected void properties(Properties properties) {

		// SliceArray
		if(name.getMethodName().equals("testNullString$1")){
			properties.setProperty(IRIArgument.NULL_STRING.toString(), "");
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
