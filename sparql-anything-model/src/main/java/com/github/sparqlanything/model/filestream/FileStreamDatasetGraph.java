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

import org.apache.jena.graph.*;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.TxnType;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapStd;
import org.apache.jena.shared.Lock;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.graph.GraphOps;
import org.apache.jena.sparql.graph.PrefixMappingMem;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class FileStreamDatasetGraph implements DatasetGraph {
	private static Logger log = LoggerFactory.getLogger(FileStreamDatasetGraph.class);
	private final PrefixMap prefixMap;
	private final FileStreamManager manager;
	private final HashMap<Node,FileStreamGraph> graphs;

	public FileStreamDatasetGraph(FileStreamManager manager){
		this.prefixMap = new PrefixMapStd();
		this.manager = manager;
		this.graphs = new HashMap<Node, FileStreamGraph>();
		for (String ds : manager.getDataSourceIds()) {
			log.trace("Data source: {}", ds);
			addFileStreamGraph(ds);
		}
	}

	private void addFileStreamGraph(String ds) {
		final Node g = NodeFactory.createURI(ds);
		PrefixMapping pm = new PrefixMappingMem();
		pm.setNsPrefixes(prefixMap.getMapping());
		// A file stream graph only proxies the find triple method to the general find quad
		graphs.put(g, new FileStreamGraph(pm) {
			@Override
			protected ExtendedIterator<Triple> findInGraph(Node node, Node node1, Node node2) {
				final Iterator<Quad> result = FileStreamDatasetGraph.this.findNG(g, node, node1, node2);
				return new ExtendedIterator<Triple>() {
					@Override
					public Triple removeNext() {
						throw new UnsupportedOperationException();
					}

					@Override
					public <X extends Triple> ExtendedIterator<Triple> andThen(Iterator<X> iterator) {
						throw new UnsupportedOperationException();
					}

					@Override
					public ExtendedIterator<Triple> filterKeep(Predicate<Triple> predicate) {
						throw new UnsupportedOperationException();
					}

					@Override
					public ExtendedIterator<Triple> filterDrop(Predicate<Triple> predicate) {
						throw new UnsupportedOperationException();
					}

					@Override
					public <U> ExtendedIterator<U> mapWith(Function<Triple, U> function) {
						throw new UnsupportedOperationException();
					}

					@Override
					public List<Triple> toList() {
						throw new UnsupportedOperationException();
					}

					@Override
					public Set<Triple> toSet() {
						throw new UnsupportedOperationException();
					}

					@Override
					public void close() {
						// Ignore
					}

					@Override
					public boolean hasNext() {
						return result.hasNext();
					}

					@Override
					public Triple next() {
						Quad q = result.next();
						return q.asTriple();
					}
				};
			}
		});
	}

	@Override
	public Graph getDefaultGraph() {
		return getUnionGraph();
	}

	@Override
	public Graph getGraph(Node node) {
		log.trace("getGraph {}", node);
		return graphs.get(node);
	}

	@Override
	public Graph getUnionGraph() {
		return GraphOps.unionGraph(this);
	}

	@Override
	public boolean containsGraph(Node node) {
		return graphs.containsKey(node);
	}

	@Override
	public void setDefaultGraph(Graph graph) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addGraph(Node node, Graph graph) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeGraph(Node node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Node> listGraphNodes() {
		return graphs.keySet().iterator();
	}

	@Override
	public void add(Quad quad) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Quad quad) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(Node node, Node node1, Node node2, Node node3) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Node node, Node node1, Node node2, Node node3) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAny(Node node, Node node1, Node node2, Node node3) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Quad> find() {
		return find(Node_ANY.ANY, Node_ANY.ANY, Node_ANY.ANY, Node_ANY.ANY);
	}

	@Override
	public Iterator<Quad> find(Quad quad) {
		return find(quad.getGraph(), quad.getSubject(), quad.getPredicate(), quad.getObject());
	}

	@Override
	public Iterator<Quad> find(Node node, Node node1, Node node2, Node node3) {
		// not sure how to handle the default graph
		return findNG(node, node1, node2, node3);
	}

	@Override
	public Iterator<Quad> findNG(Node node, Node node1, Node node2, Node node3) {
		return manager.find(node, node1, node2, node3);
	}

	@Override
	public boolean contains(Node node, Node node1, Node node2, Node node3) {
		return find(node, node1, node2, node3).hasNext();
	}

	@Override
	public boolean contains(Quad quad) {
		return find(quad).hasNext();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return false; // there is always one graph with one root!
	}

	@Override
	public Lock getLock() {
		// this is not transactional
		throw new UnsupportedOperationException();
	}

	@Override
	public Context getContext() {
		return manager.getContext();
	}

	@Override
	public long size() {
		Iterator<Node> g = listGraphNodes();
		int c = 0;
		while (g.hasNext()) {
			g.next();
			c++;
		}
		return c;
	}

	@Override
	public void close() {
		// Ignore
	}

	@Override
	public PrefixMap prefixes() {
		return prefixMap;
	}

	@Override
	public boolean supportsTransactions() {
		return false;
	}

	@Override
	public void begin(TxnType txnType) {

	}

	@Override
	public void begin(ReadWrite readWrite) {

	}

	@Override
	public boolean promote(Promote promote) {
		return false;
	}

	@Override
	public void commit() {

	}

	@Override
	public void abort() {

	}

	@Override
	public void end() {

	}

	@Override
	public ReadWrite transactionMode() {
		return null;
	}

	@Override
	public TxnType transactionType() {
		return null;
	}

	@Override
	public boolean isInTransaction() {
		return false;
	}
}
