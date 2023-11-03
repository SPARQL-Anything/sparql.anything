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

package io.github.sparqlanything.engine.functions;

import org.apache.commons.text.similarity.SimilarityScore;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.jena.sparql.function.FunctionFactory;

public class SimilarityScoreFunctionFactory<T> implements FunctionFactory {

	private final SimilarityScore<T> similarityScore;

	public SimilarityScoreFunctionFactory(SimilarityScore<T> similarityScore){
		super();
		this.similarityScore = similarityScore;
	}


	@Override
	public Function create(String s) {
		return new FunctionBase2() {
			@Override
			public NodeValue exec(NodeValue nodeValue, NodeValue nodeValue1) {
				T result = similarityScore.apply(FunctionsUtils.nodeValueAsString(nodeValue),FunctionsUtils.nodeValueAsString(nodeValue1));
				if(result instanceof Integer){
					return  NodeValue.makeInteger((Integer)result);
				} else if(result instanceof Double){
					return  NodeValue.makeDouble((Double)result);
				}
				return  NodeValue.nvNaN;
			}
		};
	}
}
