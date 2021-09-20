package com.github.spiceh2020.sparql.anything.engine;

import com.github.spiceh2020.sparql.anything.engine.functions.After;
import com.github.spiceh2020.sparql.anything.engine.functions.Before;
import com.github.spiceh2020.sparql.anything.engine.functions.Next;
import com.github.spiceh2020.sparql.anything.engine.functions.Previous;
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

import com.github.spiceh2020.sparql.anything.model.Triplifier;
import com.github.spiceh2020.sparql.anything.rdf.RDFTriplifier;

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
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.xml.XMLTriplifier",
					new String[] { "xml" }, new String[] { "application/xml" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.csv.CSVTriplifier",
					new String[] { "csv" }, new String[] { "text/csv" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.html.HTMLTriplifier",
					new String[] { "html" }, new String[] { "text/html" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.text.TextTriplifier",
					new String[] { "txt" }, new String[] { "text/plain" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.zip.TarTriplifier",
					new String[] { "tar" }, new String[] { "application/x-tar" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.zip.ZipTriplifier",
					new String[] { "zip" }, new String[] { "application/zip" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.binary.BinaryTriplifier",
					new String[] { "bin", "dat" }, new String[] { "application/octet-stream" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.json.JSONTriplifier",
					new String[] { "json" }, new String[] { "application/json", "application/problem+json" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.spreadsheet.SpreadsheetTriplifier",
					new String[] { "xls", "xlsx" }, new String[] { "application/vnd.ms-excel",
							"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
			Registry.registerTriplifier(RDFTriplifier.class.getCanonicalName(),
					new String[] { "rdf", "ttl", "nt", "jsonld", "owl", "trig", "nq", "trix", "trdf" },
					new String[] { "application/rdf+thrift", "application/trix+xml", "application/n-quads", "text/trig",
							"application/owl+xml", "text/turtle", "application/rdf+xml", "application/n-triples",
							"application/ld+json" });
			Registry.registerTriplifier("com.github.spiceh2020.sparql.anything.binary.BinaryTriplifier",
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
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "previous", Previous.class) ;
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "next", Next.class) ;
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "before", Before.class) ;
		FunctionRegistry.get().put(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "after", After.class) ;
	}
}
