/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.it;

import com.github.sparqlanything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.main.QC;

public class DocumentationHelperAndTester {

	public static void main(String[] args) {
		Dataset ds = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		String location = "https://raw.githubusercontent.com/SPARQL-Anything/sparql.anything/v0.8-DEV/FUNCTIONS_AND_MAGIC_PROPERTIES.md";
		String queryStringPattern = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * { SERVICE<x-sparql-anything:%s> {?s a xyz:FencedCodeBlock ; rdf:_1 ?f }} ";
		String queryString = String.format(queryStringPattern, location);

		Query query = QueryFactory.create(queryString);

//		QueryExecution qexec = QueryExecutionFactory.create(query,ds);
//		System.out.println(ResultSetFormatter.asText(qexec.execSelect()));

	}
}
