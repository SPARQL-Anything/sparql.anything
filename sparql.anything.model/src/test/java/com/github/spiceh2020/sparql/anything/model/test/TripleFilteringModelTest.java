/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.spiceh2020.sparql.anything.model.test;

import com.github.spiceh2020.sparql.anything.model.TripleFilteringModel;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TripleFilteringModelTest {
    public static final Logger log = LoggerFactory.getLogger(TripleFilteringModelTest.class);

    @Test
    public void test(){
        OpBGP bgp = new OpBGP();
        bgp.getPattern().add(new Triple(new Node_Variable("a"), new Node_Variable("b"), ResourceFactory.createPlainLiteral("Hello world").asNode()));

        TripleFilteringModel f = new TripleFilteringModel(NodeFactory.createURI("http://www.example.org/"), bgp);
        f.add(ResourceFactory.createResource(), RDF.type, ResourceFactory.createPlainLiteral("Hello world"));
        Assert.assertTrue(f.getModel().size() == 1);
        f.add(ResourceFactory.createResource(), RDF.type, ResourceFactory.createPlainLiteral("Hello world not!"));
        Assert.assertTrue(f.getModel().size() == 1);
    }
}
