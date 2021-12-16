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

package com.github.sparqlanything.engine.functions.reflection;

import java.util.Calendar;
import java.util.Date;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.NodeValueBoolean;
import org.apache.jena.sparql.expr.nodevalue.NodeValueDecimal;
import org.apache.jena.sparql.expr.nodevalue.NodeValueDouble;
import org.apache.jena.sparql.expr.nodevalue.NodeValueFloat;
import org.apache.jena.sparql.expr.nodevalue.NodeValueInteger;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;

public class Converters {

	public static class CharConverter implements NodeValueConverter<Character, NodeValueString> {
		@Override
		public Class<Character> getType() {
			return Character.class;
		}

		@Override
		public Class<NodeValueString> getNodeValueType() {
			return NodeValueString.class;
		}

		@Override
		public Character asType(NodeValue nodeValue) {
			if(nodeValue.isLiteral() && nodeValue.isString()) {
				return nodeValue.getString().charAt(0);
			}else{
				// cast to String
				return nodeValue.asString().charAt(0);
			}
		}

		@Override
		public NodeValueString asNodeValue(Character object) {
			return (NodeValueString) NodeValue.makeNodeString(object.toString());
		}
	}

	public static class StringConverter implements NodeValueConverter<String,NodeValueString> {
		@Override
		public Class<String> getType() {
			return String.class;
		}

		@Override
		public Class<NodeValueString> getNodeValueType() {
			return NodeValueString.class;
		}

		@Override
		public String asType(NodeValue nodeValue) {
			if(nodeValue.isLiteral() && nodeValue.isString()) {
				return nodeValue.getString();
			}else{
				// cast to String
				return nodeValue.asString();
			}
		}

		@Override
		public NodeValueString asNodeValue(String object) {
			return (NodeValueString)  NodeValue.makeString(object);
		}
	}

	public static class IntegerConverter implements NodeValueConverter<Integer, NodeValueInteger> {
		@Override
		public Class<Integer> getType() {
			return Integer.class;
		}

		@Override
		public Class<NodeValueInteger> getNodeValueType() {
			return NodeValueInteger.class;
		}

		@Override
		public Integer asType(NodeValue nodeValue) {
			// Assumed to be an integer
			return nodeValue.getInteger().intValue();
		}

		@Override
		public NodeValueInteger asNodeValue(Integer object) {
			return (NodeValueInteger) NodeValue.makeInteger(object);
		}
	}

	public static class BooleanConverter implements NodeValueConverter<Boolean, NodeValueBoolean> {
		@Override
		public Class<Boolean> getType() {
			return Boolean.class;
		}

		@Override
		public Class<NodeValueBoolean> getNodeValueType() {
			return NodeValueBoolean.class;
		}

		@Override
		public Boolean asType(NodeValue nodeValue) {
			// Assumed to be an Boolean
			return nodeValue.getBoolean();
		}

		@Override
		public NodeValueBoolean asNodeValue(Boolean object) {
			return (NodeValueBoolean) NodeValue.makeBoolean(object);
		}
	}

	public static class FloatConverter implements NodeValueConverter<Float, NodeValueFloat> {
		@Override
		public Class<Float> getType() {
			return Float.class;
		}

		@Override
		public Class<NodeValueFloat> getNodeValueType() {
			return NodeValueFloat.class;
		}

		@Override
		public Float asType(NodeValue nodeValue) {
			// Assumed to be an Float
			return nodeValue.getFloat();
		}

		@Override
		public NodeValueFloat asNodeValue(Float object) {
			return (NodeValueFloat) NodeValue.makeFloat(object);
		}
	}

	public static class DoubleConverter implements NodeValueConverter<Double, NodeValueDouble> {
		@Override
		public Class<Double> getType() {
			return Double.class;
		}

		@Override
		public Class<NodeValueDouble> getNodeValueType() {
			return NodeValueDouble.class;
		}

		@Override
		public Double asType(NodeValue nodeValue) {
			// Assumed to be an Double
			return nodeValue.getDouble();
		}

		@Override
		public NodeValueDouble asNodeValue(Double object) {
			return (NodeValueDouble) NodeValue.makeDouble(object);
		}
	}

	public static class LongConverter implements NodeValueConverter<Long,NodeValueDecimal> {
		@Override
		public Class<Long> getType() {
			return Long.class;
		}

		@Override
		public Class<NodeValueDecimal> getNodeValueType() {
			return NodeValueDecimal.class;
		}

		@Override
		public Long asType(NodeValue nodeValue) {
			// Assumed to be an Long
			return nodeValue.getDecimal().longValue();
		}

		@Override
		public NodeValueDecimal asNodeValue(Long object) {
			return (NodeValueDecimal) NodeValue.makeDecimal(object);
		}
	}

	public static class DateConverter implements NodeValueConverter<Date, NodeValue> {
		@Override
		public Class<Date> getType() {
			return Date.class;
		}

		@Override
		public Class<NodeValue> getNodeValueType() {
			return NodeValue.class;
		}

		@Override
		public Date asType(NodeValue nodeValue) {
			// Assumed to be an DateTime
			return nodeValue.getDateTime().toGregorianCalendar().getTime();
		}

		@Override
		public NodeValue asNodeValue(Date object) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(object);
			return NodeValue.makeDate(cal);
		}

		@Override
		public boolean compatibleWith(NodeValue nodeValue) {
			// Not entirely sure about this one.
			if(nodeValue.getDateTime() != null){
				return true;
			}
			return false;
		}
	}
}
