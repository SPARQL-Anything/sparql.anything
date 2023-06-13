/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.engine;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.*;

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
	public void visit(OpLateral opLateral) {
		opLateral.getLeft().visit(this);
		opLateral.getRight().visit(this);
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
	public void visit(OpExt opExt) {
		OpVisitor.super.visit(opExt);
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
