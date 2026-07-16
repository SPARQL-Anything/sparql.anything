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
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test that runs a real SPARQL Anything query against a JSON
 * resource stored in an S3 bucket. The bucket is not AWS itself but a real
 * S3-compatible service (MinIO) started via Testcontainers, so this covers
 * the full path: the SPARQL {@code SERVICE <x-sparql-anything:...>} clause,
 * the {@code s3-support} module's {@link io.github.sparqlanything.s3.S3InputService}
 * (discovered automatically via {@code ServiceLoader} once the module is on
 * the classpath), the real AWS SDK client, and a real MinIO container.
 * <p>
 * Skipped automatically if Docker is not available.
 */
public class S3InputTest {

	private static final String ACCESS_KEY = "minioadmin";
	private static final String SECRET_KEY = "minioadmin";
	private static final String BUCKET_NAME = "it-test-bucket";
	private static final String FILE_KEY = "people.json";
	private static final String FILE_CONTENT = "{ \"people\": [ { \"name\": \"Vincent\" }, { \"name\": \"Jules\" }, { \"name\": \"Beatrix\" } ] }";
	private static final String REGION = Region.US_EAST_1.toString();

	private static GenericContainer<?> minioContainer;
	private static S3Client s3Client;
	private static URI endpoint;

	private static boolean dockerAvailable = false;

	@BeforeClass
	public static void setUpClass() throws Exception {

		// Detect Docker availability once for the whole class: if Docker isn't
		// installed/running (e.g. on a CI runner without it), every test here
		// is skipped via Assume rather than failing.
		try {
			dockerAvailable = DockerClientFactory.instance().isDockerAvailable();
		} catch (Exception e) {
			dockerAvailable = false;
		}

		Assume.assumeTrue("Docker not available", dockerAvailable);

		// Start a real MinIO container to stand in for S3.
		minioContainer = new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
			.withExposedPorts(9000)
			.withCommand("server /data");
		minioContainer.start();

		// MinIO's container port is mapped to a random free host port, so the
		// endpoint URL has to be built after the container has started.
		Integer mappedPort = minioContainer.getMappedPort(9000);
		endpoint = new URI("http://localhost:" + mappedPort);

		// A plain AWS SDK client used only to seed test data (create the
		// bucket and upload the file) before running the SPARQL query.
		// pathStyleAccessEnabled(true) is required for MinIO, which doesn't
		// support virtual-hosted-style bucket addressing by default.
		s3Client = S3Client.builder()
			.endpointOverride(endpoint)
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
			.region(Region.of(REGION))
			.serviceConfiguration(b -> b.pathStyleAccessEnabled(true))
			.build();

		s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build());
		s3Client.putObject(
			PutObjectRequest.builder().bucket(BUCKET_NAME).key(FILE_KEY).build(),
			RequestBody.fromString(FILE_CONTENT)
		);
	}

	@AfterClass
	public static void tearDownClass() {
		// Release the seeding client and stop the container; guarded with
		// null checks in case setUpClass() was skipped (Docker unavailable).
		if (s3Client != null) s3Client.close();
		if (minioContainer != null) minioContainer.stop();
	}

	@Test
	public void testQueryResourceInS3Bucket() {

		Assume.assumeTrue("Docker not available", dockerAvailable);

		// A real SPARQL Anything query: the SERVICE clause routes the
		// request through S3InputService (discovered via ServiceLoader
		// because sparql-anything-s3-support is on the classpath), using
		// the fx:s3.* properties to configure the MinIO endpoint,
		// credentials, bucket, and key.
		String queryStr =
			"PREFIX fx:  <http://sparql.xyz/facade-x/ns/> "
				+ "PREFIX xyz: <http://sparql.xyz/facade-x/data/> "
				+ "SELECT ?name WHERE { "
				+ "  SERVICE <x-sparql-anything:> { "
				+ "    fx:properties "
				+ "      fx:media-type \"application/json\" ; "
				+ "      fx:s3.endpoint \"" + endpoint + "\" ; "
				+ "      fx:s3.bucket-name \"" + BUCKET_NAME + "\" ; "
				+ "      fx:s3.key \"" + FILE_KEY + "\" ; "
				+ "      fx:s3.access-key \"" + ACCESS_KEY + "\" ; "
				+ "      fx:s3.secret-key \"" + SECRET_KEY + "\" ; "
				+ "      fx:s3.region \"" + REGION + "\" . "
				+ "    ?root xyz:people/fx:anySlot/xyz:name ?name "
				+ "  } "
				+ "}";

		Query query = QueryFactory.create(queryStr);
		Dataset dataset = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ResultSet rs = QueryExecutionFactory.create(query, dataset).execSelect();

		java.util.Set<String> names = new java.util.HashSet<>();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			names.add(qs.getLiteral("name").getString());
		}

		assertTrue(names.contains("Vincent"));
		assertTrue(names.contains("Jules"));
		assertTrue(names.contains("Beatrix"));
		assertEquals(3, names.size());
	}
}
