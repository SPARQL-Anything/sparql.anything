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

import io.github.sparqlanything.model.Triplifier;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.atlas.lib.CollectionUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class BGPAnalyser {
	final static Logger L = LoggerFactory.getLogger(BGPAnalyser.class);

	private Properties properties;
	private String namesNamespace;
	private OpBGP opBGP;
	private Translation translation;

	private BGPConstraints constraints = null;
	private Set<BGPInterpretation> interpretations = null;
	private InconsistentAssumptionException exception;

	public BGPAnalyser(Properties properties, OpBGP opBGP){
		this.properties = properties;
		this.opBGP = opBGP;
		this.translation = new Translation(properties);
		this.constraints = new BGPConstraints(translation, opBGP);
		this.interpretations = traverse(this.constraints);
	}

	public Translation getTranslation(){
		return translation;
	}
	public OpBGP getOp(){
		return opBGP;
	}

	public BGPConstraints getConstraints(){
		return constraints;
	}

	public Set<BGPInterpretation> getInterpretations(){
		return interpretations;
	}

	public static final Set<Pair<Node, NodeInterpretation>> expand(Map<Node, NodeInterpretation> interpretations){
		Set<Pair<Node, NodeInterpretation>> expansion = new HashSet<>();
		for(Map.Entry<Node, NodeInterpretation> entry:interpretations.entrySet()){
			Set<Triple> tripleSet = ((NodeInterpretation) entry.getValue()).triples();
			Triple[] triples =  tripleSet.toArray(new Triple[tripleSet.size()]);
			if(entry.getValue() instanceof NodeInterpretation.Subject){
				//Subject -> ContainerTable | ContainerRow
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.ContainerTable(entry.getKey(),triples)));
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.ContainerRow(entry.getKey(),triples)));
			}else if(entry.getValue() instanceof NodeInterpretation.Predicate){
				//Predicate -> SlotRow | SlotColumn | TypeProperty
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.SlotRow(entry.getKey(),triples)));
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.SlotColumn(entry.getKey(),triples)));
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.TypeProperty(triples)));
			}else if(entry.getValue() instanceof NodeInterpretation.Object){
				//Object -> ContainerRow | SlotValue | FXRoot | TypeTable
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.ContainerRow(entry.getKey(),triples)));
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.SlotValue(entry.getKey(),triples)));
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.FXRoot(triples)));
				expansion.add(Pair.of(entry.getKey(), new NodeInterpretation.TypeTable(entry.getKey(),triples)));
			}
		}
		return expansion;
	}

	public Set<BGPInterpretation> traverse(BGPConstraints constraints) {
		BGPInterpretation start = new BGPInterpretation(constraints);
		return traverse(start);
	}

	public Set<BGPInterpretation> traverse(BGPInterpretation interpretation){
		Set<BGPInterpretation> nexts = new HashSet<>();
		// Generate a new next state for each possible interpretation
		for (Pair<Node, NodeInterpretation> ii : BGPAnalyser.expand(interpretation.signature())) {
			// Validate new interpretation before adding it to the set
			Map<Node,NodeInterpretation> possible = new HashMap<>();
			possible.putAll(interpretation.signature());
			possible.put(ii.getLeft(),ii.getRight());
			BGPConstraints constrained = new BGPConstraints(translation, opBGP, possible);
			if(!constrained.isException()) {
				BGPInterpretation next = new BGPInterpretation(interpretation, constrained.interpretations());
				nexts.add(next);
			}
		}
		Set<BGPInterpretation> ends = new HashSet<>();
		// Recursively traverse all states
		for (BGPInterpretation n : nexts) {
			if(n.isFinalState()){
				ends.add(n);
			}
			ends.addAll(traverse(n));
		}
		// return next states
		return ends;
	}
}
