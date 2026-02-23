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

import io.github.sparqlanything.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterNullIterator;
import org.apache.jena.sparql.engine.main.QC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class FXWorker {

	private static final Logger L = LoggerFactory.getLogger(FXWorker.class);

	public QueryIterator execute(Op op, QueryIterator input, ExecutionContext executionContext) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException, UnboundVariableException, URISyntaxException {
		L.debug("execute", op);
		// extract properties from service URI
		Properties p = new Properties();

		// first extract from execution context
		PropertyExtractor.extractPropertiesFromExecutionContext(executionContext, p);

		if(op instanceof OpService){
			PropertyExtractor.extractProperties(p, (OpService) op);
			// Possibly execute reused queries
			if(PropertyUtils.hasProperty(p, IRIArgument.QUERY))
				return executeReusedQuery((OpService) op, p, input, executionContext);
		}

		// Possibly read from STD in
		PropertyExtractor.readFromStdIn(p);
		if(L.isDebugEnabled()){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			p.list(pw);
			L.debug("Properties: \n{}", sw.toString());
		}
		FXExecutionStrategy s = new FXStrategySelector().getStrategy(p, op, executionContext);
		return s.execute(op, input);
		//return new FXGraphMaterialisationStrategy(p,executionContext).execute(op,input);
	}

	public QueryIterator executeReusedQuery(OpService opService, Properties properties, QueryIterator input, ExecutionContext executionContext) throws URISyntaxException, IOException {
		L.debug("executeReusedQuery");
		String queryStr = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResource(PropertyUtils.getStringProperty(properties, IRIArgument.QUERY))).toURI(), StandardCharsets.UTF_8);
		Query query = QueryFactory.create(queryStr);
		Op op = Algebra.optimize(Algebra.compile(query));
		if (query.isSelectType()) {
			return QC.execute(op, input, executionContext);
		} else if (query.isConstructQuad()){
			QueryExecution queryExecution = QueryExecutionFactory.create(query, executionContext.getDataset());
			Dataset dataset = queryExecution.execConstructDataset();
			return QC.execute(opService.getSubOp(), input, FacadeXExecutionContext.create(dataset.asDatasetGraph()));
		} else if (query.isConstructType()) {
			QueryExecution queryExecution = QueryExecutionFactory.create(query, executionContext.getDataset());
			Model result = queryExecution.execConstruct();
			return QC.execute(opService.getSubOp(), input, FacadeXExecutionContext.createForGraph(result.getGraph()));
		}
		return QueryIterNullIterator.create(executionContext);
	}
}
