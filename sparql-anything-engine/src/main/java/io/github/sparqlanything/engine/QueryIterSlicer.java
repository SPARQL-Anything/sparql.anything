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

import io.github.sparqlanything.model.*;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIter;
import org.apache.jena.sparql.engine.iterator.QueryIterNullIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import org.apache.jena.sparql.engine.main.QC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class QueryIterSlicer extends QueryIter {

	private static final Logger logger = LoggerFactory.getLogger(QueryIterSlicer.class);
	final List<Binding> elements;
	private final Iterator<Slice> iterator;
	private final ExecutionContext execCxt;
	private final String resourceId;
	private final Op op;
	final private Slicer slicer;
	private final QueryIterator input;
	private QueryIterator current = null;
	private final Properties p;

	private final CloseableIterable<Slice> it;

	public QueryIterSlicer(ExecutionContext execCxt, QueryIterator input, Triplifier t, Properties properties, Op op) throws TriplifierHTTPException, IOException {
		super(execCxt);
		this.slicer = (Slicer) t;
		this.p = properties;
		this.it = slicer.slice(p);
		this.input = input;

		elements = new ArrayList<>();
		while (input.hasNext()) {
			elements.add(input.nextBinding());
		}

		this.iterator = it.iterator();
		this.execCxt = execCxt;
		this.resourceId = Triplifier.getResourceId(p);
		if(op instanceof OpService){
			this.op = ((OpService)op).getSubOp();
		}else{
			this.op = op;
		}
	}

	@Override
	protected boolean hasNextBinding() {
		logger.trace("hasNextBinding? ");
		logger.debug("current: {}", current != null ? current.hasNext() : "null");
		while (current == null || !current.hasNext()) {
			if (iterator.hasNext()) {
				Slice slice = iterator.next();
				logger.debug("Executing on slice: {}", slice.iteration());
				// Execute and set current
				FacadeXGraphBuilder builder;
				Integer strategy = PropertyExtractor.detectStrategy(p, execCxt);
				if (strategy == 1) {
					logger.trace("Executing: {} [strategy={}]", p, strategy);
					builder = new TripleFilteringFacadeXGraphBuilder(resourceId, op, p);
				} else {
					logger.trace("Executing: {} [strategy={}]", p, strategy);
					builder = new BaseFacadeXGraphBuilder(p);
				}
				//FacadeXGraphBuilder builder = new TripleFilteringFacadeXGraphBuilder(resourceId, opService.getSubOp(), p);
				slicer.triplify(slice, p, builder);
				DatasetGraph dg = builder.getDatasetGraph();
				dg.commit();
				dg.end();

				Utils.ensureReadingTxn(dg);
				logger.debug("Executing on next slice: {} ({})", slice.iteration(), dg.size());
//				FacadeXExecutionContext ec = new FacadeXExecutionContext(new ExecutionContext(execCxt.getContext(), dg.getDefaultGraph(), dg, execCxt.getExecutor()));
				FacadeXExecutionContext ec = Utils.getFacadeXExecutionContext(execCxt, p, dg);
				logger.trace("Op {}", op);
				logger.trace("OpName {}", op.getName());
				/*
				 * input needs to be reset before each execution, otherwise the executor will skip subsequent executions
				 * since input bindings have been flushed!
				 */
				QueryIterator cloned;
				cloned = QueryIterPlainWrapper.create(elements.iterator());
				current = QC.execute(op, cloned, ec);
				logger.debug("Set current. hasNext? {}", current.hasNext());
				if (current.hasNext()) {
					logger.trace("Break.");
					break;
				}
			} else {
				logger.trace("Slices finished");
				/*
				 * Input iterator can be closed
				 */
				input.cancel();
				// Make sure the original Op is executed
				// XXX Maybe there is a better way of doing it?
				ExecutionContext exc = new ExecutionContext(DatasetGraphFactory.create());
				QC.execute(op, QueryIterNullIterator.create(exc), exc);
				try {
					this.it.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return false;
			}
		}
		logger.trace("hasNextBinding? {}", current.hasNext());
		return current.hasNext();
	}

	@Override
	protected Binding moveToNextBinding() {
		logger.trace("moveToNextBinding");
		return current.nextBinding();
	}

	@Override
	protected void closeIterator() {
		current.close();
	}

	@Override
	protected void requestCancel() {
		current.cancel();
	}
}
