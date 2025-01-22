package io.github.sparqlanything.fxbgp;

import io.github.sparqlanything.jdbc.NodeInterpretation;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.op.OpBGP;

import java.util.Set;

public interface InterpretationOfNode extends Interpretation {
	boolean consistentWith(InterpretationOfNode node);

	FX getInterpretation();

	Node getNode();

	boolean isGrounded();


}
