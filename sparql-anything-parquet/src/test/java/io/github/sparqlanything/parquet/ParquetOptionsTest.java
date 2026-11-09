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
package io.github.sparqlanything.parquet;

import com.sun.net.httpserver.HttpServer;
import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Slice;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.testutils.TestUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class ParquetOptionsTest {

	private static final String ROOT = "http://www.example.org/document";
	private static final String XYZ = Triplifier.XYZ_NS;

	private Properties properties(String fixture) throws Exception {
		Properties p = new Properties();
		p.setProperty(IRIArgument.LOCATION.toString(), resource(fixture).toUri().toString());
		p.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
		p.setProperty(IRIArgument.ROOT.toString(), ROOT);
		return p;
	}

	private Path resource(String fixture) throws Exception {
		return Paths.get(getClass().getClassLoader().getResource(fixture + ".parquet").toURI());
	}

	private static DatasetGraph triplify(Properties p) throws Exception {
		FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(p);
		new ParquetTriplifier().triplify(p, b);
		return b.getDatasetGraph();
	}

	private static Graph data(DatasetGraph dg) {
		return dg.getGraph(NodeFactory.createURI(ROOT));
	}

	private static Set<String> predicates(Graph g) {
		Set<String> result = new HashSet<>();
		g.find().forEachRemaining(t -> result.add(t.getPredicate().getURI()));
		return result;
	}

	private static Node xyz(String key) {
		return NodeFactory.createURI(XYZ + key);
	}

	private static Node row(int n) {
		return NodeFactory.createURI(ROOT + "#row" + n);
	}

	@Test
	public void untypedLiteralsArePlainStrings() throws Exception {
		Properties p = properties("Flat");
		p.setProperty(ParquetTriplifier.PROPERTY_TYPED_LITERALS.toString(), "false");
		Graph g = data(triplify(p));
		g.find().forEachRemaining(t -> {
			if (t.getObject().isLiteral()) {
				assertEquals(t.toString(), XSDDatatype.XSDstring.getURI(), t.getObject().getLiteralDatatypeURI());
			}
		});
		assertTrue(g.contains(row(1), xyz("decimal"), NodeFactory.createLiteralString("12.50")));
		assertTrue(g.contains(row(1), xyz("uint32"), NodeFactory.createLiteralString("4294967295")));
	}

	@Test
	public void projectionReadsOnlyTheGivenColumns() throws Exception {
		Properties p = properties("Flat");
		p.setProperty(ParquetTriplifier.PROPERTY_COLUMNS.toString(), "int64, string");
		Set<String> predicates = predicates(data(triplify(p)));
		predicates.removeIf(u -> u.startsWith(RDF.getURI()));
		assertEquals(Set.of(XYZ + "int64", XYZ + "string"), predicates);
	}

	@Test
	public void projectionOfNestedColumn() throws Exception {
		Properties p = properties("Nested");
		p.setProperty(ParquetTriplifier.PROPERTY_COLUMNS.toString(), "people");
		Graph g = data(triplify(p));
		assertTrue(g.contains(NodeFactory.createURI(ROOT + "#row3/people/_2"), xyz("name"), NodeFactory.createLiteralString("Cy")));
		assertFalse(g.contains(Node.ANY, xyz("id"), Node.ANY));
	}

	@Test(expected = IOException.class)
	public void unknownColumnFails() throws Exception {
		Properties p = properties("Flat");
		p.setProperty(ParquetTriplifier.PROPERTY_COLUMNS.toString(), "no_such_column");
		triplify(p);
	}

	@Test
	public void sanitizedHeaders() throws Exception {
		Properties p = properties("Headers");
		p.setProperty(ParquetTriplifier.PROPERTY_HEADERS_SANITIZE.toString(), "true");
		Graph g = data(triplify(p));
		assertTrue(g.contains(row(1), xyz("first_name"), NodeFactory.createLiteralString("Ann")));
		assertTrue(g.contains(row(1), xyz("e_mail_work"), NodeFactory.createLiteralString("ann@example.org")));
	}

	@Test
	public void allSupportedCodecsGiveTheSameGraph() throws Exception {
		Graph expected = data(triplify(properties("codec_none")));
		assertEquals(7, expected.size());
		for (String codec : List.of("gzip", "snappy", "zstd")) {
			assertTrue(codec, TestUtils.testIsomorphicGraphs(expected, data(triplify(properties("codec_" + codec)))));
		}
	}

	@Test
	public void unsupportedCodecFailsWithAClearError() throws Exception {
		try {
			triplify(properties("codec_brotli"));
			fail("Brotli is not bundled");
		} catch (Exception e) {
			Throwable t = e;
			boolean mentionsCodec = false;
			while (t != null) {
				mentionsCodec |= t.getMessage() != null && t.getMessage().toUpperCase().contains("BROTLI");
				t = t.getCause();
			}
			assertTrue("The error names the codec: " + e, mentionsCodec);
		}
	}

	@Test
	public void slicingGivesTheSameGraph() throws Exception {
		for (String fixture : List.of("Flat", "Nested", "RowGroups")) {
			Properties p = properties(fixture);
			Graph expected = data(triplify(p));
			ParquetTriplifier t = new ParquetTriplifier();
			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(p);
			int slices = 0;
			try (var it = t.slice(p)) {
				for (Slice<?> slice : it) {
					t.triplify((ParquetSlice) slice, p, b);
					slices++;
				}
			}
			assertTrue(fixture, slices > 0);
			assertTrue(fixture, TestUtils.testIsomorphicGraphs(expected, data(b.getDatasetGraph())));
		}
	}

	@Test
	public void remoteFileIsReadThroughATemporaryCopy() throws Exception {
		byte[] bytes = Files.readAllBytes(resource("Nested"));
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/nested.parquet", exchange -> {
			exchange.getResponseHeaders().add("Content-Type", "application/vnd.apache.parquet");
			exchange.sendResponseHeaders(200, bytes.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(bytes);
			}
		});
		server.start();
		try {
			Properties p = properties("Nested");
			p.setProperty(IRIArgument.LOCATION.toString(), "http://127.0.0.1:" + server.getAddress().getPort() + "/nested.parquet");
			Graph remote = data(triplify(p));
			assertTrue(TestUtils.testIsomorphicGraphs(data(triplify(properties("Nested"))), remote));
		} finally {
			server.stop(0);
		}
	}

	@Test
	public void schemaGoesToTheMetadataGraph() throws Exception {
		Properties p = properties("Flat");
		p.setProperty(IRIArgument.METADATA.toString(), "true");
		DatasetGraph dg = triplify(p);
		Graph metadata = dg.getGraph(NodeFactory.createURI(Triplifier.METADATA_GRAPH_IRI));
		assertFalse(metadata.isEmpty());
		// The data graph is the same as without metadata
		assertTrue(TestUtils.testIsomorphicGraphs(data(triplify(properties("Flat"))), data(dg)));

		String query = "PREFIX xyz: <" + XYZ + "> PREFIX rdf: <" + RDF.getURI() + "> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ASK { " +
				"<" + ROOT + "> xyz:num-rows \"2\"^^xsd:long ; xyz:schema ?schema ; xyz:key-value-metadata ?kv . " +
				"?kv ?n [ xyz:key \"origin\" ; xyz:value \"sparql-anything test\" ] . " +
				"?schema xyz:name \"schema\" ; rdf:_13 ?decimal ; rdf:_19 ?uuid . " +
				"?decimal xyz:name \"decimal\" ; xyz:physical-type \"FIXED_LEN_BYTE_ARRAY\" ; xyz:logical-type \"DECIMAL\" ; xyz:precision \"10\"^^xsd:int ; xyz:scale \"2\"^^xsd:int ; xyz:repetition \"OPTIONAL\" . " +
				"?uuid xyz:name \"uuid\" ; xyz:logical-type \"UUID\" ; xyz:type-length \"16\"^^xsd:int . }";
		assertTrue(QueryExecutionFactory.create(query, DatasetFactory.wrap(org.apache.jena.sparql.core.DatasetGraphFactory.wrap(metadata))).execAsk());
	}

	@Test
	public void nestedSchemaKeepsListAndMapStructure() throws Exception {
		Properties p = properties("Nested");
		p.setProperty(IRIArgument.METADATA.toString(), "true");
		Graph metadata = triplify(p).getGraph(NodeFactory.createURI(Triplifier.METADATA_GRAPH_IRI));
		String query = "PREFIX xyz: <" + XYZ + "> PREFIX rdf: <" + RDF.getURI() + "> ASK { " +
				"?labels xyz:name \"labels\" ; xyz:logical-type \"MAP\" ; rdf:_1 ?kv . " +
				"?kv xyz:name \"key_value\" ; xyz:repetition \"REPEATED\" ; rdf:_1 [ xyz:name \"key\" ; xyz:repetition \"REQUIRED\" ; xyz:logical-type \"STRING\" ] ; rdf:_2 [ xyz:name \"value\" ] . " +
				"?tags xyz:name \"tags\" ; xyz:logical-type \"LIST\" ; rdf:_1 [ xyz:name \"list\" ; rdf:_1 [ xyz:name \"element\" ; xyz:physical-type \"BYTE_ARRAY\" ] ] . }";
		assertTrue(QueryExecutionFactory.create(query, DatasetFactory.wrap(org.apache.jena.sparql.core.DatasetGraphFactory.wrap(metadata))).execAsk());
	}

	@Test
	public void noMetadataGraphByDefault() throws Exception {
		DatasetGraph dg = triplify(properties("Flat"));
		assertFalse(dg.containsGraph(NodeFactory.createURI(Triplifier.METADATA_GRAPH_IRI)));
	}

	@Test
	public void emptyListAndNullListDiffer() throws Exception {
		Graph g = data(triplify(properties("Nested")));
		// Row 2 has an empty list: the container exists without members
		Node tags2 = NodeFactory.createURI(ROOT + "#row2/tags");
		assertTrue(g.contains(row(2), xyz("tags"), tags2));
		assertFalse(g.contains(tags2, Node.ANY, Node.ANY));
		// Row 3 has a null list: no triple at all
		assertFalse(g.contains(row(3), xyz("tags"), Node.ANY));
	}

	@Test
	public void registeredForExtensionAndMediaType() {
		ParquetTriplifier t = new ParquetTriplifier();
		assertTrue(t.getExtensions().contains("parquet"));
		assertTrue(t.getMimeTypes().contains("application/vnd.apache.parquet"));
	}
}
