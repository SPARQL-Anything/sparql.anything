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
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCManagerTest {

	final static Logger L = LoggerFactory.getLogger(JDBCManagerTest.class);
	private JDBCManager manager;
	String username = "jdbc-test-h2";
	String password = "this-is-just-a-test";

	@Before
	public void before() {

		// Define database location
		String dbLocation = getClass().getClassLoader().getResource("./").getPath() + "jdbc-test-h2/data";
		File dbLoc = new File(dbLocation);
		dbLoc.mkdirs();
		String jdbcUrl = "jdbc:h2:" + dbLocation ;

		// Prepare properties
		Properties properties = new Properties();
		properties.setProperty(JDBC.PROPERTY_DRIVER, "org.h2.Driver");
		properties.setProperty(JDBC.PROPERTY_USER, username);
		properties.setProperty(JDBC.PROPERTY_PASSWORD, password);
		properties.setProperty(IRIArgument.LOCATION.toString(), jdbcUrl);

		// Initialise test database manager
		manager = new JDBCManager( properties );
	}

	@Test
	public void C1_connection() throws SQLException {
		Connection connection = manager.getConnection();
		L.info("Database name: {}", connection.getMetaData().getDatabaseProductName());
		L.info("Database URL: {}", connection.getMetaData().getURL());
		//
		connection.close();
	}

	@Test
	public void C2_setup() throws SQLException {
		String testCSV = getClass().getClassLoader().getResource("./test.csv").getPath();
		Connection connection = manager.getConnection();
		Statement statement = connection.createStatement();
		boolean outcome = statement.execute("CREATE TABLE TEST (ID INT PRIMARY KEY, WORD VARCHAR(255))\n" +
						"    AS SELECT * FROM CSVREAD('" + testCSV + "')");
		Assert.assertFalse(outcome);
		connection.close();
	}

	@Test
	public void C3_verify() throws SQLException {
		Connection connection = manager.getConnection();
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM TEST;");
		Assert.assertTrue(rs.next());
		connection.close();
	}

	@Test
	public void M1_listTables() throws SQLException {
		L.info("Tables: {}", manager.listTables());
		L.info("Columns of table TEST: {}", manager.listColumns("TEST"));
		Assert.assertTrue(manager.listTables().contains("TEST"));
		Assert.assertTrue(manager.listColumns("TEST").size() == 2);
	}
}
