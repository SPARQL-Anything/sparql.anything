package com.github.spiceh2020.sparql.anything.tupleurl;

import java.util.Properties;

import com.github.spiceh2020.sparql.anything.tupleurl.antlr.TupleURLBaseListener;
import com.github.spiceh2020.sparql.anything.tupleurl.antlr.TupleURLParser;

public class ParameterListener extends TupleURLBaseListener {

	private Properties properties = new Properties();
	public static final String LOCATION = "location";

	public void enterParameter(TupleURLParser.ParameterContext ctx) {
		if (ctx.url() != null) {
			properties.setProperty(LOCATION, ctx.url().LITERAL().getText());
		}

		if (ctx.keyValue() != null) {
			String key = ctx.keyValue().IDENTIFIER(0).getText();
			if (ctx.keyValue().LITERAL() != null) {
				properties.setProperty(key, ctx.keyValue().LITERAL().getText());
			}
			if (ctx.keyValue().IDENTIFIER(1) != null) {
				properties.setProperty(key, ctx.keyValue().IDENTIFIER(1).getText());
			}
		}
	}

	public Properties getProperties() {
		return properties;
	}

}
