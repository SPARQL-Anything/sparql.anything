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



package io.github.sparqlanything.s3.test;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.TriplifierHTTPException;
import io.github.sparqlanything.s3.S3InputService;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * End-to-end test for {@link S3InputService} against a real S3-compatible
 * service (MinIO), started via Testcontainers. Unlike the mock-based unit
 * tests in {@link S3InputServiceMockTest}, this exercises the real AWS SDK
 * client, the real HTTP connection, and the real builder configuration
 * (endpoint override, path-style access, credentials), which a mock cannot
 * verify.
 * <p>
 * Skipped automatically if Docker is not available.
 */
public class S3InputServiceIntegrationTest {

	private static final String ACCESS_KEY = "minioadmin";
	private static final String SECRET_KEY = "minioadmin";
	private static final String BUCKET_NAME = "test-bucket";
	private static final String FILE_KEY = "test.txt";
	private static final String FILE_CONTENT = "Hello from MinIO test!";
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
		// bucket and upload the file) before S3InputService is exercised.
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
	public void testS3Location() throws IOException, TriplifierHTTPException {

		Assume.assumeTrue("Docker not available", dockerAvailable);

		// Properties as they would be set by a SPARQL Anything location IRI
		// pointing at an S3 object: S3InputService reads the real endpoint
		// URL from IRIArgument.S3_ENDPOINT (not a boolean flag) to build the
		// AWS SDK client's endpointOverride.
		Properties properties = new Properties();

		properties.setProperty(IRIArgument.S3_ENDPOINT.toString(), endpoint.toString());
		properties.setProperty(IRIArgument.S3_BUCKET_NAME.toString(), BUCKET_NAME);
		properties.setProperty(IRIArgument.S3_KEY.toString(), FILE_KEY);
		properties.setProperty(IRIArgument.S3_ACCESS_KEY.toString(), ACCESS_KEY);
		properties.setProperty(IRIArgument.S3_SECRET_KEY.toString(), SECRET_KEY);
		properties.setProperty(IRIArgument.S3_REGION.toString(), REGION);

		// Read the object back through S3InputService end-to-end: real
		// client, real connection, real bytes off the wire from MinIO.
		String content;
		try (InputStream is = new S3InputService().getInputStream(properties)) {
			content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}

		assertEquals(FILE_CONTENT, content);
	}
}
