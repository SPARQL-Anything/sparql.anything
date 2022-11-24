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

package com.github.sparqlanything.jdbc;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;

import javax.management.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class BGPAnalyser {
	private Properties properties;
	private String namesNamespace;
	private OpBGP opBGP;
	private Translation translation;

	Map<Node, Interpretation> interpretations;
	public BGPAnalyser(Properties properties, OpBGP opBGP){
		this.properties = properties;
		this.opBGP = opBGP;
		this.translation = new Translation(properties);
		this.interpretations = new HashMap<Node,Interpretation>();
	}

	private void interpret(){

		List<Triple> tripleList = opBGP.getPattern().getList();

		// A) Gather Interpretations from the BGP, iterate until no new Interpreations are retrieved
		boolean lookForInterpretations = true;
		while(lookForInterpretations) {
			lookForInterpretations = false;
			for (Triple triple : tripleList) {

				Node subject = triple.getSubject();
				Node predicate = triple.getPredicate();
				Node object = triple.getPredicate();

				// If node was observed before, retrieve previous Interpretations
				if(interpretations.containsKey(subject)){
					Interpretation sIn = interpretations.get(subject);

				}
				if (subject.isConcrete()) {
					// Define Interpretations watching the Triple
					Set<Interpretation> currentInterpretations;

					// Check if node was observed before

					// If previous Interpretation exist, check consistency:
					// 1) they are the same -> OK, no changes
					// 2) either or specialises the other (e.g. Subject > Container > ContainerTable), keep the more specialised and remove the more general
					// 3) they are inconsistent -> raise a NoInterpretationException
					// 4) remember if Interpretations were changed on this BGP iteration
					//
					// Test if Table
//				if(translation.nodeContainerIsTable(subject)){
//					QueryComponent.table(translation.nodeContainerToTable(subject));
//				} else if(translation.nodeContainerIsRowNum(subject)){
//					QueryComponent.table(translation.nodeContainerToTable(subject));
//					assume(subject, Assumption.ContainerRow);
//				}
				} else if (subject.isVariable()) {
					// Subjects can only be Containers
				}

				// Predicate

				// Object

				// Joins?
			}
		}
	}
}
