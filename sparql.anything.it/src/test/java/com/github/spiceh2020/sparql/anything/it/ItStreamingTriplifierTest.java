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

import com.github.spiceh2020.sparql.anything.engine.FacadeX;
import com.github.spiceh2020.sparql.anything.engine.FacadeXOpExecutor;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItStreamingTriplifierTest {

    public static final Logger log = LoggerFactory.getLogger(ItStreamingTriplifierTest.class);
    @Before
    public void before(){
        ARQ.getContext().set(FacadeXOpExecutor.strategy, 2);
    }

    @After
    public void after(){
        ARQ.getContext().unset(FacadeXOpExecutor.strategy);
    }

    @Test
    public void JSON1() throws URISyntaxException {
        // a01009-14709.json
        Dataset kb = DatasetFactory.createGeneral();

        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

        String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
        Query query = QueryFactory.create(
                "SELECT DISTINCT ?p WHERE { SERVICE <x-sparql-anything:namespace=http://www.example.org#,location="
                        + location + "> {graph ?g {?s ?p ?o }}} order by ?p");

        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        List<String> mustInclude = new ArrayList<String>(
                Arrays.asList(new String[] { "http://www.example.org#thumbnailUrl", "http://www.example.org#title",
                        "http://www.w3.org/1999/02/22-rdf-syntax-ns#_1", "http://www.example.org#text",
                        "http://www.example.org#subjects", "http://www.example.org#subjectCount" }));
        while (rs.hasNext()) {
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            log.info("{} {}", rowId, qs.get("p").toString());
            mustInclude.remove(qs.get("p").toString());
        }
        Assert.assertTrue(mustInclude.isEmpty());

    }


    @Test (expected = UnsupportedOperationException.class)
    public void Audit_JSON2() throws URISyntaxException {
        // a01009-14709.json
        Dataset kb = DatasetFactory.createGeneral();

        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
        Query query = QueryFactory.create(
                "PREFIX xyz:  <http://sparql.xyz/facade-x/data/>\n" +
                        "PREFIX fx:   <http://sparql.xyz/facade-x/ns/>\n" +
                        "PREFIX sd:   <http://www.w3.org/ns/sparql-service-description#>\n" +
                        "PREFIX void: <http://rdfs.org/ns/void#>\n" +
                        "SELECT DISTINCT ?g ?triples WHERE { " +
                        "{" +
                        "SERVICE <x-sparql-anything:namespace=http://www.example.org#,audit=1,location="
                        + location + "> { graph ?g {[] ?p [] } . graph xyz:audit { ?g void:triples ?triples } } " +
                        "} UNION {" +
                        "SERVICE <x-sparql-anything:namespace=http://www.example.org#,audit=1,location="
                        + location + "> { graph ?g {[] a [] } . graph xyz:audit { ?g void:triples ?triples } } " +
                        "" +
                        "}}");

        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        // 151
        int count = 0;
        while (rs.hasNext()) {
            count++;
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            log.info("{} {}", rowId, qs);
            Assert.assertTrue(qs.get("triples").asLiteral().getInt() == 1 || qs.get("triples").asLiteral().getInt() == 151);
        }
        Assert.assertTrue(count == 2);
    }

    @Test
    public void JSONLeftJoin() throws URISyntaxException {
        // a01009-14709.json
        Dataset kb = DatasetFactory.createGeneral();

        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

        String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
        Query query = QueryFactory.create(
                "\n" +
                        "PREFIX xyz: <http://sparql.xyz/facade-x/data/>" +
                        "\n SELECT ?s ?acno  WHERE { SERVICE <x-sparql-anything:location="
                + location + "> {graph ?g {?s xyz:acno ?acno . ?s xyz:contributorCount [] }}}");
        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        boolean hasResults = false;
        while (rs.hasNext()) {
            hasResults = true;
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            log.info("{} {} {}", rowId, qs.get("s").toString(), qs.get("acno").toString());
            Assert.assertTrue(rowId == 1);
        }
        Assert.assertTrue(hasResults);
    }

    @Test
    public void JSONSamePatternTwice() throws URISyntaxException {
        // a01009-14709.json
        Dataset kb = DatasetFactory.createGeneral();

        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

        String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
        Query query = QueryFactory.create("PREFIX xyz: <http://sparql.xyz/facade-x/data/>" +
                        "\n SELECT ?s ?acno ?a ?acno2  WHERE { SERVICE <x-sparql-anything:location="
                        + location + "> {graph ?g {?s xyz:acno ?acno . ?a xyz:acno ?acno2}}}");
        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        boolean hasResults = false;
        while (rs.hasNext()) {
            hasResults = true;
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            log.info("{} {} {} {}", rowId, qs.get("s").toString(), qs.get("acno").toString(), qs.get("acno2").toString());
            Assert.assertTrue(rowId == 1);
        }
        Assert.assertTrue(hasResults);
    }

    @Test
    public void JSONRightJoin() throws URISyntaxException {
        // a01009-14709.json
        Dataset kb = DatasetFactory.createGeneral();

        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

        String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
        Query query = QueryFactory.create(
                "\n" +
                        "PREFIX xyz: <http://sparql.xyz/facade-x/data/>" +
                        "\n SELECT ?s1 ?s2  WHERE { SERVICE <x-sparql-anything:location="
                        + location + "> {graph ?g {?s1 xyz:acno ?acno . ?s2 xyz:acno ?acno }}}");
        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        boolean hasResults = false;
        while (rs.hasNext()) {
            hasResults = true;
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            //log.info("{} {} {}", rowId, qs.get("s").toString(), qs.get("s1").toString());
            Assert.assertTrue(qs.get("s1").equals(qs.get("s2")));
            Assert.assertTrue(rowId == 1);
        }
        Assert.assertTrue(hasResults);
    }


    @Test
    public void JSONJoinInAnonNode() throws URISyntaxException {
        // a01009-14709.json
        Dataset kb = DatasetFactory.createGeneral();

        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

        String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
        Query query = QueryFactory.create(
                "\n" +
                        "PREFIX xyz: <http://sparql.xyz/facade-x/data/>" +
                        "\n SELECT ?id  WHERE { SERVICE <x-sparql-anything:location="
                        + location + "> {graph ?g { ?bob  xyz:id ?id . ?bob xyz:children [] }}}");
        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        boolean hasResults = false;
        while (rs.hasNext()) {
            hasResults = true;
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            log.info("{} {}", rowId, qs.get("id").toString());
//            Assert.assertTrue(rowId == 1);
        }
        Assert.assertTrue(hasResults);
    }
}
