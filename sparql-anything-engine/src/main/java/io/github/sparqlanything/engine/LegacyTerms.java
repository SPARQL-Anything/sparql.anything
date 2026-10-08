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
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.TransformCopy;
import org.apache.jena.sparql.algebra.Transformer;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpQuad;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprTransformCopy;
import org.apache.jena.sparql.expr.NodeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Backward compatibility for Façade-X terms renamed by the specification (#681).
 * <p>
 * The root container type {@code fx:root} was renamed {@code fx:Root} (#606). Queries using the old
 * term are rewritten to the new one, with a deprecation warning, so they keep returning results.
 * <p>
 * {@code fx:root} is also the deprecated {@code fx:} form of the {@code root} engine option
 * ({@code fx:properties fx:root "..."}), so only the <em>object</em> of a triple pattern that is not an
 * option triple is rewritten, plus constants in expressions (e.g. {@code FILTER(?t = fx:root)}).
 * Predicates and option triples are left untouched.
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
	 * Rewrites deprecated Façade-X terms in an operator. Returns the operator unchanged if it contains none.
	 */
	public static Op rewrite(Op op) {
		RootTransform transform = new RootTransform();
		RootExprTransform exprTransform = new RootExprTransform();
		Op rewritten = Transformer.transform(transform, exprTransform, op);
		if (!transform.found && !exprTransform.found) {
			return op;
		}
		if (REPORTED.compareAndSet(false, true)) {
			logger.warn("fx:root is deprecated as the type of the root container: use fx:Root (<{}>). "
					+ "Queries using fx:root are rewritten to fx:Root; this will be removed in a future release.",
				Triplifier.FACADE_X_TYPE_ROOT);
		}
		return rewritten;
	}

	/** True if the object must be rewritten: it is fx:root and the triple is not an engine option. */
	private static boolean isLegacyRootObject(Node subject, Node object) {
		return ROOT_LEGACY.equals(object) && !Utils.isPropertyOp(subject);
	}

	private static Triple rewrite(Triple t) {
		return Triple.create(t.getSubject(), t.getPredicate(), ROOT);
	}

	private static final class RootTransform extends TransformCopy {
		boolean found = false;

		@Override
		public Op transform(OpBGP opBGP) {
			BasicPattern bp = new BasicPattern();
			boolean changed = false;
			for (Triple t : opBGP.getPattern()) {
				if (isLegacyRootObject(t.getSubject(), t.getObject())) {
					bp.add(rewrite(t));
					changed = true;
				} else {
					bp.add(t);
				}
			}
			if (!changed) return super.transform(opBGP);
			found = true;
			return new OpBGP(bp);
		}

		@Override
		public Op transform(OpTriple opTriple) {
			Triple t = opTriple.getTriple();
			if (!isLegacyRootObject(t.getSubject(), t.getObject())) return super.transform(opTriple);
			found = true;
			return new OpTriple(rewrite(t));
		}

		@Override
		public Op transform(OpQuadPattern opQuadPattern) {
			BasicPattern bp = new BasicPattern();
			boolean changed = false;
			for (Triple t : opQuadPattern.getBasicPattern()) {
				if (isLegacyRootObject(t.getSubject(), t.getObject())) {
					bp.add(rewrite(t));
					changed = true;
				} else {
					bp.add(t);
				}
			}
			if (!changed) return super.transform(opQuadPattern);
			found = true;
			return new OpQuadPattern(opQuadPattern.getGraphNode(), bp);
		}

		@Override
		public Op transform(OpQuad opQuad) {
			Quad q = opQuad.getQuad();
			if (!isLegacyRootObject(q.getSubject(), q.getObject())) return super.transform(opQuad);
			found = true;
			return new OpQuad(Quad.create(q.getGraph(), q.getSubject(), q.getPredicate(), ROOT));
		}

		@Override
		public Op transform(OpPath opPath) {
			TriplePath tp = opPath.getTriplePath();
			if (!isLegacyRootObject(tp.getSubject(), tp.getObject())) return super.transform(opPath);
			found = true;
			TriplePath rewritten = tp.isTriple()
				? new TriplePath(rewrite(tp.asTriple()))
				: new TriplePath(tp.getSubject(), tp.getPath(), ROOT);
			return new OpPath(rewritten);
		}
	}

	private static final class RootExprTransform extends ExprTransformCopy {
		boolean found = false;

		@Override
		public Expr transform(NodeValue nv) {
			if (nv.isIRI() && ROOT_LEGACY.equals(nv.asNode())) {
				found = true;
				return NodeValue.makeNode(ROOT);
			}
			return super.transform(nv);
		}
	}
}
