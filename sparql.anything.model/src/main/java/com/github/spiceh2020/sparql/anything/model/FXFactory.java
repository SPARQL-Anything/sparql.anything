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
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;

import java.util.Properties;

public class FXFactory {

    private final Properties properties;
    private final boolean p_blank_nodes;
    private final String p_namespace;
    private final String p_root;

    public FXFactory(String resourceId, Properties properties){
        this.properties = properties;
        this.p_blank_nodes = Triplifier.getBlankNodeArgument(properties);
        this.p_root = Triplifier.getRootArgument(properties, resourceId);
        this.p_namespace = Triplifier.getNamespaceArgument(properties);
    }

    public Quad getContainerStatement(String dataSourceId, String containerId, String slotKey, String childContainerId){
        return new Quad(NodeFactory.createURI(dataSourceId), container2node(containerId), key2predicate(slotKey), container2node(childContainerId));
    }

    public Quad getContainerStatement(String dataSourceId, String containerId, Integer slotKey, String childContainerId){
        return new Quad(NodeFactory.createURI(dataSourceId), container2node(containerId), RDF.li(slotKey).asNode(), container2node(childContainerId));
    }

    public Quad getValueStatement(String dataSourceId, String containerId, String slotKey, Object value){
        return new Quad(NodeFactory.createURI(dataSourceId), container2node(containerId), key2predicate(slotKey), value2node(value));
    }

    public Quad getValueStatement(String dataSourceId, String containerId, Integer slotKey, Object value){
        return new Quad(NodeFactory.createURI(dataSourceId), container2node(containerId), RDF.li(slotKey).asNode(), value2node(value));
    }

    public Quad getRootStatement(String dataSourceId, String rootId){
        return new Quad(NodeFactory.createURI(dataSourceId), container2node(rootId), RDF.type.asNode(), NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT));
    }

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

    public Object predicate2key(Node predicate){
        if(predicate.getNameSpace().equals(RDF.getURI())){
            return predicate2numberKey(predicate);
        }else{
            return predicate.getLocalName();
        }
    }

    public String entity2containerId(Node entity){
        if(entity.isBlank()){
            return entity.getBlankNodeId().getLabelString();
        }
        return entity.getURI();
    }

    public Integer predicate2numberKey(Node predicate){
        return Integer.valueOf(predicate.getLocalName().split("_")[1]);
    }

    public String predicate2stringKey(Node predicate){
        return predicate.getLocalName();
    }

    public Object node2value(Node node){
        return node.getLiteralValue();
    }
}
