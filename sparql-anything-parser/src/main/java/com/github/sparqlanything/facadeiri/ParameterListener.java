/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.sparqlanything.facadeiri;

import java.nio.CharBuffer;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sparqlanything.facadeiri.antlr.FacadeIRIBaseListener;
import com.github.sparqlanything.facadeiri.antlr.FacadeIRIParser;
import com.github.sparqlanything.model.IRIArgument;


public class ParameterListener extends FacadeIRIBaseListener {

	private Properties properties = new Properties();
	public static final char ESCAPE = '\\';
	public static final char[] ESCAPED = { '=', ',' };
	private static final Logger logger = LoggerFactory.getLogger(ParameterListener.class);

	public void enterParameter(FacadeIRIParser.ParameterContext ctx) {
		logger.trace("URL {}", ctx.url());
		if (ctx.url() != null) {
			properties.setProperty(IRIArgument.LOCATION.toString(), ctx.url().getText());
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
