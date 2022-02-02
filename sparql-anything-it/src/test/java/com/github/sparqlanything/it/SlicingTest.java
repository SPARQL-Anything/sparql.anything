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

import org.apache.jena.query.QuerySolution;
import org.junit.Assert;
import org.junit.Test;

public class SlicingTest extends AbstractExecutionTester {

	@Test
	public void testSliceSelect (){
		Assert.assertTrue(result.getResultVars().contains("X"));

		QuerySolution qs;
		qs = result.next();
		System.out.println(qs);
		Assert.assertTrue(qs.getLiteral("X").getString().equals("a1"));
		Assert.assertTrue(qs.getLiteral("Y").getString().equals("b1"));
		Assert.assertTrue(qs.getLiteral("Z").getString().equals("c1"));

		qs = result.next();
		System.out.println(qs);
		Assert.assertTrue(qs.getLiteral("X").getString().equals("a2"));
		Assert.assertTrue(qs.getLiteral("Y").getString().equals("b2"));
		Assert.assertTrue(qs.getLiteral("Z").getString().equals("c2"));

	}
}
