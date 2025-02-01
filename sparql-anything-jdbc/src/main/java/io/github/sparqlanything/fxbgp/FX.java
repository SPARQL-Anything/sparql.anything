package io.github.sparqlanything.fxbgp;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

public class FX {
	int hashCode;
	String name;
	protected FX(String name){
		this.name = name;
		this.hashCode = Objects.hash(getClass(), name);
	}

	public String getName(){
		return name;
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof FX) ){
			return false;
		}
		return this.getName().equals(((FX)obj).getName());
	}

	@Override
	public final String toString() {
		return super.getClass().getCanonicalName() + "/" + getName() + "@" + hashCode();
	}

	@Override
	public final int hashCode() {
		return hashCode;
	}

	public static final FX Subject = new FX("Subject");
	public static final FX Predicate = new FX("Predicate");

	public static final FX Object = new FX("Object");
	public static final FX Container = new FX("Container");
	public static final FX Slot = new FX("Slot");

	public static final FX SlotNumber = new FX("SlotNumber");
	public static final FX SlotString = new FX("SlotString");
	public static final FX Value = new FX("Value");
	public static final FX TypeProperty = new FX("TypeProperty");
	public static final FX Type = new FX("Type");
	public static final FX Root = new FX("Root");
}
