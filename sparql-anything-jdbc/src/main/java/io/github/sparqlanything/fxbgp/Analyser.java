package io.github.sparqlanything.fxbgp;

import org.apache.jena.sparql.algebra.op.OpBGP;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Analyser {
	private FXModel FXM;
	private Properties properties;

	private InterpretationFactory IF;

	public Analyser(Properties properties, FXModel model){
		this.FXM = model;
		this.IF = new InterpretationFactory(FXM);
		this.properties = properties;
	}

	public InterpretationOfBGP interpret(OpBGP bgp){


		return null;
	}

	/**
	 * This method returns the set of possible interpretations of a Node, starting from a previous interpretation.
	 * The method returns an empty set if the interpretation is 'grounded', meaning not other, more specific interpretations are possible.
	 * @param interpretation
	 * @return
	 */
	public Set<InterpretationOfNode> specialise(InterpretationOfNode interpretation){
		if(interpretation.isGrounded()){
			return Collections.emptySet();
		}else{
			Set<InterpretationOfNode> interpretations = new HashSet<>();
			Set<FX> specialisations = FXM.getSpecialisationsOf(interpretation.getInterpretation());
			for(FX el : specialisations){
				interpretations.add(IF.makeFrom(interpretation, el));
			}
			return interpretations;
		}
	}

}
