package com.github.spiceh2020.sparql.anything.fuseki;

import java.io.File;
import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class TransformerConfiguration {

	private static TransformerConfiguration instance;
	private Configuration freemarkerCfg;

	private TransformerConfiguration() throws IOException {
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.27) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		freemarkerCfg = new Configuration(Configuration.VERSION_2_3_28);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		freemarkerCfg.setDirectoryForTemplateLoading(new File("src/main/resources"));
		freemarkerCfg.setNumberFormat("computer");

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		freemarkerCfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is
		// better.
		freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		freemarkerCfg.setLogTemplateExceptions(false);

		// Wrap unchecked exceptions thrown during template processing into
		// TemplateException-s.
		freemarkerCfg.setWrapUncheckedExceptions(true);
	}

	public static TransformerConfiguration getInstance() throws IOException {
		if (instance == null) {
			instance = new TransformerConfiguration();
		}
		return instance;
	}

	public Configuration getFreemarkerCfg() {
		return freemarkerCfg;
	}

}