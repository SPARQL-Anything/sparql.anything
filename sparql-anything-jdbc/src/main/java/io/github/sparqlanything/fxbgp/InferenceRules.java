package io.github.sparqlanything.fxbgp;

import io.github.sparqlanything.jdbc.InconsistentAssumptionException;
import io.github.sparqlanything.jdbc.NodeInterpretation;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.algebra.op.OpBGP;

import java.util.Map;

public interface InferenceRules {
	Map<Node, NodeInterpretation> run(OpBGP opBGP) throws InconsistentAssumptionException;
	Map<Node, NodeInterpretation> run(OpBGP opBGP, Map<Node, NodeInterpretation> previous) throws InconsistentAssumptionException;
//
//	boolean isException();
//	Exception getException();
}
