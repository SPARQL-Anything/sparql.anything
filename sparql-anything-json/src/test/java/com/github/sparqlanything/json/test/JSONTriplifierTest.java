/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.json.test;

import com.github.sparqlanything.json.JSONTriplifier;
import com.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import com.github.sparqlanything.model.FacadeXGraphBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JSONTriplifierTest {

	private String ontologyPrefix = "https://w3id.org/resource/ontology/";
	private Logger log = LoggerFactory.getLogger(JSONTriplifierTest.class);

	@Test
	public void testEmptyAndNull() throws TriplifierHTTPException {

		Triplifier jt = new JSONTriplifier();

		try {
			Properties p1 = new Properties();
			p1.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./emptyobject.json").toString());
			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", p1);
			jt.triplify(p1, b);
			DatasetGraph g1 = b.getDatasetGraph();
			Properties p2 = new Properties();
			p2.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./emptyarray.json").toString());
			FacadeXGraphBuilder b2 = new BaseFacadeXGraphBuilder("test", p2);
			jt.triplify(p2, b2);
			DatasetGraph g2 = b2.getDatasetGraph();
			assertEquals(1L, g1.getDefaultGraph().size());
			assertEquals(1L, g2.getDefaultGraph().size());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		boolean jsonException = false;
//		try {
//			Properties p3 = new Properties();
//			p3.setProperty(IRIArgument.LOCATION.toString(),
//					getClass().getClassLoader().getResource("./emptyfile").toString());
//			jt.triplify(p3, new BaseFacadeXBuilder("test", p3));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		assertTrue(jsonException);

		boolean nullPointerException = false;
		try {
			jt.triplify(null, new BaseFacadeXGraphBuilder("test", null));
		} catch (NullPointerException e) {
			nullPointerException = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(nullPointerException);

	}
	
	

	@Test
	public void testPrimitive() throws TriplifierHTTPException {
		{

			{
				Triplifier jt = new JSONTriplifier();
				Properties properties = new Properties();
				properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
				Model m = ModelFactory.createDefaultModel();
				Resource r = m.createResource();
				m.add(r, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
				m.add(r, m.createProperty(ontologyPrefix + "a"), m.createTypedLiteral(1));
				m.add(r, m.createProperty(ontologyPrefix + "string"), m.createTypedLiteral("string"));
				m.add(r, m.createProperty(ontologyPrefix + "bool"), m.createTypedLiteral(true));
				m.add(r, m.createProperty(ontologyPrefix + "boolf"), m.createTypedLiteral(false));
				m.add(r, m.createProperty(ontologyPrefix + "zero"), m.createTypedLiteral(0));

				DatasetGraph g1;
				try {
					properties.setProperty(IRIArgument.LOCATION.toString(),
							getClass().getClassLoader().getResource("./testprimitive.json").toString());
					FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
					jt.triplify(properties, b);
					g1 = b.getDatasetGraph();
					assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			{
				Triplifier jt = new JSONTriplifier();
				Properties properties = new Properties();
				String root = "https://w3id.org/spice/resource/root";
				properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
				properties.setProperty(IRIArgument.ROOT.toString(), root);
				properties.setProperty(IRIArgument.BLANK_NODES.toString(), "false");

				Model mn = ModelFactory.createDefaultModel();
				Resource rn = mn.createResource(root);
				mn.add(rn, RDF.type, mn.createResource(Triplifier.FACADE_X_TYPE_ROOT));
				mn.add(rn, mn.createProperty(ontologyPrefix + "a"), mn.createTypedLiteral(1));
				mn.add(rn, mn.createProperty(ontologyPrefix + "string"), mn.createTypedLiteral("string"));
				mn.add(rn, mn.createProperty(ontologyPrefix + "bool"), mn.createTypedLiteral(true));
				mn.add(rn, mn.createProperty(ontologyPrefix + "boolf"), mn.createTypedLiteral(false));
				mn.add(rn, mn.createProperty(ontologyPrefix + "zero"), mn.createTypedLiteral(0));

				DatasetGraph g1;
				try {
					properties.setProperty(IRIArgument.LOCATION.toString(),
							getClass().getClassLoader().getResource("./testprimitive.json").toString());
					FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
					jt.triplify(properties, b);
					g1 = b.getDatasetGraph();
					assertTrue(mn.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	public void testNegative() throws TriplifierHTTPException {
		Triplifier jt = new JSONTriplifier();
		Properties properties = new Properties();
		properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
		Model m = ModelFactory.createDefaultModel();
		Resource r = m.createResource();
		m.add(r, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		m.add(r, m.createProperty(ontologyPrefix + "a"), m.createTypedLiteral(1));
		m.add(r, m.createProperty(ontologyPrefix + "string"), m.createTypedLiteral("string"));
		m.add(r, m.createProperty(ontologyPrefix + "bool"), m.createTypedLiteral(true));
		m.add(r, m.createProperty(ontologyPrefix + "boolf"), m.createTypedLiteral(false));
		m.add(r, m.createProperty(ontologyPrefix + "zero"), m.createTypedLiteral(0));
		m.add(r, m.createProperty(ontologyPrefix + "neg"), m.createTypedLiteral(-1));
		m.add(r, m.createProperty(ontologyPrefix + "float"), m.createTypedLiteral(0.1));

		DatasetGraph g1;
		try {
			properties.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./testnumbers.json").toString());
			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
			jt.triplify(properties, b);
			g1 = b.getDatasetGraph();

//			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");
//			m.write(System.out, "TTL");
//			System.out.println("\n\n\n");
//			RDFDataMgr.write(System.out, g1, RDFFormat.TRIG);
			assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void keys() throws TriplifierHTTPException {
		Triplifier jt = new JSONTriplifier();
		Properties properties = new Properties();
		Model m = ModelFactory.createDefaultModel();
		Resource r = m.createResource();
		m.add(r, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		m.add(r, m.createProperty(Triplifier.XYZ_NS + "ab%20cd"), m.createTypedLiteral("ef"));
		m.add(r, m.createProperty(Triplifier.XYZ_NS + "ab-cd"), m.createTypedLiteral("ef"));

		DatasetGraph g1;
		try {
			properties.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./whitespaceKeys.json").toString());

			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
			jt.triplify(properties, b);
			g1 = b.getDatasetGraph();

			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");
			m.write(System.out,"TTL");
			assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testBlankNodeProperty() throws TriplifierHTTPException {
		Triplifier jt = new JSONTriplifier();
		Properties properties = new Properties();
		String root = "https://w3id.org/spice/resource/root";
		properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
		properties.setProperty(IRIArgument.ROOT.toString(), root);
		properties.setProperty(IRIArgument.BLANK_NODES.toString(), "true");

		Model m = ModelFactory.createDefaultModel();
		Resource r = m.createResource();
		m.add(r, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		m.add(r, m.createProperty(ontologyPrefix + "a"), m.createTypedLiteral(1));
		m.add(r, m.createProperty(ontologyPrefix + "string"), m.createTypedLiteral("string"));
		m.add(r, m.createProperty(ontologyPrefix + "bool"), m.createTypedLiteral(true));
		m.add(r, m.createProperty(ontologyPrefix + "boolf"), m.createTypedLiteral(false));
		m.add(r, m.createProperty(ontologyPrefix + "zero"), m.createTypedLiteral(0));

		DatasetGraph g1;
		try {
			properties.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./testprimitive.json").toString());

			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
			jt.triplify(properties, b);
			g1 = b.getDatasetGraph();
			//			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");
			assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBlankNodeFalse() throws TriplifierHTTPException {
		Triplifier jt = new JSONTriplifier();
		Properties properties = new Properties();
		String root = "https://w3id.org/spice/resource/root";
		properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
		properties.setProperty(IRIArgument.ROOT.toString(), root);
		properties.setProperty(IRIArgument.BLANK_NODES.toString(), "false");

		Model mn = ModelFactory.createDefaultModel();
		Resource rn = mn.createResource(root);
		mn.add(rn, RDF.type, mn.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		mn.add(rn, mn.createProperty(ontologyPrefix + "a"), mn.createTypedLiteral(1));
		mn.add(rn, mn.createProperty(ontologyPrefix + "string"), mn.createTypedLiteral("string"));
		mn.add(rn, mn.createProperty(ontologyPrefix + "bool"), mn.createTypedLiteral(true));
		mn.add(rn, mn.createProperty(ontologyPrefix + "boolf"), mn.createTypedLiteral(false));
		mn.add(rn, mn.createProperty(ontologyPrefix + "zero"), mn.createTypedLiteral(0));

		DatasetGraph g1;
		try {
			properties.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./testprimitive.json").toString());
			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
			jt.triplify(properties, b);
			g1 = b.getDatasetGraph();

//			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");

			assertTrue(mn.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBlankNodeFalseNoRoot() throws TriplifierHTTPException {
		Triplifier jt = new JSONTriplifier();
		Properties properties = new Properties();
		String root = getClass().getClassLoader().getResource("./testprimitive.json").toString();
		properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
		properties.setProperty(IRIArgument.ROOT.toString(), root);
		properties.setProperty(IRIArgument.BLANK_NODES.toString(), "false");

		Model mn = ModelFactory.createDefaultModel();
		Resource rn = mn.createResource(root);
		mn.add(rn, RDF.type, mn.createResource(Triplifier.FACADE_X_TYPE_ROOT));
		mn.add(rn, mn.createProperty(ontologyPrefix + "a"), mn.createTypedLiteral(1));
		mn.add(rn, mn.createProperty(ontologyPrefix + "string"), mn.createTypedLiteral("string"));
		mn.add(rn, mn.createProperty(ontologyPrefix + "bool"), mn.createTypedLiteral(true));
		mn.add(rn, mn.createProperty(ontologyPrefix + "boolf"), mn.createTypedLiteral(false));
		mn.add(rn, mn.createProperty(ontologyPrefix + "zero"), mn.createTypedLiteral(0));

		DatasetGraph g1;
		try {
			properties.setProperty(IRIArgument.LOCATION.toString(),
					getClass().getClassLoader().getResource("./testprimitive.json").toString());

			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
			jt.triplify(properties, b);
			g1 = b.getDatasetGraph();

//			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");

			assertTrue(mn.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void s() throws TriplifierHTTPException {
		{
			{
				Model m = ModelFactory.createDefaultModel();
				Resource r = m.createResource();
				m.add(r, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
				m.add(r, RDF.li(1), m.createTypedLiteral(1));
				m.add(r, RDF.li(2), m.createTypedLiteral("abcd"));
				Resource o = m.createResource();
				m.add(r, RDF.li(3), o);
				m.add(o, m.createProperty(ontologyPrefix + "a"), m.createTypedLiteral("a"));
				m.add(r, RDF.li(5), m.createTypedLiteral(4));
				Resource arr = m.createResource();
				m.add(o, m.createProperty(ontologyPrefix + "arr"), arr);
				m.add(arr, RDF.li(1), m.createTypedLiteral(0));
				m.add(arr, RDF.li(2), m.createTypedLiteral(1));

				Properties properties = new Properties();
				properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
				Triplifier jt = new JSONTriplifier();

				DatasetGraph g1;
				try {
					properties.setProperty(IRIArgument.LOCATION.toString(),
							getClass().getClassLoader().getResource("./testarray.json").toString());
					FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
					jt.triplify(properties, b);
					g1 = b.getDatasetGraph();

//					RDFDataMgr.write(System.out, g1, RDFFormat.TRIG);
					assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			{
				String root = "https://w3id.org/spice/resource/root";
				Model m = ModelFactory.createDefaultModel();
				Resource r = m.createResource(root);
				m.add(r, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
				m.add(r, RDF.li(1), m.createTypedLiteral(1));
				m.add(r, RDF.li(2), m.createTypedLiteral("abcd"));
				Resource o = m.createResource(root + "/_3");
				m.add(r, RDF.li(3), o);
				m.add(o, m.createProperty(ontologyPrefix + "a"), m.createTypedLiteral("a"));
				m.add(r, RDF.li(5), m.createTypedLiteral(4));
				Resource arr = m.createResource(root + "/_3/arr");
				m.add(o, m.createProperty(ontologyPrefix + "arr"), arr);
				m.add(arr, RDF.li(1), m.createTypedLiteral(0));
				m.add(arr, RDF.li(2), m.createTypedLiteral(1));
				Properties properties = new Properties();

				properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
				properties.setProperty(IRIArgument.ROOT.toString(), root);
				properties.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
				Triplifier jt = new JSONTriplifier();

				DatasetGraph g1;
				try {
					properties.setProperty(IRIArgument.LOCATION.toString(),
							getClass().getClassLoader().getResource("./testarray.json").toString());
					FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", properties);
					jt.triplify(properties, b);
					g1 = b.getDatasetGraph();
					Iterator<Triple> ii = g1.getDefaultGraph().find();
					while (ii.hasNext()) {
						log.debug("{}", ii.next());
//						System.err.println(ii.next());
					}
					log.debug("---");
					ii = m.getGraph().find();
					while (ii.hasNext()) {
						log.debug("{}", ii.next());
					}
					log.debug("---");

					assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

//	@Ignore // Not sure how to reproduce with the new library
//	@Test(expected = JsonException.class)
//	public void testDuplicateKeyJSON() throws TriplifierHTTPException {
//		{
//			Triplifier jt = new JSONTriplifier();
//			try {
//				Properties p = new Properties();
//				p.setProperty(IRIArgument.LOCATION.toString(),
//						getClass().getClassLoader().getResource("./testduplicatekey.json").toString());
//				jt.triplify(p);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//	}

	@Test
	public void testUTF8encoded() throws TriplifierHTTPException {
//		/collection/artworks/p/052/p05259-15628.json {namespace=http://sparql.xyz/facade-x/data/, location=./collection/artworks/p/052/p05259-15628.json
		String f = "./p05259-15628.json";
		Triplifier jt = new JSONTriplifier();
		try {
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), getClass().getClassLoader().getResource(f).toString());
			jt.triplify(p, new BaseFacadeXGraphBuilder("test", p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore // this seems to work now but keeping the test for future reference
	@Test
	public void testContainsZerosDebug() throws TriplifierHTTPException {
//		/collection/artworks/p/052/p05259-15628.json {namespace=http://sparql.xyz/facade-x/data/, location=./collection/artworks/p/052/p05259-15628.json
		String f = "./t09122-23226.json";
		Triplifier jt = new JSONTriplifier();
		try {
			Properties p = new Properties();
			p.setProperty(IRIArgument.LOCATION.toString(), getClass().getClassLoader().getResource(f).toString());
			//DatasetGraph ds = jt.triplify(p, new BaseFacadeXGraphBuilder("test", p));
			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder("test", p);
			jt.triplify(p, b);
			DatasetGraph ds = b.getDatasetGraph();

			Iterator<Quad> i = ds.find(null);
			while (i.hasNext()) {
				Quad q = i.next();
				log.info("{} {} {} {}", new Object[] { q.getGraph(), q.getSubject(), q.getPredicate(), q.getObject() });
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
