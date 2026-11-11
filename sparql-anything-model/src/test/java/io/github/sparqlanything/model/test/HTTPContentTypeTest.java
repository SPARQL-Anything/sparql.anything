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
package io.github.sparqlanything.model.test;

import com.sun.net.httpserver.HttpServer;
import io.github.sparqlanything.model.HTTPHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * See <a href="https://github.com/SPARQL-Anything/sparql.anything/issues/708">Issue 708</a>
 */
public class HTTPContentTypeTest {

	private HttpServer server;
	private final List<String> requests = new CopyOnWriteArrayList<>();

	@Before
	public void start() throws IOException {
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		// Content type with parameters
		serve("/json", "application/json; charset=utf-8", false);
		// HEAD not supported: the content type comes from GET
		serve("/nohead", "text/csv", true);
		// No content type
		serve("/none", null, false);
		server.start();
	}

	private void serve(String path, String contentType, boolean rejectHead) {
		server.createContext(path, exchange -> {
			requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI());
			if (rejectHead && exchange.getRequestMethod().equals("HEAD")) {
				exchange.sendResponseHeaders(405, -1);
				exchange.close();
				return;
			}
			byte[] body = "{\"a\":1}".getBytes(StandardCharsets.UTF_8);
			if (contentType != null) exchange.getResponseHeaders().add("Content-Type", contentType);
			if (exchange.getRequestMethod().equals("HEAD")) {
				exchange.sendResponseHeaders(200, -1);
			} else {
				exchange.sendResponseHeaders(200, body.length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(body);
				}
			}
			exchange.close();
		});
	}

	@After
	public void stop() {
		server.stop(0);
	}

	private URL url(String path) throws IOException {
		return new URL("http://127.0.0.1:" + server.getAddress().getPort() + path);
	}

	@Test
	public void contentTypeWithoutParameters() throws IOException {
		assertEquals("application/json", HTTPHelper.getContentType(url("/json"), new Properties()));
		assertEquals(List.of("HEAD /json"), requests);
	}

	@Test
	public void queryParametersAreSent() throws IOException {
		Properties p = new Properties();
		p.setProperty(HTTPHelper.HTTPQUERY_PREFIX + "year", "2020");
		assertEquals("application/json", HTTPHelper.getContentType(url("/json"), p));
		assertEquals(List.of("HEAD /json?year=2020"), requests);
	}

	@Test
	public void fallbackOnGet() throws IOException {
		assertEquals("text/csv", HTTPHelper.getContentType(url("/nohead"), new Properties()));
		assertEquals(List.of("HEAD /nohead", "GET /nohead"), requests);
	}

	@Test
	public void noContentType() throws IOException {
		assertNull(HTTPHelper.getContentType(url("/none"), new Properties()));
	}

	@Test
	public void notFound() throws IOException {
		assertNull(HTTPHelper.getContentType(url("/missing"), new Properties()));
	}

	@Test
	public void otherMethodsAreNotProbed() throws IOException {
		Properties p = new Properties();
		p.setProperty(HTTPHelper.HTTPMETHOD, "POST");
		assertNull(HTTPHelper.getContentType(url("/json"), p));
		assertTrue(requests.isEmpty());
	}
}
