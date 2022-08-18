package com.github.sparqlanything.engine;

import com.github.sparqlanything.facadeiri.FacadeIRIParser;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.zip.FolderTriplifier;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class PropertyUtils {

	private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

	static int detectStrategy(Properties p, ExecutionContext execCxt) {
		Integer strategy = null;
		// Local value for strategy?
		String localStrategy = p.getProperty(IRIArgument.STRATEGY.toString());
		// Global value for strategy?
		Integer globalStrategy = execCxt.getContext().get(FacadeXOpExecutor.strategy);
		if (localStrategy != null) {
			if (globalStrategy != null) {
				logger.warn("Local strategy {} overriding global strategy {}", localStrategy, globalStrategy);
			}
			strategy = Integer.parseInt(localStrategy);
		} else if (globalStrategy != null) {
			strategy = globalStrategy;
		} else {
			// Defaul strategy
			strategy = 1;
		}
		return strategy;
	}


	static Triplifier getTriplifier(Properties p, TriplifierRegister triplifierRegister) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Triplifier t;

		if (!p.containsKey(IRIArgument.LOCATION.toString()) && !p.containsKey(IRIArgument.CONTENT.toString()) && !p.containsKey(IRIArgument.COMMAND.toString())) {
			logger.error("Neither location nor content provided");
//			throw new RuntimeException("Neither location nor content provided");
			return null;
		}

		if (p.containsKey(IRIArgument.TRIPLIFIER.toString())) {
			logger.trace("Triplifier enforced");
			t = (Triplifier) Class.forName(p.getProperty(IRIArgument.TRIPLIFIER.toString())).getConstructor().newInstance();
		} else if (p.containsKey(IRIArgument.MEDIA_TYPE.toString())) {
			logger.trace("MimeType enforced");
			t = (Triplifier) Class.forName(triplifierRegister.getTriplifierForMimeType(p.getProperty(IRIArgument.MEDIA_TYPE.toString()))).getConstructor().newInstance();
		} else if (p.containsKey(IRIArgument.LOCATION.toString())) {
			String urlLocation = p.getProperty(IRIArgument.LOCATION.toString());
			File f = new File(p.get(IRIArgument.LOCATION.toString()).toString().replace("file://", ""));

			logger.trace("Use location {}, exists on local FS? {}, is directory? {}", f.getAbsolutePath(), f.exists(), f.isDirectory());

			if (f.exists() && f.isDirectory()) {
				logger.trace("Return folder triplifier");
				t = new FolderTriplifier();
			} else if (IsFacadeXExtension.isFacadeXExtension(p.get(IRIArgument.LOCATION.toString()).toString())) {
				logger.trace("Guessing triplifier using file extension ");
				String tt = triplifierRegister.getTriplifierForExtension(FilenameUtils.getExtension(urlLocation));
				logger.trace("Guessed extension: {} :: {} ", FilenameUtils.getExtension(urlLocation), tt);
				t = (Triplifier) Class.forName(tt).getConstructor().newInstance();
			} else {
				return null;
			}

		} else {
			logger.trace("No location provided, using the Text triplifier");
			t = (Triplifier) Class.forName("com.github.sparqlanything.text.TextTriplifier").getConstructor().newInstance();
		}
		return t;
	}

	private static void extractPropertiesFromBGP(Properties properties, OpBGP bgp) throws UnboundVariableException {
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES)) {
				if (t.getObject().isURI()) {
					properties.put(t.getPredicate().getURI().replace(Triplifier.FACADE_X_CONST_NAMESPACE_IRI, ""), t.getObject().getURI());
				} else if (t.getObject().isLiteral()) {
					properties.put(t.getPredicate().getURI().replace(Triplifier.FACADE_X_CONST_NAMESPACE_IRI, ""), t.getObject().getLiteral().getValue().toString());
				} else if (t.getObject().isVariable()) {
					throw new UnboundVariableException(t.getObject().getName(), bgp);
				}
			}
		}
	}


	static Properties extractPropertiesFromOp(Op op) throws UnboundVariableException {
		Properties properties = new Properties();

		if (op instanceof OpBGP) {
			extractPropertiesFromBGP(properties, (OpBGP) op);
		} else if (op instanceof OpService) {
			OpService opService = (OpService) op;
			String url = opService.getService().getURI();
			// Parse IRI only if contains properties
			if (!url.equals(FacadeIRIParser.SPARQL_ANYTHING_URI_SCHEMA)) {
				FacadeIRIParser p = new FacadeIRIParser(url);
				properties = p.getProperties();
			} else {
				properties = new Properties();
			}

			// Setting defaults
			if (!properties.containsKey(IRIArgument.NAMESPACE.toString())) {
				logger.trace("Setting default value for namespace: {}", Triplifier.XYZ_NS);
				properties.setProperty(IRIArgument.NAMESPACE.toString(), Triplifier.XYZ_NS);
			}
			// Setting silent
			if (opService.getSilent()) {
				// we can only see if silent was specified at the OpService so we need to stash
				// a boolean
				// at this point so we can use it when we triplify further down the Op tree
				properties.setProperty(IRIArgument.OP_SERVICE_SILENT.toString(), "true");
			}

			Op next = opService.getSubOp();
			FXBGPFinder vis = new FXBGPFinder();
			next.visit(vis);
			logger.trace("Has Table {}", vis.hasTable());

			if (vis.getBGP() != null) {
				try {
					extractPropertiesFromBGP(properties, vis.getBGP());
				} catch (UnboundVariableException e) {
					if (vis.hasTable()) {
						logger.trace(vis.getOpTable().toString());
						logger.trace("BGP {}", vis.getBGP());
						logger.trace("Contains variable names {}", vis.getOpTable().getTable().getVarNames().contains(e.getVariableName()));
						if (vis.getOpTable().getTable().getVarNames().contains(e.getVariableName())) {
							e.setOpTable(vis.getOpTable());
						}
					}

					if (vis.getOpExtend() != null) {
						logger.trace("OpExtend {}", vis.getOpExtend());
						for (Var var : vis.getOpExtend().getVarExprList().getVars()) {
							if (var.getName().equals(e.getVariableName())) {
								e.setOpExtend(vis.getOpExtend());
							}
						}
					}

					throw e;
				}
				logger.trace("Number of properties {}", properties.size());
			} else {
				logger.trace("Couldn't find OpGraph");
			}
		}

		return properties;
	}
}
