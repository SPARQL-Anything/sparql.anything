package io.github.sparqlanything.fxbgp;

import org.apache.jena.sparql.algebra.op.OpBGP;

import java.util.Set;

public interface Analyser {
	/**
	 * Returns all possible grounded interpretations of a BGP according to a FX model
	 *
	 * @param bgp
	 * @return
	 */
	Set<InterpretationOfBGP> interpret(OpBGP bgp);
}
