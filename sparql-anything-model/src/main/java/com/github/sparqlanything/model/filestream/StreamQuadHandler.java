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

import com.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.TripleFilteringFacadeXGraphBuilder;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
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

	private ContainerNodeWrapper root = null;
	private ContainerNodeWrapper currentContainer = null;
	private ContainerNodeWrapper nextContainer = null;
	private Quad target;

	protected StreamQuadHandler(Properties properties, Quad target, Op op, LinkedBlockingQueue<Object> queue) {
		super(Triplifier.getResourceId(properties), op, DatasetGraphFactory.create(), properties);
		this.queue = queue;
		this.target = target;
	}

	public ContainerNodeWrapper getRoot(){
		return root;
	}

	public Quad getTarget(){
		return target;
	}

	/**
	 * Do not populate the DG but send the quad to the listener
	 */
	@Override
	public boolean add(Node graph, Node subject, Node predicate, Node object) {
		if (match(graph, subject, predicate, object)) {
			Quad q = new Quad(graph, subject, predicate, object);
			if(log.isDebugEnabled()){
				log.trace("{} matches ", q);
				debug++;
			}
			try {
				Quad rewrittenQuad = rewrite(q);
				queue.put(rewrittenQuad);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		return false;
	}

	private Quad rewrite(Quad q) {
		// Subjects are always containers.
		Node subject = q.getSubject();
		// Is the first container being explored?
		if(currentContainer == null){
			ContainerURIWrapper container = new ContainerURIWrapper(hashCode(), subject.getURI());
			subject = container;
			currentContainer = container;
		} else if( ((Node) currentContainer).getURI().equals(subject.getURI()) ) {
			// Is the same URI as the current container under exploration?
			subject = (Node) currentContainer;
		} else if( nextContainer != null && ((Node) nextContainer).getURI().equals(subject.getURI()) ) {
			// Is this the next container?
			currentContainer = nextContainer;
			nextContainer = null;
		} else if( ((Node) currentContainer.getParent()).getURI().equals(subject.getURI()) )  {
			// Is the subject the parent of the current container?
			// The container is "closed"
			currentContainer.setCompleted();
			currentContainer = currentContainer.getParent();
		} else {
			throw new RuntimeException("Inconsistent state");
		}

		if(q.getPredicate().getURI().equals(RDF.type.getURI()) && q.getObject().getURI().equals(Triplifier.FACADE_X_TYPE_ROOT)){
			// Is it the root declaration?
			currentContainer.setRoot(true);
			this.root = currentContainer;
		}

		Node object = q.getObject();
		if(object.isURI()){
			// Prepare next container
			ContainerURIWrapper container = new ContainerURIWrapper(hashCode(), object.getURI());
			object = container;
			nextContainer = container;
			nextContainer.setParent(currentContainer, q.getPredicate());
		}

		// Remember data in current container
		Quad rewrittenQuad = new Quad(q.getGraph(), subject, q.getPredicate(), object);
		currentContainer.add(rewrittenQuad);
		return new Quad(q.getGraph(), subject, q.getPredicate(), object);
	}


}
