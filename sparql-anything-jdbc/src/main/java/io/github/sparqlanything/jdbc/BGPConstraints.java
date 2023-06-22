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
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BGPConstraints {
	final static Logger L = LoggerFactory.getLogger(BGPConstraints.class);
	private Map<Node, NodeInterpretation> initialConstraints = null;
	private OpBGP opBGP = null;
	private Translation translation;

	private Map<Node, NodeInterpretation> constraints = null;

	private InconsistentAssumptionException exception = null;

	public BGPConstraints(Translation translation, OpBGP bgp){
		this.opBGP = bgp;
		this.translation = translation;
		this.initialConstraints = new HashMap<Node, NodeInterpretation>();
		setupConstraints();
	}

	public BGPConstraints(Translation translation, OpBGP bgp, Map<Node,NodeInterpretation> possibleInterpretation){
		this.opBGP = bgp;
		this.translation = translation;
		this.initialConstraints = possibleInterpretation;
		setupConstraints();
	}

	public Map<Node, NodeInterpretation> interpretations(){
		if(this.constraints == null){
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(this.constraints);
	}

	public boolean isException(){
		return this.exception != null;
	}

	private void setupConstraints()  {
		this.constraints = initialConstraints;
		List<Triple> tripleList = opBGP.getPattern().getList();
		try {
			// A) Gather Interpretations from the BGP, iterate until no new Interpreations are retrieved
			boolean lookForConstraints = true;
			while (lookForConstraints) {
				lookForConstraints = false;
				for (Triple triple : tripleList) {
					L.trace("interpreting triple: {}", triple);
					Node subject = triple.getSubject();
					Node predicate = triple.getPredicate();
					Node object = triple.getObject();

					// If node was observed before, retrieve previous Interpretations
					NodeInterpretation subjectNodeInterpretation = constrainSubject(subject, triple);
					lookForConstraints = updateConstraints(subject, subjectNodeInterpretation);
					L.trace("look for constraints after s: {} {}->{}", lookForConstraints,subject, subjectNodeInterpretation);
					NodeInterpretation predicateNodeInterpretation = constrainPredicate(predicate, triple);
					lookForConstraints = updateConstraints(predicate, predicateNodeInterpretation) || lookForConstraints;
					L.trace("look for constraints after p: {} {}->{}", lookForConstraints,predicate, predicateNodeInterpretation);
					NodeInterpretation objectNodeInterpretation = constrainObject(object, triple);
					lookForConstraints = updateConstraints(object, objectNodeInterpretation) || lookForConstraints;
					L.trace("look for constraints after o: {} {}->{}", lookForConstraints, object, objectNodeInterpretation);
					//System.out.println();
				}
			}
		}catch(InconsistentAssumptionException e){
			this.exception = e;
			L.trace("No solution for BGP: {}", opBGP);
			if(L.isDebugEnabled()){
				L.error("Debug enabled. Logging InconsistentAssumptionException.");
				L.error("No solution for BGP (reason):", e);
			}
		}
	}

	private boolean hasConstraint(Node n, Class<? extends NodeInterpretation> as){
		if(!constraints.containsKey(n)){
			return false;
		}
		return constraints.get(n).type().equals(as);
	}

	private NodeInterpretation constrainSubject(Node subject, Triple triple) throws InconsistentAssumptionException {

		// ContainerTable(S) <- URI(S) | FXRoot(O) | SlotRow(P) | ContainerRow(O)
		if(subject.isURI() || hasConstraint(triple.getObject(), NodeInterpretation.FXRoot.class) ||
				hasConstraint(triple.getPredicate(), NodeInterpretation.SlotRow.class) ||
				hasConstraint(triple.getObject(), NodeInterpretation.ContainerRow.class)){
			return new NodeInterpretation.ContainerTable(subject, triple);
		}

		// ContainerRow(S) <- SlotColumn(P) | SlotValue(O) | TypeTable(O)
		if(hasConstraint(triple.getPredicate(), NodeInterpretation.SlotColumn.class) ||
				hasConstraint(triple.getObject(), NodeInterpretation.TypeTable.class) ||
				hasConstraint(triple.getObject(), NodeInterpretation.SlotValue.class) ){
			return new NodeInterpretation.ContainerRow(subject, triple);
		}

		// if subject is named entity
		if(subject.isURI()){
			// ContainerTable(S) <- URI(S)
			if(translation.nodeContainerIsTable(subject)){
				return new NodeInterpretation.ContainerTable(subject, triple);
			}else{
				// Unknown entity URI
				throw new InconsistentEntityException(subject, "URI does not match table name pattern");
			}
		} else {
			// if subject is a variable or a blank node
			return new NodeInterpretation.Subject(subject,triple);
		}
	}

	private NodeInterpretation constrainObject(Node object, Triple triple) throws InconsistentEntityException {
		// SlotValue(O) <- SlotColumn(P)
		if(hasConstraint(triple.getPredicate(), NodeInterpretation.SlotColumn.class) ){
			return new NodeInterpretation.SlotValue(object, triple);
		}

		// ContainerRow(O) <- SlotRow(P)
		if(hasConstraint(triple.getPredicate(), NodeInterpretation.SlotRow.class) ){
			return new NodeInterpretation.ContainerRow(object, triple);
		}

		// if object is named entity then it can only be a table type
		if(object.isURI()){
			// TypeTable(O) <- URI(O)
			if(object.getURI().equals(Triplifier.FACADE_X_TYPE_ROOT)){
				return new NodeInterpretation.FXRoot(triple);
			} else if(translation.nodeTypeIsTable(object)){
				return new NodeInterpretation.TypeTable(object, triple);
			} else {
				// Unknown entity URI
				throw new InconsistentEntityException(object);
			}
		} else if(object.isBlank() || object.isVariable()){
			return new NodeInterpretation.Object(object, triple);
		} else if(object.isLiteral()){
			// SlotValue(O) <- TL(O)
			return new NodeInterpretation.SlotValue(object, triple);
		}
		throw new InconsistentEntityException(object, "Object cannot be of this type");
	}

	private NodeInterpretation constrainPredicate(Node predicate, Triple triple) throws InconsistentAssumptionException {
		// SlotColumn(P) <- SlotValue(O)
		if(hasConstraint(triple.getObject(), NodeInterpretation.SlotValue.class) ){
			return new NodeInterpretation.SlotColumn(predicate, triple);
		}
		// TypeProperty(P) <- TypeTable(O)
		if(hasConstraint(triple.getObject(), NodeInterpretation.TypeTable.class) ||
				hasConstraint(triple.getObject(), NodeInterpretation.FXRoot.class)){
			return new NodeInterpretation.TypeProperty(triple);
		}
		// SlotRow(P) <- ContainerRow(O)
		if(hasConstraint(triple.getObject(), NodeInterpretation.ContainerRow.class) ){
			return new NodeInterpretation.SlotRow(predicate, triple);
		}

		if(!predicate.isVariable()){
			if(translation.nodeSlotIsRowNum(predicate)){
				// SlotRow(P) <- CMP(P)
				return new NodeInterpretation.SlotRow(predicate, triple);
			}else if(translation.nodeSlotIsColumn(predicate)){
				// SlotColumn(P) <- URI(fx:*)
				return new NodeInterpretation.SlotColumn(predicate, triple);
			}else if(translation.nodeSlotIsTypeProperty(predicate)){
				// TypeProperty(P) <- URI(rdf:type)
				return new NodeInterpretation.TypeProperty(triple);
			}else{
				// Unknown entity URI
				throw new InconsistentEntityException(predicate);
			}
		}
		return new NodeInterpretation.Predicate(predicate, triple);
	}

	/**
	 * Returns true if there can be more specific interpretations
	 * Returns false otherwise
	 * Throws an exception if the proposed interpretation breaks some assumption (is invalid)_
	 *
	 * @param node
	 * @param constraint
	 * @return
	 * @throws InconsistentAssumptionException
	 */
	private boolean updateConstraints(Node node, NodeInterpretation constraint) throws InconsistentAssumptionException {
		NodeInterpretation previous = null;
		// Check if node was observed before
		if(constraints.containsKey(node)){
			previous = constraints.get(node);
			// Nothing new
			if(previous.type().equals(constraint.type())){
				return false;
			}
			Class<? extends NodeInterpretation> wasType = previous.type();
			Class<? extends NodeInterpretation> isType = constraint.type();
			// If previous Interpretation exist, check consistency:
			// If new interpretation is inconsistent with old one, throw an exception
			if(previous.inconsistentTypes().contains(constraint.type()) ||
					constraint.inconsistentTypes().contains(previous.type())){
//				//
//				Set<?> s1 = previous.inconsistentTypes();
//				Set<?> s2 = constraint.inconsistentTypes();
//				boolean x = previous.inconsistentTypes().contains(constraint.type());
//				boolean y = constraint.inconsistentTypes().contains(previous.type());
				throw new InconsistentTypesException(wasType, isType);
			}
			// Check the types
			if(isType.equals(wasType)){
				// 1) they are the same ->
				// the triples must be different, then it is a Join
				NodeInterpretation.Join joined = NodeInterpretation.makeJoin(previous, constraint);
				constraints.put(node, joined);
				return true;
			} else {
				// Types are not the same, check if they are specialisations of one another
				// 2) either or specialises the other (e.g. Subject > Container > ContainerTable), keep the more specialised and remove the more general
				if(previous.specialisationOfTypes().contains(isType)){
					// keep old type
					return false;
				} else if(constraint.specialisationOfTypes().contains(wasType)){
					// keep new type
					constraints.put(node, constraint);
					return true;
				} else {
					// OK -- they are not specialisations of one another
					// Are they joinable?
					// Subject, Object are joinable as ContainerRow
					if(previous.node().isVariable() || previous.node().isBlank()){
						// If any of the two assumptions are Joins, avoid re-joining...
						if(previous instanceof NodeInterpretation.Join && ((NodeInterpretation.Join)previous).includes(constraint)){
							return false;
						}
						NodeInterpretation.Join joined = NodeInterpretation.makeJoin(previous, constraint);
						constraints.put(node, joined);
						return true;

					}
					// XXX Is our model incomplete?
					throw new InconsistentTypesException(wasType, isType);
				}
			}
		} else {
			constraints.put(node, constraint);
			return true;
		}
	}

}
