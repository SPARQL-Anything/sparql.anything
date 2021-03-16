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
import org.apache.jena.iri.IRI;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 */
public class TripleFilteringModel {
    private Op op;
    private List<Object> opComponents = new ArrayList<Object>();
    private Node graph;
    private Model model;


    public TripleFilteringModel(Node graph, Op op){
        this(graph, op, ModelFactory.createDefaultModel());
    }

    public TripleFilteringModel(Node graph, Op op, Model model){
        this.op = op;
        if(op != null) {
            ComponentsCollector collector = new ComponentsCollector();
            op.visit(collector);
        }
        this.model = model;
        this.graph = graph;
    }

    public boolean match(Node subject, Node predicate, Node object){
        if(op == null) return true;

        for (Object o : opComponents){
            if (o instanceof Quad){
                Quad q = (Quad) o;
                if((!q.getGraph().isConcrete() || q.getGraph().matches(graph))
                        &&(!q.getSubject().isConcrete() || q.getSubject().matches(subject))
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

    public void add(Resource subject, Property predicate, RDFNode object){
        if(match(subject.asNode(), predicate.asNode(), object.asNode())){
            model.add(subject, predicate, object);
        }
    }

    public Model getModel(){
        return model;
    }

    class ComponentsCollector implements OpVisitor{
        @Override
        public void visit(OpBGP opBGP) {
            opComponents.addAll(opBGP.getPattern().getList());
        }

        @Override
        public void visit(OpQuadPattern opQuadPattern) {
            opComponents.addAll(opQuadPattern.getPattern().getList());
        }

        @Override
        public void visit(OpQuadBlock opQuadBlock) {

        }

        @Override
        public void visit(OpTriple opTriple) {

        }

        @Override
        public void visit(OpQuad opQuad) {

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

        }

        @Override
        public void visit(OpPropFunc opPropFunc) {

        }

        @Override
        public void visit(OpFilter opFilter) {

        }

        @Override
        public void visit(OpGraph opGraph) {

        }

        @Override
        public void visit(OpService opService) {

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

        }

        @Override
        public void visit(OpLeftJoin opLeftJoin) {

        }

        @Override
        public void visit(OpUnion opUnion) {

        }

        @Override
        public void visit(OpDiff opDiff) {

        }

        @Override
        public void visit(OpMinus opMinus) {

        }

        @Override
        public void visit(OpConditional opConditional) {

        }

        @Override
        public void visit(OpSequence opSequence) {

        }

        @Override
        public void visit(OpDisjunction opDisjunction) {

        }

        @Override
        public void visit(OpList opList) {

        }

        @Override
        public void visit(OpOrder opOrder) {

        }

        @Override
        public void visit(OpProject opProject) {

        }

        @Override
        public void visit(OpReduced opReduced) {

        }

        @Override
        public void visit(OpDistinct opDistinct) {

        }

        @Override
        public void visit(OpSlice opSlice) {

        }

        @Override
        public void visit(OpGroup opGroup) {

        }

        @Override
        public void visit(OpTopN opTopN) {

        }
    }
}
