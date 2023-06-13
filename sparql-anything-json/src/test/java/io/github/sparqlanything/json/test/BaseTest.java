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

package io.github.sparqlanything.json.test;

import io.github.sparqlanything.json.JSONTriplifier;
import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDF;
import org.jsfr.json.Collector;
import org.jsfr.json.JacksonParser;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.ValueBox;
import org.jsfr.json.provider.JacksonProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertTrue;

public class BaseTest {
	private static final Logger L = LoggerFactory.getLogger(BaseTest.class);
	@Test
	public void testContent() throws TriplifierHTTPException {

		Triplifier jt = new JSONTriplifier();

		try {
			Properties p1 = new Properties();
			p1.setProperty(IRIArgument.CONTENT.toString(), "{\"a\":\"b\"}");
			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(p1);
			jt.triplify(p1, b);
			DatasetGraph g1 = b.getDatasetGraph();
//			RDFDataMgr.write(System.out, g1, RDFFormat.TRIG);
			Graph expected = GraphFactory.createDefaultGraph();
			Node n = NodeFactory.createBlankNode();
			expected.add(n, NodeFactory.createURI(Triplifier.XYZ_NS + "a"), NodeFactory.createLiteral("b"));
			expected.add(n, RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
			
			assertTrue(g1.getDefaultGraph().isIsomorphicWith(expected));

		} catch (IOException e1) {
			L.error("",e1);
		}

	}
//
	@Ignore
	@Test
	public void jsonPathTest(){
		InputStream is = getClass().getClassLoader().getResourceAsStream("MultiJsonPath.json");
//		InputStream is = getClass().getClassLoader().getResourceAsStream("SliceArray_2.json");
		JsonSurfer surfer = new JsonSurfer(JacksonParser.INSTANCE, JacksonProvider.INSTANCE);
		Collector collector = surfer.collector(is);
		ValueBox<Collection<Object>> matches = collector.collectAll("$..[?(@.number == 1.2)]");
		collector.exec();

		Iterator<Object> iter = matches.get().iterator();
		while(iter.hasNext()){
			Object o = iter.next();
			System.err.println(o.getClass());
			//ObjectNode on = (ObjectNode) o;
			if(o instanceof Map) {
				Iterator<Entry> iten = ((Map) o).entrySet().iterator();
				while (iten.hasNext()) {
					System.err.println(" > " + iten.next());
				}
			}
		}
	}
}
