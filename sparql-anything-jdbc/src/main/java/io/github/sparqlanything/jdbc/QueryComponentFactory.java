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

import java.lang.reflect.Type;

public class QueryComponentFactory {

	public static class Table implements QueryComponent {
		private String tableName;
		Table(String tableName){
			this.tableName = tableName;
		}
		public String getName(){
			return tableName;
		}
	}

	public static class Column implements QueryComponent {
		private String columnName;

		Column(String columnName){
			this.columnName = columnName;
		}

		public String getName(){
			return columnName;
		}
	}

	public static class Projection implements QueryComponent {
		private Column column;

		Projection(Column column){
			this.column = column;
		}

		public Column getColumn(){
			return column;
		}
	}

	public static class Condition implements QueryComponent {
		private Column leftColumn;
		private Value rightValue = null;
		private Column rightColumn = null;

		Condition(Column onColumn, Value value){
			this.leftColumn = onColumn;
			this.rightValue = value;
		}
		Condition(Column leftColumn, Column rightColumn){
			this.leftColumn = leftColumn;
			this.rightColumn = rightColumn;
		}

		public Column getLeftColumn(){
			return leftColumn;
		}
		public Column getRightColumn(){
			return rightColumn;
		}
		public Value getRightValue(){
			return rightValue;
		}
	}

	public static class Value implements QueryComponent {
		private Object value;
		private Type type;

		Value(Object value, Type type){
			this.value = value;
			this.type = type;
		}

		public Object getValue(){
			return value;
		}
		public Type getType(){
			return type;
		}
	}

	public static Table table(String name){
		return new Table(name);
	}

	public static Column column(String column){
		return new Column(column);
	}
	public static Value value(String value, Type type){
		return new Value(value, type);
	}
	public static Value value(int value, Type type){
		return new Value(value, type);
	}
	public static Value value(double value, Type type){
		return new Value(value, type);
	}
	public static Value value(Object value, Type type){
		return new Value(value, type);
	}
	public static Condition condition(Column left, Column right){
		return new Condition(left, right);
	}

	public static Condition condition(Column left, Value right){
		return new Condition(left, right);
	}

	public static Projection projection(Column column){
		return new Projection(column);
	}

}
