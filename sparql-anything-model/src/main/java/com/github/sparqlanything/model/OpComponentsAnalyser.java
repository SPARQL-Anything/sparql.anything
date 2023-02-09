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

package com.github.sparqlanything.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprFunction0;
import org.apache.jena.sparql.expr.ExprFunction1;
import org.apache.jena.sparql.expr.ExprFunction2;
import org.apache.jena.sparql.expr.ExprFunction3;
import org.apache.jena.sparql.expr.ExprFunctionN;
import org.apache.jena.sparql.expr.ExprFunctionOp;
import org.apache.jena.sparql.expr.ExprNone;
import org.apache.jena.sparql.expr.ExprTripleTerm;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.ExprVisitor;
import org.apache.jena.sparql.expr.NodeValue;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpComponentsAnalyser implements OpVisitor {
	private final Logger log = LoggerFactory.getLogger(OpComponentsAnalyser.class);
	private static final Node unionGraph = NodeFactory.createURI("urn:x-arq:UnionGraph");
	private final List<Object> opComponents = new ArrayList<Object>();

	public List<Object> getOpComponents() {
		return Collections.unmodifiableList(opComponents);
	}

	@Override
	public void visit(OpBGP opBGP) {
		log.trace(" - OpBGP - ", opBGP);
		log.trace("{}:", opBGP.getPattern().getList().toString());
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
		log.trace("Sub Op Filter - {}", opFilter.getSubOp().getClass().toString());
		opFilter.getExprs().getList().forEach(e -> {
			log.trace("Exp - {} - {}", e.toString(), e.getClass().toString());
			extractFromExpression(e);
		});
		opFilter.getSubOp().visit(this);

	}

	private List<Object> extractFromExpression(Expr e) {
		List<Object> result = new ArrayList<>();
		OpComponentsAnalyser oca = this;
		e.visit(new ExprVisitor() {

			@Override
			public void visit(ExprNone exprNone) {
				log.trace("Expr - exprNone");
			}

			@Override
			public void visit(ExprAggregator eAgg) {
				log.trace("Expr - eAgg");
				eAgg.visit(this);

			}

			@Override
			public void visit(ExprVar nv) {
				log.trace("Expr - ExprVar");

			}

			@Override
			public void visit(NodeValue nv) {
				log.trace("Expr - NodeValue");
				if (nv.getNode().isURI()) {
					opComponents.add(new Triple(Node.ANY, nv.getNode(), Node.ANY));
				}
			}

			@Override
			public void visit(ExprTripleTerm tripleTerm) {
				log.trace("Expr - tripleTerm");
				opComponents.add(tripleTerm.getTriple());
			}

			@Override
			public void visit(ExprFunctionOp funcOp) {
				log.trace("Expr - funcOp");
				if (funcOp.isGraphPattern()) {
					funcOp.getGraphPattern().visit(oca);
				}
				funcOp.getArgs().forEach(e -> {
					log.trace("Arg - {}", e.getClass().toString());
					e.visit(this);
				});

			}

			@Override
			public void visit(ExprFunctionN func) {
				log.trace("Expr - funcOp - N");
				if (func.isGraphPattern()) {
					func.getGraphPattern().visit(oca);
				}
				func.getArgs().forEach(e -> {
					e.visit(this);
				});
			}

			@Override
			public void visit(ExprFunction3 func) {
				log.trace("Expr - funcOp - 3");
				if (func.isGraphPattern()) {
					func.getGraphPattern().visit(oca);
				}
				func.getArgs().forEach(e -> {
					e.visit(this);
				});
			}

			@Override
			public void visit(ExprFunction2 func) {
				log.trace("Expr - funcOp - 2");
				if (func.isGraphPattern()) {
					func.getGraphPattern().visit(oca);
				}
				func.getArgs().forEach(e -> {
					e.visit(this);
				});

			}

			@Override
			public void visit(ExprFunction1 func) {
				log.trace("Expr - funcOp - 1");
				if (func.isGraphPattern()) {
					func.getGraphPattern().visit(oca);
				}
				func.getArgs().forEach(e -> {
					e.visit(this);
				});
			}

			@Override
			public void visit(ExprFunction0 func) {
				log.trace("Expr - funcOp - 0");
				if (func.isGraphPattern()) {
					func.getGraphPattern().visit(oca);
				}
				func.getArgs().forEach(e -> {
					e.visit(this);
				});
			}
		});

		return result;
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
	public void visit(OpLateral opLateral) {
		opLateral.getLeft().visit(this);
		opLateral.getRight().visit(this);
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

	public boolean match(Node graph, Node subject, Node predicate, Node object) {
		if (opComponents.isEmpty())
			return true;

		for (Object o : opComponents) {
			if (o instanceof Quad) {
				Quad q = (Quad) o;
				if (matchQuad(q, graph, subject, predicate, object)) {
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

	protected boolean matchQuad(Quad q, Node graph, Node subject, Node predicate, Node object) {
		if ((!q.getGraph().isConcrete() || q.getGraph().matches(graph) || q.getGraph().matches(unionGraph))
				&& (!q.getSubject().isConcrete() || q.getSubject().matches(subject))
				&& predicateMatch(q.getPredicate(), predicate) // (!q.getPredicate().isConcrete() ||
				// q.getPredicate().matches(predicate))
				&& (!q.getObject().isConcrete() || q.getObject().matches(object))) {
			return true;
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

}
