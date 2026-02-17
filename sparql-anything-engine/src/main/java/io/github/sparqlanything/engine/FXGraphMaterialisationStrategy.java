package io.github.sparqlanything.engine;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.PropertyUtils;
import io.github.sparqlanything.model.Slicer;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierHTTPException;
import io.github.sparqlanything.model.TriplifierRegister;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterNullIterator;
import org.apache.jena.sparql.engine.main.QC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * Execution based on materialisation of resource into an in-memory dataset graph.
 */
public class FXGraphMaterialisationStrategy implements FXExecutionStrategy{
	private final static Logger logger = LoggerFactory.getLogger(FXGraphMaterialisationStrategy.class);
	private TriplifierRegister triplifierRegister;
	private DatasetGraphCreator dgc;
	private Properties properties;
	private ExecutionContext executionContext;
	public FXGraphMaterialisationStrategy(Properties properties, ExecutionContext execCxt) {
		this.triplifierRegister = TriplifierRegister.getInstance();
		this.dgc = new DatasetGraphCreator(execCxt);
		this.properties = properties;
		this.executionContext = execCxt;
	}

	@Override
	public QueryIterator execute(Op op, QueryIterator input) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException {
		// guess triplifier
		Triplifier t = PropertyExtractor.getTriplifier(this.properties, triplifierRegister);

		if (t == null) {
			logger.warn("No triplifier found");
			return QueryIterNullIterator.create(executionContext);
		}

		// check execution with slicing
		if (PropertyUtils.getBooleanProperty(properties, IRIArgument.SLICE)) {
			if (t instanceof Slicer) {
				logger.trace("Execute with slicing");
				return new QueryIterSlicer(executionContext, input, t, properties, op);
			} else {
				logger.warn("Slicing is not supported by triplifier: {}", t.getClass().getName());
			}
		}

		// Execute with default, bulk method
		DatasetGraph dg = dgc.getDatasetGraph(t, properties, op);
		Utils.ensureReadingTxn(dg);

		if(op instanceof OpService){
			return QC.execute(Algebra.optimize(((OpService)op).getSubOp()), input, Utils.getFacadeXExecutionContext(executionContext, properties, dg));
		}else{
			return QC.execute(op, input, ExecutionContext.create(dg));
		}
	}

	public static final FXExecutionStrategy make(Properties properties, ExecutionContext execCxt) {
		return new FXGraphMaterialisationStrategy(properties, execCxt);
	}
}
