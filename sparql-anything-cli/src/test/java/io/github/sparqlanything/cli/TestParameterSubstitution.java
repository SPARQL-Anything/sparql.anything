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

package io.github.sparqlanything.cli;

import io.github.basilapi.basil.sparql.QueryParameter;
import io.github.basilapi.basil.sparql.Specification;
import io.github.basilapi.basil.sparql.SpecificationFactory;
import io.github.basilapi.basil.sparql.UnknownQueryTypeException;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TestParameterSubstitution {

	private static final Logger logger = LoggerFactory.getLogger(TestParameterSubstitution.class);

	@Test
	public void test() throws IOException, UnknownQueryTypeException {
		String str = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("./queryWithParameter.sparql")), StandardCharsets.UTF_8);
		Specification specification = SpecificationFactory.create("", str);
		Collection<QueryParameter> co = specification.getParameters();
		Set<String> parameters = new HashSet<>();
		for (QueryParameter queryParameter : co) {
			parameters.add(queryParameter.getName());
		}
		Assert.assertEquals(Sets.newHashSet("fileName", "filePath"), parameters);
//
//		while (it.hasNext()) {
//			QuerySolution qs = parameters.nextSolution();
//		}
	}

}
