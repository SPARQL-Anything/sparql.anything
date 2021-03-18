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
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class TripleFilteringModel {
    private final Properties properties;
    private final Op op;
    private final List<Object> opComponents = new ArrayList<Object>();
    private final Node mainGraphName;
    private final DatasetGraph datasetGraph;
    private static final Logger log = LoggerFactory.getLogger(TripleFilteringModel.class);

    //
    private final boolean p_blank_nodes;
    private final String p_namespace;
    private final String p_root;

    TripleFilteringModel(String resourceId, Op op, DatasetGraph ds, Properties properties){
        this.properties = properties;
        this.op = op;
        if(op != null) {
            ComponentsCollector collector = new ComponentsCollector();
            op.visit(collector);
        }
        this.datasetGraph = ds;
        this.mainGraphName = NodeFactory.createURI(resourceId);
        //
        this.p_blank_nodes = Triplifier.getBlankNodeArgument(properties);
        this.p_root = Triplifier.getRootArgument(properties, resourceId);
        this.p_namespace = Triplifier.getNamespaceArgument(properties);
    }


    public TripleFilteringModel(String resourceId, Op op, Properties properties){
        this(resourceId, op, DatasetGraphFactory.createTxnMem(), properties);
    }

    public TripleFilteringModel(URL location, Op op, Properties properties){
        this(location.toString(), op, properties);
    }

    public boolean match(Node graph, Node subject, Node predicate, Node object){
        if(op == null || opComponents.isEmpty()) return true;

        for (Object o : opComponents){

            if (o instanceof Quad){
                Quad q = (Quad) o;
                if((!q.getGraph().isConcrete() || q.getGraph().matches(graph))
                        && (!q.getSubject().isConcrete() || q.getSubject().matches(subject))
                        && (!q.getPredicate().isConcrete() || q.getPredicate().matches(predicate))
                        && (!q.getObject().isConcrete() || q.getObject().matches(object))){
                    return true;
                }
            } else if(o instanceof Triple){
                Triple t = (Triple) o;
                if((!t.getSubject().isConcrete() || t.getSubject().matches(subject))
                   && (!t.getPredicate().isConcrete() || t.getPredicate().matches(predicate))
                   && (!t.getObject().isConcrete() || t.getObject().matches(object))){
                    return true;
                }
            }
        }
        return false;
    }

    @Deprecated
    public void add(Resource subject, Property predicate, RDFNode object){
        if(match(mainGraphName, subject.asNode(), predicate.asNode(), object.asNode())){
            datasetGraph.getGraph(mainGraphName).add(new Triple(subject.asNode(), predicate.asNode(), object.asNode()));
        }
    }

    /**
     * Triples are added to the main data source / graph
     * Triplifiers generating multiple data sources / graphs, should use add(Node g, Node s, Node p, Node o) instead
     */
    public boolean add(Node subject, Node predicate, Node object){
        if(match(mainGraphName, subject, predicate, object)){
            datasetGraph.getGraph(mainGraphName).add(new Triple(subject, predicate, object));
            return true;
        }
        return false;
    }

    public boolean add(Node graph, Node subject, Node predicate, Node object){
        if(match(graph, subject, predicate, object)){
            datasetGraph.getGraph(graph).add(new Triple(subject, predicate, object));
            return true;
        }
        return false;
    }

    public boolean addContainer(String dataSourceId, String containerId, String slotKey, String childContainerId){
        return add(container2node(dataSourceId), container2node(containerId), key2predicate(slotKey), container2node(childContainerId));
    }

    public boolean addContainer(String dataSourceId, String containerId, Integer slotKey, String childContainerId){
        return add(container2node(dataSourceId), container2node(containerId), RDF.li(slotKey).asNode(), container2node(childContainerId));
    }

    public boolean addValue(String dataSourceId, String containerId, String slotKey, Object value){
        return add(container2node(dataSourceId), container2node(containerId), key2predicate(slotKey), value2node(value));
    }

    public boolean addValue(String dataSourceId, String containerId, Integer slotKey, Object value){
        return add(container2node(dataSourceId), container2node(containerId), RDF.li(slotKey).asNode(), value2node(value));
    }

    public boolean addRoot(String dataSourceId, String rootId){
        return add(container2node(dataSourceId), container2node(rootId), RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
    }

    /**
     *
     */
    public Node container2node(String container){
        if (p_blank_nodes) {
            return NodeFactory.createBlankNode(container);
        } else {
            return NodeFactory.createURI(container);
        }
    }

    public Node key2predicate(String key){
        return NodeFactory.createURI(this.p_namespace + key);
    }

    public Node value2node(Object value){
        return ResourceFactory.createTypedLiteral(value).asNode();
    }

    /**
     * This includes triples from the default graph / union of all graphs.
     * @return
     */
    public Model getModel(){
        return ModelFactory.createModelForGraph(getDatasetGraph().getUnionGraph());
    }

    public DatasetGraph getDatasetGraph(){
        datasetGraph.setDefaultGraph(datasetGraph.getUnionGraph());
        return datasetGraph;
    }

    /**
     * The main graph is created when adding triples instead of quads.
     * The main graph uses the resourceId as data source identifier / graph name
     *
     * @return
     */
    public Node getMainGraphName(){
        return mainGraphName;
    }

    public Graph getMainGraph(){
        return datasetGraph.getGraph(mainGraphName);
    }

    class ComponentsCollector implements OpVisitor{
        @Override
        public void visit(OpBGP opBGP) {
            log.trace(" - OpBGP - ", opBGP);
            opComponents.addAll(opBGP.getPattern().getList());
        }

        @Override
        public void visit(OpQuadPattern opQuadPattern) {
            log.trace(" - OpQuadPattern - ", opQuadPattern);
            opComponents.addAll(opQuadPattern.getPattern().getList());
        }

        @Override
        public void visit(OpQuadBlock opQuadBlock) {
            log.trace(" - OpQuadBlock - ", opQuadBlock);
            opComponents.addAll(opQuadBlock.getPattern().getList());
        }

        @Override
        public void visit(OpTriple opTriple) {
            log.trace(" - OpBGP - ", opTriple);
            opComponents.add(opTriple.getTriple());
        }

        @Override
        public void visit(OpQuad opQuad) {
            log.trace(" - OpQuad - ", opQuad);
            opComponents.add(opQuad.getQuad());
        }

        @Override
        public void visit(OpPath opPath) {

        }

        @Override
        public void visit(OpFind opFind) {

        }

        @Override
        public void visit(OpTable opTable) {

        }

        @Override
        public void visit(OpNull opNull) {

        }

        @Override
        public void visit(OpProcedure opProcedure) {
            opProcedure.getSubOp().visit(this);
        }

        @Override
        public void visit(OpPropFunc opPropFunc) {
            opPropFunc.getSubOp().visit(this);
        }

        @Override
        public void visit(OpFilter opFilter) {
            opFilter.getSubOp().visit(this);
        }

        @Override
        public void visit(OpGraph opGraph) {
            log.trace(" - OpGraph - ", opGraph);
            opGraph.getSubOp().visit(this);
        }

        @Override
        public void visit(OpService opService) {
            opService.getSubOp().visit(this);
        }

        @Override
        public void visit(OpDatasetNames opDatasetNames) {

        }

        @Override
        public void visit(OpLabel opLabel) {

        }

        @Override
        public void visit(OpAssign opAssign) {

        }

        @Override
        public void visit(OpExtend opExtend) {

        }

        @Override
        public void visit(OpJoin opJoin) {
            opJoin.getLeft().visit(this);
            opJoin.getRight().visit(this);
        }

        @Override
        public void visit(OpLeftJoin opLeftJoin) {
            opLeftJoin.getLeft().visit(this);
            opLeftJoin.getRight().visit(this);
        }

        @Override
        public void visit(OpUnion opUnion) {
            opUnion.getLeft().visit(this);
            opUnion.getRight().visit(this);
        }

        @Override
        public void visit(OpDiff opDiff) {
            opDiff.getLeft().visit(this);
            opDiff.getRight().visit(this);
        }

        @Override
        public void visit(OpMinus opMinus) {
            opMinus.getLeft().visit(this);
            opMinus.getRight().visit(this);
        }

        @Override
        public void visit(OpConditional opConditional) {
            opConditional.getLeft().visit(this);
            opConditional.getRight().visit(this);
        }

        @Override
        public void visit(OpSequence opSequence) {
            for(Op o: opSequence.getElements()){
                o.visit(this);
            }
        }

        @Override
        public void visit(OpDisjunction opDisjunction) {
            for(Op o: opDisjunction.getElements()){
                o.visit(this);
            }
        }

        @Override
        public void visit(OpList opList) {
            opList.getSubOp().visit(this);
        }

        @Override
        public void visit(OpOrder opOrder) {
            opOrder.getSubOp().visit(this);
        }

        @Override
        public void visit(OpProject opProject) {
            opProject.getSubOp().visit(this);
        }

        @Override
        public void visit(OpReduced opReduced) {
            opReduced.getSubOp().visit(this);
        }

        @Override
        public void visit(OpDistinct opDistinct) {
            opDistinct.getSubOp().visit(this);
        }

        @Override
        public void visit(OpSlice opSlice) {
            opSlice.getSubOp().visit(this);
        }

        @Override
        public void visit(OpGroup opGroup) {
            opGroup.getSubOp().visit(this);
        }

        @Override
        public void visit(OpTopN opTopN) {

        }
    }
}
