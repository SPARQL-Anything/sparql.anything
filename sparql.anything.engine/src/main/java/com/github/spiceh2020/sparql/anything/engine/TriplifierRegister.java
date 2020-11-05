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

	public void removeTriplifier(Triplifier t) {
		for (String ext : t.getExtensions()) {
			if (extension.containsKey(ext) && extension.get(ext).getClass().equals(t.getClass())) {
				extension.remove(ext);
			}
		}

		for (String mimeType : t.getMimeTypes()) {
			if (this.mimeType.containsKey(mimeType) && this.mimeType.get(mimeType).getClass().equals(t.getClass())) {
				this.mimeType.remove(mimeType);
			}
		}
	}

	public Triplifier getTriplifierForMimeType(String f) {
		return this.mimeType.get(f);
	}

	public Triplifier getTriplifierForExtension(String f) {
		return this.extension.get(f);
	}

}
