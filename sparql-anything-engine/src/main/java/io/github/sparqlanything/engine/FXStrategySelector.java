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

	private static final Set<String> streamSupportedOptions = new HashSet<>();

	static {

		// General
		streamSupportedOptions.add("charset");
		streamSupportedOptions.add("s3.region");
		streamSupportedOptions.add("metadata");
		streamSupportedOptions.add("read-from-std-in");
		streamSupportedOptions.add("blank-nodes");
		streamSupportedOptions.add("from-archive");
		streamSupportedOptions.add("generate-predicate-labels");
		streamSupportedOptions.add("triplifier");
		streamSupportedOptions.add("s3.bucket-name");
		streamSupportedOptions.add("content");
		streamSupportedOptions.add("use-cache");
		streamSupportedOptions.add("s3.secret-key");
		streamSupportedOptions.add("trim-strings");
		streamSupportedOptions.add("s3.endpoint");
		streamSupportedOptions.add("opservice.silent");
		streamSupportedOptions.add("slice");
		streamSupportedOptions.add("null-string");
		streamSupportedOptions.add("audit");
		streamSupportedOptions.add("root");
		streamSupportedOptions.add("media-type");
		streamSupportedOptions.add("use-rdfs-member");
		streamSupportedOptions.add("query");
		streamSupportedOptions.add("command");
		streamSupportedOptions.add("s3.access-key");
		streamSupportedOptions.add("annotate-triples-with-slot-keys");
		streamSupportedOptions.add("s3.key");
		streamSupportedOptions.add("archive-format");
		streamSupportedOptions.add("namespace");
		streamSupportedOptions.add("location");
		streamSupportedOptions.add("strategy");

		// CSV
		streamSupportedOptions.add("csv.headers");
		streamSupportedOptions.add("csv.headers-row");
		streamSupportedOptions.add("csv.format");
		streamSupportedOptions.add("csv.delimiter");
		streamSupportedOptions.add("csv.quote-char");
		streamSupportedOptions.add("csv.null-string");

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
