/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	public void registerTriplifier(String t, String[] extensions, String[] mimeTypes)
			throws TriplifierRegisterException {
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
		Set<String> extToRemove = new HashSet<>();
		for (String ext : extension.keySet()) {
			if (extension.get(ext).equals(t)) {
//				extension.remove(ext);
				extToRemove.add(ext);
			}
		}

		for (String ext : extToRemove) {
			extension.remove(ext);
		}
		
		Set<String> mimeToRemove = new HashSet<>();

		for (String mimeType : mimeType.keySet()) {
			if (this.mimeType.get(mimeType).equals(t)) {
//				this.mimeType.remove(mimeType);
				mimeToRemove.add(mimeType);
			}
		}
		
		for(String mimeType:mimeToRemove) {
			this.mimeType.remove(mimeType);
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

	public Set<String> getRegisteredExtensions() {
		return extension.keySet();
	}

	public Set<String> getRegisteredMediaTypes() {
		return mimeType.keySet();
	}

}
