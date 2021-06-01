package com.github.spiceh2020.sparql.anything.engine;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TriplifierRegister {
	private static final Logger log = LoggerFactory.getLogger(TriplifierRegister.class);

	private static TriplifierRegister instance;

	private Map<String, String> mimeType = new HashMap<>();
	private Map<String, String> extension = new HashMap<>();

	private TriplifierRegister() {

	}

	public static TriplifierRegister getInstance() {
		if (instance == null) {
			instance = new TriplifierRegister();
		}

		return instance;
	}

	public void registerTriplifier(String t, String [] extensions, String[] mimeTypes) throws TriplifierRegisterException {
		log.trace("Registering {}", t);
		for (String ext : extensions) {
			if (extension.containsKey(ext)) {
				throw new TriplifierRegisterException(
						"A triplifier for " + ext + " extension has been already registered!");
			}
			log.trace("Registering triplifier for extension {} : {}", ext, t);
			extension.put(ext, t);
		}

		for (String mimeType : mimeTypes) {
			if (this.mimeType.containsKey(mimeType)) {
				throw new TriplifierRegisterException(
						"A triplifier for " + mimeType + " mime has been already registered!");
			}
			log.trace("Registering triplifier for mime-type {} : {}", mimeType, t);
			this.mimeType.put(mimeType, t);
		}

	}

	public void removeTriplifier(String t) {
		for (String ext : extension.keySet()) {
			if (extension.get(ext).equals(t)) {
				extension.remove(ext);
			}
		}

		for (String mimeType : mimeType.keySet()) {
			if ( this.mimeType.get(mimeType).equals(t)) {
				this.mimeType.remove(mimeType);
			}
		}
	}

	public String getTriplifierForMimeType(String f) {
		return this.mimeType.get(f);
	}

	public String getTriplifierForExtension(String f) {
		return this.extension.get(f);
	}

	public void printMediaTypes() {
		this.mimeType.keySet().forEach(mt -> {
			System.out.println(mt);
		});
	}

}
