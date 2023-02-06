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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Not used yet
 */
public class OpCloner implements OpVisitor {

	Op original = null;
	Op subOp = null;
	Op copy = null;
	 public OpCloner(Op original){
		 this.original = original;
		 this.original.visit(this);
	 }

	 public Op getCopy(){
		 return copy;
	 }

	@Override
	public void visit(OpBGP opBGP) {
		OpBGP op = (OpBGP) opBGP.copy();
		copy = op;
	}

	@Override
	public void visit(OpQuadPattern quadPattern) {
		OpQuadPattern op = (OpQuadPattern) quadPattern.copy();
		copy = op;
	}

	@Override
	public void visit(OpQuadBlock quadBlock) {
		OpQuadBlock op = (OpQuadBlock) quadBlock.copy();
		copy = op;
	}

	@Override
	public void visit(OpTriple opTriple) {
		OpTriple op = (OpTriple) opTriple.copy();
		copy = op;
	}

	@Override
	public void visit(OpQuad opQuad) {
		OpQuad op = (OpQuad) opQuad.copy();
		copy = op;
	}

	@Override
	public void visit(OpPath opPath) {
		OpPath op = (OpPath) opPath.copy();
		copy = op;
	}

	@Override
	public void visit(OpTable opTable) {
		OpTable op = (OpTable) opTable.copy();
		copy = op;
	}

	@Override
	public void visit(OpNull opNull) {
		OpNull op = (OpNull) opNull.copy();
		copy = op;
	}

	@Override
	public void visit(OpProcedure opProc) {
		 opProc.getSubOp().visit(this);
		OpProcedure op = (OpProcedure) opProc.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpPropFunc opPropFunc) {
		opPropFunc.getSubOp().visit(this);
		OpPropFunc op = (OpPropFunc) opPropFunc.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpFilter opFilter) {
		opFilter.getSubOp().visit(this);
		OpFilter op = (OpFilter) opFilter.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpGraph opGraph) {
		opGraph.getSubOp().visit(this);
		OpGraph op = (OpGraph) opGraph.copy(opGraph);
		copy = op;
	}

	@Override
	public void visit(OpService opService) {
		 opService.getSubOp().visit(this);
		OpService op = (OpService) opService.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpDatasetNames dsNames) {
		OpDatasetNames op = (OpDatasetNames) dsNames.copy();
		copy = op;
	}

	@Override
	public void visit(OpLabel opLabel) {
		 opLabel.getSubOp().visit(this);
		OpLabel op = (OpLabel) opLabel.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpAssign opAssign) {
		 opAssign.getSubOp().visit(this);
		 OpAssign op = (OpAssign) opAssign.copy(copy);
		 copy = op;
	}

	@Override
	public void visit(OpExtend opExtend) {
		opExtend.getSubOp().visit(this);
		OpExtend op = (OpExtend) opExtend.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpJoin opJoin) {
		opJoin.getLeft().visit(this);
		Op left = copy;
		opJoin.getRight().visit(this);
		Op right = copy;
		OpJoin op = (OpJoin) opJoin.copy(left, right);
		copy = op;
	}

	@Override
	public void visit(OpLeftJoin opLeftJoin) {
		opLeftJoin.getLeft().visit(this);
		Op left = copy;
		opLeftJoin.getRight().visit(this);
		Op right = copy;
		OpLeftJoin op = (OpLeftJoin) opLeftJoin.copy(left, right);
		copy = op;
	}

	@Override
	public void visit(OpUnion opUnion) {
		opUnion.getLeft().visit(this);
		Op left = copy;
		opUnion.getRight().visit(this);
		Op right = copy;
		OpUnion op = (OpUnion) opUnion.copy(left, right);
		copy = op;
	}

	@Override
	public void visit(OpDiff opDiff) {
		opDiff.getLeft().visit(this);
		Op left = copy;
		opDiff.getRight().visit(this);
		Op right = copy;
		OpDiff op = (OpDiff) opDiff.copy(left, right);
		copy = op;
	}

	@Override
	public void visit(OpMinus opMinus) {
		opMinus.getLeft().visit(this);
		Op left = copy;
		opMinus.getRight().visit(this);
		Op right = copy;
		OpMinus op = (OpMinus) opMinus.copy(left, right);
		copy = op;
	}

	@Override
	public void visit(OpConditional opCondition) {
		opCondition.getLeft().visit(this);
		Op left = copy;
		opCondition.getRight().visit(this);
		Op right = copy;
		OpConditional op = (OpConditional) opCondition.copy(left, right);
		copy = op;
	}

	@Override
	public void visit(OpSequence opSequence) {
		List<Op> s = new ArrayList<Op>();
		 Iterator<Op> it = opSequence.iterator();
		 while(it.hasNext()){
			 Op o = it.next();
			 o.visit(this);
			 s.add(copy);
		 }
		OpSequence op = (OpSequence) opSequence.copy(s);
		copy = op;
	}

	@Override
	public void visit(OpDisjunction opDisjunction) {
		List<Op> s = new ArrayList<Op>();
		Iterator<Op> it = opDisjunction.iterator();
		while(it.hasNext()){
			Op o = it.next();
			o.visit(this);
			s.add(copy);
		}
		OpDisjunction op = (OpDisjunction) opDisjunction.copy(s);
		copy = op;
	}

	@Override
	public void visit(OpList opList) {
		opList.getSubOp().visit(this);
		OpList op = (OpList) opList.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpOrder opOrder) {
		opOrder.getSubOp().visit(this);
		OpOrder op = (OpOrder) opOrder.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpProject opProject) {
		opProject.getSubOp().visit(this);
		OpProject op = (OpProject) opProject.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpReduced opReduced) {
		opReduced.getSubOp().visit(this);
		OpReduced op = (OpReduced) opReduced.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpDistinct opDistinct) {
		opDistinct.getSubOp().visit(this);
		OpDistinct op = (OpDistinct) opDistinct.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpSlice opSlice) {
		opSlice.getSubOp().visit(this);
		OpSlice op = (OpSlice) opSlice.copy(copy);
		copy = op;

	}

	@Override
	public void visit(OpGroup opGroup) {
		opGroup.getSubOp().visit(this);
		OpGroup op = (OpGroup) opGroup.copy(copy);
		copy = op;
	}

	@Override
	public void visit(OpTopN opTop) {
		opTop.getSubOp().visit(this);
		OpTopN op = (OpTopN) opTop.copy(copy);
		copy = op;
	}
}
