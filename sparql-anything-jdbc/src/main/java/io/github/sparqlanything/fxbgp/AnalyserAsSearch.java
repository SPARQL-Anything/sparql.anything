package io.github.sparqlanything.fxbgp;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * This is very inefficient...
 */
@Deprecated
public class AnalyserAsSearch implements Analyser {
	private static final Logger L = LoggerFactory.getLogger(AnalyserAsSearch.class);
	private FXModel FXM;
	private Properties properties;

	public AnalyserAsSearch(Properties properties, FXModel model){
		this.FXM = model;
		this.properties = properties;
	}


	@Override
	public Set<InterpretationOfBGP> interpret(OpBGP bgp, boolean complete){

		// To collect the solutions
		Set<InterpretationOfBGP> finalStates = new HashSet<>();

		// We make a starting interpretation. This is the root of the search space.
		InterpretationOfBGP start = FXM.getIF().make(bgp);

		// For any triple, s, p, o are all different
		for(Triple t : bgp.getPattern()){
			if(t.getSubject().equals(t.getObject()) ||
				t.getSubject().equals(t.getPredicate()) ||
				t.getObject().equals(t.getPredicate()) ){
				return Collections.emptySet();
			}
		}

		// No cycles are allowed
		if(FXM.hasCycle(bgp)){
			return Collections.emptySet();
		}

		iteration = 0;
		Set<InterpretationOfBGP> interpretations = interpret(start, new HashSet<>(), complete);
		L.info("{} iterations",iteration);
		return interpretations;
	}

	private int iteration = 0;
	private Set<InterpretationOfBGP> interpret(InterpretationOfBGP ibgp, Set<InterpretationOfBGP> results, boolean complete){
		iteration += 1;
		// The incoming interpretation is always consistent

		// It is never final (otherwise it would not be the first parameter)
		if(ibgp.isGrounded()){
			throw new RuntimeException("This must never happen");
		}

		// For any new interpretation
		Set<InterpretationOfBGP> hypotheses = specialise(ibgp);
		for(InterpretationOfBGP nibgp: hypotheses){
			boolean inconsistent = false;

			// Make inferences
			for(Node focus: nibgp.nodes()) {
					// For each node, run inference rules
				Set<NodeInterpretationRule> rules = FXM.getInferenceRules();
				for(NodeInterpretationRule rule: rules){
					// For each rule that resolves, check if interpretation is consistent
					boolean resolves = rule.when(focus, nibgp);
					if(resolves){
						InterpretationOfNode nni = rule.infer();
						// Is it redundant?
						InterpretationOfNode prev = nibgp.getInterpretation(focus);
						if(nni.equals(prev)){
							// Ignore redundant inferences, move to the next rule
							continue;
						}
						// Verify consistency with previous interpretation
						if(FXM.consistent(nni.getTerm(),prev.getTerm())){
							// Check next rule
							continue;
						}else{
							// If it is not consistent, discard the current interpretation 'nibgp'
							// It means that the hypothesised specialisation cannot be!
							// And stop executing rules!
							L.trace(" -- inconsistency -- {} % {} vs {}",focus, nni.getTerm(),prev.getTerm());
							inconsistent = true;
							break;
						}
					}
				}

				if(inconsistent) {
					// If a rule generates an inference that is inconsistent with the hypothesised
					// expansion, stop running inferences on other nodes and drop the hypothesised
					// specialisation
					break;
				}
				// If we arrived here, all rules inferences are plausible
				// Check the next node
				// End for each node
			}

			if(inconsistent){
				// discard hypothesis
			}else{
				if(nibgp.isGrounded()){
					//  If the new inferred interpretation is grounded, add to the return set
					L.trace(" -- grounded -- {}",nibgp.toString());
					results.add(nibgp);
				}else{
					// if not, keep interpreting it
					results.addAll(interpret(nibgp, results, complete));
				}
				if(!complete && results.size()>0){
					return results;
				}
			}
			// End for each hypothesised specialisation
		}

		return results;
	}

	/**
	 * This method returns the set of possible interpretations of a Node, starting from a previous interpretation.
	 * The method returns an empty set if the interpretation is 'grounded', meaning no other, more specific interpretations are possible.
	 * @param interpretation
	 * @return
	 */
	public Set<InterpretationOfNode> specialise(InterpretationOfNode interpretation){
		if(interpretation.isGrounded()){
			return Collections.emptySet();
		}else{
			Set<InterpretationOfNode> interpretations = new HashSet<>();
			for(FX el : FXM.getSpecialisedBy(interpretation.getTerm())){
				interpretations.add(FXM.getIF().makeFrom(interpretation, el));
			}
			return interpretations;
		}
	}

	public Set<InterpretationOfBGP> specialise(InterpretationOfBGP interpretation){
		if(interpretation.isGrounded()){
			return Collections.emptySet();
		}else{
			Set<InterpretationOfBGP> interpretations = new HashSet<>();
			boolean hasSpecialisations = false;
			for(InterpretationOfNode ni: interpretation.getInterpretationOfNodes().values()){
				for(InterpretationOfNode in: specialise(ni) ) {
					interpretations.add(FXM.getIF().make(interpretation, in));
					hasSpecialisations = true;
				}
			}
			if(!hasSpecialisations){
				throw new RuntimeException("This should never happen");
			}
			return interpretations;
		}
	}
}
