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

package com.github.sparqlanything.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.OpAssign;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpConditional;
import org.apache.jena.sparql.algebra.op.OpDatasetNames;
import org.apache.jena.sparql.algebra.op.OpDiff;
import org.apache.jena.sparql.algebra.op.OpDisjunction;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLabel;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpList;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpNull;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProcedure;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpQuad;
import org.apache.jena.sparql.algebra.op.OpQuadBlock;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTopN;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.path.P_Alt;
import org.apache.jena.sparql.path.P_Distinct;
import org.apache.jena.sparql.path.P_FixedLength;
import org.apache.jena.sparql.path.P_Inverse;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.P_Mod;
import org.apache.jena.sparql.path.P_Multi;
import org.apache.jena.sparql.path.P_NegPropSet;
import org.apache.jena.sparql.path.P_OneOrMore1;
import org.apache.jena.sparql.path.P_OneOrMoreN;
import org.apache.jena.sparql.path.P_ReverseLink;
import org.apache.jena.sparql.path.P_Seq;
import org.apache.jena.sparql.path.P_Shortest;
import org.apache.jena.sparql.path.P_ZeroOrMore1;
import org.apache.jena.sparql.path.P_ZeroOrMoreN;
import org.apache.jena.sparql.path.P_ZeroOrOne;
import org.apache.jena.sparql.path.PathVisitor;

/**
 *
 */
public class TripleFilteringFacadeXBuilder extends BaseFacadeXBuilder {
	private final Op op;
	private final List<Object> opComponents = new ArrayList<Object>();

	public TripleFilteringFacadeXBuilder(String resourceId, Op op, DatasetGraph ds, Properties properties) {
		super(resourceId, ds, properties);
		this.op = op;
		if (op != null) {
			ComponentsCollector collector = new ComponentsCollector();
			op.visit(collector);
		}
		//
	}

	public TripleFilteringFacadeXBuilder(String resourceId, Op op, Properties properties) {
		this(resourceId, op, DatasetGraphFactory.create(), properties);
//        this(resourceId, op, DatasetGraphFactory.createTxnMem(), properties);
	}
//
//	public TripleFilteringFacadeXBuilder(URL location, Op op, Properties properties) {
//		this(location.toString(), op, properties);
//	}

	public boolean match(Node graph, Node subject, Node predicate, Node object) {
		if (op == null || opComponents.isEmpty())
			return true;

		for (Object o : opComponents) {

			if (o instanceof Quad) {
				Quad q = (Quad) o;
				if ((!q.getGraph().isConcrete() || q.getGraph().matches(graph))
						&& (!q.getSubject().isConcrete() || q.getSubject().matches(subject))
						&& predicateMatch(q.getPredicate(), predicate) // (!q.getPredicate().isConcrete() ||
																		// q.getPredicate().matches(predicate))
						&& (!q.getObject().isConcrete() || q.getObject().matches(object))) {
					return true;
				}
			} else if (o instanceof Triple) {
				Triple t = (Triple) o;
				if ((!t.getSubject().isConcrete() || t.getSubject().matches(subject))
						&& predicateMatch(t.getPredicate(), predicate) // (!t.getPredicate().isConcrete() ||
																		// t.getPredicate().matches(predicate))
						&& (!t.getObject().isConcrete() || t.getObject().matches(object))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean predicateMatch(Node queryPredicate, Node dataPredicate) {
		// If queryPredicate is fx:anySLot match any container membership property
		if (queryPredicate.isConcrete()
				&& queryPredicate.getURI().equals(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot")) {
			if (dataPredicate.getURI().startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#_")) {
				return true;
			} else {
				return false;
			}
		}
		return (!queryPredicate.isConcrete() || queryPredicate.matches(dataPredicate));
	}
//
//	@Override
//	@Deprecated
//	public void add(Resource subject, Property predicate, RDFNode object) {
//		if (match(mainGraphName, subject.asNode(), predicate.asNode(), object.asNode())) {
//			datasetGraph.getGraph(mainGraphName).add(new Triple(subject.asNode(), predicate.asNode(), object.asNode()));
//		}
//	}

	/**
	 * Triples are added to the main data source / graph Triplifiers generating
	 * multiple data sources / graphs, should use add(Node g, Node s, Node p, Node
	 * o) instead
	 */
//	@Override
//	public boolean add(Node subject, Node predicate, Node object) {
//		if (match(mainGraphName, subject, predicate, object)) {
//			datasetGraph.getGraph(mainGraphName).add(new Triple(subject, predicate, object));
//			return true;
//		}
//		return false;
//	}

	@Override
	public boolean add(Node graph, Node subject, Node predicate, Node object) {
		boolean startedTransactionHere = false ;
		if(datasetGraph.supportsTransactions() && datasetGraph.isInTransaction()){
			log.debug("commiting and ending current txn");
			datasetGraph.commit();
			datasetGraph.end();
			log.debug("begin new txn");
			startedTransactionHere = true ;
			datasetGraph.begin();
		}
		// log.debug("meh1: " + datasetGraph.getGraph(graph));
		// log.debug("meh2: " + datasetGraph.getGraph(graph).class());
		if (match(graph, subject, predicate, object)) {
			datasetGraph.getGraph(graph).add(new Triple(subject, predicate, object));
			return true;
		}
		// if(datasetGraph.supportsTransactions() && startedTransactionHere){
		// 	log.debug("end txn");
		// 	datasetGraph.end();
		// }
		return false;
	}

	class ComponentsCollector implements OpVisitor {
		@Override
		public void visit(OpBGP opBGP) {
			log.trace(" - OpBGP - ", opBGP);
			opComponents.addAll(opBGP.getPattern().getList());
		}

		@Override
		public void visit(OpQuadPattern opQuadPattern) {
			log.trace(" - OpQuadPattern - ", opQuadPattern);
			opComponents.addAll(opQuadPattern.getPattern().getList());
		}

		@Override
		public void visit(OpQuadBlock opQuadBlock) {
			log.trace(" - OpQuadBlock - ", opQuadBlock);
			opComponents.addAll(opQuadBlock.getPattern().getList());
		}

		@Override
		public void visit(OpTriple opTriple) {
			log.trace(" - OpBGP - ", opTriple);
			opComponents.add(opTriple.getTriple());
		}

		@Override
		public void visit(OpQuad opQuad) {
			log.trace(" - OpQuad - ", opQuad);
			opComponents.add(opQuad.getQuad());
		}

		@Override
		public void visit(OpPath opPath) {
			log.trace(" - OpPath - {}", opPath.toString());
			opPath.getTriplePath().getPath().visit(new PathVisitor() {

				@Override
				public void visit(P_Seq pathSeq) {
					pathSeq.getLeft().visit(this);
					pathSeq.getRight().visit(this);
				}

				@Override
				public void visit(P_Alt pathAlt) {
					pathAlt.getLeft().visit(this);
					pathAlt.getRight().visit(this);
				}

				@Override
				public void visit(P_OneOrMoreN path) {
					path.getSubPath().visit(this);
				}

				@Override
				public void visit(P_OneOrMore1 path) {
					path.getSubPath().visit(this);

				}

				@Override
				public void visit(P_ZeroOrMoreN path) {
					path.getSubPath().visit(this);
				}

				@Override
				public void visit(P_ZeroOrMore1 path) {
					path.getSubPath().visit(this);
				}

				@Override
				public void visit(P_ZeroOrOne path) {
					path.getSubPath().visit(this);
				}

				@Override
				public void visit(P_Shortest pathShortest) {
					pathShortest.getSubPath().visit(this);
				}

				@Override
				public void visit(P_Multi pathMulti) {
					pathMulti.getSubPath().visit(this);
				}

				@Override
				public void visit(P_Distinct pathDistinct) {
					pathDistinct.getSubPath().visit(this);
				}

				@Override
				public void visit(P_FixedLength pFixedLength) {
					pFixedLength.getSubPath().visit(this);
				}

				@Override
				public void visit(P_Mod pathMod) {
					pathMod.getSubPath().visit(this);
				}

				@Override
				public void visit(P_Inverse inversePath) {
					inversePath.getSubPath().visit(this);
				}

				@Override
				public void visit(P_NegPropSet pathNotOneOf) {
					// TODO Auto-generated method stub
				}

				@Override
				public void visit(P_ReverseLink pathNode) {
					// TODO Auto-generated method stub

				}

				@Override
				public void visit(P_Link pathNode) {
					opComponents.add(new Triple(Node.ANY, pathNode.getNode(), Node.ANY));

				}
			});

		}

		@Override
		public void visit(OpTable opTable) {

		}

		@Override
		public void visit(OpNull opNull) {

		}

		@Override
		public void visit(OpProcedure opProcedure) {
			opProcedure.getSubOp().visit(this);
		}

		@Override
		public void visit(OpPropFunc opPropFunc) {
			opPropFunc.getSubOp().visit(this);
		}

		@Override
		public void visit(OpFilter opFilter) {
			opFilter.getSubOp().visit(this);
		}

		@Override
		public void visit(OpGraph opGraph) {
			log.trace(" - OpGraph - ", opGraph);
			opGraph.getSubOp().visit(this);
		}

		@Override
		public void visit(OpService opService) {
			opService.getSubOp().visit(this);
		}

		@Override
		public void visit(OpDatasetNames opDatasetNames) {

		}

		@Override
		public void visit(OpLabel opLabel) {
			opLabel.getSubOp().visit(this);
		}

		@Override
		public void visit(OpAssign opAssign) {
			opAssign.getSubOp().visit(this);
		}

		@Override
		public void visit(OpExtend opExtend) {
			opExtend.getSubOp().visit(this);
		}

		@Override
		public void visit(OpJoin opJoin) {
			opJoin.getLeft().visit(this);
			opJoin.getRight().visit(this);
		}

		@Override
		public void visit(OpLeftJoin opLeftJoin) {
			opLeftJoin.getLeft().visit(this);
			opLeftJoin.getRight().visit(this);
		}

		@Override
		public void visit(OpUnion opUnion) {
			opUnion.getLeft().visit(this);
			opUnion.getRight().visit(this);
		}

		@Override
		public void visit(OpDiff opDiff) {
			opDiff.getLeft().visit(this);
			opDiff.getRight().visit(this);
		}

		@Override
		public void visit(OpMinus opMinus) {
			opMinus.getLeft().visit(this);
			opMinus.getRight().visit(this);
		}

		@Override
		public void visit(OpConditional opConditional) {
			opConditional.getLeft().visit(this);
			opConditional.getRight().visit(this);
		}

		@Override
		public void visit(OpSequence opSequence) {
			for (Op o : opSequence.getElements()) {
				o.visit(this);
			}
		}

		@Override
		public void visit(OpDisjunction opDisjunction) {
			for (Op o : opDisjunction.getElements()) {
				o.visit(this);
			}
		}

		@Override
		public void visit(OpList opList) {
			opList.getSubOp().visit(this);
		}

		@Override
		public void visit(OpOrder opOrder) {
			opOrder.getSubOp().visit(this);
		}

		@Override
		public void visit(OpProject opProject) {
			opProject.getSubOp().visit(this);
		}

		@Override
		public void visit(OpReduced opReduced) {
			opReduced.getSubOp().visit(this);
		}

		@Override
		public void visit(OpDistinct opDistinct) {
			opDistinct.getSubOp().visit(this);
		}

		@Override
		public void visit(OpSlice opSlice) {
			opSlice.getSubOp().visit(this);
		}

		@Override
		public void visit(OpGroup opGroup) {
			opGroup.getSubOp().visit(this);
		}

		@Override
		public void visit(OpTopN opTopN) {
			opTopN.getSubOp().visit(this);
		}
	}
}
