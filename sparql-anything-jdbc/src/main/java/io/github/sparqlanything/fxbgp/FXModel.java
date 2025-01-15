package io.github.sparqlanything.fxbgp;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FXModel {
	private static FXModel instance = null;
	private Set<FX> elements;
	private Map<FX,Set<FX>> specialisesAs;
	private Map<FX,Set<FX>> specialisationOf;
	private Map<FX,Set<FX>> inconsistentWith;

	FXModel(){
		elements = new HashSet<>();
		specialisesAs = new HashMap<>();
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
		if(!specialisesAs.containsKey(element)){
			specialisesAs.put(element, new HashSet<>());
		}
		if(!specialisationOf.containsKey(element)){
			specialisationOf.put(element, new HashSet<>());
		}
		if(!inconsistentWith.containsKey(element)){
			inconsistentWith.put(element, new HashSet<>());
		}
		return this.elements.add(element);
	}

	protected void specialisesAs(FX thiss, FX thatt){
		add(thiss);
		add(thatt);
		boolean newlyAdded = specialisesAs.get(thiss).add(thatt);
		if(newlyAdded){
			specialisationOf(thatt, thiss);
		}
	}

	protected void specialisationOf(FX thiss, FX thatt){
		add(thiss);
		add(thatt);
		boolean newlyAdded = specialisationOf.get(thiss).add(thatt);
		if(newlyAdded){
			specialisesAs(thatt, thiss);
		}
	}

	protected void inconsistentWith(FX thiss, FX... thatt){
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

	public Set<FX> specialisesAs(FX element){
		if(!specialisesAs.containsKey(element)){
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(specialisesAs.get(element));
	}

	public Set<FX> specialisationOf(FX element){
		if(!specialisationOf.containsKey(element)){
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(specialisationOf.get(element));
	}

	public Set<FX> inconsistentWith(FX element){
		if(!inconsistentWith.containsKey(element)){
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(inconsistentWith.get(element));
	}

	public boolean inconsistent(FX el1, FX el2){
		return inconsistentWith.get(el1).contains(el2);
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
		inconsistentWith(FX.Subject, FX.Predicate);
		inconsistentWith(FX.Object, FX.Predicate);
		inconsistentWith(FX.TypeProperty, FX.Subject, FX.Object, FX.Slot, FX.Type, FX.Container);
		inconsistentWith(FX.Type, FX.Slot, FX.Container, FX.Value);
		inconsistentWith(FX.Container, FX.Predicate, FX.Slot, FX.Value, FX.Type);
		inconsistentWith(FX.Slot, FX.Type, FX.Subject, FX.Object);
		inconsistentWith(FX.Value, FX.Predicate, FX.Subject, FX.Type, FX.Container);
		inconsistentWith(FX.Root, FX.Slot, FX.Container, FX.Predicate, FX.Value);
		inconsistentWith(FX.SlotNumber, FX.SlotString, FX.Subject, FX.Object);
		inconsistentWith(FX.SlotString, FX.SlotNumber, FX.Subject, FX.Object);

		// Add hierarchy information
		specialisesAs(FX.Subject, FX.Container);
		specialisesAs(FX.Predicate, FX.Slot);
		specialisesAs(FX.Predicate, FX.TypeProperty);
		specialisesAs(FX.Object, FX.Container);
		specialisesAs(FX.Object, FX.Value);
		specialisesAs(FX.Object, FX.Type);
		specialisesAs(FX.Object, FX.Root);
		specialisesAs(FX.Slot, FX.SlotNumber);
		specialisesAs(FX.Slot, FX.SlotString);
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
