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
//		freemarkerCfg.setDirectoryForTemplateLoading(new File("src/main/resources"));
		freemarkerCfg.setClassLoaderForTemplateLoading(TransformerConfiguration.class.getClassLoader(), ".");
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
