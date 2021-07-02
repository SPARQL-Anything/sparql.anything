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
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.TxnType;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.shared.Lock;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphWrapper;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.graph.GraphOps;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.UnionDatasetGraph;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.NiceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class FacadeXResource implements DatasetGraph {

    protected final Map<Node, FacadeXDataSource> dataSources;
    protected final Map<Integer, List<BufferedIterator>> iterators;
    protected final Context context;
    protected final String resourceId;
    private final PrefixMapping prefixMappings;
    protected final FacadeXGraphBuilder builder;
    protected StreamingTriplifier triplifier;
    protected final List<Node> dataSourcesList;
    private int generation = 0;
    private int runningGeneration = 0;
    private static final Logger log = LoggerFactory.getLogger(FacadeXResource.class);
    public FacadeXResource (URL url, Op operation, Context context, Properties properties, StreamingTriplifier triplifier) throws IOException {
        this.dataSources = new HashMap<Node,FacadeXDataSource>();
        this.context = context;
        this.resourceId = url.toString();
        this.builder = new TripleFilteringFacadeXBuilder(resourceId, operation, this, properties);
        this.prefixMappings = PrefixMapping.Factory.create(); // FIXME Not sure what to do with this or where to take the mappings in the sparql query.
        this.iterators = new HashMap<Integer,List<BufferedIterator>>();
        this.triplifier = triplifier;
        // Always call setup first
        this.triplifier.setup(url, properties, builder);
        this.dataSourcesList = new ArrayList<>();
        for(String ds: triplifier.getDataSourcesIds()){
            log.trace("Data source: {}", ds);
            addFacadeXDataSource(ds);
        }
    }

//    public FacadeXGraphBuilder getBuilder(){
//        return this.builder;
//    }
    private boolean streamInactive = true;

    private class BufferedIterator extends NiceIterator<Triple> {
        LinkedList<Triple> buffer = new LinkedList<Triple>();
        boolean isWaiting = false;
        private Node g;
        private Node s;
        private Node p;
        private Node o;
        boolean isCompleted = false;
        private int myGeneration;
        BufferedIterator(int generation, Node graph, Node subject, Node predicate, Node object){
            this.myGeneration = generation;
            this.g = graph;
            this.s = subject;
            this.p = predicate;
            this.o = object;
        }

        boolean about(Node graph, Node subject, Node predicate, Node object){
            return this.g.equals(graph) && this.s.equals(subject) && this.p.equals(predicate) && this.o.equals(object);
        }

        /**
         *
         * @param graph
         * @param subject
         * @param predicate
         * @param object
         * @return Returns true if the iterator was not waiting or the triple does not match the iterator pattern. False otherwise
         */
        public boolean contribute(Node graph, Node subject, Node predicate, Node object){
            if((!g.isConcrete() || g.matches(graph))
                    && (!s.isConcrete() || s.matches(subject))
                    && (!p.isConcrete() || p.matches(predicate))
                    && (!o.isConcrete() || o.matches(object))){
                buffer.add(new Triple(subject, predicate, object));
                if(isWaiting){
                    isWaiting = false;
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean hasNext() {
            if(isCompleted){
                log.trace("BufferedIterator is completed");
                return false;
            }
            log.trace("Data is in buffer: {}", !buffer.isEmpty());
            if(!buffer.isEmpty()){
                isWaiting = false;
                return true;
            }
            isWaiting = true;

            if(runningGeneration != myGeneration){
                log.trace("Calling hasNext on generation {} but running generation is {}", myGeneration, runningGeneration);
                try {
                    FacadeXResource.this.triplifier.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                FacadeXResource.this.streamInactive = true;
                runningGeneration += 1;
                // throw new RuntimeException("This cannot be done " + runningGeneration + " " + myGeneration);
            }
            try {

                while(buffer.isEmpty()){
                    log.trace("Buffer is empty, streaming for new results");
                    FacadeXResource.this.streamInactive = false;
                    if( FacadeXResource.this.triplifier.stream() == false){
                        log.trace("Streaming complete {}", this.hashCode());
                        FacadeXResource.this.streamInactive = true;
                        isCompleted = true;
                        // Remove this from the stack of iterators?
                        break;
                    }
                }
                return !buffer.isEmpty();
            } catch (IOException e) {
                log.error("Exception occurred while streaming", e);
                return false;
            }
        }

        public Triple nextTriple(){
            if(!buffer.isEmpty()){
                Triple toreturn = buffer.poll();
                log.trace("Returning triple: {}", toreturn);
                return toreturn;
            }
            if(isWaiting){
                throw new IllegalStateException();
            }
            throw new NoSuchElementException("Buffer is empty");
        }

        @Override
        public Triple next() {
            return nextTriple();
        }
    }

    protected void addFacadeXDataSource(String dataSourceId) {

        final Node graph = NodeFactory.createURI(dataSourceId);
        this.dataSourcesList.add(graph);
        this.dataSources.put(graph, new FacadeXDataSource(this.prefixMappings) {
            @Override
            protected ExtendedIterator<Triple> findInDataSource(Node node, Node node1, Node node2) {
                log.trace("findInDataSource: {} {} {}", node, node1, node2);
                log.trace("generation {} running {}", generation, runningGeneration);
                if(log.isInfoEnabled() ){
                    log.info("Allocating generation {} includes {} iterators", generation, (iterators.containsKey(generation)) ? iterators.get(generation).size() : 0);
                    log.info("Running generation {} includes {} iterators", runningGeneration, (iterators.containsKey(runningGeneration)) ? iterators.get(runningGeneration).size() : 0);
                }
                if(FacadeXResource.this.streamInactive != true){
                    // Allocate iterator to next generation
                    generation += 1;
                    log.trace("Allocate iterator to generation {} (running {})", generation, runningGeneration);
                    // throw new RuntimeException("Allocating iterators while streaming!");
                }

                // Looking for iterators already returned, if found, return same iterator
                // Check current iterators, if they are all completed, reset data source
                boolean allCompleted = true;
                if(FacadeXResource.this.iterators.containsKey(runningGeneration)) {
                    log.trace("Inspetting running generation {}", runningGeneration);
                    for (BufferedIterator i : FacadeXResource.this.iterators.get(runningGeneration)) {
                        if (!i.isCompleted) {
                            allCompleted = false;
                        }
                        if (!i.isCompleted && i.about(graph, node, node1, node2)) {
                            log.trace("Found iterator: {} {} {}", node, node1, node2);
                            return i;
                        }
                    }

                    if(allCompleted){
                        log.trace("Generation {} completed", runningGeneration);
                        log.trace("Resetting stream");
                        try {
                            FacadeXResource.this.triplifier.reset();
                        }catch(IOException ex){
                            throw new RuntimeException(ex);
                        }
                    } else {
                        log.trace("Generation {} not completed", runningGeneration);
                        // Flush generation
                        try {
                            log.warn("Flushing generation");
                            FacadeXResource.this.triplifier.flush();
                            FacadeXResource.this.streamInactive = true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Previous generation data should be all in the buffers at this point
                    // Moving to next generation
                    runningGeneration += 1;
                    generation += 1;
                    log.trace("Moving to generation {}", runningGeneration);
                }
                log.trace("generation {} running {}", generation, runningGeneration);
                BufferedIterator bufi = new BufferedIterator(generation, graph, node, node1, node2);
                log.trace("adding iterator: {} (generation: {})", bufi, generation);
                log.trace(" - {} - {} {} {}", new Object[]{graph, node, node1, node2});
                if(!FacadeXResource.this.iterators.containsKey(generation)){
                    FacadeXResource.this.iterators.put(generation, new ArrayList<BufferedIterator>());
                }
                FacadeXResource.this.iterators.get(generation).add(bufi);
                return bufi;
            }

            @Override
            public void add(Triple triple) throws AddDeniedException {
                log.trace("Adding triple to iterators in generation {}", runningGeneration );
                // Find iterator and
                for(BufferedIterator bufi: iterators.get(runningGeneration)){
                    if(!bufi.isCompleted){
                        log.trace("contributing triple to iterator: {} {}", triple, bufi);
                        bufi.contribute(graph, triple.getSubject(), triple.getPredicate(), triple.getObject());
                    }
                }
            }
        });
    }

    // DATASETGRAPH
    @Override
    public Graph getDefaultGraph() {
        return getUnionGraph();
    }

    @Override
    public Graph getGraph(Node node) {
        log.trace("getGraph {}", node);
        return dataSources.get(node);
    }

    @Override
    public Graph getUnionGraph() {
        return GraphOps.unionGraph(this);
    }

    @Override
    public boolean containsGraph(Node node) {
        return dataSources.containsKey(node);
    }

    @Override
    public void setDefaultGraph(Graph graph) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addGraph(Node node, Graph graph) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGraph(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Node> listGraphNodes() {
        return dataSources.keySet().iterator();
    }

    @Override
    public void add(Quad quad) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Quad quad) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Node node, Node node1, Node node2, Node node3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Node node, Node node1, Node node2, Node node3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAny(Node node, Node node1, Node node2, Node node3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Quad> find() {
        return find(Node_ANY.ANY, Node_ANY.ANY, Node_ANY.ANY, Node_ANY.ANY);
    }

    @Override
    public Iterator<Quad> find(Quad quad) {
        return find(quad.getGraph(),quad.getSubject(), quad.getPredicate(), quad.getObject());
    }

    @Override
    public Iterator<Quad> find(Node node, Node node1, Node node2, Node node3) {
        // not sure how to handle the default graph
        return findNG(node,node1,node2,node3);
    }

    @Override
    public Iterator<Quad> findNG(Node node, Node node1, Node node2, Node node3) {
        return new Iterator<Quad>() {
            int position = 0;
            Node g = null;
            Iterator<Triple> it = null;
            @Override
            public boolean hasNext() {
                System.out.println(".");
                if(it == null ){
                    log.trace("Allocating iterators for data source " + position);
                    g = dataSourcesList.get(position);
                    it = dataSources.get(g).findInDataSource(node1, node2, node3);
                    position += 1;
                }
                if(!it.hasNext() && dataSourcesList.size() > position){
                    log.trace("Move to next data source, if any " + position);
                    // Move to the next iterator, if any
                    g = dataSourcesList.get(position);
                    it = dataSources.get(g).findInDataSource(node1, node2, node3);
                    position += 1;
                }else{
                    log.trace("No more data from data source " + position);
                    // No more data
                    return false;
                }
                return it.hasNext();
            }

            @Override
            public Quad next() {
                return new Quad(g, it.next());
            }
        };
    }

    @Override
    public boolean contains(Node node, Node node1, Node node2, Node node3) {
        return find( node, node1, node2, node3).hasNext();
    }

    @Override
    public boolean contains(Quad quad) {
        return find(quad).hasNext();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return false; // there is always one graph with one root!
    }

    @Override
    public Lock getLock() {
        // this is not transactional
        throw new UnsupportedOperationException();
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public long size() {
        Iterator<Node> g = listGraphNodes();
        int c =0;
        while(g.hasNext()){
            g.next();
            c++;
        }
        return c;
    }

    @Override
    public void close() {
        // Ignored
    }

    @Override
    public boolean supportsTransactions() {
        return false;
    }

    @Override
    public void begin(TxnType txnType) {
        // Ignored
    }

    @Override
    public void begin(ReadWrite readWrite) {
        // Ignored
    }

    @Override
    public boolean promote(Promote promote) {
        return false;
    }

    @Override
    public void commit() {
        // Ignored
    }

    @Override
    public void abort() {
        // Ignored
    }

    @Override
    public void end() {
        // Ignored
    }

    @Override
    public ReadWrite transactionMode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TxnType transactionType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInTransaction() {
        return false;
    }
}
