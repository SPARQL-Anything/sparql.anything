package io.github.sparqlanything.fxbgp;

import org.apache.jena.graph.Node;

public interface InterpretationOfNode extends Interpretation {
	boolean consistentWith(InterpretationOfNode node);

	FX getTerm();

	Node getNode();

	boolean isGrounded();

}
