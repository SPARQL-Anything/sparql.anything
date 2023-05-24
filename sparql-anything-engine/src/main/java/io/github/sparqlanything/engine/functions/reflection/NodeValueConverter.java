/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.engine.functions.reflection;

import org.apache.jena.sparql.expr.NodeValue;

public interface NodeValueConverter<T extends Object,V extends NodeValue> {
	Class<T> getType();
	Class<V> getNodeValueType();

	T asType(NodeValue nodeValue);

	default NodeValue objectAsNodeValue(Object object) throws IncompatibleObjectException {
		if(object.getClass().equals(getType())){
			return asNodeValue((T) object);
		}else {
			throw new IncompatibleObjectException();
		}
	}

	V asNodeValue(T object);

	default boolean compatibleWith(NodeValue nodeValue){
		return nodeValue.getClass().equals(getNodeValueType());
	}
}
