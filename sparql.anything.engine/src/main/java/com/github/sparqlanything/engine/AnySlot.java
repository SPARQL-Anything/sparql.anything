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
import java.util.NoSuchElementException;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingBuilder;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import org.apache.jena.sparql.pfunction.PFuncSimple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnySlot extends PFuncSimple {
	private static final Logger logger = LoggerFactory.getLogger(AnySlot.class);

	@Override
	public QueryIterator execEvaluated(Binding parent, Node subject, Node predicate, Node object,
			ExecutionContext execCxt) {
		Node s, o;

		if (subject.isURI()) {
			s = subject;
		} else {
			s = Node.ANY;
		}

		if (object.isURI() || object.isLiteral()) {
			o = object;
		} else {
			o = Node.ANY;
		}

		ExtendedIterator<Triple> it = execCxt.getActiveGraph().find(s, Node.ANY, o);
		logger.trace("S {} {} P {} O {} {} BP {} : {}", subject.toString(), s.toString(), predicate.toString(),
				object.toString(), o.toString(), Utils.bindingToString(parent), it.hasNext());

		QueryIterator res = QueryIterPlainWrapper.create(new Iterator<Binding>() {

			private Triple cached;
			private boolean hasCached;

			@Override
			public Binding next() {
				if (hasCached) {
					hasCached = false;
					return bindTriple(cached);
				}

				cached = fillNext();

				if (cached != null) {
					return bindTriple(cached);
				}

				throw new NoSuchElementException();
			}

			@Override
			public boolean hasNext() {
				if (hasCached)
					return true;
				cached = fillNext();
				if (cached != null) {
					hasCached = true;
					return true;
				}
				return false;
			}

			private Triple fillNext() {
				while (it.hasNext()) {
					Triple t = it.next();
					if (t.getPredicate().getURI().startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#_")) {
						return t;
					}
				}

				return null;
			}

			private Binding bindTriple(Triple t) {
				BindingBuilder bb = Binding.builder(parent);
//				BindingMap bm = BindingFactory.create(parent);
				if (subject.isVariable()) {
					if (parent.contains((Var) subject)) {
//						bm.add((Var) subject, parent.get((Var) subject));
						bb.add((Var) subject, parent.get((Var) subject));
					} else {
//						bm.add((Var) subject, t.getSubject());
						bb.add((Var) subject, t.getSubject());
					}
				}
				if (object.isVariable()) {
					if (parent.contains((Var) object)) {
//						bm.add((Var) object, parent.get((Var) object));
						bb.add((Var) subject, t.getSubject());
					} else {
//						bm.add((Var) object, t.getObject());
						bb.add((Var) object, t.getObject());
					}
				}
				return bb.build();
			}
		});

		return res;
	}

}
