package io.github.sparqlanything.fxbgp;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;

public abstract class NodeInterpretationRule implements InterpretationRule{
	private InterpretationOfNode interpretation = null;
	protected NodeInterpretationRule(){
	}
	abstract boolean when(Node node, InterpretationOfBGP previous);
	public InterpretationOfNode infer(){
		return interpretation;
	}
	public boolean resolved(){
		return interpretation != null;
	}
	protected void set(InterpretationOfNode outcome){
		this.interpretation = outcome;
	}
}
