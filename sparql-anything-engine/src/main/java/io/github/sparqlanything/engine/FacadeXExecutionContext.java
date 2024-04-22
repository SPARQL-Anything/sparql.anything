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

import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.util.Symbol;

public class FacadeXExecutionContext extends ExecutionContext {

	private boolean silent = false;
	public static final Symbol hasServiceClause = Symbol.create("has-service");

	public FacadeXExecutionContext(ExecutionContext other) {
		super(other);
		other.getContext().set(hasServiceClause, true);
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}
}
