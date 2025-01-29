package io.github.sparqlanything.fxbgp;

import org.apache.jena.graph.Node;

import java.util.Map;
import java.util.Set;

public interface InterpretationOfBGP extends Interpretation {
	Map<Node, InterpretationOfNode> getInterpretationOfNodes();

	public boolean isGrounded();

	public boolean isStart();

	public InterpretationOfNode getInterpretation(Node node);

	public InterpretationOfBGP previous();

	public Set<Node> nodes();
}
