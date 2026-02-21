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

		// Add support for `s` configuration property
		// 1 - Materialisation
		// 2 - Materialisation + Slicing
		// 3 - Streaming

		// If Op is supported
		Op testOp = op;
		if(op instanceof OpService){
			testOp = ((OpService) op).getSubOp();
		}
		// If it does have a mime type, use that

		if((testOp instanceof OpBGP || testOp instanceof OpGraph)){
			String media = p.getProperty(IRIArgument.MEDIA_TYPE.toString());
			String location = p.getProperty(IRIArgument.LOCATION.toString());
			String type = null;
			if(media != null){
				if(media.contains("/")) {
					type = media.split("/")[1];
				}else{
					// We can't determine the subtype
				}
			}else if(location != null){
				type = FilenameUtils.getExtension(location);
			}
			FXStreamParser fxparser = null;
			if(type != null){
				switch (type) {
					case "json":
						fxparser = new JSONStreamParser(p);
						break;
					case "xml":
						fxparser = new XMLStreamParser(p);
						break;
					case "csv":
						fxparser = new CSVStreamParser(p);
				}
			}

			if(fxparser != null){
				L.info("Select stream execution strategy");
				if(!(execCxt instanceof FacadeXExecutionContext)){
					execCxt = new FacadeXExecutionContext(execCxt);
				}
				return new FXStreamExecutionStrategy(fxparser, p,(FacadeXExecutionContext) execCxt);
			}
		}
		L.info("Fallback to graph materialisation strategy");
		return FXGraphMaterialisationStrategy.make(p, execCxt);
	}
}
