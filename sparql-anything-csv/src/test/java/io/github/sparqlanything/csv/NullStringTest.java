/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.csv;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.fail;

public class NullStringTest {

	private CSVTriplifier triplifier = new CSVTriplifier();
	
	@Test
	public void testCsvNullStrings() throws IOException, TriplifierHTTPException {
		testCsvNullString("");
		testCsvNullString("N/A");
		testCsvNullString(" ");
	}

	public void testCsvNullString(String nullString) throws IOException, TriplifierHTTPException {
		Properties properties = new Properties();
		properties.setProperty("namespace", "http://www.example.org#");
		properties.setProperty("csv.null-string", nullString);
		URL csv1 = getClass().getClassLoader().getResource("./test3.csv");
		assert csv1 != null;
		properties.setProperty(IRIArgument.LOCATION.toString(), csv1.toString());
		BasicPattern bp = new BasicPattern();
		bp.add(Triple.create(NodeFactory.createVariable("s"), NodeFactory.createVariable("p"),
				NodeFactory.createVariable("o")));
		FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(properties);
		triplifier.triplify(properties, b);
		DatasetGraph graph = b.getDatasetGraph();
		// with csv.null-string set to nullString we should not see any quads with nullString in the object position
		if (graph.find(Node.ANY, Node.ANY, Node.ANY, NodeFactory.createLiteralString(nullString)).hasNext()) {
			fail("csv.null-string didn't work for: \"" + nullString + "\"");
		}
	}
}
