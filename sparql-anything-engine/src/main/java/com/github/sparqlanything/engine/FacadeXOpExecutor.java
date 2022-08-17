/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.engine;

import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Slicer;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.core.DatasetGraph;
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
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

public class FacadeXOpExecutor extends OpExecutor {

	public final static Symbol strategy = Symbol.create("facade-x-strategy");
	private static final Logger logger = LoggerFactory.getLogger(FacadeXOpExecutor.class);
	private final DatasetGraphCreator dgc;
	private final TriplifierRegister triplifierRegister;

	public FacadeXOpExecutor(ExecutionContext execCxt) {
		super(execCxt);
		triplifierRegister = TriplifierRegister.getInstance();
		dgc = new DatasetGraphCreator(execCxt);
	}

	protected QueryIterator execute(final OpService opService, QueryIterator input) {
		// check if service iri is a variable, in case postpone the execution
		if (opService.getService().isVariable()) return Utils.postpone(opService, input, execCxt);

		// check if the service is a FacadeXURI
		if (opService.getService().isURI() && Utils.isFacadeXURI(opService.getService().getURI())) {
			try {
				// go with the FacadeX default execution
				return executeDefaultFacadeX(opService, input);
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

	private FacadeXExecutionContext getFacadeXExecutionContext(Properties p, DatasetGraph dg) {
		FacadeXExecutionContext ec;
		if (p.containsKey(IRIArgument.ONDISK.toString())) {
			ec = new FacadeXExecutionContext(new ExecutionContext(execCxt.getContext(), dg.getUnionGraph(), dg, execCxt.getExecutor()));
		} else {
			ec = new FacadeXExecutionContext(new ExecutionContext(execCxt.getContext(), dg.getDefaultGraph(), dg, execCxt.getExecutor()));
		}
		return ec;
	}


	private QueryIterator executeMagicProperties(QueryIterator input, List<Triple> propFuncTriples) {
		QueryIterator input2 = input;

		for (Triple t : propFuncTriples) {
			input2 = QC.execute(Utils.getOpPropFuncAnySlot(t), input2, execCxt);
		}
		return input2;
	}

	protected QueryIterator execute(final OpBGP opBGP, QueryIterator input) {
		// check that the BGP is within a FacadeX-SERVICE clause
		if (this.execCxt.getClass() == FacadeXExecutionContext.class) {
			// check that the BGP contains FacadeX Magic properties
			Utils.ensureReadingTxn(this.execCxt.getDataset());
			List<Triple> propFuncTriples = Utils.getPropFuncTriples(opBGP.getPattern());
			if (!propFuncTriples.isEmpty()) {
				// execute magic properties and bgp without FacadeX magic properties
				return super.execute(Utils.excludeOpPropFunction(Utils.excludeFXProperties(opBGP)), executeMagicProperties(input, propFuncTriples));
			} else {
				// execute BGP by excluding FX properties
				return QC.execute(Utils.excludeFXProperties(opBGP), input, new ExecutionContext(this.execCxt.getDataset()));
			}
		}
		// go with the default Jena execution
		return super.execute(opBGP, input);
	}


	protected QueryIterator executeDefaultFacadeX(OpService opService, QueryIterator input) throws TriplifierHTTPException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, UnboundVariableException {

		// extract properties from service URI
		Properties p = PropertyUtils.extractPropertiesFromOp(opService);

		// guess triplifier
		Triplifier t = PropertyUtils.getTriplifier(p, triplifierRegister);

		if (t == null) {
			logger.warn("No triplifier found");
			return QueryIterNullIterator.create(execCxt);
		}

		// check execution with slicing
		if (Triplifier.getSliceArgument(p)) {
			if (t instanceof Slicer) {
				logger.trace("Execute with slicing");
				return new QueryIterSlicer(execCxt, input, t, p, opService);
			} else {
				logger.warn("Slicing is not supported by triplifier: {}", t.getClass().getName());
			}
		}

		// Execute with default, bulk method
		DatasetGraph dg = dgc.getDatasetGraph(t, p, opService.getSubOp());
		Utils.ensureReadingTxn(dg);

		return QC.execute(opService.getSubOp(), input, getFacadeXExecutionContext(p, dg));

	}

}
