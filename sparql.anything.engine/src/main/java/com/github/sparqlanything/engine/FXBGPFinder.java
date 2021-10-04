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

import org.apache.jena.graph.Triple;
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

	}

	@Override
	public void visit(OpPropFunc opPropFunc) {

	}

	@Override
	public void visit(OpFilter opFilter) {
		opFilter.getSubOp().visit(this);

	}

	@Override
	public void visit(OpGraph opGraph) {

	}

	@Override
	public void visit(OpService opService) {

	}

	@Override
	public void visit(OpDatasetNames dsNames) {

	}

	@Override
	public void visit(OpLabel opLabel) {

	}

	@Override
	public void visit(OpAssign opAssign) {

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

	}

	@Override
	public void visit(OpDiff opDiff) {

	}

	@Override
	public void visit(OpMinus opMinus) {

	}

	@Override
	public void visit(OpConditional opCondition) {

	}

	@Override
	public void visit(OpSequence opSequence) {

	}

	@Override
	public void visit(OpDisjunction opDisjunction) {

	}

	@Override
	public void visit(OpList opList) {

	}

	@Override
	public void visit(OpOrder opOrder) {

	}

	@Override
	public void visit(OpProject opProject) {

	}

	@Override
	public void visit(OpReduced opReduced) {

	}

	@Override
	public void visit(OpDistinct opDistinct) {

	}

	@Override
	public void visit(OpSlice opSlice) {

	}

	@Override
	public void visit(OpGroup opGroup) {

	}

	@Override
	public void visit(OpTopN opTop) {

	}

}
