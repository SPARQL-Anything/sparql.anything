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

public class BGPInterpretation {
	private int hashCode = -1;

	private BGPInterpretation previousInterpretation;
	private Set<BGPInterpretation> nextInterpretations;

	private Map<Node, NodeInterpretation> signature;
	private boolean isFinal = true;
	private void init(Map<Node, NodeInterpretation> signature){
		this.nextInterpretations = new HashSet<BGPInterpretation>();
		this.previousInterpretation = null;
		this.signature = signature;
		HashCodeBuilder b = new HashCodeBuilder();
		for(Map.Entry<Node, NodeInterpretation> en :signature.entrySet()){
			b.append(en.getKey());
			b.append(en.getValue());
			if(en.getValue() instanceof NodeInterpretation.Subject || en.getValue() instanceof NodeInterpretation.Predicate || en.getValue() instanceof NodeInterpretation.Object){
				isFinal = false;
			}
		}
		this.hashCode = b.toHashCode();
	}

	public BGPInterpretation(BGPConstraints constraints){
		init(constraints.interpretations());
	}

	public BGPInterpretation(BGPInterpretation previousInterpretation, Map<Node,NodeInterpretation> thisInterpretation){
		init(thisInterpretation);
		//
		this.previousInterpretation = previousInterpretation;
		this.previousInterpretation.andNext(this);
	}

	void andNext(BGPInterpretation next){
		this.nextInterpretations.add(next);
	}

	public Set<BGPInterpretation> next(){
		return Collections.unmodifiableSet(nextInterpretations);
	}

	public BGPInterpretation previous(){
		return previousInterpretation;
	}

	public Map<Node, NodeInterpretation> signature(){
		return Collections.unmodifiableMap(signature);
	}

	public boolean isInitialState(){
		return previousInterpretation == null;
	}

	public boolean isFinalState(){
		return isFinal;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(this.getClass()) && obj.hashCode() == this.hashCode();
	}

	public static String toString(BGPInterpretation BGPInterpretation) {
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		boolean f = true;
		for(Map.Entry<Node, NodeInterpretation> en: BGPInterpretation.signature().entrySet()){
			if(f){
				f = false;
			}else{
				sb.append(", ");
			}
			sb.append(en.getKey());
			sb.append(" -> ");
			sb.append(en.getValue().type().getSimpleName());
		}
		sb.append(" }");
		return sb.toString();
	}
}
