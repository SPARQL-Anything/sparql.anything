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

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;

import java.util.List;
import java.util.Properties;

public class Interpretation {
	private Properties properties;
	private String namesNamespace;
	private OpBGP opBGP;
	private Translation translation;

	public Interpretation(Properties properties, OpBGP opBGP){
		this.properties = properties;
		this.opBGP = opBGP;
		this.translation = new Translation(properties);
	}

	private void interpret(){

		List<Triple> tripleList = opBGP.getPattern().getList();

		for(Triple triple: tripleList){
			// Subject

			// Predicate

			// Object

			// Joins?
		}
	}


}
