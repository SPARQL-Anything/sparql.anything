package io.github.sparqlanything.fxbgp;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.vocabulary.RDF;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FXModel {
	private static FXModel instance = null;

	private InterpretationFactory IF = null;
	private Set<FX> terms;
	private Map<FX,Set<FX>> specialisedBy;
	private Map<FX,Set<FX>> specialisationOf;
	private Map<FX,Set<FX>> inconsistentWith;

	private Set<NodeInterpretationRule> inferenceRules;

	// FIXME Use constant from model package
	protected static final Node FXRoot = NodeFactory.createURI("http://sparql.xyz/facade-x/ns/Root");

	FXModel(){
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
		setInconsistentWith(FX.Type, FX.Slot, FX.SlotString, FX.SlotNumber, FX.Container, FX.Value, FX.Predicate, FX.Subject, FX.Value);
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
			boolean when(Node node, InterpretationOfBGP previous) {
				if(previous.getInterpretation(node).getTerm().equals(FX.Subject)){
					set(IF.make(previous.getOpBGP(), node, FX.Container));
					return true;
				}else{
					// FIXME Inspect triples, in case there is a o-s join??
				}

				return false;
			}
		});

		// 2. If node is fx:Root, then is Root
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			boolean when(Node node, InterpretationOfBGP previous) {
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
			boolean when(Node node, InterpretationOfBGP previous) {
				if(node.equals(RDF.type.asNode())){
					set(IF.make(previous.getOpBGP(), node, FX.Root));
					return true;
				}
				return false;
			}
		});

		// 4. If a Property and not a variable nor rdf:type, then a Slot
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			boolean when(Node n, InterpretationOfBGP p) {
				if(n.isConcrete()) {
					// Find if predicate
					for(Triple t: p.getOpBGP().getPattern().getList()) {
						if (n.equals(t.getPredicate()) && !n.equals(RDF.type.asNode())) {
							set(IF.make(p.getOpBGP(), n, FX.Slot));
							return true;
						}
					}
				}
				return false;
			}
		});


		// 5. If Object not Var and not fx:Root but Predicate rdf:type, then Type
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			boolean when(Node n, InterpretationOfBGP p) {
				if(n.isConcrete() && p.getInterpretation(n).getTerm().equals(FX.Object) && !n.equals(FXRoot) ){
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
			boolean when(Node n, InterpretationOfBGP p) {
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
			boolean when(Node n, InterpretationOfBGP p) {
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
			boolean when(Node n, InterpretationOfBGP p) {
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
			boolean when(Node n, InterpretationOfBGP p) {
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
			boolean when(Node n, InterpretationOfBGP p) {
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
			boolean when(Node n, InterpretationOfBGP p) {
				if(n.isURI() && p.getInterpretation(n).getTerm().equals(FX.Object)){
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
			boolean when(Node n, InterpretationOfBGP p) {
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
		// 13. On focus node is Predicate position: and Object is Container, then Predicate is Slot
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			boolean when(Node n, InterpretationOfBGP p) {
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
	}

	/**
	 * 	An element is grounded when no element specialises it.
	 */
	public boolean isGrounded(FX element){
		return getSpecialisedBy(element).isEmpty();
	}

	/**
	 * The FX model can be extended.
	 */
	protected void extend(){
	}

	public static FXModel getFXModel(){
		if(instance == null){
			instance = new FXModel();
		}
		return instance;
	}
	public boolean isExtension(){
		return !this.getClass().equals(FX.class);
	}
}
