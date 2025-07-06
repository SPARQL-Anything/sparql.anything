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


	default boolean isSatisfiable(OpBGP bgp) {
		return interpret(bgp, false).size()>0;
	}

	default Set<InterpretationOfBGP> interpret(OpBGP bgp){
		return interpret(bgp, true);
	}

	Set<InterpretationOfBGP> interpret(OpBGP bgp, boolean complete);

}
