/*
 * Copyright (c) [2021] SPARQL Anything https://sparql-anything.cc/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.sparqlanything.html;

import org.apache.any23.writer.TripleHandlerException;
import org.apache.any23.writer.TripleWriterHandler;
import org.apache.jena.graph.BlankNodeId;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

import com.github.sparqlanything.model.FacadeXGraphBuilder;

public class MetadataWriter extends TripleWriterHandler {

	private FacadeXGraphBuilder facadeXGraphBuilder;

	public MetadataWriter(FacadeXGraphBuilder facadeXGraphBuilder) {
		this.facadeXGraphBuilder = facadeXGraphBuilder;
	}

	@Override
	public void close() throws TripleHandlerException {

	}

	@Override
	public void writeTriple(Resource s, IRI p, Value o, Resource g) throws TripleHandlerException {
		facadeXGraphBuilder.add(resolveValue(g), resolveValue(s), NodeFactory.createURI(p.stringValue()),
				resolveValue(o));
	}

	public Node resolveValue(Value r) {
		if (r instanceof BNode) {
			return NodeFactory.createBlankNode(BlankNodeId.create(((BNode) r).getID()));
		} else if (r instanceof IRI) {
			return NodeFactory.createURI(r.stringValue());
		} else {
			return NodeFactory.createLiteral(r.stringValue());
		}
	}

	@Override
	public void writeNamespace(String prefix, String uri) throws TripleHandlerException {

	}

}
