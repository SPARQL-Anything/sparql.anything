package com.github.spiceh2020.sparql.anything.engine;

import java.util.HashMap;
import java.util.Map;

import com.github.spiceh2020.sparql.anything.model.Triplifier;

public final class TriplifierRegister {

	private static TriplifierRegister instance;

	private Map<String, Triplifier> mimeType = new HashMap<>();
	private Map<String, Triplifier> extension = new HashMap<>();

	private TriplifierRegister() {

	}

	public static TriplifierRegister getInstance() {
		if (instance == null) {
			instance = new TriplifierRegister();
		}

		return instance;
	}

	public void registerTriplifier(Triplifier t) throws TriplifierRegisterException {
		for (String ext : t.getExtensions()) {
			if (extension.containsKey(ext)) {
				throw new TriplifierRegisterException(
						"A triplifier for " + ext + " extension has been already registered!");
			}
			extension.put(ext, t);
		}

		for (String mimeType : t.getMimeTypes()) {
			if (this.mimeType.containsKey(mimeType)) {
				throw new TriplifierRegisterException(
						"A triplifier for " + mimeType + " mime has been already registered!");
			}
			this.mimeType.put(mimeType, t);
		}

	}

	public Triplifier getTriplifierForMimeType(String f) {
		return this.mimeType.get(f);
	}

	public Triplifier getTriplifierForExtension(String f) {
		return this.extension.get(f);
	}

}
