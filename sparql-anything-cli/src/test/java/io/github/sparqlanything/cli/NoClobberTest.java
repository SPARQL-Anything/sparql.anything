/*
 * Copyright (c) 2025 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

/*
 */

package io.github.sparqlanything.cli;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

/**
 * See issue #528
 * Queries don't do anything, some of them result in incomplete service clauses... it doesn't matter, we only need to check if the output file was touched.
 */
public class NoClobberTest {
	//Logger L = LoggerFactory.getLogger(NoClobberTest.class);
	private File tempFile = null;
	private Long before = null;
	private final String queryFile = Objects.requireNonNull(getClass().getClassLoader().getResource("NoClobber.sparql")).getFile();
	private final String paramFile = Objects.requireNonNull(getClass().getClassLoader().getResource("NoClobberParams.csv")).getFile();
	private final String csvFile = Objects.requireNonNull(getClass().getClassLoader().getResource("NoClobberTest.csv")).getFile();
	@Before
	public void before() throws Exception {
		tempFile = File.createTempFile("no-clobber-test",".ttl");
		before = tempFile.lastModified();
		// We halt it for a while to make sure some time passes ...
		Thread.sleep(100);
	}

	@Test
	public void overwrite_inline() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			"CONSTRUCT { [] a <Something> } WHERE { ?x ?y ?z }",
			"-o",
			tempFile.getAbsolutePath()
		});
		overwritten(true);
	}

	@Test
	public void overwrite_queryFile() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			queryFile,
			"-o",
			tempFile.getAbsolutePath()
		});
		overwritten(true);
	}

	@Test
	public void dontoverwrite_inline() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			"CONSTRUCT { [] a <Something> } WHERE { ?x ?y ?z }",
			"-o",
			tempFile.getAbsolutePath(),
			"-nc"
		});
		overwritten(false);
	}

	@Test
	public void dontoverwrite_queryFile() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			queryFile,
			"-o",
			tempFile.getAbsolutePath(),
			"-nc"
		});
		overwritten(false);
	}


	@Test
	public void dontoverwrite_queryFile_conf() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			queryFile,
			"-c",
			"location=" + csvFile,
			"-o",
			tempFile.getAbsolutePath(),
			"-nc"
		});
		overwritten(false);
	}

	@Test
	public void overwrite_queryFile_conf() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			queryFile,
			"-c",
			"location=" + csvFile,
			"-o",
			tempFile.getAbsolutePath()
		});
		overwritten(true);
	}

	@Test
	public void dontoverwritelong_inline() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			"CONSTRUCT { [] a <Something> } WHERE { ?x ?y ?z }",
			"-o",
			tempFile.getAbsolutePath(),
			"--no-clobber"
		});
		overwritten(false);
	}

	@Test
	public void dontoverwritelong_queryFile() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			queryFile,
			"-o",
			tempFile.getAbsolutePath(),
			"--no-clobber"
		});
		overwritten(false);
	}

	@Test
	public void overwriteWithParams() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			queryFile,
			"-v",
			paramFile,
			"-c",
			"location=" + csvFile,
			"-o",
			tempFile.getAbsolutePath()
		});
		overwritten(true);
	}

	@Test
	public void dontoverwriteWithParams() throws Exception {
		SPARQLAnything.callMain(new String[]{
			"-q",
			queryFile,
			"-v",
			paramFile,
			"-c",
			"location=" + csvFile,
			"-o",
			tempFile.getAbsolutePath(),
			"-nc"
		});
		overwritten(false);
	}

	private void overwritten(boolean expect)  {
		Long after = tempFile.lastModified();
		//L.info("before {} and after {}", (Object) before, (Object) after);
		// If overwrite, show content
		//if(expect){
		//	L.info("file overwritten: {}", tempFile.getAbsolutePath());
		//	L.info("content: {}", Files.readString(tempFile.toPath(), StandardCharsets.UTF_8));
		//}
		Assert.assertTrue(
			(expect) ? after > before : after.equals(before)
		);
	}
}
