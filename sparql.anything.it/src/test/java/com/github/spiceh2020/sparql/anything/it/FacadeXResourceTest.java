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

package com.github.spiceh2020.sparql.anything.it;

import com.github.spiceh2020.sparql.anything.json.JSONStreamingTriplifier;
import com.github.spiceh2020.sparql.anything.model.BufferedTripleIterator;
import com.github.spiceh2020.sparql.anything.model.FacadeXDataSource;
import com.github.spiceh2020.sparql.anything.model.FacadeXResource;
import org.apache.jena.graph.*;
import org.apache.jena.query.ARQ;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.util.iterator.WrappedIterator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class FacadeXResourceTest {

    public static final Logger log = LoggerFactory.getLogger(FacadeXResourceTest.class);
    Properties p = null;
    OpBGP bgp = null;
    URL url = null;
    FacadeXResource dg = null;
    Node gn = null;
    FacadeXDataSource g = null;

    @Before
    public void before() throws IOException {
        p = new Properties();
        bgp = new OpBGP();
        bgp.getPattern().add(new Triple(new Node_Variable("a"),
                new Node_Variable("b"),
                new Node_Variable("c")));
        url = getClass().getClassLoader().getResource("./tate-gallery/a01009-14709.json");
        dg = new FacadeXResource(url, bgp, ARQ.getContext(), p, new JSONStreamingTriplifier());
        gn = dg.listGraphNodes().next();
        g = (FacadeXDataSource) dg.getGraph(gn);
    }

    @Test
    public void testGeneration() throws IOException {
        BufferedTripleIterator bti = (BufferedTripleIterator) g.find(NodeFactory.createVariable("s"), NodeFactory.createVariable("p"), NodeFactory.createVariable("o"));
        Assert.assertTrue(bti.getGeneration() == 0);
        BufferedTripleIterator bti2 = (BufferedTripleIterator) g.find(NodeFactory.createVariable("q"), NodeFactory.createVariable("l"), NodeFactory.createVariable("u"));
        Assert.assertTrue(bti2.getGeneration() == 0);
    }
}
