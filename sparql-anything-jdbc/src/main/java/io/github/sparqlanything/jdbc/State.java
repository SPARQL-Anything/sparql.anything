/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.jdbc;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.jena.graph.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class State {
	private int hashCode = -1;

	private State previous;
	private Set<State> next;

	private Map<Node,Interpretation> signature;

	private void init(Map<Node,Interpretation> signature){
		this.signature = signature;
		HashCodeBuilder b = new HashCodeBuilder();
		for(Map.Entry<Node,Interpretation> en :signature.entrySet()){
			b.append(en.getKey());
			b.append(en.getValue());
		}
		this.hashCode = b.toHashCode();
	}

	public State(Map<Node,Interpretation> signature){
		init(signature);
		this.next = new HashSet<State>();
		this.previous = null;
	}

	public State(State previous, Node node, Interpretation hypothesis){
		// Generate hash code

		Map<Node,Interpretation> asignature = new HashMap<>();
		for(Map.Entry<Node,Interpretation> en : previous.signature().entrySet()){
			asignature.put(en.getKey(),en.getValue());
		}
		// Override local hypothesis
		asignature.put(node, hypothesis);
		init(asignature);
		//
		this.previous = previous;
		this.previous.andNext(this);
	}

	void andNext(State next){
		this.next.add(next);
	}

	public Set<State> next(){
		return Collections.unmodifiableSet(next);
	}

	public State previous(){
		return previous;
	}

	public Map<Node,Interpretation> signature(){
		return Collections.unmodifiableMap(signature);
	}

	public boolean isInitialState(){
		return previous == null;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(this.getClass()) && obj.hashCode() == this.hashCode();
	}
}
