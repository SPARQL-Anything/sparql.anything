/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.engine;

import com.github.sparqlanything.engine.functions.After;
import com.github.sparqlanything.engine.functions.Backward;
import com.github.sparqlanything.engine.functions.Before;
import com.github.sparqlanything.engine.functions.Entity;
import com.github.sparqlanything.engine.functions.Forward;
import com.github.sparqlanything.engine.functions.Literal;
import com.github.sparqlanything.engine.functions.Next;
import com.github.sparqlanything.engine.functions.Previous;
import com.github.sparqlanything.engine.functions.RemoveTags;
import com.github.sparqlanything.engine.functions.Serial;
import com.github.sparqlanything.engine.functions.reflection.ReflectionFunctionFactory;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.rdf.RDFTriplifier;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.WordUtils;
import org.apache.jena.query.ARQ;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.pfunction.PropertyFunction;
import org.apache.jena.sparql.pfunction.PropertyFunctionFactory;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;

public final class FacadeX {

	private static final Logger log = LoggerFactory.getLogger(FacadeX.class);

	public final static OpExecutorFactory ExecutorFactory = new OpExecutorFactory() {
		@Override
		public OpExecutor create(ExecutionContext execCxt) {
			return new FacadeXOpExecutor(execCxt);
		}
	};

	public final static TriplifierRegister Registry = TriplifierRegister.getInstance();

	static {
		try {
			log.trace("Registering isFacadeXExtension function");

			FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "isFacadeXExtension",
					IsFacadeXExtension.class);
			enablingMagicProperties();
			enablingFunctions();
			log.trace("Registering standard triplifiers");
			Registry.registerTriplifier("com.github.sparqlanything.bib.BibtexTriplifier",
					new String[] { "bib", "bibtex" }, new String[] { "application/x-bibtex" });
			Registry.registerTriplifier("com.github.sparqlanything.xml.XMLTriplifier", new String[] { "xml" },
					new String[] { "application/xml", "text/xml" });
			Registry.registerTriplifier("com.github.sparqlanything.csv.CSVTriplifier", new String[] { "csv", "tsv", "tab" },
					new String[] { "text/csv", "text/tab-separated-values" });
			Registry.registerTriplifier("com.github.sparqlanything.html.HTMLTriplifier", new String[] { "html" },
					new String[] { "text/html" });
			Registry.registerTriplifier("com.github.sparqlanything.text.TextTriplifier", new String[] { "txt" },
					new String[] { "text/plain" });
			Registry.registerTriplifier("com.github.sparqlanything.markdown.MARKDOWNTriplifier", new String[] { "md" },
					new String[] { "text/markdown", "text/x-markdown" });
			Registry.registerTriplifier("com.github.sparqlanything.docs.DocxTriplifier", new String[] { "docx" },
					new String[] { "application/vnd.openxmlformats-officedocument.wordprocessingml.document" });
			Registry.registerTriplifier("com.github.sparqlanything.zip.TarTriplifier", new String[] { "tar" },
					new String[] { "application/x-tar" });
			Registry.registerTriplifier("com.github.sparqlanything.zip.ZipTriplifier", new String[] { "zip" },
					new String[] { "application/zip" });
			Registry.registerTriplifier("com.github.sparqlanything.binary.BinaryTriplifier",
					new String[] { "bin", "dat" }, new String[] { "application/octet-stream" });
			Registry.registerTriplifier("com.github.sparqlanything.json.JSONTriplifier", new String[] { "json" },
					new String[] { "application/json", "application/problem+json" });
			Registry.registerTriplifier("com.github.sparqlanything.yaml.YAMLTriplifier", new String[] { "yaml" },
					new String[] { "application/yaml", "text/yaml", "x-text/yaml" });
			Registry.registerTriplifier("com.github.sparqlanything.spreadsheet.SpreadsheetTriplifier",
					new String[] { "xls", "xlsx" }, new String[] { "application/vnd.ms-excel",
							"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
			Registry.registerTriplifier(RDFTriplifier.class.getCanonicalName(),
					new String[] { "rdf", "ttl", "nt", "jsonld", "owl", "trig", "nq", "trix", "trdf" },
					new String[] { "application/rdf+thrift", "application/trix+xml", "application/n-quads", "text/trig",
							"application/owl+xml", "text/turtle", "application/rdf+xml", "application/n-triples",
							"application/ld+json" });
			Registry.registerTriplifier("com.github.sparqlanything.binary.BinaryTriplifier",
					new String[] { "png", "jpeg", "jpg", "bmp", "tiff", "tif", "ico" },
					new String[] { "image/png", "image/jpeg", "image/bmp", "image/tiff", "image/vnd.microsoft.icon" });

		} catch (TriplifierRegisterException e) {
			throw new RuntimeException(e);
		}

	}

	public static void enablingMagicProperties() {
		log.trace("Enabling magic properties");
		ARQ.setTrue(ARQ.enablePropertyFunctions);
		PropertyFunctionFactory p = new PropertyFunctionFactory() {
			@Override
			public PropertyFunction create(String uri) {
				log.trace("Creating any slot");
				return new AnySlot();
			}
		};

		final PropertyFunctionRegistry reg = PropertyFunctionRegistry.chooseRegistry(ARQ.getContext());
		log.trace("Registering {} magic property", Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot");
		reg.put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot", p);
		PropertyFunctionRegistry.set(ARQ.getContext(), reg);
	}

	public static void enablingFunctions() {
		log.trace("Enabling functions");
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "entity", Entity.class);
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "literal", Literal.class);
		log.trace("Enabling collection functions");
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "previous", Previous.class);
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "next", Next.class);
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "before", Before.class);
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "after", After.class);
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "backward", Backward.class);
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "forward", Forward.class);

		log.trace("Enabling String functions");
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.trim",
				ReflectionFunctionFactory.get().makeFunction(String.class, "trim"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.substring",
				ReflectionFunctionFactory.get().makeFunction(String.class, "substring"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.indexOf",
				ReflectionFunctionFactory.get().makeFunction(String.class, "indexOf"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.startsWith",
				ReflectionFunctionFactory.get().makeFunction(String.class, "startsWith"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.endsWith",
				ReflectionFunctionFactory.get().makeFunction(String.class, "endsWith"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.endsWith",
				ReflectionFunctionFactory.get().makeFunction(String.class, "endsWith"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.replace",
				ReflectionFunctionFactory.get().makeFunction(String.class, "replace"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.strip",
				ReflectionFunctionFactory.get().makeFunction(String.class, "strip"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.stripLeading",
				ReflectionFunctionFactory.get().makeFunction(String.class, "stripLeading"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.stripTrailing",
				ReflectionFunctionFactory.get().makeFunction(String.class, "stripTrailing"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.removeTags", RemoveTags.class);

		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "DigestUtils.md2Hex",
				ReflectionFunctionFactory.get().makeFunction(DigestUtils.class, "md2Hex"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "DigestUtils.md5Hex",
				ReflectionFunctionFactory.get().makeFunction(DigestUtils.class, "md5Hex"));

		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "DigestUtils.sha1Hex",
				ReflectionFunctionFactory.get().makeFunction(DigestUtils.class, "sha1Hex"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "DigestUtils.sha256Hex",
				ReflectionFunctionFactory.get().makeFunction(DigestUtils.class, "sha256Hex"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "DigestUtils.sha384Hex",
				ReflectionFunctionFactory.get().makeFunction(DigestUtils.class, "sha384Hex"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "DigestUtils.sha512Hex",
				ReflectionFunctionFactory.get().makeFunction(DigestUtils.class, "sha512Hex"));

		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "WordUtils.capitalize",
				ReflectionFunctionFactory.get().makeFunction(WordUtils.class, "capitalize"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "WordUtils.capitalizeFully",
				ReflectionFunctionFactory.get().makeFunction(WordUtils.class, "capitalizeFully"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "WordUtils.initials",
				ReflectionFunctionFactory.get().makeFunction(WordUtils.class, "initials"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "WordUtils.swapCase",
				ReflectionFunctionFactory.get().makeFunction(WordUtils.class, "swapCase"));
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "WordUtils.uncapitalize",
				ReflectionFunctionFactory.get().makeFunction(WordUtils.class, "uncapitalize"));

		try {
			FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.toLowerCase",
					ReflectionFunctionFactory.get().makeFunction(String.class.getMethod("toLowerCase")));
			FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "String.toUpperCase",
					ReflectionFunctionFactory.get().makeFunction(String.class.getMethod("toUpperCase")));
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		try {
			FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "URLEncoder.encode", ReflectionFunctionFactory.get().makeFunction(URLEncoder.class.getMethod("encode", String.class, String.class)));
			FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "URLDecoder.decode", ReflectionFunctionFactory.get().makeFunction(URLDecoder.class.getMethod("decode", String.class, String.class)));
		} catch (NoSuchMethodException e) {
			log.error("",e);
		}

		log.trace("Enabling function `serial`");
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "serial", Serial.class);
	}
}
