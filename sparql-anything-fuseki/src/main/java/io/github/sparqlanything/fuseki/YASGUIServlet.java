/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package io.github.sparqlanything.fuseki;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class YASGUIServlet extends HttpServlet {
	final static Logger logger = LoggerFactory.getLogger(YASGUIServlet.class);
	@Serial
	private static final long serialVersionUID = 1L;

	private final String fxPrefix;
	private final String endpointPath;
	private String generatedSnippetsJs = null;

	public YASGUIServlet(String fxPrefix, String endpointPath) {
		this.fxPrefix = fxPrefix;
		this.endpointPath = endpointPath;
		loadGeneratedSnippets();
	}

	/**
	 * Loads generated snippets from the generated JavaScript file.
	 * Falls back to empty array if file doesn't exist.
	 */
	private void loadGeneratedSnippets() {
		try {
			InputStream is = getClass().getResourceAsStream("/io/github/sparqlanything/fuseki/generated-snippets.js");
			if (is != null) {
				generatedSnippetsJs = new String(is.readAllBytes());
				logger.info("Loaded generated snippets");
			} else {
				logger.warn("Generated snippets file not found, using empty array");
				generatedSnippetsJs = "[]";
			}
		} catch (IOException e) {
			logger.error("Failed to load generated snippets", e);
			generatedSnippetsJs = "[]";
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");// setting the content type
		
		Configuration c = TransformerConfiguration.getInstance().getFreemarkerCfg();
		c.setClassForTemplateLoading(this.getClass(), "");
		Template temp = c.getTemplate("yasgui.ftlh");

		Map<String, Object> var = new HashMap<>();
		var.put("sparqlPath", this.endpointPath);
		var.put("fxPrefix", this.fxPrefix);
		var.put("generatedSnippets", generatedSnippetsJs);

		PrintWriter pw = res.getWriter();
//		StringWriter sw = new StringWriter();
		try {
			temp.process(var, pw);
//			temp.process(var, sw);
		} catch (TemplateException | IOException e1) {
			logger.error("",e1);
		}
		pw.close();
//		System.out.println(sw.toString());
	}
}
