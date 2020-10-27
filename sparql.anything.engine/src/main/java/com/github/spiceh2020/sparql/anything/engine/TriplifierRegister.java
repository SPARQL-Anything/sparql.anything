package com.github.spiceh2020.sparql.anything.engine;

import java.util.HashMap;
import java.util.Map;

import com.github.spiceh2020.sparql.anything.model.Format;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public final class TriplifierRegister {

	private static TriplifierRegister instance;

	private Map<Format, Class<? extends Triplifier>> triplifierMap = new HashMap<>();

	private TriplifierRegister() {

	}

	public static TriplifierRegister getInstance() {
		if (instance == null) {
			instance = new TriplifierRegister();
		}

		return instance;
	}

	public void registerTriplifierForFormat(Format f, Class<? extends Triplifier> t) {
		this.triplifierMap.put(f, t);
	}

	public Class<? extends Triplifier> getTriplifierForFormat(Format f) {
		return this.triplifierMap.get(f);
	}

}
