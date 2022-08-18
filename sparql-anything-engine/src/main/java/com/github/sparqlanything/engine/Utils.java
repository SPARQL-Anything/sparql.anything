/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.engine;

import com.github.sparqlanything.facadeiri.FacadeIRIParser;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.TxnType;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.table.TableUnit;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIterDefaulting;
import org.apache.jena.sparql.engine.iterator.QueryIterRepeatApply;
import org.apache.jena.sparql.engine.iterator.QueryIterSingleton;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.pfunction.PropFuncArg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	static boolean isPropertyOp(OpGraph opTripleNext) {
		return opTripleNext.getNode().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES);
	}

	static boolean isPropertyOp(Node node) {
		return node.isURI() && node.getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES);
	}

	static String queryIteratorToString(QueryIterator q) {
		StringBuilder sb = new StringBuilder();
		while (q.hasNext()) {
			Binding binding = q.next();
			sb.append(bindingToString(binding));
		}
		return sb.toString();
	}

	static String bindingToString(Binding binding) {
		StringBuilder sb = new StringBuilder();
		Iterator<Var> vars = binding.vars();
		while (vars.hasNext()) {
			Var var = vars.next();
			sb.append(String.format("%s -> %s\n", var.getName(), binding.get(var)));
		}
		return sb.toString();
	}

	static boolean isFacadeXMagicPropertyNode(Node node) {
		return node.isURI() && node.getURI().equals(FacadeX.ANY_SLOT_URI);
	}

	static QueryIterator postpone(final Op op, QueryIterator input, ExecutionContext execCxt) {
//		logger.trace("is variable: {}", opService.getService());
		// Postpone to next iteration
		return new QueryIterRepeatApply(input, execCxt) {
			@Override
			protected QueryIterator nextStage(Binding binding) {
				Op op2 = QC.substitute(op, binding);
				QueryIterator thisStep = QueryIterSingleton.create(binding, this.getExecContext());
				QueryIterator cIter = QC.execute(op2, thisStep, super.getExecContext());
				cIter = new QueryIterDefaulting(cIter, binding, this.getExecContext());
				return cIter;
			}
		};
	}

	static List<Triple> getFacadeXMagicPropertyTriples(BasicPattern e) {
	   List<Triple> result = new ArrayList<>();
	   e.forEach(t -> {
		   if (isFacadeXMagicPropertyNode(t.getPredicate())) {
			   result.add(t);
		   }
	   });
	   return result;
   }

	static OpBGP extractFakePattern(OpBGP bgp) {
		BasicPattern pattern = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES)) {
				if (t.getObject().isVariable()) {
					Var s = Var.alloc("s" + System.currentTimeMillis());
					Var p = Var.alloc("p" + System.currentTimeMillis());
					pattern.add(new Triple(s, p, t.getObject()));
				}
			}
		}
		return new OpBGP(pattern);
	}

	static OpBGP excludeMagicPropertyTriples(OpBGP bgp) {
		BasicPattern result = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (isFacadeXMagicPropertyNode(t.getPredicate())) continue;
			result.add(t);
		}
		return new OpBGP(result);
	}

	static boolean isFacadeXURI(String uri) {
		return uri.startsWith(FacadeIRIParser.SPARQL_ANYTHING_URI_SCHEMA);
	}

	static OpPropFunc getOpPropFuncAnySlot(Triple t) {
		return new OpPropFunc(NodeFactory.createURI(FacadeX.ANY_SLOT_URI), new PropFuncArg(t.getSubject()), new PropFuncArg(t.getObject()), OpTable.create(new TableUnit()));
	}

	static OpBGP excludeFXProperties(OpBGP bgp) {
		BasicPattern result = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES)) continue;
			result.add(t);
		}
		return new OpBGP(result);
	}

	static void ensureReadingTxn(DatasetGraph dg) {
		// i think we can consider this the start of the query execution and therefore the read txn.
		// we won't end this read txn until the next query takes execution back through BaseFacadeXBuilder
		if (!dg.isInTransaction()) {
			// i think we need the test (instead of just unconditionally starting the txn) because if we postpone
			// during a query execution, execution could pass through here again
			logger.debug("begin read txn");
			dg.begin(TxnType.READ);
		}
	}
}
