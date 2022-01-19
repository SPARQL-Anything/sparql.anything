/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.it;

import com.github.sparqlanything.csv.CSVTriplifier;
import com.github.sparqlanything.engine.FacadeX;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.filestream.FileStreamDatasetGraph;
import com.github.sparqlanything.model.filestream.FileStreamManager;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Properties;

public class FileStreamTest {
	private static Logger log = LoggerFactory.getLogger(FileStreamTest.class);

	private Node var(String v){
		return NodeFactory.createVariable(v);
	}

	@Test
	public void test() throws URISyntaxException, MalformedURLException {
		Properties properties = new Properties();
		properties.put("location", getClass().getClassLoader().getResource("test1.csv").toURI().toString());
		properties.put("blank-nodes", "false");
		BasicPattern bp = new BasicPattern();
		bp.add(new Triple(var("s"),var("p"),var("o")));
		OpBGP op = new OpBGP();
		FileStreamManager man = new FileStreamManager(ARQ.getContext(), op, properties, new CSVTriplifier());
		FileStreamDatasetGraph dg = new FileStreamDatasetGraph(man);
		Iterator<Quad> it = dg.findNG(var("g"),var("s"),var("p"),var("o"));
		int c=0;
		while(it.hasNext()){
			c++;
		}
		Assert.assertTrue(c == 16);
	}

	@Test
	public void testQueryJoin() throws URISyntaxException, MalformedURLException {
		Dataset kb = DatasetFactory.createGeneral();
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
		String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
		Query query = QueryFactory.create(
				"PREFIX fx:  <http://sparql.xyz/facade-x/ns/>\n" +
						"PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n" +
//						"SELECT * WHERE { SERVICE <x-sparql-anything:csv.headers=true,strategy=2,location="
//						+ location + "> { ?a ?b ?c }} ");
						"SELECT ?a ?b ?c ?d WHERE { SERVICE <x-sparql-anything:csv.headers=true,strategy=2,blank-nodes=false,location="
						+ location + "> { [] xyz:A ?a ; xyz:B ?b ; xyz:C ?c ; xyz:D ?d . filter(?d != \"\") }} ");
		ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
		int c = 0;
		while(rs.hasNext()){
			System.out.println(rs.next());
			c++;
		}
		Assert.assertTrue(c == 2);
	}
}
