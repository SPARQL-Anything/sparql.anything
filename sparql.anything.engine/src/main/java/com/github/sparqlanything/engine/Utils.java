/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.sparqlanything.engine;

import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;

import com.github.sparqlanything.model.Triplifier;

public class Utils {

	static boolean isPropertyOp(OpGraph opTripleNext) {
		return opTripleNext.getNode().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES);
	}

	static boolean isPropertyOp(Node node) {
		return node.isURI() && node.getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES);
	}

	static String queryIteratorToString(QueryIterator q) {
		StringBuilder sb = new StringBuilder();
		while (q.hasNext()) {
			Binding binding = (Binding) q.next();
			sb.append(bindingToString(binding));
		}
		return sb.toString();
	}

	static String bindingToString(Binding binding) {
		StringBuilder sb = new StringBuilder();
		Iterator<Var> vars = binding.vars();
		while (vars.hasNext()) {
			Var var = (Var) vars.next();
			sb.append(String.format("%s -> %s\n", var.getName(), binding.get(var)));
		}
		return sb.toString();
	}

}
