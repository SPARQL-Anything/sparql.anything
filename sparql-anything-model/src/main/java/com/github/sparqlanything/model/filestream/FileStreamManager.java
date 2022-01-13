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

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class FileStreamManager {
	private static Logger log = LoggerFactory.getLogger(FileStreamManager.class);

	private final Context context;
	private final Properties properties;
	private final FileStreamTriplifier triplifier;
	private final Op op;
	private List<Object> index;
	private boolean streamInProgress = false;

	public FileStreamManager(Context context, Op op, Properties properties, FileStreamTriplifier triplifier){
		this.context = context;
		this.op = op;
		this.properties = properties;
		this.triplifier = triplifier;
	}

	private Iterator<Quad> streamFromFile(Quad target){
		// Run Triplifier, intercept triples which are useful to answer the pattern, return them as quads
		// To store indexes of relevant quads
		index = Collections.synchronizedList(new ArrayList<Object>());
		// To stream the currently requested Quads
		LinkedBlockingQueue<Object> buffer = new LinkedBlockingQueue<Object>();
		StreamQuadHandler handler = new StreamQuadHandler(properties, target, op, buffer, index);
		FileStreamer streamer = new FileStreamer(properties, triplifier, buffer, index, handler);
		Thread worker = new Thread(streamer);
		log.debug("Starting thread to seek {}", target);
		worker.start();
		return new FileStreamQuadIterator(buffer);
	}

	public Iterator<Quad> find(Node g, Node s, Node p, Node o){
		Quad target = new Quad(g, s, p, o);
		if (streamInProgress) {
			return new FileStreamIndexIterator(index, new Quad(g,s,p,o));
		} else {
			streamInProgress = true;
			return streamFromFile(target);
		}
	}

	public List<String> getDataSourceIds(){
		return triplifier.getDataSourceIds(properties);
	}

	public Context getContext() {
		return context;
	}
}
