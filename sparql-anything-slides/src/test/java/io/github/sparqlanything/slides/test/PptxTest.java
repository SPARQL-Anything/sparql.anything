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

package io.github.sparqlanything.slides.test;

import io.github.sparqlanything.model.*;
import io.github.sparqlanything.slides.PptxTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class PptxTest extends AbstractTriplifierTester {

	public PptxTest() {
		super(new PptxTriplifier(), new Properties(), "pptx");
	}

	@Override
	protected void properties(Properties properties) {
		if (name.getMethodName().equals("testPresentation2")) {
			properties.setProperty(PptxTriplifier.EXTRACT_SECTIONS.toString(), "true");
		}
	}


	@Test
	public void testPresentation1() {
		this.assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testPresentation2() {
		this.assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testPresentation3 (){
		this.assertResultIsIsomorphicWithExpected();
	}

	@Test
	public void testIssue429 (){
		this.assertResultIsIsomorphicWithExpected();
	}
}
