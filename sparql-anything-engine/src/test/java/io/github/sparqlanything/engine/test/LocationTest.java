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



package io.github.sparqlanything.engine.test;

import io.github.sparqlanything.engine.Location;
import io.github.sparqlanything.model.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class LocationTest {


	private static final String ACCESS_KEY = "minioadmin";
	private static final String SECRET_KEY = "minioadmin";
	private static final String BUCKET_NAME = "test-bucket";
	private static final String FILE_KEY = "test.txt";
	private static final String FILE_CONTENT = "Hello from MinIO test!";
	private static final String REGION =  Region.US_EAST_1.toString();

	private static GenericContainer<?> minioContainer;
	private static S3Client s3Client;
	private static URI endpoint;

	private static boolean dockerAvailable = false;

	@BeforeClass
	public static void setUpClass() throws Exception {

		try {
			dockerAvailable = DockerClientFactory.instance().isDockerAvailable();
		} catch (Exception e) {
			dockerAvailable = false;
		}

		Assume.assumeTrue("Docker not available", dockerAvailable);

		minioContainer = new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
			.withExposedPorts(9000)
			.withCommand("server /data");
		minioContainer.start();

		Integer mappedPort = minioContainer.getMappedPort(9000);
		endpoint = new URI("http://localhost:" + mappedPort);

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
		if (s3Client != null) s3Client.close();
		if (minioContainer != null) minioContainer.stop();
	}

	@Test
	public void testReadFileFromMinio() {

		Assume.assumeTrue("Docker not available", dockerAvailable);


		GetObjectRequest request = GetObjectRequest.builder()
			.bucket(BUCKET_NAME)
			.key(FILE_KEY)
			.build();

		String content = s3Client.getObjectAsBytes(request)
			.asString(StandardCharsets.UTF_8);

		assertEquals(FILE_CONTENT, content);
	}



	private static InputStream getInputStream(URL url, Properties properties) throws IllegalArgumentException, IOException {

		// If local throw exception
		if (url.getProtocol().equals("file")) {
			return url.openStream();
		}

		// If HTTP
		if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {

			if (PropertyUtils.getBooleanProperty(properties, IRIArgument.S3_ENDPOINT)) {
				return Location.getInputStreamFromS3Bucket(url, properties);
			}

			CloseableHttpResponse response = HTTPHelper.getInputStream(url, properties);
			if (!HTTPHelper.isSuccessful(response)) {
				throw new IOException(response.getStatusLine().toString());
			}
			return response.getEntity().getContent();
		}

		// If other protocol, try URL and Connection
		return url.openStream();
	}

	@Test
	public void testS3Location() throws IOException, TriplifierHTTPException {

		Assume.assumeTrue("Docker not available", dockerAvailable);

		Properties properties = new Properties();

		properties.setProperty(IRIArgument.S3_ENDPOINT.toString(), "true");
		properties.setProperty(IRIArgument.S3_BUCKET_NAME.toString(), BUCKET_NAME);
		properties.setProperty(IRIArgument.S3_KEY.toString(), FILE_KEY);
		properties.setProperty(IRIArgument.S3_ACCESS_KEY.toString(), ACCESS_KEY);
		properties.setProperty(IRIArgument.S3_SECRET_KEY.toString(), SECRET_KEY);
		properties.setProperty(IRIArgument.S3_REGION.toString(), REGION);

		InputStream is = getInputStream(endpoint.toURL(), properties);
		String content = IOUtils.toString(is, Charset.defaultCharset());

		assertEquals(FILE_CONTENT, content);
	}
}
