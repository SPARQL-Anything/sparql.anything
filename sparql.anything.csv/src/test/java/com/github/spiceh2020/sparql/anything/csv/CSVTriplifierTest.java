/*
 * Copyright (c) 2020 Enrico Daga @ http://www.enridaga.net
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

package com.github.spiceh2020.sparql.anything.csv;

import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class CSVTriplifierTest {
    private CSVTriplifier triplifier = new CSVTriplifier();
    @Test
    public void test() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("namespace", "http://www.example.org#");
//        properties.setProperty("uriRoot", "http://www.example.org#");
        URL csv1 = getClass().getClassLoader().getResource("./test1.csv");
        DatasetGraph graph = triplifier.triplify(csv1, properties);
        Iterator<Quad> iter = graph.find(null,null,null,null);
        while(iter.hasNext()){
            Quad t = iter.next();
            System.err.println(t);
        }
    }
}
