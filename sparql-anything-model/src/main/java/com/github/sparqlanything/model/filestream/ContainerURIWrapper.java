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

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.sparql.core.Quad;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ContainerURIWrapper extends Node_URI implements ContainerNodeWrapper {
	private final List<Quad> data;
	protected int contextHash;
	protected boolean isCompleted = false;
	protected Boolean isRoot = null;
	protected ContainerNodeWrapper parent = null;
	protected Node parentSlot = null;
	protected ContainerURIWrapper(int contextHash, String uri) {
		super(uri);
		this.contextHash = contextHash;
		this.data = Collections.synchronizedList(new ArrayList<Quad>());
	}

	@Override
	public Iterator<Quad> find(final Node graph, final Node subject, final Node property, final Node value) {
		if(!subject.equals(this)){
			throw new RuntimeException("Inconsistent state");
		}
		return new Iterator<Quad>() {
			int idx = 0;
			Quad next = null;
			@Override
			public boolean hasNext() {
				while(!isCompleted()) {
					if(data.size() < idx + 1){
						// wait
						continue;
					}
					Quad q = data.get(idx);
					idx++;
					// Pass if matches
					if(q.matches(graph, subject, property, value)){
						next = q;
						return true;
					}
				}
				return false;
			}

			@Override
			public Quad next() {
				return next;
			}
		};
	}

	@Override
	public synchronized void add(Quad quad) {
		this.data.add(quad);
	}

	@Override
	public int contextHash() {
		return contextHash;
	}

	@Override
	public synchronized ContainerNodeWrapper getParent() {
		return this.parent;
	}

	@Override
	public synchronized void setParent(ContainerNodeWrapper parent, Node slot) {
		// Cannot be called twice.
		if(this.parent != null || this.parentSlot != null || this.isRoot != null){
			throw new RuntimeException("Inconsistent state");
		}
		this.parent = parent;
		this.parentSlot = slot;
		this.isRoot = false;
	}

	@Override
	public synchronized Node getParentSlot() {
		return parentSlot;
	}

	@Override
	public synchronized boolean isRoot() {
		return isRoot;
	}

	@Override
	public synchronized void setRoot(boolean isRoot) {
		// Cannot be called twice.
		if(this.parent != null || this.parentSlot != null || this.isRoot != null){
			throw new RuntimeException("Inconsistent state");
		}
		this.isRoot = isRoot;
	}

	@Override
	public synchronized  boolean isCompleted() {
		return isCompleted;
	}

	@Override
	public synchronized void setCompleted() {
		isCompleted = true;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof ContainerURIWrapper && contextHash() == ((ContainerURIWrapper) other).contextHash() && this.getURI().equals(((ContainerURIWrapper)other).getURI());
	}
}
