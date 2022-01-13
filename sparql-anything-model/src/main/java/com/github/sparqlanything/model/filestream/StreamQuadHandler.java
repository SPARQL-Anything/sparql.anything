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

import com.github.sparqlanything.model.TripleFilteringFacadeXGraphBuilder;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is temporary and should be replaced by an alternative implementation of FacadeXGraphBuilder that does not populate a DG.
 * Triplifier#triplify should be changed to not return (nor know about) DatasetGraph, the caller should get it from the FacadeXGraphBuilder.
 * FacadeXGraphBuilder interface should be known by the Triplifier, and be a subinterface of FacadeXBuilder, only to be used when the builder needs to populate a dataset graph.
 * FacadeXBuilder interface should be the one extended by a QuadHandler interface
 * The QuadHandler interface shall be implemented by this StreamQuadHandler
 */
public class StreamQuadHandler extends TripleFilteringFacadeXGraphBuilder {
	protected static final Logger log = LoggerFactory.getLogger(StreamQuadHandler.class);
	private LinkedBlockingQueue<Object>  queue;
	private static final Node unionGraph = NodeFactory.createURI("urn:x-arq:UnionGraph");
	public int debug = 0;
	private Quad target;
	private List<Object> index;

	protected StreamQuadHandler(Properties properties, Quad target, Op op, LinkedBlockingQueue<Object> queue, List<Object> index) {
		super(Triplifier.getResourceId(properties), op, DatasetGraphFactory.create(), properties);
		this.queue = queue;
		this.target = target;
		this.index = index;
	}

//	public Node getRoot(){
//		return root;
//	}

	public Quad getTarget(){
		return target;
	}

	/**
	 * Do not populate the DG but send the quad to the listener
	 */
	@Override
	public boolean add(Node graph, Node subject, Node predicate, Node object) {
		Quad q = new Quad(graph, subject, predicate, object);
		if(log.isDebugEnabled()){
			log.trace("{} matches ", q);
			debug++;
		}

		if (match(graph, subject, predicate, object)) {
			// Relevant to any of following invocations
			try {
				index.add(q);
				queue.put(q);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		return false;
	}
}
