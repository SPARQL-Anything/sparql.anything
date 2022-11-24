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

import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TranslationTest {
	private static final Logger L = LoggerFactory.getLogger(TranslationTest.class);

	private Properties properties = new Properties();

	private Translation translation;

	private String testNamespace = "http://www.example.com/test/";

	@Rule
	public TestName testName = new TestName();

	@Before
	public void before(){
		if(!testName.getMethodName().endsWith("Default")) {
			properties.setProperty(JDBC.PROPERTY_NAMESPACE, testNamespace);
			properties.setProperty(IRIArgument.NAMESPACE.toString(), testNamespace);
		}
		translation = new Translation(properties);
	}

	@Test
	public void nodeToTable(){
		String table = "address";
		Node n = NodeFactory.createURI(testNamespace + table);
		L.debug("{}",translation.nodeContainerToTable(n));
		Assert.assertTrue(table.equals(translation.nodeContainerToTable(n)));
	}

	@Test
	public void nodeToTableDefault(){
		String table = "address";
		Node n = NodeFactory.createURI(JDBC.DEFAULT_NAMES_NAMESPACE + table);
		L.debug("{}",translation.nodeContainerToTable(n));
		Assert.assertTrue(table.equals(translation.nodeContainerToTable(n)));
	}

	@Test
	public void nodeToColumn(){
		String col = "postcode";
		Node n = NodeFactory.createURI(testNamespace + col);
		L.debug("{}",translation.nodeSlotToColumn(n));
		Assert.assertTrue(col.equals(translation.nodeSlotToColumn(n)));
	}

	@Test
	public void nodeToColumnDefault(){
		String col = "postcode";
		Node n = NodeFactory.createURI(Triplifier.XYZ_NS + col);
		L.debug("{}",translation.nodeSlotToColumn(n));
		Assert.assertTrue(col.equals(translation.nodeSlotToColumn(n)));
	}

	@Test
	public void nodeToRowNum(){
		Integer row = 23;
		String table = "address";
		Node n = NodeFactory.createURI(testNamespace + table + "/" + row.toString());
		L.debug("{}",translation.nodeContainerToRowNum(n));
		Assert.assertTrue(row.equals(translation.nodeContainerToRowNum(n)));
	}

	@Test
	public void rowNumToNode(){
		Integer row = 23;
		String table = "address";
		Node n = NodeFactory.createURI(testNamespace + table + "/" + row.toString());
		L.debug("{}",translation.rowNumToNodeContainer( table, row));
		Assert.assertTrue(n.equals(translation.rowNumToNodeContainer(table, row)));
	}

	@Test
	public void columnToNode(){
		String col = "postcode";
		Node n = NodeFactory.createURI(testNamespace + col);
		L.debug("{}",translation.columnToNodeSlot(col));
		Assert.assertTrue(n.equals(translation.columnToNodeSlot(col)));
	}

	@Test
	public void nodeIsTable(){
		Node table = translation.tableToNodeContainer("address");
		Assert.assertTrue(translation.nodeContainerIsTable(table));
		Assert.assertFalse(translation.nodeContainerIsRowNum(table));
		Assert.assertFalse(translation.nodeSlotIsRowNum(table));
	}

	@Test
	public void nodeIsRowNum(){
		Node table = translation.rowNumToNodeContainer("address", 23);
		Assert.assertTrue(translation.nodeContainerIsRowNum(table));
		Assert.assertFalse(translation.nodeSlotIsRowNum(table));
		Assert.assertFalse(translation.nodeContainerIsTable(table));
	}
	@Test
	public void nodeIsRowSlot(){
		Node slot = translation.rowNumToNodeSlot(23);
		Assert.assertTrue(translation.nodeSlotIsRowNum(slot));
		Assert.assertFalse(translation.nodeContainerIsRowNum(slot));
		Assert.assertFalse(translation.nodeContainerIsTable(slot));
	}
}
