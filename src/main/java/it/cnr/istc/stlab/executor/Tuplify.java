package it.cnr.istc.stlab.executor;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

public class Tuplify extends FunctionBase1 {
	public Tuplify() {
		super();
	}

	public NodeValue exec(NodeValue v) {
		return NodeValue.makeString(v.getString() + "?tuple");
	}
}
