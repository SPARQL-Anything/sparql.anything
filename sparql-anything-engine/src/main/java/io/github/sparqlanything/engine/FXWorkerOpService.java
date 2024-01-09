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

import io.github.sparqlanything.facadeiri.FacadeIRIParser;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Triplifier;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.main.QC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class FXWorkerOpService extends FXWorker<OpService> {

	private static final Logger logger = LoggerFactory.getLogger(FXWorkerOpService.class);

	public FXWorkerOpService(TriplifierRegister tr, DatasetGraphCreator dgc){
		super(tr, dgc);
	}

	@Override
	public QueryIterator execute(OpService op, QueryIterator input, ExecutionContext executionContext, DatasetGraph dg, Properties p) {
		return QC.execute(Algebra.optimize(op.getSubOp()), input, Utils.getFacadeXExecutionContext(executionContext, p, dg));
	}

	@Override
	public void extractProperties(Properties properties, OpService opService) throws UnboundVariableException {
		String url = opService.getService().getURI();
		// Parse IRI only if contains properties
		if (!url.equals(FacadeIRIParser.SPARQL_ANYTHING_URI_SCHEMA)) {
			FacadeIRIParser p = new FacadeIRIParser(url);
			properties.putAll(p.getProperties());
		}

		// Setting defaults
		if (!properties.containsKey(IRIArgument.NAMESPACE.toString())) {
			logger.trace("Setting default value for namespace: {}", Triplifier.XYZ_NS);
			properties.setProperty(IRIArgument.NAMESPACE.toString(), Triplifier.XYZ_NS);
		}
		// Setting silent
		if (opService.getSilent()) {
			// we can only see if silent was specified at the OpService so we need to stash
			// a boolean
			// at this point so we can use it when we triplify further down the Op tree
			properties.setProperty(IRIArgument.OP_SERVICE_SILENT.toString(), "true");
		}

		Op next = opService.getSubOp();
		FXBGPFinder vis = new FXBGPFinder();
		next.visit(vis);
		logger.trace("Has Table {}", vis.hasTable());

		if (vis.getBGP() != null) {
			try {
				PropertyExtractor.extractPropertiesFromBGP(properties, vis.getBGP());
			} catch (UnboundVariableException e) {
				if (vis.hasTable()) {
					logger.trace(vis.getOpTable().toString());
					logger.trace("BGP {}", vis.getBGP());
					logger.trace("Contains variable names {}", vis.getOpTable().getTable().getVarNames().contains(e.getVariableName()));
					if (vis.getOpTable().getTable().getVarNames().contains(e.getVariableName())) {
						e.setOpTable(vis.getOpTable());
					}
				}

				if (vis.getOpExtend() != null) {
					logger.trace("OpExtend {}", vis.getOpExtend());
					for (Var var : vis.getOpExtend().getVarExprList().getVars()) {
						if (var.getName().equals(e.getVariableName())) {
							e.setOpExtend(vis.getOpExtend());
						}
					}
				}

				throw e;
			}
			logger.trace("Number of properties {}: {}", properties.size(), properties);
		} else {
			logger.trace("Couldn't find OpGraph");
		}
	}
}
