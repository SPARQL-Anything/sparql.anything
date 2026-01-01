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



package io.github.sparqlanything.cli;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.lang.LangJSONLD11;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RiotUtils {
	static class ReaderRIOTFactoryJSONLD implements ReaderRIOTFactory {
		@Override
		public ReaderRIOT create(Lang language, ParserProfile profile) {
			if ( !JSON.equals(language) )
				throw new InternalErrorException("Attempt to parse " + language + " as JSON-LD");
			return new LangJSONLD11(language, profile, profile.getErrorHandler());
		}
	}

	/**
	 * We add a lang JSON to attempt to load JSON files as JSON-LD
	 * See #339
	 */
	public static Lang JSON;

	static {
		JSON = LangBuilder.create("JSON", "application/ld+json")
				.addAltNames("JSON")
				.addFileExtensions("json")
				.build();
	}


	public static ResultSet asResultSet(DatasetGraph dsg) {
		final List<String> varnames = List.of("g", "s", "p", "o");
		final Iterator<Quad> qiter = dsg.stream().iterator();
		return new ResultSet() {
			@Override
			public boolean hasNext() {
				return qiter.hasNext();
			}

			@Override
			public QuerySolution next() {
				rowno += 1;
				final Quad q = qiter.next();
				return new QuerySolution() {
					@Override
					public RDFNode get(String s) {
						return switch (s) {
							case "g" -> ResourceFactory.createPlainLiteral(q.getGraph().toString());
							case "s" -> ResourceFactory.createPlainLiteral(q.getSubject().toString());
							case "p" -> ResourceFactory.createPlainLiteral(q.getPredicate().toString());
							case "o" -> ResourceFactory.createPlainLiteral(q.getObject().toString());
							default -> throw new RuntimeException();
						};
					}

					@Override
					public Resource getResource(String s) {
						return switch (s) {
							case "g" -> ResourceFactory.createResource(q.getGraph().toString());
							case "s" -> ResourceFactory.createResource(q.getSubject().toString());
							case "p" -> ResourceFactory.createResource(q.getPredicate().toString());
							case "o" -> ResourceFactory.createResource(q.getObject().toString());
							default -> throw new RuntimeException();
						};
					}

					@Override
					public Literal getLiteral(String s) {
						switch (s) {
							case "g":
								return ResourceFactory.createPlainLiteral(q.getGraph().toString());
							case "s":
								return ResourceFactory.createPlainLiteral(q.getSubject().toString());
							case "p":
								return ResourceFactory.createPlainLiteral(q.getPredicate().toString());
							case "o":
								return ResourceFactory.createPlainLiteral(q.getObject().toString());
						}
						throw new RuntimeException();
					}

					@Override
					public StatementTerm getStatementTerm(String s) {
						throw new UnsupportedOperationException();
					}

					@Override
					public boolean contains(String s) {
						return varnames.contains(s);
					}

					@Override
					public Iterator<String> varNames() {
						return varnames.iterator();
					}
				};
			}

			@Override
			public void forEachRemaining(Consumer<? super QuerySolution> consumer) {
				while(this.hasNext()) {
					consumer.accept(next());
				}
			}

			@Override
			public QuerySolution nextSolution() {
				return next();
			}

			@Override
			public Binding nextBinding() {

				final QuerySolution qs = next();
				return new Binding() {
					@Override
					public Iterator<Var> vars() {
						return Var.varList(varnames).iterator();
					}

					@Override
					public Set<Var> varsMentioned() {
						return new HashSet(varnames);
					}

					@Override
					public void forEach(BiConsumer<Var, Node> action) {
						throw new NotImplementedException();
					}

					@Override
					public boolean contains(Var var) {
						return Var.varList(varnames).contains(var);
					}

					@Override
					public Node get(Var var) {
						return qs.get(var.getVarName()).asNode();
					}

					@Override
					public int size() {
						return 4;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public Binding detach() {
						throw new UnsupportedOperationException();
					}
				};
			}
			private int rowno = 0;

			@Override
			public int getRowNumber() {
				return rowno;
			}

			@Override
			public List<String> getResultVars() {
				return varnames;
			}

			@Override
			public Model getResourceModel() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void close() {

			}
		};
	}
}
