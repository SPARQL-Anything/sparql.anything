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

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SandboxTest {

    @Ignore
    @Test
    public void model(){
        Model m = ModelFactory.createDefaultModel();
        long start = System.nanoTime();
        for(int i = 1; i < 10000000; i ++){
            m.add(m.createResource(new AnonId(new Object().toString())), RDF.li(i), m.createLiteral(String.valueOf(i)));
        }
        long loaded = System.nanoTime();
        String query = "SELECT * WHERE {[] ?P []} ORDER BY DESC(1)";
        ResultSet rs = QueryExecutionFactory.create(query, m).execSelect();
        long queried = System.nanoTime();
        while(rs.hasNext()){
            rs.next();
        }
        long flushed = System.nanoTime();

        System.err.println("loaded in " + (loaded - start)/1_000_000_000 + " s");
        System.err.println("queried in " + (queried - loaded)/1_000_000_000 + " s");
        System.err.println("flushed in " + (flushed - queried)/1_000_000_000 + " s");

//        loaded in 2 s
//        queried in 0 s
//        flushed in 12 s
//        loaded in 46 s
//        queried in 0 s
//        flushed in 245 s
    }

    @Ignore
    @Test
    public void dataset(){
        Dataset d = DatasetFactory.create();
        Graph g = d.asDatasetGraph().getDefaultGraph();
        double start = System.nanoTime();
        for(int i = 1; i < 10000000; i ++){
            g.add(new Triple(NodeFactory.createBlankNode(new Object().toString()), RDF.li(i).asNode(), NodeFactory.createLiteral(String.valueOf(i))));
        }
        double loaded = System.nanoTime();
        String query = "SELECT * WHERE {[] ?P []} ORDER BY DESC(1)";
        ResultSet rs = QueryExecutionFactory.create(query, d).execSelect();
        double queried = System.nanoTime();

        while(rs.hasNext()){
            rs.next();
        }
        double flushed = System.nanoTime();

        System.err.println("loaded in " + (loaded - start)/1_000_000_000 + " s");
        System.err.println("queried in " + (queried - loaded)/1_000_000_000 + " s");
        System.err.println("flushed in " + (flushed - queried)/1_000_000_000 + " s");

//        loaded in 2.051866464 s
//        queried in 0.11462891 s
////        flushed in 12.635617477 s
//        loaded in 20.449738886 s
//        queried in 0.122078255 s
//        flushed in 169.618465255 s
    }

    @Test
    public void testrange(){
        Assert.assertTrue("1...10".matches("^[0-9]+\\.\\.\\.[0-9]+$"));
    }
}
