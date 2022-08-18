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

import org.apache.jena.graph.Triple;
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

public class FXBGPFinder implements OpVisitor {

	private OpBGP serviceBGP;
	private OpTable opTable;
	private OpExtend opExtend;
	private boolean hasTable = false;

	public OpBGP getBGP() {
		return serviceBGP;
	}

	public OpTable getOpTable() {
		return opTable;
	}

	public OpExtend getOpExtend() {
		return opExtend;
	}

	public boolean hasTable() {
		return hasTable;
	}

	@Override
	public void visit(OpBGP opBGP) {
		for (Triple t : opBGP.getPattern()) {
			if (Utils.isPropertyOp(t.getSubject())) {
				serviceBGP = opBGP;
				break;
			}
		}
	}

	@Override
	public void visit(OpQuadPattern quadPattern) {

	}

	@Override
	public void visit(OpQuadBlock quadBlock) {

	}

	@Override
	public void visit(OpTriple opTriple) {

	}

	@Override
	public void visit(OpQuad opQuad) {

	}

	@Override
	public void visit(OpPath opPath) {

	}

	@Override
	public void visit(OpTable opTable) {
		this.hasTable = true;
		this.opTable = opTable;
	}

	@Override
	public void visit(OpNull opNull) {

	}

	@Override
	public void visit(OpProcedure opProc) {
		opProc.getSubOp().visit(this);
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
		opGraph.getSubOp().visit(this);
	}

	@Override
	public void visit(OpService opService) {
//		opService.getSubOp().visit(this);
	}

	@Override
	public void visit(OpDatasetNames dsNames) {

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
		this.opExtend = opExtend;
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
	public void visit(OpConditional op) {
		op.getLeft().visit(this);
		op.getRight().visit(this);
	}

	@Override
	public void visit(OpSequence opSequence) {
		for (Op op : opSequence.getElements()) {
			op.visit(this);
		}
	}

	@Override
	public void visit(OpDisjunction op) {
		for (Op op1 : op.getElements()) {
			op1.visit(this);
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
	public void visit(OpTopN opTop) {
		opTop.getSubOp().visit(this);
	}

}
