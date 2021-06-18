package com.github.spiceh2020.sparql.anything.engine;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.engine.binding.BindingMap;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.pfunction.PFuncSimple;
import org.apache.jena.sparql.pfunction.PropertyFunction;
import org.apache.jena.sparql.pfunction.PropertyFunctionFactory;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

public class Property {

	public static void main(String[] args) {

		Dataset ds = DatasetFactory.create();
		Model m = ds.getDefaultModel();

		ds.begin(ReadWrite.WRITE);
		m.add(m.createResource("https://w3id.org/a"), RDF.li(1), m.createResource("https://w3id.org/b"));
		m.add(m.createResource("https://w3id.org/a"), RDF.li(2), m.createResource("https://w3id.org/c"));
		m.add(m.createResource("https://w3id.org/c"), RDF.li(1), m.createResource("https://w3id.org/d"));
		ds.commit();

		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		ARQ.setTrue(ARQ.enablePropertyFunctions);

		PropertyFunctionFactory p = new PropertyFunctionFactory() {
			@Override
			public PropertyFunction create(final String uri) {
				System.err.println("Ehy");
				return new PFuncSimple() {
					@Override
					public QueryIterator execEvaluated(final Binding parent, final Node subject, final Node predicate,
							final Node object, final ExecutionContext execCxt) {
						System.err.println("Ehy " + Utils.bindingToString(parent));
						System.out.println(execCxt.getActiveGraph().size());
						System.out.println(subject.isURI());
						System.out.println(subject);
						System.out.println(predicate);
						System.out.println(object);

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

							Triple cached;
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
								System.err.println("fill");
								while (it.hasNext()) {
									Triple t = it.next();
									if (t.getPredicate().getURI()
											.startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#_")) {
										System.err.println(t);
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

//									bm.add((Var) object, t.getObject());
								}

								return bm;
							}
						});
//						return QueryIterNullIterator.create(execCxt);
					}
				};
			}
		};

		final PropertyFunctionRegistry reg = PropertyFunctionRegistry.chooseRegistry(ARQ.getContext());
		reg.put("http://example.org/func", p);
		PropertyFunctionRegistry.set(ARQ.getContext(), reg);

		String s = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT * {?s <http://example.org/func>/<http://example.org/func> ?o}";

		QueryExecution qexec = QueryExecutionFactory.create(s, ds);

		System.out.println(ResultSetFormatter.asText(qexec.execSelect()));
	}

}
