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

import org.apache.jena.sparql.core.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class FileStreamQuadIterator implements Iterator<Quad> {
	private static Logger log = LoggerFactory.getLogger(FileStreamQuadIterator.class);
	public static final String ENDSIGNAL = "endsignal";
	private Quad next = null;
	private LinkedBlockingQueue<Object> queue;
	public FileStreamQuadIterator(LinkedBlockingQueue<Object> queue){
		this.queue = queue;
	}
	@Override
	public boolean hasNext() {
		try {
			Object o = queue.take();
			if(o.equals(ENDSIGNAL)){
				return false;
			}
			next = (Quad) o;
			return true;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Quad next() {
		Quad n = next;
		next = null;
		return n;
	}
}

