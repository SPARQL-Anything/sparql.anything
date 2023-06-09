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

import java.util.Properties;

import io.github.sparqlanything.model.IRIArgument;
import org.junit.Test;

import io.github.sparqlanything.csv.CSVTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;

public class ValueOptionsTest extends AbstractTriplifierTester {

	public ValueOptionsTest() {
		super(new CSVTriplifier(), new Properties(), "csv");
	}

	@Override
	protected void properties(Properties properties) {
		if (name.getMethodName().equals("testTrimStringsTrue")) {
			properties.setProperty("blank-nodes", "true");
			properties.setProperty("trim-strings", "true");
		} else if (name.getMethodName().equals("testTrimStringsFalse")) {
			properties.setProperty("blank-nodes", "true");
			properties.setProperty("trim-strings", "false");
		} else if (name.getMethodName().equals("testNullStringsTrue")) {
			properties.setProperty("blank-nodes", "true");
			properties.setProperty("null-string", "");
		} else if (name.getMethodName().equals("testNullStringsFalse")) {
			properties.setProperty("blank-nodes", "true");
			// properties.setProperty("null-string", "");
		} else if (name.getMethodName().equals("testMemberTrue")) {
			properties.setProperty("use-rdfs-member", "true");
			properties.setProperty("blank-nodes", "true");
		} else if (name.getMethodName().equals("testMemberFalse")) {
			properties.setProperty("use-rdfs-member", "false");
			properties.setProperty("blank-nodes", "true");
		} else if(name.getMethodName().equals("testReifySlotStatements")){
			properties.setProperty("use-rdfs-member", "true");
			properties.setProperty("blank-nodes", "false");
			properties.setProperty(IRIArgument.ANNOTATE_TRIPLE_WITH_SLOT_KEY.toString(), "true");
		}
	}

	@Test
	public void testTrimStringsTrue() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testTrimStringsFalse() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testNullStringsTrue() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testNullStringsFalse() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testMemberTrue() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testMemberFalse() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testReifySlotStatements() {
		assertResultIsIsomorphicWithExpected();
	}
}


