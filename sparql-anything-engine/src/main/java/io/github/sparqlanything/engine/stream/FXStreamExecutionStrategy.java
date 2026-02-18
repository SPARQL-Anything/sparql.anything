package io.github.sparqlanything.engine.stream;

import io.github.sparqlanything.engine.FXExecutionStrategy;
import io.github.sparqlanything.engine.FXGraphMaterialisationStrategy;
import io.github.sparqlanything.fxbgp.stream.FXStreamExecutor;
import io.github.sparqlanything.fxbgp.stream.FXStreamParser;
import io.github.sparqlanything.fxbgp.stream.NotATreeException;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.main.QC;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class FXStreamExecutionStrategy implements FXExecutionStrategy {
	private FXStreamParser parser;
	private FXStreamExecutor executor;
	private ExecutionContext context;
	private Properties properties;
	public FXStreamExecutionStrategy(FXStreamParser parser, Properties p, ExecutionContext ctx) {
		this.parser = parser;
		this.executor = new FXStreamExecutor();
		this.context = ctx;
		this.properties = p;
	}

	@Override
	public QueryIterator execute(Op op, QueryIterator input) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException {
		if(op instanceof OpBGP || op instanceof OpGraph){
			try{
				return executor.exec(op, properties);
			}catch(NotATreeException e){
				// TODO Find a way to avoid this to happen
				return new FXGraphMaterialisationStrategy(properties,
					context).execute(op, input);
			}
		}
		return QC.execute(op,input,context);
	}
}
