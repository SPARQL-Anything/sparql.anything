package io.github.sparqlanything.fxrdb;

import io.github.sparqlanything.fxbgp.FX;

public class FXRDB extends FX {

	protected FXRDB(String name) {
		super(name);
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (obj instanceof FXRDB) {
			FXRDB other = (FXRDB) obj;
			return super.equals(other);
		}
		return false;
	}

	public static final FXRDB ContainerTable = new FXRDB("ContainerTable");
	public static final FXRDB ContainerEntity = new FXRDB("ContainerEntity");
	public static final FXRDB SlotColumn = new FXRDB("SlotColumn");
	public static final FXRDB SlotRow = new FXRDB("SlotRow");
	public static final FXRDB Cell = new FXRDB("Cell");
	public static final FXRDB TypeTable = new FXRDB("TypeTable");

}
