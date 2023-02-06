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

package com.github.sparqlanything.cli;

import io.github.basilapi.basil.sparql.QueryParameter;
import io.github.basilapi.basil.sparql.Specification;
import io.github.basilapi.basil.sparql.SpecificationFactory;
import io.github.basilapi.basil.sparql.UnknownQueryTypeException;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ResultSetFormatter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

public class TestIssue320 {

	@Test
	public void testIssue320() throws Exception {
		// This is to reproduce Issue #320
		// No output form below
		ResultSetFormatter.outputAsCSV(System.err,true);
		ResultSetFormatter.outputAsCSV(System.err,false);

		String str = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("./ask.sparql"), StandardCharsets.UTF_8);
		String output = SPARQLAnything.callMain(new String[]{"-q", str, "-f", "text"});
		Assert.assertTrue(output.equals("false"));
	}
}
