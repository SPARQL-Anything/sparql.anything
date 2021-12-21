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

import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.TripleFilteringFacadeXBuilder;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * This is temporary and should be replaced by an alternative implementation of FacadeXGraphBuilder that does not populate a DG.
 * Triplifier#triplify should be changed to not return (nor know about) DatasetGraph, the caller should get it from the FacadeXGraphBuilder.
 * FacadeXGraphBuilder interface should be known by the Triplifier, and be a subinterface of FacadeXBuilder, only to be used when the builder needs to populate a dataset graph.
 * FacadeXBuilder interface should be the one extended by a QuadHandler interface
 * The QuadHandler interface shall be implemented by this StreamQuadHandler
 */
public class StreamQuadHandler extends BaseFacadeXBuilder {
	protected static final Logger log = LoggerFactory.getLogger(StreamQuadHandler.class);
	private FileStreamerQueue  queue;
	private Quad target;
	private static final Node unionGraph = NodeFactory.createURI("urn:x-arq:UnionGraph");
	protected StreamQuadHandler(Properties properties, Quad target, FileStreamerQueue queue) {
		super(Triplifier.getResourceId(properties), DatasetGraphFactory.create(), properties);
		this.target = target;
		this.queue = queue;
	}

	/**
	 * Do not populate the DG but send the quad to the listener
	 */
	@Override
	public boolean add(Node graph, Node subject, Node predicate, Node object) {
		// XXX Duplicated code, shall be done by the superclass, here we should only intercept the triple.
		if(p_null_string != null && object.isLiteral() && object.getLiteral().toString().equals(p_null_string)){
			return false;
		}
		Quad q = new Quad(graph, subject, predicate, object);
		if(match(graph, subject, predicate, object)) {
			log.trace("{} matches {}", q,target);
			try {
				queue.put(q);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	// FIXME Duplicated code, see TripleFilteringFacadeXBuilder
	// XXX Not the same code! This one includes matching the ARQ constant for union graphs
	public boolean match(Node graph, Node subject, Node predicate, Node object) {
		Quad q = target;
		if ((!q.getGraph().isConcrete() || q.getGraph().matches(graph) || q.getGraph().matches(unionGraph))
				&& (!q.getSubject().isConcrete() || q.getSubject().matches(subject))
				&& predicateMatch(q.getPredicate(), predicate)
				&& (!q.getObject().isConcrete() || q.getObject().matches(object))) {
			return true;
		}
		return false;
	}


	/**
	 * FIXME Duplicated code, see TripleFilteringFacadeXBuilder
	 * @param queryPredicate
	 * @param dataPredicate
	 * @return
	 */
	private boolean predicateMatch(Node queryPredicate, Node dataPredicate) {
		// If queryPredicate is fx:anySLot match any container membership property
		if (queryPredicate.isConcrete()
				&& queryPredicate.getURI().equals(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot")) {
			if (dataPredicate.getURI().startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#_")) {
				return true;
			} else {
				return false;
			}
		}
		return (!queryPredicate.isConcrete() || queryPredicate.matches(dataPredicate));
	}

}
