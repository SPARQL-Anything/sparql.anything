package io.github.sparqlanything.it;

import io.github.sparqlanything.cli.SPARQLAnything;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.QueryFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/** ReuseQueryTest#reuseSelectQueryTest, but via the CLI. */
public class ReuseQueryCLITest {
	private final static Logger L = LoggerFactory.getLogger(ReuseQueryCLITest.class);
	private static final String OUTER = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> " +
		"SELECT ?s ?p ?o { SERVICE <x-sparql-anything:query=%s> { } FILTER(?o = fx:Root) }";

	private static void assertOneRootRow(String out) throws Exception {
		List<CSVRecord> records = new CSVParser(new StringReader(out),
			CSVFormat.DEFAULT.withFirstRecordAsHeader()).getRecords();
		Assert.assertEquals(1, records.size());
		Assert.assertEquals("http://sparql.xyz/facade-x/ns/Root", records.get(0).get("o"));
	}

	/** query= as classpath-relative path, as in the IT test. Expected: PASS. */
	@Test
	public void reuseSelectClasspathPath() throws Exception {
		String out = SPARQLAnything.callMain(new String[]{
			"-q", String.format(OUTER, "queryReuse/subQuerySelect.rq"), "-f", "CSV"});
		assertOneRootRow(out);
	}

	private static final String OUTER_BGP = "PREFIX fx: <http://sparql.xyz/facade-x/ns/> " +
		"PREFIX fxe: <http://sparql.xyz/facade-x/engine/> " +
		"SELECT ?s ?p ?o { SERVICE <x-sparql-anything:> { fxe:properties fxe:query \"%s\" . } FILTER(?o = fx:Root) }";

	private File innerQueryFile() throws Exception {
		File inner = Files.createTempFile("subQuerySelect", ".rq").toFile();
		inner.deleteOnExit();
		FileUtils.writeStringToFile(inner, IOUtils.toString(Objects.requireNonNull(
				getClass().getClassLoader().getResource("queryReuse/subQuerySelect.rq")), StandardCharsets.UTF_8),
			StandardCharsets.UTF_8);
		return inner;
	}

	/** Path in the SERVICE IRI, with forward slashes (C:/Users/... on Windows): IRIs cannot contain '\'. */
	@Test
	public void reuseSelectFilesystemPathInIRI() throws Exception {
		String path = innerQueryFile().getAbsolutePath().replace('\\', '/');
		assertOneRootRow(SPARQLAnything.callMain(new String[]{"-q", String.format(OUTER, path), "-f", "CSV"}));
	}

	/** file: URL in the SERVICE IRI (file:/C:/Users/... on Windows). */
	@Test
	public void reuseSelectFileURLInIRI() throws Exception {
		String url = innerQueryFile().toURI().toString();
		assertOneRootRow(SPARQLAnything.callMain(new String[]{"-q", String.format(OUTER, url), "-f", "CSV"}));
	}

	/** Native path (C:\Users\... on Windows) as a BGP option: backslashes escaped as in any SPARQL string. */
	@Test
	public void reuseSelectNativePathInBGP() throws Exception {
		String literal = innerQueryFile().getAbsolutePath().replace("\\", "\\\\");
		assertOneRootRow(SPARQLAnything.callMain(new String[]{"-q", String.format(OUTER_BGP, literal), "-f", "CSV"}));
	}

	@Test
	public void testClassLoaderReadsPath() throws Exception {
		File inner = Files.createTempFile("subQuerySelect", ".rq").toFile();
		inner.deleteOnExit();
		FileUtils.writeStringToFile(inner, IOUtils.toString(Objects.requireNonNull(
				getClass().getClassLoader().getResource("queryReuse/subQuerySelect.rq")), StandardCharsets.UTF_8),
			StandardCharsets.UTF_8);
		L.info("reused query path: {}", inner.getAbsolutePath());
		Assert.assertNotNull(inner.getAbsolutePath());
		Assert.assertNull(getClass().getClassLoader().getResource(inner.getAbsolutePath()));
		// Use jena reader instead
		Assert.assertNotNull(QueryFactory.read(inner.getAbsolutePath()));
	}

	/** Outer query from file (-q <file>), inner via classpath. Isolates CLI query-file handling. */
	@Test
	public void reuseSelectOuterFromFile() throws Exception {
		File outer = Files.createTempFile("reuseSelect", ".rq").toFile();
		outer.deleteOnExit();
		FileUtils.writeStringToFile(outer, String.format(OUTER, "queryReuse/subQuerySelect.rq"), StandardCharsets.UTF_8);
		String out = SPARQLAnything.callMain(new String[]{"-q", outer.getAbsolutePath(), "-f", "CSV"});
		assertOneRootRow(out);
	}
}
