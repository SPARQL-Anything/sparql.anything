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

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;

import java.net.URI;

public interface FacadeXGraphBuilder {
    boolean add(Node subject, Node predicate, Node object);

    boolean add(Node graph, Node subject, Node predicate, Node object);

    boolean addContainer(String dataSourceId, String containerId, String slotKey, String childContainerId);

	boolean addContainer(String dataSourceId, String containerId, URI customKey, String childContainerId);

    boolean addContainer(String dataSourceId, String containerId, Integer slotKey, String childContainerId);

	boolean addType(String dataSourceId, String containerId, String typeId);

	boolean addType(String dataSourceId, String containerId, URI type);

	boolean addValue(String dataSourceId, String containerId, String slotKey, Object value);

	boolean addValue(String dataSourceId, String containerId, URI customKey, Object value);

    boolean addValue(String dataSourceId, String containerId, Integer slotKey, Object value);

    boolean addRoot(String dataSourceId, String rootId);

    Node container2node(String container);

    Node key2predicate(String key);

    Node value2node(Object value);

    DatasetGraph getDatasetGraph();

    Node getMainGraphName();

    Graph getMainGraph();
}
