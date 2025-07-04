package io.github.sparqlanything.fxbgp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.github.sparqlanything.model.Triplifier;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FXModel {
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private static FXModel instance = null;

	private InterpretationFactory IF = null;
	private Set<FX> terms;
	private Map<FX,Set<FX>> specialisedBy;
	private Map<FX,Set<FX>> specialisationOf;
	private Map<FX,Set<FX>> inconsistentWith;

	private Set<NodeInterpretationRule> inferenceRules;

	// FIXME Use constant from model package
	protected static final Node FXRoot = NodeFactory.createURI(Triplifier.FACADE_X_TYPE_ROOT);

	protected FXModel(){
		terms = new HashSet<>();
		specialisedBy = new HashMap<>();
		specialisationOf = new HashMap<>();
		inconsistentWith = new HashMap<>();
		inferenceRules = new HashSet<>();
		IF = new InterpretationFactory(this);
		init();
		extend();
	}

	protected InterpretationFactory getIF(){
		return IF;
	}

	/**
	 * returns true if element is new.
	 * the method is private because extending FX requires
	 * new terms to specialise existing ones
	 *
	 * @param element
	 * @return
	 */
	private boolean add(FX element){
		if(!specialisedBy.containsKey(element)){
			specialisedBy.put(element, new HashSet<>());
		}
		if(!specialisationOf.containsKey(element)){
			specialisationOf.put(element, new HashSet<>());
		}
		if(!inconsistentWith.containsKey(element)){
			inconsistentWith.put(element, new HashSet<>());
		}
		return this.terms.add(element);
	}

	protected void setSpecialisedBy(FX thiss, FX thatt){
		add(thiss);
		add(thatt);
		boolean newlyAdded = specialisedBy.get(thiss).add(thatt);
		if(newlyAdded){
			setSpecialisationOf(thatt, thiss);
		}
	}

	protected void setSpecialisationOf(FX thiss, FX thatt){
		add(thiss);
		add(thatt);
		boolean newlyAdded = specialisationOf.get(thiss).add(thatt);
		if(newlyAdded){
			setSpecialisedBy(thatt, thiss);
		}
	}

	protected void setInconsistentWith(FX thiss, FX... thatt){
		add(thiss);
		for(FX th : thatt){
			add(th);
			inconsistentWith.get(thiss).add(th);
			inconsistentWith.get(th).add(thiss);
		}
	}

	protected void addInferenceRule(NodeInterpretationRule rule){
		inferenceRules.add(rule);
	}

	public Set<NodeInterpretationRule> getInferenceRules(){
		return Collections.unmodifiableSet(inferenceRules);
	}
	public boolean elementExists(FX element){
		return this.terms.contains(element);
	}

	/**
	 * returns elements that are specialisations of the element given
	 * @param element
	 * @return
	 */
	public Set<FX> getSpecialisedBy(FX element){
		if(!specialisedBy.containsKey(element)){
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(specialisedBy.get(element));
	}

	public Set<FX> getSpecialisationsOf(FX element){
		if(!specialisationOf.containsKey(element)){
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(specialisationOf.get(element));
	}

	public Set<FX> setInconsistentWith(FX element){
		if(!inconsistentWith.containsKey(element)){
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(inconsistentWith.get(element));
	}

	public boolean inconsistent(FX el1, FX el2){
		return inconsistentWith.get(el1).contains(el2);
	}
	public boolean isSpecialisedBy(FX thiss, FX thatt){
		return specialisedBy.get(thiss).contains(thatt);
	}
	public boolean isSpecialisationOf(FX thiss, FX thatt){
		return specialisedBy.get(thatt).contains(thiss);
	}

	public boolean consistent(FX el1, FX el2){
		return !inconsistent(el1,el2);
	}

	private void init(){
		// Add Elements
		add(FX.Subject);
		add(FX.Predicate);
		add(FX.Object);
		add(FX.Container);
		add(FX.Slot);
		add(FX.SlotNumber);
		add(FX.SlotString);
		add(FX.Value);
		add(FX.TypeProperty);
		add(FX.Type);
		add(FX.Root);

		// Add consistency table (full binary matrix)
		setInconsistentWith(FX.Subject, FX.Predicate, FX.Slot, FX.SlotString, FX.SlotNumber, FX.Root, FX.Type, FX.TypeProperty);
		setInconsistentWith(FX.Object, FX.Predicate, FX.Slot, FX.SlotString, FX.SlotNumber, FX.TypeProperty);
		setInconsistentWith(FX.TypeProperty, FX.Subject, FX.Object, FX.Slot, FX.SlotString, FX.SlotNumber, FX.Type, FX.Container, FX.Root);
		setInconsistentWith(FX.Type, FX.Slot, FX.SlotString, FX.SlotNumber, FX.Container, FX.Value, FX.Subject, FX.Value, FX.Root);
		setInconsistentWith(FX.Container, FX.Predicate, FX.Slot, FX.SlotString, FX.SlotNumber, FX.TypeProperty, FX.Value, FX.Type, FX.Root);
		setInconsistentWith(FX.Slot, FX.Subject, FX.Object, FX.TypeProperty, FX.Root,FX.Type, FX.Container, FX.Value);
		setInconsistentWith(FX.Value, FX.Predicate, FX.Slot, FX.Subject, FX.Type, FX.Container, FX.Root, FX.SlotNumber, FX.SlotString, FX.TypeProperty);
		setInconsistentWith(FX.Root, FX.Slot, FX.Container, FX.Predicate, FX.Value, FX.Type, FX.SlotNumber, FX.SlotString, FX.TypeProperty);
		setInconsistentWith(FX.SlotNumber, FX.SlotString, FX.Subject, FX.Object, FX.Container, FX.Value, FX.Type, FX.TypeProperty, FX.Root);
		setInconsistentWith(FX.SlotString, FX.SlotNumber, FX.Subject, FX.Object, FX.Container, FX.Value, FX.Type, FX.TypeProperty, FX.Root);

		// Add hierarchy information
		setSpecialisedBy(FX.Subject, FX.Container);
		setSpecialisedBy(FX.Predicate, FX.Slot);
		setSpecialisedBy(FX.Predicate, FX.TypeProperty);
		setSpecialisedBy(FX.Object, FX.Container);
		setSpecialisedBy(FX.Object, FX.Value);
		setSpecialisedBy(FX.Object, FX.Type);
		setSpecialisedBy(FX.Object, FX.Root);
		setSpecialisedBy(FX.Slot, FX.SlotNumber);
		setSpecialisedBy(FX.Slot, FX.SlotString);

		// Add inference rules

		// 1. If a Subject, then a Container
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern().getList()) {
					if (
						node.equals(t.getSubject())
					) {
						set(IF.make(previous.getOpBGP(), node, FX.Container));
						return true;
					}
				}
				return false;
			}
		});

		// 2. If node is fx:Root, then is Root
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				if(node.equals(FXRoot)){
					set(IF.make(previous.getOpBGP(), node, FX.Root));
					return true;
				}
				return false;
			}
		});

		// 3. If node is rdf:type, then is TypeProperty
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				if(node.equals(RDF.type.asNode())){
					set(IF.make(previous.getOpBGP(), node, FX.TypeProperty));
					return true;
				}
				return false;
			}
		});

		// 4. If a Property and not a variable nor rdf:type, then a Slot
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				if(n.isConcrete()) {
					// Find if predicate
					for(Triple t: p.getOpBGP().getPattern().getList()) {
						if (
							n.equals(t.getPredicate()) &&
								!n.equals(RDF.type.asNode())
						) {
							set(IF.make(p.getOpBGP(), n, FX.Slot));
							return true;
						}
					}
				}
				return false;
			}
		});


		// 5. If Object not Var/Bnode and not fx:Root but Predicate rdf:type, then Type
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				if(n.isConcrete() && !n.equals(FXRoot) ){
					// Find the predicate
					for(Triple t: p.getOpBGP().getPattern().getList()){
						if(t.getObject().equals(n) &&
							t.getPredicate().equals(RDF.type.asNode())){
							set(IF.make(p.getOpBGP(), n, FX.Type));
							return true;
						}
					}
				}
				return false;
			}
		});

		// 6. If Predicate is focus and Object is Root, then Predicate is TypeProperty
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				// Find object
				for (Triple t : p.getOpBGP().getPattern().getList()) {
					if (t.getPredicate().equals(n) &&
						p.getInterpretation(t.getObject()).getTerm().equals(FX.Root)) {
						set(IF.make(p.getOpBGP(), n, FX.TypeProperty));
						return true;
					}
				}

				return false;
			}
		});

		// 7. If Predicate is focus and Object is Type, then Predicate is TypeProperty
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				// Find object
				for (Triple t : p.getOpBGP().getPattern().getList()) {
					if (t.getPredicate().equals(n) &&
						p.getInterpretation(t.getObject()).getTerm().equals(FX.Type)) {
						set(IF.make(p.getOpBGP(), n, FX.TypeProperty));
						return true;
					}
				}

				return false;
			}
		});

		// 8. If Predicate is a CMP, then Predicate is SlotNumber
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				String prefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#_";
				if(n.isConcrete()){
					for(Triple t: p.getOpBGP().getPattern().getList()) {
						if (t.getPredicate().equals(n)) {
							if (n.getURI().startsWith(prefix)) {
								set(IF.make(p.getOpBGP(), n, FX.SlotNumber));
								return true;
							}
						}
					}
				}
				return false;
			}
		});

		// 9. If Predicate is not Var and not rdf:type and not a CMP, then is SlotString
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				String prefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#_";
				if(n.isConcrete() && !n.equals(RDF.type.asNode())){
					for(Triple t: p.getOpBGP().getPattern().getList()) {
						if (t.getPredicate().equals(n)) {
							if (!n.getURI().startsWith(prefix)) {
								set(IF.make(p.getOpBGP(), n, FX.SlotString));
								return true;
							}
						}
					}
				}
				return false;
			}
		});

		// 10. If Object is Literal, then is Value
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				if(n.isConcrete()
					&& n.isLiteral()){
						set(IF.make(p.getOpBGP(), n, FX.Value));
						return true;
				}
				return false;
			}
		});

		// 11. If Object is IRI and Predicate is Slot, then Object is Container
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				if(n.isURI()){
					// Find predicate
					for(Triple t: p.getOpBGP().getPattern().getList()) {
						if (t.getObject().equals(n)) {
							Node r = t.getPredicate();
							if(p.getInterpretation(r).getTerm().equals(FX.Slot)) {
								set(IF.make(p.getOpBGP(), n, FX.Container));
								return true;
							}
						}
					}
				}
				return false;
			}
		});

		// 12. On focus node is Predicate position: and Object is Value, then Predicate is Slot
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				// Find object
				for (Triple t : p.getOpBGP().getPattern().getList()) {
					if (t.getPredicate().equals(n)) {
						// If object is value
						Node o = t.getObject();
						if (p.getInterpretation(o).getTerm().equals(FX.Value)) {
							set(IF.make(p.getOpBGP(), n, FX.Slot));
							return true;
						}
					}
				}
				return false;
			}
		});

		// 13. Object cannot be IRI, != Root, and Root
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				if(previous.getInterpretation(node).getTerm().equals(FX.Root)){
					if(node.isURI() && !node.equals(FXRoot)){
						setFailure();
						return true;
					}
				}
				return false;
			}
		});

		// 14. Type cannot be the primitive Root
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				if(previous.getInterpretation(node).getTerm().equals(FX.Type)){
					if(node.equals(FXRoot)){
						setFailure();
						return true;
					}
				}
				return false;
			}
		});

		// 15. Object cannot be IRI and Value
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				if(previous.getInterpretation(node).getTerm().equals(FX.Value)){
					if(node.isURI()){
						setFailure();
						return true;
					}
				}
				return false;
			}
		});

		// 16. On focus node is Predicate position: and Object is Container, then Predicate is Slot
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				// Find object
				for (Triple t : p.getOpBGP().getPattern().getList()) {
					if (t.getPredicate().equals(n)) {
						// If object is value
						Node o = t.getObject();
						if (p.getInterpretation(o).getTerm().equals(FX.Container)) {
							set(IF.make(p.getOpBGP(), n, FX.Slot));
							return true;
						}
					}
				}
				return false;
			}
		});

		// 17. If node is predicate, and it is a slot, and is in two triples that have the same subject, they also need to have the same object
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				FX term = p.getInterpretation(n).getTerm();
				Set<FX> spec = getSpecialisedBy(FX.Slot);
				if(term.equals(FX.Slot) || spec.contains(term)) {
					List<Triple> havingNodeAsP = new ArrayList<>();
					for (Triple t : p.getOpBGP().getPattern().getList()) {
						if (t.getPredicate().equals(n)) {
							havingNodeAsP.add(t);
						}
					}
					if (havingNodeAsP.size() > 1) {
						//
						Set product = Sets.cartesianProduct(ImmutableList.of(ImmutableSet.copyOf(havingNodeAsP), ImmutableSet.copyOf(havingNodeAsP)));
						for (Object lo: product) {
							List<Triple> ls = (List<Triple>) lo;
							Triple l = ls.get(0);
							Triple r = ls.get(1);
							// If equals, OK
							if (l.getSubject().equals(r.getSubject())) {
								if(!l.getObject().isConcrete() ||
								!r.getObject().isConcrete()){
									// If either is not concrete, this is fine
								}else{
									if (!l.getObject().equals(r.getObject())) {
										setFailure();
										return true;
									}
								}
							}
						}
					}
				}
				return false;
			}
		});
		// 18. No subject join when object is Root (no path to root...)
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node n, InterpretationOfBGP p) {
				// Object is root
				if(p.getInterpretation(n).getTerm().equals(FX.Root)){
					for (Triple t : p.getOpBGP().getPattern().getList()) {
						// If it is not the same node
						if (t.getObject().equals(n) ){
							Node subject = t.getSubject();
							for (Triple t2 : p.getOpBGP().getPattern().getList()) {
								if(t2.getObject().equals(subject)){
									setFailure();
									return true;
								}
							}
						}
					}
				}
				return false;
			}
		});

		// 19. Matching-path constraint when node is Object and (Container or Root)
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {

				// Only if node is a container or root
				if(!previous.getInterpretation(node).getTerm().equals(FX.Container)
					&& !previous.getInterpretation(node).getTerm().equals(FX.Root)){
					return false;
				}

				// Collect triples with this node as object
				Set<Triple> withNodeAsObject = new HashSet<>();
				for (Triple t: previous.getOpBGP().getPattern().getList()){
					if (t.getObject().equals(node)) {
						withNodeAsObject.add(t);
					}
				}
				// Make unique pairs
				Set<Set<Triple>> sets = new HashSet<>();
				for (Triple t1: withNodeAsObject){
					for (Triple t2: withNodeAsObject){
						if(!t1.equals(t2)){
							sets.add(new HashSet<>(Arrays.asList(t1,t2)));
						}
					}
				}
				List<Pair<Triple, Triple>> paris = new ArrayList<>();
				for(Set<Triple> set: sets){
					Triple left = null;
					Triple right = null;
					for(Triple t: set){
						if(left == null){
							left = t;
						}else if(right == null){
							right = t;
						}else{
							// This should never happen
							throw new RuntimeException("This should never happen");
						}
					}
					paris.add(Pair.of(left, right));
				}
				// TODO: Avoid duplicates?

				// Make backward paths out of triples...
				List<Pair<List<Node>, List<Node>>> london = new ArrayList<>();
				for(Pair<Triple,Triple> pair : paris){
					london.add(Pair.of(this.asList(pair.getLeft(), previous.getOpBGP().getPattern()),this.asList(pair.getRight(), previous.getOpBGP().getPattern())));
				}

				// Verify the terms can match on each path pair
				for(Pair<List<Node>, List<Node>> pair : london){
					List<Node> left = pair.getLeft();
					List<Node> right = pair.getRight();

					// If one of the two is shorter, its origin cannot be root
					if(left.size() != right.size()){
						List<Node> shorter = left.size() < right.size() ? left : right;
						Node shorterStart = shorter.get(shorter.size()-1);
						// Check that shorter cannot be a subject of a triple where object is FX.Root!
						for(Triple t: previous.getOpBGP().getPattern().getList()){
							if (t.getSubject().equals(shorterStart) &&
								previous.getInterpretation(t.getObject()).getTerm().equals(FX.Root)
							) {
								setFailure();
								return true;
							}
						}
					}
					// Check if terms are compatible
					for(int x = 0; x < left.size(); x++){
						Node ln = left.get(x);
						if(right.get(x) == null){
							// exit
							break;
						}
						Node rn = right.get(x);
						boolean ok = false;
						if(ln.equals(rn)){
							// OK
							ok = true;
						}
						if(!ok && (ln.isBlank() || rn.isBlank())){
							// OK
							ok = true;
						}
						if(!ok && (ln.isVariable() || rn.isVariable())){
							// OK
							ok = true;
						}
						// If not OK, FAIL
						if(!ok){
							setFailure();
							return true;
						}
					}
				}

				return false;
			}

			List<Node> asList(Triple triple, BasicPattern pattern){
				List<Node> list = new ArrayList<>();
				list.add(triple.getObject());
				list.add(triple.getPredicate());
				list.add(triple.getSubject());
				// Search for subject-object join
				for(Triple t: pattern.getList()){
					if(!t.equals(triple) && t.getObject().equals(triple.getSubject())){
						list.addAll(asList(t, pattern));
					}
				}
				return list;
			}

		});
	}

	/**
	 * 	An element is grounded when no element specialises it.
	 */
	public boolean isGrounded(FX element){
		return getSpecialisedBy(element).isEmpty();
	}

	public static final boolean hasCycle(OpBGP bgp){
		// For each node, follow the paths, if we find a node in the stack, return false
		for (Triple t: bgp.getPattern().getList()){
			List<Node> start = new ArrayList<>();
			start.add(t.getSubject());
			if(detectCycle(bgp, start)){
				return true;
			}
		}
		return false;
	}

	private static final boolean detectCycle(OpBGP bgp, List<Node> visited){
		Node last = visited.get(visited.size() - 1);
		for (Triple t : bgp.getPattern().getList()) {
			if (t.getSubject().equals(last)) {
				Node o = t.getObject();
				if (visited.contains(o)) {
					return true;
				} else {
					visited.add(o);
					return detectCycle(bgp, visited);
				}
			}
		}
		return false;
	}

	/**
	 * The FX model can be extended.
	 */
	protected void extend(){
	}

	public final static FXModel getFXModel(){
		if(instance == null){
			instance = new FXModel();
		}
		return instance;
	}

	public boolean isExtension(){
		return !this.getClass().equals(FX.class);
	}

	public Set<FX> groundedSpecialisations(FX term){
		Set<FX> result = new HashSet<>();
		for(FX f : getSpecialisedBy(term)){
			if(isGrounded(f)){
				result.add(f);
			}else{
				result.addAll(groundedSpecialisations(f));
			}
		}
		return result;
	}

	/**
	 * This method assumes that the terms assigned to nodes are consistent with relation to being Subject, Predicate, and Objects of triples.
	 * @param nibgp
	 * @return
	 */
	public final boolean isConsistent(InterpretationOfBGP nibgp){

		// Make inferences
		for(Node focus: nibgp.nodes()) {
			// For each node, run inference rules
			Set<NodeInterpretationRule> rules = this.getInferenceRules();
			for(NodeInterpretationRule rule: rules){
				// For each rule that resolves, check if interpretation is consistent
				boolean resolves = rule.when(focus, nibgp);
				if(resolves){
					// Rule says a constraint hasn't been satisfied
					if(rule.failure()){
						return false;
					}
					InterpretationOfNode nni = rule.infer();
					// Is it redundant?
					InterpretationOfNode prev = nibgp.getInterpretation(focus);
					if(nni.equals(prev)){
						// Ignore redundant inferences, move to the next rule
						continue;
					}
					// Verify consistency with previous interpretation
					if(this.consistent(nni.getTerm(), prev.getTerm())){
						// Check next rule
						continue;
					}else{
						// If it is not consistent, discard the current interpretation 'nibgp'
						// It means that the hypothesised specialisation cannot be!
						// And stop executing rules!
						LOGGER.trace(" -- inconsistency -- {} % {} vs {}",focus, nni.getTerm(),prev.getTerm());
						return false;
						//break;
					}
				}
			}
			// If we arrived here, all rules inferences are plausible
			// Check the next node
			// End for each node
		}
		return true;
	}
}
