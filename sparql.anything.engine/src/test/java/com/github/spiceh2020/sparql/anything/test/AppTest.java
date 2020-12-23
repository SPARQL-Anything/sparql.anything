package com.github.spiceh2020.sparql.anything.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.engine.FacadeXOpExecutor;
import com.github.spiceh2020.sparql.anything.engine.TriplifierRegister;
import com.github.spiceh2020.sparql.anything.engine.TriplifierRegisterException;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class AppTest {
	private static String PREFIX = "http://example.org/";

	private static DatasetGraph createExampleGraph() {
		DatasetGraph dg = DatasetGraphFactory.create();
		Graph g = GraphFactory.createGraphMem();
		g.add(new Triple(NodeFactory.createURI(PREFIX + "s"), NodeFactory.createURI(PREFIX + "p"),
				NodeFactory.createURI(PREFIX + "o")));
		dg.addGraph(NodeFactory.createURI(PREFIX + "g"), g);
		dg.setDefaultGraph(g);
		return dg;
	}

	@Test
	public void testConstructAndSelect() throws IOException {
		Triplifier t = new Triplifier() {

			@Override
			public DatasetGraph triplify(URL url, Properties properties) throws IOException {
				return createExampleGraph();
			}

			@Override
			public Set<String> getMimeTypes() {
				return Sets.newHashSet("test-mime");
			}

			@Override
			public Set<String> getExtensions() {
				return Sets.newHashSet("test");
			}
		};
		try {

			OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
				@Override
				public OpExecutor create(ExecutionContext execCxt) {
					return new FacadeXOpExecutor(execCxt);
				}
			};

			QC.setFactory(ARQ.getContext(), customExecutorFactory);

			TriplifierRegister.getInstance().registerTriplifier(t);

			Dataset kb = DatasetFactory.createGeneral();
			Query q = QueryFactory
					.create("CONSTRUCT {?s ?p ?o} WHERE { SERVICE<facade-x:http://example.org/file.test>{?s ?p ?o}}");

			Model m = ModelFactory.createDefaultModel();
			m.add(m.createResource(PREFIX + "s"), m.createProperty(PREFIX + "p"), m.createResource(PREFIX + "o"));

			assertTrue(QueryExecutionFactory.create(q, kb).execConstruct().isIsomorphicWith(m));

			Query select = QueryFactory.create(
					"SELECT DISTINCT ?g ?s ?p ?o WHERE { SERVICE<facade-x:http://example.org/file.test>{GRAPH ?g {?s ?p ?o}}}");
			ResultSet rs = QueryExecutionFactory.create(select, kb).execSelect();
			QuerySolution qs = rs.next();

			assertTrue(qs.getResource("g").getURI().equals(PREFIX + "g"));
			assertTrue(qs.getResource("s").getURI().equals(PREFIX + "s"));
			assertTrue(qs.getResource("p").getURI().equals(PREFIX + "p"));
			assertTrue(qs.getResource("o").getURI().equals(PREFIX + "o"));

		} catch (TriplifierRegisterException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRelativePath() throws IOException {
		Triplifier t = new Triplifier() {

			@Override
			public DatasetGraph triplify(URL url, Properties properties) throws IOException {
				DatasetGraph dg = DatasetGraphFactory.create();
				Graph g = GraphFactory.createGraphMem();

				String content = IOUtils.toString(url, Charset.defaultCharset());

				g.add(new Triple(NodeFactory.createURI(PREFIX + "s"), NodeFactory.createURI(PREFIX + "p"),
						NodeFactory.createLiteral(content)));
				dg.addGraph(NodeFactory.createURI(PREFIX + "g"), g);
				dg.setDefaultGraph(g);
				return dg;
			}

			@Override
			public Set<String> getMimeTypes() {
				return Sets.newHashSet("test-mime2");
			}

			@Override
			public Set<String> getExtensions() {
				return Sets.newHashSet("test2");
			}
		};
		try {

			OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
				@Override
				public OpExecutor create(ExecutionContext execCxt) {
					return new FacadeXOpExecutor(execCxt);
				}
			};

			QC.setFactory(ARQ.getContext(), customExecutorFactory);

			TriplifierRegister.getInstance().registerTriplifier(t);

			Dataset kb = DatasetFactory.createGeneral();

			Query select = QueryFactory.create(
					"SELECT DISTINCT ?g ?s ?p ?o WHERE { SERVICE<facade-x:media-type=test-mime2,location=src/main/resources/test.json> {GRAPH ?g {?s ?p ?o}}}");

			ResultSet rs = QueryExecutionFactory.create(select, kb).execSelect();
			QuerySolution qs = rs.next();

			String content = IOUtils.toString(new File("src/main/resources/test.json").toURI().toURL(),
					Charset.defaultCharset());

			assertTrue(qs.getResource("g").getURI().equals(PREFIX + "g"));
			assertTrue(qs.getResource("s").getURI().equals(PREFIX + "s"));
			assertTrue(qs.getResource("p").getURI().equals(PREFIX + "p"));
			assertTrue(qs.get("o").toString().replace("\\", "").equals(content));

		} catch (TriplifierRegisterException e) {
			e.printStackTrace();
		}
	}

}
