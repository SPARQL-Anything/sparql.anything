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
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterNullIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.Scanner;

public abstract class FXWorker<T extends Op> {

	private static final Logger logger = LoggerFactory.getLogger(FXWorkerOpService.class);
	private final TriplifierRegister tr;
	private final DatasetGraphCreator dgc;

	public FXWorker(TriplifierRegister tr, DatasetGraphCreator dgc) {
		this.tr = tr;
		this.dgc = dgc;
	}

	public QueryIterator execute(T op, QueryIterator input, ExecutionContext executionContext) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException, UnboundVariableException {

		// extract properties from service URI
		Properties p = new Properties();

		// first extract from execution context
		PropertyExtractor.extractPropertiesFromExecutionContext(executionContext, p);

		//then, from opservice (so that can be overwritten)
		extractProperties(p, op);

		// Possibly read from STD in
		readFromStdIn(p);


		// guess triplifier
		Triplifier t = PropertyExtractor.getTriplifier(p, tr);

		if (t == null) {
			logger.warn("No triplifier found");
			return QueryIterNullIterator.create(executionContext);
		}

		// check execution with slicing
		if (PropertyUtils.getBooleanProperty(p, IRIArgument.SLICE)) {
			if (t instanceof Slicer) {
				logger.trace("Execute with slicing");
				return new QueryIterSlicer(executionContext, input, t, p, op);
			} else {
				logger.warn("Slicing is not supported by triplifier: {}", t.getClass().getName());
			}
		}

		// Execute with default, bulk method
		DatasetGraph dg = dgc.getDatasetGraph(t, p, op);
		Utils.ensureReadingTxn(dg);

		return execute(op, input, executionContext, dg, p);
	}

	public abstract void extractProperties(Properties p, T op) throws UnboundVariableException;

	private void readFromStdIn(Properties p) {
		if (p.containsKey(IRIArgument.READ_FROM_STD_IN.toString())) {
			Scanner sc = new Scanner(System.in);
			StringBuilder sb = new StringBuilder();
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine());
				sb.append('\n');
			}
			p.setProperty(IRIArgument.CONTENT.toString(), sb.substring(0, sb.length()-1));
		}
	}

	public abstract QueryIterator execute(T op, QueryIterator input, ExecutionContext executionContext, DatasetGraph dg, Properties p);
}
