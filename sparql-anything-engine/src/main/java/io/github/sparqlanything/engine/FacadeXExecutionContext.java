/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.util.Symbol;

import java.util.HashSet;
import java.util.Set;

public class FacadeXExecutionContext extends ExecutionContext {

	private boolean silent = false;
	public static final Symbol processed = Symbol.create("processed");

	public FacadeXExecutionContext(ExecutionContext other) {
		super(other);
	}

	public static boolean isAlreadyProcessed(ExecutionContext ex, Op op){
		if(!ex.getContext().isDefined(processed)){
			return false;
		}
		HashSet<Op> processedOps = ex.getContext().get(processed);
		return processedOps.contains(op);
	}

	public static void addProcessedOp(ExecutionContext ex, Op op){
		if(!ex.getContext().isDefined(processed)){
			ex.getContext().set(processed, new HashSet<Op>());
		}
		Set<Op> executedOps = ex.getContext().get(processed);
		executedOps.add(op);
	}





	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}
}
