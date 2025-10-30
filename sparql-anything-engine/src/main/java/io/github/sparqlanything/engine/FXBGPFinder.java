/*
 * Copyright (c) 2025 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 */

package io.github.sparqlanything.engine;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.*;

public class FXBGPFinder extends OpVisitorSkip {

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
	public void visit(OpTable opTable) {
		this.hasTable = true;
		this.opTable = opTable;
	}

	@Override
	public void visit(OpService opService) {
	}

	@Override
	public void visit(OpExtend opExtend) {
		this.opExtend = opExtend;
		opExtend.getSubOp().visit(this);
	}

}
