/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package io.github.sparqlanything.it;

import org.apache.jena.query.QuerySolution;
import org.junit.Assert;
import org.junit.Test;

public class SlicingTest extends AbstractExecutionTester {

	@Test
	public void testSliceSelect() {
		Assert.assertTrue(result.getResultVars().contains("X"));

		QuerySolution qs = result.next();
//		ex = expected.next();
//		System.out.println(qs);
		Assert.assertEquals("a1", qs.getLiteral("X").getString());
		Assert.assertEquals("b1", qs.getLiteral("Y").getString());
		Assert.assertEquals("c1", qs.getLiteral("Z").getString());

		qs = result.next();
		Assert.assertEquals("a2", qs.getLiteral("X").getString());
		Assert.assertEquals("b2", qs.getLiteral("Y").getString());
		Assert.assertEquals("c2", qs.getLiteral("Z").getString());

	}

	@Test
	public void testSliceSelect2() {
		Assert.assertTrue(result.getResultVars().contains("X"));

		QuerySolution qs;
		qs = result.next();
//		ex = expected.next();
		Assert.assertEquals("a1", qs.getLiteral("X").getString());
		Assert.assertEquals("b1", qs.getLiteral("Y").getString());
		Assert.assertEquals("c1", qs.getLiteral("Z").getString());

		qs = result.next();
//		System.out.println(qs);
		Assert.assertEquals("a2", qs.getLiteral("X").getString());
		Assert.assertEquals("b2", qs.getLiteral("Y").getString());
		Assert.assertEquals("c2", qs.getLiteral("Z").getString());

	}

	@Test
	public void testSliceSelectSize() {
		// 5 rows, slice.size=2 -> batches of [1,2],[3,4],[5]; result must equal the unbatched (slice.size=1) case.
		Assert.assertTrue(result.getResultVars().contains("X"));

		QuerySolution qs;
		for (int i = 1; i <= 5; i++) {
			qs = result.next();
			Assert.assertEquals("a" + i, qs.getLiteral("X").getString());
			Assert.assertEquals("b" + i, qs.getLiteral("Y").getString());
			Assert.assertEquals("c" + i, qs.getLiteral("Z").getString());
		}
		Assert.assertFalse(result.hasNext());
	}
}
