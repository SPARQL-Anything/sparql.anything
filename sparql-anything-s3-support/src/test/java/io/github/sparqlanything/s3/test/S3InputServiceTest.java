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
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link S3InputService}.
 * <p>
 * {@code S3Client} is built internally via {@code S3Client.builder()}, a
 * static factory method. Rather than mocking that static method (which
 * requires bytecode instrumentation and is fragile across JDK/Mockito
 * version combinations), the service exposes a constructor that accepts the
 * client factory to use. Tests inject a factory that always returns a plain
 * Mockito mock, so no static/bytecode mocking is needed at all.
 * <p>
 * These tests focus on the InputStream/S3Client lifecycle: the client must stay
 * open while the stream is being read, and must be closed exactly once, either
 * when the caller closes the stream or when {@code getObject()} fails.
 * <p>
 * For an end-to-end test against a real S3-compatible service, see
 * {@link S3InputServiceTest2}, which uses Testcontainers with MinIO.
 */
public class S3InputServiceTest {

	// Minimal set of Properties the service reads via PropertyUtils/IRIArgument;
	// values are fake since no real S3 endpoint is ever contacted in this class.
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
	 * The stream returned by {@code getInputStream} must stay readable until
	 * the caller explicitly closes it, and closing it must close the
	 * underlying {@code S3Client} exactly once (see
	 * {@code S3ClientClosingInputStream} in the production class).
	 */
	@Test
	public void getInputStream_readsContent_beforeExplicitClose() throws Exception {
		Properties props = baseProperties();
		byte[] payload = "hello world".getBytes();

		// Wrap the fake payload in the real AWS SDK response-stream types so
		// the mock's getObject() return value behaves like the genuine SDK call.
		AbortableInputStream abortable = AbortableInputStream.create(new ByteArrayInputStream(payload));
		ResponseInputStream<GetObjectResponse> responseStream =
			new ResponseInputStream<>(GetObjectResponse.builder().build(), abortable);

		S3Client clientMock = mock(S3Client.class);
		when(clientMock.getObject(any(GetObjectRequest.class)))
			.thenReturn((ResponseInputStream) responseStream);

		// Inject the mock via the factory constructor instead of going
		// through S3Client.builder(): see the class Javadoc for why.
		S3InputService service = new S3InputService(props2 -> clientMock);
		InputStream result = service.getInputStream(props);

		// The client has NOT been closed yet, so reading still works.
		byte[] read = result.readAllBytes();
		assertEquals("hello world", new String(read));

		verify(clientMock, never()).close();

		// Closing the returned stream must close the underlying client too.
		result.close();
		verify(clientMock, times(1)).close();
	}

	/**
	 * If {@code getObject()} fails before any stream is handed back to the
	 * caller, the service must not leak the client: it has to close it
	 * itself, since nobody else ever gets a reference to it.
	 */
	@Test
	public void getInputStream_closesClient_ifGetObjectThrows() {
		Properties props = baseProperties();

		S3Client clientMock = mock(S3Client.class);
		when(clientMock.getObject(any(GetObjectRequest.class)))
			.thenThrow(NoSuchKeyException.builder().message("not found").build());

		S3InputService service = new S3InputService(props2 -> clientMock);

		// getObject() fails before any stream is ever returned to the
		// caller, so the service itself must clean up the client.
		assertThrows(NoSuchKeyException.class, () -> service.getInputStream(props));

		verify(clientMock, times(1)).close();
	}

	/**
	 * Calling close() more than once on the returned stream must not close
	 * the underlying S3Client more than once (some SDK/HTTP clients log
	 * warnings or misbehave on repeated close calls).
	 */
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

		S3InputService service = new S3InputService(props2 -> clientMock);
		InputStream result = service.getInputStream(props);

		result.close();
		result.close(); // calling close() twice must not close the client twice

		verify(clientMock, times(1)).close();
	}
}
