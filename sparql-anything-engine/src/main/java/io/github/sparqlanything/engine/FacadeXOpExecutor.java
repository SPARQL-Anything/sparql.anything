/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.engine;

import io.github.sparqlanything.model.SPARQLAnythingConstants;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.ARQ;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.algebra.optimize.TransformPropertyFunction;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterAssign;
import org.apache.jena.sparql.engine.join.Join;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.util.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class FacadeXOpExecutor extends OpExecutor {

	public final static Symbol strategy = Symbol.create("facade-x-strategy");
	private static final Logger logger = LoggerFactory.getLogger(FacadeXOpExecutor.class);
	private final FXWorkerOpService fxWorkerOpService;

	private final FXWorkerOp fxWorkerOp;

	public FacadeXOpExecutor(ExecutionContext execCxt) {
		super(execCxt);
		TriplifierRegister triplifierRegister = TriplifierRegister.getInstance();
		DatasetGraphCreator dgc = new DatasetGraphCreator(execCxt);
		fxWorkerOpService = new FXWorkerOpService(triplifierRegister, dgc);
		fxWorkerOp = new FXWorkerOp(triplifierRegister, dgc);
	}


	protected QueryIterator exec(Op op, QueryIterator input) {
		if (this.execCxt.getContext().isDefined(SPARQLAnythingConstants.NO_SERVICE_MODE) && this.execCxt.getContext().getTrueOrFalse(SPARQLAnythingConstants.NO_SERVICE_MODE)) {
			try {
				this.execCxt.getContext().setFalse(SPARQLAnythingConstants.NO_SERVICE_MODE);
				return fxWorkerOp.execute(op, input, this.execCxt);
			} catch (ClassNotFoundException | NoSuchMethodException | TriplifierHTTPException |
					 InvocationTargetException | InstantiationException | IllegalAccessException | IOException |
					 UnboundVariableException e) {
				throw new RuntimeException(e);
			}
		}
		return super.exec(op, input);
	}

	protected QueryIterator execute(final OpBGP opBGP, QueryIterator input) {
		logger.trace("Execute OpBGP {}", opBGP.getPattern().toString());

		// check that the BGP is within a FacadeX-SERVICE clause
		if (this.execCxt.getClass() == FacadeXExecutionContext.class) {
			// check that the BGP contains FacadeX Magic properties
			logger.trace("FacadeX Execution context");
			List<Triple> magicPropertyTriples = Utils.getFacadeXMagicPropertyTriples(opBGP.getPattern());
			if (!magicPropertyTriples.isEmpty()) {
				logger.trace("BGP has magic properties");
				return super.execute(Utils.excludeMagicPropertyTriples(Utils.excludeFXProperties(opBGP)), executeMagicProperties(input, magicPropertyTriples, this.execCxt));
			} else {
				// execute BGP by excluding FX properties
				logger.trace("Execute BGP by excluding FX properties");
				return QC.execute(Utils.excludeFXProperties(opBGP), input, new ExecutionContext(ARQ.getContext(), execCxt.getActiveGraph(), execCxt.getDataset(), execCxt.getExecutor()));
			}
		}

		Op opTransformed = TransformPropertyFunction.transform(opBGP, this.execCxt.getContext());
		if (!opTransformed.equals(opBGP)) {
			return super.executeOp(opTransformed, input);
		}
		logger.trace("Execute with default Jena execution");

		// go with the default Jena execution
		return super.execute(opBGP, input);
	}

	protected QueryIterator execute(final OpService opService, QueryIterator input) {
		logger.trace("Execute opService {}", opService.toString());

		if(!this.execCxt.getContext().isDefined(SPARQLAnythingConstants.NO_SERVICE_MODE)) {

			// check if service iri is a variable, in case postpone the execution
			if (opService.getService().isVariable()) return Utils.postpone(opService, input, execCxt);

			// check if the service is a FacadeXURI
			if (opService.getService().isURI() && Utils.isFacadeXURI(opService.getService().getURI())) {

				try {
					// go with the FacadeX default execution
//				return executeDefaultFacadeX(opService, input);
					return fxWorkerOpService.execute(opService, input, execCxt);
				} catch (IllegalArgumentException | SecurityException | IOException | InstantiationException |
						 IllegalAccessException | InvocationTargetException | NoSuchMethodException |
						 ClassNotFoundException | TriplifierHTTPException e) {
					logger.error("An error occurred: {}", e.getMessage());
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
			logger.trace("Executing table");
			QueryIterator qIterT = e.getOpTable().getTable().iterator(execCxt);
			QueryIterator qIter = Join.join(input, qIterT, execCxt);
			return Utils.postpone(op, qIter, execCxt);
		} else if (e.getOpExtend() != null) {
			logger.trace("Executing op extend");
			QueryIterator qIter = exec(e.getOpExtend().getSubOp(), input);
			qIter = new QueryIterAssign(qIter, e.getOpExtend().getVarExprList(), execCxt, true);
			return Utils.postpone(op, qIter, execCxt);
		}
		logger.trace("Executing fake pattern {}", fakeBGP);
		return Utils.postpone(op, QC.execute(fakeBGP, input, execCxt), execCxt);
	}

	private QueryIterator executeMagicProperties(QueryIterator input, List<Triple> propFuncTriples, ExecutionContext execCxt) {
		QueryIterator input2 = input;
		for (Triple t : propFuncTriples) {
			input2 = QC.execute(Utils.getOpPropFuncAnySlot(t), input2, execCxt);
		}
		return input2;
	}

}
