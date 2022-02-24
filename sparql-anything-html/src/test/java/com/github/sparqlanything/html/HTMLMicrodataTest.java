/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.html;

import com.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.junit.Test;

import java.util.Properties;

public class HTMLMicrodataTest extends AbstractTriplifierTester {

	public HTMLMicrodataTest() {
		super(new HTMLTriplifier(), new Properties(), "html", "nq");
		this.setPrintWholeGraph(true);
	}

	@Test
	public void testMicrodata1() {
		this.assertResultIsIsomorphicWithExpected();
	}
	
	protected void properties(Properties properties) {
		properties.setProperty(HTMLTriplifier.PROPERTY_METADATA, "true");
	}

}
