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

import org.apache.jena.graph.*;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.shared.DeleteDeniedException;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.util.iterator.ExtendedIterator;

abstract class FacadeXDataSource implements Graph {

    private final PrefixMapping prefixMappings;

    FacadeXDataSource(PrefixMapping prefixMappings){
        this.prefixMappings = prefixMappings;
    }

    @Override
    public boolean dependsOn(Graph graph) {
        return false;
    }

    @Override
    public TransactionHandler getTransactionHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Capabilities getCapabilities() {
        return new Capabilities() {
            @Override
            public boolean sizeAccurate() {
                return false;
            }

            @Override
            public boolean addAllowed() {
                return false;
            }

            @Override
            public boolean deleteAllowed() {
                return false;
            }

            @Override
            public boolean addAllowed(boolean b) {
                return false;
            }

            @Override
            public boolean deleteAllowed(boolean b) {
                return false;
            }

            @Override
            public boolean iteratorRemoveAllowed() {
                return false;
            }

            @Override
            public boolean canBeEmpty() {
                return true;
            }

            @Override
            public boolean findContractSafe() {
                return false;
            }

            @Override
            public boolean handlesLiteralTyping() {
                return true;
            }
        };
    }

    @Override
    public GraphEventManager getEventManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphStatisticsHandler getStatisticsHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrefixMapping getPrefixMapping() {
        return prefixMappings;
    }

    @Override
    public void add(Triple triple) throws AddDeniedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Triple triple) throws DeleteDeniedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExtendedIterator<Triple> find(Triple triple) {
        return find(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    @Override
    public ExtendedIterator<Triple> find(Node node, Node node1, Node node2) {
        return findInDataSource(node, node1, node2);
    }

    protected abstract ExtendedIterator<Triple> findInDataSource(Node node, Node node1, Node node2);

    @Override
    public boolean isIsomorphicWith(Graph graph) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Node node, Node node1, Node node2) {
        return findInDataSource( node,  node1,  node2).hasNext();
    }

    @Override
    public boolean contains(Triple triple) {
        return findInDataSource(triple.getSubject(), triple.getPredicate(), triple.getObject()).hasNext();
    }

    @Override
    public void clear() {

    }

    @Override
    public void remove(Node node, Node node1, Node node2) {

    }

    @Override
    public void close() {
        // Ignore
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        // TODO do this only 1 time for static files and cache result
        // Find and count
        int c = 0;
        ExtendedIterator<Triple> t = findInDataSource(Node_ANY.ANY, Node_ANY.ANY, Node_ANY.ANY);
        while(t.hasNext()){
            t.next();
            c++;
        }
        return c;
    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
