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

import com.github.sparqlanything.facadeiri.FacadeIRIParser;
import com.github.sparqlanything.model.IRIArgument;
import com.github.sparqlanything.model.Slicer;
import com.github.sparqlanything.model.Triplifier;
import com.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.TxnType;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.table.TableUnit;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.*;
import org.apache.jena.sparql.engine.join.Join;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.pfunction.PropFuncArg;
import org.apache.jena.sparql.util.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FacadeXOpExecutor extends OpExecutor {

	// TODO
//	private final static Symbol audit = Symbol.create("facade-x-audit");
	public final static Symbol strategy = Symbol.create("facade-x-strategy");
	private static final Logger logger = LoggerFactory.getLogger(FacadeXOpExecutor.class);
	private final DatasetGraphCreator dgc;
	private final TriplifierRegister triplifierRegister;

	public FacadeXOpExecutor(ExecutionContext execCxt) {
		super(execCxt);
		triplifierRegister = TriplifierRegister.getInstance();
		dgc = new DatasetGraphCreator(execCxt);
	}

	private boolean isFacadeXURI(String uri) {
		return uri.startsWith(FacadeIRIParser.SPARQL_ANYTHING_URI_SCHEMA);
	}

	private List<Triple> getPropFuncTriples(BasicPattern e) {
		List<Triple> result = new ArrayList<>();
		e.forEach(t -> {
			if (t.getPredicate().isURI() && t.getPredicate().getURI().equals(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot")) {
				result.add(t);
			}
		});
		return result;
	}

	private OpPropFunc getOpPropFuncAnySlot(Triple t) {
		return new OpPropFunc(NodeFactory.createURI(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot"), new PropFuncArg(t.getSubject()), new PropFuncArg(t.getObject()), OpTable.create(new TableUnit()));
	}

	protected QueryIterator execute(final OpService opService, QueryIterator input) {
		logger.trace("SERVICE uri: {} {}", opService.getService(), opService);
		if (opService.getService().isVariable()) return postponeService(opService, input);
		if (opService.getService().isURI() && isFacadeXURI(opService.getService().getURI())) {
			logger.trace("Facade-X uri: {}", opService.getService());
			try {

				Properties p = PropertyUtils.getProperties(opService.getService().getURI(), opService);
				Triplifier t = PropertyUtils.getTriplifier(p, triplifierRegister);

				if (t == null) {
					logger.warn("No triplifier found");
					return QueryIterNullIterator.create(execCxt);
				}

				if (Triplifier.getSliceArgument(p)) {
					// Execute with slicing
					if (t instanceof Slicer) {
						logger.trace("Execute with slicing");
						return new QueryIterSlicer(execCxt, input, t, p, opService);
					} else {
						logger.warn("Slicing is not supported by triplifier: {}", t.getClass().getName());
					}
				}

				// Execute with default, bulk method
				DatasetGraph dg = dgc.getDatasetGraph(t, p, opService.getSubOp());
				logger.trace("Execute with default method dg is in transaction? {} transaction type {}", dg.isInTransaction(), dg.transactionType());
				if (!dg.isInTransaction()) {
					logger.debug("begin read txn");
					dg.begin(TxnType.READ);
				}

				return QC.execute(opService.getSubOp(), input, getFacadeXExecutionContext(p, dg));

			} catch (IllegalArgumentException | SecurityException | IOException | InstantiationException |
					 IllegalAccessException | InvocationTargetException | NoSuchMethodException |
					 ClassNotFoundException | TriplifierHTTPException e) {
				logger.error("An error occurred: {}", e.getMessage());
				throw new RuntimeException(e);
			} catch (UnboundVariableException e) {
				// Proceed with the next operation
				OpBGP fakeBGP = extractFakePattern(e.getOpBGP());
				if (e.getOpTable() != null) {
					logger.trace("Executing table");
					QueryIterator qIterT = e.getOpTable().getTable().iterator(execCxt);
					QueryIterator qIter = Join.join(input, qIterT, execCxt);
					return postponeService(opService, qIter);
				} else if (e.getOpExtend() != null) {
					logger.trace("Executing op extend");
					QueryIterator qIter = exec(e.getOpExtend().getSubOp(), input);
					qIter = new QueryIterAssign(qIter, e.getOpExtend().getVarExprList(), execCxt, true);
					return postponeService(opService, qIter);
				}
				logger.trace("Executing fake pattern {}", fakeBGP);
				return postponeService(opService, QC.execute(fakeBGP, input, execCxt));
			}
		}
		logger.trace("Not a Variable and not a IRI: {}", opService.getService());
		return super.execute(opService, input);
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

	private QueryIterator postponeService(final OpService opService, QueryIterator input) {
		logger.trace("is variable: {}", opService.getService());
		// Postpone to next iteration
		return new QueryIterRepeatApply(input, execCxt) {
			@Override
			protected QueryIterator nextStage(Binding binding) {
				Op op2 = QC.substitute(opService, binding);
				QueryIterator thisStep = QueryIterSingleton.create(binding, this.getExecContext());
				QueryIterator cIter = QC.execute(op2, thisStep, super.getExecContext());
				cIter = new QueryIterDefaulting(cIter, binding, this.getExecContext());
				return cIter;
			}
		};
	}

	private QueryIterator postponeBGP(final OpBGP opBGP, QueryIterator input) {
		// Postpone to next iteration
		return new QueryIterRepeatApply(input, execCxt) {
			@Override
			protected QueryIterator nextStage(Binding binding) {
				logger.trace(Utils.bindingToString(binding));
				Op op2 = QC.substitute(opBGP, binding);
				QueryIterator thisStep = QueryIterSingleton.create(binding, this.getExecContext());
				QueryIterator cIter = QC.execute(op2, thisStep, super.getExecContext());
				cIter = new QueryIterDefaulting(cIter, binding, this.getExecContext());
				return cIter;
			}
		};
	}


	private OpBGP extractFakePattern(OpBGP bgp) {
		BasicPattern pattern = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES)) {
				if (t.getObject().isVariable()) {
					Var s = Var.alloc("s" + System.currentTimeMillis());
					Var p = Var.alloc("p" + System.currentTimeMillis());
					pattern.add(new Triple(s, p, t.getObject()));
				}
			}
		}
		return new OpBGP(pattern);
	}

	private OpBGP excludeFXProperties(OpBGP bgp) {
		BasicPattern result = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().isURI() && t.getSubject().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES)) continue;
			result.add(t);
		}
		return new OpBGP(result);
	}

	private OpBGP excludeOpPropFunction(OpBGP bgp) {
		BasicPattern result = new BasicPattern();
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getPredicate().isURI() && t.getPredicate().getURI().equals(Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "anySlot"))
				continue;
			result.add(t);
		}
		return new OpBGP(result);
	}

	protected QueryIterator execute(final OpBGP opBGP, QueryIterator input) {

		if (this.execCxt.getClass() != FacadeXExecutionContext.class) {
			return super.execute(opBGP, input);
		}

		// i think we can consider this the start of the query execution and therefore the read txn.
		// we won't end this read txn until the next query takes execution back through BaseFacadeXBuilder
		if (!this.execCxt.getDataset().isInTransaction()) {
			// i think we need the test (instead of just unconditionally starting the txn) because if we postpone
			// during a query execution, execution could pass through here again
			logger.debug("begin read txn");
			this.execCxt.getDataset().begin(TxnType.READ);
		}

		this.execCxt.getDataset().listGraphNodes().forEachRemaining(g -> logger.trace("Graph {}", g.toString()));

		logger.trace("executing  BGP {}", opBGP.toString());
		logger.trace("Size: {} {}", this.execCxt.getDataset().size(), this.execCxt.getDataset().getDefaultGraph().size());

		List<Triple> l = getPropFuncTriples(opBGP.getPattern());
		logger.trace("Triples with OpFunc: {}", l.size());
		QueryIterator input2 = input;
		for (Triple t : l) {
			input2 = QC.execute(getOpPropFuncAnySlot(t), input2, execCxt);
		}

		try {
			Properties p = new Properties();
			logger.debug("Input BGP {} ", opBGP);
			PropertyUtils.extractPropertiesFromOpGraph(p, opBGP);
			if (p.size() > 0) {
				// if we have FX properties we at least need to excludeFXProperties()
				logger.trace("BGP Properties {}", p);
				DatasetGraph dg;
				if (this.execCxt.getDataset().isEmpty()) {
					// we only need to call getDatasetGraph() if we have an empty one
					// otherwise we could triplify the same data multiple times
					dg = dgc.getDatasetGraph(PropertyUtils.getTriplifier(p, triplifierRegister), p, opBGP);
				} else {
					dg = this.execCxt.getDataset();
				}

				return QC.execute(excludeOpPropFunction(excludeFXProperties(opBGP)), input2, getFacadeXExecutionContext(p, dg));
			}
		} catch (UnboundVariableException e) {
			logger.trace("Unbound variables");
			OpBGP fakeBGP = extractFakePattern(opBGP);
			return postponeBGP(excludeOpPropFunction(opBGP), QC.executeDirect(fakeBGP.getPattern(), input2, execCxt));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
				 ClassNotFoundException | IOException e) {
			logger.error(e.getMessage());
		}
		logger.debug("Execute default {} {}", opBGP, excludeOpPropFunction(opBGP));
		return super.execute(excludeOpPropFunction(opBGP), input2);
	}

}
