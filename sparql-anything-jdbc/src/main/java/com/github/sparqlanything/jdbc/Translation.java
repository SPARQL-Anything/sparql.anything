/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.jdbc;

import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;

import java.util.Properties;

public class Translation {
	private Properties properties;
	private String ns;
	public Translation(Properties properties) {
		this.properties = properties;
		this.ns = JDBC.getNamespace(properties);
	}

	public String nodeToTable(Node node){
		if(node.isConcrete() && node.isURI()){
			return node.getURI().substring(ns.length());
		}
		return null;
	}

	public Integer nodeToRowNum(Node node){
		if(node.isConcrete() && node.isURI()){
			String lastPart = node.getURI().substring(ns.length());
			if(lastPart.indexOf('/') != -1){
				return Integer.parseInt(lastPart.substring(lastPart.indexOf('/') + 1 ));
			}
		}
		return null;
	}

	public String tableToContainer(String tableName){
		return this.ns + tableName;
	}

	public String rowNumToContainer(String tableName, Integer rowNum){
		return this.ns + tableName + "/" + rowNum.toString();
	}

	public String nodeToColumn(Node node){
		if(node.isConcrete() && node.isURI()){
			return node.getURI().substring(Triplifier.getNamespaceArgument(properties).length());
		}
		return null;
	}
}
