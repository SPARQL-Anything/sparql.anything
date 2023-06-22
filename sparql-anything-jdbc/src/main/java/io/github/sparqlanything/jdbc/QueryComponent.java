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

public class QueryComponent {

	public static class Table extends QueryComponent {
		private String tableName;
		Table(String tableName){
			this.tableName = tableName;
		}
		public String getName(){
			return tableName;
		}
	}

	public static class Column extends QueryComponent {
		private String columnName;

		Column(String columnName){
			this.columnName = columnName;
		}

		public String getName(){
			return columnName;
		}
	}

	public static Table table(String name){
		return new Table(name);
	}


	public static Column column(String column){
		return new Column(column);
	}
}
