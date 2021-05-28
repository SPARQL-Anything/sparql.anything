package com.github.spiceh2020.sparql.anything.fuseki;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		Template temp = TransformerConfiguration.getInstance().getFreemarkerCfg().getTemplate("yasgui.ftlh");

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
