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

package io.github.sparqlanything.model.test;

import io.github.sparqlanything.model.TripleFilteringFacadeXGraphBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TripleFilteringFacadeXBuilderTest {
    public static final Logger log = LoggerFactory.getLogger(TripleFilteringFacadeXBuilderTest.class);

	private static Properties getProperties(){
		Properties p = new Properties();
		p.put("location", "http://www.example.org/");
		return p;
	}

    /**
     * BGP, 1, {? ? L}
     */
    @Test
    public void bgp1(){
        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(new Node_Variable("a"), new Node_Variable("b"), ResourceFactory.createPlainLiteral("Hello world").asNode()));

        TripleFilteringFacadeXGraphBuilder f = new TripleFilteringFacadeXGraphBuilder("http://www.example.org/", bgp, getProperties());
		Resource g = ResourceFactory.createResource("http://www.example.org/");
        f.add(g.asNode(), ResourceFactory.createResource().asNode(), RDF.type.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(g.asNode(),ResourceFactory.createResource().asNode(), RDF.type.asNode(), ResourceFactory.createPlainLiteral("Hello world not!").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
    }

    /**
     * BGP, 1, {[] ? L}
     */
    @Test
    public void bgp2(){
        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(Node_Variable.ANY, new Node_Variable("b"), ResourceFactory.createPlainLiteral("Hello world").asNode()));

        TripleFilteringFacadeXGraphBuilder f = new TripleFilteringFacadeXGraphBuilder("http://www.example.org/", bgp, getProperties());
		Node g = ResourceFactory.createResource("http://www.example.org/").asNode();
        f.add(g, ResourceFactory.createResource().asNode(), RDF.type.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(g, ResourceFactory.createResource().asNode(), RDF.type.asNode(), ResourceFactory.createPlainLiteral("Hello world not!").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
    }

    /**
     * BGP, 1, {[] P L}
     */
    @Test
    public void bgp3(){
        Property property = ResourceFactory.createProperty("http://sparql.xyz/facade-x/data/property");

        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(Node_Variable.ANY, property.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode()));

        TripleFilteringFacadeXGraphBuilder f = new TripleFilteringFacadeXGraphBuilder("http://www.example.org/", bgp, getProperties());
		Node g = ResourceFactory.createResource("http://www.example.org/").asNode();
		//
        f.add(g, ResourceFactory.createResource().asNode(), RDF.type.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 0);
        f.add(g, ResourceFactory.createResource().asNode(), RDF.type.asNode(), ResourceFactory.createPlainLiteral("Hello world not!").asNode());
        Assert.assertTrue(f.getModel().size() == 0);
        //
        f.add(g, ResourceFactory.createResource().asNode(), property.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(g, ResourceFactory.createResource().asNode(), property.asNode(), ResourceFactory.createPlainLiteral("Hello world not!").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
    }

    /**
     * BGP, 2, {[] P L, [] P2 L}
     */
    @Test
    public void bgp4(){
        Property property1 = ResourceFactory.createProperty("http://sparql.xyz/facade-x/data/property1");
        Property property2 = ResourceFactory.createProperty("http://sparql.xyz/facade-x/data/property2");

        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(Node_Variable.ANY, property1.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode()));
        bgp.getPattern().add(new Triple(Node_Variable.ANY, property2.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode()));

        TripleFilteringFacadeXGraphBuilder f = new TripleFilteringFacadeXGraphBuilder("http://www.example.org/", bgp, getProperties());
		Node g = ResourceFactory.createResource("http://www.example.org/").asNode();
		//
        f.add(g, ResourceFactory.createResource().asNode(), property1.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(g, ResourceFactory.createResource().asNode(), property2.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 2);
    }

    /**
     * BGP, 2, {R1 P1 R3, R2 P2 R4}
     */
    @Test
    public void bgp5(){
        Node property1 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/property1");
		Node property2 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/property2");
		Node resource1 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource1");
		Node resource2 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource2");
		Node resource3 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource3");
		Node resource4 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource4");

        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(resource1, property1, resource3));
        bgp.getPattern().add(new Triple(resource2, property2, resource4));

        TripleFilteringFacadeXGraphBuilder f = new TripleFilteringFacadeXGraphBuilder("http://www.example.org/", bgp, getProperties());
		Node g = ResourceFactory.createResource("http://www.example.org/").asNode();
		//
        f.add(g,ResourceFactory.createResource().asNode(), property1, NodeFactory.createLiteral("Hello world"));
        Assert.assertTrue(f.getModel().size() == 0);
        f.add(g, NodeFactory.createBlankNode(), property2, NodeFactory.createLiteral("Hello world"));
        Assert.assertTrue(f.getModel().size() == 0);
        //
        f.add(g, resource1, property1, resource3); // OK
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(g, resource1, property1, resource2); // NOT OK
        Assert.assertTrue(f.getModel().size() == 1);

        f.add(g, resource2, property2, resource4); // OK
        Assert.assertTrue(f.getModel().size() == 2);
        f.add(g, resource2, property1, resource2); // NOT OK
        f.add(g, resource2, property1, resource3); // NOT OK
        f.add(g, resource2, property1, resource4); // NOT OK
        f.add(g, resource1, property2, resource4); // NOT OK
        Assert.assertTrue(f.getModel().size() == 2);
    }

    /**
     * BGP, 3, {R1 P1 R3, R2 P2 R4, [] ? []}
     */
    @Test
    public void bgp6(){
		Node property1 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/property1");
		Node property2 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/property2");
		Node resource1 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource1");
		Node resource2 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource2");
		Node resource3 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource3");
		Node resource4 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource4");

        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(resource1, property1, resource3));
        bgp.getPattern().add(new Triple(resource2, property2, resource4));
        bgp.getPattern().add(new Triple(Node_Variable.ANY, new Node_Variable("p"), Node_Variable.ANY));

        TripleFilteringFacadeXGraphBuilder f = new TripleFilteringFacadeXGraphBuilder("http://www.example.org/", bgp, getProperties());
		Node g = ResourceFactory.createResource("http://www.example.org/").asNode();
        //
        f.add(g, NodeFactory.createBlankNode(), property1, NodeFactory.createLiteral("Hello world"));
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(g, NodeFactory.createBlankNode(), property2, NodeFactory.createLiteral("Hello world"));
        Assert.assertTrue(f.getModel().size() == 2);
        //
        f.add(g, resource1, property1, resource3); // OK
        Assert.assertTrue(f.getModel().size() == 3);
        f.add(g, resource1, property1, resource2); // OK
        Assert.assertTrue(f.getModel().size() == 4);

        f.add(g, resource2, property2, resource4); // OK
        Assert.assertTrue(f.getModel().size() == 5);
        f.add(g, resource2, property1, resource2); // OK
        f.add(g, resource2, property1, resource3); // OK
        f.add(g, resource2, property1, resource4); // OK
        f.add(g, resource1, property2, resource4); // OK
        Assert.assertTrue(f.getModel().size() == 9);
    }

    /**
     * QP, 1, {? ? ? L}
     */
    @Test
    public void qp1(){
        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(new Node_Variable("a"), new Node_Variable("b"), ResourceFactory.createPlainLiteral("Hello world").asNode()));

        OpQuadPattern qp = new OpQuadPattern(new Node_Variable("g"), bgp.getPattern());

        TripleFilteringFacadeXGraphBuilder f = new TripleFilteringFacadeXGraphBuilder("http://www.example.org/", qp, getProperties());
		Node g = ResourceFactory.createResource("http://www.example.org/").asNode();
        f.add(g, NodeFactory.createBlankNode(), RDF.type.asNode(), ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(g, ResourceFactory.createResource().asNode(), RDF.type.asNode(), ResourceFactory.createPlainLiteral("Hello world not!").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
    }

    /**
     * QP, 1, {G1 {R1 P1 R3, R2 P2 R4, [] ? []}}
     */
    @Test
    public void qp2(){
		Node property1 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/property1");
		Node property2 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/property2");
		Node resource1 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource1");
		Node resource2 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource2");
		Node resource3 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource3");
		Node resource4 = NodeFactory.createURI("http://sparql.xyz/facade-x/data/resource4");

        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(resource1, property1, resource3));
        bgp.getPattern().add(new Triple(resource2, property2, resource4));
        bgp.getPattern().add(new Triple(Node_Variable.ANY, new Node_Variable("p"), Node_Variable.ANY));

        OpQuadPattern qp = new OpQuadPattern(NodeFactory.createURI("http://www.example.org/"), bgp.getPattern());

        TripleFilteringFacadeXGraphBuilder f = new TripleFilteringFacadeXGraphBuilder("http://www.example.org/", qp, getProperties());
		Node g = ResourceFactory.createResource("http://www.example.org/").asNode();
        //
        f.add(g, ResourceFactory.createResource().asNode(), property1, ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(g, ResourceFactory.createResource().asNode(), property2, ResourceFactory.createPlainLiteral("Hello world").asNode());
        Assert.assertTrue(f.getModel().size() == 2);
        //
        f.add(g, resource1, property1, resource3); // OK
        Assert.assertTrue(f.getModel().size() == 3);
        f.add(g, resource1, property1, resource2); // OK
        Assert.assertTrue(f.getModel().size() == 4);

        f.add(g, resource2, property2, resource4); // OK
        Assert.assertTrue(f.getModel().size() == 5);
        f.add(g, resource2, property1, resource2); // OK
        f.add(g, resource2, property1, resource3); // OK
        f.add(g, resource2, property1, resource4); // OK
        f.add(g, resource1, property2, resource4); // OK
        Assert.assertTrue(f.getModel().size() == 9);
    }
}
