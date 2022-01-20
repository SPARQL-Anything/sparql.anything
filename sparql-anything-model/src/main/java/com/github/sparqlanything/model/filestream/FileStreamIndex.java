/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FileStreamIndex {
	private final static Logger L = LoggerFactory.getLogger(FileStreamIndex.class);
	//
	private List<Quad> index;
	//
	private IndexMap<Node,List<Quad>> s_index;
	private IndexMap<Node,List<Quad>> p_index;
	private IndexMap<Node,List<Quad>> o_index;
	//
	private IndexMap<Pair<Node,Node>,List<Quad>> sp_index;
	private IndexMap<Pair<Node,Node>,List<Quad>> po_index;
	private IndexMap<Pair<Node,Node>,List<Quad>> so_index;

	private IndexMap<Triple<Node,Node,Node>,Set<Quad>> spo_index;

	// Union Graph
	public final static Node UNIONGRAPH = NodeFactory.createURI("urn:x-arq:UnionGraph");
	private boolean completed = false;

	public FileStreamIndex(){
		index = new ArrayList<>();
		//
		s_index = new IndexMap<>();
		p_index = new IndexMap<>();
		o_index = new IndexMap<>();
		//
		sp_index = new IndexMap<>();
		po_index = new IndexMap<>();
		so_index = new IndexMap<>();
		//
		spo_index = new IndexMap<>();

	}

	public void add(Quad q){

		if(completed == true) {
			throw new RuntimeException();
		}
		L.trace("Add {}", q);
		index.add(q);
		Quad uq = new Quad(UNIONGRAPH, q.getSubject(), q.getPredicate(), q.getObject());
		if(!index.contains(uq)){
			index.add(uq);
		}
		//
		add(s_index, q.getSubject(), q);
		add(p_index, q.getPredicate(),  q);
		add(o_index, q.getObject(),  q);
		//
		add(sp_index, Pair.of(q.getSubject(),q.getPredicate()), q);
		add(po_index, Pair.of(q.getPredicate(),q.getObject()), q);
		add(so_index, Pair.of(q.getSubject(),q.getObject()), q);
		//
		add(spo_index, Triple.of(q.getSubject(),q.getPredicate(),q.getObject()), q);
	}

	public void add(Map index, Object key, Quad value){
		safeGetIndex(index, key).add(value);
		// Generate Union Graph Equivalent
		Quad uq = new Quad(UNIONGRAPH, value.getSubject(), value.getPredicate(), value.getObject());
		if(!safeGetIndex(index, key).contains(uq)){
			safeGetIndex(index, key).add(uq);
		}
	}

	private List<Quad> safeGetIndex(Map ix, Object key){
		synchronized (ix) {
			if (!ix.containsKey(key)) {
				ix.put(key, new ArrayList<>());
			}
			return (List<Quad>) ix.get(key);
		}
	}

	private List<Quad> selectIndex(Quad target){
		// GSPO
		if(target.isConcrete()){
			L.debug("GSPO index selected for {}", target);
			return index;
		}

		// SPO
		if(target.getSubject().isConcrete() && target.getPredicate().isConcrete() && target.getObject().isConcrete()){
			L.debug("SPO index selected for {}", target);
			return safeGetIndex(spo_index, Triple.of(target.getSubject(), target.getPredicate(), target.getObject()));
		}

		// SP
		if(target.getSubject().isConcrete() && target.getPredicate().isConcrete()){
			L.debug("SP index selected for {}", target);
			// If the list is not populated yet, just prepare it to create a valid reference
			return safeGetIndex(sp_index, Pair.of(target.getSubject(), target.getPredicate()));
		}
		// PO
		if(target.getPredicate().isConcrete() && target.getObject().isConcrete()){
			L.debug("PO index selected for {}", target);
			// If the list is not populated yet, just prepare it to create a valid reference
			return safeGetIndex(po_index, Pair.of(target.getPredicate(), target.getObject()));
		}
		// SO
		if(target.getSubject().isConcrete() && target.getObject().isConcrete()){
			L.debug("SO index selected for {}", target);
			// If the list is not populated yet, just prepare it to create a valid reference
			return safeGetIndex(so_index, Pair.of(target.getSubject(), target.getObject()));
		}

		// S
		if(target.getSubject().isConcrete()){
			L.debug("S index selected for {}", target);
			return safeGetIndex(s_index, target.getSubject());
		}
		// P
		if(target.getPredicate().isConcrete()){
			L.debug("P index selected for {}", target);
			return safeGetIndex(p_index, target.getPredicate());
		}
		// O
		if(target.getObject().isConcrete()){
			L.debug("O index selected for {}", target);
			return safeGetIndex(o_index, target.getObject());
		}

		return index;
	}
	public Iterator<Quad> find (Node graph, Node s, Node p, Node o ) {
		final Quad target = new Quad(graph, s,p,o);
		final List<Quad> ix = selectIndex(target);
		return new Iterator<Quad>() {
			int x = 0;
			Quad next = null;
			@Override
			public boolean hasNext() {
				L.trace("hasNext() seeking target: {}", target);
				synchronized (ix) {
					if (ix.contains(target) && next == null) {
						next = target;
						return true;
					} else if (ix.contains(target)) {
						return false;
					}
					// Otherwise, iterate over index
					while (!isCompleted() || x < ix.size()) {
						if (ix.size() <= x) {
							// Wait
							continue;
						}
						Quad qq = ix.get(x);
						x++;

						boolean mg = false;
						boolean ms = false;
						boolean mp = false;
						boolean mo = false;
						if (!target.getGraph().isConcrete() || target.getGraph().matches(qq.getGraph())){
							// If target graph is variable or [], only match with qq not in Union Graph
							if(!target.getGraph().isConcrete() && qq.getGraph().matches(UNIONGRAPH)){
								mg = false;
							} else {
								mg = true;
							}
						}
						if (!target.getSubject().isConcrete() || target.getSubject().matches(qq.getSubject())){
							ms = true;
						}
						if (!target.getPredicate().isConcrete() || target.getPredicate().matches(qq.getPredicate())){
							mp = true;
						}
						if (!target.getObject().isConcrete() || target.getObject().matches(qq.getObject())){
							mo = true;
						}
						if(mg && ms && mp && mo){
							next = qq;
							L.trace("hasNext() {}", next);
							return true;
						}
					}
				}
				return false;
			}

			@Override
			public Quad next() {
				L.trace("next() {}", next);
				Quad ret = next;
				next = null;
				return ret;
			}
		};
	}

	public synchronized void setCompleted(){
		completed = true;
	}

	public synchronized boolean isCompleted(){
		return completed;
	}

	static class IndexMap<K,V> extends HashMap {
		private List<Object> sequential;

		public IndexMap(){
			sequential = new ArrayList<>();
		}

		@Override
		public Object put(Object key, Object value) {
			if(!sequential.contains(key)){
				sequential.add(key);
			}
			return super.put(key, value);
		}

		@Override
		public boolean remove(Object key, Object value) {
			sequential.remove(key);
			return super.remove(key, value);
		}

		public Object getValueAtIndex(int x){
			Object key = sequential.get(x);
			return get(key);
		}

		public Object getKeyAtIndex(int x){
			return sequential.get(x);
		}

		public boolean hasKeyAtIndex(int x){
			return sequential.size() > x;
		}

		@Override
		public void putAll(Map map) {
			Set<Entry> entrySet= map.entrySet();
			for(Entry entry : entrySet) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

}
