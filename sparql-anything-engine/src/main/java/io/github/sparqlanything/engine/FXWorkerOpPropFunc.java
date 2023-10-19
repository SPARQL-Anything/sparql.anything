package io.github.sparqlanything.engine;

import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.main.QC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class FXWorkerOpPropFunc extends FXWorker<OpPropFunc> {

	private static final Logger logger = LoggerFactory.getLogger(FXWorkerOpPropFunc.class);

	public FXWorkerOpPropFunc(TriplifierRegister tr, DatasetGraphCreator dgc){
		super(tr, dgc);
	}

	@Override
	public QueryIterator execute(OpPropFunc op, QueryIterator input, ExecutionContext executionContext, DatasetGraph dg, Properties p) {
		return QC.execute(op, input, Utils.getFacadeXExecutionContext(executionContext, p, dg));
	}

	@Override
	public void extractProperties(Properties p, OpPropFunc op) throws UnboundVariableException {
		// Do nop
	}

}
