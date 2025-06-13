package io.github.sparqlanything.fxbgp;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;

public abstract class NodeInterpretationRule implements InterpretationRule {
	private InterpretationOfNode interpretation = null;
	private Boolean failure = null;
	protected NodeInterpretationRule(){
	}
	protected abstract boolean when(Node node, InterpretationOfBGP previous);
	public InterpretationOfNode infer(){
		InterpretationOfNode toReturn = interpretation;
		clean();
		return toReturn;

	}
	public boolean resolved(){
		return interpretation != null || failure != null;
	}
	protected void set(InterpretationOfNode outcome){
		failure = false;
		this.interpretation = outcome;
	}

	protected void setFailure(){
		failure = true;
		this.interpretation = null;
	}

	private void clean(){
		interpretation = null;
	}
	public boolean failure(){
		return failure == null ? false : failure;
	}
}
