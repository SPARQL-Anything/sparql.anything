package io.github.sparqlanything.engine.stream;

import io.github.sparqlanything.engine.FXExecutionStrategy;
import io.github.sparqlanything.engine.FXGraphMaterialisationStrategy;
import io.github.sparqlanything.engine.FXStrategySelector;
import io.github.sparqlanything.engine.FacadeXExecutionContext;
import io.github.sparqlanything.engine.Utils;
import io.github.sparqlanything.fxbgp.AnalyserGrounder;
import io.github.sparqlanything.fxbgp.FXBGPAnnotation;
import io.github.sparqlanything.fxbgp.FXModel;
import io.github.sparqlanything.fxbgp.stream.FXParserQueryIterator;
import io.github.sparqlanything.fxbgp.stream.FXProxyEventListener;
import io.github.sparqlanything.fxbgp.stream.FXQuerySolutionBuilder;
import io.github.sparqlanything.fxbgp.stream.FXStreamExecutor;
import io.github.sparqlanything.fxbgp.stream.FXStreamParser;
import io.github.sparqlanything.fxbgp.stream.FXStreamParserRegistry;
import io.github.sparqlanything.fxbgp.stream.FXTreePattern;
import io.github.sparqlanything.fxbgp.stream.NotATreeException;
import io.github.sparqlanything.fxbgp.stream.StreamEventsHandler;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.main.QC;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class FXStreamExecutionStrategy implements FXExecutionStrategy {
	private FXStreamParser parser;
	private FacadeXExecutionContext context;
	private Properties properties;
	public FXStreamExecutionStrategy(FXStreamParser parser, Properties p, FacadeXExecutionContext ctx) {
		this.parser = parser;
		this.context = ctx;
		this.properties = p;
	}

	@Override
	public QueryIterator execute(Op op, QueryIterator input) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException {
		Op playOp = op;
		if(op instanceof OpService){
			playOp = ((OpService) op).getSubOp();
		}
		if(!((playOp instanceof OpBGP)||(playOp instanceof OpGraph))){
			// If we've done our checks properly before, this should not happen
			throw new RuntimeException("Not a BGP or GraphBGP");
		}

		try{
			Node graphNode = null;
			OpBGP opBGP = null;
			if (playOp instanceof OpGraph) {
				try {
					graphNode = ((OpGraph) playOp).getNode();
					opBGP = (OpBGP) ((OpGraph) playOp).getSubOp();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else if (playOp instanceof OpBGP) {
				opBGP = (OpBGP) playOp;
			}
			opBGP = Utils.excludeFXProperties((OpBGP) opBGP);
			if (opBGP == null) {
				throw new RuntimeException("Only Basic Graph Patterns are supported");
			}
			AnalyserGrounder ag = new AnalyserGrounder(properties, FXModel.getFXModel());
			Set<FXBGPAnnotation> annotations = ag.annotate(opBGP, true);
			final Set<Binding> bindings = new HashSet<>();
			final Set<FXQuerySolutionBuilder> patterns = new HashSet<>();
			for (FXBGPAnnotation annotation : annotations) {
				FXTreePattern tp;
				if (graphNode == null) {
					// Play with default graph
					tp = FXTreePattern.make(annotation);
				} else {
					// Play with named graph
					tp = FXTreePattern.make(annotation, graphNode);
				}
				patterns.add(new FXQuerySolutionBuilder(tp, bindings));
			}
			StreamEventsHandler handler = new StreamEventsHandler(properties,
				FXProxyEventListener.make(patterns));
			return new FXParserQueryIterator(parser, handler, bindings);
		}catch(NotATreeException e){
			FXStrategySelector.L.warn("Not a tree BGP (fallback on in-memory graph materialisation)", e);
			// TODO Find a way to avoid this to happen
			return FXGraphMaterialisationStrategy.make(properties, context).execute(op, input);
		}
	}
}
