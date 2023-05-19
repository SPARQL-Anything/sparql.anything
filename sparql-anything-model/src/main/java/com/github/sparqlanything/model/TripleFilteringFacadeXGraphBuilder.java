/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sparqlanything.model;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 *
 */
public class TripleFilteringFacadeXGraphBuilder extends BaseFacadeXGraphBuilder {
	private final Op op;
	private OpComponentsAnalyser analyser;
	private Logger log = LoggerFactory.getLogger(TripleFilteringFacadeXGraphBuilder.class);
	
	public TripleFilteringFacadeXGraphBuilder(String resourceId, Op op, DatasetGraph ds, Properties properties) {
		super(resourceId,  properties);
		this.op = op;
		analyser = new OpComponentsAnalyser();
		op.visit(analyser);
	}

	public Op getOp(){
		return op;
	}
	public TripleFilteringFacadeXGraphBuilder(String resourceId, Op op, Properties properties) {
		// don't make a DatasetGraph here
		// instead let BaseFacadeXBuilder do all the DatasetGraph making
		this(resourceId, op, null, properties);
	}

	@Override
	public boolean add(Node graph, Node subject, Node predicate, Node object) {
		if (analyser.match(graph, subject, predicate, object)) {
//			datasetGraph.getGraph(graph).add(new Triple(subject, predicate, object));
			return super.add(graph, subject, predicate, object);
		}
		return false;
	}
}
