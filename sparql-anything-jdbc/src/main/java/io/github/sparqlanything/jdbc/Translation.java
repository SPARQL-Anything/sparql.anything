/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.sparqlanything.jdbc;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.PropertyUtils;
import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.vocabulary.RDF;

import java.util.Properties;

public class Translation {
	private Properties properties;
	private String ns;
	public Translation(Properties properties) {
		this.properties = properties;
		this.ns = JDBC.getNamespace(properties);
	}

	public String nodeContainerToTable(Node node){
		if(node.isConcrete() && node.isURI()){
			return node.getURI().substring(ns.length());
		}
		return null;
	}

	public Integer nodeContainerToRowNum(Node node){
		if(node.isConcrete() && node.isBlank()){
			String lastPart = node.getBlankNodeLabel().substring(ns.length());
			if(lastPart.indexOf('/') != -1){
				return Integer.parseInt(lastPart.substring(lastPart.indexOf('/') + 1 ));
			}
		}
		return null;
	}

	public Integer nodeSlotToRowNum(Node node){
		if(node.isConcrete() && node.isURI()){
			String lastPart = node.getURI().substring(RDF.getURI().length());
			if(lastPart.indexOf('_') == 0){
				return Integer.parseInt(lastPart.substring(1));
			}
		}
		return null;
	}

	public Node rowNumToNodeSlot(Integer slot){
		return RDF.li(slot).asNode();
	}

	public String tableToContainer(String tableName){
		return this.ns + tableName;
	}

	public String tableToType(String tableName){
		return JDBC.getNamespace(properties) + tableName;
	}

	public boolean nodeTypeIsTable(Node node){
		return tableToType(node.getURI().substring(ns.length())).equals(node.getURI());
	}

	public Node tableToNodeType(String tableName){
		return NodeFactory.createURI(tableToType(tableName));
	}

	public Node tableToNodeContainer(String tableName){
		return NodeFactory.createURI(tableToContainer(tableName));
	}

	public String rowNumToContainer(String resultSetTokenId, Integer rowNum){
		return this.ns + resultSetTokenId + "/" + rowNum.toString();
	}

	public Node rowNumToNodeContainer(String resultSetTokenId, Integer rowNum){
		return NodeFactory.createBlankNode(rowNumToContainer(resultSetTokenId, rowNum));
	}

	public String nodeSlotToColumn(Node node){
		if(node.isConcrete() && node.isURI()){
			return node.getURI().substring(PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE)
.length());
		}
		return null;
	}
	public boolean nodeSlotIsColumn(Node node){
		if(node.isURI() && node.getURI().startsWith(RDF.getURI())){
			return false;
		}
		return nodeSlotToColumn(node) != null;
	}

	public String columnToSlot(String colName){
		return PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE) + colName;
	}

	public boolean nodeSlotIsTypeProperty(Node node){
		return RDF.type.asNode().equals(node);
	}

	public Node columnToNodeSlot(String colName){
		return NodeFactory.createURI(PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE) + colName);
	}

	public boolean nodeContainerIsTable(Node node){
		return (node.isConcrete() && node.isURI() &&
			node.getURI().startsWith(ns) & node.getURI().indexOf('/', ns.length()) == -1);
	}
	public boolean nodeContainerIsRowNum(Node node){
		return (node.isConcrete() && node.isBlank() &&
				node.getBlankNodeLabel().startsWith(ns) & node.getBlankNodeLabel().indexOf('/', ns.length()) != -1);
	}

	public boolean nodeSlotIsRowNum(Node node){
		return (node.isConcrete() && node.isURI() &&
				node.getURI().startsWith(RDF.getURI()) &&
						node.getURI().substring(RDF.getURI().length(), RDF.getURI().length()+1).equals("_"));
	}

}
