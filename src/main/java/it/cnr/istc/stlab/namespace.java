package it.cnr.istc.stlab;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.sparql.util.FmtUtils;

public class namespace extends FunctionBase1 {
	public namespace() {
		super();
	}

	public NodeValue exec(NodeValue v) {
		Node n = v.asNode();
		if (!n.isURI())
			throw new ExprEvalException("Not a URI: " + FmtUtils.stringForNode(n));
		String str = n.getNameSpace();
		return NodeValue.makeString(str);
	}
}
