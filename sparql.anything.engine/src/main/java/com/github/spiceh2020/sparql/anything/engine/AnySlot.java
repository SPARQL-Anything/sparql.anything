package com.github.spiceh2020.sparql.anything.engine;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.engine.binding.BindingMap;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import org.apache.jena.sparql.pfunction.PFuncSimple;
import org.apache.jena.util.iterator.ExtendedIterator;

public class AnySlot extends PFuncSimple {

	@Override
	public QueryIterator execEvaluated(Binding parent, Node subject, Node predicate, Node object,
			ExecutionContext execCxt) {
		Node s, o;

		if (subject.isURI()) {
			s = subject;
		} else {
			s = Node.ANY;
		}

		if (object.isURI()) {
			o = object;
		} else {
			o = Node.ANY;
		}

		ExtendedIterator<Triple> it = execCxt.getActiveGraph().find(s, Node.ANY, o);

		return new QueryIterPlainWrapper(new Iterator<Binding>() {

			private Triple cached;
			private boolean hasCached;

			@Override
			public Binding next() {
				if (hasCached) {
					hasCached = false;
					return bindTriple(cached);
				}

				cached = fillNext();

				if (cached != null) {
					return bindTriple(cached);
				}

				throw new NoSuchElementException();
			}

			@Override
			public boolean hasNext() {
				if (hasCached)
					return true;
				cached = fillNext();
				if (cached != null) {
					hasCached = true;
					return true;
				}
				return false;
			}

			private Triple fillNext() {
				while (it.hasNext()) {
					Triple t = it.next();
					if (t.getPredicate().getURI().startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#_")) {
						return t;
					}
				}

				return null;
			}

			private Binding bindTriple(Triple t) {
				BindingMap bm = BindingFactory.create(parent);
				if (subject.isVariable()) {
					if (parent.contains((Var) subject)) {
						bm.add((Var) subject, parent.get((Var) subject));
					} else {
						bm.add((Var) subject, t.getSubject());
					}
				}
				if (object.isVariable()) {
					if (parent.contains((Var) object)) {
						bm.add((Var) object, parent.get((Var) object));
					} else {
						bm.add((Var) object, t.getObject());
					}
				}
				return bm;
			}
		});
	}

}
