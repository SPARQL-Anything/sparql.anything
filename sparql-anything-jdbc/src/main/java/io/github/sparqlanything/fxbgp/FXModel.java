package io.github.sparqlanything.fxbgp;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FXModel {
	private static FXModel instance = null;
	private Set<FX> elements;
	private Map<FX,Set<FX>> specialisedBy;
	private Map<FX,Set<FX>> specialisationOf;
	private Map<FX,Set<FX>> inconsistentWith;

	FXModel(){
		elements = new HashSet<>();
		specialisedBy = new HashMap<>();
		specialisationOf = new HashMap<>();
		inconsistentWith = new HashMap<>();
		init();
		extend();
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
		return this.elements.add(element);
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

	protected void getInconsistentWith(FX thiss, FX... thatt){
		add(thiss);
		for(FX th : thatt){
			add(th);
			inconsistentWith.get(thiss).add(th);
			inconsistentWith.get(th).add(thiss);
		}
	}

	public boolean elementExists(FX element){
		return this.elements.contains(element);
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

	public Set<FX> getInconsistentWith(FX element){
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
		return !specialisedBy.get(el1).contains(el2);
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

		// Add consistency table
		getInconsistentWith(FX.Subject, FX.Predicate);
		getInconsistentWith(FX.Object, FX.Predicate);
		getInconsistentWith(FX.TypeProperty, FX.Subject, FX.Object, FX.Slot, FX.Type, FX.Container);
		getInconsistentWith(FX.Type, FX.Slot, FX.Container, FX.Value);
		getInconsistentWith(FX.Container, FX.Predicate, FX.Slot, FX.Value, FX.Type);
		getInconsistentWith(FX.Slot, FX.Type, FX.Subject, FX.Object);
		getInconsistentWith(FX.Value, FX.Predicate, FX.Subject, FX.Type, FX.Container);
		getInconsistentWith(FX.Root, FX.Slot, FX.Container, FX.Predicate, FX.Value);
		getInconsistentWith(FX.SlotNumber, FX.SlotString, FX.Subject, FX.Object);
		getInconsistentWith(FX.SlotString, FX.SlotNumber, FX.Subject, FX.Object);

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
	}

	/**
	 * 	An element is grounded when no element specialises it.
	 */
	public boolean isGrounded(FX element){
		return getSpecialisedBy(element).isEmpty();
	}

	protected void extend(){

	}

	public static FXModel getFXModel(){
		if(instance == null){
			instance = new FXModel();
		}
		return instance;
	}

}
