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

package com.github.spiceh2020.sparql.anything.json.test;

import com.github.spiceh2020.sparql.anything.json.JSONStreamingTriplifier;
import com.github.spiceh2020.sparql.anything.json.JSONTriplifier;
import com.github.spiceh2020.sparql.anything.model.FacadeXResource;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.ARQ;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class JSONStreamingTriplifierTest {

    public static final Logger log = LoggerFactory.getLogger(JSONStreamingTriplifierTest.class);

    @Test
    public void friendsSinglePattern() throws IOException {


        Properties properties = new Properties();
        OpBGP bgp = null;
//        bgp.getPattern().add(new Triple(new Node_Variable("a"), new Node_Variable("b"), ResourceFactory.createPlainLiteral("Romance").asNode()));

//        g1 = jt.triplify(getClass().getClassLoader().getResource("./friends.json"), properties, bgp);
        FacadeXResource fxr = new FacadeXResource(getClass().getClassLoader().getResource("./friends.json"), bgp, ARQ.getContext(), properties, new JSONStreamingTriplifier());
        DatasetGraph g1 = fxr;
        // Only two triples matching the BGP
//        log.info("Size is: {}", g1.getDefaultGraph().size());
        Iterator quads = g1.find();
        while(quads.hasNext()){
            Quad q = (Quad) quads.next();
            log.info("{} {} {}", q.getSubject(), q.getPredicate(), q.getObject());
        }
//        assertTrue(g1.getDefaultGraph().size() == 2);
    }

}
