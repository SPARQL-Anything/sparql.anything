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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JDBCManager {
	private final Properties properties;

	private Connection connection = null;

	public JDBCManager(Properties properties){
		this.properties = properties;
	}

	Connection getConnection() throws SQLException {
		// TODO When/How to close the connection?
		if(connection == null){
			try {
				Class.forName(JDBC.getDriver(properties));
				this.connection = DriverManager.getConnection(JDBC.getConnectionUrl(properties), JDBC.getUser(properties), JDBC.getPassword(properties));
			}catch(ClassNotFoundException e){
				throw new SQLException(e);
			}

		}
		return this.connection;
	}

	public List<String> listTables() throws SQLException {
		DatabaseMetaData md = getConnection().getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		List<String> tables = new ArrayList<>();
		while (rs.next()) {
			tables.add(rs.getString(3));
		}
		return Collections.unmodifiableList(tables);
	}

	/**
	 *
	 * @param table
	 * @return Map - column name and type
	 * @throws SQLException
	 */
	public Map<String,String> listColumns(String table) throws SQLException {
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getColumns(null, null, table, null);
		Map<String,String> columns = new HashMap<String,String>();
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			String type = rs.getString("TYPE_NAME");
			columns.put(name, type);
//			int size = resultSet.getInt("COLUMN_SIZE");
		}
		return Collections.unmodifiableMap(columns);
	}
}
