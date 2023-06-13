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

package io.github.sparqlanything.csv;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.TxnType;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class OnDiskTest {

	private static final Logger logger = LoggerFactory.getLogger(OnDiskTest.class);
	private final CSVTriplifier triplifier = new CSVTriplifier();

	@Test
	public void testWithOnDiskGraph1() throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
//		properties.setProperty("ondisk", "/tmp");
		File tmp = Files.createTempDirectory(null).toFile();
		boolean isTmpFactoryCreated = tmp.mkdirs();
		logger.trace("tmp directory created? {}", isTmpFactoryCreated);
		properties.setProperty("ondisk", tmp.getAbsolutePath());
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("./test3.csv")).toString();
		properties.setProperty(IRIArgument.LOCATION.toString(), location);

		FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(location, properties);
		triplifier.triplify(properties, b);
		DatasetGraph graph = b.getDatasetGraph();

		// end the write txn because triplifiers don't do that, FacadeXOpExecutor does
		graph.commit();
		graph.end();

		graph.begin(TxnType.READ);
		AtomicInteger ai = new AtomicInteger();
		graph.find(null, null, null, null).forEachRemaining(q -> ai.incrementAndGet());
		Assert.assertEquals(ai.get(), 21);
		graph.end();
		FileUtils.deleteQuietly(tmp);
	}

	@Test
	public void testWithOnDiskGraph2() throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
		//properties.setProperty("ondisk", "/tmp");
		File tmp = Files.createTempDirectory(null).toFile();
		boolean isTmpFactoryCreated = tmp.mkdirs();
		logger.trace("tmp directory created? {}", isTmpFactoryCreated);
		properties.setProperty("ondisk", tmp.getAbsolutePath());
		String location = Objects.requireNonNull(getClass().getClassLoader().getResource("1.csv")).toString();
		properties.setProperty(IRIArgument.LOCATION.toString(), location);
		FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(location, properties);
		triplifier.triplify(properties, b);
		DatasetGraph graph = b.getDatasetGraph();
		// end the write txn because triplifiers don't do that, FacadeXOpExecutor does
		graph.commit();
		graph.end();

		graph.begin(TxnType.READ);
		AtomicInteger ai = new AtomicInteger();
		graph.find(null, null, null, null).forEachRemaining(q -> ai.incrementAndGet());
		Assert.assertEquals(ai.get(), 13);
		graph.end();
		FileUtils.deleteQuietly(tmp);
	}
}
