/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.spreadsheet.test;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.spreadsheet.SpreadsheetTriplifier;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestTriplifier {

	@Test
	public void testCellLink() {
		SpreadsheetTriplifier st = new SpreadsheetTriplifier();
		URL spreadsheet = st.getClass().getClassLoader().getResource("./testResources/Book2.xlsx");
		Properties p = new Properties();
		p.setProperty(SpreadsheetTriplifier.PROPERTY_COMPOSITE_VALUES.toString(), "true");
		DatasetGraph dg;
		try {

			p.setProperty(IRIArgument.LOCATION.toString(), Objects.requireNonNull(spreadsheet).toString());

			FacadeXGraphBuilder builder = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, builder);
			dg = builder.getDatasetGraph();

			String prefixes = "PREFIX xyz: <http://sparql.xyz/facade-x/data/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
			String[] asks = new String[]{" ASK {[ a xyz:STRING ; rdf:_1 \"C12\" ; xyz:address \"http://www.example.org/C12\" ; xyz:label \"C12\" ]}", " ASK {[ a xyz:STRING ; rdf:_1 \"B\" ]}", " ASK {[ a xyz:STRING ; rdf:_1 \"B12\" ; xyz:author ?author ; xyz:threadedComment ?tc  ] FILTER(REGEX(?tc, \"This is a comment\"))  }",};

			for (String ask : asks) {
				try {
					assertTrue(QueryExecutionFactory.create(QueryFactory.create(prefixes + ask), dg).execAsk());
				} catch (Exception e) {
					fail(prefixes + ask);
					fail(e.getMessage());
				}
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
