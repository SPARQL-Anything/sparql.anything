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

package com.github.spiceh2020.sparql.anything.model;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.List;

public interface BufferedTripleIterator extends ExtendedIterator<Triple> {

    boolean about(Node graph, Node subject, Node predicate, Node object);

    /**
     * @param graph
     * @param subject
     * @param predicate
     * @param object
     * @return Returns true if the iterator was not waiting or the triple does not match the iterator pattern. False otherwise
     */
    boolean contribute(Node graph, Node subject, Node predicate, Node object);

    Triple nextTriple();

    @Override
    Triple next();

    boolean isWaiting();

    Node getG();

    Node getS();

    Node getP();

    Node getO();

    boolean isCompleted();

    int getGeneration();

    List<Triple> inspectBuffer();
}
