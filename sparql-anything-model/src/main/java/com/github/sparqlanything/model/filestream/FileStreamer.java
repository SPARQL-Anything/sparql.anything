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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

public class FileStreamer implements Runnable {
	private static Logger log = LoggerFactory.getLogger(FileStreamer.class);
	private final Properties properties;
	private final Triplifier triplifier;
	private final StreamQuadHandler handler;
	private final LinkedBlockingQueue<Object> buffer;

	public FileStreamer(Properties properties, Triplifier triplifier, LinkedBlockingQueue<Object> buffer, StreamQuadHandler handler){
		this.properties = properties;
		this.triplifier = triplifier;
		this.handler = handler;
		this.buffer = buffer;
	}
	@Override
	public void run() {
		try {
			log.debug("start seeking quad: {}", handler.getTarget());
			triplifier.triplify(properties, handler);
			buffer.put(FileStreamQuadIterator.ENDSIGNAL);
			if(log.isDebugEnabled()) {
				log.debug("finished seeking quad ({} found): {}", handler.debug, handler.getTarget());
			}
		} catch (IOException | TriplifierHTTPException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
