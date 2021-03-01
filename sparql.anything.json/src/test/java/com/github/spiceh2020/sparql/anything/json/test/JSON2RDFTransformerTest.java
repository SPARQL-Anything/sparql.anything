package com.github.spiceh2020.sparql.anything.json.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.RDF;
import org.json.JSONException;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.json.JSONTriplifier;
import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class JSON2RDFTransformerTest {

	private String ontologyPrefix = "https://w3id.org/resource/ontology/";

	@Test
	public void testEmptyAndNull() {

		JSONTriplifier jt = new JSONTriplifier();

		try {
			DatasetGraph g1 = jt.triplify(getClass().getClassLoader().getResource("./emptyobject.json"),
					new Properties());
			DatasetGraph g2 = jt.triplify(getClass().getClassLoader().getResource("./emptyarray.json"),
					new Properties());
			assertEquals(1L, g1.getDefaultGraph().size());
			assertEquals(1L, g2.getDefaultGraph().size());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		boolean jsonException = false;
		try {
			jt.triplify(getClass().getClassLoader().getResource("./emptyfile"), new Properties());
		} catch (JSONException e) {
			jsonException = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(jsonException);

		boolean nullPointerException = false;
		try {
			jt.triplify(null, new Properties());
		} catch (NullPointerException e) {
			nullPointerException = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(nullPointerException);

	}

	@Test
	public void testPrimitive() {
		{

			{
				JSONTriplifier jt = new JSONTriplifier();
				Properties properties = new Properties();
				properties.setProperty(IRIArgument.NAMESPACE.toString(), ontologyPrefix);
				Model m = ModelFactory.createDefaultModel();
				Resource r = m.createResource();
				m.add(r, RDF.type, m.createResource(Triplifier.FACADE_X_TYPE_ROOT));
				m.add(r, m.createProperty(ontologyPrefix + "a"), m.createTypedLiteral(1));
				m.add(r, m.createProperty(ontologyPrefix + "string"), m.createTypedLiteral("string"));
				m.add(r, m.createProperty(ontologyPrefix + "bool"), m.createTypedLiteral(true));
				m.add(r, m.createProperty(ontologyPrefix + "boolf"), m.createTypedLiteral(false));

				DatasetGraph g1;
				try {
					g1 = jt.triplify(getClass().getClassLoader().getResource("./testprimitive.json"), properties);
					assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			{
				JSONTriplifier jt = new JSONTriplifier();
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

				DatasetGraph g1;
				try {
					g1 = jt.triplify(getClass().getClassLoader().getResource("./testprimitive.json"), properties);
					assertTrue(mn.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	public void testBlankNodeProperty() {
		JSONTriplifier jt = new JSONTriplifier();
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

		DatasetGraph g1;
		try {
			g1 = jt.triplify(getClass().getClassLoader().getResource("./testprimitive.json"), properties);
			assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBlankNodeFalse() {
		JSONTriplifier jt = new JSONTriplifier();
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

		DatasetGraph g1;
		try {
			g1 = jt.triplify(getClass().getClassLoader().getResource("./testprimitive.json"), properties);
//			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");

			assertTrue(mn.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBlankNodeFalseNoRoot() {
		JSONTriplifier jt = new JSONTriplifier();
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

		DatasetGraph g1;
		try {
			g1 = jt.triplify(getClass().getClassLoader().getResource("./testprimitive.json"), properties);
//			ModelFactory.createModelForGraph(g1.getDefaultGraph()).write(System.out, "TTL");

			assertTrue(mn.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testArray() {
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
				JSONTriplifier jt = new JSONTriplifier();

				DatasetGraph g1;
				try {
					g1 = jt.triplify(getClass().getClassLoader().getResource("./testarray.json"), properties);
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
				JSONTriplifier jt = new JSONTriplifier();

				DatasetGraph g1;
				try {
					g1 = jt.triplify(getClass().getClassLoader().getResource("./testarray.json"), properties);
					assertTrue(m.getGraph().isIsomorphicWith(g1.getDefaultGraph()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Test(expected = org.json.JSONException.class)
	public void testDuplicateKeyJSON() {
		{
			JSONTriplifier jt = new JSONTriplifier();
			try {
				jt.triplify(getClass().getClassLoader().getResource("./testduplicatekey.json"), new Properties());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
