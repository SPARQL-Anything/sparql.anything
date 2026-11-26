/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
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
package io.github.sparqlanything.engine;

import org.apache.jena.graph.Graph;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.Symbol;

/**
 * Facade-X execution contexts.
 * <p>
 * Since Jena 6.3, {@link ExecutionContext} is final and cannot be extended. A Facade-X execution
 * context (i.e. one used to evaluate the body of a SPARQL Anything SERVICE clause) is therefore a plain
 * {@link ExecutionContext} whose {@link Context} contains the symbol {@link #FACADEX} set to {@code true}.
 * The symbol is set on a copy of the context, so that it does not leak into the rest of the query.
 * <p>
 * <b>To check whether an execution context is a Facade-X one, use {@link #isFacadeX(ExecutionContext)}.</b>
 * Do not rely on the Java type: it is always {@link ExecutionContext}.
 */
public final class FacadeXExecutionContext {

	/** Marks a Facade-X execution context. Test it with {@link #isFacadeX(ExecutionContext)}. */
	public static final Symbol FACADEX = Symbol.create("http://sparql.xyz/facade-x/ns/executionContext");

	private FacadeXExecutionContext() {
	}

	/**
	 * Creates a Facade-X execution context on the given dataset and active graph.
	 * The context is copied and marked with {@link #FACADEX}.
	 */
	public static ExecutionContext create(DatasetGraph dg, Graph activeGraph, Context context) {
		Context cxt = context.copy();
		cxt.set(FACADEX, true);
		return ExecutionContext.create(dg, activeGraph, cxt);
	}

	/** Creates a Facade-X copy of an existing execution context. */
	public static ExecutionContext create(ExecutionContext other) {
		return create(other.getDataset(), other.getActiveGraph(), other.getContext());
	}

	/**
	 * True if the execution context is a Facade-X one, i.e. if its {@link Context} has
	 * {@link #FACADEX} set to {@code true}.
	 */
	public static boolean isFacadeX(ExecutionContext ex) {
		return ex.getContext().isTrue(FACADEX);
	}
}
