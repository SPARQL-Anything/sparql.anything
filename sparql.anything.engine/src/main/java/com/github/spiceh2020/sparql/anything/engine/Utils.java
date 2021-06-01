package com.github.spiceh2020.sparql.anything.engine;

import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class Utils {

	static boolean isPropertyOp(OpGraph opTripleNext) {
		return opTripleNext.getNode().getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES);
	}

	static boolean isPropertyOp(Node node) {
		return node.isURI() && node.getURI().equals(Triplifier.FACADE_X_TYPE_PROPERTIES);
	}

	static String queryIteratorToString(QueryIterator q) {
		StringBuilder sb = new StringBuilder();
		while (q.hasNext()) {
			Binding binding = (Binding) q.next();
			sb.append(bindingToString(binding));
		}
		return sb.toString();
	}

	static String bindingToString(Binding binding) {
		StringBuilder sb = new StringBuilder();
		Iterator<Var> vars = binding.vars();
		while (vars.hasNext()) {
			Var var = (Var) vars.next();
			sb.append(String.format("%s -> %s\n", var.getName(), binding.get(var)));
		}
		return sb.toString();
	}

}
