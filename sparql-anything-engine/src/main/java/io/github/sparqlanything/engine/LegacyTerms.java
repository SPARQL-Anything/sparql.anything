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

import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.graph.NodeTransform;
import org.apache.jena.sparql.graph.NodeTransformLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Backward compatibility for Façade-X terms renamed by the specification (#681).
 * <p>
 * {@code fx:root} was renamed {@code fx:Root} (#606). Queries using the old term are rewritten
 * to the new one, with a deprecation warning, so they keep returning results.
 */
public final class LegacyTerms {

	private static final Logger logger = LoggerFactory.getLogger(LegacyTerms.class);

	/** @deprecated use {@link Triplifier#FACADE_X_TYPE_ROOT}; still accepted in queries. */
	@Deprecated
	public static final String FACADE_X_TYPE_ROOT_LEGACY = Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "root";

	private static final Node ROOT_LEGACY = NodeFactory.createURI(FACADE_X_TYPE_ROOT_LEGACY);
	private static final Node ROOT = NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT);

	// warn once per JVM, as for the deprecated fx: options
	private static final AtomicBoolean REPORTED = new AtomicBoolean(false);

	private LegacyTerms() {
	}

	/**
	 * Rewrites deprecated Façade-X terms in an operator (triple patterns, quads, property paths and expressions).
	 * Returns the operator unchanged if it contains none.
	 */
	public static Op rewrite(Op op) {
		final boolean[] found = {false};
		NodeTransform nt = n -> {
			if (ROOT_LEGACY.equals(n)) {
				found[0] = true;
				return ROOT;
			}
			return n;
		};
		Op rewritten = NodeTransformLib.transform(nt, op);
		if (!found[0]) {
			return op;
		}
		if (REPORTED.compareAndSet(false, true)) {
			logger.warn("fx:root is deprecated: use fx:Root (<{}>). Queries using fx:root are rewritten to fx:Root; "
					+ "this will be removed in a future release.", Triplifier.FACADE_X_TYPE_ROOT);
		}
		return rewritten;
	}
}
