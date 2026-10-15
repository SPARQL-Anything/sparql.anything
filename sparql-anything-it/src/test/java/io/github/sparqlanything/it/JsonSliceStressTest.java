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

import io.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Stress test for slicing a top-level JSON array with fx:slice.size.
 *
 * <p>A large, irregular JSON array (objects with nested objects and arrays, empty containers,
 * nulls, escaped and non-ASCII strings, numbers, booleans, plus top-level scalars and arrays) is
 * queried with slicing disabled and with many batch sizes. For every batch size the triplified
 * content must be identical to the unsliced case, slot numbers must be preserved, and a self-join
 * must match exactly the pairs of items that fall in the same batch.</p>
 *
 * <p>Regression test for slice.size > 1 consuming the tokens of lazy JSON slices before they
 * were triplified (parse errors, NPEs and silent data loss).</p>
 */
public class JsonSliceStressTest {

	private static final int ITEMS = 1500;
	private static final int GROUPS = 7;
	private static final long SEED = 20261015L;
	private static final int[] SLICE_SIZES = {1, 2, 3, 4, 5, 7, 16, 64, 333, ITEMS - 1, ITEMS, ITEMS + 10};

	private static final String PREFIXES = """
			PREFIX fx:  <http://sparql.xyz/facade-x/ns/>
			PREFIX fxe: <http://sparql.xyz/facade-x/engine/>
			PREFIX xyz: <http://sparql.xyz/facade-x/data/>
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			""";

	private static Path dir;
	private static Path json;
	/** Top-level position (0-based) of each object item, and its group; -1 for non-object items. */
	private static int[] groupAt;

	@BeforeClass
	public static void generate() throws IOException {
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		dir = Files.createTempDirectory("json-slice-stress");
		json = dir.resolve("stress.json");
		Random r = new Random(SEED);
		groupAt = new int[ITEMS];
		StringBuilder sb = new StringBuilder("[\n");
		for (int i = 0; i < ITEMS; i++) {
			if (i > 0) {
				sb.append(",\n");
			}
			int kind = r.nextInt(20);
			if (kind == 0) {
				// top-level scalar
				groupAt[i] = -1;
				sb.append(r.nextBoolean() ? "\"scalar " + i + "\"" : String.valueOf(i));
			} else if (kind == 1) {
				// top-level array, possibly nested and empty
				groupAt[i] = -1;
				sb.append("[").append(i).append(", [], [\"x\", [").append(i).append(", null]], {}]");
			} else {
				groupAt[i] = r.nextInt(GROUPS);
				sb.append(object(i, groupAt[i], r));
			}
		}
		sb.append("\n]\n");
		Files.writeString(json, sb.toString(), StandardCharsets.UTF_8);
	}

	private static String object(int id, int group, Random r) {
		StringBuilder o = new StringBuilder("{");
		o.append("\"id\": ").append(id);
		o.append(", \"group\": ").append(group);
		o.append(", \"name\": \"item \\\"").append(id).append("\\\" \\u00e8 \\n ").append("é".repeat(r.nextInt(3))).append("\"");
		if (r.nextBoolean()) {
			o.append(", \"score\": ").append(r.nextInt(1000)).append('.').append(r.nextInt(100));
		}
		if (r.nextBoolean()) {
			o.append(", \"active\": ").append(r.nextBoolean());
		}
		if (r.nextInt(4) == 0) {
			o.append(", \"missing\": null");
		}
		int tags = r.nextInt(5);
		o.append(", \"tags\": [");
		for (int t = 0; t < tags; t++) {
			if (t > 0) {
				o.append(", ");
			}
			o.append(r.nextBoolean() ? "\"t" + t + "\"" : String.valueOf(t));
		}
		o.append("]");
		if (r.nextBoolean()) {
			o.append(", \"nested\": {\"level\": 1, \"inner\": {\"level\": 2, \"list\": [[1, 2], [], [{\"deep\": \"")
				.append(id).append("\"}]]}, \"empty\": {}}");
		}
		return o.append("}").toString();
	}

	@AfterClass
	public static void cleanup() throws IOException {
		Files.deleteIfExists(json);
		Files.deleteIfExists(dir);
	}

	private static String service(Integer sliceSize, String pattern) {
		String opts = sliceSize == null ? "" : " ; fxe:slice true ; fxe:slice.size " + sliceSize;
		return "SERVICE <x-sparql-anything:> { fxe:properties fxe:location \""
			+ json.toAbsolutePath().toString().replace("\\", "/") + "\"" + opts + " . " + pattern + " }";
	}

	private static List<String> rows(String query) {
		List<String> out = new ArrayList<>();
		try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(query), DatasetFactory.createGeneral())) {
			ResultSet rs = qe.execSelect();
			List<String> vars = rs.getResultVars();
			while (rs.hasNext()) {
				QuerySolution qs = rs.next();
				StringBuilder row = new StringBuilder();
				for (String v : vars) {
					RDFNode n = qs.get(v);
					row.append(n == null ? "UNBOUND" : n.isLiteral() ? n.asLiteral().getLexicalForm() + "^^" + n.asLiteral().getDatatypeURI() : n.isAnon() ? "_" : n.toString()).append('\t');
				}
				out.add(row.toString());
			}
		}
		Collections.sort(out);
		return out;
	}

	/** Every literal reachable from each object item, keyed by the item's id. */
	private static List<String> itemContent(Integer sliceSize) {
		return rows(PREFIXES + "SELECT ?id ?p ?o WHERE { " + service(sliceSize,
			"?item xyz:id ?id . ?item (!rdf:type)* ?n . ?n ?p ?o FILTER(isLiteral(?o))") + " }");
	}

	/** The root slot of every top-level item, with the item's id or, for scalars, the value. */
	private static List<String> rootSlots(Integer sliceSize) {
		return rows(PREFIXES + "SELECT ?slot ?v WHERE { " + service(sliceSize,
			"?root a fx:Root ; ?slot ?x . FILTER(?slot != rdf:type) OPTIONAL { ?x xyz:id ?id } BIND(COALESCE(?id, ?x) AS ?v)") + " }");
	}

	private static long count(String query) {
		List<String> r = rows(query);
		Assert.assertEquals(1, r.size());
		return Long.parseLong(r.get(0).split("\\^\\^")[0]);
	}

	/** Pairs of distinct items with the same group: only pairs within the same batch can match. */
	private static long sameGroupPairs(Integer sliceSize) {
		return count(PREFIXES + "SELECT (COUNT(*) AS ?n) WHERE { " + service(sliceSize,
			"?a xyz:group ?g . ?b xyz:group ?g FILTER(?a != ?b)") + " }");
	}

	private static long expectedPairs(int batchSize) {
		Map<String, Integer> perBatchGroup = new HashMap<>();
		for (int i = 0; i < ITEMS; i++) {
			if (groupAt[i] >= 0) {
				perBatchGroup.merge((i / batchSize) + ":" + groupAt[i], 1, Integer::sum);
			}
		}
		long pairs = 0;
		for (int n : perBatchGroup.values()) {
			pairs += (long) n * (n - 1);
		}
		return pairs;
	}

	@Test
	public void unslicedBaselineIsComplete() {
		long objects = java.util.Arrays.stream(groupAt).filter(g -> g >= 0).count();
		Assert.assertEquals(objects, count(PREFIXES + "SELECT (COUNT(DISTINCT ?id) AS ?n) WHERE { " + service(null, "?item xyz:id ?id") + " }"));
		Assert.assertEquals(ITEMS, rootSlots(null).size());
		Assert.assertEquals(expectedPairs(ITEMS), sameGroupPairs(null));
	}

	@Test
	public void contentIsIndependentOfSliceSize() {
		List<String> expected = itemContent(null);
		Assert.assertFalse(expected.isEmpty());
		for (int size : SLICE_SIZES) {
			Assert.assertEquals("item content differs with slice.size=" + size, expected, itemContent(size));
		}
	}

	@Test
	public void rootSlotsAreIndependentOfSliceSize() {
		List<String> expected = rootSlots(null);
		for (int size : SLICE_SIZES) {
			Assert.assertEquals("root slots differ with slice.size=" + size, expected, rootSlots(size));
		}
	}

	@Test
	public void joinsMatchExactlyWithinEachBatch() {
		for (int size : SLICE_SIZES) {
			Assert.assertEquals("same-group pairs with slice.size=" + size, expectedPairs(size), sameGroupPairs(size));
		}
	}
}
