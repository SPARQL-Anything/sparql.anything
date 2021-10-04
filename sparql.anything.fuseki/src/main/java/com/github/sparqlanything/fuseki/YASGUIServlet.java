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

package com.github.sparqlanything.fuseki;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class YASGUIServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private String fxPrefix, endpointPath;

	public YASGUIServlet(String fxPrefix, String endpointPath) {
		this.fxPrefix = fxPrefix;
		this.endpointPath = endpointPath;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");// setting the content type
		
		Configuration c = TransformerConfiguration.getInstance().getFreemarkerCfg();
		c.setClassForTemplateLoading(this.getClass(), "");
		Template temp = c.getTemplate("yasgui.ftlh");

		Map<String, Object> var = new HashMap<>();
		var.put("sparqlPath", this.endpointPath);
		var.put("fxPrefix", this.fxPrefix);

		PrintWriter pw = res.getWriter();
//		StringWriter sw = new StringWriter();
		try {
			temp.process(var, pw);
//			temp.process(var, sw);
		} catch (TemplateException | IOException e1) {
			e1.printStackTrace();
		}
		pw.close();
//		System.out.println(sw.toString());
	}
}
