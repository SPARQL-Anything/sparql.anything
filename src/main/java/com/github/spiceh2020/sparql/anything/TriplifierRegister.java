package com.github.spiceh2020.sparql.anything;

import java.util.HashMap;
import java.util.Map;

public final class TriplifierRegister {

	private static TriplifierRegister instance;

	private Map<Format, Triplifier> triplifierMap = new HashMap<>();

	private TriplifierRegister() {

	}

	public static TriplifierRegister getInstance() {
		if (instance == null) {
			instance = new TriplifierRegister();
		}

		return instance;
	}

	public void registerTriplifierForFormat(Format f, Triplifier t) {
		this.triplifierMap.put(f, t);
	}

	public Triplifier getTriplifierForFormat(Format f) {
		return this.triplifierMap.get(f);
	}

}
