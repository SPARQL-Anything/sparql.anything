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

package io.github.sparqlanything.docs.test;

import io.github.sparqlanything.docs.DocxTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class DocxTriplifierTest extends AbstractTriplifierTester {

	public DocxTriplifierTest() {
		super(new DocxTriplifier(), new Properties(), "docx");
		this.setPrintWholeGraph(true);
	}

	@Test
	public void testDoc1() {
		assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testDoc2() {
		assertResultIsIsomorphicWithExpected();
	}
	
	@Test
	public void testDoc3() {
		assertResultIsIsomorphicWithExpected();
	}

}
