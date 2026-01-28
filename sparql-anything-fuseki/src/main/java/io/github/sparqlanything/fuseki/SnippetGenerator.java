/*
 * Copyright (c) 2025 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

/*
 */

package io.github.sparqlanything.fuseki;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.github.sparqlanything.documentationgenerator.ExampleSection;
import io.github.sparqlanything.documentationgenerator.FormatSection;
import io.github.sparqlanything.documentationgenerator.OptionSection;
import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierRegister;
import io.github.sparqlanything.model.annotations.*;
import org.apache.jena.query.ARQ;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Generates YASGUI snippets from SPARQL Anything annotations.
 * Reads @Format, @Option, @Example annotations from format packages,
 * and @FXFunctionDoc, @MagicPropertyDoc from function/property classes.
 */
public class SnippetGenerator {

	private static final Logger logger = LoggerFactory.getLogger(SnippetGenerator.class);

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 1) {
			logger.error("Usage: SnippetGenerator <output-file>");
			System.exit(1);
		}

		String outputFile = args[0];
		logger.info("Generating snippets to: {}", outputFile);

		// Initialize Facade-X to register functions and properties
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		SnippetGenerator generator = new SnippetGenerator();
		List<SnippetSection> snippets = generator.generateAllSnippets();

		generator.writeSnippetsToFile(snippets, outputFile);
		logger.info("Successfully generated {} snippets", snippets.size());
	}

	/**
	 * Generates all snippets from annotations.
	 */
	public List<SnippetSection> generateAllSnippets() throws ClassNotFoundException {
		List<SnippetSection> allSnippets = new ArrayList<>();

		// Generate format snippets
		allSnippets.addAll(generateFormatSnippets());

		// Generate function snippets
		allSnippets.addAll(generateFunctionSnippets());

		// Generate magic property snippets
		allSnippets.addAll(generateMagicPropertySnippets());

		return allSnippets;
	}

	/**
	 * Generates snippets for all file formats from @Format and @Option annotations.
	 */
	public List<SnippetSection> generateFormatSnippets() throws ClassNotFoundException {
		List<SnippetSection> snippets = new ArrayList<>();
		Map<Package, Set<Class<?>>> packageToClasses = getFormatPackages();

		for (Map.Entry<Package, Set<Class<?>>> entry : packageToClasses.entrySet()) {
			Package pkg = entry.getKey();
			Set<Class<?>> classes = entry.getValue();
			
			FormatSection formatSection = new FormatSection(pkg, classes);
			SnippetSection snippet = createSnippetFromFormat(formatSection);
			if (snippet != null) {
				snippets.add(snippet);
			}
		}

		// Sort by format name
		snippets.sort(Comparator.comparing(SnippetSection::getLabel));
		return snippets;
	}

	/**
	 * Creates a snippet from a FormatSection using @Example annotations.
	 */
	private SnippetSection createSnippetFromFormat(FormatSection formatSection) {
		String label = formatSection.getName();
		String description = formatSection.getDescription();
		
		// Build the code snippet from options and examples
		StringBuilder code = new StringBuilder();
		code.append("# Query ").append(label).append(" file\n");
		
		// Add format description as comment
		if (description != null && !description.isEmpty()) {
			String[] descLines = description.split("\\n");
			for (int i = 0; i < Math.min(3, descLines.length); i++) {
				String line = descLines[i].trim();
				if (!line.isEmpty() && !line.startsWith("#")) {
					code.append("# ").append(line).append("\n");
				}
			}
		}

		// List all options with descriptions
		List<OptionSection> options = formatSection.getOptionSections();
		if (!options.isEmpty()) {
			code.append("# All ").append(label).append(" options:\n");
			for (OptionSection option : options) {
				try {
					String optionName = option.getName();
					String optionDesc = option.getDescription();
					// Shorten description to first sentence or 80 chars
					if (optionDesc.length() > 80) {
						int period = optionDesc.indexOf('.');
						if (period > 0 && period < 80) {
							optionDesc = optionDesc.substring(0, period + 1);
						} else {
							optionDesc = optionDesc.substring(0, 77) + "...";
						}
					}
					code.append("#   ").append(optionName).append(" - ").append(optionDesc).append("\n");
				} catch (NoSuchMethodException | IllegalAccessException e) {
					logger.warn("Failed to get option details", e);
				}
			}
		}

		// Generate example query - use first @Example if available
		String exampleQuery = findBestExampleQuery(options, formatSection);
		code.append(exampleQuery);

		return new SnippetSection(label, code.toString(), "FILE FORMATS");
	}

	/**
	 * Finds the best example query from @Example annotations.
	 */
	private String findBestExampleQuery(List<OptionSection> options, FormatSection formatSection) {
		// Look for examples in options
		for (OptionSection option : options) {
			List<ExampleSection> examples = option.getExamples();
			if (examples != null && !examples.isEmpty()) {
				ExampleSection firstExample = examples.get(0);
				String query = firstExample.getQuery();
				if (query != null && !query.isEmpty()) {
					return query;
				}
			}
		}

		// Fallback: generate a basic query template
		return generateDefaultQuery(formatSection);
	}

	/**
	 * Generates a default query template for a format.
	 */
	private String generateDefaultQuery(FormatSection formatSection) {
		StringBuilder query = new StringBuilder();
		query.append("PREFIX xyz: <http://sparql.xyz/facade-x/data/>\n");
		query.append("PREFIX fx: <http://sparql.xyz/facade-x/ns/>\n\n");
		query.append("SELECT * WHERE {\n");
		query.append("  SERVICE <x-sparql-anything:> {\n");
		query.append("    fx:properties\n");
		query.append("      fx:location \"").append(formatSection.getResourceExample()).append("\" ;\n");
		
		// Add first option as example if available
		List<OptionSection> options = formatSection.getOptionSections();
		if (!options.isEmpty()) {
			try {
				OptionSection firstOption = options.get(0);
				String optionName = firstOption.getName();
				String defaultValue = getDefaultValue(firstOption);
				query.append("      fx:").append(optionName).append(" \"").append(defaultValue).append("\" ;\n");
			} catch (NoSuchMethodException | IllegalAccessException e) {
				logger.warn("Failed to get option name", e);
			}
		}
		
		query.append("    .\n");
		query.append("    ?s ?p ?o .\n");
		query.append("  }\n");
		query.append("} LIMIT 10");

		return query.toString();
	}

	/**
	 * Gets a reasonable default value for an option.
	 */
	private String getDefaultValue(OptionSection option) {
		String validValues = option.getValidValues();
		if (validValues != null && validValues.contains("true/false")) {
			return "false";
		}
		return "value";
	}

	/**
	 * Generates snippets for SPARQL functions from @FXFunctionDoc annotations
	 * and ReflectionFunctionDocRegistry for reflection-based functions.
	 */
	public List<SnippetSection> generateFunctionSnippets() {
		List<SnippetSection> snippets = new ArrayList<>();
		FunctionRegistry registry = FunctionRegistry.get();

		// Get all registered function URIs
		Iterator<String> functionUris = registry.keys();
		while (functionUris.hasNext()) {
			String uri = functionUris.next();
			
			// Only process fx: namespace functions
			if (uri.startsWith(Triplifier.FACADE_X_CONST_NAMESPACE_IRI)) {
				try {
					// Extract function name (e.g., "String.trim" from "http://sparql.xyz/facade-x/ns/String.trim")
					String functionName = uri.substring(Triplifier.FACADE_X_CONST_NAMESPACE_IRI.length());
					
					// Try reflection-based documentation first
					if (ReflectionFunctionDocRegistry.isRegistered(functionName)) {
						ReflectionFunctionDocRegistry.FunctionDocEntry docEntry = 
							ReflectionFunctionDocRegistry.getDocumentation(functionName);
						
						String code = "# " + docEntry.getDescription() + "\n" + docEntry.getExample();
						
						SnippetSection snippet = new SnippetSection(
							docEntry.getLabel(),
							code,
							docEntry.getGroup()
						);
						snippets.add(snippet);
					} else {
						// Fall back to @FXFunctionDoc annotation
						Class<?> functionClass = extractFunctionClass(registry.get(uri));
						if (functionClass != null) {
							FunctionMetadata metadata = new FunctionMetadata(uri, functionClass);
							
							String code = metadata.getExample();
							if (code == null || code.isEmpty()) {
								// Generate default example
								code = metadata.getExample();
							}
							
							String description = metadata.getDescription();
							if (!description.isEmpty()) {
								code = "# " + description + "\n" + code;
							}
							
							SnippetSection snippet = new SnippetSection(
								metadata.getLabel(),
								code,
								metadata.getGroup()
							);
							snippets.add(snippet);
						}
					}
				} catch (Exception e) {
					logger.warn("Failed to process function: {}", uri, e);
				}
			}
		}

		return snippets;
	}

	/**
	 * Generates snippets for magic properties from @MagicPropertyDoc annotations.
	 */
	public List<SnippetSection> generateMagicPropertySnippets() {
		List<SnippetSection> snippets = new ArrayList<>();
		PropertyFunctionRegistry registry = PropertyFunctionRegistry.chooseRegistry(ARQ.getContext());

		// Get all registered property URIs
		Iterator<String> propertyUris = registry.keys();
		while (propertyUris.hasNext()) {
			String uri = propertyUris.next();
			
			// Only process fx: namespace properties
			if (uri.startsWith(Triplifier.FACADE_X_CONST_NAMESPACE_IRI)) {
				try {
					Class<?> propertyClass = registry.get(uri).getClass();
					MagicPropertyMetadata metadata = new MagicPropertyMetadata(uri, propertyClass);
					
					String code = metadata.getExample();
					String description = metadata.getDescription();
					if (!description.isEmpty()) {
						code = "# " + description + "\n" + code;
					}
					
					SnippetSection snippet = new SnippetSection(
						metadata.getLabel(),
						code,
						metadata.getGroup()
					);
					snippets.add(snippet);
				} catch (Exception e) {
					logger.warn("Failed to process magic property: {}", uri, e);
				}
			}
		}

		return snippets;
	}

	/**
	 * Gets all format packages from registered triplifiers.
	 */
	private Map<Package, Set<Class<?>>> getFormatPackages() throws ClassNotFoundException {
		Map<Package, Set<Class<?>>> packageToClass = new HashMap<>();
		for (String triplifierClassName : TriplifierRegister.getInstance().getTriplifiers()) {
			Class<?> triplifierClass = Class.forName(triplifierClassName);
			if (Triplifier.class.isAssignableFrom(triplifierClass)) {
				Package p = triplifierClass.getPackage();
				Format f = p.getAnnotation(Format.class);
				if (f != null) {
					Set<Class<?>> classes = packageToClass.computeIfAbsent(p, k -> new HashSet<>());
					classes.add(triplifierClass);
				}
			}
		}
		return packageToClass;
	}

	/**
	 * Extracts the actual function class from a FunctionFactory.
	 * Uses reflection to unwrap FunctionFactoryAuto to get the underlying class.
	 */
	private Class<?> extractFunctionClass(org.apache.jena.sparql.function.FunctionFactory factory) {
		if (factory == null) {
			return null;
		}
		
		try {
			// FunctionFactoryAuto wraps the actual function class
			// Use reflection to extract it since the class is package-private
			String factoryClassName = factory.getClass().getName();
			if (factoryClassName.equals("org.apache.jena.sparql.function.FunctionFactoryAuto")) {
				// Try all fields to find the one holding the Class
				for (java.lang.reflect.Field field : factory.getClass().getDeclaredFields()) {
					field.setAccessible(true);
					Object value = field.get(factory);
					if (value instanceof Class) {
						return (Class<?>) value;
					}
				}
			}
		} catch (Exception e) {
			logger.warn("Failed to extract function class from factory: {}", factory.getClass().getName(), e);
		}
		
		// Fallback to the factory's class
		return factory.getClass();
	}

	/**
	 * Writes snippets to a JSON file.
	 */
	private void writeSnippetsToFile(List<SnippetSection> snippets, String outputFile) throws IOException {
		Configuration freemarkerCfg = getFreemarkerConfiguration();
		Template template = freemarkerCfg.getTemplate("snippets.ftlh");

		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("snippets", snippets);

		try (FileWriter writer = new FileWriter(outputFile)) {
			template.process(dataModel, writer);
		} catch (TemplateException e) {
			throw new IOException("Failed to process template", e);
		}
	}

	/**
	 * Configures FreeMarker for template processing.
	 */
	private Configuration getFreemarkerConfiguration() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
		cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "io/github/sparqlanything/fuseki");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(true);
		cfg.setWrapUncheckedExceptions(true);
		return cfg;
	}
}
