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
package io.github.sparqlanything.engine.functions;

import org.apache.jena.query.QueryExecException;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.util.ExprUtils;
import org.apache.jena.sparql.util.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for the entity-building functions (fx:entity, fx:literal, fx:bnode).
 * Error handling is controlled by the query-level option fx.onerror (see #712).
 */
public abstract class FXBuilderFunction extends FunctionBase implements FXFunction {

	public static final Symbol ONERROR = Symbol.create("fx.onerror");

	public enum OnError { UNBOUND, WARN, FAIL, LENIENT }

	private static final Logger L = LoggerFactory.getLogger(FXBuilderFunction.class);

	/** Validation failure. Being an ExprEvalException, it leaves the variable unbound. */
	public static class FXBuildException extends ExprEvalException {
		public FXBuildException(String msg) { super(msg); }
	}

	/** Fail-fast. Not an ExprEvalException, so neither BIND nor COALESCE can swallow it. */
	public static class FXFunctionException extends QueryExecException {
		public FXFunctionException(String msg, Throwable cause) { super(msg, cause); }
	}

	protected abstract NodeValue build(List<NodeValue> args, OnError mode, FunctionEnv env);

	@Override
	public NodeValue exec(Binding binding, ExprList args, String uri, FunctionEnv env) {
		OnError mode = mode(env);
		List<NodeValue> vals = new ArrayList<>(args.size());
		List<String> argErrors = new ArrayList<>(args.size());
		boolean argFailed = false;
		for (Expr e : args) {
			try {
				vals.add(e.eval(binding, env));
				argErrors.add(null);
			} catch (ExprEvalException ex) { // includes VariableNotBoundException
				vals.add(null);
				argErrors.add(ex.getMessage());
				argFailed = true;
			}
		}
		try {
			if (argFailed) throw new FXBuildException("unbound or invalid argument(s)");
			return build(vals, mode, env);
		} catch (FXBuildException ex) {
			report(mode, uri, args, vals, argErrors, binding, ex);
			throw ex; // unbound
		}
	}

	/** Direct calls without an environment: legacy behaviour. */
	@Override
	public NodeValue exec(List<NodeValue> args) {
		return build(args, OnError.LENIENT, null);
	}

	static OnError mode(FunctionEnv env) {
		if (env == null || env.getContext() == null) return OnError.UNBOUND;
		Object o = env.getContext().get(ONERROR);
		if (o == null) return OnError.UNBOUND;
		try {
			return OnError.valueOf(o.toString().trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			L.warn("Unknown value for fx.onerror: '{}'. Using 'unbound'.", o);
			return OnError.UNBOUND;
		}
	}

	private static void report(OnError mode, String uri, ExprList args, List<NodeValue> vals,
	                           List<String> argErrors, Binding binding, Exception ex) {
		if (mode != OnError.WARN && mode != OnError.FAIL) return;
		StringBuilder sb = new StringBuilder();
		sb.append('<').append(uri).append("> failed: ").append(ex.getMessage());
		for (int i = 0; i < args.size(); i++) {
			sb.append("\n  arg ").append(i + 1).append(": ").append(ExprUtils.fmtSPARQL(args.get(i))).append(" = ");
			sb.append(vals.get(i) != null ? vals.get(i).toString() : "<unbound: " + argErrors.get(i) + ">");
		}
		sb.append("\n  solution: ").append(binding);
		if (mode == OnError.FAIL) throw new FXFunctionException(sb.toString(), ex);
		L.warn(sb.toString());
	}
}
