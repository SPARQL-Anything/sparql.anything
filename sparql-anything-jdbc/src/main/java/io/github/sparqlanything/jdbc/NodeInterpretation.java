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
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NodeInterpretation {
	private Set<Triple> triples;
	private Node node;

	protected NodeInterpretation(Node n, Triple... t){
		this.node = n;
		this.triples = new HashSet<Triple>();
		if(t.length == 0){
			throw new RuntimeException("Assumption must be initialised with a triple");
		}
		for(Triple x:t){
			this.triples.add(x);
		}
	}

	public Set<Class<? extends NodeInterpretation>> inconsistentTypes(){
		return Collections.emptySet();
	}
	public Set<Class<? extends NodeInterpretation>> specialisationOfTypes(){
		return Collections.emptySet();
	}
	public Class<? extends NodeInterpretation> type(){
		return getClass();
	}

	public Set<Triple> triples(){
		return Collections.unmodifiableSet(triples);
	}

	public Node node(){
		return node;
	}

	static class Subject extends NodeInterpretation {
		protected Subject(Node n, Triple... t) {
			super(n, t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}

	static class Predicate extends NodeInterpretation {
		protected Predicate(Node n, Triple... t) {
			super(n, t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(Subject.class, Object.class);
		}
	}
	static class Object extends NodeInterpretation {
		protected Object(Node n, Triple... t) {
			super(n, t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}

	static class TypeProperty extends NodeInterpretation {
		protected TypeProperty(Triple... t) {
			super(RDF.type.asNode(), t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(Subject.class, Object.class, SlotValue.class, SlotColumn.class, SlotRow.class, TypeTable.class, ContainerRow.class, ContainerTable.class);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}

	static class TypeTable extends NodeInterpretation {
		protected TypeTable(Node n, Triple... t) {
			super(n, t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotValue.class, SlotColumn.class, SlotRow.class, ContainerRow.class, ContainerTable.class);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Object.class);
		}
	}

	static class ContainerRow extends NodeInterpretation {
		protected ContainerRow(Node n, Triple... t) {
			super(n, t);
		}
		protected ContainerRow(Node n, Set<Triple> t) {
			super(n, t.toArray(new Triple[t.size()]));
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(Predicate.class, SlotRow.class, SlotColumn.class, SlotValue.class, ContainerTable.class );
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Subject.class, Object.class);
		}
	}

	static class ContainerTable extends NodeInterpretation {
		protected ContainerTable(Node n, Triple... t) {
			super(n, t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(ContainerRow.class, Predicate.class, Object.class, Predicate.class, SlotRow.class, SlotColumn.class, SlotValue.class);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Subject.class);
		}
	}

	static class SlotRow extends NodeInterpretation {
		protected SlotRow(Node n, Triple... t) {
			super(n, t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotValue.class, ContainerRow.class, TypeProperty.class, Subject.class, Object.class, ContainerTable.class, SlotColumn.class);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Predicate.class);
		}

	}

	static class SlotColumn extends NodeInterpretation {
		protected SlotColumn(Node n, Triple... t) {
			super(n, t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotRow.class,SlotValue.class, ContainerRow.class, TypeProperty.class, Subject.class, Object.class, ContainerTable.class );
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class SlotValue extends NodeInterpretation {
		protected SlotValue(Node n, Triple... t) {
			super(n, t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotRow.class,SlotColumn.class, ContainerRow.class, TypeProperty.class, Subject.class, ContainerTable.class );
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Object.class);
		}
	}

	static class FXRoot extends NodeInterpretation {
		protected FXRoot(Triple... t) {
			super(NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT), t);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotValue.class, SlotRow.class,SlotColumn.class, ContainerRow.class, TypeProperty.class, Predicate.class, Subject.class, ContainerTable.class );
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Object.class);
		}
	}

	static class Join extends NodeInterpretation {

		Set<? extends NodeInterpretation> joined;
		Class<? extends NodeInterpretation> type;
		protected Join(Node node, Class<? extends NodeInterpretation> type, Triple[] triples, NodeInterpretation a1, NodeInterpretation a2) {
			super(node, triples);
			joined = new HashSet<>(List.of(a1, a2));
			this.type = type;
		}

		public NodeInterpretation[] assumptions(){
			return joined.toArray(new NodeInterpretation[joined.size()]);
		}

		public Class<? extends NodeInterpretation> type(){
			return type;
		}
		@Override
		public Set<Class<? extends NodeInterpretation>> inconsistentTypes() {
			Set<Class<? extends NodeInterpretation>> types = new HashSet<>();
			for(NodeInterpretation j : joined)
				types.addAll(j.inconsistentTypes());
			return ImmutableSet.copyOf(types);
		}

		@Override
		public Set<Class<? extends NodeInterpretation>> specialisationOfTypes() {
			Set<Class<? extends NodeInterpretation>> s = new HashSet<>();
			for(NodeInterpretation a:joined)
				s.addAll(a.specialisationOfTypes());
			return Collections.unmodifiableSet(s);
		}

		public boolean includes(NodeInterpretation i){
			if(joined.contains(i)){
				return true;
			}
			for(NodeInterpretation j:joined){
				if(j instanceof Join){
					return ((Join)j).includes(i);
				}
			}
			return false;
		}
	}

	public static Join makeJoin(NodeInterpretation assumption1, NodeInterpretation assumption2) throws InconsistentJoinException {

		// Nodes must be the same
		Node node = assumption1.node();
		if(!assumption2.node().equals(node)){
			throw new InconsistentJoinException(assumption1, assumption2);
		}
		Class<? extends NodeInterpretation> cls = null;
		// Check type and node and collect triples
		if(assumption1.type().equals(assumption2.type())){
			cls = assumption1.type();
		}else {
			// Override type
			// Set the appropriate type
			Map<Set<?>,Class<? extends NodeInterpretation>> m = new HashMap<>();
			// Subject , Object = ContainerRow
			// ContainerRow , Object = ContainerRow
			// ContainerRow , Subject = ContainerRow
			// Predicate , SlotRow = SlotRow
			// Predicate , SlotColumn = SlotColumn
			// Predicate , TypeProperty = TypeProperty
			// Subject , ContainerTable = ContainerTable
			// Object , TypeTable = TypeTable
			// Object , SlotValue = SlotValue
			// Object , FXRoot = FXRoot
			m.put(Set.of(Subject.class,Object.class), ContainerRow.class);
			m.put(Set.of(ContainerRow.class,Object.class), ContainerRow.class);
			m.put(Set.of(ContainerRow.class,Subject.class), ContainerRow.class);
			m.put(Set.of(Predicate.class,SlotRow.class), SlotRow.class);
			m.put(Set.of(Predicate.class,SlotColumn.class), SlotColumn.class);
			m.put(Set.of(Predicate.class,TypeProperty.class), TypeProperty.class);
			m.put(Set.of(Subject.class,ContainerTable.class), ContainerTable.class);
			m.put(Set.of(Object.class,TypeTable.class), TypeTable.class);
			m.put(Set.of(Object.class,SlotValue.class), SlotValue.class);
			m.put(Set.of(Object.class,FXRoot.class), FXRoot.class);
			Set<?> key = Set.of(assumption1.type(),assumption2.type());
			if(m.containsKey(key)){
				cls = m.get(key);
			}
		}
		if(cls == null){
			// last check, cannot mix types
			throw new InconsistentJoinException(assumption1, assumption2);
		}
		List<Triple> triples = new ArrayList<>();
		triples.addAll(assumption1.triples());
		triples.addAll(assumption2.triples());
		return new Join(node, cls, triples.toArray(new Triple[triples.size()]), assumption1, assumption2);
	}
	@Override
	public boolean equals(java.lang.Object obj) {
		if(this.getClass().equals(obj.getClass())){
			NodeInterpretation ass = (NodeInterpretation) obj;
			if(ass.triples().equals(this.triples()) && ass.node().equals(this.node())){
				return true;
			}
		}
		return false;
	}

	private int hashCode = -1;
	@Override
	public int hashCode() {
		if(hashCode == -1){
			this.hashCode = new HashCodeBuilder().append(getClass()).append(node).append(triples).hashCode();
		}
		return hashCode;
	}
	public NodeInterpretation[] assumptions(){
		return new NodeInterpretation[]{this};
	}
}
