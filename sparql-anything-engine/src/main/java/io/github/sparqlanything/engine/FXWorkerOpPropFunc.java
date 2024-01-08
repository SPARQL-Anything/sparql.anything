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
