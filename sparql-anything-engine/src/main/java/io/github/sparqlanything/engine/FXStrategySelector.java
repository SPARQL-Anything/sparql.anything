package io.github.sparqlanything.engine;

import io.github.sparqlanything.engine.stream.FXStreamExecutionStrategy;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.PropertyUtils;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class FXStrategySelector {
	public static final Logger L = LoggerFactory.getLogger(FXStrategySelector.class);

	private static final Set<String> streamSupportedOptions;

	static {
		streamSupportedOptions = new HashSet<>();
		for (IRIArgument option : IRIArgument.getOptions()) {
			if(!option.toString().startsWith("json") && ! option.toString().startsWith("xml"))
				streamSupportedOptions.add(option.toString());
		}

		streamSupportedOptions.remove(IRIArgument.ONDISK.toString());
		streamSupportedOptions.remove(IRIArgument.ONDISK_REUSE.toString());
	}

	private static boolean hasUnsupportedOptions(Properties properties) {
		boolean allContained = true;
		for (Object option : properties.keySet()) {
			if (!streamSupportedOptions.contains(option.toString())) {
				allContained = false;
			}
		}
		return !allContained;
	}

	public FXExecutionStrategy getStrategy(Properties p, Op op, ExecutionContext execCxt) {
		L.debug("Getting strategy for {}", op);

		if (hasUnsupportedOptions(p)) {
			L.info("Fallback to graph materialisation strategy");
			return FXGraphMaterialisationStrategy.make(p, execCxt);
		}

		// Support for `s` configuration property
		if (p.containsKey(IRIArgument.STRATEGY.toString())) {
			String strategy = p.getProperty(IRIArgument.STRATEGY.toString());
			switch (strategy) {
				case "2":
					try {
						return FXStreamExecutionStrategy.make(op, p, execCxt);
					} catch (FXStreamExecutionStrategy.CantExecException e) {
						// The user specifically asked for this strategy, we throw an exception
						throw new RuntimeException(e);
					}
				case "0":
				case "1":
					// 0 - Materialisation
					// 1 - Materialisation + Only matching triples
					return FXGraphMaterialisationStrategy.make(p, execCxt);
			}
		}
		try {
			return FXStreamExecutionStrategy.make(op, p, execCxt);
		} catch (Exception e) {
			// Always pass
		}

		L.info("Fallback to graph materialisation strategy");
		return FXGraphMaterialisationStrategy.make(p, execCxt);
	}
}
