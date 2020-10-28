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
			properties.setProperty(ctx.keyValue().IDENTIFIER().getText(), ctx.keyValue().LITERAL().getText());
		}
	}

	public Properties getProperties() {
		return properties;
	}
	
	

}
