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

package io.github.sparqlanything.model.test;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class BaseFacadeXGraphBuilderTest {

	@Test
	public void testWithPredicateLabels(){
		BaseFacadeXGraphBuilder builder = getBaseFacadeXGraphBuilder(true);
		Graph defaultGraph = builder.getDatasetGraph().getDefaultGraph();
		Assert.assertTrue(defaultGraph.contains(NodeFactory.createURI("http://sparql.xyz/facade-x/data/Class2"), RDFS.label.asNode(), NodeFactory.createLiteralString("Class2")));
		Assert.assertTrue(defaultGraph.contains(NodeFactory.createURI("http://sparql.xyz/facade-x/data/slot%201"), RDFS.label.asNode(), NodeFactory.createLiteralString("slot 1")));
		Assert.assertTrue(defaultGraph.contains(NodeFactory.createURI("http://sparql.xyz/facade-x/data/slot2"), RDFS.label.asNode(), NodeFactory.createLiteralString("slot2")));
		Assert.assertTrue(defaultGraph.contains(NodeFactory.createURI("http://sparql.xyz/facade-x/data/Class%201"), RDFS.label.asNode(), NodeFactory.createLiteralString("Class 1")));
	}

	@Test
	public void testWithNoPredicateLabels(){
		BaseFacadeXGraphBuilder builder = getBaseFacadeXGraphBuilder(false);
		Graph defaultGraph = builder.getDatasetGraph().getDefaultGraph();
		Assert.assertFalse(defaultGraph.contains(NodeFactory.createURI("http://sparql.xyz/facade-x/data/Class2"), RDFS.label.asNode(), NodeFactory.createLiteralString("Class2")));
		Assert.assertFalse(defaultGraph.contains(NodeFactory.createURI("http://sparql.xyz/facade-x/data/slot%201"), RDFS.label.asNode(), NodeFactory.createLiteralString("slot 1")));
		Assert.assertFalse(defaultGraph.contains(NodeFactory.createURI("http://sparql.xyz/facade-x/data/slot2"), RDFS.label.asNode(), NodeFactory.createLiteralString("slot2")));
		Assert.assertFalse(defaultGraph.contains(NodeFactory.createURI("http://sparql.xyz/facade-x/data/Class%201"), RDFS.label.asNode(), NodeFactory.createLiteralString("Class 1")));
	}

	private static BaseFacadeXGraphBuilder getBaseFacadeXGraphBuilder(boolean predicateLabels) {
		Properties p = new Properties();
		p.setProperty(IRIArgument.LOCATION.toString(), "http://example.com/ex1");
		p.setProperty(IRIArgument.GENERATE_PREDICATE_LABELS.toString(), Boolean.toString(predicateLabels));
		BaseFacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
		builder.addRoot("");
		builder.addType("","", "Class 1");
		builder.addType("","", "Class2");
		builder.addValue("","","slot 1", "value 1");
		builder.addValue("","","slot2", "value 2");
		return builder;
	}
}
