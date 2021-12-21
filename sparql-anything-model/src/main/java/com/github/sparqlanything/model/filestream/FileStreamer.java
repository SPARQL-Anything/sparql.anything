/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.model.filestream;

import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;

import java.io.IOException;
import java.util.Properties;

public class FileStreamer implements Runnable {

	private final Properties properties;
	private final Triplifier triplifier;
	private final StreamQuadHandler handler;
	private final FileStreamerQueue buffer;

	public FileStreamer(Properties properties, Triplifier triplifier, FileStreamerQueue buffer, StreamQuadHandler handler){
		this.properties = properties;
		this.triplifier = triplifier;
		this.handler = handler;
		this.buffer = buffer;
	}
	@Override
	public void run() {
		try {
			triplifier.triplify(properties, handler);
			buffer.setFinished();
		} catch (IOException  | TriplifierHTTPException e) {
			throw new RuntimeException(e);
		}
	}
}
