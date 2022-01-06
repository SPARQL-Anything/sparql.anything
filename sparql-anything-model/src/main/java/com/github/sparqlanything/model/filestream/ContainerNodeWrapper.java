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
import org.apache.jena.sparql.core.Quad;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface ContainerNodeWrapper {
	Iterator<Quad> find( Node graph,  Node subject,  Node property,  Node value) ;
	int contextHash();
	void add(Quad quad);
	ContainerNodeWrapper getParent();
	void setParent(ContainerNodeWrapper parent, Node slot);
	Node getParentSlot();
	boolean isRoot();
	void setRoot(boolean isRoot);
	boolean isCompleted();
	void setCompleted();
}
