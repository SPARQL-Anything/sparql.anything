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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BGPAnalyser {
	private Properties properties;
	private String namesNamespace;
	private OpBGP opBGP;
	private Translation translation;

	private Map<Node, Interpretation> interpretations = null;
	private InconsistentAssumptionException exception;

	public BGPAnalyser(Properties properties, OpBGP opBGP){
		this.properties = properties;
		this.opBGP = opBGP;
		this.translation = new Translation(properties);
		interpret();
	}

	public Map<Node, Interpretation> interpretations(){
		if(this.interpretations == null){
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(this.interpretations);
	}

	public boolean isException(){
		return this.exception != null;
	}

	private void interpret()  {
		this.interpretations = new HashMap<Node,Interpretation>();
		List<Triple> tripleList = opBGP.getPattern().getList();
		try {
			// A) Gather Interpretations from the BGP, iterate until no new Interpreations are retrieved
			boolean lookForInterpretations = true;
			while (lookForInterpretations) {
				lookForInterpretations = false;
				for (Triple triple : tripleList) {

					Node subject = triple.getSubject();
					Node predicate = triple.getPredicate();
					Node object = triple.getPredicate();

					// If node was observed before, retrieve previous Interpretations
					Interpretation subjectInterpretation = interpretSubject(subject, triple);
					lookForInterpretations = updateInterpretation(subject, subjectInterpretation);
					Interpretation predicateInterpretation = interpretPredicate(predicate, triple);
					lookForInterpretations = lookForInterpretations || updateInterpretation(predicate, predicateInterpretation);
					Interpretation objectInterpretation = interpretObject(object, triple);
					lookForInterpretations = lookForInterpretations || updateInterpretation(object, objectInterpretation);
				}
			}
		}catch(InconsistentAssumptionException e){
			this.exception = e;
		}
	}

	private boolean interpretedAs(Node n, Class<? extends Interpretation> as){
		return interpretations.get(n).getClass().equals(as);
	}

	private Interpretation interpretSubject(Node subject, Triple triple) throws InconsistentAssumptionException {

		// If predicate is a TypeProperty
		// or object is a TypeTable
		// then this is a ContainerTable
		// or predicate is a SlotRow
		// or object is a ContainerRow
		if(interpretedAs(triple.getPredicate(), Assumption.TypeProperty.class) ||
			interpretedAs(triple.getObject(), Assumption.ContainerTable.class) ||
			interpretedAs(triple.getPredicate(), Assumption.SlotRow.class) ||
			interpretedAs(triple.getObject(), Assumption.ContainerRow.class)){
			return new Assumption.ContainerTable();
		}

		// If predicate is a SlotColumn
		// or object is a SlotValue
		// then this is a ContainerRow
		if(interpretedAs(triple.getPredicate(), Assumption.SlotColumn.class) ||
				interpretedAs(triple.getObject(), Assumption.SlotValue.class) ){
			return new Assumption.ContainerRow();
		}

		// if subject is named entity
		if(subject.isURI()){
			if(translation.nodeContainerIsTable(subject)){
				return new Assumption.ContainerTable();
			}else if(translation.nodeContainerIsRowNum(subject)){
				return new Assumption.ContainerRow();
			}else{
				// Unknown entity URI
				throw new InconsistentEntityException(subject);
			}
		}else{
			// if subject is a variable or a blank node
			return new Assumption.Subject();
		}
	}

	private Interpretation interpretObject(Node object, Triple triple) throws InconsistentEntityException {
		// If predicate is a SlotColumn
		// or subject is a ContainerRow
		// then this is a SlotValue
		if(interpretedAs(triple.getPredicate(), Assumption.SlotColumn.class) ||
				interpretedAs(triple.getSubject(), Assumption.ContainerRow.class) ){
			return new Assumption.SlotValue();
		}

		// If predicate is a TypeProperty
		// then this is a TypeTable
		if(interpretedAs(triple.getPredicate(), Assumption.TypeProperty.class)){
			return new Assumption.TypeTable();
		}

		// If predicate is SlotRow
		// or subject is ContainerTable
		// then this is ContainerRow
		if(interpretedAs(triple.getPredicate(), Assumption.SlotRow.class) ||
				interpretedAs(triple.getSubject(), Assumption.ContainerTable.class) ){
			return new Assumption.ContainerRow();
		}

		// if subject is named entity
		if(object.isURI()){
			if(translation.nodeContainerIsRowNum(object)){
				return new Assumption.ContainerRow();
			}else if(translation.nodeTypeIsTable(object)){
				return new Assumption.TypeTable();
			}else{
				// Unknown entity URI
				throw new InconsistentEntityException(object);
			}
		}else{
			// if subject is a variable or a blank node
			return new Assumption.Object();
		}
	}

	private Interpretation interpretPredicate(Node predicate, Triple triple) throws InconsistentAssumptionException {
		// If subject is ContainerRow
		// or object is SlotValue
		// then this is SlotColumn
		if(interpretedAs(triple.getSubject(), Assumption.ContainerRow.class) ||
				interpretedAs(triple.getObject(), Assumption.SlotValue.class) ){
			return new Assumption.SlotColumn();
		}
		// If object is TableType
		// this is rdf:type
		if(interpretedAs(triple.getObject(), Assumption.TypeTable.class) ){
			return new Assumption.TypeProperty();
		}
		// If object is ContainerRow
		// then this is SlotRow
		if(interpretedAs(triple.getObject(), Assumption.ContainerRow.class) ){
			return new Assumption.SlotRow();
		}
		// if subject is named entity
		if(predicate.isURI()){
			if(translation.nodeSlotIsRowNum(predicate)){
				return new Assumption.SlotRow();
			}else if(translation.nodeSlotIsColumn(predicate)){
				return new Assumption.SlotColumn();
			}else if(translation.nodeSlotIsTypeProperty(predicate)){
				return new Assumption.TypeProperty();
			}else{
				// Unknown entity URI
				throw new InconsistentEntityException(predicate);
			}
		}else{
			// if subject is a variable or a blank node
			return new Assumption.Object();
		}
	}

	private boolean updateInterpretation(Node node, Interpretation interpretation) throws InconsistentAssumptionException {
		Interpretation previous = null;
		// Check if node was observed before
		if(interpretations.containsKey(node)){
			previous = interpretations.get(node);
			Class<? extends Interpretation> wasType = previous.getClass();
			Class<? extends Interpretation> isType = interpretation.getClass();
			// If previous Interpretation exist, check consistency:
			// If new interpretation is inconsistent with old one, throw an exception
			if(previous.inconsistentTypes().contains(isType) ||
				interpretation.inconsistentTypes().contains(wasType)){
				throw new InconsistentTypesException(wasType, isType);
			}
			// Types are consistent, now keep the most specific
			if(isType.equals(wasType)){
				// 1) they are the same -> OK, no changes
				return false;
			} else {
				// 2) either or specialises the other (e.g. Subject > Container > ContainerTable), keep the more specialised and remove the more general
				if(previous.specialisationOfTypes().contains(isType)){
					// keep old type
					return false;
				}else if(interpretation.specialisationOfTypes().contains(wasType)){
					// keep new type
					interpretations.put(node, interpretation);
					return true;
				}else{
					// Ops, we haven't specified things properly!
					throw new InconsistentTypesException(wasType, isType);
				}
			}
		} else {
			interpretations.put(node, interpretation);
			return true;
		}
	}
}
