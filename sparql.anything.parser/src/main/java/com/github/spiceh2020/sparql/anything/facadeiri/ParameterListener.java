package com.github.spiceh2020.sparql.anything.facadeiri;

import java.nio.CharBuffer;
import java.util.Properties;

import com.github.spiceh2020.sparql.anything.facadeiri.antlr.FacadeIRIBaseListener;
import com.github.spiceh2020.sparql.anything.facadeiri.antlr.FacadeIRIParser;


public class ParameterListener extends FacadeIRIBaseListener{

	private Properties properties = new Properties();
	public static final String LOCATION = "location";
	public static final char ESCAPE = '\\';
	public static final char[] ESCAPED = { '=', ',' };

	public void enterParameter(FacadeIRIParser.ParameterContext ctx) {
		if (ctx.url() != null) {
			properties.setProperty(LOCATION, ctx.url().LITERAL().getText());
		}

		if (ctx.keyValue() != null) {
			String key = unescape(ctx.keyValue().IDENTIFIER(0).getText());
			if (ctx.keyValue().LITERAL() != null) {
				properties.setProperty(key, unescape(ctx.keyValue().LITERAL().getText()));
			}
			if (ctx.keyValue().IDENTIFIER(1) != null) {
				properties.setProperty(key, unescape(ctx.keyValue().IDENTIFIER(1).getText()));
			}
		}
	}

	private String unescape(String s) {
		String result = s;
		for (char escaped : ESCAPED) {

			result = result
					.replace(CharBuffer.wrap(new char[] { ESCAPE, escaped }),
					CharBuffer.wrap(new char[] { escaped }));
		}
		return result;
	}

	public Properties getProperties() {
		return properties;
	}

}
