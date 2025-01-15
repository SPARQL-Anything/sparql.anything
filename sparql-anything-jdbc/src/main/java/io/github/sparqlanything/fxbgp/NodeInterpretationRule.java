package io.github.sparqlanything.fxbgp;
import io.github.sparqlanything.jdbc.NodeInterpretation;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;

public abstract class NodeInterpretationRule implements InterpretationRule{
	private final Triple triple;

	protected NodeInterpretationRule(Triple triple){
		this.triple = triple;
	}
	abstract boolean when(OpBGP bgp, NodeInterpretations previous);
	public abstract NodeInterpretation infer();
}
