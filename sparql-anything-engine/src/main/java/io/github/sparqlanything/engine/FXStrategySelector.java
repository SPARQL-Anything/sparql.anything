package io.github.sparqlanything.engine;

import org.apache.jena.sparql.engine.ExecutionContext;

import java.util.Properties;

public class FXStrategySelector {
	public FXExecutionStrategy getStrategy(Properties p, ExecutionContext execCxt){
		return FXGraphMaterialisationStrategy.make(p, execCxt);
	}
}
