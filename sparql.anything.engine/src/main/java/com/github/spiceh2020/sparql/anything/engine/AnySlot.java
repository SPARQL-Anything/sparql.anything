package com.github.spiceh2020.sparql.anything.engine;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.pfunction.PFuncSimple;

public class AnySlot extends PFuncSimple {

	@Override
	public QueryIterator execEvaluated(Binding binding, Node subject, Node predicate, Node object,
			ExecutionContext execCxt) {
		return null;
	}

}
