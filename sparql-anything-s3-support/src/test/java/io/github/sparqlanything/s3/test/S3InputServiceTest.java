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
import io.github.sparqlanything.s3.S3InputService;
import org.junit.Test;
import org.mockito.MockedStatic;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link S3InputService}.
 * <p>
 * {@code S3Client} is an interface: the production code never calls
 * {@code new S3Client(...)}, it goes through the fluent builder returned by
 * the static method {@code S3Client.builder()}. Because of that,
 * {@code Mockito.mockConstruction} does not apply here (it only intercepts
 * real constructor calls on concrete classes). Instead, we mock the static
 * method {@code S3Client.builder()} so it returns a builder mock which, in
 * turn, returns our {@code S3Client} mock from {@code build()}.
 * <p>
 * These tests focus on the InputStream/S3Client lifecycle: the client must stay
 * open while the stream is being read, and must be closed exactly once, either
 * when the caller closes the stream or when {@code getObject()} fails.
 * <p>
 * For an end-to-end test against a real S3-compatible service, see
 * {@link S3InputServiceTest2}, which uses Testcontainers with MinIO.
 */
public class S3InputServiceTest {

	private Properties baseProperties() {
		Properties p = new Properties();
		p.setProperty(IRIArgument.S3_ACCESS_KEY.toString(), "AKIAFAKE");
		p.setProperty(IRIArgument.S3_SECRET_KEY.toString(), "secretFake");
		p.setProperty(IRIArgument.S3_REGION.toString(), "us-east-1");
		p.setProperty(IRIArgument.S3_ENDPOINT.toString(), "http://localhost:4566");
		p.setProperty(IRIArgument.S3_BUCKET_NAME.toString(), "my-bucket");
		p.setProperty(IRIArgument.S3_KEY.toString(), "path/to/object.json");
		return p;
	}

	/**
	 * Mocks {@code S3Client.builder()} so that the fluent chain used in
	 * production code (endpointOverride/credentialsProvider/region/
	 * serviceConfiguration/build) returns the given client mock.
	 */
	private MockedStatic<S3Client> mockS3ClientBuilder(S3Client clientToReturn) {
		S3ClientBuilder builderMock = mock(S3ClientBuilder.class, RETURNS_SELF);
		when(builderMock.build()).thenReturn(clientToReturn);

		MockedStatic<S3Client> staticMock = mockStatic(S3Client.class);
		staticMock.when(S3Client::builder).thenReturn(builderMock);
		return staticMock;
	}

	@Test
	public void getInputStream_readsContent_beforeExplicitClose() throws Exception {
		Properties props = baseProperties();
		byte[] payload = "hello world".getBytes();

		AbortableInputStream abortable = AbortableInputStream.create(new ByteArrayInputStream(payload));
		ResponseInputStream<GetObjectResponse> responseStream =
			new ResponseInputStream<>(GetObjectResponse.builder().build(), abortable);

		S3Client clientMock = mock(S3Client.class);
		when(clientMock.getObject(any(GetObjectRequest.class)))
			.thenReturn((ResponseInputStream) responseStream);

		try (MockedStatic<S3Client> ignored = mockS3ClientBuilder(clientMock)) {
			S3InputService service = new S3InputService();
			InputStream result = service.getInputStream(props);

			// The client has NOT been closed yet, so reading still works.
			byte[] read = result.readAllBytes();
			assertEquals("hello world", new String(read));

			verify(clientMock, never()).close();

			// Closing the returned stream must close the underlying client too.
			result.close();
			verify(clientMock, times(1)).close();
		}
	}

	@Test
	public void getInputStream_closesClient_ifGetObjectThrows() {
		Properties props = baseProperties();

		S3Client clientMock = mock(S3Client.class);
		when(clientMock.getObject(any(GetObjectRequest.class)))
			.thenThrow(NoSuchKeyException.builder().message("not found").build());

		try (MockedStatic<S3Client> ignored = mockS3ClientBuilder(clientMock)) {
			S3InputService service = new S3InputService();

			// getObject() fails before any stream is ever returned to the
			// caller, so the service itself must clean up the client.
			assertThrows(NoSuchKeyException.class, () -> service.getInputStream(props));

			verify(clientMock, times(1)).close();
		}
	}

	@Test
	public void getInputStream_closeIsIdempotent() throws Exception {
		Properties props = baseProperties();
		byte[] payload = "abc".getBytes();

		AbortableInputStream abortable = AbortableInputStream.create(new ByteArrayInputStream(payload));
		ResponseInputStream<GetObjectResponse> responseStream =
			new ResponseInputStream<>(GetObjectResponse.builder().build(), abortable);

		S3Client clientMock = mock(S3Client.class);
		when(clientMock.getObject(any(GetObjectRequest.class)))
			.thenReturn((ResponseInputStream) responseStream);

		try (MockedStatic<S3Client> ignored = mockS3ClientBuilder(clientMock)) {
			S3InputService service = new S3InputService();
			InputStream result = service.getInputStream(props);

			result.close();
			result.close(); // calling close() twice must not close the client twice

			verify(clientMock, times(1)).close();
		}
	}
}
