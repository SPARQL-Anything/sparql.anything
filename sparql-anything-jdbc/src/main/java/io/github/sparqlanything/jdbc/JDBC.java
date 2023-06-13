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
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;

import java.util.Properties;

public class JDBC {
	public static final String PROPERTY_NAMESPACE = "jdbc.namespace";
	public static final String PROPERTY_USER = "jdbc.user";
	public static final String PROPERTY_PASSWORD = "jdbc.password";

	public static final String PROPERTY_DRIVER = "jdbc.driver";
	public static final String DEFAULT_NAMES_NAMESPACE = "http://sparql.xyz/facade-x/db/";

	public static QueryIterator execute (Properties properties, OpBGP pattern, QueryIterator input, ExecutionContext ctx){
//		pattern.
		return null;
	}

	/**
	 * The namespace to be used for naming entities
	 *
	 * @param properties
	 * @return
	 */
	public static String getNamespace(Properties properties){
		return properties.getProperty(PROPERTY_NAMESPACE, DEFAULT_NAMES_NAMESPACE);
	}

	public static String getUser(Properties properties){
		return properties.getProperty(PROPERTY_USER, "");
	}

	public static String getPassword(Properties properties){
		return properties.getProperty(PROPERTY_PASSWORD, "");
	}

	public static String getDriver(Properties properties){
		return properties.getProperty(PROPERTY_DRIVER, "");
	}

	public static String getConnectionUrl(Properties properties){
		return properties.getProperty(IRIArgument.LOCATION.toString());
	}
}
