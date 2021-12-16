/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.bib.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import com.github.sparqlanything.bib.BibtexTriplifier;
import com.github.sparqlanything.model.BaseFacadeXBuilder;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;

public class BaseTest {

	@Test
	public void testContent() throws TriplifierHTTPException {

		Triplifier jt = new BibtexTriplifier();

		try {
			Properties p1 = new Properties();
			p1.setProperty(IRIArgument.CONTENT.toString(), "@article{Knuth1984, title={Literate Programming}}");
			DatasetGraph g1 = jt.triplify(p1, new BaseFacadeXBuilder("test", p1));
//			RDFDataMgr.write(System.out, g1, RDFFormat.TRIG);
			Graph expected = GraphFactory.createDefaultGraph();
			Node n = NodeFactory.createBlankNode();
			expected.add(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
			Node article = NodeFactory.createBlankNode();
			expected.add(n, RDF.li(1).asNode(), article);
			expected.add(article, RDF.type.asNode(), NodeFactory.createURI(Triplifier.XYZ_NS + "article"));
			expected.add(article, NodeFactory.createURI(Triplifier.XYZ_NS + "title"),
					NodeFactory.createLiteral("Literate Programming"));
			assertTrue(g1.getDefaultGraph().isIsomorphicWith(expected));

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	@Test
	public void testBlankNodeFalse() throws TriplifierHTTPException {

		Triplifier jt = new BibtexTriplifier();

		try {
			Properties p1 = new Properties();
			p1.setProperty(IRIArgument.CONTENT.toString(), "@article{Knuth1984, title={Literate Programming}}");
			p1.setProperty(IRIArgument.BLANK_NODES.toString(), "false");
			DatasetGraph g1 = jt.triplify(p1, new BaseFacadeXBuilder("test", p1));
			g1.find().forEachRemaining(q->{
				assertFalse(q.getGraph().isBlank());
				assertFalse(q.getSubject().isBlank());
				assertFalse(q.getPredicate().isBlank());
				assertFalse(q.getObject().isBlank());
			});

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}
