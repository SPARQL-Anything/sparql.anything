/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.model.test;

import org.junit.Test;

public class StreamWithMemorySandbox {

	@Test
	public void test(){
		// One thread reads and loads into the container node wrapper
		// The other thread reads from the container node wrapper, and waits if needed until the PO set is complete

	}


//	/**
//	 * Rewriting Node_Blank methods as the Jena class cannot be subclassed!
//	 */
//	class ContainerBNodeWrapper extends Node_Blank implements ContainerNodeWrapper {
//		private final ConcurrentHashMap<Node, List<Node>> data;
//		protected int contextHash;
//		protected ContainerBNodeWrapper(int contextHash, String id) {
//			super(new BlankNodeId(id));
//			this.contextHash = contextHash;
//			this.data = new ConcurrentHashMap<Node, List<Node>>();
//		}
//
//		public boolean isBlank() {
//			return true;
//		}
//
//		public BlankNodeId getBlankNodeId() {
//			return (BlankNodeId)this.label;
//		}
//
//		public Object visitWith(NodeVisitor v) {
//			return v.visitBlank(this, (BlankNodeId)this.label);
//		}
//
//		public boolean equals(Object other) {
//			if (this == other) {
//				return true;
//			} else {
//				return other instanceof ContainerBNodeWrapper && contextHash() == ((ContainerBNodeWrapper) other).contextHash() && this.label.equals(((ContainerBNodeWrapper)other).getBlankNodeId().getLabelString());
//			}
//		}
//
//		@Override
//		public Iterator<Quad> find(Node property, Node value) {
//			return null;
//		}
//
//
//		@Override
//		public void put(Node property, Node value) {
//			if(!this.data.containsKey(property)){
//				this.data.put(property, new ArrayList<Node>());
//			}
//			this.data.get(property).add(value);
//		}
//
//		@Override
//		public int contextHash() {
//			return contextHash;
//		}
//
//		@Override
//		public Map<Node, List<Node>> data() {
//			return data;
//		}
//	}
}
