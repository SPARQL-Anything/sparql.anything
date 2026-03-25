package io.github.sparqlanything.engine.stream;

import io.github.sparqlanything.engine.*;
import io.github.sparqlanything.fxbgp.AnalyserGrounder;
import io.github.sparqlanything.fxbgp.FXBGPAnnotation;
import io.github.sparqlanything.fxbgp.FXModel;
import io.github.sparqlanything.fxbgp.stream.CSVStreamParser;
import io.github.sparqlanything.fxbgp.stream.FXParserQueryIterator;
import io.github.sparqlanything.fxbgp.stream.FXProxyEventListener;
import io.github.sparqlanything.fxbgp.stream.FXQuerySolutionBuilder;
import io.github.sparqlanything.fxbgp.stream.FXStreamParser;
import io.github.sparqlanything.fxbgp.stream.FXTreePattern;
import io.github.sparqlanything.fxbgp.stream.SharedPathAccessor;
import io.github.sparqlanything.fxbgp.stream.JSONStreamParser;
import io.github.sparqlanything.fxbgp.stream.NotATreeException;
import io.github.sparqlanything.fxbgp.stream.StreamEventsHandler;
import io.github.sparqlanything.fxbgp.stream.XMLStreamParser;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.join.QueryIterNestedLoopJoin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class FXStreamExecutionStrategy implements FXExecutionStrategy {
	private final FXStreamParser parser;
	private final Properties properties;

	public FXStreamExecutionStrategy(FXStreamParser parser, Properties p) {
		this.parser = parser;
		this.properties = p;
	}

	@Override
	public QueryIterator execute(Op op, final QueryIterator input, ExecutionContext execCxt) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException {
		Op playOp = op;
		if (op instanceof OpService) {
			playOp = ((OpService) op).getSubOp();
		}
//		if (!((playOp instanceof OpBGP) || (playOp instanceof OpGraph))) {
//			// If we've done our checks properly before, this should not happen
//			throw new RuntimeException("Not a BGP or GraphBGP");
//		}


		try {
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
			SharedPathAccessor accessor = new SharedPathAccessor();
			int threshold = Integer.parseInt(properties.getProperty(
				FXProxyEventListener.PARALLEL_THRESHOLD_OPTION,
				String.valueOf(FXProxyEventListener.DEFAULT_PARALLEL_THRESHOLD)));
			for (FXBGPAnnotation annotation : annotations) {
				FXTreePattern tp;
				if (graphNode == null) {
					// Play with default graph
					tp = FXTreePattern.make(annotation);
				} else {
					// Play with named graph
					tp = FXTreePattern.make(annotation, graphNode);
				}
				patterns.add(new FXQuerySolutionBuilder(tp, bindings, accessor));
			}
			StreamEventsHandler handler = new StreamEventsHandler(properties, FXProxyEventListener.make(patterns, threshold, accessor));
			return new QueryIterNestedLoopJoin(input, new FXParserQueryIterator(parser, handler, bindings), execCxt);
		} catch (NotATreeException e) {
			FXStrategySelector.L.warn("Not a tree BGP (fallback on in-memory graph materialisation)", e);
			// TODO Find a way to avoid this to happen
			return FXGraphMaterialisationStrategy.make(properties).execute(op, input, execCxt);
		}
	}


	public static final FXStreamExecutionStrategy make(Op op, Properties p, ExecutionContext execCxt) throws CantExecException {

		// If Op is supported
		final Op[] testOp = {op};
		if (op instanceof OpService) {
			op.visit(new OpVisitorSkip(){
				public void visit(OpBGP opBGP) {
					testOp[0] = opBGP;
				}
			});
		}

		if ((testOp[0] instanceof OpBGP || testOp[0] instanceof OpGraph)) {

			String media = p.getProperty(IRIArgument.MEDIA_TYPE.toString());
			String location = p.getProperty(IRIArgument.LOCATION.toString());
			String type = null;
			if (media != null) {
				if (media.contains("/")) {
					type = media.split("/")[1];
				} else {
					// We can't determine the subtype
				}
			} else if (location != null) {
				type = FilenameUtils.getExtension(location);
			}
			FXStreamParser fxparser = null;
			if (type != null) {
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

			if (fxparser != null) {
				return new FXStreamExecutionStrategy(fxparser, p);
			}
		}
		throw new CantExecException();
	}

	public static class CantExecException extends Exception {
		private static final long serialVersionUID = 1L;

		public CantExecException() {
			super();
		}

		public CantExecException(String s) {
			super(s);
		}
	}
}
