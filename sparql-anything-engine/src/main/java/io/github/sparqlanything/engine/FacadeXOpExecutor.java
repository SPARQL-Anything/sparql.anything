/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.github.sparqlanything.engine;

import io.github.sparqlanything.engine.stream.FXStreamExecutionStrategy;
import io.github.sparqlanything.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.algebra.optimize.TransformPropertyFunction;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterAssign;
import org.apache.jena.sparql.engine.iterator.QueryIterNullIterator;
import org.apache.jena.sparql.engine.join.Join;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.util.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class FacadeXOpExecutor extends OpExecutor {

	public final static Symbol strategy = Symbol.create("facade-x-strategy");
	private static final Logger L = LoggerFactory.getLogger(FacadeXOpExecutor.class);

	public FacadeXOpExecutor(ExecutionContext execCxt) {
		super(execCxt);
	}

	protected QueryIterator exec(Op op, QueryIterator input) {
		if (this.execCxt.getContext().isDefined(SPARQLAnythingConstants.NO_SERVICE_MODE) && this.execCxt.getContext().getTrueOrFalse(SPARQLAnythingConstants.NO_SERVICE_MODE)) {
			// If no-service-mode = true, then set no-service-mode=false and execute with the worker
			// Otherwise, proceed with Jena default
			try {
				this.execCxt.getContext().setFalse(SPARQLAnythingConstants.NO_SERVICE_MODE);
				return extractPropertiesAndSelectStrategy(op, input);
			} catch (ClassNotFoundException | NoSuchMethodException | TriplifierHTTPException |
					 InvocationTargetException | InstantiationException | URISyntaxException | IllegalAccessException |
					 IOException |
					 UnboundVariableException e) {
				throw new RuntimeException(e);
			}
		}
		return super.exec(op, input);
	}

	protected QueryIterator execute(final OpGraph opGraph, QueryIterator input) {
		if (this.execCxt instanceof FacadeXExecutionContext fxExecutionContext) {
			try {
				return executeStrategy(fxExecutionContext, opGraph, input);
			} catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
					 IllegalAccessException | NoSuchMethodException | TriplifierHTTPException | IOException e) {
				throw new RuntimeException(e);
			}
		}
		return super.execute(opGraph, input);
	}


	protected QueryIterator execute(final OpPropFunc opPropFunc, QueryIterator input) {
		if (this.execCxt instanceof FacadeXExecutionContext fxExecutionContext) {
			try {
//				return fxExecutionContext.getExecutionStrategy().execute(opPropFunc, input, this.execCxt);
				return executeStrategy(fxExecutionContext, opPropFunc, input);
			} catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
					 IllegalAccessException | NoSuchMethodException | TriplifierHTTPException | IOException e) {
				throw new RuntimeException(e);
			}
		}
		return super.execute(opPropFunc, input);
	}

	protected QueryIterator execute(final OpBGP opBGP, QueryIterator input) {
		L.trace("Execute OpBGP {}", opBGP.getPattern().toString());

		// check that the BGP is within a FacadeX context (either the BGP is in a FX Service clause or is in no-service-mode)
		if (this.execCxt instanceof FacadeXExecutionContext fxExecutionContext) {

			// extract possible magic properties
			List<Triple> magicPropertyTriples = Utils.getFacadeXMagicPropertyTriples(opBGP.getPattern());

			// exclude fx properties from the bgp to execute
			OpBGP opBGPToExecute = Utils.excludeFXProperties(opBGP);
			QueryIterator inputForNextExecution = input;

			// check that the BGP contains FacadeX Magic properties
			if (!magicPropertyTriples.isEmpty()) {

				// exclude magic properties from the bgp to execute
				opBGPToExecute = Utils.excludeMagicPropertyTriples(opBGPToExecute);

				// execute magic properties on input and pass the result as input for the next operation
				inputForNextExecution = executeMagicProperties(input, magicPropertyTriples, fxExecutionContext);
			}

			try {
				return executeStrategy(fxExecutionContext, opBGPToExecute, inputForNextExecution);
			} catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
					 IllegalAccessException | NoSuchMethodException | TriplifierHTTPException | IOException e) {
				throw new RuntimeException(e);
			}
		}

		// rewrite a property function with the call to the corresponding op
		Op opTransformed = TransformPropertyFunction.transform(opBGP, this.execCxt.getContext());
		if (!opTransformed.equals(opBGP)) {
			return super.executeOp(opTransformed, input);
		}

		// execute with jena default
		return super.execute(opBGP, input);
	}

	private  QueryIterator executeStrategy(FacadeXExecutionContext fxExecutionContext, Op op, QueryIterator inputForNextExecution) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException {
		if (fxExecutionContext.getExecutionStrategy() instanceof FXStreamExecutionStrategy) {
			return fxExecutionContext.getExecutionStrategy().execute(op, inputForNextExecution, fxExecutionContext);
		} else {
			return QC.execute(op, inputForNextExecution, ExecutionContext.create(fxExecutionContext.getDataset()));
		}
	}

	protected QueryIterator execute(final OpService opService, QueryIterator input) {
		L.trace("Execute opService {}", opService.toString());

		if (!this.execCxt.getContext().isDefined(SPARQLAnythingConstants.NO_SERVICE_MODE)) {

			// check if service iri is a variable, in case postpone the execution
			if (opService.getService().isVariable()) return Utils.postpone(opService, input, execCxt);

			// check if the service is a FacadeXURI
			if (Utils.isFacadeXServiceNode(opService)) {

				try {
					// go with the FacadeX default execution
					return extractPropertiesAndSelectStrategy(opService, input);
				} catch (IllegalArgumentException | SecurityException | IOException | InstantiationException |
						 IllegalAccessException | InvocationTargetException | NoSuchMethodException |
						 ClassNotFoundException | URISyntaxException | TriplifierHTTPException e) {
					L.error("An error occurred: {}", e.getMessage());
					throw new RuntimeException(e);
				} catch (UnboundVariableException e) {
					// manage the case of properties are passed via BGP and there are variables in it
					return catchUnboundVariableException(opService, e.getOpBGP(), input, e);
				}
			}
		}

		// go with the default Jena execution
		return super.execute(opService, input);
	}

	private QueryIterator catchUnboundVariableException(Op op, OpBGP opBGP, QueryIterator input, UnboundVariableException e) {
		// Proceed with the next operation
		OpBGP fakeBGP = Utils.extractFakePattern(opBGP);
		if (e.getOpTable() != null) {
			L.trace("Executing table");
			QueryIterator qIterT = e.getOpTable().getTable().iterator(execCxt);
			QueryIterator qIter = Join.join(input, qIterT, execCxt);
			return Utils.postpone(op, qIter, execCxt);
		} else if (e.getOpExtend() != null) {
			L.trace("Executing op extend");
			QueryIterator qIter = exec(e.getOpExtend().getSubOp(), input);
			qIter = new QueryIterAssign(qIter, e.getOpExtend().getVarExprList(), execCxt, true);
			return Utils.postpone(op, qIter, execCxt);
		}
		L.trace("Executing fake pattern {}", fakeBGP);
		return Utils.postpone(op, QC.execute(fakeBGP, input, execCxt), execCxt);
	}

	private QueryIterator executeMagicProperties(QueryIterator input, List<Triple> propFuncTriples, ExecutionContext execCxt) {
		QueryIterator input2 = input;
		for (Triple t : propFuncTriples) {
			input2 = QC.execute(Utils.getOpPropFuncAnySlot(t), input2, execCxt);
		}
		return input2;
	}


	private QueryIterator extractPropertiesAndSelectStrategy(Op op, QueryIterator input) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException, UnboundVariableException, URISyntaxException {

		L.debug("execute {}", op);

		// extract properties from service URI
		Properties p = new Properties();

		// first extract from execution context
		PropertyExtractor.extractPropertiesFromExecutionContext(this.execCxt, p);

		if (op instanceof OpService) {

			// possibly extract properties from opservice
			PropertyExtractor.extractProperties(p, (OpService) op);

			// Possibly execute reused queries
			if (PropertyUtils.hasProperty(p, IRIArgument.QUERY))
				return executeReusedQuery((OpService) op, p, input);
		}

		// Possibly read from STD in
		PropertyExtractor.readFromStdIn(p);


		if (L.isDebugEnabled()) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			p.list(pw);
			L.debug("Properties: \n{}", sw);
		}

		Op opToExecute = op;
		if (op instanceof OpService opService) {
			opToExecute = opService.getSubOp();
		}

		// Select strategy
		FXExecutionStrategy strategy = new FXStrategySelector().getStrategy(p, op, this.execCxt);
		FacadeXExecutionContext facadeXExecutionContext = Utils.getFacadeXExecutionContext(this.execCxt, p, DatasetGraphFactory.createGeneral(), strategy);

		// if is materialisation strategy, then create dataset graph and wrap into a fxExecutionContext
		if (strategy instanceof FXGraphMaterialisationStrategy) {
			return strategy.execute(opToExecute, input, facadeXExecutionContext);
		}

		// if is a stream strategy, postpone execution
		return QC.execute(opToExecute, input, facadeXExecutionContext);
	}

	private QueryIterator executeReusedQuery(OpService opService, Properties properties, QueryIterator input) throws URISyntaxException, IOException {
		L.debug("executeReusedQuery");
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource(PropertyUtils.getStringProperty(properties, IRIArgument.QUERY))).toURI(), StandardCharsets.UTF_8);
		Query query = QueryFactory.create(queryStr);
		Op op = Algebra.optimize(Algebra.compile(query));
		if (query.isSelectType()) {
			return QC.execute(op, input, this.execCxt);
		} else if (query.isConstructQuad()) {
			QueryExecution queryExecution = QueryExecutionFactory.create(query, this.execCxt.getDataset());
			Dataset dataset = queryExecution.execConstructDataset();
			return QC.execute(opService.getSubOp(), input, FacadeXExecutionContext.create(dataset.asDatasetGraph()));
		} else if (query.isConstructType()) {
			QueryExecution queryExecution = QueryExecutionFactory.create(query, this.execCxt.getDataset());
			Model result = queryExecution.execConstruct();
			return QC.execute(opService.getSubOp(), input, FacadeXExecutionContext.createForGraph(result.getGraph()));
		}
		return QueryIterNullIterator.create(this.execCxt);
	}

}
