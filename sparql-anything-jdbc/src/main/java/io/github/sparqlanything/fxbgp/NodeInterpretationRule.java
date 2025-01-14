package io.github.sparqlanything.fxbgp;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

public abstract class NodeInterpretationRule implements InterpretationRule{
	private final Triple triple;

	protected NodeInterpretationRule(Triple triple){
		this.triple = triple;
	}
}
