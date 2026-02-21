package io.github.sparqlanything.engine;

import io.github.sparqlanything.engine.stream.FXStreamExecutionStrategy;
import io.github.sparqlanything.fxbgp.stream.CSVStreamParser;
import io.github.sparqlanything.fxbgp.stream.FXStreamParser;
import io.github.sparqlanything.fxbgp.stream.JSONStreamParser;
import io.github.sparqlanything.fxbgp.stream.XMLStreamParser;
import io.github.sparqlanything.model.IRIArgument;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class FXStrategySelector {
	public static final Logger L = LoggerFactory.getLogger(FXStrategySelector.class);
	public FXExecutionStrategy getStrategy(Properties p, Op op, ExecutionContext execCxt){
		L.info("Getting strategy for {}", op);

		// Support for `s` configuration property
		if(p.containsKey(IRIArgument.STRATEGY.toString())) {
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
			try{
				return FXStreamExecutionStrategy.make(op, p, execCxt);
			}catch(Exception e){
				// Always pass
			}

		L.info("Fallback to graph materialisation strategy");
		return FXGraphMaterialisationStrategy.make(p, execCxt);
	}
}
