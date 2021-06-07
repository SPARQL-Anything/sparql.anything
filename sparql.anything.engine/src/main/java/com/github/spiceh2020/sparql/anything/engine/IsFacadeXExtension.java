package com.github.spiceh2020.sparql.anything.engine;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsFacadeXExtension extends FunctionBase1 {
	
	private static final Logger logger = LoggerFactory.getLogger(IsFacadeXExtension.class);

	public static boolean isFacadeXExtension(String v) {
		logger.trace("isFacadeXExtension({})",v);
		return FilenameUtils.isExtension(v, FacadeX.Registry.getRegisteredExtensions());
	}

	@Override
	public NodeValue exec(NodeValue v) {
		return NodeValue.makeBoolean(isFacadeXExtension(v.asNode().getLiteralValue().toString()));
	}

}
